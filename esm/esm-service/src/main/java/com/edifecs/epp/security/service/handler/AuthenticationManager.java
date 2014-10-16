package com.edifecs.epp.security.service.handler;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.exception.AuthenticationFailureException;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.IAuthenticationManager;
import com.edifecs.epp.security.remote.SecurityManager;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.data.token.CertificateAuthenticationToken;
import com.edifecs.epp.security.data.token.IAuthenticationToken;
import com.edifecs.epp.security.data.token.PasswordResetToken;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.exception.EmailSendException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.service.RealmManager;
import com.edifecs.epp.security.service.SecurityContext;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.*;

/**
 * Provides authentication methods for checking to see if the user is signed in,
 * and to login and logout if needed.
 *
 * @author willclem
 */
public class AuthenticationManager extends AbstractCommandHandler implements IAuthenticationManager {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final SecurityContext sc;

	// TODO: All of these variables need to be moved to the Flex Field and ESM
	// Profile configurations
	private final static String MAIL_FROM = "no-reply@edifecs-sm.com";
	private final static String MAIL_SUBJECT = "Reset Password For Security Manager";
	private final static String MAIL_TEMPLATE = "/passwd-reset-template.html";
	private final static String MSG_SERVICE = "Email Service";
	private final static long TOKEN_VALIDITY = 30; // minutes

	// FIXME: This is hard coded along with the port, this will NOT work when
	// the port is changed. This needs to be fixed.
	private String URL = "http://" + getIpAddress()
			+ ":8080/security/login/#!/ResetPassword/";

	public AuthenticationManager(SecurityContext context) {
		this.sc = context;
	}

	private SessionId getUserSession() {
		return sc.manager().getSessionManager().getCurrentSession();
	}

	private Subject getSubject() {
		final SessionId userSession = getUserSession();
		if (userSession == null) {
			return null;
		}
		return new Subject.Builder(sc.shiroManager()).sessionId(
				userSession.getSessionId()).buildSubject();
	}

	@Override
	public PrincipalCollection getSubjectPrincipals() {
		final Subject subject = getSubject();
		return subject.getPrincipals();
	}

	@Override
	public boolean isSubjectAuthenticated() {
		Subject subject = getSubject();
		return subject != null && subject.isAuthenticated();
	}

	@Override
	public SessionId loginCertificate(String domain, String organization,
			String certificate, String username) {
		if (certificate != null && !certificate.isEmpty()) {
			return loginToken(new CertificateAuthenticationToken(domain,
					organization, Base64.decode(certificate),username));
		} else {
			throw new IllegalArgumentException(
					"The command 'loginCert' requires an valid 'certificate' argument.");
		}
	}

	@Override
	public SessionId login(
            Subject subject,
			AuthenticationToken authenticationToken,
            String username,
			Serializable password,
            String domain,
            String organization,
			Boolean remember) throws AuthenticationFailureException {

		setupTenantContext(domain, username, organization, authenticationToken);

		if (subject == null) {
			subject = getSubject();
		}
		if (authenticationToken == null) {
			if (domain != null && username != null && password != null) {
				char[] pwdChars;
				if (password instanceof char[]) {
					pwdChars = (char[]) password;
				} else if (password instanceof byte[]) {
					pwdChars = new String((byte[]) password).toCharArray();
				} else {
					pwdChars = password.toString().toCharArray();
				}
				authenticationToken = new UsernamePasswordAuthenticationToken(
						domain, organization, username, pwdChars);
			} else {
				throw new IllegalArgumentException(
						"The command 'login' requires"
								+ " either an 'authenticationToken' argument or 'domain', 'username'"
								+ " and 'password' arguments.");
			}
		}

		try {
			final Subject s = sc.shiroManager().login(subject, authenticationToken);
			return new SessionId(s.getSession().getId());
		} catch (AuthenticationException e) {
			throw new AuthenticationFailureException(e);
		}
	}

    @Override
    public SessionId loginToken(AuthenticationToken authenticationToken) {
        setupTenantContext(null, null, null, authenticationToken);
        Subject subject = getSubject();

        try {
            final Subject s = sc.shiroManager().login(subject, authenticationToken);
            return new SessionId(s.getSession().getId());
        } catch (AuthenticationException e) {
            throw new AuthenticationFailureException(e);
        }
    }

	private String getDomain(IAuthenticationToken token) {
		if (null == token.getDomain() || token.getDomain().isEmpty()) {
			return null;
		}
		return token.getDomain();
	}

	private String getOrganization(IAuthenticationToken token) {
		if (null == token.getOrganization()
				|| token.getOrganization().isEmpty()) {
			return null;
		}
		return token.getOrganization();
	}

    //FIXME: This method needs to be updated
	private void setupTenantContext(String domain, String username,
			String organizationName, AuthenticationToken token)
			throws SecurityManagerException {

		if (null != token) {
			domain = getDomain((IAuthenticationToken) token);
			organizationName = getOrganization((IAuthenticationToken) token);
			if (null == domain) {
				throw new SecurityException(
						"Invalid Token, token does not contain any information about the domain, "
								+ "cannot Login without a valid Tenant Domain");
			}
		} else {
			if (null == domain || domain.isEmpty()) {
				throw new SecurityException("No Domain Specified, cannot Login without a valid Tenant Domain");
			}
		}

		Tenant tenant;
		try {
			tenant = sc.dataStore().getTenantDataStore()
					.getTenantByDomain(domain);
		} catch (Exception e) {
			throw new AuthenticationFailureException(e);
		}

		Organization org;
		if ((organizationName == null || organizationName.isEmpty())) {
			PaginatedList<Organization> tenantOrgs = sc.dataStore()
					.getOrganizationDataStore()
					.getOrganizationsForTenant(tenant.getId(), 0, 1);
			if (token == null || !(token instanceof CertificateAuthenticationToken)) {
				if (tenantOrgs.getTotal() == 0) {
					throw new AuthenticationFailureException(new SecurityException(
							String.format(
									"Login Failed, Domain : '%s' has no Organizations.",
									domain)));
				} else if (tenantOrgs.getTotal() > 1) {
                    throw new AuthenticationFailureException(new SecurityException(
							"Login Failed, Organization is required."));
				}
			}
			org = new ArrayList<>(tenantOrgs.getResultList()).get(0);
		} else {
			try {
				org = sc.dataStore().getOrganizationDataStore()
						.getOrganizationByDomainAndOrganizationName(domain,organizationName);
			} catch (Exception e) {
				logger.error("unable to find organization : {} for authentication", organizationName, e);
				throw new AuthenticationFailureException(e);
			}
		}

		try {
			RealmManager.loadRealmsForOrganization(org);
		} catch (Exception e) {
			throw new AuthenticationFailureException(e);
		}
	}

	@Override
	public void logout() {
		try {
			sc.dataStore()
					.getUserDataStore()
					.removeSessionFromUser(
							getSecurityManager().getSubjectManager()
									.getUserId());
		} catch (Exception e) {
			logger.debug("error removing session from user, reason : {}", e.getMessage(), e);
		}

		sc.shiroManager().logout(getSubject());
		logger.info("Successfully logged out User");
	}

	@Override
	public scala.Option<SecurityManager> getReceivingSecurityManager() {
		return scala.Option.apply(sc.manager());
	}

    @Override
	public boolean initiatePasswordReset(String email) {

        try {
            // TODO : validate input email

            // check if user account exists
            User user = sc.dataStore().getUserDataStore().getUserByEmail(email);

            // user active
            if (!user.isActive())
                throw new SecurityException("User not active, Please contact admin");

            // generate token
            PasswordResetToken token = new PasswordResetToken();
            token.setDateGenerated(new Date());
            token.setExpiryDate(new Date(System.currentTimeMillis()
                    + TOKEN_VALIDITY * 60 * 1000));

            // seed value
            String key = user.getId().toString() + user.getCreatedDateTime()
                    + user.getContact().getEmailAddress()
                    + new Timestamp(new Date().getTime());

            token.setToken(createSecureToken(key));


            sc.dataStore().getUserDataStore().addAuthenticationTokenToUser(user, token);

            // TODO: Externalize this message as a configurable template
            // prepare mail, add generated token
            String body = "Hello "
                    + user.getContact().getFirstName()
                    + " "
                    + user.getContact().getLastName()
                    + ", <br/><br/>"
                    + "We have received new password request for your account "
                    + ". <br/><br/>"
                    + "If this request was initiated by you, please click on following link and change your password: "
                    + "<a href='" + URL + token.getToken() + "'> click here </a>"
                    + "<br/><br/><br/><br/><br/><br/>" +

                    "This request is valid until " + TOKEN_VALIDITY
                    + " minutes. Sincerely,<br/><br/>";

            // send mail via SSL
            Address add = getCommandCommunicator().getAddressRegistry()
                    .getAddressForServiceTypeName(MSG_SERVICE);

            if (null == add)
                throw new SecurityException("Unable to find Messaging Service,"
                        + MSG_SERVICE + " is needed for sending messages");

            Map<String, Serializable> args = new HashMap<>();

            args.put("to", email);
            args.put("from", MAIL_FROM);
            args.put("subject", MAIL_SUBJECT);
            args.put("msg", getHTML(body));

            Object success = getCommandCommunicator().sendSyncMessage(add, "mail.sendmail", args);

            return (Boolean) success;
        } catch (Exception e) {
            throw new EmailSendException(e);
        }
	}

	private String createSecureToken(String key) {

		String result = "";
		try {
			final Charset asciiCs = Charset.forName("US-ASCII");
			Mac sha256_HMAC;
			sha256_HMAC = Mac.getInstance("HmacSHA256");
			final SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(
					asciiCs.encode("key").array(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			final byte[] mac_data = sha256_HMAC.doFinal(asciiCs.encode(key)
					.array());
			for (final byte element : mac_data) {
				result += Integer.toString((element & 0xff) + 0x100, 16)
						.substring(1);
			}

			logger.debug("Sucessfully generated token, Result:[{}]", result);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error occured while creating the token : ", e);
		}
		return result;
	}

	private String getHTML(String val) throws IOException {

		InputStream in = this.getClass().getResourceAsStream(MAIL_TEMPLATE);

		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read;

		read = br.readLine();

		while (read != null) {
			if (read.contains("${body}")) {
				logger.debug("repalcing body placeholder in the template");
				read = read.replace("${body}", val);
			}
			sb.append(read);
			read = br.readLine();
		}

		return sb.toString();
	}

	public boolean updatePassword(String newPasswd, String token) {

		User user = validateToken(token);
		logger.debug("token is valid");

		getSecurityManager().getSubjectManager().getTenant();

		// FIXME: This needs to be updated to provided the domain context
		UsernamePasswordAuthenticationToken newToken = new UsernamePasswordAuthenticationToken(
				SystemVariables.DEFAULT_TENANT_NAME,
				SystemVariables.DEFAULT_ORG_NAME, user.getUsername(),
				newPasswd.toCharArray());

		sc.dataStore().getUserDataStore()
				.updateAuthenticationToken(user, newToken);
		logger.debug("Password updated sucessfully");

		// delete token
		updateUserAndDeleteResetToken(user, new PasswordResetToken(token));
		return true;
	}

    private User validateToken(String token) {
		// validate token
		try {
			return sc.dataStore().getUserDataStore().validateUserAuthenticationToken(new PasswordResetToken(token));
		} catch (SecurityDataException e) {
			e.printStackTrace();
			throw new SecurityException("Error validating token form the Datastore.");
		}
	}

	private void updateUserAndDeleteResetToken(User user, PasswordResetToken token) {
		try {
			sc.dataStore().getUserDataStore().deleteAuthenticationToken(token);
		} catch (Exception e) {
			logger.error("error deleting token :", e);
		}
	}

	private String getIpAddress() {

		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				if (!current.isUp() || current.isLoopback()
						|| current.isVirtual())
					continue;

				Enumeration<InetAddress> addresses = current.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress current_addr = addresses.nextElement();
					if (current_addr.isLoopbackAddress())
						continue;
					if (current_addr instanceof Inet4Address) {
						return current_addr.getHostAddress();

					}
				}
			}
		} catch (SocketException e) {
            //FIXME: This needs to log the exception properly
			e.printStackTrace();
		}
		return "localhost";
	}
}

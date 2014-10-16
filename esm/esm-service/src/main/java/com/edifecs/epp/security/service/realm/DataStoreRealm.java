package com.edifecs.epp.security.service.realm;

import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.CertificateAuthenticationToken;
import com.edifecs.epp.security.data.token.CookieAuthenticationToken;
import com.edifecs.epp.security.data.token.IAuthenticationToken;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.datastore.ISecurityDataStore;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.service.SecurityContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A Shiro realm that uses a {@link ISecurityDataStore} as its backing database.
 *
 * @author i-adamnels
 */
// TODO : clean up needed. persist login attempts? handle case where sm restarted and user is suspended
public class DataStoreRealm extends AuthorizingRealm {
    private final static String ADMIN_USR = "admin";
    private final ISecurityDataStore dataStore;
    private final int INITIAL_DELAY = 1000;
    private final int PERIODIC_INTERVAL = 60000;
    ConcurrentHashMap<Long, LoginAttempt> loginAttempts = new ConcurrentHashMap<Long, LoginAttempt>();
    ConcurrentHashMap<Long, LoginAttempt> suspendedUsers = new ConcurrentHashMap<Long, LoginAttempt>();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private int LOGIN_ATTEMPTS_ALLOWED;
    private int ATTEMPTS_RESET_INTERVAL; // min
    private int ACCOUNT_RESET_INTERVAL; // min

    public DataStoreRealm(ISecurityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public DataStoreRealm(ISecurityDataStore dataStore,
                          MemoryConstrainedCacheManager cacheManager) {
        super(cacheManager);
        super.setName(RealmType.DATABASE.getVal());
        this.dataStore = dataStore;

        // start reset thread
        handleAccountLockout();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof IAuthenticationToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        final Collection<?> principal = principals.fromRealm(getName());
        if (principal.isEmpty()) {
            return new SimpleAuthorizationInfo();
        }
        final Long key = Long.valueOf(principal.iterator().next().toString());
        try {
            final SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
            final User user = dataStore.getUserDataStore().getById(key);
            if (user == null) {
                return null;
            }
            for (Role r : dataStore.getRoleDataStore().getRolesForUser(user)) {
                authInfo.addRole(r.getCanonicalName());
            }
            for (Permission p : dataStore.getPermissionDataStore()
                    .getTransitivePermissionsForUser(user.getId())) {
                authInfo.addStringPermission(p.toString());
            }

            return authInfo;
        } catch (Exception ex) {
            logger.error("Fatal Error authorizing user", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authenticationToken)
            throws AuthenticationException {
        try {
            if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
                return handleUserPasswordToken((UsernamePasswordAuthenticationToken) authenticationToken);
            } else if (authenticationToken instanceof CertificateAuthenticationToken) {
                return handleCertificateToken((CertificateAuthenticationToken) authenticationToken);
            } else if (authenticationToken instanceof CookieAuthenticationToken) {
                return handleCookieToken((CookieAuthenticationToken) authenticationToken);
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new SecurityException(
                    "Login failed, the login credential is incorrect", e);
        }
    }

    private void configureRealmForUserTenantPasswordPolicy(User user) {
        try {
            Tenant te = dataStore.getTenantDataStore().getTenantByUserId(
                    user.getId());
            PasswordPolicy pp = te.getPasswordPolicy();
            this.LOGIN_ATTEMPTS_ALLOWED = pp.getPasswdMaxFailure();
            this.ATTEMPTS_RESET_INTERVAL = pp.getPasswdResetFailureLockout();
            this.ACCOUNT_RESET_INTERVAL = pp.getPasswdLockoutDuration();

            logger.debug("DataStore Realm configured with params : login attempts : "
                    + LOGIN_ATTEMPTS_ALLOWED
                    + " attempt reset interval : "
                    + ATTEMPTS_RESET_INTERVAL
                    + " account reset interval :"
                    + ACCOUNT_RESET_INTERVAL
                    + " Tenant : "
                    + te.getCanonicalName());

            if (LOGIN_ATTEMPTS_ALLOWED != 0) {
                logger.debug("Account locking mechanism enabled for tenant : "
                        + te.getCanonicalName());
            } else
                logger.debug("Account locking mechanism disabled for tenant : "
                        + te.getCanonicalName());

        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    private AuthenticationInfo handleUserPasswordToken(UsernamePasswordAuthenticationToken token) {
        try {
            User user = dataStore.getUserDataStore()
                    .validateUserAuthenticationToken(token);
            if (user != null) {
                // Check to see if the user is active or suspended
                if (!user.isActive() || user.isSuspended() || user.isDeleted()) {
                    throw new SecurityException("User with username : "
                            + user.getUsername()
                            + " is deactivated, Please contact admin.");
                } else {
                    // use passwd policy for reset counter values
                    configureRealmForUserTenantPasswordPolicy(user);

                    user.setLastLoginDateTime(new Date());
                    boolean rememberMe = token.isRememberMe();
                    // Attach User Session if remember me is checked.
                    if (rememberMe) {
                        dataStore.getUserDataStore().attachSessionToUser(
                                new SessionId(SecurityUtils.getSubject()
                                        .getSession().getId()), user.getId()
                        );
                        logger.debug(
                                "successfully attached session : {}, to user : {}",
                                SecurityUtils.getSubject().getSession().getId(),
                                user.getUsername());
                    }

                    // Reset failed attempts
                    if (loginAttempts.containsKey(user.getId())) {
                        loginAttempts.remove(user.getId());
                    }
                    try {
                        dataStore.getUserDataStore().update(user, SecurityContext.getSystemUser());
                    } catch (Exception e) {
                        logger.error(
                                "error setting last login time, after authentication",
                                e);
                    }

                    logger.debug("successfully logged in user : {}",
                            token.getUsername());
                    return new SimpleAuthenticationInfo(user.getId(),
                            token.getCredentials(), getName());
                }
            }
            return null;
        } catch (SecurityDataException ex) {
            logger.error("error occurred while authentication", ex);

            String username = token.getUsername();
            User user = dataStore.getUserDataStore()
                    .getUserByUsername(token.getDomain(), username);

            if (LOGIN_ATTEMPTS_ALLOWED != 0 && null != user
                    && !username.equals(ADMIN_USR)) {
                handleFailedLoginAttempt(user);
                if (validateAttempts(user)) {
                    if (ACCOUNT_RESET_INTERVAL != 0) {
                        throw new SecurityException(
                                "Max login attempts exceeded, account temporarily suspended for "
                                        + ACCOUNT_RESET_INTERVAL + " minutes"
                        );
                    } else {
                        throw new SecurityException(
                                "Max login attempts exceeded, account suspended. "
                                        + "Please contact Site / CommunityGateway / Admin, to unlock the account"
                        );
                    }
                }
            }
            throw new AuthenticationException(
                    "Error occurred while validating authentication: "
                            + ex.getMessage(), ex
            );
        }
    }

    private AuthenticationInfo handleCertificateToken(CertificateAuthenticationToken token) {
        try {
            User user = dataStore.getUserDataStore().validateUserAuthenticationToken(token);
            if (user != null) {
                return new SimpleAuthenticationInfo(user.getId(), token.getCredentials(), getName());
            }
            return null;
        } catch (SecurityDataException ex) {
            throw new AuthenticationException(
                    "Error occurred while validating authentication: " + ex.getMessage(), ex);
        }
    }

    private AuthenticationInfo handleCookieToken(
            CookieAuthenticationToken authenticationToken) {
        CookieAuthenticationToken token = new CookieAuthenticationToken(
                authenticationToken.getCookieId());
        try {
            User user = dataStore.getUserDataStore()
                    .validateUserAuthenticationToken(token);
            if (user != null) {
                return new SimpleAuthenticationInfo(user.getId(),
                        authenticationToken.getCredentials(), getName());
            }
            return null;
        } catch (SecurityDataException ex) {
            throw new AuthenticationException(
                    "Error occurred while validating authentication: "
                            + ex.getMessage(), ex
            );
        }
    }

    // TODO : use timer task?
    private void handleAccountLockout() {
        ScheduledExecutorService sExecutorService = Executors.newScheduledThreadPool(2);
        sExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                if (!loginAttempts.isEmpty()) {
                    for (Entry<Long, LoginAttempt> entry : loginAttempts.entrySet()) {
                        LoginAttempt attempt = entry.getValue();
                        long time = attempt.getLastAttempt().getTime() + ATTEMPTS_RESET_INTERVAL * 60 * 1000;
                        Date currentDate = new Date();

                        logger.debug("login attempt for user id, "
                                + entry.getKey() + " date reset : "
                                + new Date(time) + " curr date : " + new Date());

                        if (currentDate.after(new Date(time))) {
                            loginAttempts.remove(entry.getKey());
                            logger.debug(
                                    "login attempts reset for userid : {}",
                                    entry.getKey());
                        }
                    }
                }
            }
        }, INITIAL_DELAY, PERIODIC_INTERVAL, TimeUnit.MILLISECONDS);

        sExecutorService.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                if (!suspendedUsers.isEmpty()) {
                    if (ACCOUNT_RESET_INTERVAL == 0) {
                        logger.debug("Accounts will be reinstated manually by Site / CommunityGateway / Admin");
                        return;
                    }

                    try {
                        Iterator<Entry<Long, LoginAttempt>> it = suspendedUsers.entrySet().iterator();

                        while (it.hasNext()) {

                            Entry<Long, LoginAttempt> entry = it.next();
                            LoginAttempt attempt = entry.getValue();
                            long time = attempt.getDateSuspended().getTime() + ACCOUNT_RESET_INTERVAL * 60 * 1000;
                            Date currentDate = new Date();

                            logger.debug("login attempt for suspended user id, "
                                    + entry.getKey()
                                    + " date reset : "
                                    + new Date(time)
                                    + " curr date : "
                                    + new Date());

                            if (currentDate.after(new Date(time))) {

                                dataStore.getUserDataStore().unSuspend(entry.getKey());
                                it.remove();
                                logger.debug(
                                        "user : {} reinstated successfully, total suspended : {}.",
                                        entry.getKey(), suspendedUsers.size());
                            }
                        }
                    } catch (Exception | Error e) {
                        logger.error("error occured in account reset thread :",
                                e);
                    }
                }
            }

        }, INITIAL_DELAY, PERIODIC_INTERVAL, TimeUnit.MILLISECONDS);

    }

    private void handleFailedLoginAttempt(User user) {

        if (!loginAttempts.containsKey(user.getId()))
            loginAttempts.put(user.getId(), new LoginAttempt(new Date(), 1));
        else {
            LoginAttempt attempt = loginAttempts.get(user.getId());
            attempt.setTotalFailedAttempts(attempt.getTotalFailedAttempts() + 1);
            attempt.setLastAttempt(new Date());
            loginAttempts.put(user.getId(), attempt);
        }

        logger.error("invalid login credentials, userId : {}, attempt : {}",
                user.getId(), loginAttempts.get(user.getId())
                        .getTotalFailedAttempts()
                        + ", last attempt : "
                        + loginAttempts.get(user.getId()).getLastAttempt()
        );

    }

    private boolean validateAttempts(User user) {

        long userId = user.getId();
        LoginAttempt attempt = loginAttempts.get(userId);

        int attempts = attempt.getTotalFailedAttempts();
        if (attempts >= LOGIN_ATTEMPTS_ALLOWED) {

            logger.info(
                    "max attempts reached, blocking user account, user id : {}",
                    user.getId());

            try {
                dataStore.getUserDataStore().suspend(userId);
                logger.debug("user account suspended.");

                attempt.setDateSuspended(new Date());
                suspendedUsers.put(userId, attempt);
                loginAttempts.remove(userId);

                return true;
            } catch (Exception e1) {
                logger.error("error suspending user", e1);
            }
        }
        return false;
    }
}

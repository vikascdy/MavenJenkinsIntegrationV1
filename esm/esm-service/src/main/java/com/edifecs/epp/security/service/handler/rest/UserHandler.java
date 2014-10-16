package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.isc.json.JsonArg;
import com.edifecs.epp.isc.stream.MessageStream;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.CertificateAuthenticationToken;
import com.edifecs.epp.security.data.token.IAuthenticationToken;
import com.edifecs.epp.security.data.token.LdapAuthenticationToken;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.exception.AuthorizationFailureException;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.PasswordValidationException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.handler.rest.IUserHandler;
import com.edifecs.epp.security.jpa.entity.RoleEntity;
import com.edifecs.epp.security.jpa.entity.UserGroupEntity;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper.UsersValidator;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.realm.SecurityLdapRealm;
import com.edifecs.epp.security.data.CSVJsonUtil;
import com.edifecs.epp.security.service.util.JsonObjectLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.edifecs.epp.security.service.util.JsonValidator;
import com.edifecs.epp.security.service.util.UserJsonHelper;
import com.edifecs.epp.security.service.util.UserJsonHelper.importUsers;

import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class UserHandler extends AbstractSecurityRestHandler<User> implements
        IUserHandler {

    public UserHandler(SecurityContext context) {
        super(context, User.class);
    }
 
    @Override
    public User get(String url) throws Exception {
        final long id = idFromUrl(url);
        return sc.dataStore().getUserDataStore().getById(id);
    }

    @Override
    public Collection<User> list(Pagination pg) throws Exception {
        final Collection<User> users;
        if (pg.limit() == 0)
            users = sc.dataStore().getUserDataStore().getAll();
        else if (null != pg.query() && !pg.query().isEmpty())
            users = sc.dataStore().getUserDataStore()
                    .queryUsers(pg.query(), pg.start(), pg.limit());
        else
            users = sc.dataStore().getUserDataStore()
                    .getRange(pg.start(), pg.limit());
        return sort(users, pg.sorters());
    }

    @Override
    public User post(User user) throws Exception {
        try {
            Organization organization = getSecurityManager()
                    .getSubjectManager().getOrganization();
            User u = sc.dataStore().getUserDataStore()
                    .create(organization.getId(), user, getSecurityManager().getSubjectManager().getUser());
            return u;
        } catch (Exception e) {
            if (e instanceof PersistenceException
                    && e.getClass().equals(ConstraintViolationException.class))
                throw new ItemAlreadyExistsException("User", user.getUsername(), e);
            else
                throw new SecurityDataException(e);
        }
    }

    @Override
    public User put(String url, User user) throws Exception {
        final long id = idFromUrl(url);
        if (user.getId() != id)
            user.setId(id);
        return updateUser(user);
    }

    @Override
    public void delete(String url) throws Exception {
        final long id = idFromUrl(url);
        final User user = new User();
        user.setId(id);
        sc.dataStore().getUserDataStore().delete(user);
        getLogger().info("user deleted : {} ", id);
    }

    @Override
    public User createUser(User user,
                           UsernamePasswordAuthenticationToken token, Long organizationId)
            throws Exception {
        Organization org = sc.dataStore().getOrganizationDataStore()
                .getById(organizationId);
        PasswordPolicy pp = org.getTenant().getPasswordPolicy();
        validatePassword(token, pp);
        user.setChangePasswordAtFirstLogin(pp.isChangePasswdAtFirstLogin());
        User u = sc.dataStore().getUserDataStore()
                .create(organizationId, user, token, getSecurityManager().getSubjectManager().getUser());
        return u;
    }

    // FIXME: The logic here and the parameters are strange
    @Override
    public User createLdapUser(User user, LdapAuthenticationToken token,
                               Long organizationId) throws Exception {
        User u = sc.dataStore().getUserDataStore()
                .create(organizationId, user, token, getSecurityManager().getSubjectManager().getUser());
        return u;
    }

    // FIXME: The logic here and the parameters are strange
    @Override
    public User createCertificateUser(User user, String domain,
                                      String organization, String certificate, Long organizationId, String username)
            throws Exception {
        CertificateAuthenticationToken token = new CertificateAuthenticationToken(
                domain, organization, certificate.getBytes(), username);
        User u = sc.dataStore().getUserDataStore()
                .create(organizationId, user, token, getSecurityManager().getSubjectManager().getUser());
        return u;
    }

    @Override
    public void updateCertificateTokenForUser(User user, String domain,
                                              String organization, String certificate) throws Exception {
        CertificateAuthenticationToken token = new CertificateAuthenticationToken(
                domain, organization, certificate.getBytes(), user.getUsername());
        sc.dataStore().getUserDataStore().updateAuthenticationToken(user, token);
    }

    /**
     * Throws an exception if the password policy is incorrect.
     *
     * @param token
     * @param policy
     */
    private void validatePassword(UsernamePasswordAuthenticationToken token,
                                  PasswordPolicy policy) throws SecurityDataException {
        if (null == policy.getPasswdRegex() || policy.getPasswdRegex().isEmpty()) {
            getLogger().warn("No Password Validation Performed, regex is null.");
        } else {
            UsernamePasswordAuthenticationToken passwdToken = token;
            String passswd = new String(passwdToken.getPassword());
            if (!matchPattern(policy.getPasswdRegex(), passswd)) {
                throw new PasswordValidationException(policy);
            }
        }
    }

    @Override
    public boolean matchPattern(String regex, String seed) {
        return seed.matches(regex);
    }

    @Override
    public void addUsernamePasswordAuthenticationTokenToUser(User user,
                                                             UsernamePasswordAuthenticationToken authenticationToken)
            throws Exception {
        sc.dataStore().getUserDataStore()
                .addAuthenticationTokenToUser(user, authenticationToken);
        getLogger().info(
                "UsernamePasswordAuthenticationToken, added to User : {}",
                authenticationToken.getUsername());
    }

    @Override
    public boolean deleteUser(Long id) throws Exception {
        final User u = new User();
        u.setId(id);
        sc.dataStore().getUserDataStore().delete(u);
        getLogger().info("User deleted : {}", id);
        return true;
    }

    @Override
    public User getUserById(Long id) throws Exception {
        return sc.dataStore().getUserDataStore().getById(id);
    }

    @Override
    public boolean deactivateUser(Long id) throws Exception {
        sc.dataStore().getUserDataStore().deactivate(id);
        getLogger().info("User id : {}, Deactivated.", id);
        return true;
    }

    @Override
    public boolean activateUser(Long id) throws Exception {
        sc.dataStore().getUserDataStore().activate(id);
        getLogger().info("User id : {}, Activated.", id);
        return true;
    }

    @Override
    public boolean suspendUser(Long id) throws Exception {
        sc.dataStore().getUserDataStore().suspend(id);
        getLogger().info("User Suspended : {}", id);
        return true;
    }

    @Override
    public boolean unSuspendUser(Long id) throws Exception {
        sc.dataStore().getUserDataStore().unSuspend(id);
        getLogger().info("User : {} UnSuspended", id);
        return true;
    }

    @Override
    public User getUserByUsername(String username)
            throws SecurityManagerException {
        String domain = getSecurityManager().getSubjectManager().getTenant()
                .getDomain();
        return sc.dataStore().getUserDataStore()
                .getUserByUsername(domain, username);
    }

    @Deprecated
    @Override
    public PaginatedList<User> getUsers(long startRecord, long recordCount)
            throws Exception {
        return sc.dataStore().getUserDataStore()
                .getPaginatedRange(startRecord, recordCount);
    }

    @Override
    public void updateUsernamePasswordAuthenticationToken(User user,
                                                          UsernamePasswordAuthenticationToken authenticationToken)
            throws Exception {
        sc.dataStore().getUserDataStore()
                .updateAuthenticationToken(user, authenticationToken);
        getLogger().info(
                "UsernamePasswordAuthenticationToken, updated for User : {}",
                authenticationToken.getUsername());
    }
    
    @Override
    public void updateCurrentUserAuthenticationToken(UsernamePasswordAuthenticationToken authenticationToken)
            throws Exception {
    	User existingUser = getSecurityManager().getSubjectManager().getUser();
        sc.dataStore().getUserDataStore()
                .updateAuthenticationToken(existingUser, authenticationToken);
        getLogger().info(
                "UsernamePasswordAuthenticationToken, updated for User : {}",
                authenticationToken.getUsername());
    }

    @Override
    public void changePasswordAtFirstLogin(User user, UsernamePasswordAuthenticationToken authenticationToken) throws Exception {
        sc.dataStore().getUserDataStore().updateAuthenticationToken(user, authenticationToken);
    }

    @Override
    public User updateUser(User user) throws Exception {
        try {
            User u = sc.dataStore().getUserDataStore().update(user, getSecurityManager().getSubjectManager().getUser());
            return u;
        } catch (Exception e) {
            // TODO: This is a hack it looks like
            if (e.getCause().getClass().isAssignableFrom(ConstraintViolationException.class))
                throw new ItemAlreadyExistsException("Email", user.getContact().getEmailAddress(), e);
            else
                throw new SecurityDataException(e);
        }
    }
    
    @Override 
    public User updateCurrentUser(User user) throws Exception {
    	User existingUser = getSecurityManager().getSubjectManager().getUser();
    	if (user.getId() != existingUser.getId()){
    		throw new AuthorizationFailureException();
    	}
    	return updateUser(user);
    }

    @Override
    public void batchImportUsers(ArrayList<String> users)
            throws SecurityManagerException {
        if (users != null) {
            Organization organization = getSecurityManager()
                    .getSubjectManager().getOrganization();
            createCSVUsers(organization, users);
        } else
            throw new SecurityException("User list is null or empty");
    }

    @Override
    public Collection<CSVJsonUtil> validateUsersCSV(MessageStream messageStream) {
        if (messageStream != null)
            return validateCSV(messageStream.toInputStream(128000));
        else
            throw new SecurityException("Unable to read Input Stream");
    }

    @Override
    public Collection<User> searchByFirstOrMiddleOrLastName(String seed,
                                                            long startRecord, long recordCount) {
        return sc.dataStore().getUserDataStore()
                .queryUsers(seed, startRecord, recordCount);
    }

    @Override
    public boolean deleteAuthenticationToken(
            IAuthenticationToken newAuthenticationToken) throws Exception {
        sc.dataStore().getUserDataStore()
                .deleteAuthenticationToken(newAuthenticationToken);
        getLogger().info("IAuthenticationToken deleted : {}");
        return true;
    }

    @Override
    public PaginatedList<User> getUsersForGroup(Long groupId, long startRecord,
                                                long recordCount) throws Exception {
        return sc.dataStore().getUserDataStore()
                .getUsersForGroup(groupId, startRecord, recordCount);
    }

    @Override
    public void updateAuthenticationToken(User user, IAuthenticationToken authenticationToken) throws Exception {
        sc.dataStore().getUserDataStore()
                .updateAuthenticationToken(user, authenticationToken);
        getLogger().info("AuthenticationToken, updated.");
    }

    @Override
    public PaginatedList<User> getUsersForRole(long roleId, long startRecord,
                                               long recordCount) throws Exception {
        return sc.dataStore().getUserDataStore()
                .getTransitiveUsersForRole(roleId, startRecord, recordCount);
    }

    @Override
    public PaginatedList<User> getUserRangeForGroup(long groupId,
                                                    long startRecord, long recordCount) throws Exception {
        return sc.dataStore().getUserDataStore()
                .getUsersForGroup(groupId, startRecord, recordCount);
    }

    @Override
    public PaginatedList<User> getUsersForOrganization(long organizationId,
                                                       long startRecord, long recordCount) throws Exception {
        return sc
                .dataStore()
                .getUserDataStore()
                .getUsersForOrganization(organizationId, startRecord,
                        recordCount);
    }

    @Override
    public PaginatedList<User> searchUsers(String userName, long startRecord,
                                           long recordCount) throws Exception {
        return sc.dataStore().getUserDataStore()
                .searchUsersByName(userName, startRecord, recordCount);
    }

    @Override
    public PaginatedList<User> searchUsersHavingRole(String userName,
                                                     long roleId, long startRecord, long recordCount) throws Exception {
        return sc.dataStore().getUserDataStore()
                .searchUsersForRole(userName, roleId, startRecord, recordCount);
    }

    @Override
    public PaginatedList<User> getUsersForPermission(String permissionString,
                                                     long startRecord, long recordCount) throws Exception {
        Permission permission = new Permission();
        if (permissionString != null) {
            String[] permissionArray = permissionString.split(":");
            if (permissionArray.length == 5) {
                permission.setProductCanonicalName(permissionArray[0]);
                permission.setCategoryCanonicalName(permissionArray[1]);
                permission.setTypeCanonicalName(permissionArray[2]);
                permission.setSubTypeCanonicalName(permissionArray[3]);
                permission.setCanonicalName(permissionArray[4]);
            }
        }
        return sc.dataStore().getUserDataStore()
                .getUsersForPermission(permission, startRecord, recordCount);
    }

    @Override
    public PaginatedList<String> getUserNamesForPermission(
            String permissionString, long startRecord, long recordCount)
            throws Exception {
        Permission permission = new Permission();
        if (permissionString != null) {
            String[] permissionArray = permissionString.split(":");
            if (permissionArray.length == 5) {
                permission.setProductCanonicalName(permissionArray[0]);
                permission.setCategoryCanonicalName(permissionArray[1]);
                permission.setTypeCanonicalName(permissionArray[2]);
                permission.setSubTypeCanonicalName(permissionArray[3]);
                permission.setCanonicalName(permissionArray[4]);
            }
        }
        return sc
                .dataStore()
                .getUserDataStore()
                .getUserNamesForPermission(permission, startRecord, recordCount);
    }

    @Override
    public List<Credential> getCredentialForUser(Long id) throws Exception {
        return sc.dataStore().getUserDataStore().getCredentialForUser(id);
    }
    
    
    @Override
    public List<Credential> getCredentialForCurrentUser() throws Exception {
    	User existingUser = getSecurityManager().getSubjectManager().getUser();
        return sc.dataStore().getUserDataStore().getCredentialForUser(existingUser.getId());
    }

    @Override
    public User getLdapUserAttributes(String username, Long organizationId)
            throws Exception {
        Organization org = sc.dataStore().getOrganizationDataStore()
                .getById(organizationId);
        SecurityLdapRealm ldapRealm = null;
        for (SecurityRealm sr : org.getSecurityRealms()) {
            if (sr.getRealmType().equals(RealmType.LDAP)) {
                ldapRealm = new SecurityLdapRealm(sc.dataStore(), sr);
                break;
            }
        }
        if (ldapRealm == null) {
            throw new IllegalStateException(String.format(
                    "The Organization : '%s' has no LDAP Realm configured",
                    org.getCanonicalName()));
        }
        return ldapRealm.createLdapUser(username);
    }

    private void createCSVUsers(Organization organization,
                                ArrayList<String> users) {

        // Set the delimiter used in file
        final String DELIMITER = ",";

        // Read the file line by line
        for (String line : users) {
            String username = null;

            User user = new User();
            // Get all tokens available in line
            String[] tokens = line.split(DELIMITER);
            username = tokens[0];

            // ignore header
            if (!username.startsWith("#")) {
                if (username.equalsIgnoreCase("username"))
                    continue;

                user.setActive(true);
                user.setCreatedDateTime(new Date());

                IAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        null, null, username, tokens[1]);

                Contact contact = new Contact();
                contact.setFirstName(tokens[2]);
                contact.setMiddleName(tokens[3]);
                contact.setLastName(tokens[4]);
                contact.setEmailAddress(tokens[5]);

                user.setJobTitle(tokens[6]);
                Address userAddress = new Address();
                userAddress.setPhone(tokens[7]);

                contact.getAddresses().add(userAddress);
                user.setContact(contact);
                if (tokens.length == 9 && !tokens[8].isEmpty()
                        && tokens[8].equalsIgnoreCase("Y"))
                    user.setChangePasswordAtFirstLogin(true);
                user.setId(null);

                try {
                    user = sc.dataStore().getUserDataStore()
                            .create(organization.getId(), user, getSecurityManager().getSubjectManager().getUser());
                    sc.dataStore().getUserDataStore()
                            .addAuthenticationTokenToUser(user, token);

                    getLogger().debug("created user from csv : {}", username);
                } catch (Exception e) {
                    getLogger().error(
                            "error persisting user : {}, reason : {}",
                            username, e.getMessage());
                }
            }
        }
    }

    private List<CSVJsonUtil> validateCSV(InputStream inputStream) {

        List<CSVJsonUtil> resultMap = new ArrayList<>();

        try {
            BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(inputStream));

            // Set the delimiter used in file
            final String DELIMITER = ",";

            String line;
            // Read the file line by line
            int lineNumber = 0;
            while ((line = fileReader.readLine()) != null) {
                String username = null;
                lineNumber++;

                try {
                    User u = new User();
                    // Get all tokens available in line
                    String[] tokens = line.split(DELIMITER);
                    if (null == tokens)
                        throw new SecurityException(String.format(
                                "invalid csv, only '%s' delimeter is allowed.",
                                DELIMITER));

                    if (null == tokens[0]) {
                        resultMap.add(new CSVJsonUtil(line, false,
                                "missing required property : Username",
                                lineNumber));
                        continue;
                    }

                    username = tokens[0];
                    // ignore header
                    if (!username.startsWith("#")) {
                        if (username.equalsIgnoreCase("username"))
                            continue;

                        if (tokens[1].isEmpty()) {
                            u.setUsername(username);
                            resultMap
                                    .add(new CSVJsonUtil(
                                            line,
                                            false,
                                            "missing required property : Password, at Index : 2",
                                            lineNumber));
                            continue;
                        }
                        if (tokens[5].isEmpty()) {
                            u.setUsername(username);
                            resultMap
                                    .add(new CSVJsonUtil(
                                            line,
                                            false,
                                            "missing required property : Email at Index : 6",
                                            lineNumber));
                            continue;
                        }

                        // check user already exists
                        try {
                            String domain = getSecurityManager()
                                    .getSubjectManager().getTenant()
                                    .getDomain();

                            User user = sc.dataStore().getUserDataStore()
                                    .getUserByUsername(domain, username);
                            if (null != user) {
                                resultMap
                                        .add(new CSVJsonUtil(
                                                line,
                                                false,
                                                String.format(
                                                        "User with username : %s already exists",
                                                        username), lineNumber));
                                continue;
                            }
                        } catch (Exception e) {
                            getLogger()
                                    .debug("error getting user by username : {}, reason : {}",
                                            username, e.getMessage());
                        }
                        // valid user
                        resultMap.add(new CSVJsonUtil(line, true, null,
                                lineNumber));
                    }

                } catch (IndexOutOfBoundsException e) {
                    getLogger().warn(
                            "error parsing csv line : {}, reason : {}", line,
                            e.getMessage());
                    resultMap.add(new CSVJsonUtil(line, false,
                            formatIndexOutOfBoundsException(e.getMessage()),
                            lineNumber));
                }
            }

            getLogger().info("validate import users csv complete.");

            fileReader.close();
            // inputStream.close();

        } catch (IOException e) {
            getLogger().error("error validating users from csv, reason : {}",
                    e.getMessage());
        }
        return resultMap;
    }

    private String formatIndexOutOfBoundsException(String index) {
        switch (index) {
            case "1":
                return "missing required property : Password";
            case "5":
                return "missing required property : Email";
            default:
                return "Invalid Format/ Missing Delimeter ','";
        }
    }

    @Override
    public boolean deleteUsers(ArrayList<Long> ids) throws Exception {
    	for(Long id : ids) {
    		deleteUser(id);
    	}
    	return true;
    }

	@Override
	public Serializable restCommand(String method, String urlSuffix,
			JsonArg body, Long page, Long start, Long limit, String query,
			String sortersJson, String filtersJson) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String importUsersJson(InputStream inputStream) throws Exception{
		Gson gson = new Gson();
		Type datasetListType = new TypeToken<Collection<UserJsonHelper>>() {}.getType();
    	List<UserJsonHelper> datasets = JsonObjectLoader.load(inputStream, datasetListType);
    	List<User> retUserList = new ArrayList<>();
    	long organizationId;
    	User existingUser = getSecurityManager().getSubjectManager().getUser();
    	
    	Organization existingOrg = getSecurityManager().getSubjectManager().getOrganization();
    	organizationId = existingOrg.getId();
    	
    	for (UserJsonHelper objOrg : datasets){
    		Organization org = null;
    		if (objOrg.getName() != null){
    			org = new Organization();
        		org.setCanonicalName(objOrg.getName());
        		org.setDescription(objOrg.getDescription());
        		Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
    			try{
    				org = sc.dataStore().getOrganizationDataStore().create(tenant.getId(), org, existingUser);
    				organizationId = org.getId();
    			}catch (ItemAlreadyExistsException | SecurityDataException e){
    				org = existingOrg;
    				getLogger().info("Organization "+org.getCanonicalName() +" already exist ..");
    			}
    		}
    		
    		List<importUsers> userList = objOrg.getUserList();
    		for (importUsers userObj : userList){
    			User user = new User();
        		user.setActive(true);
        		user.setUsername(userObj.getUsername());
        		
        		List<RoleEntity> roleEntity = new ArrayList<>();
        		List<String> roles = userObj.getAssigned_roles();
        		for (String userRoles : roles){
        			RoleEntity role = new RoleEntity();
        			role.setCanonicalName(userRoles);
        			roleEntity.add(role);
        		}
        		
        		List<UserGroupEntity> lstGrpEntity = new ArrayList<>();
        		List<String> grps = userObj.getAssigned_groups();
        		for (String userRoles : grps){
        			UserGroupEntity grpEntity = new UserGroupEntity();
        			grpEntity.setCanonicalName(userRoles);
        			lstGrpEntity.add(grpEntity);
        		}
        		Contact contact = new Contact();
        		contact.setEmailAddress(userObj.getEmail());
        		contact.setFirstName(userObj.getFirst_name());
        		contact.setLastName(userObj.getLast_name());
        		contact.setMiddleName(userObj.getMiddle_name());
        		user.setJobTitle(userObj.getTitle());
        		user.setContact(contact);
        		try{
        			User retUser = sc.dataStore().getUserDataStore().importUsers(organizationId, user, roleEntity, lstGrpEntity, org, userObj.getPassword(), existingUser);
        			retUserList.add(retUser);
        		} catch (ItemAlreadyExistsException | SecurityDataException e){
        			 getLogger().info(user.getUsername() +" already exist ..");
        		}
    		}
    	}
    	if (retUserList == null || retUserList.size() == 0){
    		throw new ItemAlreadyExistsException("All users", " specified Tenant.");
    	}
    	return gson.toJson(retUserList);
	}
	
	
	@Override
	public String validateImportUsers(InputStream inputStream) throws Exception {
		InputStream schemaPath = null; 
		String inputData = JsonObjectLoader.StreamToString(inputStream);
    	try{
    		schemaPath = UserHandler.class.getResourceAsStream("/user_validation_schema.json");
    	}catch (Exception e){
    		schemaPath = new FileInputStream("/user_validation_schema.json");
    	}
		String response = new JsonValidator().ValidateJsonFromSchema(schemaPath, inputData);
		if (schemaPath != null){
   			schemaPath.close();
   		}
		System.out.println(response);
   		if (!response.equalsIgnoreCase("success")){
   			return response;
   		}
   		Gson gson = new Gson();
		Type datasetListType = new TypeToken<Collection<UserJsonHelper>>() {}.getType();
    	List<UserJsonHelper> datasets = JsonObjectLoader.load(inputData, datasetListType);
    	List<UsersValidator> retUserList = new ArrayList<>();
    	User existingUser = getSecurityManager().getSubjectManager().getUser();
    	Organization existingOrg = getSecurityManager().getSubjectManager().getOrganization();
    	Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
    	long organizationId = existingOrg.getId();
    	
    	for (UserJsonHelper objOrg : datasets){
    		OrgValidationHelper helper = null;
    		Organization org = null;
    		if (objOrg.getName() != null){
    			org = new Organization();
        		org.setCanonicalName(objOrg.getName());
        		org.setDescription(objOrg.getDescription());
    			try{
    				helper = sc.dataStore().getOrganizationDataStore().validateOrgImports(tenant.getId(), org);
    			}catch (ItemAlreadyExistsException | SecurityDataException e){
    				org = existingOrg;
    				getLogger().info("Organization "+org.getCanonicalName() +" already exist ..");
    			}
    		}
    		
    		List<importUsers> userList = objOrg.getUserList();
    		for (importUsers userObj : userList){
    			User user = new User();
        		user.setActive(true);
        		user.setUsername(userObj.getUsername());
        		
        		List<RoleEntity> roleEntity = new ArrayList<>();
        		List<String> roles = userObj.getAssigned_roles();
        		for (String userRoles : roles){
        			RoleEntity role = new RoleEntity();
        			role.setCanonicalName(userRoles);
        			roleEntity.add(role);
        		}
        		
        		List<UserGroupEntity> lstGrpEntity = new ArrayList<>();
        		List<String> grps = userObj.getAssigned_groups();
        		for (String userRoles : grps){
        			UserGroupEntity grpEntity = new UserGroupEntity();
        			grpEntity.setCanonicalName(userRoles);
        			lstGrpEntity.add(grpEntity);
        		}
        		
        		UsersValidator validator = (new OrgValidationHelper()).new UsersValidator();
        		
        		if (helper.getDescription().equalsIgnoreCase(ImportValidatorErrorCodes._VALID_ENTITY)){
					validator.setName(user.getUsername());
					validator.setErrorCode(ImportValidatorErrorCodes._VALID_ENTITY);
				} else if (helper.getDescription().equalsIgnoreCase(ImportValidatorErrorCodes._INVALID_ENTITY)){
					validator.setName(user.getUsername());
					validator.setErrorCode(ImportValidatorErrorCodes._INVALID_ENTITY);
				} else {
					try{
						validator = sc.dataStore().getUserDataStore().validateUserImports(tenant.getId(), user);
	        			retUserList.add(validator);
	        		} catch (ItemAlreadyExistsException | SecurityDataException e){
	        			 getLogger().info(user.getUsername() +" already exist ..");
	        		}
				}
    		}
    	}
    	if (retUserList == null || retUserList.size() == 0){
    		throw new ItemAlreadyExistsException("All users", " specified Tenant.");
    	}
    	return gson.toJson(retUserList);
	}
}

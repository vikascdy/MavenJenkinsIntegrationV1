package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.isc.json.JsonArg;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.handler.rest.ITenantHandler;
import com.edifecs.epp.security.jpa.entity.*;
import com.edifecs.epp.security.jpa.helper.ObjectConverter;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper.UsersValidator;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.GroupsValidator;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.RolesValidator;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.util.JsonObjectLoader;
import com.edifecs.epp.security.service.util.JsonValidator;
import com.edifecs.epp.security.service.util.TenantHelper;
import com.edifecs.epp.security.service.util.TenantHelper.Groups;
import com.edifecs.epp.security.service.util.TenantHelper.Roles;
import com.edifecs.epp.security.service.util.UserJsonHelper;
import com.edifecs.epp.security.service.util.UserJsonHelper.importUsers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hibernate.exception.ConstraintViolationException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;

public class TenantHandler extends AbstractSecurityRestHandler<Tenant>
        implements ITenantHandler {

    private PasswordPolicy defPasswordPolicy = new PasswordPolicy();

    public TenantHandler(SecurityContext context) {
        super(context, Tenant.class);
    }

    public PasswordPolicy getDefPasswordPolicy() {
        return defPasswordPolicy;
    }

    public void setDefPasswordPolicy(PasswordPolicy defPasswordPolicy) {
        this.defPasswordPolicy = defPasswordPolicy;
    }

    @Override
    public Tenant get(String url) throws Exception {
        final long id = idFromUrl(url);
        return sc.dataStore().getTenantDataStore().getById(id);
    }

    @Override
    public Collection<Tenant> list(Pagination pg) throws Exception {
        final Collection<Tenant> tenants;
        if (pg.limit() == 0)
            tenants = sc.dataStore().getTenantDataStore().getAll();
        else
            tenants = sc.dataStore().getTenantDataStore()
                    .getRange(pg.start(), pg.limit());
        return sort(tenants, pg.sorters());
    }

    @Override
    public Tenant post(Tenant tenant) throws Exception {
        Site site = getSecurityManager().getSubjectManager().getSite();
        return createTenant(site, tenant);
    }

    @Override
    public Tenant put(String url, Tenant tenant) throws Exception {
        final long id = idFromUrl(url);
        if (tenant.getId() != id)
            tenant.setId(id);
        return updateTenant(tenant);
    }

    @Override
    public void delete(String url) throws Exception {
        final long id = idFromUrl(url);
        final Tenant tenant = new Tenant();
        tenant.setId(id);
        sc.dataStore().getTenantDataStore().delete(tenant);
    }

    @Override
    public Tenant createTenant(Tenant tenant) throws Exception {
        Site site = getSecurityManager().getSubjectManager().getSite();
        return createTenant(site, tenant);
    }

    @Override
    public Tenant createTenantForSite(Site site, Tenant tenant)
            throws Exception {
        return createTenant(site, tenant);
    }

    private Tenant createTenant(Site site, Tenant tenant) throws Exception {
        tenant.setPasswordPolicy(getDefPasswordPolicy());
        return sc.dataStore().getTenantDataStore().create(site.getId(), tenant, getSecurityManager().getSubjectManager().getUser());
    }

    public boolean deleteTenant(Long id) throws Exception {
        Tenant t = new Tenant();
        t.setId(id);
        sc.dataStore().getTenantDataStore().delete(t);
        return true;
    }

    public Tenant getTenantById(Long id) throws Exception {
        return sc.dataStore().getTenantDataStore().getById(id);
    }

    public PaginatedList<Tenant> getTenants(long startRecord, long recordCount)
            throws Exception {
        return sc.dataStore().getTenantDataStore()
                .getPaginatedRange(startRecord, recordCount);
    }

    public Tenant updateTenant(Tenant tenant) throws Exception {
        try {
            Tenant r = sc.dataStore().getTenantDataStore().update(getSecurityManager().getSubjectManager().getSite()
                            .getId(), tenant,
                    getSecurityManager().getSubjectManager().getUser());
            return r;
        } catch (Exception e) {
            if (e.getCause().getClass().isAssignableFrom(ConstraintViolationException.class))
                throw new ItemAlreadyExistsException("Tenant", tenant.getCanonicalName(), e);
            else
                throw new SecurityDataException(e);
        }
    }

    @Override
    public Tenant getTenantByName(String canonicalName) throws Exception {
        return sc.dataStore().getTenantDataStore()
                .getTenantByName(canonicalName);
    }

    @Override
    public Tenant updateTenantPasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy) throws Exception {
        return sc.dataStore().getTenantDataStore()
                .updateTenantPasswordPolicy(tenantId, passwordPolicy);
    }

    @Override
    public Boolean updateTenantLogo(Long tenantId, String data) {
        return sc.dataStore().getTenantDataStore().updateLogo(tenantId, data);
    }

    @Override
    public String getTenantLogo(String tenant) {
        if (tenant == null) {
            tenant = getSecurityManager().getSubjectManager().getTenant().getCanonicalName();
        }
        return sc.dataStore().getTenantDataStore().getLogo(tenant);
    }

    @Override
    public Boolean updateTenantLandingPage(Long tenantId, String landingPage) {
        return sc.dataStore().getTenantDataStore().updateLandingPage(tenantId, landingPage);
    }

    @Override
    public Boolean setTenantLandingPage(Long tenantId, String landingPage) {
        return sc.dataStore().getTenantDataStore().updateLandingPage(tenantId, landingPage);
    }

    @Override
    public String getTenantLandingPage(Long tenantId) {
        return sc.dataStore().getTenantDataStore().getLandingPage(tenantId);
    }

    @Override
    public boolean deleteTenants(ArrayList<Long> ids) throws Exception {
        for (Long id : ids) {
            deleteTenant(id);
        }
        return true;

    }

    @Override
    public String importTenantFromJson(InputStream inputStream) throws Exception {
    	
    	Gson gson = new Gson();
        Type datasetListType = new TypeToken<Collection<TenantHelper>>() { }.getType();
        List<TenantHelper> datasets = JsonObjectLoader.load(inputStream, datasetListType);
        User existingUser = getSecurityManager().getSubjectManager().getUser();
        Site site = getSecurityManager().getSubjectManager().getSite();
        Collection<TenantEntity> listTenants = new ArrayList<>();
        
        for (TenantHelper helper : datasets) {
        	
        	TenantEntity tenantEntity = new TenantEntity();
        	tenantEntity.setCanonicalName(helper.getName());
        	tenantEntity.setDescription(helper.getDescription());
        	
        	Tenant tenant = (Tenant) ObjectConverter.jpaToApi(tenantEntity);
        	try{
        		tenant = sc.dataStore().getTenantDataStore().create(site.getId(), tenant, existingUser);
        	} catch (ItemAlreadyExistsException | SecurityDataException e){
        		tenant = sc.dataStore().getTenantDataStore().getTenantByName(tenant.getCanonicalName());
        	}
        	tenantEntity.setId(tenant.getId());
        	
        	List<OrganizationEntity> orgList = new ArrayList<>();
        	List<RoleEntity> listRoles = new ArrayList<>();
        	List<UserGroupEntity> listGroups = new ArrayList<>();
        	
            // Need to fix the list of groups
            List<Groups> groupsList = helper.getListOfGroups();
            for (Groups objGroup : groupsList) {
                UserGroupEntity groupEntity = new UserGroupEntity();
                groupEntity.setCanonicalName(objGroup.getName());
                groupEntity.setDescription(objGroup.getDescription());

                //Adding roles in groups
                List<RoleEntity> roleList = new ArrayList<>();
                List<String> objRoles = objGroup.getRoles();
                for (String rolesname : objRoles) {
                    RoleEntity role = new RoleEntity();
                    role.setCanonicalName(rolesname);
                    roleList.add(role);
                }
                groupEntity.setRoles(roleList);

                //Adding the users in the usergroup
                List<UserEntity> userList = new ArrayList<>();
                List<String> objUser = objGroup.getUsers();
                for (String rolesname : objUser) {
                    User user = new User();
                    user.setUsername(rolesname);
                    UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);
                    userList.add(userDB);
                }
                groupEntity.setUsers(userList);
                UserGroup userGroupDB = (UserGroup) ObjectConverter.jpaToApi(groupEntity);

                try{
                	userGroupDB = sc.dataStore().getUserGroupDataStore().importgroups(tenant.getId(), userGroupDB);
                } catch (ItemAlreadyExistsException | SecurityDataException e){
        			getLogger().info(userGroupDB.getCanonicalName() +" already exist ..");
        		}
                
                groupEntity = (UserGroupEntity) ObjectConverter.apiToJpa(userGroupDB);
                listGroups.add(groupEntity);
            }

            //Handling the roles
            List<Roles> listOfRoles = helper.getListOfRoles();
            for (Roles objRole : listOfRoles) {
                RoleEntity entity = new RoleEntity();
                entity.setCanonicalName(objRole.getName());
                entity.setDescription(objRole.getDescription());
                List<String> lstPermissions = objRole.getPermissions();
                List<PermissionEntity> permissions = new ArrayList<>();
                for (String permission : lstPermissions) {
                    PermissionEntity perEntity = new PermissionEntity();
                    perEntity.setCanonicalName(permission);
                    permissions.add(perEntity);
                }
                entity.setPermissions(permissions);
                Role roleDB = (Role) ObjectConverter.jpaToApi(entity);
                try{
                	roleDB = sc.dataStore().getRoleDataStore().importRoles(tenant.getId(), roleDB);
                } catch (ItemAlreadyExistsException | SecurityDataException e){
        			getLogger().info(roleDB.getCanonicalName() +" already exist ..");
        		}
                
                entity = (RoleEntity) ObjectConverter.apiToJpa(roleDB);
                listRoles.add(entity);
            }
            //Handling the list of Users
            List<UserJsonHelper> listOfUsers = helper.getListOfOrganization();
            
        	for (UserJsonHelper objOrg : listOfUsers){
        		Organization org = new Organization();
        		OrganizationEntity entity = new OrganizationEntity();
        		
        		org.setCanonicalName(objOrg.getName());
        		org.setDescription(objOrg.getDescription());
        		
        		try{
        			org = sc.dataStore().getOrganizationDataStore().create(tenant.getId(), org, existingUser);
        		}catch(ItemAlreadyExistsException | SecurityDataException e){
        			org = sc.dataStore().getOrganizationDataStore().getOrganizationByName(objOrg.getName());
        			getLogger().info(org.getCanonicalName() +" already exist ..");
        		}
        		entity = (OrganizationEntity) ObjectConverter.apiToJpa(org);
        		
        		List<importUsers> userList = objOrg.getUserList();
        		List<UserEntity> objUserDB = new ArrayList<>();
        		
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
            		for (String userGrp : grps){
            			UserGroupEntity grpEntity = new UserGroupEntity();
            			grpEntity.setCanonicalName(userGrp);
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
            			user = sc.dataStore().getUserDataStore().importUsers(org.getId(), user, roleEntity, lstGrpEntity, org, userObj.getPassword(), existingUser);
            		}catch (ItemAlreadyExistsException | SecurityDataException e){
            			getLogger().info(user.getUsername() +" already exist ..");
            		}
            		
            		UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);
        			objUserDB.add(userDB);
        		}
        		entity.setUsers(objUserDB);
        		orgList.add(entity);
        	}
        	tenantEntity.setRoles(listRoles);
        	tenantEntity.setGroups(listGroups);
        	tenantEntity.setOrganizations(orgList);
        	listTenants.add(tenantEntity);
        }
        if (listTenants == null || listTenants.size() == 0){
    		throw new ItemAlreadyExistsException("All Tenants", " specified domain.");
    	}
        return gson.toJson(listTenants);
    }
    
    
    @Override
    public String validateImportTenants(InputStream inputStream) throws Exception {
    	String inputData = JsonObjectLoader.StreamToString(inputStream);
    	InputStream schemaPath = null; 
    	try{
    		schemaPath = TenantHandler.class.getResourceAsStream("/tenant_validation_schema.json");
    	}catch (Exception e){
    		schemaPath = new FileInputStream("/tenant_validation_schema.json");
    	}
   		String response = new JsonValidator().ValidateJsonFromSchema(schemaPath, inputData);
   		if (schemaPath != null){
   			schemaPath.close();
   		}
   		if (!response.equalsIgnoreCase("success")){
   			return response;
   		}
   		
    	Gson gson = new Gson();
        Type datasetListType = new TypeToken<Collection<TenantHelper>>() { }.getType();
        List<TenantHelper> datasets = JsonObjectLoader.load(inputData, datasetListType);
        User existingUser = getSecurityManager().getSubjectManager().getUser();
        Site site = getSecurityManager().getSubjectManager().getSite();
        Collection<TenantValidationHelper> listTenants = new ArrayList<>();
        
        tenantLoop: for (TenantHelper helper : datasets) {
        	
        	TenantValidationHelper validator = new TenantValidationHelper();
        	Tenant tenant = new Tenant();
        	tenant.setCanonicalName(helper.getName());
        	tenant.setDescription(helper.getDescription());
        	
        	validator = sc.dataStore().getTenantDataStore().validateTenantImport(tenant);
        	
        	if (validator.getDescription().equalsIgnoreCase(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST)){
        		tenant = sc.dataStore().getTenantDataStore().getTenantByName(tenant.getCanonicalName());
        	} else {
        		listTenants.add(validator);
        		continue tenantLoop;
        	}
        	List<OrgValidationHelper> orgList = new ArrayList<>();
        	List<RolesValidator> listRoles = new ArrayList<>();
        	List<GroupsValidator> listGroups = new ArrayList<>();
        	
            // Need to fix the list of groups
            List<Groups> groupsList = helper.getListOfGroups();
            for (Groups objGroup : groupsList) {
                GroupsValidator gValidator = (new TenantValidationHelper()).new GroupsValidator();
                UserGroup grp = new UserGroup();
                grp.setCanonicalName(objGroup.getName());
                grp.setDescription(objGroup.getDescription());

                //Adding roles in groups
                List<RolesValidator> roleList = new ArrayList<>();
                List<String> objRoles = objGroup.getRoles();
                for (String rolesname : objRoles) {
                	RolesValidator validatorR = (new TenantValidationHelper()).new RolesValidator();
                    Role role = new Role();
                    role.setCanonicalName(rolesname);
                    validatorR = sc.dataStore().getRoleDataStore().ValidateRolesImport(tenant.getId(), role);
                    roleList.add(validatorR);
                }

                //Adding the users in the usergroup
                List<UsersValidator> userList = new ArrayList<>();
                List<String> objUser = objGroup.getUsers();
                for (String rolesname : objUser) {
                	UsersValidator rValidator = (new OrgValidationHelper()).new UsersValidator();
                    User user = new User();
                    user.setUsername(rolesname);
                    rValidator = sc.dataStore().getUserDataStore().validateUserImports(tenant.getId(), user);
                    userList.add(rValidator);
                }
                gValidator = sc.dataStore().getUserGroupDataStore().validateImportGroups(tenant.getId(), grp);
                gValidator.setRolesValidator(roleList);
                gValidator.setUsersValidator(userList);
                listGroups.add(gValidator);
            }

            //Handling the roles
            List<Roles> listOfRoles = helper.getListOfRoles();
            for (Roles objRole : listOfRoles) {
            	RolesValidator rValidator = (new TenantValidationHelper()).new RolesValidator();
                Role entity = new Role();
                entity.setCanonicalName(objRole.getName());
                entity.setDescription(objRole.getDescription());
                rValidator = sc.dataStore().getRoleDataStore().ValidateRolesImport(tenant.getId(), entity);
                listRoles.add(rValidator);
            }
            //Handling the list of Users
            List<UserJsonHelper> listOfUsers = helper.getListOfOrganization();
            
        	userLoop: for (UserJsonHelper objOrg : listOfUsers){
        		Organization org = new Organization();
        		OrgValidationHelper oValidator = new OrgValidationHelper();
        		
        		org.setCanonicalName(objOrg.getName());
        		org.setDescription(objOrg.getDescription());
        		oValidator = sc.dataStore().getOrganizationDataStore().validateOrgImports(tenant.getId(), org);
        		if (oValidator.getDescription().equalsIgnoreCase(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST)){
            		org = sc.dataStore().getOrganizationDataStore().getOrganizationByName(org.getCanonicalName());
            	} else {
            		orgList.add(oValidator);
            		continue userLoop;
            	}
        		List<importUsers> userList = objOrg.getUserList();
        		List<UsersValidator> objUserDB = new ArrayList<>();
        		
        		for (importUsers userObj : userList){
        			UsersValidator uValidator = (new OrgValidationHelper()).new UsersValidator();
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
            		for (String userGrp : grps){
            			UserGroupEntity grpEntity = new UserGroupEntity();
            			grpEntity.setCanonicalName(userGrp);
            			lstGrpEntity.add(grpEntity);
            		}
            		uValidator = sc.dataStore().getUserDataStore().validateUserImports(tenant.getId(), user);
        			objUserDB.add(uValidator);
        		}
        		oValidator.setUserList(objUserDB);
        		orgList.add(oValidator);
        	}
        	validator.setRoles(listRoles);
        	validator.setGroups(listGroups);
        	validator.setOrganization(orgList);
        	listTenants.add(validator);
        }
        return gson.toJson(listTenants);
    }

	@Override
	public Serializable restCommand(String method, String urlSuffix,
			JsonArg body, Long page, Long start, Long limit, String query,
			String sortersJson, String filtersJson) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.isc.json.JsonArg;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.handler.rest.IOrganizationHandler;
import com.edifecs.epp.security.jpa.entity.OrganizationEntity;
import com.edifecs.epp.security.jpa.entity.RoleEntity;
import com.edifecs.epp.security.jpa.entity.UserEntity;
import com.edifecs.epp.security.jpa.entity.UserGroupEntity;
import com.edifecs.epp.security.jpa.helper.ObjectConverter;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper.UsersValidator;
import com.edifecs.epp.security.service.RealmManager;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.util.JsonObjectLoader;
import com.edifecs.epp.security.service.util.JsonValidator;
import com.edifecs.epp.security.service.util.UserJsonHelper;
import com.edifecs.epp.security.service.util.UserJsonHelper.importUsers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrganizationHandler extends
        AbstractSecurityRestHandler<Organization> implements
        IOrganizationHandler {

    public OrganizationHandler(SecurityContext context) {
        super(context, Organization.class);
    }

    @Override
    public Organization get(String url) throws Exception {
        final long id = idFromUrl(url);
        return sc.dataStore().getOrganizationDataStore().getById(id);
    }

    @Override
    public Collection<Organization> list(Pagination pg) throws Exception {
        final Collection<Organization> organizations;
        if (pg.limit() == 0)
            organizations = sc.dataStore().getOrganizationDataStore().getAll();
        else
            organizations = sc.dataStore().getOrganizationDataStore()
                    .getRange(pg.start(), pg.limit());
        return sort(organizations, pg.sorters());
    }

    @Override
    public Organization post(Organization organization) throws Exception {
        return createOrganization(organization);
    }

    @Override
    public Organization put(String url, Organization organization)
            throws Exception {
        final long id = idFromUrl(url);
        if (organization.getId() != id)
            organization.setId(id);
        return updateOrganization(organization);
    }

    @Override
    public void delete(String url) throws Exception {
        final long id = idFromUrl(url);
        final Organization organization = new Organization();
        organization.setId(id);
        sc.dataStore().getOrganizationDataStore().delete(organization);
    }

    public Organization createOrganization(Organization organization)
            throws Exception {
        Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
        return createOrganizationForTenant(tenant, organization);
    }

    public Organization createOrganizationForTenant(Tenant tenant,
                                                    Organization organization) throws Exception {
        Organization org = sc.dataStore().getOrganizationDataStore()
                .create(tenant.getId(), organization, getSecurityManager().getSubjectManager().getUser());
        return org;
    }

    public void deleteOrganization(Long id) throws Exception {
        final Organization org = new Organization();
        org.setId(id);
        sc.dataStore().getOrganizationDataStore().delete(org);
        getLogger().info("Organization deleted : {}", id);
    }

    public Organization getOrganizationById(Long id) throws Exception {
        return sc.dataStore().getOrganizationDataStore().getById(id);
    }

    public PaginatedList<Organization> getOrganizations(long startRecord,
                                                        long recordCount) throws Exception {
        return sc.dataStore().getOrganizationDataStore()
                .getPaginatedRange(startRecord, recordCount);
    }

    public Organization updateOrganization(Organization organization)
            throws Exception {
        Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
        Organization org = sc.dataStore().getOrganizationDataStore()
                .update(tenant.getId(), organization, getSecurityManager().getSubjectManager().getUser());
        return org;
    }

    public SecurityRealm addRealmToOrganization(SecurityRealm realm,
                                                Long organizationId) throws Exception {
        SecurityRealm sr = sc.dataStore().getOrganizationDataStore()
                .addRealmToOrganization(organizationId, realm);
        RealmManager.refresh(organizationId);
        return sr;
    }

    public SecurityRealm updateRealm(SecurityRealm realm, Long organizationId)
            throws Exception {
        SecurityRealm securityRealm = sc.dataStore().getOrganizationDataStore()
                .updateRealm(realm);
        RealmManager.refresh(organizationId);
        getLogger().info("Updated Realm : {}", realm.getName());
        return securityRealm;
    }

    @Override
    public Collection<Organization> getChildOrganizationsById(
            Long organizationId) throws Exception {
        return sc.dataStore().getOrganizationDataStore()
                .getChildOrganizationsById(organizationId);
    }

    @Override
    public void addChildOrganization(Long organizationId,
                                     Long childOrganizationId) throws Exception {

        Organization childOrganization = new Organization();
        childOrganization.setId(childOrganizationId);
        sc.dataStore().getOrganizationDataStore()
                .addChildOrganization(organizationId, childOrganization);
    }

    @Override
    public void removeChildOrganization(Long organizationId,
                                        Long childOrganizationId) throws Exception {
        Organization childOrganization = new Organization();
        childOrganization.setId(childOrganizationId);
        sc.dataStore().getOrganizationDataStore()
                .removeChildOrganization(organizationId, childOrganization);
    }

    @Override
    public PaginatedList<Organization> getOrganizationsForTenant(Long id,
                                                                 long startRecord, long recordCount) throws Exception {
        return sc.dataStore().getOrganizationDataStore()
                .getOrganizationsForTenant(id, startRecord, recordCount);
    }

    @Override
    public void removeRoleFromOrganization(Long organizationId, Long roleId)
            throws Exception {
        sc.dataStore().getOrganizationDataStore()
                .removeRoleFromOrganization(organizationId, roleId);
    }

    @Override
    public void removeRolesFromOrganization(Long organizationId,
                                            ArrayList<Role> roles) throws Exception {
        for (Role r : roles) {
            sc.dataStore().getOrganizationDataStore()
                    .removeRoleFromOrganization(organizationId, r.getId());
        }
    }

    @Override
    public void addRoleToOrganization(Long organizationId, Long roleId)
            throws Exception {
        sc.dataStore().getOrganizationDataStore()
                .addRoleToOrganization(organizationId, roleId);
    }

    @Override
    public void addRolesToOrganization(Long organizationId,
                                       ArrayList<Role> roles) throws Exception {
        for (Role r : roles) {
            sc.dataStore().getOrganizationDataStore()
                    .addRoleToOrganization(organizationId, r.getId());
        }
    }

    @Override
    public PaginatedList<Organization> getOrganizationsForGroup(Long id,
                                                                long startRecord, long recordCount) throws Exception {
        return sc.dataStore().getOrganizationDataStore()
                .getOrganizationsForGroup(id, startRecord, recordCount);
    }

    @Override
    public OrganizationDetail getOrganizationDetail(Long organizationId)
            throws Exception {
        return sc.dataStore().getOrganizationDataStore()
                .getTransitiveChildOrganizationsForOrganization(organizationId);
    }

    @Override
    public void deleteOrganizations(ArrayList<Long> ids) throws Exception {
        for (Long id : ids) {
            deleteOrganization(id);
        }
    }	
    
   
    @Override
    public String validateImportOrganization(InputStream inputStream)  throws Exception {
    	String inputData = JsonObjectLoader.StreamToString(inputStream);
    	InputStream schemaPath = null; 
    	try{
    		schemaPath = RoleHandler.class.getResourceAsStream("/org_validation_schema.json");
    	}catch (Exception e){
    		schemaPath = new FileInputStream("/org_validation_schema.json");
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
    	List<OrgValidationHelper> orgValidator = new ArrayList<>();
    	User existingUser = getSecurityManager().getSubjectManager().getUser();
    	Organization existingOrg = getSecurityManager().getSubjectManager().getOrganization();
    	Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
    	long organizationId = existingOrg.getId();
    	
    	for (UserJsonHelper objOrg : datasets){
    		List<UsersValidator> retUserList = new ArrayList<>();
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
        		Contact contact = new Contact();
        		contact.setEmailAddress(userObj.getEmail());
        		contact.setFirstName(userObj.getFirst_name());
        		contact.setLastName(userObj.getLast_name());
        		contact.setMiddleName(userObj.getMiddle_name());
        		user.setJobTitle(userObj.getTitle());
        		user.setContact(contact);
        		
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
    		helper.setUserList(retUserList);
    		orgValidator.add(helper);
    	}
    	return gson.toJson(orgValidator);
    }
    
    
    @Override
	public String importOrganizationFromJson(InputStream inputStream)  throws Exception {
    	
    	Gson json = new Gson();
		Type datasetListType = new TypeToken<Collection<UserJsonHelper>>() {}.getType();
    	List<UserJsonHelper> datasets = JsonObjectLoader.load(inputStream, datasetListType);
    	Collection<OrganizationEntity> orgList = new ArrayList<>();
    	//Getting the existing user and tenant
    	User existingUser = getSecurityManager().getSubjectManager().getUser();
    	Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
    	for (UserJsonHelper objOrg : datasets){
    		Map<String, String> myUserCredential = new HashMap<>();
    		Organization org = new Organization();
    		OrganizationEntity entity = new OrganizationEntity();
    		
    		org.setCanonicalName(objOrg.getName());
    		org.setDescription(objOrg.getDescription());
    		
    		try{
    			org = sc.dataStore().getOrganizationDataStore().create(tenant.getId(), org, existingUser);
    		}catch(ItemAlreadyExistsException | SecurityDataException e){
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
        			UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);
        			objUserDB.add(userDB);
        		}catch (ItemAlreadyExistsException | SecurityDataException e){
        			getLogger().info(user.getUsername() +" already exist ..");
        		}
    		}
    		entity.setUsers(objUserDB);
    		orgList.add(entity);
    	}
    	 if (orgList == null || orgList.size() == 0){
     		throw new ItemAlreadyExistsException("All Organizations", " specified domain.");
     	}
    	return json.toJson(orgList);
	}

	@Override
	public Serializable restCommand(String method, String urlSuffix,
			JsonArg body, Long page, Long start, Long limit, String query,
			String sortersJson, String filtersJson) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

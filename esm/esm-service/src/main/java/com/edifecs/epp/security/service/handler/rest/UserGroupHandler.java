package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.isc.json.JsonArg;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.handler.rest.IUserGroupHandler;
import com.edifecs.epp.security.jpa.entity.OrganizationEntity;
import com.edifecs.epp.security.jpa.entity.RoleEntity;
import com.edifecs.epp.security.jpa.entity.UserEntity;
import com.edifecs.epp.security.jpa.entity.UserGroupEntity;
import com.edifecs.epp.security.jpa.helper.ObjectConverter;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper.UsersValidator;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.GroupsValidator;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.RolesValidator;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.util.JsonObjectLoader;
import com.edifecs.epp.security.service.util.JsonValidator;
import com.edifecs.epp.security.service.util.TenantHelper.Groups;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserGroupHandler extends AbstractSecurityRestHandler<UserGroup>
        implements IUserGroupHandler {

    public UserGroupHandler(SecurityContext context) {
        super(context, UserGroup.class);
    }

    @Override
    public UserGroup get(String url) throws Exception {
        final long id = idFromUrl(url);
        return sc.dataStore().getUserGroupDataStore().getById(id);
    }

    @Override
    public Collection<UserGroup> list(Pagination pg) throws Exception {
        final Collection<UserGroup> groups;
        if (pg.limit() == 0)
            groups = sc.dataStore().getUserGroupDataStore().getAll();
        else if (null != pg.query() && !pg.query().isEmpty())
            groups = sc.dataStore().getUserGroupDataStore()
                    .queryGroups(pg.query(), pg.start(), pg.limit());
        else
            groups = sc.dataStore().getUserGroupDataStore()
                    .getRange(pg.start(), pg.limit());
        return sort(groups, pg.sorters());
    }

    @Override
    public UserGroup post(UserGroup group) throws Exception {
        return createGroup(group);
    }

    @Override
    public UserGroup put(String url, UserGroup group) throws Exception {
        final long id = idFromUrl(url);
        if (group.getId() != id)
            group.setId(id);
        return updateGroup(group);
    }

    @Override
    public void delete(String url) throws Exception {
        final long id = idFromUrl(url);
        final UserGroup group = new UserGroup();
        group.setId(id);
        sc.dataStore().getUserGroupDataStore().delete(group);
        getLogger().info("group : {}", id);
    }

    public UserGroup createGroup(UserGroup group) throws Exception {
        Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
        return createGroupForTenant(tenant, group);
    }

    public UserGroup createGroupForTenant(Tenant tenant, UserGroup group)
            throws Exception {
        UserGroup grp = sc.dataStore().getUserGroupDataStore()
                .create(tenant.getId(), group, getSecurityManager().getSubjectManager().getUser());
        return grp;
    }

    public boolean deleteGroup(Long id) throws Exception {
        final UserGroup g = new UserGroup();
        g.setId(id);
        sc.dataStore().getUserGroupDataStore().delete(g);
        getLogger().info("User Group deleted : {}", id);
        return true;
    }

    public PaginatedList<UserGroup> getGroups(long startRecord, long recordCount)
            throws Exception {
        return sc.dataStore().getUserGroupDataStore()
                .getPaginatedRange(startRecord, recordCount);
    }

    public UserGroup getGroupById(Long id) throws Exception {
        return sc.dataStore().getUserGroupDataStore().getById(id);
    }

    public UserGroup updateGroup(UserGroup group) throws Exception {
        Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
        UserGroup grp = sc.dataStore().getUserGroupDataStore().update(tenant.getId(), group,
                getSecurityManager().getSubjectManager
                ().getUser());
        return grp;
    }

    public void addGroupToUser(UserGroup group, User user) throws Exception {
        sc.dataStore().getUserGroupDataStore().addUserToUserGroup(user, group);
        getLogger().info("group :{}, added to user : {}",
                group.getCanonicalName(), user.getUsername());
    }

    public void addGroupsToUser(ArrayList<UserGroup> groups, User user)
            throws Exception {
        for (UserGroup g : groups)
            sc.dataStore().getUserGroupDataStore().addUserToUserGroup(user, g);
    }

    public void addUsersToGroup(UserGroup group, ArrayList<User> users)
            throws Exception {
        for (User u : users)
            sc.dataStore().getUserGroupDataStore().addUserToUserGroup(u, group);
    }

    public void removeUsersFromGroup(UserGroup group, ArrayList<User> users)
            throws Exception {
        for (User u : users)
            sc.dataStore().getUserGroupDataStore()
                    .removeUserFromUserGroup(u, group);
    }

    public void removeGroupsFromUser(ArrayList<UserGroup> groups, User user)
            throws Exception {
        for (UserGroup g : groups)
            sc.dataStore().getUserGroupDataStore()
                    .removeUserFromUserGroup(user, g);
    }

    public void removeGroupFromUser(UserGroup group, User user)
            throws Exception {
        sc.dataStore().getUserGroupDataStore()
                .removeUserFromUserGroup(user, group);
        getLogger().info("group :{}, removed from user : {}",
                group.getCanonicalName(), user.getUsername());
    }

    public PaginatedList<UserGroup> getGroupsForUser(long userId,
                                                     long startRecord, long recordCount) throws Exception {
        User u = new User();
        u.setId(userId);
        return sc
                .dataStore()
                .getUserGroupDataStore()
                .getTransitiveUserGroupsForUser(u.getId(), startRecord,
                        recordCount);
    }

    @Override
    public PaginatedList<UserGroup> getGroupsForTenant(Long id,
                                                       long startRecord, long recordCount) throws Exception {
        return sc.dataStore().getUserGroupDataStore()
                .getGroupsForTenant(id, startRecord, recordCount);
    }

    @Override
    public Collection<UserGroup> getChildGroupsForGroup(UserGroup group)
            throws Exception {
        return sc.dataStore().getUserGroupDataStore()
                .getChildGroupsForUserGroup(group);
    }

    @Override
    public void addChildGroupsToGroup(UserGroup group, UserGroup parentGroup)
            throws Exception {
        sc.dataStore().getUserGroupDataStore()
                .addChildGroupToUserGroup(group, parentGroup);
    }

    @Override
    public void addOrganizationToGroup(UserGroup group,
                                       Organization organization) throws Exception {
        sc.dataStore().getUserGroupDataStore()
                .addOrganizationToUserGroup(organization, group);
    }

    @Override
    public void removeOrganizationFromGroup(UserGroup group,
                                            Organization organization) throws Exception {
        sc.dataStore().getUserGroupDataStore()
                .removeOrganizationFromUserGroup(organization, group);
    }

    @Override
    public void addOrganizationsToGroup(UserGroup group,
                                        ArrayList<Organization> organizations) throws Exception {
        for (Organization o : organizations) {
            addOrganizationToGroup(group, o);
        }
    }

    @Override
    public void removeOrganizationsFromGroup(UserGroup group,
                                             ArrayList<Organization> organizations) throws Exception {
        for (Organization o : organizations) {
            removeOrganizationFromGroup(group, o);
        }
    }
    
    @Override
    public boolean deleteGroups(ArrayList<Long> ids) throws Exception {
    	for(Long id : ids) {
    		deleteGroup(id);
    	}
    	return true;
    }
    
    
    @Override
   	public String importGroupsFromJson(InputStream inputStream) throws Exception {
   		
       	Gson gson = new Gson();
       	List<UserGroupEntity> retUserGrpList = new ArrayList<>();
       	Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
       	User existingUser = getSecurityManager().getSubjectManager().getUser();
       	
   		Type datasetListType = new TypeToken<Collection<Groups>>() {}.getType();
       	List<Groups> datasets = JsonObjectLoader.load(inputStream, datasetListType);
       	for (Groups objGroups : datasets){
       		
       		UserGroupEntity userGrpEntity = new UserGroupEntity();
       		userGrpEntity.setCanonicalName(objGroups.getName());
       		userGrpEntity.setDescription(objGroups.getDescription());
       		
       		List<RoleEntity> entityList = new ArrayList<RoleEntity>();
       		for (String roles : objGroups.getRoles()){
       			RoleEntity entity = new RoleEntity();
       			entity.setCanonicalName(roles);
       			Role role = null;
       			try{
       				role = sc.dataStore().getRoleDataStore().importRoles
       						(tenant.getId(), (Role) ObjectConverter.jpaToApi(entity));
       			} catch (ItemAlreadyExistsException | SecurityDataException e){
       				try{
       					role = sc.dataStore().getRoleDataStore().update
           						(tenant.getId(), (Role) ObjectConverter.jpaToApi(entity), existingUser);
       				} catch (ItemAlreadyExistsException | SecurityDataException e1){
       					// Leave this block as it is ...
       				}
       			}
       			if (role != null){
       				entity = (RoleEntity) ObjectConverter.apiToJpa(role);
           			entityList.add(entity);
       			}
       		}
       		
       		List<UserEntity> userEnt = new ArrayList<UserEntity>();
       		for (String users : objGroups.getUsers()){
       			User entity = new User();
       			entity.setUsername(users);
       			try{
       				entity = sc.dataStore().getUserDataStore().create(entity, existingUser);
       			} catch (ItemAlreadyExistsException | SecurityDataException e){
       				try{
       					entity = sc.dataStore().getUserDataStore().update(entity, existingUser);
       				} catch (ItemAlreadyExistsException | SecurityDataException e1){
       					// Leave this block as it is ...
       					entity = null;
       				}
       			}
       			if (entity != null){
       				userEnt.add((UserEntity) ObjectConverter.apiToJpa(entity));
       			}
       		}
       		
       		List<OrganizationEntity> orgEnt = new ArrayList<OrganizationEntity>();
       		for (String org : objGroups.getOrganizations()){
       			OrganizationEntity entity = new OrganizationEntity();
       			entity.setCanonicalName(org);
       			Organization objOrg = null;
       			try{
       				objOrg = sc.dataStore().getOrganizationDataStore().create
       						(tenant.getId(), (Organization)ObjectConverter.jpaToApi(entity), existingUser);
       			} catch (ItemAlreadyExistsException | SecurityDataException e){
       				try{
       					objOrg = sc.dataStore().getOrganizationDataStore().update
           						(tenant.getId(), (Organization)ObjectConverter.jpaToApi(entity), existingUser);
       				} catch (ItemAlreadyExistsException | SecurityDataException e1){
       					// Leave this block as it is ...
       				}
       			}
       			if (objOrg != null){
       				entity = (OrganizationEntity) ObjectConverter.jpaToApi(objOrg);
       				orgEnt.add(entity);
       			}
       		}
       		
       		UserGroup groupDB = (UserGroup) ObjectConverter.jpaToApi(userGrpEntity);
       		
       		try{
       			groupDB = sc.dataStore().getUserGroupDataStore().importgroups(tenant.getId(), groupDB);
       		}catch (ItemAlreadyExistsException | SecurityDataException e){
       			 getLogger().info("Group "+groupDB.getCanonicalName() +" already exist ..");
       			try{
       				groupDB = sc.dataStore().getUserGroupDataStore().update(tenant.getId(), groupDB, existingUser);
       			} catch (ItemAlreadyExistsException | SecurityDataException e1){
       				// Leave this catch block empty ...
       			}
       		}
       		userGrpEntity = (UserGroupEntity) ObjectConverter.apiToJpa(groupDB);
       		userGrpEntity.setOrganizations(orgEnt);
       		userGrpEntity.setRoles(entityList);
       		userGrpEntity.setUsers(userEnt);
       		retUserGrpList.add(userGrpEntity);
       	}
   		return gson.toJson(retUserGrpList);
   	}
       
       
    @Override
   	public String validateImportGroups(InputStream inputStream) throws Exception {
    	String inputData = JsonObjectLoader.StreamToString(inputStream);
    	InputStream schemaPath = null; 
    	try{
    		schemaPath = UserGroupHandler.class.getResourceAsStream("/group_validation_schema.json");
    	}catch (Exception e){
    		schemaPath = new FileInputStream("/group_validation_schema.json");
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
       	List<GroupsValidator> retUserGrpList = new ArrayList<>();
       	Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
       	
   		Type datasetListType = new TypeToken<Collection<Groups>>() {}.getType();
       	List<Groups> datasets = JsonObjectLoader.load(inputData, datasetListType);
       	for (Groups objGroups : datasets){
       		GroupsValidator userGrpEntity = (new TenantValidationHelper()).new GroupsValidator();
       		
       		List<RolesValidator> entityList = new ArrayList<>();
       		for (String roles : objGroups.getRoles()){
       			RoleEntity entity = new RoleEntity();
       			entity.setCanonicalName(roles);
       			RolesValidator validator = sc.dataStore().getRoleDataStore().
       					ValidateRolesImport(tenant.getId(), (Role) ObjectConverter.jpaToApi(entity));
       			entityList.add(validator);
       		}
       		
       		List<UsersValidator> userEnt = new ArrayList<>();
       		for (String users : objGroups.getUsers()){
       			User entity = new User();
       			entity.setUsername(users);
       			UsersValidator usersValidator = sc.dataStore().getUserDataStore().validateUserImports(tenant.getId(), entity);
       			userEnt.add(usersValidator);
       		}
       		
       		List<OrgValidationHelper> orgEnt = new ArrayList<>();
       		for (String org : objGroups.getOrganizations()){
       			OrganizationEntity entity = new OrganizationEntity();
       			entity.setCanonicalName(org);
       			OrgValidationHelper objOrgValidator = sc.dataStore().getOrganizationDataStore().
       					validateOrgImports(tenant.getId(), (Organization)ObjectConverter.jpaToApi(entity));
       			orgEnt.add(objOrgValidator);
       		}
       		
       			UserGroup grp = new UserGroup();
       			grp.setCanonicalName(objGroups.getName());
       			
       			userGrpEntity = sc.dataStore().getUserGroupDataStore().validateImportGroups(tenant.getId(), grp);
       			userGrpEntity.setRolesValidator(entityList);
       			userGrpEntity.setOrgValidator(orgEnt);
       			userGrpEntity.setUsersValidator(userEnt);
       			retUserGrpList.add(userGrpEntity);
       	}
   		return gson.toJson(retUserGrpList);
   	}

	@Override
	public Serializable restCommand(String method, String urlSuffix,
			JsonArg body, Long page, Long start, Long limit, String query,
			String sortersJson, String filtersJson) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

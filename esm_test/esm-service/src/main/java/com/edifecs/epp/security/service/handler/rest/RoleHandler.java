package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.isc.json.JsonArg;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.handler.rest.IRoleHandler;
import com.edifecs.epp.security.jpa.entity.PermissionEntity;
import com.edifecs.epp.security.jpa.entity.RoleEntity;
import com.edifecs.epp.security.jpa.helper.ObjectConverter;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.RolesValidator;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.util.JsonObjectLoader;
import com.edifecs.epp.security.service.util.JsonValidator;
import com.edifecs.epp.security.service.util.TenantHelper.Roles;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoleHandler extends AbstractSecurityRestHandler<Role> implements
		IRoleHandler {

	public RoleHandler(SecurityContext context) {
		super(context, Role.class);
	}

	@Override
	public Role get(String url) throws Exception {
		final long id = idFromUrl(url);
		return sc.dataStore().getRoleDataStore().getById(id);
	}

	@Override
	public Collection<Role> list(Pagination pg) throws Exception {
		final Collection<Role> roles;
		if (pg.limit() == 0)
			roles = sc.dataStore().getRoleDataStore().getAll();
		else if (null != pg.query() && !pg.query().isEmpty())
			roles = sc.dataStore().getRoleDataStore()
					.queryRoles(pg.query(), pg.start(), pg.limit());
		else
			roles = sc.dataStore().getRoleDataStore()
					.getRange(pg.start(), pg.limit());
		return sort(roles, pg.sorters());
	}

	@Override
	public Role post(Role role) throws Exception {
		return createRole(role);
	}

	@Override
	public Role put(String url, Role role) throws Exception {
		final long id = idFromUrl(url);
		if (role.getId() != id)
			role.setId(id);
		return updateRole(role);
	}

	@Override
	public void delete(String url) throws Exception {
		final long id = idFromUrl(url);
		final Role role = new Role();
		role.setId(id);
		sc.dataStore().getRoleDataStore().delete(role);
	}

	public Role createRole(Role role) throws Exception {
		Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
		return createRoleForTenant(tenant, role);
	}

	public Role createRoleForTenant(Tenant tenant, Role role) throws Exception {
		Role r = sc.dataStore().getRoleDataStore().create(tenant.getId(), role, getSecurityManager().getSubjectManager().getUser());
		return r;
	}

	public boolean deleteRole(Long id) throws Exception {
		Role r = new Role();
		r.setId(id);
		sc.dataStore().getRoleDataStore().delete(r);
		return true;
	}

	public Role getRoleById(Long id) throws Exception {
		return sc.dataStore().getRoleDataStore().getById(id);
	}

	public PaginatedList<Role> getRoles(long startRecord, long recordCount)
			throws Exception {
		return sc.dataStore().getRoleDataStore()
				.getPaginatedRange(startRecord, recordCount);
	}

	public Role updateRole(Role role) throws Exception {
            Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
            Role r = sc.dataStore().getRoleDataStore().update(tenant.getId(), role,
                    getSecurityManager().getSubjectManager().getUser());
			return r;
	}

	public void addChildRoleToRole(Role role, Role parentRole) throws Exception {
		sc.dataStore().getRoleDataStore().addChildRoleToRole(role, parentRole);
	}

	public void addChildRolesToRole(ArrayList<Role> roles, Role parentRole)
			throws Exception {
		for (Role r : roles)
			sc.dataStore().getRoleDataStore().addChildRoleToRole(r, parentRole);
	}

	public void removeChildRolesFromRole(ArrayList<Role> roles, Role parentRole)
			throws Exception {
		for (Role r : roles)
			sc.dataStore().getRoleDataStore()
					.removeChildRoleFromRole(r, parentRole);
	}

	public void addRoleToUser(Role role, User user) throws Exception {
		sc.dataStore().getRoleDataStore().addRoleToUser(user, role);
		getLogger().info("role :{}, added to user : {}",
				role.getCanonicalName(), user.getUsername());
	}

	public void addRolesToUser(ArrayList<Role> roles, User user)
			throws Exception {
		sc.dataStore().getRoleDataStore().addRolesToUser(user, roles);
		getLogger().info(" {} roles, added to user : {}", roles.size(),
				user.getUsername());
	}

	public void removeRolesFromUser(ArrayList<Role> roles, User user)
			throws Exception {
		for (Role r : roles) {
			sc.dataStore().getRoleDataStore().removeRoleFromUser(user, r);
		}
	}

	public void removeRoleFromUser(Role role, User user) throws Exception {
		sc.dataStore().getRoleDataStore().removeRoleFromUser(user, role);
		getLogger().info("role :{}, removed from user : {}",
				role.getCanonicalName(), user.getUsername());
	}

	public PaginatedList<Role> getRolesForUser(long userId, long startRecord,
			long recordCount) throws Exception {
		User u = new User();
		u.setId(userId);
		return sc.dataStore().getRoleDataStore()
				.getTransitiveRolesForUser(u, startRecord, recordCount);
	}

	public PaginatedList<Role> getChildRolesForRole(Role role,
			long startRecord, long recordCount) throws Exception {
		return sc.dataStore().getRoleDataStore()
				.getChildRolesForRole(role, startRecord, recordCount);
	}

	public void addRoleToGroup(Role role, UserGroup group) throws Exception {
		sc.dataStore().getRoleDataStore().addRoleToGroup(group, role);
		getLogger().info("role :{}, added to group : {}",
				role.getCanonicalName(), group.getCanonicalName());
	}

	public void addRolesToGroup(ArrayList<Role> roles, UserGroup group)
			throws Exception {
		sc.dataStore().getRoleDataStore().addRolesToGroup(group, roles);
	}

	public void removeRolesFromGroup(ArrayList<Role> roles, UserGroup group)
			throws Exception {
		for (Role r : roles)
			sc.dataStore().getRoleDataStore().removeRoleFromGroup(group, r);
	}

	public void removeRoleFromGroup(Role role, UserGroup group)
			throws Exception {
		sc.dataStore().getRoleDataStore().removeRoleFromGroup(group, role);
		getLogger().info("role :{}, removed from group : {}",
				role.getCanonicalName(), group.getCanonicalName());
	}

	public Collection<Role> getRolesForGroup(Long groupId, long startRecord,
                                             long recordCount) throws Exception {
		UserGroup group = new UserGroup();
		group.setId(groupId);
		return sc.dataStore().getRoleDataStore()
				.getTransitiveRolesForGroup(group);
	}

	public Role getRoleByRoleName(String roleName) throws Exception {
		return sc.dataStore().getRoleDataStore().getRoleByRoleName(roleName);
	}

	@Override
	public PaginatedList<Role> getRolesForTenant(Long id, long startRecord,
			long recordCount) throws Exception {
		return sc.dataStore().getRoleDataStore()
				.getRolesForTenant(id, startRecord, recordCount);
	}

	@Override
	public PaginatedList<Role> getRolesForOrganization(Long organizationId,
			long startRecord, long recordCount) throws Exception {
		return sc
				.dataStore()
				.getRoleDataStore()
				.getRolesForOrganization(organizationId, startRecord,
						recordCount);
	}

	@Override
	public boolean deleteRoles(ArrayList<Long> ids) throws Exception {
		for(Long id : ids) {
    		deleteRole(id);
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
	public String importRoleFromJson(InputStream inputStream) throws Exception {
		Gson gson = new Gson();
		 
		Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
		Collection<Role> retRoleList = new ArrayList<>();
		Type datasetListType = new TypeToken<Collection<Roles>>() {}.getType();
    	List<Roles> datasets = JsonObjectLoader.load(inputStream, datasetListType);
    	for (Roles objRoles : datasets){
    		RoleEntity roleEntity = new RoleEntity();
    		roleEntity.setCanonicalName(objRoles.getName());
    		roleEntity.setDescription(objRoles.getDescription());
    		List<PermissionEntity> entityList = new ArrayList<PermissionEntity>();
    		for (String permissions : objRoles.getPermissions()){
    			PermissionEntity entity = new PermissionEntity();
    			entity.setCanonicalName(permissions);
    			entityList.add(entity);
    		}
    		
    		roleEntity.setPermissions(entityList);
    		Role role = (Role) ObjectConverter.jpaToApi(roleEntity);
    		try{
    			role = sc.dataStore().getRoleDataStore().importRoles(tenant.getId(), role);
    		}catch (ItemAlreadyExistsException | SecurityDataException e){
    			//TODO : Update if the Roles already exist ..
    			 getLogger().info("Role "+role.getCanonicalName() +" already exist ..");
    		}
    		retRoleList.add(role);
    	}
    	//TODO : Need to change the return type
    	 if (retRoleList == null || retRoleList.size() == 0){
     		throw new ItemAlreadyExistsException("All Roles", " specified Tenant.");
     	}
    	return gson.toJson(retRoleList);
		//return retRoleList;
	}
	
	
	@Override
	public String validateImportRoles(InputStream inputStream) throws Exception {
		String inputData = JsonObjectLoader.StreamToString(inputStream);
		InputStream schemaPath = null; 
    	try{
    		schemaPath = RoleHandler.class.getResourceAsStream("/role_validation_schema.json");
    	}catch (Exception e){
    		try{
    			schemaPath = new FileInputStream("/role_validation_schema.json");
    		}catch (Exception e1){
    			e1.printStackTrace();
    		}
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
		Tenant tenant = getSecurityManager().getSubjectManager().getTenant();
		Collection<RolesValidator> retRoleList = new ArrayList<>();
		Type datasetListType = new TypeToken<Collection<Roles>>() {}.getType();
    	List<Roles> datasets = JsonObjectLoader.load(inputData, datasetListType);
    	
    	for (Roles objRoles : datasets){
    		RoleEntity roleEntity = new RoleEntity();
    		roleEntity.setCanonicalName(objRoles.getName());
    		roleEntity.setDescription(objRoles.getDescription());
    		List<PermissionEntity> entityList = new ArrayList<PermissionEntity>();
    		for (String permissions : objRoles.getPermissions()){
    			PermissionEntity entity = new PermissionEntity();
    			entity.setCanonicalName(permissions);
    			entityList.add(entity);
    		}
    		
    		roleEntity.setPermissions(entityList);
    		Role role = (Role) ObjectConverter.jpaToApi(roleEntity);
    		RolesValidator validaator = sc.dataStore().getRoleDataStore().ValidateRolesImport(tenant.getId(), role);
    		retRoleList.add(validaator);
    	}
    	//TODO : Need to change the return type
    	 if (retRoleList == null || retRoleList.size() == 0){
     		throw new ItemAlreadyExistsException("All Roles", " specified Tenant.");
     	}
    	return gson.toJson(retRoleList);
		//return retRoleList;
	}
}

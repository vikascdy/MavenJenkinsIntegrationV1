package com.edifecs.epp.security.handler.rest;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.command.IRestCommandHandler;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.security.data.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Command Handler exposing management options around Organizations.
 */
@CommandHandler(
        namespace = "organization",
        description = "Contains methods that can be used to get back information about Organizations")
public interface IOrganizationHandler extends IRestCommandHandler<Organization> {

    @RequiresPermissions("platform:security:administrative:organization:view")
    @Override
    public Organization get(String url) throws Exception;

    @RequiresPermissions("platform:security:administrative:organization:view")
    @Override
    public Collection<Organization> list(Pagination pg) throws Exception;

    @RequiresPermissions("platform:security:administrative:organization:create")
    @Override
    public Organization post(Organization organization) throws Exception;

    @RequiresPermissions("platform:security:administrative:organization:edit")
    public Organization put(String url, Organization organization) throws Exception;

    @Override
    @RequiresPermissions("platform:security:administrative:organization:delete")
    public void delete(String url) throws Exception;

    /**
     * Creates an organization for the tenant that the user belongs too.
     *
     * @param organization Organization to create
     * @return The complete, created Organization with the Generated ID
     * @throws Exception
     */
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:create")
    Organization createOrganization(
            @Arg(name = "organization", required = true) Organization organization)
            throws Exception;

    /**
     * Creates an Organization for the tenant specified. Only those people who have the ability to edit the tenant
     * should have the ability to create a new organization for the specified tenant.
     *
     * @param tenant       Tenant to create the organization in
     * @param organization Organization to create
     * @return The complete, created Organization with the Generated ID
     * @throws Exception
     */
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:create")
    Organization createOrganizationForTenant(
            @Arg(name = "tenant", required = true) Tenant tenant,
            @Arg(name = "organization", required = true) Organization organization)
            throws Exception;

    /**
     * Deletes the specified Organization. Only those Organizations the user is able to view, he can delete.
     *
     * @param id ID of the organization to delete.
     * @throws Exception If there was a problem deleting the Organization
     */
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:delete")
    public void deleteOrganization(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:delete")
    public void deleteOrganizations(@Arg(name = "ids", required = true) ArrayList<Long> ids)
            throws Exception;
    
    /**
     * Given an Organization ID, Return the Organization.
     *
     * @param id ID of the Organization
     * @return The Organization object
     * @throws Exception
     */
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:view")
    public Organization getOrganizationById(
            @Arg(name = "id", required = true) Long id) throws Exception;


    /**
     * Given an Organization object, update the fields.
     * <p/>
     * Note: The ID in the organization MUST be populated. If not ID is present, it will not be able to update the
     * record.
     *
     * @param organization Organization to update
     * @return Updated Organization object after persistence
     * @throws Exception
     */
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:edit")
    public Organization updateOrganization(
            @Arg(name = "organization", required = true) Organization organization)
            throws Exception;

    /**
     * Returns a list of child Organizations that belong to the specified parent Organization Id.
     *
     * @param organizationId ID of the parent Organization
     * @return Collection of all child Organizations
     * @throws Exception
     */
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:view")
    public Collection<Organization> getChildOrganizationsById(
            @Arg(name = "organizationId", required = true) Long organizationId)
            throws Exception;


    // TODO: Review this commands to see if there is a better solution.
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:view")
    public OrganizationDetail getOrganizationDetail(
            @Arg(name = "organizationId", required = true) Long organizationId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:view")
    public PaginatedList<Organization> getOrganizationsForTenant(
            @Arg(name = "id", required = true) Long id,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:view")
    public PaginatedList<Organization> getOrganizationsForGroup(
            @Arg(name = "id", required = true) Long id,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:edit")
    public void addChildOrganization(
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "childOrganizationId", required = true) Long childOrganizationId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:edit")
    public void removeChildOrganization(
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "childOrganizationId", required = true) Long childOrganizationId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:edit")
    public void removeRoleFromOrganization(
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "roleId", required = true) Long roleId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:edit")
    public void removeRolesFromOrganization(
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "roles", required = true) ArrayList<Role> roles)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:edit")
    public void addRoleToOrganization(
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "roleId", required = true) Long roleId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:organization:edit")
    public void addRolesToOrganization(
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "roles", required = true) ArrayList<Role> roles)
            throws Exception;


    /*
    REALM Commands
     */

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:realm:edit")
    public SecurityRealm addRealmToOrganization(
            @Arg(name = "realm", required = true) SecurityRealm realm,
            @Arg(name = "organizationId", required = true) Long organizationId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:realm:edit")
    public SecurityRealm updateRealm(
            @Arg(name = "realm", required = true) SecurityRealm realm,
            @Arg(name = "organizationId", required = true) Long organizationId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public String importOrganizationFromJson(@StreamArg(name = "inputStream") InputStream inputStream) throws Exception;
    
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public String validateImportOrganization(@StreamArg(name = "inputStream") InputStream inputStream) 
    		throws Exception;
}

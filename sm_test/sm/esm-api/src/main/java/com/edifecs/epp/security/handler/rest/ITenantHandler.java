package com.edifecs.epp.security.handler.rest;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.command.IRestCommandHandler;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.PasswordPolicy;
import com.edifecs.epp.security.data.Site;
import com.edifecs.epp.security.data.Tenant;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@CommandHandler(
        namespace = "tenant",
        description = "Contains methods that can be used to get back information about Tenants")
public interface ITenantHandler extends IRestCommandHandler<Tenant> {

    @RequiresPermissions("platform:security:administrative:tenant:view")
    @Override
    public Tenant get(String url) throws Exception;

    @RequiresPermissions("platform:security:administrative:tenant:view")
    @Override
    public Collection<Tenant> list(Pagination pg) throws Exception;

    @RequiresPermissions("platform:security:administrative:tenant:view")
    @Override
    public Tenant post(Tenant tenant) throws Exception;

    @RequiresPermissions("platform:security:administrative:tenant:edit")
    @Override
    public Tenant put(String url, Tenant tenant) throws Exception;

    @RequiresPermissions("platform:security:administrative:tenant:delete")
    @Override
    public void delete(String url) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:create")
    public Tenant createTenant(
            @Arg(name = "tenant", required = true) Tenant tenant)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:create")
    public Tenant createTenantForSite(
            @Arg(name = "site", required = true) Site site,
            @Arg(name = "tenant", required = true) Tenant tenant)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:delete")
    public boolean deleteTenant(@Arg(name = "id", required = true) Long id)
            throws Exception;


    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:delete")
    public boolean deleteTenants(@Arg(name = "ids", required = true) ArrayList<Long> ids)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:view")
    public Tenant getTenantById(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:view")
    public Tenant getTenantByName(
            @Arg(name = "canonicalName", required = true) String canonicalName)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:view")
    public PaginatedList<Tenant> getTenants(
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:edit")
    public Tenant updateTenant(
            @Arg(name = "tenant", required = true) Tenant tenant)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:create")
    public Tenant updateTenantPasswordPolicy(
            @Arg(name = "tenantId", required = true) Long tenantId,
            @Arg(name = "policy", required = true) PasswordPolicy passwordPolicy)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:edit")
    public Boolean updateTenantLogo(
            @Arg(name = "tenantId", required = true) Long tenantId,
            @Arg(name = "data", required = true, description = "logo image") String data);

    @SyncCommand
    public String getTenantLogo(
            @Arg(name = "tenant", required = false) String tenant);

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:edit")
    public Boolean updateTenantLandingPage(
            @Arg(name = "tenantId", required = true) Long tenantId,
            @Arg(name = "landingPage", description = "landing page") String data);

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:edit")
    public Boolean setTenantLandingPage(
            @Arg(name = "tenantId", required = true) Long tenantId,
            @Arg(name = "landingPage", required = true, description = "landing page") String data);

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:tenant:view")
    public String getTenantLandingPage(
            @Arg(name = "tenantId", required = true) Long tenantId);

    @SuppressWarnings("deprecation")
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public String importTenantFromJson(
            @StreamArg(name = "inputStream") InputStream inputStream) throws Exception ;

    @SuppressWarnings("deprecation")
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public String validateImportTenants(
            @StreamArg(name = "inputStream") InputStream inputStream) throws Exception;
}

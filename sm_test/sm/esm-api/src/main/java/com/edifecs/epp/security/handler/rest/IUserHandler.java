package com.edifecs.epp.security.handler.rest;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.command.IRestCommandHandler;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.isc.stream.MessageStream;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.IAuthenticationToken;
import com.edifecs.epp.security.data.token.LdapAuthenticationToken;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.data.CSVJsonUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UserHandler contains all the methods available for managing data surrounding
 * a user.
 *
 * @author willclem
 */
@CommandHandler(
        namespace = "user",
        description = "Contains methods that can be used to get back the currently logged in user data.")
public interface IUserHandler extends IRestCommandHandler<User> {

    @Override
    @RequiresPermissions("platform:security:administrative:user:view")
    public User get(String url) throws Exception;

    @Override
    @RequiresPermissions("platform:security:administrative:user:view")
    public Collection<User> list(Pagination pg) throws Exception;

    @Override
    @RequiresPermissions("platform:security:administrative:user:create")
    public User post(User user) throws Exception;

    @Override
    @RequiresPermissions("platform:security:administrative:user:edit")
    public User put(String url, User user) throws Exception;

    @Override
    @RequiresPermissions("platform:security:administrative:user:delete")
    public void delete(String url) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public User createUser(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "token", required = true) UsernamePasswordAuthenticationToken token,
            @Arg(name = "organizationId", required = true) Long organizationId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public User createLdapUser(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "token", required = true) LdapAuthenticationToken token,
            @Arg(name = "organizationId", required = true) Long organizationId)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public User createCertificateUser(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "domain", required = true) String domain,
            @Arg(name = "organization", required = true) String organization,
            @Arg(name = "certificate", required = true) String certificate,
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "username", required = true) String username)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public void updateCertificateTokenForUser(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "domain", required = true) String domain,
            @Arg(name = "organization", required = true) String organization,
            @Arg(name = "certificate", required = true) String certificate)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:password")
    public void addUsernamePasswordAuthenticationTokenToUser(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "authenticationToken", required = true) UsernamePasswordAuthenticationToken authenticationToken)
            throws Exception;

    // TODO: Move this out to another command handler
    @SyncCommand
    public void changePasswordAtFirstLogin(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "authenticationToken", required = true) UsernamePasswordAuthenticationToken authenticationToken)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:delete")
    public boolean deleteUser(@Arg(name = "id", required = true) Long id)
            throws Exception;
    
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:delete")
    public boolean deleteUsers(@Arg(name = "ids", required = true) ArrayList<Long> ids)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public User getUserById(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public List<Credential> getCredentialForUser(
            @Arg(name = "id", required = true) Long id) throws Exception;
    
    @SyncCommand
    public List<Credential> getCredentialForCurrentUser() throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public boolean deactivateUser(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public boolean activateUser(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public boolean suspendUser(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:create")
    public boolean unSuspendUser(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public User getUserByUsername(
            @Arg(name = "username", required = true) String username)
            throws SecurityManagerException;

    @Deprecated
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> getUsers(
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:password")
    public void updateUsernamePasswordAuthenticationToken(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "authenticationToken", required = true) UsernamePasswordAuthenticationToken authenticationToken)
            throws Exception;
    
    
    @SyncCommand
    public void updateCurrentUserAuthenticationToken(@Arg(name = "authenticationToken", required = true) UsernamePasswordAuthenticationToken authenticationToken)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public User updateUser(@Arg(name = "user", required = true) User user)
            throws Exception;
    
    
    @SyncCommand
    public User updateCurrentUser(@Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public void batchImportUsers(@Arg(name = "users") ArrayList<String> users)
            throws SecurityManagerException;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public Collection<CSVJsonUtil> validateUsersCSV(
            @Arg(name = "inputStream") MessageStream messageStream);

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public Collection<User> searchByFirstOrMiddleOrLastName(
            @Arg(name = "seed") String seed,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount);

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:password")
    public boolean deleteAuthenticationToken(
            @Arg(name = "newAuthenticationToken", required = true) IAuthenticationToken newAuthenticationToken)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> getUsersForGroup(
            @Arg(name = "groupId", required = true) Long groupId,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:password")
    public void updateAuthenticationToken(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "authenticationToken", required = true) IAuthenticationToken authenticationToken)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> getUsersForRole(
            @Arg(name = "roleId", required = true) long roleId,
            @Arg(name = "startRecord", required = false) long startRecord,
            @Arg(name = "recordCount", required = false) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> getUserRangeForGroup(
            @Arg(name = "groupId", required = true) long groupId,
            @Arg(name = "startRecord", required = false) long startRecord,
            @Arg(name = "recordCount", required = false) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> getUsersForOrganization(
            @Arg(name = "organizationId", required = true) long organizationId,
            @Arg(name = "startRecord", required = false) long startRecord,
            @Arg(name = "recordCount", required = false) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> searchUsers(
            @Arg(name = "userName", required = true) String userName,
            @Arg(name = "startRecord", required = false) long startRecord,
            @Arg(name = "recordCount", required = false) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> searchUsersHavingRole(
            @Arg(name = "userName", required = true) String userName,
            @Arg(name = "roleId", required = true) long roleId,
            @Arg(name = "startRecord", required = false) long startRecord,
            @Arg(name = "recordCount", required = false) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<User> getUsersForPermission(
            @Arg(name = "permissionString", required = true) String permissionString,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<String> getUserNamesForPermission(
            @Arg(name = "permissionString", required = true) String permissionString,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    // TODO: This needs to be moved out to another handler
    @SyncCommand
    @NullSessionAllowed
    boolean matchPattern(@Arg(name = "regex", required = true) String regex,
                         @Arg(name = "seed", required = true) String seed);

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    User getLdapUserAttributes(
            @Arg(name = "username", required = true) String username,
            @Arg(name = "organizationId", required = true) Long organizationId)
            throws Exception;
    
    @SuppressWarnings("deprecation")
	@SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public String importUsersJson(
            @StreamArg(name = "inputStream") InputStream inputStream )
            		throws Exception;
    
    @SuppressWarnings("deprecation")
   	@SyncCommand
       @RequiresPermissions("platform:security:administrative:user:import")
       public String validateImportUsers(
               @StreamArg(name = "inputStream") InputStream inputStream )
               		throws Exception;

}

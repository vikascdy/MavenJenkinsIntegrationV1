package com.edifecs.epp.security.datastore;

import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.IAuthenticationToken;
import com.edifecs.epp.security.jpa.entity.RoleEntity;
import com.edifecs.epp.security.jpa.entity.UserGroupEntity;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper.UsersValidator;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;

import java.util.Collection;
import java.util.List;

public interface IUserDataStore extends IBaseOwnerDataStore<User> {

    List<User> getUsersForGroup(Long groupId) throws SecurityDataException;

    PaginatedList<User> getUsersForGroup(long groupId, long startRecord,
                                         long recordCount) throws SecurityDataException;

    PaginatedList<User> getUsersForOrganization(long organizationId,
                                                long startRecord, long recordCount) throws SecurityDataException;

    PaginatedList<User> getTransitiveUsersForRole(long roleId,
                                                  long startRecord, long recordCount);

    PaginatedList<User> searchUsersForRole(String name, long roleId,
                                           long startRecord, long recordCount);

    PaginatedList<User> searchUsersByName(String name, long startRecord,
                                          long recordCount);

    List<User> getUsersForOrganization(long organizationId)
            throws SecurityDataException;

    List<User> getUsersForRole(long roleId);

    List<User> searchUsersForRole(String name, long roleId);

    List<User> searchUsersByName(String name);

    User getUserByEmail(String email) throws SecurityDataException;

    PaginatedList<User> getUsersForPermission(Permission permission,
                                              long startRecord, long recordCount) throws SecurityDataException;

    PaginatedList<String> getUserNamesForPermission(Permission permission,
                                                    long startRecord, long recordCount) throws SecurityDataException;

    List<User> getUsersForPermission(Permission permission)
            throws SecurityDataException;

    List<String> getUserNamesForPermission(Permission permission)
            throws SecurityDataException;

    // TODO: Review these
    User validateUserAuthenticationToken(
            IAuthenticationToken newAuthenticationToken)
            throws SecurityDataException;

    void addAuthenticationTokenToUser(User user,
                                      IAuthenticationToken authenticationToken)
            throws SecurityDataException;

    void updateAuthenticationToken(User user,
                                   IAuthenticationToken authenticationToken)
            throws SecurityDataException, ItemNotFoundException;

    void addOrganizationToUser(Organization organization, User user) throws SecurityDataException;

    void deleteAuthenticationToken(IAuthenticationToken newAuthenticationToken)
            throws SecurityDataException;

    void deactivate(long userId) throws SecurityDataException;

    void activate(long userId) throws SecurityDataException;

    void suspend(long userId) throws SecurityDataException;

    void unSuspend(long userId) throws SecurityDataException;

    /**
     * Gets the user by the specified username.
     *
     * @param username username of the user to retrieve
     * @return the user
     */
    User getUserByUsername(String domain, String username);

    /**
     * Gets the user by the specified userId.
     *
     * @param userId userId of the user to retrieve
     * @return the user
     * @throws SecurityDataException
     */
    User getUserByUserId(Long userId) throws SecurityDataException,
            ItemNotFoundException;

    SessionId attachSessionToUser(SessionId session, long userId) throws SecurityDataException;

    void removeSessionFromUser(Long sessionId) throws SecurityDataException;

    Collection<User> queryUsers(String seed, long startRecord, long maxRecords);

    User create(Long organizationId, User user, IAuthenticationToken token, User auditor)
            throws ItemAlreadyExistsException, SecurityDataException;

    User create(Long organizationId, User user, User auditor)
            throws ItemAlreadyExistsException, SecurityDataException;

    List<Credential> getCredentialForUser(Long userId) throws SecurityDataException, ItemNotFoundException;
    
    User importUsers(Long organizationId, User user, List<RoleEntity> roleEntity, 
    		List<UserGroupEntity> grpEntity, Organization org, String userPassword, User createdBy) 
    		throws ItemAlreadyExistsException, SecurityDataException;
    
    UsersValidator validateUserImports(Long tenantId, User user) throws ItemAlreadyExistsException, SecurityDataException;
}

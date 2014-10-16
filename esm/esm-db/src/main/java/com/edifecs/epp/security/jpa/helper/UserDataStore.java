package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.*;
import com.edifecs.epp.security.datastore.IUserDataStore;
import com.edifecs.epp.security.exception.*;
import com.edifecs.epp.security.jpa.entity.*;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper.UsersValidator;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.criteria.*;

import java.util.*;

class UserDataStore implements IUserDataStore {

	@Override
	public Collection<User> getAll() throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			List<User> users = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<UserEntity> uListDB = entityManager.createNamedQuery(
					UserEntity.FIND_ALL_USERS).getResultList();

			for (UserEntity userDB : uListDB) {
				users.add((User) ObjectConverter.jpaToApi(userDB));
			}
			return users;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public Collection<User> getRange(long startRecord, long recordCount)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			List<User> users = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<UserEntity> uListDB = entityManager
					.createNamedQuery(UserEntity.FIND_ALL_USERS)
					.setFirstResult((int) startRecord)
					.setMaxResults((int) recordCount).getResultList();

			for (UserEntity userDB : uListDB) {
				users.add((User) ObjectConverter.jpaToApi(userDB));
			}
			return users;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public PaginatedList<User> getPaginatedRange(long startRecord,
			long recordCount) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			List<User> users = new ArrayList<>();
			int total = entityManager
					.createNamedQuery(UserEntity.FIND_ALL_USERS,
							UserEntity.class).setFirstResult(0)
					.setMaxResults(Integer.MAX_VALUE).getResultList().size();

			List<UserEntity> uListDB = entityManager
					.createNamedQuery(UserEntity.FIND_ALL_USERS,
							UserEntity.class).setFirstResult((int) startRecord)
					.setMaxResults((int) recordCount).getResultList();

			for (UserEntity userDB : uListDB) {
				users.add((User) ObjectConverter.jpaToApi(userDB));
			}
			return new PaginatedList<User>(users, total);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public User getById(long id) throws ItemNotFoundException,
			SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			UserEntity user = entityManager.find(UserEntity.class, id);
			if (null != user) {
				return (User) ObjectConverter.jpaToApi(user);
			} else {
				return null;
			}
		} finally {
			entityManager.close();
		}
	}

	@Override
	public List<Credential> getCredentialForUser(Long id)
			throws ItemNotFoundException, SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			UserEntity user = entityManager.find(UserEntity.class, id);

			if (null == user) {
				throw new ItemNotFoundException("User ", id);
			}
			List<Credential> credentials = new ArrayList<>();
			for (CredentialEntity crDB : user.getCredentials()) {
				// checked conversion instead of object converter
				// avoid abuse, not allow passwd to be set
				credentials.add(CredentialJpaToApi(crDB));
			}
			return credentials;

		} finally {
			entityManager.close();
		}
	}

	private Credential CredentialJpaToApi(CredentialEntity crDB) {
		Credential cr = new Credential();
		if (crDB.getCredentialType().getCanonicalName()
				.equals(CertificateAuthenticationToken.class.getName())) {
			// TODO : send public key raw or encrypted?
			// cr.setCredentialKey(CertificateAuthenticationToken.getKeyLookup(crDB.getCredentialBinary()));
			cr.setCredentialKey(new String(crDB.getCredentialBinary()));
		} else {
			cr.setCredentialKey(crDB.getCredentialKey());
		}
		cr.setCredentialType(CredentialTypeJpaToApi(crDB.getCredentialType()));
		cr.setExpired(crDB.isExpired());
		cr.setId(crDB.getId());
		cr.setUserId(crDB.getUser().getId());
		return cr;
	}

	private CredentialType CredentialTypeJpaToApi(CredentialTypeEntity crTypeDB) {
		CredentialType crType = new CredentialType();
		crType.setCanonicalName(crTypeDB.getCanonicalName());
		crType.setId(crTypeDB.getId());
		if (null != crTypeDB.getParentCredentialType()) {
			crType.setParentCredentialType(CredentialTypeJpaToApi(crTypeDB
					.getParentCredentialType()));
		}
		return crType;
	}

	@Override
	public void addOrganizationToUser(Organization organization, User user) throws SecurityDataException {
		if (user.getId() == null) {
			throw new ItemCannotBeNullException("User");
		}
        if (organization.getId() == null) {
            throw new ItemCannotBeNullException("Organization");
        }

		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			// Creating JPA Objects
			UserEntity userDB = entityManager.find(UserEntity.class,
					user.getId());
			OrganizationEntity org = entityManager.find(
					OrganizationEntity.class, organization.getId());
			userDB.setOrganization(org);

			userDB.setLastUpdatedBy((UserEntity) ObjectConverter.apiToJpa(user));
			userDB.setLastUpdatedDate(new Date());

			// Persist Entities
			entityManager.merge(userDB);
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public final User create(User user, User auditor)
			throws ItemAlreadyExistsException, SecurityDataException {
		if (user.getId() != null) {
			throw new ItemAlreadyExistsException("User", user.getId());
		}
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			// Creating JPA Objects
			UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);

			// Contact Entity.
			if (user.getContact() != null) {
				ContactEntity contactDB = (ContactEntity) ObjectConverter
						.apiToJpa(user.getContact());
				userDB.setContact(contactDB);
				user.setContact((com.edifecs.epp.security.data.Contact) ObjectConverter
						.jpaToApi(contactDB));
			}
			// set other user columns
			userDB.setCreatedDateTime(new Date());
			userDB.setModifiedDateTime(new Date());

			if (null != auditor)
				userDB.setCreatedBy((UserEntity) ObjectConverter
						.apiToJpa(auditor));
			userDB.setCreatedDateTime(new Date());

			// Persist Entities
			entityManager.persist(userDB);
			user = (User) ObjectConverter.jpaToApi(userDB);

			return user;
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public User create(Long organizationId, User user, User auditor)
			throws ItemAlreadyExistsException, SecurityDataException {
		if (user.getId() != null) {
			throw new ItemAlreadyExistsException("User", user.getId());
		}
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			// Creating JPA Objects
			UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);

			// Contact Entity.
			if (user.getContact() != null) {
				ContactEntity contactDB = (ContactEntity) ObjectConverter
						.apiToJpa(user.getContact());
				userDB.setContact(contactDB);
				user.setContact((com.edifecs.epp.security.data.Contact) ObjectConverter
						.jpaToApi(contactDB));
			}
			// set other user columns
			userDB.setCreatedDateTime(new Date());
			userDB.setModifiedDateTime(new Date());

			OrganizationEntity organization = new OrganizationEntity();
			organization.setId(organizationId);
			userDB.setOrganization(organization);

			userDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
			userDB.setCreatedDateTime(new Date());

			// Persist Entities
			entityManager.persist(userDB);

			// Refresh the user after persistence for use later
			entityManager.refresh(userDB);
			user = (User) ObjectConverter.jpaToApi(userDB);

			return user;
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public User create(Long organizationId, User user,
			IAuthenticationToken token, User auditor)
			throws ItemAlreadyExistsException, SecurityDataException {
		if (user.getId() != null) {
			throw new ItemAlreadyExistsException("User", user.getUsername());
		}
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {

			tx.begin();

			// Check if email already exist
			UserEntity objUserEntity = null;
			String userEmail = user.getContact().getEmailAddress();
			try {
				objUserEntity = entityManager
						.createNamedQuery(UserEntity.VERIFY_USER_BY_EMAIL,
								UserEntity.class)
						.setParameter("email", userEmail.toLowerCase())
						.getSingleResult();
			} catch (Exception e) {
				// safe to ignore
			}

			if (objUserEntity != null) {
				User usr = (User) ObjectConverter.jpaToApi(objUserEntity);
				String dbEmail = usr.getContact().getEmailAddress().toLowerCase();
				if (dbEmail.equalsIgnoreCase(userEmail.toLowerCase())) {
					throw new UserWithEmailAddressAlreadyExistsException(usr.getContact().getEmailAddress());
				}
			}

			// check if user already exists for the tenant.
			if (token instanceof UsernamePasswordAuthenticationToken) {
				String username = ((UsernamePasswordAuthenticationToken) token)
						.getUsername();
				if (username != null && username.length() >= 1) {
					username = username.toLowerCase();
				}

				TenantEntity tenant1;
				try {
					tenant1 = entityManager
							.createNamedQuery(
									OrganizationEntity.FIND_TENANT_BY_ORGANIZATION_ID,
									TenantEntity.class)
							.setParameter("organizationId", organizationId)
							.getSingleResult();
				} catch (Exception e) {
					throw new ItemNotFoundException("Organization ", organizationId);
				}

				TenantEntity tenant2 = null;
				try {
					tenant2 = entityManager
							.createNamedQuery(
									TenantEntity.FIND_TENANT_BY_USER_NAME,
									TenantEntity.class)
							.setParameter("userName", username)
							.getSingleResult();
				} catch (Exception e) {
					// safe to ignore
				}

				if (tenant2 != null && tenant1.getId() == tenant2.getId()) {
					throw new ItemAlreadyExistsException("User", username);
				}
			}

			// Creating JPA Objects
			UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);

			// Contact Entity.
			if (user.getContact() != null) {
				ContactEntity contactDB = (ContactEntity) ObjectConverter
						.apiToJpa(user.getContact());
				userDB.setContact(contactDB);
				user.setContact((com.edifecs.epp.security.data.Contact) ObjectConverter
						.jpaToApi(contactDB));
			}

			// set other user columns
			userDB.setCreatedDateTime(new Date());
			userDB.setModifiedDateTime(new Date());

			OrganizationEntity organization = new OrganizationEntity();
			organization.setId(organizationId);
			userDB.setOrganization(organization);

			userDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
			userDB.setCreatedDateTime(new Date());

			// Persist Entities
			entityManager.persist(userDB);

			// Refresh the user after persistence for use later
			entityManager.refresh(userDB);
			addAuthenticationTokenToUser(userDB, token, entityManager);

			user = (User) ObjectConverter.jpaToApi(userDB);

			return user;
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public User importUsers(Long organizationId, User user,
			List<RoleEntity> roleEntity, List<UserGroupEntity> grpEntity,
			Organization org, String userPassword, User createdBy)
			throws ItemAlreadyExistsException, SecurityDataException {
		if (user.getId() != null) {
			throw new ItemAlreadyExistsException("User", user.getUsername());
		}

		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			// Check if email already exist
			UserEntity objUserEntity = null;
			String userEmail = user.getContact().getEmailAddress();
			try {
				objUserEntity = entityManager
						.createNamedQuery(UserEntity.VERIFY_USER_BY_EMAIL,
								UserEntity.class)
						.setParameter("email", userEmail.toLowerCase())
						.getSingleResult();
			} catch (Exception e) {
				// safe to ignore
			}

			if (objUserEntity != null) {
				User usr = (User) ObjectConverter.jpaToApi(objUserEntity);
				String dbEmail = usr.getContact().getEmailAddress()
						.toLowerCase();
				if (dbEmail.equalsIgnoreCase(userEmail.toLowerCase())) {
					throw new ItemAlreadyExistsException("Email address: "
							+ dbEmail, "User " + usr.getUsername());
				}
			}

			// check if user already exists for the tenant.
			String username = user.getUsername();
			if (username != null && username.length() >= 1) {
				username = username.toLowerCase();
			}

			TenantEntity tenant1;
			try {
				tenant1 = entityManager
						.createNamedQuery(
								OrganizationEntity.FIND_TENANT_BY_ORGANIZATION_ID,
								TenantEntity.class)
						.setParameter("organizationId", organizationId)
						.getSingleResult();
			} catch (Exception e) {
				throw new ItemNotFoundException("Organization ", organizationId);
			}

			TenantEntity tenant2 = null;
			try {
				tenant2 = entityManager
						.createNamedQuery(
								TenantEntity.FIND_TENANT_BY_USER_NAME,
								TenantEntity.class)
						.setParameter("userName", username).getSingleResult();
			} catch (Exception e) {
				// safe to ignore
			}
			if (tenant2 != null && tenant1.getId() == tenant2.getId()) {
				throw new ItemAlreadyExistsException("User: " + username,
						"Tenant " + tenant1.getCanonicalName());
			}

			// If user does not pass the organization ...
			if (org == null){
				OrganizationEntity OrgEntity;
				try {
					OrgEntity = entityManager
							.createNamedQuery(
									OrganizationEntity.FIND_ORGANIZATION_BY_ID,
									OrganizationEntity.class)
							.setParameter("id", organizationId)
							.getSingleResult();
				} catch (Exception e) {
					throw new ItemNotFoundException("Organization ", organizationId);
				}
				
				org = (Organization) ObjectConverter.jpaToApi(OrgEntity);
			}
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					tenant1.getCanonicalName(), org.getCanonicalName(),
					user.getUsername(), userPassword);
			// Creating JPA Objects
			UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);

			// Contact Entity.
			if (user.getContact() != null) {
				ContactEntity contactDB = (ContactEntity) ObjectConverter
						.apiToJpa(user.getContact());
				userDB.setContact(contactDB);
				user.setContact((com.edifecs.epp.security.data.Contact) ObjectConverter
						.jpaToApi(contactDB));
			}
			for (RoleEntity objEntity : roleEntity) {
				Role role = (Role) ObjectConverter.jpaToApi(objEntity);
				try {
					role = new RoleDataStore().create(tenant1.getId(), role, createdBy);
				} catch (ItemAlreadyExistsException e) {
					// Ignore this error message ...
				}
			}
			// Setting the role entity ...
			userDB.setRoles(roleEntity);
			
			for (UserGroupEntity objEntity : grpEntity) {
				UserGroup objGroup = (UserGroup) ObjectConverter
						.jpaToApi(objEntity);
				try {
					new UserGroupDataStore().create(tenant1.getId(), objGroup,
							createdBy);
				} catch (ItemAlreadyExistsException e) {
					// Ignore this error message ...
				}
			}
			// Setting the role entity ...
			userDB.setGroups(grpEntity);
			
			// adding the organization to userdb
			org.setId(organizationId);
			OrganizationEntity orgDB = (OrganizationEntity) ObjectConverter
					.apiToJpa(org);
			userDB.setOrganization(orgDB);

			// set other user columns
			userDB.setCreatedDateTime(new Date());
			userDB.setModifiedDateTime(new Date());

			OrganizationEntity organization = new OrganizationEntity();
			organization.setId(organizationId);
			userDB.setOrganization(organization);
			userDB.setCreatedBy((((UserEntity) ObjectConverter.apiToJpa(createdBy))));
			userDB.setCreatedDateTime(new Date());

			// Persist Entities
			entityManager.persist(userDB);
			// Refresh the user after persistence for use later
			entityManager.refresh(userDB);
			addAuthenticationTokenToUser(userDB, token, entityManager);
			user = (User) ObjectConverter.jpaToApi(userDB);
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
		return user;
	}
	
	
	@Override
	public UsersValidator validateUserImports(Long TenantId, User user)
			throws ItemAlreadyExistsException, SecurityDataException {
		UsersValidator valUsers = (new OrgValidationHelper()).new UsersValidator();
		valUsers.setName(user.getUsername());
		if (user.getId() != null) {
			valUsers.setErrorCode(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
			return valUsers;
		}
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			// check if user already exists for the tenant.
			String username = user.getUsername();
			if (username != null && username.length() >= 1) {
				username = username.toLowerCase();
			}

			TenantEntity tenant = null;
			try {
				tenant = entityManager
						.createNamedQuery(
								TenantEntity.FIND_TENANT_BY_TENANT_USER_NAME,
								TenantEntity.class).setParameter("tenantId", TenantId)
								.setParameter("userName", username).getSingleResult();
				if (tenant != null) {
					valUsers.setErrorCode(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
					return valUsers;
				}
			} catch (Exception e) {
				valUsers.setErrorCode(ImportValidatorErrorCodes._VALID_ENTITY);
				return valUsers;
			}
		} catch (Exception e) {
			tx.rollback();
			valUsers.setErrorCode(ImportValidatorErrorCodes._INVALID_ENTITY);
			return valUsers;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
		valUsers.setErrorCode(ImportValidatorErrorCodes._VALID_ENTITY);
		return valUsers;
	}

	@Override
	public User update(User user, User auditor) throws ItemNotFoundException,
			SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			UserEntity userDB = entityManager.find(UserEntity.class,
					user.getId());
			if (null != userDB) {
				ObjectConverter.copyCommonBeanProperties(user, userDB);
				userDB.setModifiedDateTime(new Date());
				userDB.setLastUpdatedBy((UserEntity) ObjectConverter
						.apiToJpa(auditor));
				userDB.setLastUpdatedDate(new Date());
				entityManager.merge(userDB);
				return (User) ObjectConverter.jpaToApi(userDB);
			} else {
				throw new ItemAlreadyExistsException("User : " + user.getId(),
						"tenant.");
			}
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public void delete(User user) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			UserEntity userDB = entityManager.find(UserEntity.class,
					user.getId());
			// for (UserGroupEntity grp : userDB.getGroups())
			// grp.getUsers().remove(userDB);
			// entityManager.remove(userDB);
			for (CredentialEntity cr : userDB.getCredentials()) {
				if (cr.getCredentialType()
						.getCanonicalName()
						.equals(UsernamePasswordAuthenticationToken.class
								.getName())) {
					String oldKey = cr.getCredentialKey();
					cr.setCredentialKey("__DELETED_" + oldKey + "_"
							+ System.currentTimeMillis());
				}
			}
			String oldEmail = userDB.getContact().getEmailAddress();
			userDB.getContact().setEmailAddress(
					"__DELETED_" + oldEmail + "_" + System.currentTimeMillis());
			userDB.setDeleted(true);
			entityManager.flush();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public void deactivate(long userId) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			UserEntity userDB = entityManager.find(UserEntity.class, userId);
			if (userDB.isActive())
				userDB.setActive(false);
			else
				throw new SecurityException("User is already deactivated.");

		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public void activate(long userId) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			UserEntity userDB = entityManager.find(UserEntity.class, userId);
			if (!userDB.isActive())
				userDB.setActive(true);
			else
				throw new SecurityException("User is already activate.");

		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public void suspend(long userId) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			UserEntity userDB = entityManager.find(UserEntity.class, userId);
			userDB.setSuspended(true);
			userDB.setModifiedDateTime(new Date());
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public void unSuspend(long userId) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			UserEntity userDB = entityManager.find(UserEntity.class, userId);
			userDB.setSuspended(false);

		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersForGroup(Long groupId)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			return jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_GROUP)
					.setParameter("groupId", groupId).getResultList());
		} finally {
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaginatedList<User> getUsersForGroup(long groupId, long startRecord,
			long recordCount) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			int total = entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_GROUP)
					.setParameter("groupId", groupId).setFirstResult(0)
					.setMaxResults(Integer.MAX_VALUE).getResultList().size();

			List<User> users = jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_GROUP)
					.setParameter("groupId", groupId)
					.setFirstResult((int) startRecord)
					.setMaxResults((int) recordCount).getResultList());

			return new PaginatedList<User>(users, total);
		} finally {
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaginatedList<User> getUsersForOrganization(long organizationId,
			long startRecord, long recordCount) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {

			int total = entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_ORGANIZATION)
					.setParameter("organizationId", organizationId)
					.setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
					.getResultList().size();

			List<User> users = jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_ORGANIZATION)
					.setParameter("organizationId", organizationId)
					.setFirstResult((int) startRecord)
					.setMaxResults((int) recordCount).getResultList());

			return new PaginatedList<User>(users, total);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public PaginatedList<User> getTransitiveUsersForRole(long roleId,
			long startRecord, long recordCount) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			RoleEntity role = entityManager.find(RoleEntity.class, roleId);

			Set<UserEntity> users = new HashSet<>();
			users.addAll(role.getUsers());

			Set<Long> roles = new HashSet<>();
			Set<Long> groups = new HashSet<>();

			users.addAll(getTransitiveUsers(role, groups, roles));

			return new PaginatedList<User>(jpaUsersToApiUsers(users),
					users.size());
		} finally {
			entityManager.close();
		}
	}

	private Set<UserEntity> getTransitiveUsers(RoleEntity role,
			Set<Long> groups, Set<Long> roles) {
		Set<UserEntity> users = new HashSet<>();
		users.addAll(role.getUsers());
		roles.add(role.getId());
		for (RoleEntity r : role.getParentRoles()) {
			if (!roles.contains(r.getId())) {
				users.addAll(getTransitiveUsers(r, groups, roles));
			}
		}
		for (UserGroupEntity g : role.getGroups()) {
			if (!groups.contains(g.getId())) {
				users.addAll(getTransitiveUsers(g, groups, roles));
			}
		}
		return users;
	}

	private Set<UserEntity> getTransitiveUsers(UserGroupEntity group,
			Set<Long> groups, Set<Long> roles) {
		Set<UserEntity> users = new HashSet<>();
		users.addAll(group.getUsers());
		groups.add(group.getId());
		for (RoleEntity r : group.getRoles()) {
			if (!roles.contains(r.getId())) {
				users.addAll(getTransitiveUsers(r, groups, roles));
			}
		}
		for (UserGroupEntity g : group.getChildGroups()) {
			if (!groups.contains(g.getId())) {
				users.addAll(getTransitiveUsers(g, groups, roles));
			}
		}
		return users;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaginatedList<User> searchUsersForRole(String name, long roleId,
			long startRecord, long recordCount) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {

			int total = entityManager
					.createNamedQuery(UserEntity.SEARCH_USERS_BY_ROLE)
					.setParameter("roleId", roleId).setParameter("name", name)
					.setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
					.getResultList().size();

			List<User> users = jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.SEARCH_USERS_BY_ROLE)
					.setParameter("roleId", roleId).setParameter("name", name)
					.setFirstResult((int) startRecord)
					.setMaxResults((int) recordCount).getResultList());

			return new PaginatedList<User>(users, total);
		} finally {
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaginatedList<User> searchUsersByName(String name, long startRecord,
			long recordCount) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {

			int total = entityManager
					.createNamedQuery(UserEntity.SEARCH_USERS_BY_NAME)
					.setParameter("name", "%" + name + "%").setFirstResult(0)
					.setMaxResults(Integer.MAX_VALUE).getResultList().size();

			List<User> users = jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.SEARCH_USERS_BY_NAME)
					.setParameter("name", "%" + name + "%")
					.setFirstResult((int) startRecord)
					.setMaxResults((int) recordCount).getResultList());

			return new PaginatedList<User>(users, total);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public User getUserByEmail(String email) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			@SuppressWarnings("unchecked")
			List<User> users = jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USER_BY_EMAIL)
					.setParameter("email", email).getResultList());

			if (users.isEmpty()) {
				throw new ItemNotFoundException("User ", email);
			}
			return users.get(0);

		} finally {
			entityManager.close();
		}
	}

	@Override
	public User validateUserAuthenticationToken(
			IAuthenticationToken authenticationToken)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
				return handleValidateUserPasswordToken(
						entityManager,
						(UsernamePasswordAuthenticationToken) authenticationToken);
			} else if (authenticationToken instanceof CertificateAuthenticationToken) {
				return handleValidateCertificateToken(entityManager,
						(CertificateAuthenticationToken) authenticationToken);
			} else if (authenticationToken instanceof PasswordResetToken) {
				return handleValidatePasswordResetToken(entityManager,
						(PasswordResetToken) authenticationToken);
			} else if (authenticationToken instanceof CookieAuthenticationToken) {
				return handleCookieAuthenticationToken(entityManager,
						(CookieAuthenticationToken) authenticationToken);
			} else {
				return null;
			}
		} catch (NoResultException e) {
			return null;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public void addAuthenticationTokenToUser(User user, IAuthenticationToken authenticationToken)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			UserEntity userEntity = entityManager.find(UserEntity.class, user.getId());
			addAuthenticationTokenToUser(userEntity, authenticationToken, entityManager);
		} catch (Exception e) {
			tx.rollback();
			throw new InvalidTokenException(e);
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	private void addAuthenticationTokenToUser(UserEntity user,
			IAuthenticationToken authenticationToken,
			EntityManager entityManager) throws SecurityDataException {
		if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
			handleAddUsernamePasswordToken(entityManager, user,
					(UsernamePasswordAuthenticationToken) authenticationToken);
		} else if (authenticationToken instanceof CertificateAuthenticationToken) {
			handleAddCertificateToken(entityManager, user,
					(CertificateAuthenticationToken) authenticationToken);
		} else if (authenticationToken instanceof PasswordResetToken) {
			handleAddPasswordResetToken(entityManager, user,
					(PasswordResetToken) authenticationToken);
		} else if (authenticationToken instanceof LdapAuthenticationToken) {
			handleAddLdapAuthentication(entityManager, user,
					(LdapAuthenticationToken) authenticationToken);
		} else if (authenticationToken == null) {
			throw new InvalidTokenException();
		} else {
			throw new InvalidTokenException();
		}
	}

	private void handleAddLdapAuthentication(EntityManager entityManager,
			UserEntity userEntity, LdapAuthenticationToken token) {

		CredentialEntity credential = new CredentialEntity();
		credential.setCredentialKey(token.getUsername().toString());

		credential.setUser(userEntity);
		credential.setCredentialType(getCredentialTypeEntity(entityManager,
				token.getClass().getName()));

		entityManager.persist(credential);
	}

	@Override
	public void updateAuthenticationToken(User user, IAuthenticationToken authenticationToken)
			throws SecurityDataException, ItemNotFoundException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			if (null != user.getId()) {
				if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
					UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authenticationToken;

					CredentialEntity credential = entityManager
							.createNamedQuery(
									CredentialEntity.FIND_CREDENTIAL_BY_USER_AND_TYPE,
									CredentialEntity.class)
							.setParameter("id", user.getId())
							.setParameter(
									"credentialTypeName",
									UsernamePasswordAuthenticationToken.class
											.getName()).getSingleResult();

					if (null != credential) {
						// TODO : username shud not be allowed to change?
						// if (null != token.getUsername())
						// credential.setCredentialKey(token.getUsername());

						// TODO: Make sure this is encrypted
						credential.setCredentialBinary(new String(token.getPassword()).getBytes());
						credential.setLastChangesDateTime(new Date());

						// remove reset flag from user.
						UserEntity userDB = entityManager.find(UserEntity.class, user.getId());
						if (userDB.isChangePasswordAtFirstLogin())
							userDB.setChangePasswordAtFirstLogin(false);

						userDB.setModifiedDateTime(new Date());
						credential.setExpiresDateTime(calPasswordExpiryDate(getPasswordPolicyForUser(userDB)));
					} else {
						throw new ItemNotFoundException("User credential", user.getId());
					}
				} else if (authenticationToken instanceof CertificateAuthenticationToken) {
					CertificateAuthenticationToken token = (CertificateAuthenticationToken) authenticationToken;
					CredentialEntity credential = entityManager
							.createNamedQuery(
									CredentialEntity.FIND_CREDENTIAL_BY_USER_AND_TYPE,
									CredentialEntity.class)
							.setParameter("id", user.getId())
							.setParameter(
									"credentialTypeName",
									CertificateAuthenticationToken.class
											.getName()).getSingleResult();
					if (null != credential) {
						credential.setCredentialBinary(token.getKey());
						credential.setLastChangesDateTime(new Date());
					}
					UserEntity userDB = entityManager.find(UserEntity.class,
							user.getId());
					userDB.setModifiedDateTime(new Date());
					credential.setExpiresDateTime(calPasswordExpiryDate(getPasswordPolicyForUser(userDB)));
				}
			} else {
				throw new ItemNotFoundException("User ", user.getId());
			}
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public void deleteAuthenticationToken(
			IAuthenticationToken authenticationToken)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {

			tx.begin();

			if (authenticationToken instanceof PasswordResetToken) {
				PasswordResetToken token = (PasswordResetToken) authenticationToken;
				deleteCredentialByCredentialKey(token.getToken(), entityManager);
			} else if (authenticationToken instanceof LdapAuthenticationToken) {
				LdapAuthenticationToken token = (LdapAuthenticationToken) authenticationToken;
				deleteCredentialByCredentialKey(token.getUsername().toString(),
						entityManager);
			}

		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	private void deleteCredentialByCredentialKey(String credentialKey,
			EntityManager entityManager) {
		CredentialEntity credentialEntity = (CredentialEntity) entityManager
				.createNamedQuery(CredentialEntity.FIND_CREDENTIAL_BY_KEY)
				.setParameter("credentialKey", credentialKey).getSingleResult();
		UserEntity userEntity = credentialEntity.getUser();
		userEntity.getCredentials().remove(credentialEntity);

		entityManager.remove(credentialEntity);
	}

	@Override
	public User getUserByUsername(String domain, String username) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			UserEntity user = (UserEntity) entityManager
					.createNamedQuery(UserEntity.FIND_USER_BY_CREDENTIAL_KEY)
					.setParameter("domain", domain)
					.setParameter("credentialKey", username).getSingleResult();

			return (User) ObjectConverter.jpaToApi(user);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public User getUserByUserId(Long userId) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			if (userId == null) {
				throw new ItemCannotBeNullException("UserID");
			}
			UserEntity userDB = entityManager.find(UserEntity.class, userId);
			if (userDB != null) {
				return (User) ObjectConverter.jpaToApi(userDB);
			} else {
				return null;
			}
		} finally {
			entityManager.close();
		}
	}

	/*
	 * 
	 * Private Methods
	 */

	private List<User> jpaUsersToApiUsers(Collection<UserEntity> usersDB) {
		List<User> users = new ArrayList<User>();
		for (UserEntity userDB : usersDB) {
			users.add((User) ObjectConverter.jpaToApi(userDB));
		}
		return users;
	}

	private User handleValidateUserPasswordToken(EntityManager entityManager,
			UsernamePasswordAuthenticationToken token)
			throws SecurityDataException {

		CredentialEntity credential = entityManager
				.createNamedQuery(CredentialEntity.FIND_CREDENTIAL_BY_KEY,
						CredentialEntity.class)
				.setParameter("credentialKey", token.getUsername())
				.setParameter("domain", token.getDomain()).getSingleResult();

		if (expired(credential)) {
			throw new CredentialExpiredException();
		}

		String password = new String(credential.getCredentialBinary());

		if (token.getUsername().equals(credential.getCredentialKey())
				&& new String(token.getPassword()).equals(password)) {
			return (User) ObjectConverter.jpaToApi(credential.getUser());
		}
		return null;
	}

	private User handleValidateCertificateToken(EntityManager entityManager,
			CertificateAuthenticationToken token) {
		UserEntity user = (UserEntity) entityManager
				.createNamedQuery(UserEntity.FIND_USER_BY_CREDENTIAL_KEY)
				.setParameter("domain", token.getDomain())
				.setParameter("credentialKey", token.getUsername())
				.getSingleResult();
		if (user != null) {
			return (User) ObjectConverter.jpaToApi(user);
		}
		return null;
	}

	private void handleAddUsernamePasswordToken(EntityManager entityManager,
			UserEntity userEntity, UsernamePasswordAuthenticationToken token)
			throws SecurityDataException {

		CredentialEntity credential = new CredentialEntity();
		credential.setCredentialKey(token.getUsername());

		// TODO: Make sure this is encrypted
		if (null != token.getPassword()) {
			credential.setCredentialBinary(new String(token.getPassword())
					.getBytes());
		}
		credential.setUser(userEntity);

		credential.setCredentialType(getCredentialTypeEntity(entityManager,
				token.getClass().getName()));
		PasswordPolicyEntity passwordPolicyEntity = getPasswordPolicyForUser(userEntity);
		if (passwordPolicyEntity == null) {
			passwordPolicyEntity = new PasswordPolicyEntity();
		}
		credential
				.setExpiresDateTime(calPasswordExpiryDate(passwordPolicyEntity));
		entityManager.persist(credential);
	}

	private Date calPasswordExpiryDate(PasswordPolicyEntity pe) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, pe.getPasswdAge());
		return cal.getTime();
	}

	private PasswordPolicyEntity getPasswordPolicyForUser(UserEntity ue)
			throws SecurityDataException {
		PasswordPolicyEntity ppe = ue.getOrganization().getTenant()
				.getPasswordPolicy();
		return ppe;
	}

	private void handleAddCertificateToken(EntityManager entityManager,
			UserEntity userEntity, CertificateAuthenticationToken token) {

		CredentialEntity credential = new CredentialEntity();
		credential.setCredentialKey(token.getUsername());
		credential.setCredentialBinary(token.getKey());

		credential.setUser(userEntity);
		credential.setCredentialType(getCredentialTypeEntity(entityManager,
				token.getClass().getName()));

		entityManager.persist(credential);
	}

	private void handleAddPasswordResetToken(EntityManager entityManager,
			UserEntity userEntity, PasswordResetToken token)
			throws SecurityDataException {

		// remove existingreset token
		List<CredentialEntity> existingCredentials = userEntity
				.getCredentials();
		List<CredentialEntity> updatedCredentialList = new ArrayList<>();
		List<CredentialEntity> removeCredentialList = new ArrayList<>();

		for (CredentialEntity userCredential : existingCredentials) {
			if (userCredential.getCredentialType().getCanonicalName()
					.equals(token.getClass().getName()))
				removeCredentialList.add(userCredential);
			else
				updatedCredentialList.add(userCredential);
		}

		if (!removeCredentialList.isEmpty()) {
			for (CredentialEntity cr : removeCredentialList)
				deleteCredentialByCredentialKey(cr.getCredentialKey(),
						entityManager);
		}

		if (!updatedCredentialList.isEmpty())
			userEntity.setCredentials(updatedCredentialList);

		CredentialEntity credential = new CredentialEntity();
		credential.setCredentialKey(token.getToken());
		credential.setExpiresDateTime(token.getExpiryDate());
		credential.setLastChangesDateTime(token.getDateGenerated());
		credential.setUser(userEntity);
		credential.setCredentialType(getCredentialTypeEntity(entityManager,
				token.getClass().getName()));

		entityManager.persist(credential);
		userEntity.getCredentials().add(credential);
	}

	private User handleValidatePasswordResetToken(EntityManager entityManager,
			PasswordResetToken token) throws SecurityDataException {

		UserEntity user = (UserEntity) entityManager
				.createNamedQuery(UserEntity.FIND_USER_BY_CREDENTIAL_KEY)
				.setParameter("credentialKey", token.getToken())
				.getSingleResult();

		CredentialEntity credential = entityManager
				.createNamedQuery(CredentialEntity.FIND_CREDENTIAL_BY_KEY,
						CredentialEntity.class)
				.setParameter("credentialKey", token.getToken())
				.getSingleResult();

		if (token.getToken().equals(credential.getCredentialKey())
				&& !expired(credential)) {
			return (User) ObjectConverter.jpaToApi(user);
		} else {
			throw new SecurityException("Inavlid Token, token may be expired.");
		}
	}

	private User handleCookieAuthenticationToken(EntityManager entityManager,
			CookieAuthenticationToken token) {

		try {
			UserSessionEntity userSession = (UserSessionEntity) entityManager
					.createNamedQuery(UserSessionEntity.GET_BY_SESSION_ID)
					.setParameter("sessionId", token.getCookieId())
					.getSingleResult();

			UserEntity user = userSession.getBelongsToUser();

			return (User) ObjectConverter.jpaToApi(user);
		} catch (NoResultException e) {
			throw new SecurityException("Inavlid Certificate Token.");
		}
	}

	private CredentialTypeEntity getCredentialTypeEntity(
			EntityManager entityManager, String tokenName) {
		try {
			return entityManager
					.createNamedQuery(
							CredentialTypeEntity.FIND_CREDENTIAL_TYPES_BY_NAME,
							CredentialTypeEntity.class)
					.setParameter("canonicalName", tokenName).getSingleResult();
		} catch (NoResultException e) {
			CredentialTypeEntity credentialTypeEntity = new CredentialTypeEntity();
			credentialTypeEntity.setCanonicalName(tokenName);
			entityManager.persist(credentialTypeEntity);
			return credentialTypeEntity;
		}
	}

	private boolean expired(CredentialEntity credential) {
		Date currentDate = new Date();
		return (currentDate.after(credential.getExpiresDateTime()));

	}

	@Override
	public SessionId attachSessionToUser(SessionId session, long userId)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			UserEntity userDB = (UserEntity) entityManager.find(
					UserEntity.class, userId);

			UserSessionEntity userSession = userDB.getUserSession();
			if (userDB.getUserSession() == null) {
				userSession = new UserSessionEntity();
			}

			userSession.setSessionId(session.getSessionId().toString());
			userSession.setDateCreated(new Date());
			userSession.setBelongsToUser(userDB);

			entityManager.persist(userSession);

			userDB.setUserSession(userSession);
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
		return session;
	}

	@Override
	public void removeSessionFromUser(Long userId) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			// Query for User and delete it
			UserEntity user = entityManager.find(UserEntity.class, userId);

			entityManager.remove(user.getUserSession());
			user.setUserSession(null);
			entityManager.persist(user);
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			entityManager.close();
		}
	}

	@Override
	public Collection<User> queryUsers(String seed, long startRecord,
			long maxRecords) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {

			if (seed != null && seed.length() >= 1) {
				seed = seed.toLowerCase();
			}
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserEntity> criteriaQuery = builder
					.createQuery(UserEntity.class);
			Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);

			Join<UserEntity, CredentialEntity> crJoin = userRoot.join(
					"credentials", JoinType.INNER);
			Join<UserEntity, ContactEntity> cnJoin = userRoot.join("contact",
					JoinType.INNER);

			List<Predicate> predicates = new ArrayList<>();
			predicates
					.add(builder.like(
							builder.lower(crJoin.get("credentialKey").as(
									String.class)), "%" + seed + "%"));
			predicates.add(builder.like(
					builder.lower(cnJoin.get("firstName").as(String.class)),
					"%" + seed + "%"));
			predicates.add(builder.like(
					builder.lower(cnJoin.get("lastName").as(String.class)), "%"
							+ seed + "%"));

			Predicate[] conditions = predicates
					.toArray(new Predicate[predicates.size()]);

			criteriaQuery.where(builder.or(conditions));
			criteriaQuery.distinct(true);

			List<User> users = new ArrayList<>();

			List<UserEntity> userDBList = entityManager
					.createQuery(criteriaQuery)
					.setFirstResult((int) startRecord)
					.setMaxResults((int) maxRecords).getResultList();

			for (UserEntity userDB : userDBList) {
				users.add((User) ObjectConverter.jpaToApi(userDB));
			}

			return users;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public PaginatedList<User> getUsersForPermission(Permission permission,
			long startRecord, long maxRecords) throws SecurityDataException {

		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			PermissionEntity perm = (PermissionEntity) ObjectConverter
					.apiToJpa(permission);

			int total = entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_PERMISSION,
							UserEntity.class)
					.setParameter("productCanonicalName",
							perm.getProductCanonicalName())
					.setParameter("categoryCanonicalName",
							perm.getCategoryCanonicalName())
					.setParameter("typeCanonicalName",
							perm.getTypeCanonicalName())
					.setParameter("subTypeCanonicalName",
							perm.getSubTypeCanonicalName())
					.setParameter("canonicalName", perm.getCanonicalName())
					.setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
					.getResultList().size();

			List<User> users = jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_PERMISSION,
							UserEntity.class)
					.setParameter("productCanonicalName",
							perm.getProductCanonicalName())
					.setParameter("categoryCanonicalName",
							perm.getCategoryCanonicalName())
					.setParameter("typeCanonicalName",
							perm.getTypeCanonicalName())
					.setParameter("subTypeCanonicalName",
							perm.getSubTypeCanonicalName())
					.setParameter("canonicalName", perm.getCanonicalName())
					.setFirstResult((int) startRecord)
					.setMaxResults((int) maxRecords).getResultList());

			return new PaginatedList<User>(users, total);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public PaginatedList<String> getUserNamesForPermission(
			Permission permission, long startRecord, long maxRecords)
			throws SecurityDataException {

		List<String> userNames = new ArrayList<String>();
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			PermissionEntity perm = (PermissionEntity) ObjectConverter
					.apiToJpa(permission);
			int total = entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_PERMISSION,
							UserEntity.class)
					.setParameter("productCanonicalName",
							perm.getProductCanonicalName())
					.setParameter("categoryCanonicalName",
							perm.getCategoryCanonicalName())
					.setParameter("typeCanonicalName",
							perm.getTypeCanonicalName())
					.setParameter("subTypeCanonicalName",
							perm.getSubTypeCanonicalName())
					.setParameter("canonicalName", perm.getCanonicalName())
					.setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
					.getResultList().size();

			List<UserEntity> userDBList = entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_PERMISSION,
							UserEntity.class)
					.setParameter("productCanonicalName",
							perm.getProductCanonicalName())
					.setParameter("categoryCanonicalName",
							perm.getCategoryCanonicalName())
					.setParameter("typeCanonicalName",
							perm.getTypeCanonicalName())
					.setParameter("subTypeCanonicalName",
							perm.getSubTypeCanonicalName())
					.setParameter("canonicalName", perm.getCanonicalName())
					.setFirstResult((int) startRecord)
					.setMaxResults((int) maxRecords).getResultList();

			for (UserEntity userEntity : userDBList) {
				userNames.add(((User) ObjectConverter.jpaToApi(userEntity))
						.getUsername());
			}

			return new PaginatedList<String>(userNames, total);
		} finally {
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersForOrganization(long organizationId)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			return jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_ORGANIZATION)
					.setParameter("organizationId", organizationId)
					.setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
					.getResultList());
		} finally {
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersForRole(long roleId) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {

			return jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_ROLE)
					.setParameter("roleId", roleId).setFirstResult(0)
					.setMaxResults(Integer.MAX_VALUE).getResultList());
		} finally {
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> searchUsersForRole(String name, long roleId) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {

			return jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.SEARCH_USERS_BY_ROLE)
					.setParameter("roleId", roleId).setParameter("name", name)
					.setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
					.getResultList());
		} finally {
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> searchUsersByName(String name) {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			return jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.SEARCH_USERS_BY_NAME)
					.setParameter("name", "%" + name + "%").setFirstResult(0)
					.setMaxResults(Integer.MAX_VALUE).getResultList());
		} finally {
			entityManager.close();
		}
	}

	@Override
	public List<User> getUsersForPermission(Permission permission)
			throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			PermissionEntity perm = (PermissionEntity) ObjectConverter
					.apiToJpa(permission);
			return jpaUsersToApiUsers(entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_PERMISSION,
							UserEntity.class)
					.setParameter("productCanonicalName",
							perm.getProductCanonicalName())
					.setParameter("categoryCanonicalName",
							perm.getCategoryCanonicalName())
					.setParameter("typeCanonicalName",
							perm.getTypeCanonicalName())
					.setParameter("subTypeCanonicalName",
							perm.getSubTypeCanonicalName())
					.setParameter("canonicalName", perm.getCanonicalName())
					.getResultList());
		} finally {
			entityManager.close();
		}
	}

	@Override
	public List<String> getUserNamesForPermission(Permission permission)
			throws SecurityDataException {
		List<String> userNames = new ArrayList<String>();
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			PermissionEntity perm = (PermissionEntity) ObjectConverter
					.apiToJpa(permission);
			List<UserEntity> userDBList = entityManager
					.createNamedQuery(UserEntity.FIND_USERS_BY_PERMISSION,
							UserEntity.class)
					.setParameter("productCanonicalName",
							perm.getProductCanonicalName())
					.setParameter("categoryCanonicalName",
							perm.getCategoryCanonicalName())
					.setParameter("typeCanonicalName",
							perm.getTypeCanonicalName())
					.setParameter("subTypeCanonicalName",
							perm.getSubTypeCanonicalName())
					.setParameter("canonicalName", perm.getCanonicalName())
					.getResultList();
			for (UserEntity userEntity : userDBList) {
				userNames.add(((User) ObjectConverter.jpaToApi(userEntity))
						.getUsername());
			}
		} finally {
			entityManager.close();
		}
		return userNames;
	}
}

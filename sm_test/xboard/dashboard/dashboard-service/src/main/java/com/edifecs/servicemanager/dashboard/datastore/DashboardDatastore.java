package com.edifecs.servicemanager.dashboard.datastore;

import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.servicemanager.dashboard.XBoardException;
import com.edifecs.servicemanager.dashboard.entity.CompositeDataset;
import com.edifecs.servicemanager.dashboard.entity.DashboardDataObject;
import com.edifecs.servicemanager.dashboard.entity.DashboardDataType;
import com.edifecs.servicemanager.dashboard.entity.Dataset;
import com.edifecs.servicemanager.dashboard.entity.DatasetType;
import com.edifecs.servicemanager.dashboard.entity.Datasource;
import com.edifecs.servicemanager.dashboard.entity.DatastoreType;
import com.edifecs.servicemanager.dashboard.entity.Parameter;
import com.edifecs.servicemanager.dashboard.entity.ParameterDef;
import com.edifecs.servicemanager.dashboard.entity.Widget;
import com.edifecs.servicemanager.dashboard.entity.WidgetType;
import com.edifecs.servicemanager.dashboard.entity.XBoard;
import com.edifecs.servicemanager.dashboard.entity.XBoardType;
import com.edifecs.servicemanager.dashboard.util.ExceptionMessages;

public class DashboardDatastore {

	private SessionFactory sessionFactory;

	private Logger logger;

	public DashboardDatastore() {
		super();
	}

	public DashboardDatastore(Logger logger) {
		super();
		this.logger = logger;
	}

	public void initialize() throws XBoardException {
		Properties resourceProperties = new Properties();
		// resourceProperties.put("Username", "root");
		// resourceProperties.put("Password", "root");
		// resourceProperties.put("Driver", "com.mysql.jdbc.Driver");
		// resourceProperties.put("URL", "jdbc:mysql://localhost:3306/katrina");
		// resourceProperties.put("Dialect",
		// "org.hibernate.dialect.MySQLDialect");

		resourceProperties.put("Username", "sa");
		resourceProperties.put("Password", "");
		resourceProperties.put("Driver", "org.h2.Driver");
		resourceProperties.put("URL", "jdbc:h2:mem:TestDatabase");
		resourceProperties.put("Dialect", "org.hibernate.dialect.H2Dialect");
		resourceProperties.put("AutoCreate", true);

		logger = LoggerFactory.getLogger(this.getClass());

		initialize(resourceProperties);
	}

	public void initialize(Properties resourceProperties)
			throws XBoardException {
		if (resourceProperties == null) {
			throw new XBoardException(ExceptionMessages.DB_RESOURCE_ERROR_MSG);
		}

		Properties properties = new Properties();
		properties.put("javax.persistence.provider",
				"org.hibernate.jpa.HibernatePersistenceProvider");
		properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");

		properties.put("hibernate.connection.username",
				resourceProperties.get("Username"));
		properties.put("hibernate.connection.password",
				resourceProperties.get("Password"));
		properties.put("hibernate.connection.driver_class",
				resourceProperties.get("Driver"));
		properties.put("hibernate.connection.url",
				resourceProperties.get("URL"));
		properties.put("hibernate.dialect", resourceProperties.get("Dialect"));

		if (resourceProperties.get("AutoCreate") != null
				& new Boolean(resourceProperties.get("AutoCreate").toString())) {
			properties.put("hibernate.hbm2ddl.auto", "update");
		}

		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.format_sql", "true");
		properties.put("hibernate.archive.autodetection", "class ,hbm");
		properties.put("hibernate.current_session_context_class", "thread");

		// c3p0
		properties.put("hibernate.c3p0.validate", "true");
		properties
				.put("hibernate.connection.provider_class",
						"org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
		properties.put("hibernate.c3p0.min_size", "5");
		properties.put("hibernate.c3p0.max_size", "600");
		properties.put("hibernate.c3p0.timeout", "1800");
		properties.put("hibernate.c3p0.max_statements", "50");
		properties.put("hibernate.c3p0.preferredTestQuery", "SELECT 1;");
		properties.put("hibernate.c3p0.testConnectionOnCheckout", "true");
		properties.put("hibernate.c3p0.idle_test_period", "3000");

		connect(properties);
	}

	/**
	 * This creates a connection to the database directly using JPS style
	 * properties.
	 * 
	 * 
	 */
	public void connect(Properties properties) {

		try {

			logger.debug("configuring hibernate");
			Configuration configuration = new Configuration();
			configuration.addProperties(properties);

			configuration.addAnnotatedClass(ParameterDef.class);
			configuration.addAnnotatedClass(DatastoreType.class);
			configuration.addAnnotatedClass(Datasource.class);
			configuration.addAnnotatedClass(Dataset.class);
			configuration.addAnnotatedClass(DatasetType.class);
			configuration.addAnnotatedClass(Parameter.class);
			configuration.addAnnotatedClass(WidgetType.class);
			configuration.addAnnotatedClass(Widget.class);
			configuration.addAnnotatedClass(XBoardType.class);
			configuration.addAnnotatedClass(XBoard.class);
			configuration.addAnnotatedClass(CompositeDataset.class);

			StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties());

			logger.debug(
					"configuring hibernate : building service registery : {}",
					serviceRegistryBuilder.getBootstrapServiceRegistry());

			sessionFactory = configuration
					.buildSessionFactory(serviceRegistryBuilder
							.getBootstrapServiceRegistry());

			logger.debug("hibernate session created successfully");

		} catch (Error e) {
			e.printStackTrace();
			logger.error("error configuring dashboard datastore", e);
		}
	}

	public void close() {
		sessionFactory.getCurrentSession().close();
	}

	public <T extends DashboardDataObject> Long create(T prototype)
			throws XBoardException {
		if (prototype == null) {
			return null;
		}

		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

		long id;
		try {

			id = (Long) sessionFactory.getCurrentSession().save(prototype);
			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			if (e.getClass().equals(ConstraintViolationException.class))
				throw new XBoardException(
						"Name not unique. Please use a unique name");
			e.printStackTrace();
			throw new XBoardException("Error inserting " + prototype.type()
					+ " into database");
		}
		return id;
	}

	public <T extends DashboardDataObject> boolean update(T prototype)
			throws XBoardException {

		boolean success = false;
		if (prototype == null) {
			return success;
		}

		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

		try {

			sessionFactory.getCurrentSession().saveOrUpdate(prototype);
			tx.commit();
			success = true;

		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new XBoardException("Error updating " + prototype.type()
					+ " into database");
		}
		return success;
	}

	public <T extends DashboardDataObject> boolean delete(T prototype)
			throws XBoardException {

		boolean success = false;
		if (prototype == null) {
			return success;
		}

		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

		try {

			sessionFactory.getCurrentSession().delete(prototype);
			tx.commit();
			success = true;

		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new XBoardException("Error deleting " + prototype.type());
		}
		return success;
	}

	@SuppressWarnings("unchecked")
	public <T extends DashboardDataObject> T getById(Class<T> type, long id)
			throws XBoardException {

		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

		Object obj;
		try {
			obj = sessionFactory.getCurrentSession().get(type, id);
			tx.commit();

		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new XBoardException("Item not found : " + type
					+ " with id : " + id);
		}
		return (T) obj;
	}

	public <T extends DashboardDataObject> List<T> getAll(Class<T> type)
			throws XBoardException {
		return getRange(type, 0, Integer.MAX_VALUE);
	}

	@SuppressWarnings("unchecked")
	public <T extends DashboardDataObject> List<T> getRange(Class<T> type,
			long startRecord, long recordCount) throws XBoardException {

		List<T> collection;
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

		try {
			switch (DashboardDataType.byClass(type)) {
			case DATASOURCE_TYPE:

				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT dt FROM DatastoreType dt")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			case DATASOURCE:
				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT d FROM Datasource d")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			case DATASET:
				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT dat FROM Dataset dat")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			case DATASET_TYPE:
				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT datT FROM DatasetType datT")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			case WIDGET:
				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT w FROM Widget w")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			case WIDGET_TYPE:
				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT wt FROM WidgetType wt")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			case XBOARD:
				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT x FROM XBoard x")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			case XBOARD_TYPE:
				collection = sessionFactory.getCurrentSession()
						.createQuery("SELECT xt FROM XBoardType xt")
						.setFirstResult((int) startRecord)
						.setMaxResults((int) recordCount).list();
				break;
			default:
				throw new XBoardException(
						"This data store does not support looking up objects"
								+ " of type " + type.getSimpleName() + ".");
			}
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new XBoardException("Items not found : " + type);
		}

		tx.commit();
		return collection;
	}

	@SuppressWarnings("unchecked")
	public ParameterDef getParameterDefFromName(String defName)
			throws XBoardException {

		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		ParameterDef pDef;
		try {

			List<ParameterDef> pdefs = sessionFactory
					.getCurrentSession()
					.createQuery(
							"SELECT pd FROM ParameterDef pd WHERE pd.name = :name")
					.setParameter("name", defName).list();

			if (null != pdefs && !pdefs.isEmpty())
				pDef = pdefs.get(0);
			else
				pDef = null;

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new XBoardException(e);
		}

		return pDef;
	}

	@SuppressWarnings("unchecked")
	public List<DatasetType> getDatasetTypesForDatasourceType(Long id)
			throws XBoardException {

		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		List<DatasetType> dTypes;
		try {

			dTypes = sessionFactory
					.getCurrentSession()
					.createQuery(
							"SELECT dst FROM DatasetType dst WHERE dst.datastoreType.id = :id")
					.setParameter("id", id).list();

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new XBoardException(e);
		}

		return dTypes;
	}

	@SuppressWarnings("unchecked")
	public List<Widget> getWidgetsForWidgetType(Long widgetTypeId)
			throws XBoardException {

		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		List<Widget> widgets;
		try {

			widgets = sessionFactory
					.getCurrentSession()
					.createQuery(
							"SELECT w FROM Widget w WHERE w.widgetType.id = :id")
					.setParameter("id", widgetTypeId).list();

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new XBoardException(e);
		}

		return widgets;

	}
}

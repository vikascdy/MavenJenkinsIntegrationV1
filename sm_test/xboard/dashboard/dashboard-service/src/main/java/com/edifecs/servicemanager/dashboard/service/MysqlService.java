package com.edifecs.servicemanager.dashboard.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.servicemanager.dashboard.XBoardException;

public class MysqlService implements IDatasourceService {

	private String hostname;

	private String dbName;

	private String username;

	private String passwd;

	private int port;

	int count = 0;

	private Connection con;

	private Logger logger;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

	public MysqlService() {
		super();
		logger = LoggerFactory.getLogger(getClass());
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public Connection createConnection() throws XBoardException {

		String url = "";
		try {
			Class.forName(MYSQL_DRIVER);

			url = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName;
			logger.debug(" URL ::" + url);
			Connection con = DriverManager.getConnection(url, username, passwd);

			logger.debug("Mysql Connection created: " + (++count));
			return con;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new XBoardException(
					"Cannot Load Mysql driver class, Class Not Found. ");
		} catch (Exception e) {
			e.printStackTrace();
			throw new XBoardException("Error connecting to the datasource : "
					+ url + " \n reason : " + e.getMessage());
		}
	}

	public boolean testConnection() throws Exception {

		con = createConnection();
		if (null != con) {
			con.close();
			return true;
		}
		return false;
	}

	public Object getSchema() {

		Map<String, List<Properties>> schemaMap = new HashMap<String, List<Properties>>();
		try {
			if (null == con || con.isClosed())
				con = createConnection();
			java.sql.DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);

			while (rs.next()) {

				ResultSet p = md.getPrimaryKeys(null, null, rs.getString(3));
				String pKeys = "";
				while (p.next()) {
					if (pKeys != "")
						pKeys = pKeys + "," + p.getString(4);
					else
						pKeys = p.getString(4);
				}
				p.close();

				ResultSet rs1 = md.getColumns(null, rs.getString(2),
						rs.getString(3), null);

				List<Properties> columns = new ArrayList<Properties>();
				Properties pKey = new Properties();
				pKey.put("PRIMARY_KEYS", pKeys);
				columns.add(pKey);

				while (rs1.next()) {
					Properties columnMeta = new Properties();
					columnMeta.put("COLUMN_NAME", rs1.getString(4));
					columnMeta.put("COLUMN_TYPE", rs1.getString(6));
					columnMeta.put("IS_AUTOINCREMENT", rs1.getBoolean(23));

					columns.add(columnMeta);
				}

				schemaMap.put(rs.getString(3), columns);
				rs1.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (HashMap<String, List<Properties>>) schemaMap;
	}

	public ResultSet executeQuery(final String query) throws Exception {

		if (con == null || con.isClosed()) {
			logger.debug("connection not active, creating new Mysql connection");
			con = createConnection();
		}
		try {
			final Statement stmt = con.createStatement();
			// TODO : validate query
			ResultSet result = null;
			// ExecutorService executorService =
			// Executors.newFixedThreadPool(3);
			// executorService.execute(new Runnable() {
			//
			// @Override
			// public void run() {
			//
			// try {
			//
			// stmt.executeQuery(query);
			//
			// } catch (SQLException e) {
			// e.printStackTrace();
			// logger.error(
			// "Error executing the MySQL query, reason : "
			// + e.getMessage(), e);
			// }
			//
			// }
			// });
			//
			// executorService.shutdown();
			// while (!executorService.isTerminated())
			// logger.debug("processing query ...");

			logger.debug("executing query : {}", query);
			result = stmt.executeQuery(query);
			logger.debug("query executed successfully. Preparing to Index Result Set...");

			return result;

		} catch (Exception e) {
			throw new XBoardException(
					"Error executing the MySQL query, reason : "
							+ e.getMessage());
		}
	}

	// Testing only
	public static void main(String[] args) {

		MysqlService ms = new MysqlService();
		ms.setDbName("classicmodels");
		ms.setHostname("localhost");
		ms.setPasswd("root");
		ms.setUsername("root");
		ms.setPort(3306);

		try {
			// Gson gson = new Gson();
			// System.out.println("##" + gson.toJson(ms.getSchema()));

			String query = "insert into orderdetails (orderNumber,productCode,quantityOrdered,priceEach,orderLineNumber) "
					+ "select orderNumber,"
					+ "(select productCode from products order by rand() limit 1) productCode,"
					+ "FLOOR(RAND() * 401) + 100 quantityOrdered,"
					+ "FLOOR(RAND() * 101) + 100 priceEach,"
					+ "FLOOR(RAND() * 50) + 50 orderLineNumber "
					+ "from orders limit 3075668";

			String query2 = "insert into orders (orderDate,requiredDate, shippedDate, status, comments, customerNumber)"
					+ "select date((FROM_UNIXTIME(UNIX_TIMESTAMP('2003-04-30 14:53:27') + FLOOR(0 + (RAND() * 63072000))))) orderDate,"
					+ " date((FROM_UNIXTIME(UNIX_TIMESTAMP('2003-04-30 14:53:27') + FLOOR(0 + (RAND() * 63072000)))))requiredDate,"
					+ " date((FROM_UNIXTIME(UNIX_TIMESTAMP('2003-04-30 14:53:27') + FLOOR(0 + (RAND() * 63072000)))))shippedDate,"
					+ " status,"
					+ " comments,"
					+ " (select customerNumber from customers order by rand() limit 1) customerNumber"
					+ " from orders limit 1000000";

			Statement stmt = ms.createConnection().createStatement();
			for (int i = 1; i <= 1; i++) {

				System.out.println("prcoessing.....");
				try {
					System.out.println("Counter : " + i + " Rows Affected : "
							+ stmt.executeUpdate(query));
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

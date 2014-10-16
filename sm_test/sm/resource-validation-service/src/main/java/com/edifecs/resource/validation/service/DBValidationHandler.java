package com.edifecs.resource.validation.service;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

import com.edifecs.epp.isc.annotations.Arg;

import com.edifecs.resource.validation.service.helpers.DBConnection;
import com.edifecs.resource.validation.service.helpers.DBConnectionException;
import com.edifecs.resource.validation.service.helpers.DBConnectionStatus;
import com.edifecs.resource.validation.service.helpers.MSSQLConnection;
import com.edifecs.resource.validation.service.helpers.MySQLConnection;

public class DBValidationHandler extends AbstractCommandHandler implements IDBValidationHandler {

    public boolean check() {
        return true;
    }

    public DBConnectionStatus validateMySQLServer(
            @Arg(name = "connection", required = true, description = "") DBConnection connection) {
        DBConnectionStatus status = new DBConnectionStatus();
        try {
            status.setConnected(MySQLConnection.validate(connection));
        } catch (Exception e) {
            status.setException(e.getMessage());
        }

        return status;
    }

    public DBConnectionStatus validateMSSQLServer(DBConnection connection)
            throws DBConnectionException {
        DBConnectionStatus status = new DBConnectionStatus();
        try {
            status.setConnected(MSSQLConnection.validate(connection));
        } catch (Exception e) {
            status.setException(e.getMessage());
        }

        return status;
    }

    public boolean validateDB(String dbType, String serverType, String server, int port, String dbname, String username,
            String password) {

        boolean connected = false;

        switch (dbType) {
        case "SQLServer":
            try {
                connected = MSSQLConnection.validate(serverType, server, port, dbname, username, password);
            } catch (DBConnectionException e) {
                connected = false;
                getLogger().error("Unable to connect to Resource", e);
            }
            break;
        default:
            break;
        }

        return connected;
    }

    // For Testing Purposes
    public static void main(String[] args) throws DBConnectionException {
        DBValidationHandler handler = new DBValidationHandler();

        DBConnection connection = new DBConnection();
        connection.setServerType("sqlserver");
        connection.setServer("172.29.100.251");
        connection.setPort(1433);
        connection.setDatabase("Test");
        connection.setUser("sahil");
        connection.setPassword("Edifecs@work12");

        handler.validateMSSQLServer(connection);
    }
}

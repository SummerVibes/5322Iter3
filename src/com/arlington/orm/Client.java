package com.arlington.orm;

import java.util.List;

// Bridge pattern, provide stable interface
public class Client {
    DatabaseInterface database;

    public Client(String databaseName, String userName, String password) {
        database = new MysqlDatabase(databaseName, userName, password);
        if (!database.init()) {
            System.out.println("Init database failed");
            System.exit(-1);
        }
    }

    public void close() {
        database.close();
    }

    public Session getSession() {
        return database.getSession();
    }

    public void createTable(Class cls) {
        database.createTable(cls);
    }

    public void createTable(List<Class> clazz) {
        database.createTable(clazz);
    }

    public void updateTable(Class cls) {
        database.updateTable(cls);
    }

    public void recovery(SessionCmd ses) {
        database.recovery(ses);
    }

    public int getInitConnectionNumber() {
        return database.getInitConnectionNumber();
    }

    public void setInitConnectionNumber(int val) {
        database.setInitConnectionNumber(val);
    }

    public int getMaxConnectionNumber() {
        return database.getMaxConnection();
    }

    public void setMaxConnectionNumber(int mac) {
        database.setMaxConnection(mac);
    }

    public int getIncrementalConnections() {
        return database.getIncrementalConnections();
    }

    public void setIncrementalConnections(int inc) {
        database.setIncrementalConnections(inc);
    }
}

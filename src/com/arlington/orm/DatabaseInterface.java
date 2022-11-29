package com.arlington.orm;

import java.util.List;


public interface DatabaseInterface {
    boolean init();
    void close();
    Session getSession();
    void createTable(Class cls);
    void createTable(List<Class> clazz);
    void updateTable(Class cls);
    void recovery(SessionCmd ses);

    int getInitConnectionNumber();
    void setInitConnectionNumber(int initConnectionNumber);
    int getMaxConnection();
    void setMaxConnection(int maxConnection);
    int getIncrementalConnections();
    void setIncrementalConnections(int incrementalConnections);
}

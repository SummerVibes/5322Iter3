package com.arlington.orm;


import com.arlington.orm.common.DataAccessObject;

public interface SessionCmd {
    boolean addSavepoint(String savepointName);
    void rollback();
    void rollback(String savepointName);
    void commit();
    <T> ExecutorCmd<T> getExecutor(Class<T> cls);
    DataAccessObject getDao();
}

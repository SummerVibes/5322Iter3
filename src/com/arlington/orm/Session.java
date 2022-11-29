package com.arlington.orm;

import com.arlington.orm.common.DataAccessObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;

class Session implements SessionCmd {
    private final Connection con;
    private final DataAccessObject dao;
    private final ArrayList<Savepoint> savepoints;
    private boolean used = false;

    Session(Connection con) {
        this.con = con;
        dao = new DataAccessObject(con);
        savepoints = new ArrayList<>();
        Savepoint initSavepoint;
        try {
            initSavepoint = con.setSavepoint("init");
            savepoints.add(initSavepoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataAccessObject getDao() {
        return dao;
    }

    boolean getUsed() {
        return used;
    }

    void setUsed(boolean used) {
        this.used = used;
    }

    public boolean addSavepoint(String savepointName) {
        if (savepoints.contains(savepointName)) {
            return false;
        } else {
            try {
                savepoints.add(con.setSavepoint(savepointName));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    public void rollback() {
        try {
            con.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rollback(String savepointName) {
        try {
            for (Savepoint savepoint : savepoints) {
                if (savepoint.getSavepointName().equals(savepointName)) {
                    con.rollback(savepoint);
                    savepoints.remove(savepoint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dispose() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> ExecutorCmd<T> getExecutor(Class<T> cls) {
        return new Executor<>(cls, this);
    }
}

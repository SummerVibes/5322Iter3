package com.arlington.orm;

import com.arlington.orm.annotation.MaxLength;
import com.arlington.orm.annotation.NotNull;
import com.arlington.orm.annotation.PrimaryKey;
import com.arlington.orm.common.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;

class DatabaseProxy implements DatabaseInterface {
    private boolean init = false;

    private final ArrayList<Session> sessionPool;

    private int connectionNumber = 0;

    private int initConnectionNumber = 5;

    private int maxConnection = 50;

    private int incrementalConnections = 5;

    protected String getTableColumnSqlScript = null;

    protected String databaseName = null;

    DatabaseProxy() {
        sessionPool = new ArrayList<>();
    }

    public int getInitConnectionNumber() {
        return initConnectionNumber;
    }

    public void setInitConnectionNumber(int initConnectionNumber) {
        this.initConnectionNumber = initConnectionNumber;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getIncrementalConnections() {
        return incrementalConnections;
    }

    public void setIncrementalConnections(int incrementalConnections) {
        this.incrementalConnections = incrementalConnections;
    }

    public boolean init() {
        addConnection(initConnectionNumber);
        init = true;
        return true;
    }

    protected Connection getConnection() {
        return null;
    }



    public void createTable(List<Class> classes) {
        for (Class c : classes) {
            createTable(c);
        }
    }

    public void createTable(Class c) {
        if (!init) {
            System.out.println("Init failed");
            return;
        }

        Field[] fields = c.getDeclaredFields();

        String tableName = Utils.getTableName(c);

        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + "(");

        for (Field field : fields) {
            String sqlType = Utils.getSqlType(field);
            sql.append(field.getName()).append(" ").append(sqlType);

            annotationHandler(field, sql);

            sql.append(",");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        System.out.println(sql);

        try {
            sessionPool.get(0).getDao().executeUpdate(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTable(Class c) {
        if (!init) {
            System.out.println("Init failed");
            return;
        }

        String tableName = Utils.getTableName(c);

        Object[] params = new Object[2];
        params[0] = tableName;
        params[1] = databaseName;

        List<Map<String, Object>> result;

        result = sessionPool.get(0).getDao().executeQuery(getTableColumnSqlScript, params);

        Field[] fields = c.getDeclaredFields();

        StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName);

        for (Field field : fields) {
            int flag = 0;
            for (Map<String, Object> map : result) {
                if (field.getName().equals(map.get("COLUMN_NAME").toString())) {
                    result.remove(map);

                    flag = 1;
                    break;
                }
            }

            if (flag == 0) {
                String sqlType = Utils.getSqlType(field);
                sql.append(" ADD ").append(field.getName()).append(" ").append(sqlType);

                annotationHandler(field, sql);

                sql.append(",");
            }
        }

        for (Map<String, Object> map : result) {
            sql.append(" DROP COLUMN ").append(map.get("COLUMN_NAME").toString()).append(",");
        }

        System.out.println(sql);
        try {
            sessionPool.get(0).getDao().executeUpdate(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void annotationHandler(Field field, StringBuilder sql) {
        String sqlType = Utils.getSqlType(field);

        if ("VARCHAR".equals(sqlType)) {
            Annotation a = field.getAnnotation(MaxLength.class);
            if (a != null) {
                sql.append("(").append(field.getAnnotation(MaxLength.class).value()).append(")");
            } else {
                sql.append("(255)");
            }
        }

        if (field.getAnnotation(NotNull.class) != null) {
            sql.append(" NOT NULL ");
        }

        if (field.getAnnotation(PrimaryKey.class) != null) {
            sql.append(" PRIMARY KEY AUTO_INCREMENT ");
        }
    }

    private void addConnection(int addNumber) {
        for (int i = 0; i < addNumber; i++) {
            sessionPool.add(new Session(getConnection()));
        }
        connectionNumber += addNumber;
    }

    public Session getSession() {

        for (Session ses : sessionPool) {
            if (!ses.getUsed()) {
                ses.setUsed(true);
                return ses;
            }
        }

        if (connectionNumber < maxConnection) {
            addConnection(Math.max(maxConnection - connectionNumber, incrementalConnections));
            for (Session ses : sessionPool) {
                if (!ses.getUsed()) {
                    ses.setUsed(true);
                    return ses;
                }
            }
        }

        System.out.println("Exceed max pool size");
        return null;
    }

    public void close() {
        for (Session ses : sessionPool) {
            ses.dispose();
        }
        init = false;
    }

    public void recovery(SessionCmd session) {
        for (Session ses : sessionPool) {
            if (ses.equals(session)) {
                ses.setUsed(false);
            }
        }
    }
}

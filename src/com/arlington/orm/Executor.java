package com.arlington.orm;

import com.arlington.orm.common.DataAccessObject;
import com.arlington.orm.common.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Executor<T> implements ExecutorCmd<T> {
    private final Class<T> cls;
    private StringBuilder sql = new StringBuilder();
    private final DataAccessObject dao;

    // State pattern
    private static final int INIT = 0;
    private static final int UPDATE = 1;
    private static final int SELECT = 2;

    private int status = INIT;

    Executor(Class<T> cls, Session ses) {
        this.cls = cls;
        this.dao = ses.getDao();
    }

    public void add(T obj) {
        sql.append("INSERT INTO ");

        sql.append("`").append(Utils.getTableName(cls)).append("` ");

        sql.append(columns()).append(" VALUES ").append(values(obj));

        System.out.println(sql);

        dao.executeUpdate(sql.toString());

        clearSql();

    }

    public void addAll(List<T> objs) {
        sql.append("INSERT INTO ");

        sql.append("`").append(Utils.getTableName(cls)).append("` ");

        sql.append(columns()).append(" VALUES ");

        for (Object obj : objs) {
            sql.append(values(obj)).append(",");
        }
        sql.deleteCharAt(sql.length()-1);

        System.out.println(sql);

        dao.executeUpdate(sql.toString());

        clearSql();
    }

    private String values(Object obj) {
        StringBuilder values = new StringBuilder("(");

        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            try {
                values.append(Utils.getValueString(Utils.getValue(obj, field))).append(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        values.deleteCharAt(values.length()-1);
        values.append(")");

        return values.toString();
    }

    private String columns() {
        Field[] fields = cls.getDeclaredFields();

        StringBuilder columns = new StringBuilder("(");

        for (Field field : fields) {
            columns.append("`").append(field.getName()).append("`,");
        }
        //delete comma
        columns.deleteCharAt(columns.length()-1);
        columns.append(")");

        return columns.toString();
    }

    public ExecutorCmd<T> delete() {
        if (status == INIT) {
            sql.append("DELETE FROM ").append(Utils.getTableName(cls));
            status = UPDATE;
        }
        return this;
    }

    public ExecutorCmd<T> update(String exp) {
        if (status == INIT) {
            sql.append("UPDATE ").append(Utils.getTableName(cls)).append(" SET ").append(exp);
            status = UPDATE;
        }
        return this;
    }

    public ExecutorCmd<T> where(String exp) {
        sql.append(" WHERE ").append(exp);
        if (status == UPDATE) {
            return this;
        } else if (status == SELECT) {
            return this;
        }
        return null;
    }

    public List<T> execute() {
        System.out.println(sql);

        if (status == SELECT) {
            List<Map<String, Object>> result = dao.executeQuery(sql.toString());

            List<T> lst = new ArrayList<>();
            try {
                for (Map<String, Object> map : result) {
                    T obj = cls.newInstance();
                    Field[] fields = cls.getDeclaredFields();

                    for (Field field : fields) {
                        Utils.setValue(obj, field, map.get(field.getName().toLowerCase()));
                    }

                    lst.add(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lst;
        } else {
            dao.executeUpdate(sql.toString());
            clearSql();
            return null;
        }
    }

    public ExecutorCmd<T> select() {
        status = SELECT;
        sql.append("SELECT * FROM ").append(Utils.getTableName(cls));
        return this;
    }

    public ExecutorCmd<T> limit(int rows) {
        if (status == SELECT) {
            sql.append(" LIMIT ").append(rows);
        }
        return this;
    }

    public ExecutorCmd<T> limit(int offset, int rows) {
        if (status == SELECT) {
            sql.append(" LIMIT ").append(offset).append(", ").append(rows);
        }
        return this;
    }

    private void clearSql() {
        sql = new StringBuilder();
    }
}

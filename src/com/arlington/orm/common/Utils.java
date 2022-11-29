package com.arlington.orm.common;

import java.lang.reflect.Field;
import java.util.Locale;

public class Utils {
    public static <T> boolean contains(T[] array, T obj) {
        for (T i : array) {
            if (i.equals(obj)) {
                return true;
            }
        }
        return false;
    }
    public static String getGetterName(final String fieldName) {
        StringBuilder result = new StringBuilder("get" + fieldName);

        result.setCharAt(3, Character.toUpperCase(fieldName.charAt(0)));

        return result.toString();
    }

    public static String getSetterName(final String fieldName) {
        StringBuilder result = new StringBuilder("set" + fieldName);

        result.setCharAt(3, Character.toUpperCase(fieldName.charAt(0)));

        return result.toString();
    }

    public static Object getValue(Object obj, Field field) throws Exception {
        return obj.getClass().getMethod(getGetterName(field.getName())).invoke(obj);
    }

    public static void setValue(Object obj, Field filed, Object value) throws Exception {
        obj.getClass().getMethod(getSetterName(filed.getName()), filed.getType()).invoke(obj, value);
    }

    public static String getSqlType(Field field) {
        return _getSqlType(field.getGenericType().toString());
    }

    public static String getSqlType(Object obj) {
        return _getSqlType(obj.getClass().getName());
    }

    private static String _getSqlType(String nameString) {
        switch (nameString) {
            case "java.lang.Integer":
            case "int":
                return "INT";

            case "java.lang.String":
            case "class java.lang.String":
                return "VARCHAR";

            case "java.lang.Float":
            case "java.lang.Double":
            case "double":
            case "float":
                return "FLOAT";

            case "java.util.Date":
                return "DATE";
        }
        return null;
    }

    public static String getValueString(Object obj) {
        switch (obj.getClass().getName()) {
            case "java.lang.Integer":
            case "int":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.util.Date":
                return obj.toString();
            case "java.lang.String":
            case "class java.lang.String":
                return "'" + obj + "'";
        }

        return null;
    }

    public static String getTableName(Class c) {
        String[] clsNames = c.getName().split("\\.");
        return clsNames[clsNames.length - 1].toLowerCase(Locale.ROOT);
    }
}

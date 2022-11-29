package com.arlington.orm;

import java.util.List;

public interface ExecutorCmd<T> {
    void add(T obj);
    void addAll(List<T> objs);
    ExecutorCmd<T> delete();
    ExecutorCmd<T> update(String exp);
    ExecutorCmd<T> select();
    ExecutorCmd<T> where(String exp);
    ExecutorCmd<T> limit(int offset, int rows);
    ExecutorCmd<T> limit(int rows);
    List<T> execute();
}

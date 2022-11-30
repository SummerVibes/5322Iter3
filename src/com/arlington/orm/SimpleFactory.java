package com.arlington.orm;

import java.util.List;

//factory method pattern
/*factory class*/
class Factory {
    //In the factory interface, only the method of obtaining the class is written
    public static Number createNumber(){
        return new Number () ;
    }
    public static Number2 createNumber2(){
        return new Number2 ();
    }

    static class Number {
    }

    protected static class Number2 {
    }
}

/*Implementation class*/
class Number2 implements ExecutorCmd{

    public static void cry() {
        System.out.println("XXX");
    }

    @Override
    public void add(Object obj) {

    }

    @Override
    public void addAll(List objs) {

    }

    @Override
    public ExecutorCmd delete() {
        return null;
    }

    @Override
    public ExecutorCmd update(String exp) {
        return null;
    }

    @Override
    public ExecutorCmd select() {
        return null;
    }

    @Override
    public ExecutorCmd where(String exp) {
        return null;
    }

    @Override
    public ExecutorCmd limit(int offset, int rows) {
        return null;
    }

    @Override
    public ExecutorCmd limit(int rows) {
        return null;
    }

    @Override
    public List execute() {
        return null;
    }
}
class Number implements ExecutorCmd{
    public static void cry(){
        System.out.println("XXXXXX");
    }

    @Override
    public void add(Object obj) {

    }

    @Override
    public void addAll(List objs) {

    }

    @Override
    public ExecutorCmd delete() {
        return null;
    }

    @Override
    public ExecutorCmd update(String exp) {
        return null;
    }

    @Override
    public ExecutorCmd select() {
        return null;
    }

    @Override
    public ExecutorCmd where(String exp) {
        return null;
    }

    @Override
    public ExecutorCmd limit(int offset, int rows) {
        return null;
    }

    @Override
    public ExecutorCmd limit(int rows) {
        return null;
    }

    @Override
    public List execute() {
        return null;
    }
}

public class SimpleFactory {
    public static void main(String[] args) {
        //Factory pattern creates objects
        Factory.Number number = Factory.createNumber() ;
        Factory.Number2 number2 = Factory.createNumber2() ;
        Number.cry();
        Number2.cry();

    }
}

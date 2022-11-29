package com.arlington.app;


import com.arlington.orm.Client;
import com.arlington.orm.SessionCmd;

import java.util.*;

public class NTSS {
    public static void main(String[] args) {
        Client db = new Client("test", "root", "123456");
        if (!db.init()) {
            System.out.println("init failed");
            return;
        }

//        db.createTable(Student.class);

        SessionCmd s = db.getSession();

//        Student stu = new Student();
//        stu.setAge(45);
//        stu.setName("Tom");
//
//        Student stu2 = new Student();
//        stu2.setAge(30);
//        stu2.setName("Sofia");
//
//        List<Student> list = new ArrayList<>();
//        list.add(stu);
//        list.add(stu2);
//
//        s.getExecuter(Student.class).addAll(list);
//        s.getExecuter(Student.class).delete().where("id = 2").execute();
        s.getExecutor(Student.class).update("name = \'Maria\'").where("id = 1").execute();


        List<Student> res = s.getExecutor(Student.class).select().execute();
        for (Student st : res) {
            System.out.println(st.getId() + " " + st.getName());
        }

        res = s.getExecutor(Student.class).select().limit(1, 2).execute();
        for (Student st : res) {
            System.out.println(st.getId() + " " + st.getName());
        }

        db.updateTable(Student.class);

        s.commit();

        db.close();
    }
}

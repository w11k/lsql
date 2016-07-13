package com.w11k.lsql.example;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        System.out.println(Driver.class);

        Connection conn = DriverManager.getConnection("jdbc:h2:mem:lsqlexamples;mode=postgresql");

    }

}

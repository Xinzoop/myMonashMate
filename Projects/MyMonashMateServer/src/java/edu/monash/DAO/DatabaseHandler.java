/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.monash.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author zipv5_000
 */
public class DatabaseHandler {
    
    private static String host = "jdbc:derby://localhost:1527/MyMonashMate";
    private static String userName = "petter";
    private static String password = "petter";
    private static Connection databaseConnection;
    
    public static Connection getConnection() throws SQLException{
        if(null != databaseConnection)
            return databaseConnection;
        return DriverManager.getConnection(host, userName, password);
    }
}

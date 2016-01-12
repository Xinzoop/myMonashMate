/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.DAO;

import edu.monash.mymonashmate.entities.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipv5_000
 */
public class AccountDAO {

    private static final String TABLE_USER = "ACCOUNT";
    private static final String FIELD_USERID = "USERID";
    private static final String FIELD_USERNAME = "USERNAME";
    private static final String FIELD_PASSWORD = "PASSWORD";
    private static final String FIELD_PUBLKEY = "PUBLKEY";

    public int login(User info) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("select * from %s where upper(%s) = ? and %s = ?", TABLE_USER, FIELD_USERNAME, FIELD_PASSWORD);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setString(1, info.getEmail().toUpperCase());
            query.setString(2, info.getPassword());
            ResultSet result = query.executeQuery();
            Boolean flag = false;
            if (result.next()) {
                flag = true;
                info.setUserid(result.getInt("USERID"));
            }
            query.close();
            if (!flag) {
                throw new Exception("Username or password is incorrect.");
            }

            // update user public key
            sql = String.format("update %s set %s = ? where %s = ?", TABLE_USER, FIELD_PUBLKEY, FIELD_USERID);
            query = conn.prepareStatement(sql);
            query.setString(1, info.getPublKeyString());
            query.setInt(2, info.getUserid());
            if (1 != query.executeUpdate()) {
                throw new Exception("updating user public key failed.");
            }
            query.close();
            return info.getUserid();
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public int signup(User info) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("select %s from %s where %s=?", FIELD_USERNAME, TABLE_USER, FIELD_USERNAME);
            PreparedStatement query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            query.setString(1, info.getEmail());
            ResultSet result = query.executeQuery();
            if (result.next()) {
                throw new Exception("Email has been registered.");
            }
            query.close();

            sql = String.format("insert into %s (%s, %s, %s) values (?, ?, ?)",
                    TABLE_USER, FIELD_USERNAME, FIELD_PASSWORD, FIELD_PUBLKEY);
            query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            query.setString(1, info.getEmail());
            query.setString(2, info.getPassword());
            query.setString(3, info.getPublKeyString());
            if (1 != query.executeUpdate()) {
                throw new Exception("Updating user table failed.");
            }
            result = query.getGeneratedKeys();
            if (result.next()) {
                info.setUserid(result.getInt(1));
            }
            query.close();
            return info.getUserid();
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public Boolean logout(int userid) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("update %s set %s=null where %s=?",
                    TABLE_USER, FIELD_PUBLKEY, FIELD_USERID);
            PreparedStatement query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            query.setInt(1, userid);
            return query.executeUpdate() == 1;
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public String getClientKeyString(Integer userid) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("select %s from %s where %s=?",
                    FIELD_PUBLKEY, TABLE_USER, FIELD_USERID);
            PreparedStatement query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            query.setInt(1, userid);
            ResultSet results = query.executeQuery();
            if(!results.next())
                throw new Exception("Acquiring user public key failed.");
            return results.getString(FIELD_PUBLKEY);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

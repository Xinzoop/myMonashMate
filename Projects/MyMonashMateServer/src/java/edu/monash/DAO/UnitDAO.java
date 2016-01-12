/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.DAO;

import edu.monash.mymonashmate.entities.Unit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipv5_000
 */
public class UnitDAO {

    private final static String TABLE_UNIT = "UNIT";
    private final static String FIELD_UNITID = "ID";
    private final static String FIELD_UNITNAME = "NAME";
    private final static String FIELD_UNITDETAILS = "DETAILS";
    public final static String TABLE_USERUNIT = "USERUNIT";
    public final static String FIELD_UUSERID = "USERID";
    public final static String FIELD_UUNITID = "UNITID";

    public List<Unit> getUserUnit(int userid) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            List<Unit> units = new ArrayList<>();
            String sql = String.format("select b.%s, b.%s from %s a left join %s b on a.%s = b.%s where a.%s=?",
                    FIELD_UNITID, FIELD_UNITNAME, TABLE_USERUNIT, TABLE_UNIT, FIELD_UUNITID, FIELD_UNITID, FIELD_UUSERID);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, userid);
            ResultSet result = query.executeQuery();
            while (result.next()) {
                Unit unit = new Unit();
                unit.setId(result.getInt(FIELD_UNITID));
                unit.setName(result.getString(FIELD_UNITNAME));
                units.add(unit);
            }
            return units;
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
    public Unit getUnit(int unitid) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("select %s, %s from %s where %s=?",
                    FIELD_UNITID, FIELD_UNITNAME,TABLE_UNIT, FIELD_UNITID);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, unitid);
            ResultSet result = query.executeQuery();
            if (result.next()) {
                Unit unit = new Unit();
                unit.setId(result.getInt(FIELD_UNITID));
                unit.setName(result.getString(FIELD_UNITNAME));
                return unit;
            }
            return null;
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
    public void updateUserUnits(int userid, List<Unit> userUnits) throws Exception{
         Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("delete from %s where %s=?", TABLE_USERUNIT, FIELD_UUSERID);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, userid);
            query.executeUpdate();
            query.close();
            
            for(Unit unit : userUnits){
                sql = String.format("insert into %s (%s, %s) values (?, ?)", TABLE_USERUNIT, FIELD_UUSERID, FIELD_UUNITID);
                query = conn.prepareStatement(sql);
                query.setInt(1, userid);
                query.setInt(2, unit.getId());
                query.executeUpdate();
                query.close();
            }
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
    public List<Unit> getAllUnits() throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            List<Unit> units = new ArrayList<>();
            String sql = String.format("select %s, %s from %s", FIELD_UNITID, FIELD_UNITNAME, TABLE_UNIT);
            PreparedStatement query = conn.prepareStatement(sql);
            ResultSet result = query.executeQuery();
            while (result.next()) {
                Unit unit = new Unit();
                unit.setId(result.getInt(FIELD_UNITID));
                unit.setName(result.getString(FIELD_UNITNAME));
                units.add(unit);
            }
            return units;
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

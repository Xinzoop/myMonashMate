/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.DAO;

import edu.monash.mymonashmate.entities.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipv5_000
 */
public class ProfileDAO {

    private final static String TABLE_PROFILE = "PROFILE";
    private final static String[] fields = {"ID", "SURNAME", "FIRSTNAME", "NICKNAME", "COURSEID", "LATITUDE",
        "LONGITUDE", "NATIONALITY", "NATIVELANGID", "SECONDLANGID", "SUBURB", "FAVFOOD", "FAVMOVIE", "FAVPROGLANG", "FAVUNITID",
        "CURJOB", "PREVJOB"};

    private final static String TABLE_PRIVACY = "PRIVACY";
    private final static String FIELD_PRIVID = "USERID";
    private final static String FIELD_PRIVATTRID = "ATTRID";
    private final static String FIELD_PRIVISIBILITY = "VISIBILITY";

    public Profile getProfile(int userid, Boolean isPrivacy) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            Profile profile = new Profile();
            String fieldID = fields[Profile.ATTR_ID];
            String innerSql = "a." + fieldID;
            for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_UNIT; i++) {
                innerSql += ",a." + fields[i] + " attr" + i;
            }
            innerSql = "select " + innerSql + ",b." + FIELD_PRIVATTRID + " from " + TABLE_PROFILE + " a left join "
                    + TABLE_PRIVACY + " b on a." + fieldID + "=b." + FIELD_PRIVID + " where a." + fieldID + "=?";
            String sql = "select " + fieldID;
            for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_NATIONALITY; i++) {
                sql += ", max(attr" + i + ") col" + i;
            }
            for (int i = Profile.ATTR_NATIONALITY; i < Profile.ATTR_UNIT; i++) {
                sql += ", max(case when " ;
                if(isPrivacy){
                    sql += FIELD_PRIVATTRID + " is null or " + FIELD_PRIVATTRID + "!=" + i;
                }
                else{
                    sql += "1=2";
                }
                sql += " then null else attr" + i + " end) col" + i;
            }
            sql += " from (" + innerSql + ") a group by " + fieldID;
            // read profile info
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, userid);
            ResultSet result = query.executeQuery();
            if (!result.next()) {
                throw new Exception("Not found related profile.");
            }
            profile.setId(userid);
            profile.setFirstname(result.getString("col" + Profile.ATTR_FIRSTNAME));
            profile.setSurname(result.getString("col" + Profile.ATTR_SURNAME));
            profile.setNickname(result.getString("col" + Profile.ATTR_NICKNAME));
            profile.setCourse(new CourseDAO().getCourse(result.getInt("col" + Profile.ATTR_COURSE)));
            // get unit
            profile.setUnits(new UnitDAO().getUserUnit(userid));
            profile.setLatitude(result.getDouble("col" + Profile.ATTR_LATITUDE));
            profile.setLongitude(result.getDouble("col" + Profile.ATTR_LONGITUDE));
            profile.setNationality(result.getString("col" + Profile.ATTR_NATIONALITY));
            profile.setNativLang(result.getString("col" + Profile.ATTR_NATIVLANG));
            profile.setSecondLang(result.getString("col" + Profile.ATTR_SECONDLANG));
            profile.setSuburb(result.getString("col" + Profile.ATTR_SUBURB));
            profile.setFavFood(result.getString("col" + Profile.ATTR_FAVFOOD));
            profile.setFavMovie(result.getString("col" + Profile.ATTR_FAVMOVIE));
            profile.setFavProgLang(result.getString("col" + Profile.ATTR_FAVPROGLANG));
            profile.setFavUnit(new UnitDAO().getUnit(result.getInt("col" + Profile.ATTR_FAVUNIT)));
            profile.setPrevJob(result.getString("col" + Profile.ATTR_PREVJOB));
            profile.setCurJob(result.getString("col" + Profile.ATTR_CURJOB));
            result.close();
            query.close();
            return profile;
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

    public Boolean updateProfile(Profile profile) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            // read current record
            String sql = String.format("delete from %s where %s=?", TABLE_PROFILE, fields[Profile.ATTR_ID]);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, profile.getId());
            query.executeUpdate();
            query.close();
            // insert new record
            sql = String.format("insert into %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) "
                    + "values (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,?,?,?)", TABLE_PROFILE, fields[Profile.ATTR_ID],
                    fields[Profile.ATTR_FIRSTNAME], fields[Profile.ATTR_SURNAME],
                    fields[Profile.ATTR_NICKNAME], fields[Profile.ATTR_COURSE],
                    fields[Profile.ATTR_LATITUDE], fields[Profile.ATTR_LONGITUDE],
                    fields[Profile.ATTR_NATIONALITY], fields[Profile.ATTR_NATIVLANG],
                    fields[Profile.ATTR_SECONDLANG], fields[Profile.ATTR_SUBURB],
                    fields[Profile.ATTR_FAVFOOD], fields[Profile.ATTR_FAVMOVIE],
                    fields[Profile.ATTR_FAVPROGLANG], fields[Profile.ATTR_FAVUNIT],
                    fields[Profile.ATTR_CURJOB], fields[Profile.ATTR_PREVJOB]);
            query = conn.prepareStatement(sql);
            query.setInt(1, profile.getId());
            query.setString(2, profile.getFirstname());
            query.setString(3, profile.getSurname());
            query.setString(4, profile.getNickname());
            if (profile.getCourse() != null) {
                query.setInt(5, profile.getCourse().getId());
            } else {
                query.setNull(5, Types.INTEGER);
            }
            query.setDouble(6, profile.getLatitude());
            query.setDouble(7, profile.getLongitude());
            query.setString(8, profile.getNationality());
            query.setString(9, profile.getNativLang());
            query.setString(10, profile.getSecondLang());
            query.setString(11, profile.getSuburb());
            query.setString(12, profile.getFavFood());
            query.setString(13, profile.getFavMovie());
            query.setString(14, profile.getFavProgLang());
            if (profile.getFavUnit() != null) {
                query.setInt(15, profile.getFavUnit().getId());
            } else {
                query.setNull(15, Types.INTEGER);
            }
            query.setString(16, profile.getCurJob());
            query.setString(17, profile.getPrevJob());
            if (query.executeUpdate() != 1) {
                throw new Exception("inserting profile info failed.");
            }
            // update unit info
            new UnitDAO().updateUserUnits(profile.getId(), profile.getUnits());

            return true;
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

    public Privacy getPrivacy(int userid) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("select %s from %s where %s=? and %s=?", FIELD_PRIVATTRID,
                    TABLE_PRIVACY, FIELD_PRIVID, FIELD_PRIVISIBILITY);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, userid);
            query.setBoolean(2, true);
            ResultSet result = query.executeQuery();
            Privacy privacy = new Privacy();
            privacy.setUserid(userid);
            while (result.next()) {
                privacy.getPublAttrs().add(result.getInt(FIELD_PRIVATTRID));
            }
            query.close();
            result.close();
            return privacy;
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

    public Boolean updatePrivacy(Privacy privacy) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            String sql = String.format("delete from %s where %s=?", TABLE_PRIVACY, FIELD_PRIVID);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, privacy.getUserid());
            query.executeUpdate();

            for (int attrid : privacy.getPublAttrs()) {
                sql = String.format("insert into %s (%s, %s, %s) values (?, ?, ?)",
                        TABLE_PRIVACY, FIELD_PRIVID, FIELD_PRIVATTRID, FIELD_PRIVISIBILITY);
                query = conn.prepareStatement(sql);
                query.setInt(1, privacy.getUserid());
                query.setInt(2, attrid);
                query.setBoolean(3, true);
                query.executeUpdate();
                query.close();
            }
            return true;
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
    
    public String findMatesSql(int userid, MatchCriteria criterion) throws Exception {
        String fieldID = fields[Profile.ATTR_ID];
        // JOIN
        String innerSql = "a." + fieldID;
        for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_UNIT; i++) {
            innerSql += ",a." + fields[i] + " attr" + i;
        }
        innerSql = "select " + innerSql + ",b." + FIELD_PRIVATTRID + ", c." + UnitDAO.FIELD_UUNITID + " from "
                + TABLE_PROFILE + " a left join " + TABLE_PRIVACY + " b on a." + fieldID + "=b." + FIELD_PRIVID
                + " left join " + UnitDAO.TABLE_USERUNIT + " c on a." + fieldID + "=c." + UnitDAO.FIELD_UUSERID;
        String sql = "select " + fieldID + "," + UnitDAO.FIELD_UUNITID + " col" + Profile.ATTR_UNIT;
        for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_NATIONALITY; i++) {
            sql += ", max(attr" + i + ") col" + i;
        }
        for (int i = Profile.ATTR_NATIONALITY; i < Profile.ATTR_UNIT; i++) {
            sql += ", max(case when " + FIELD_PRIVATTRID + "!=" + i + " then null else attr" + i + " end) col" + i;
        }
        sql += " from (" + innerSql + ") a group by " + fieldID + "," + UnitDAO.FIELD_UUNITID;
        // apply criterion
        sql = "select * from (" + sql + ") a where " + fieldID + "!=" + userid;
        // WHERE
        if (criterion.getSourceIDs().size() > 0) {
            String whereSql = "";
            for (int index : criterion.getSourceIDs()) {
                whereSql = "," + index;
            }
            sql += " and " + fieldID + " in (" + whereSql.substring(1) + ") ";
        }

        if (criterion.getCriteriaItems().size() > 0) {
            for (MatchCriteriaItem item : criterion.getCriteriaItems()) {
                String whereSql = "";
                if (item.getAttrID() == Profile.ATTR_UNIT || item.getAttrID() == Profile.ATTR_COURSE
                        || item.getAttrID() == Profile.ATTR_FAVUNIT) {
                    for (String value : item.getAttrValueList()) {
                        whereSql += "," + value;
                    }
                    whereSql = " col" + item.getAttrID() + " in (" + whereSql.substring(1) + ")";
                } else {
                    for (String value : item.getAttrValueList()) {
                        whereSql += ",'" + value.toUpperCase() + "'";
                    }
                    whereSql = "upper(col" + item.getAttrID() + ") in (" + whereSql.substring(1) + ")";
                }
                sql += " and " + whereSql;
            }
        }
        // aggregate, calculate same unit count
        String selFields = "";
        for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_UNIT; i++) {
            selFields += ",col" + i;
        }
        String fieldUnitCount = "sameunits";
        sql = "select " + fieldID + selFields + ", count(col" + Profile.ATTR_UNIT + ") as " + fieldUnitCount
                + " from (" + sql + ") a group by " + fieldID + selFields;
        return sql;
    }
    
    public List<MatchResult> findMates(int userid, MatchCriteria criterion) throws Exception {
        Connection conn = null;
        try {
            // populate current user profile
            Profile userProfile = getProfile(userid, false);
            // query target profiles against criterion
            conn = DatabaseHandler.getConnection();
            String fieldID = fields[Profile.ATTR_ID];
            // JOIN
            String innerSql = "a." + fieldID;
            for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_UNIT; i++) {
                innerSql += ",a." + fields[i] + " attr" + i;
            }
            innerSql = "select " + innerSql + ",b." + FIELD_PRIVATTRID + ", c." + UnitDAO.FIELD_UUNITID + " from " 
                    + TABLE_PROFILE + " a left join " + TABLE_PRIVACY + " b on a." + fieldID + "=b." + FIELD_PRIVID
                    + " left join " + UnitDAO.TABLE_USERUNIT + " c on a." + fieldID + "=c." + UnitDAO.FIELD_UUSERID;
            String sql = "select " + fieldID + "," + UnitDAO.FIELD_UUNITID + " col" + Profile.ATTR_UNIT;
            for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_NATIONALITY; i++) {
                sql += ", max(attr" + i + ") col" + i;
            }
            for (int i = Profile.ATTR_NATIONALITY; i < Profile.ATTR_UNIT; i++) {
                sql += ", max(case when " + FIELD_PRIVATTRID + " is null or " + FIELD_PRIVATTRID + "!=" + i + " then null else attr" + i + " end) col" + i;
            }
            sql += " from (" + innerSql + ") a group by " + fieldID + "," + UnitDAO.FIELD_UUNITID;
            // apply criterion
            sql = "select * from (" + sql + ") a where " + fieldID + "!=" + userid;
            // WHERE
            if (criterion.getSourceIDs().size() > 0) {
                String whereSql = "";
                for (int index : criterion.getSourceIDs()) {
                    whereSql = "," + index;
                }
                sql += " and " + fieldID + " in (" + whereSql.substring(1) + ") ";
            }
            
            if(criterion.getCriteriaItems().size() > 0){
                for(MatchCriteriaItem item : criterion.getCriteriaItems()){
                    String whereSql = "";
                    if (item.getAttrID() == Profile.ATTR_UNIT || item.getAttrID() == Profile.ATTR_COURSE
                            || item.getAttrID() == Profile.ATTR_FAVUNIT) {
                        for(String value : item.getAttrValueList()){
                            whereSql += "," + value;
                        }
                        whereSql = " col" + item.getAttrID() + " in (" + whereSql.substring(1) + ")";
                    }
                    else{
                        for(String value : item.getAttrValueList()){
                            whereSql += ",'" + value.toUpperCase() + "'";
                        }
                        whereSql = "upper(col" + item.getAttrID() + ") in (" + whereSql.substring(1) + ")";
                    }
                    sql += " and " + whereSql;
                }
            }
            // aggregate, calculate same unit count
            String selFields = "";            
            for (int i = Profile.ATTR_SURNAME; i < Profile.ATTR_UNIT; i++) {
                selFields += ",col" + i;
            }
            String fieldUnitCount = "sameunits";
            sql = "select " + fieldID + selFields + ", count(col" + Profile.ATTR_UNIT + ") as " + fieldUnitCount
                    + " from (" + sql + ") a group by " + fieldID + selFields;
           
            PreparedStatement query = conn.prepareStatement(sql);
            ResultSet result = query.executeQuery();
            List<MatchResult> matches = new ArrayList<>();
            while (result.next()) {
                Profile profile = new Profile();
                profile.setId(result.getInt(fieldID));
                profile.setFirstname(result.getString("col" + Profile.ATTR_FIRSTNAME));
                profile.setSurname(result.getString("col" + Profile.ATTR_SURNAME));
                profile.setNickname(result.getString("col" + Profile.ATTR_NICKNAME));
                profile.setCourse(new CourseDAO().getCourse(result.getInt("col" + Profile.ATTR_COURSE)));
                // get unit
                profile.setUnits(new UnitDAO().getUserUnit(profile.getId()));
                profile.setLatitude(result.getDouble("col" + Profile.ATTR_LATITUDE));
                profile.setLongitude(result.getDouble("col" + Profile.ATTR_LONGITUDE));
                profile.setNationality(result.getString("col" + Profile.ATTR_NATIONALITY));
                profile.setNativLang(result.getString("col" + Profile.ATTR_NATIVLANG));
                profile.setSecondLang(result.getString("col" + Profile.ATTR_SECONDLANG));
                profile.setSuburb(result.getString("col" + Profile.ATTR_SUBURB));
                profile.setFavFood(result.getString("col" + Profile.ATTR_FAVFOOD));
                profile.setFavMovie(result.getString("col" + Profile.ATTR_FAVMOVIE));
                profile.setFavProgLang(result.getString("col" + Profile.ATTR_FAVPROGLANG));
                profile.setFavUnit(new UnitDAO().getUnit(result.getInt("col" + Profile.ATTR_FAVUNIT)));
                profile.setPrevJob(result.getString("col" + Profile.ATTR_PREVJOB));
                profile.setCurJob(result.getString("col" + Profile.ATTR_CURJOB));

                MatchResult match = new MatchResult();
                match.setMate(profile);
                calculateSimilarity(userProfile, match);
                matches.add(match);
            }
            Collections.sort(matches, new Comparator<MatchResult>() {
                @Override
                public int compare(MatchResult t, MatchResult t1) {
                    return t.getDegree() - t1.getDegree();
                }
            });
            return matches;
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
    
    private void calculateSimilarity(Profile prof, MatchResult match){
        int degree = 0;
        if(prof.getCourse()!= null && match.getMate().getClass() != null && 
                prof.getCourse().getId() == match.getMate().getCourse().getId())
            degree += 10;
        for(Unit unit : prof.getUnits()){
            for(Unit m : match.getMate().getUnits()){
                if(unit.getId() == m.getId()){
                    degree += 3;
                    break;
                }
            }
        }
        if(prof.getNationality().equalsIgnoreCase(match.getMate().getNationality())){
            degree += 2;
        }
        if(prof.getNativLang().equalsIgnoreCase(match.getMate().getNativLang())){
            degree += 2;
        }
        if(prof.getSecondLang().equalsIgnoreCase(match.getMate().getSecondLang())){
            degree += 2;
        }
        if(prof.getFavFood().equalsIgnoreCase(match.getMate().getFavFood())){
            degree += 2;
        }
        if(prof.getFavMovie().equalsIgnoreCase(match.getMate().getFavMovie())){
            degree += 2;
        }
        if(prof.getFavProgLang().equalsIgnoreCase(match.getMate().getFavProgLang())){
            degree += 2;
        }
        if(prof.getFavUnit() != null && match.getMate().getFavUnit() != null 
                && prof.getFavUnit().getId() == match.getMate().getFavUnit().getId()){
            degree += 2;
        }
        if(prof.getCurJob().equalsIgnoreCase(match.getMate().getCurJob())){
            degree += 2;
        }
        if(prof.getPrevJob().equalsIgnoreCase(match.getMate().getPrevJob())){
            degree += 2;
        }
        if(prof.getSuburb().equalsIgnoreCase(match.getMate().getSuburb())){
            degree += 2;
        }
        if(prof.getFirstname().equalsIgnoreCase(match.getMate().getFirstname())){
            degree += 1;
        }
        if(prof.getSurname().equalsIgnoreCase(match.getMate().getSurname())){
            degree += 1;
        }
        if(prof.getNickname().equalsIgnoreCase(match.getMate().getNickname())){
            degree += 1;
        }
        
        match.setDistance(distFrom(prof.getLatitude(), prof.getLongitude(), 
                match.getMate().getLatitude(), match.getMate().getLongitude()));
        match.setDegree(degree);
    }
    private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return dist * meterConversion;
    }
}

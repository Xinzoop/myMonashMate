/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.DAO;

import edu.monash.mymonashmate.entities.Course;
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
public class CourseDAO {

    private final static String TABLE_COURSE = "COURSE";
    private final static String FIELD_COURID = "ID";
    private final static String FIELD_COURNAME = "NAME";
    private final static String FIELD_COURDETAILS = "DETAILS";

    public Course getCourse(int courseID) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseHandler.getConnection();
            // get course
            String sql = String.format("select * from %s where %s=?", TABLE_COURSE, FIELD_COURID);
            PreparedStatement query = conn.prepareStatement(sql);
            query.setInt(1, courseID);
            ResultSet result = query.executeQuery();
            Course course = null;
            if (result.next()) {
                course = new Course();
                course.setId(result.getInt(FIELD_COURID));
                course.setName(result.getString(FIELD_COURNAME));
            }
            result.close();
            query.close();
            return course;
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
    public List<Course> getAllCourses() throws Exception {
        Connection conn = null;
        try {
            List<Course> allCourses = new ArrayList<Course>();
            conn = DatabaseHandler.getConnection();
            // get course
            String sql = String.format("select * from %s", TABLE_COURSE, FIELD_COURID);
            PreparedStatement query = conn.prepareStatement(sql);
            ResultSet result = query.executeQuery();
            while (result.next()) {
                Course course = new Course();
                course.setId(result.getInt(FIELD_COURID));
                course.setName(result.getString(FIELD_COURNAME));
                allCourses.add(course);
            }
            result.close();
            query.close();
            return allCourses;
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

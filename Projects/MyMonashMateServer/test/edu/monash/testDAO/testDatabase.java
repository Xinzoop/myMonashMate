/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.monash.testDAO;

import com.google.gson.Gson;
import edu.monash.DAO.AccountDAO;
import edu.monash.DAO.CourseDAO;
import edu.monash.DAO.DatabaseHandler;
import edu.monash.DAO.ProfileDAO;
import edu.monash.DAO.UnitDAO;
import edu.monash.mymonashmate.entities.Course;
import edu.monash.mymonashmate.entities.MatchCriteria;
import edu.monash.mymonashmate.entities.MatchCriteriaItem;
import edu.monash.mymonashmate.entities.MatchResult;
import edu.monash.mymonashmate.entities.Privacy;
import edu.monash.mymonashmate.entities.Profile;
import edu.monash.mymonashmate.entities.Unit;
import edu.monash.mymonashmate.entities.User;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zipv5_000
 */
public class testDatabase {
    
    public testDatabase() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    private String getJSON(Object obj){
        return new Gson().toJson(obj);
    }
    
    @Test
    public void testLogin() throws Exception{
        AccountDAO dao = new AccountDAO();
        User user = new User();
        user.setEmail("admin@gmail.com");
        user.setPassword("123");
        user.setPublKeyString("siehsfhskhefkshfjehfkjefjsagfjgajfesagjaguffishihfiasheugheshfkasfhieif");
        assertEquals(dao.login(user), true);
        System.out.println(getJSON(user));
    }
    
    @Test
    public void testSignup() throws Exception{
        AccountDAO dao = new AccountDAO();
        User user = new User();
        user.setEmail("zip@monash.com");
        user.setPassword("123");
        user.setPublKeyString("siehsfhskhefkshfjehfkjefjsagfjgajfesagjaguffishihfiasheugheshfkasfhieif");
        dao.signup(user);
        System.out.println(user.getUserid());
    }
    @Test
    public void testLogout() throws Exception{
        AccountDAO dao = new AccountDAO();
        assertEquals(dao.logout(1008), true);
    }
    @Test
    public void testGetProfilePrivacy() throws Exception{
        ProfileDAO dao = new ProfileDAO();
        Profile profile = dao.getProfile(1004, Boolean.TRUE);
        System.out.println(getJSON(profile));
    }
    @Test
    public void gestGetProfile() throws Exception{
        ProfileDAO dao = new ProfileDAO();
        Profile profile = dao.getProfile(1004, Boolean.FALSE);
        System.out.println(getJSON(profile));
    }
    @Test
    public void updatePrivacy() throws Exception{
        ProfileDAO dao = new ProfileDAO();
        Privacy privacy = new Privacy();
        privacy.setUserid(1007);
        privacy.getPublAttrs().add(Profile.ATTR_NATIONALITY);
        privacy.getPublAttrs().add(Profile.ATTR_NATIVLANG);
        assertEquals(dao.updatePrivacy(privacy), true);
    }
    @Test
    public void getPrivacy() throws Exception{        
        ProfileDAO dao = new ProfileDAO();
        Privacy privacy = dao.getPrivacy(1004);
        System.out.println(getJSON(privacy));
    }
    
    @Test
    public void updateProfile()throws Exception{
        ProfileDAO dao = new ProfileDAO();
        Profile profile = new Profile();
        profile.setId(1007);
        profile.setSurname("Smith");
        profile.setFirstname("Jack");
        profile.setNickname("Dick");
        Course course = new Course();
        course.setId(3348);
        profile.setCourse(course);
        profile.setLatitude(0.0);
        profile.setLongitude(0.0);
        profile.setNationality("Chinese");
        profile.setNativLang("Chinese");
        profile.setSecondLang("English");
        profile.setSuburb("Caulfield");
        profile.setFavFood("rice");
        profile.setFavMovie("007");
        profile.setFavProgLang("c#");
        Unit unit = new Unit();
        unit.setId(4);
        profile.getUnits().add(unit);
        unit = new Unit();
        unit.setId(5);
        profile.getUnits().add(unit);
        unit = new Unit();
        unit.setId(6);
        profile.getUnits().add(unit);
        unit = new Unit();
        unit.setId(7);
        profile.getUnits().add(unit);
        
        unit = new Unit();
        unit.setId(8);
        profile.getUnits().add(unit);
        profile.setFavUnit(unit);
        profile.setCurJob("Student");
        profile.setPrevJob("Software engineer");
        assertEquals(dao.updateProfile(profile), true);
    }
    
    @Test
    public void getUserUnit() throws Exception{
        
        UnitDAO dao = new UnitDAO();
        System.out.println(getJSON(dao.getUserUnit(1014)));
    }
    
    @Test
    public void getAllUnits() throws Exception{
        UnitDAO dao = new UnitDAO();
        for(Unit unit : dao.getAllUnits()){
            System.out.println(unit.getId() + ":" + unit.getName());
        }
    }
    @Test
    public void getAllCourses() throws Exception{
        CourseDAO dao = new CourseDAO();
        for(Course course : dao.getAllCourses()){
            System.out.println(course.getId() + ":" + course.getName());
        }
    }
    
    @Test
    public void findMates() throws Exception{
        ProfileDAO profile =  new ProfileDAO();
        MatchCriteria criterion = new MatchCriteria();
        //criterion.getSourceIDs().add(1017);
        MatchCriteriaItem item = new MatchCriteriaItem();
//        item.setAttrID(7);
//        item.getAttrValueList().add("chinese");
//        criterion.getCriteriaItems().add(item);
//        item = new MatchCriteriaItem();
//        item.setAttrID(4);
//        item.getAttrValueList().add(2402);
//        criterion.getCriteriaItems().add(item);
//        item = new MatchCriteriaItem();
//        item.setAttrID(14);
//        item.getAttrValueList().add("8");
//        criterion.getCriteriaItems().add(item);
        item = new MatchCriteriaItem();
        item.setAttrID(17);
//        units.add(6);
        item.getAttrValueList().add("3");
        item.getAttrValueList().add("5");
        criterion.getCriteriaItems().add(item);
        //System.out.println(profile.findMatesSql(1016, criterion));
        List<MatchResult> results = profile.findMates(1017, criterion);
        System.out.println(getJSON(results));
    }
    @Test
    public void hello() throws Exception{
        Connection conn = DatabaseHandler.getConnection();
        String sql = "select * from account where username ?";
        PreparedStatement query = conn.prepareStatement(sql);
        query.setString(1, "like '%zip%'");
        query.executeQuery();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.testwebservice;

import com.google.gson.Gson;
import edu.monash.DAO.ProfileDAO;
import edu.monash.mymonashmate.entities.Course;
import edu.monash.mymonashmate.entities.MatchCriteria;
import edu.monash.mymonashmate.entities.MatchCriteriaItem;
import edu.monash.mymonashmate.entities.MatchResult;
import edu.monash.mymonashmate.entities.Privacy;
import edu.monash.mymonashmate.entities.Profile;
import edu.monash.mymonashmate.entities.Unit;
import edu.monash.mymonashmate.entities.User;
import edu.monash.services.ApplicationConfig;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author zipv5_000
 */
public class TestRESTful {

    public TestRESTful() throws NoSuchAlgorithmException {
        secureSender = new SecureSender();
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

    private SecureSender secureSender;
    private User client;
    private Profile profile;
    private List<Course> allCourses;
    private List<Unit> allUnits;

    public Object invokeAsyntask(String uri, String method, String accept, String contentType, Object data, Boolean sign) throws Exception {
        // Header
        HashMap<String, String> headers = new HashMap<String, String>();
        // Content
        String content = null;
        if (null != data) {
            if(contentType == MediaType.APPLICATION_JSON)
                content = (new Gson()).toJson(data);
            else
                content = data.toString();
        }
        String timeStamp = secureSender.getUTCTime();
        headers.put(ApplicationConfig.HEADER_KEY, secureSender.getSecrectKeyString());
        headers.put(ApplicationConfig.HEADER_TIMESTAMP, timeStamp);

        if (sign) {
            String userid = String.valueOf(client.getUserid());
            String contentMD5 = null == content ? "" : secureSender.calculateMD5(content);
            String raw = uri + ApplicationConfig.SIGN_DELIMETR + userid + ApplicationConfig.SIGN_DELIMETR + method 
                    + ApplicationConfig.SIGN_DELIMETR + contentMD5 + ApplicationConfig.SIGN_DELIMETR + timeStamp;

            headers.put(ApplicationConfig.HEADER_USER, userid);
            headers.put(ApplicationConfig.HEADER_SIGN, secureSender.sign(raw));
        }

        // encrypt
        if (null != content) {
            content = secureSender.Encrypt(content);
        }
        
        Object result = secureSender.senderRequest(uri, method, accept, contentType, headers, content);
        if(!(result instanceof Exception))
            return secureSender.Decrypt(result.toString());
        if(result instanceof UnsupportedOperationException)
        {
            UnsupportedOperationException e = (UnsupportedOperationException)result;
            return new Exception(secureSender.Decrypt(e.getMessage()));
        }
        return result;
    }
    private String getJSON(Object obj){
        return new Gson().toJson(obj);
    }
    @Test
    public void testConnect() throws Exception{
        // Header
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(ApplicationConfig.HEADER_TIMESTAMP, secureSender.getUTCTime());
        Object result = secureSender.senderRequest(ApplicationConfig.URI_FACADE, ApplicationConfig.METHOD_GET, 
                MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, headers);
        System.out.println(result.toString());
        secureSender.setServerPubKey(result.toString());
    }

    @Test
    public void testLogin() throws Exception {
        
        testConnect();
        
        client = new User();
        client.setEmail("zip@monash.edu");
        client.setPassword(secureSender.calculateMD5("123456"));
        client.setPublKeyString(secureSender.getClientPublKeyString());
        Object result = invokeAsyntask(ApplicationConfig.URI_SIGNIN, ApplicationConfig.METHOD_POST, MediaType.TEXT_PLAIN, 
                MediaType.APPLICATION_JSON, client, false);
        System.out.println(result.toString());
        if(result instanceof Exception)
            return;
        client.setUserid(Integer.valueOf(result.toString()));
    }
    
    @Test
    public void testSignup() throws Exception{
        testConnect();
        
        client = new User();
        client.setEmail("zip@monash.edu");
        client.setPassword(secureSender.calculateMD5("12345678"));
        client.setPublKeyString(secureSender.getClientPublKeyString());
        Object result = invokeAsyntask(ApplicationConfig.URI_SIGNUP, ApplicationConfig.METHOD_POST, 
                MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, client, false);
        System.out.println(result.toString());
    }
    @Test
    public void testGetProfile() throws Exception{
        testLogin();
        
        Object result = invokeAsyntask(ApplicationConfig.URI_PROFILE + "/" + String.valueOf(client.getUserid()), 
                ApplicationConfig.METHOD_GET, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, null, Boolean.TRUE);
        System.out.println(result);
    }
    
    @Test
    public void testPrivacyProfile() throws Exception{
        testLogin();
        
        int targetID= 1007;
        Object result = invokeAsyntask(ApplicationConfig.URI_PROFILE + "/" + String.valueOf(targetID), 
                ApplicationConfig.METHOD_GET, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, null, Boolean.TRUE);
        System.out.println(result);
    }
    @Test
    public void testUpdatePrivacy() throws Exception{
        testLogin();
        
        Privacy privacy = new Privacy();
        privacy.setUserid(1014);
        privacy.getPublAttrs().add(Profile.ATTR_NATIVLANG);
        Object result = invokeAsyntask(ApplicationConfig.URI_PRIVACY, 
                ApplicationConfig.METHOD_POST, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, privacy, Boolean.TRUE);
        Assert.assertEquals(result, "true");
    }
    @Test
    public void testGetPrivacy() throws Exception{
        testLogin();
        
        Object result = invokeAsyntask(ApplicationConfig.URI_PRIVACY, 
                ApplicationConfig.METHOD_GET, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, null, Boolean.TRUE);
        System.out.println(result);
        //System.out.println(getJSON(result));
    }
    
    @Test
    public void updateProfile() throws Exception{
        testLogin();
        
        profile = new Profile();
        profile.setId(client.getUserid());
        profile.setSurname("Zhao");
        profile.setFirstname("Peng");
        profile.setNickname("Petter");
        Course course = new Course();
        course.setId(3348);
        profile.setCourse(course);
        profile.setLatitude(0);
        profile.setLongitude(0);
        Unit unit = new Unit();
        unit.setId(4);
        profile.getUnits().add(unit);
        unit = new Unit();
        unit.setId(5);
        profile.getUnits().add(unit);
        unit = new Unit();
        unit.setId(6);
        profile.getUnits().add(unit);
        
        System.out.println(new Gson().toJson(profile));
        
        Object result = invokeAsyntask(ApplicationConfig.URI_PROFILE, ApplicationConfig.METHOD_POST, 
                MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, profile, Boolean.TRUE);
        System.out.println(result);
    }
    
    @Test
    public void testLogout() throws Exception{
        testLogin();        
        Object result = invokeAsyntask(ApplicationConfig.URI_LOGOUT, ApplicationConfig.METHOD_POST, 
                MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, client.getUserid(), Boolean.TRUE);
        System.out.println(result);
    }
    
    @Test
    public void testFindMates() throws Exception{
        testLogin();
        
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
        Object results = invokeAsyntask(ApplicationConfig.URI_FINDMATES + "/sql", ApplicationConfig.METHOD_POST, 
                MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, criterion, Boolean.TRUE);;
        System.out.println(results.toString());
    }
}

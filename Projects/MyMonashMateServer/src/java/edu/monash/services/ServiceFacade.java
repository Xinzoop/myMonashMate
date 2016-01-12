/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.services;

import edu.monash.DAO.*;
import edu.monash.mymonashmate.entities.*;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author zipv5_000
 */
@Path("Facade")
public class ServiceFacade {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceDescription() {
        return "MyMonashMate APIs (v1.0.0)";
    }

    @POST
    @Path("Signin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public int login(User user) throws Exception {
        return new AccountDAO().login(user);
    }

    @POST
    @Path("Signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public int signup(User user) throws Exception {
        return new AccountDAO().signup(user);
    }

    @POST
    @Path("Logout")
    @Consumes(MediaType.TEXT_PLAIN)
    public void logout(int uselessID, @Context HttpHeaders header) throws Exception {
        // param id is useless, id will be fetched from header
        String userID = header.getHeaderString(ApplicationConfig.HEADER_USER);
        new AccountDAO().logout(Integer.valueOf(userID));
    }

    @GET
    @Path("Profile/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Profile getProfile(@PathParam("id") String id, @Context HttpHeaders header) throws Exception {
        // verify user id
        String userID = header.getHeaderString(ApplicationConfig.HEADER_USER);
        return new ProfileDAO().getProfile(Integer.valueOf(id), !userID.equals(id));
    }

    @POST
    @Path("Profile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean updateProfile(Profile profile, @Context HttpHeaders header) throws Exception {
        // verify userid
        int userID = Integer.valueOf(header.getHeaderString(ApplicationConfig.HEADER_USER));
        if (userID != profile.getId()) {
            throw new Exception("Illegal operation.");
        }
        return new ProfileDAO().updateProfile(profile);
    }
    
    @POST
    @Path("Privacy")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean updatePrivacy(Privacy privacy, @Context HttpHeaders header) throws Exception {
        // verify userid
        int userID = Integer.valueOf(header.getHeaderString(ApplicationConfig.HEADER_USER));
        if (userID != privacy.getUserid()) {
            throw new Exception("Illegal operation.");
        }
        return  new ProfileDAO().updatePrivacy(privacy);
    }
    
    @GET
    @Path("Privacy")
    @Produces(MediaType.APPLICATION_JSON)
    public Privacy getPrivacy(@Context HttpHeaders header) throws Exception{
        int userID = Integer.valueOf(header.getHeaderString(ApplicationConfig.HEADER_USER));
        return new ProfileDAO().getPrivacy(userID);
    }

    @GET
    @Path("Courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getAllCourses() throws Exception {
        return new CourseDAO().getAllCourses();
    }

    @GET
    @Path("Units")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Unit> getAllUnits() throws Exception {
        return new UnitDAO().getAllUnits();
    }

    @POST
    @Path("Findmates")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MatchResult> findMates(MatchCriteria criteria, @Context HttpHeaders header) throws Exception {
        int userID = Integer.valueOf(header.getHeaderString(ApplicationConfig.HEADER_USER));
        return new ProfileDAO().findMates(userID, criteria);
    }
    
    @POST
    @Path("Findmates/sql")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String findMatesSql(MatchCriteria criteria, @Context HttpHeaders header) throws Exception {
        int userID = Integer.valueOf(header.getHeaderString(ApplicationConfig.HEADER_USER));
        return new ProfileDAO().findMatesSql(userID, criteria);
    }
}

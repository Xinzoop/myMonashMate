/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.monash.services;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author zipv5_000
 */
@ApplicationPath("APIs")
public class ApplicationConfig extends Application{
    
    public final static String URL_CODE = "UTF-8";
    public final static String SIGN_DELIMETR = "|";

    public final static String METHOD_GET = "GET";
    public final static String METHOD_POST = "POST";

    public final static String URI_FACADE = "/MyMonashMateServer/APIs/Facade";
    public final static String URI_SIGNIN = "/MyMonashMateServer/APIs/Facade/Signin";
    public final static String URI_SIGNUP = "/MyMonashMateServer/APIs/Facade/Signup";
    public final static String URI_LOGOUT = "/MyMonashMateServer/APIs/Facade/Logout";
    public final static String URI_PROFILE = "/MyMonashMateServer/APIs/Facade/Profile";
    public final static String URI_PRIVACY = "/MyMonashMateServer/APIs/Facade/Privacy";
    public final static String URI_FINDMATES = "/MyMonashMateServer/APIs/Facade/Findmates";    
    public final static String URI_COURSE = "/MyMonashMateServer/APIs/Facade/Courses";
    public final static String URI_UNIT = "/MyMonashMateServer/APIs/Facade/Units";

    public final static String HEADER_KEY = "KEY";
    public final static String HEADER_USER = "USER";
    public final static String HEADER_TIMESTAMP = "TIMESTAMP";
    public final static String HEADER_SIGN = "SIGNATURE";
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(ServiceFacade.class);
        resources.add(MonashExceptionMapper.class);
        return resources;
    }
    
}

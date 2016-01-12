package edu.monash.mymonashmate.client;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;

import edu.monash.mymonashmate.entities.Course;
import edu.monash.mymonashmate.entities.MatchCriteria;
import edu.monash.mymonashmate.entities.MatchResult;
import edu.monash.mymonashmate.entities.Privacy;
import edu.monash.mymonashmate.entities.Profile;
import edu.monash.mymonashmate.entities.Unit;
import edu.monash.mymonashmate.entities.User;
import android.app.Application;
import android.util.Base64;

public class MonashApplication extends Application {

	private SecureSender secureSender;
	
	private User client;
	private Profile profile;
	private Privacy privacy;
	private List<Course> allCourses;
	private List<Unit> allUnits;
	
	private final static String SIGN_DELIMETR = "|";
	
	private final static String MEDIA_TEXT = "text/plain";
	private final static String MEDIA_JSON = "application/json";
	
	private final static String METHOD_GET = "GET";
	private final static String METHOD_POST = "POST";
	
	private final static String URI_FACADE ="/MyMonashMateServer/APIs/Facade";
	private final static String URI_SIGNIN = "/MyMonashMateServer/APIs/Facade/Signin";
	private final static String URI_SIGNUP = "/MyMonashMateServer/APIs/Facade/Signup";
	private final static String URI_LOGOUT = "/MyMonashMateServer/APIs/Facade/Logout";
	private final static String URI_PROFILE = "/MyMonashMateServer/APIs/Facade/Profile";
	private final static String URI_PRIVACY = "/MyMonashMateServer/APIs/Facade/Privacy";
	private final static String URI_FINDMATES = "/MyMonashMateServer/APIs/Facade/Findmates";
	private final static String URI_COURSE = "/MyMonashMateServer/APIs/Facade/Courses";
	private final static String URI_UNIT = "/MyMonashMateServer/APIs/Facade/Units";
	
	private final static String HEADER_KEY = "KEY";
	private final static String HEADER_USER = "USER";
	private final static String HEADER_TIMESTAMP = "TIMESTAMP";
	private final static String HEADER_SIGN = "SIGNATURE";
	
	public void Initialize() throws NoSuchAlgorithmException{
		secureSender = new SecureSender();
	}
	public User getClient(){
		return client;
	}
	public Profile getProfile(){
		return profile;
	}
	public List<Course> getAllCourses(){
		return allCourses;
	}
	public List<Unit> getAllUnits(){
		return allUnits;
	}
	public Privacy getPrivacy(){
		return privacy;
	}
	
	/*
	 * First shakehand
	 */
	public void ConnectServer(final BackgroundWorker.PostExecuteListener postListener){
		BackgroundWorker backWorker = new BackgroundWorker(new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				try {
					
					if(result instanceof String){
						secureSender.setServerPubKey(result.toString());
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					result = e;
				}
				
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
		// Header
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HEADER_TIMESTAMP, secureSender.getUTCTime());
		backWorker.execute(URI_FACADE, METHOD_GET, MEDIA_TEXT, MEDIA_TEXT, headers);
	}
	/*
	 * user login
	 */
	public void signin(String email, String password, final BackgroundWorker.PostExecuteListener postListener){
		
		// wrap client object
		client = new User();
		client.setEmail(email);
		try {
			client.setPassword(secureSender.calculateMD5(password));
		} catch (Exception e) {
			// TODO: handle exception
			if(null != postListener)
				postListener.OnPostExecute(e);
			return;
		}
		
		client.setPublKeyString(secureSender.getClientPublKeyString());
		
		invokeAsyntask(URI_SIGNIN, METHOD_POST, MEDIA_TEXT, MEDIA_JSON, client, false, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(null != result && !(result instanceof Exception)){
					try {
						client.setUserid(Integer.valueOf(result.toString()));
					} catch (Exception e) {
						// TODO: handle exception
						result = new Exception(result.toString());
					}
				}
				
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	/*
	 * user sign up
	 */
	public void signup(String email, String password, final BackgroundWorker.PostExecuteListener postListener){
		
		// wrap client object
		client = new User();
		client.setEmail(email);
		try {
			client.setPassword(secureSender.calculateMD5(password));
		} catch (Exception e) {
			// TODO: handle exception
			if(null != postListener)
				postListener.OnPostExecute(e);
			return;
		}
		client.setPublKeyString(secureSender.getClientPublKeyString());
		
		invokeAsyntask(URI_SIGNUP, METHOD_POST, MEDIA_TEXT, MEDIA_JSON, client, false, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(null != result && !(result instanceof Exception)){
					try {
						client.setUserid(Integer.valueOf(result.toString()));
					} catch (Exception e) {
						// TODO: handle exception
						result = new Exception(result.toString());
					}
				}
				
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void logout(final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_LOGOUT, METHOD_POST, MEDIA_TEXT, MEDIA_TEXT, client.getUserid(), true, 
				new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				client = null;
				profile = null;
				privacy = null;
				allCourses.clear();
				allUnits.clear();
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void getCurProfile(final BackgroundWorker.PostExecuteListener postListener){
		getProfile(client.getUserid(), new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(result instanceof Profile){
					profile = (Profile)result;
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void getProfile(int userid, final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_PROFILE + "/" + String.valueOf(userid), METHOD_GET, MEDIA_JSON, MEDIA_TEXT, null, true, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(null != result && result instanceof String){
					try {
						
						result = new Gson().fromJson(result.toString(), Profile.class);
						
					} catch (Exception e) {
						// TODO: handle exception
						result = new Exception(result.toString());
					}
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void updateProfile(final Profile profile, final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_PROFILE, METHOD_POST, MEDIA_TEXT, MEDIA_JSON, profile, true, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(result != null && !(result instanceof Exception)){
					MonashApplication.this.profile = profile;
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void getPrivacy(final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_PRIVACY, METHOD_GET, MEDIA_JSON, MEDIA_TEXT, null, true, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(null != result && result instanceof String){
					try {
						
						privacy = new Gson().fromJson(result.toString(), Privacy.class);
						
					} catch (Exception e) {
						// TODO: handle exception
						result = new Exception(result.toString());
					}
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void updatePrivacy(final Privacy privacy, final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_PRIVACY, METHOD_POST, MEDIA_TEXT, MEDIA_JSON, privacy, true, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(result != null && !(result instanceof Exception)){
					MonashApplication.this.privacy = privacy;
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void getAllCourses(final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_COURSE, METHOD_GET, MEDIA_JSON, MEDIA_TEXT, null, true, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(result != null && !(result instanceof Exception)){
					try {
						Type listType = new TypeToken<ArrayList<Course>>(){}.getType();
						allCourses = new Gson().fromJson(result.toString(), listType);
						
					} catch (Exception e) {
						// TODO: handle exception
						allCourses = new ArrayList<Course>();
						result = e;
					}
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void getAllUnits(final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_UNIT, METHOD_GET, MEDIA_JSON, MEDIA_TEXT, null, true, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(result != null && !(result instanceof Exception)){
					try {
						Type listType = new TypeToken<ArrayList<Unit>>(){}.getType();
						allUnits = new Gson().fromJson(result.toString(), listType);
						
					} catch (Exception e) {
						// TODO: handle exception
						allUnits = new ArrayList<Unit>();
						result = e;
					}
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void findMates(MatchCriteria criterion, final BackgroundWorker.PostExecuteListener postListener){
		invokeAsyntask(URI_FINDMATES, METHOD_POST, MEDIA_JSON, MEDIA_JSON, criterion, true, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(null != result && result instanceof String){
					try {
						Type listType = new TypeToken<ArrayList<MatchResult>>(){}.getType();
						result = new Gson().fromJson(result.toString(), listType);
						
					} catch (Exception e) {
						// TODO: handle exception
						result = new Exception(result.toString());
					}
				}
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});
	}
	
	public void invokeAsyntask(String uri, String method, String accept, String contentType, Object data, Boolean sign, 
			final BackgroundWorker.PostExecuteListener postListener){
		BackgroundWorker backWorker = new BackgroundWorker(new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				try {
					if(!(result.toString().isEmpty()) && !(result instanceof Exception))
			            result = secureSender.Decrypt(result.toString());
					else if(result instanceof UnsupportedOperationException)
			        {
			            UnsupportedOperationException e = (UnsupportedOperationException)result;
			            result = new Exception(secureSender.Decrypt(e.getMessage()));
			        }					
				} catch (Exception e) {
					// TODO: handle exception
					result = e;
				}
				
				if(null != postListener)
					postListener.OnPostExecute(result);
			}
		});

		// Header
		HashMap<String, String> headers = new HashMap<String, String>();
		// Content
		String content = null;
		
		try {
			if (null != data) {
	            if(contentType == MEDIA_JSON)
	                content = (new Gson()).toJson(data);
	            else
	                content = data.toString();
	        }
	        String timeStamp = secureSender.getUTCTime();
	        headers.put(HEADER_KEY, secureSender.getSecrectKeyString());
	        headers.put(HEADER_TIMESTAMP, timeStamp);

	        if (sign) {
	            String userid = String.valueOf(client.getUserid());
	            String contentMD5 = null == content ? "" : secureSender.calculateMD5(content);
	            String raw = uri + SIGN_DELIMETR + userid + SIGN_DELIMETR + method + SIGN_DELIMETR + contentMD5 + SIGN_DELIMETR + timeStamp;

	            headers.put(HEADER_USER, userid);
	            headers.put(HEADER_SIGN, secureSender.sign(raw));
	        }

	        // encrypt
	        if (null != content) {
	            content = secureSender.Encrypt(content);
	        }
			
		} catch (Exception e) {
			// // TODO: handle exception
			if (null != postListener)
				postListener.OnPostExecute(e);
			return;
		}

		backWorker.execute(uri, method, accept, contentType, headers, content);
	}
}

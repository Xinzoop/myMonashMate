package edu.monash.mymonashmate.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;

import android.os.AsyncTask;

public class BackgroundWorker extends AsyncTask<Object, String, Object> {

	private static final String URL_CODE = "UTF-8";
	
	private String baseURL = "http://10.0.2.2:8080";
	
	private PostExecuteListener postExecuteListener;
	
	public BackgroundWorker(PostExecuteListener postListener){
		
		postExecuteListener = postListener;
	}
	
	
    /* Parameters
    0: URI
    1: method
    2: Accept
    3: Content-Type
    4: Header
    5: Content
   
    */
	@Override
	protected Object doInBackground(Object... params) {
		try {
            String uri = params[0].toString();
            String method = params[1].toString();
            URL url = new URL(baseURL + uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            // Accept
            if (params.length > 2) {
                conn.setRequestProperty("Accept", params[2].toString());
            }
            // Content-Type
            if (params.length > 3) {
                conn.setRequestProperty("Content-Type", params[3].toString());
            }
            // Header
            if (params.length > 4 && null != params[4] && params[4] instanceof HashMap<?, ?>) {
                HashMap<String, String> header = (HashMap<String, String>) params[4];
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), URLEncoder.encode(entry.getValue(), URL_CODE));
                }
            }
            // Content
            if (params.length > 5 && null != params[5]) {
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(params[5].toString().getBytes());
                out.flush();
            }
            // use HTTP_ACCEPTED to indicate RESTful exception
            if (conn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                throw new UnsupportedOperationException(getResponseContent(conn));
            }
            // allow return void.
            if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT){
                return "";
            }
            // handle other exceptions
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception(conn.getResponseMessage() + "(" + conn.getResponseCode() + ")");
            }

            // Acquire response content
            return getResponseContent(conn);
        } catch (Exception e) {
            return e;
        }
	}
	
	private String getResponseContent(HttpURLConnection conn) throws Exception{
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line=reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	@Override
	protected void onPostExecute(Object result) {
		if(null != postExecuteListener){
			postExecuteListener.OnPostExecute(result);
		}
	}

	public interface PostExecuteListener{
		public void OnPostExecute(Object result);
	}
}

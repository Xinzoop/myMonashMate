package edu.monash.mymonashmate.activities;

import edu.monash.mymonashmate.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import edu.monash.mymonashmate.client.*;

public class Splash extends Activity implements BackgroundWorker.PostExecuteListener{
	
	private TextView errText; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash);
		errText = (TextView)findViewById(R.id.splashErrorMsg);
		
		MonashApplication app = (MonashApplication)getApplication();
		
		try {
			
			app.Initialize();
			
		} catch (Exception e) {
			errText.setText("Initialization failed: " + e.getMessage());
			return;
		}
		
		app.ConnectServer(this);
	}

	@Override
	public void OnPostExecute(Object result) {
		
		if(result instanceof Exception){
			Exception e = (Exception)result;
			errText.setText(e.getClass().toString() + " : " + e.getMessage());
			return;
		}
		
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}

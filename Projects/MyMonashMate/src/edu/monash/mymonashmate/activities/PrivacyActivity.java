package edu.monash.mymonashmate.activities;

import java.util.TooManyListenersException;

import com.google.gson.Gson;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.R.id;
import edu.monash.mymonashmate.R.layout;
import edu.monash.mymonashmate.R.menu;
import edu.monash.mymonashmate.client.BackgroundWorker;
import edu.monash.mymonashmate.client.MonashApplication;
import edu.monash.mymonashmate.entities.Privacy;
import edu.monash.mymonashmate.entities.Profile;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.os.Build;

public class PrivacyActivity extends Activity{

	private MonashApplication app;
	private MenuItem menuItem;
	private LinearLayout layoutParent;
	private Switch switchNationality;
	private Switch switchNativeLang;
	private Switch switchSecondLang;
	private Switch switchSuburb;
	private Switch switchFavFood;
	private Switch switchFavMovie;
	private Switch switchFavProgLang;
	private Switch switchFavUnit;
	private Switch switchCurJob;
	private Switch switchPreJob;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy);
		
		app = (MonashApplication)getApplication();
		layoutParent = (LinearLayout)findViewById(R.id.container);
		switchNationality = (Switch)findViewById(R.id.swNationality);
		switchNativeLang = (Switch)findViewById(R.id.swNativeLang);
		switchSecondLang = (Switch)findViewById(R.id.swSecondLang);
		switchSuburb = (Switch)findViewById(R.id.swSuburb);
		switchFavFood = (Switch)findViewById(R.id.swFavFood);
		switchFavMovie = (Switch)findViewById(R.id.swFavMovie);
		switchFavProgLang = (Switch)findViewById(R.id.swFavProgLang);
		switchFavUnit = (Switch)findViewById(R.id.swFavUnit);
		switchCurJob = (Switch)findViewById(R.id.swCurJob);
		switchPreJob = (Switch)findViewById(R.id.swPreJob);
		
		switchNationality.setTag(Profile.ATTR_NATIONALITY);
		switchNativeLang.setTag(Profile.ATTR_NATIVLANG);
		switchSecondLang.setTag(Profile.ATTR_SECONDLANG);
		switchSuburb.setTag(Profile.ATTR_SUBURB);
		switchFavFood.setTag(Profile.ATTR_FAVFOOD);
		switchFavMovie.setTag(Profile.ATTR_FAVMOVIE);
		switchFavProgLang.setTag(Profile.ATTR_FAVPROGLANG);
		switchFavUnit.setTag(Profile.ATTR_FAVUNIT);
		switchCurJob.setTag(Profile.ATTR_CURJOB);
		switchPreJob.setTag(Profile.ATTR_PREVJOB);
		
		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.privacy, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_privacy_update) {
			menuItem = item;
			updatePrivacy();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initialize(){
		if(app.getPrivacy() != null){
			for(int i=0; i<layoutParent.getChildCount(); ++i){
				Switch item = (Switch)layoutParent.getChildAt(i);
				int tag = Integer.valueOf(item.getTag().toString());
				if(app.getPrivacy().getPublAttrs().contains(tag)){
					item.setChecked(true);
				}
			}
		}
	}
	
	private Privacy collectInput(){
		Privacy privacy = new Privacy();
		privacy.setUserid(app.getClient().getUserid());
		for(int i=0; i<layoutParent.getChildCount(); ++i){
			Switch item = (Switch)layoutParent.getChildAt(i);
			if(item.isChecked()){
				privacy.getPublAttrs().add(Integer.valueOf(item.getTag().toString()));
			}
		}
		return privacy;
	}
	
	private void updatePrivacy(){
		toggleProgress();
		app.updatePrivacy(collectInput(), new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(result instanceof Exception){
					Exception e = (Exception)result;
					Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				toggleProgress();
			}
		});
	}
	private void toggleProgress(){
		if(menuItem.getActionView() == null){
			menuItem.setActionView(R.layout.actionbar_progressbar);
			menuItem.expandActionView();
		}
		else {
			menuItem.setActionView(null);
			menuItem.collapseActionView();
		}
	}
}

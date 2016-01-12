package edu.monash.mymonashmate.activities;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.R.id;
import edu.monash.mymonashmate.R.layout;
import edu.monash.mymonashmate.R.menu;
import edu.monash.mymonashmate.activities.ProfileFragment.PostCreateViewListener;
import edu.monash.mymonashmate.client.BackgroundWorker;
import edu.monash.mymonashmate.client.MonashApplication;
import edu.monash.mymonashmate.entities.Profile;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class FirstSignupActivity extends Activity {

	private ProfileFragment profileFrag;
	private MenuItem menuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_signup);

		if (savedInstanceState == null) {
			profileFrag = new ProfileFragment();
			profileFrag.setPostCreateViewListener(new PostCreateViewListener() {
				
				@Override
				public void OnCreateViewCompleted() {
					// TODO Auto-generated method stub
					profileFrag.toggleEditStatus();
				}
			});
			getFragmentManager().beginTransaction().add(R.id.container, profileFrag).commit();
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first_signup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_next) {
			menuItem = item;
			beginUpdateProfile();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void beginUpdateProfile(){
		
		Profile profile = profileFrag.collectInput();
		if(null == profile)
			return;
		
		menuItem.setActionView(R.layout.actionbar_progressbar);
		menuItem.expandActionView();
		((MonashApplication)getApplication()).updateProfile(profile, new BackgroundWorker.PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				menuItem.setActionView(null);
				menuItem.collapseActionView();
				if(result instanceof Exception){
					Exception e = (Exception)result;
					Toast.makeText(FirstSignupActivity.this, "Updating failed." + e.getClass().toString() + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
					return;
				}
				
				// Jump to main page
				Intent intent = new Intent(FirstSignupActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}

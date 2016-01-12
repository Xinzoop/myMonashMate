package edu.monash.mymonashmate.activities;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.R.id;
import edu.monash.mymonashmate.R.layout;
import edu.monash.mymonashmate.R.menu;
import edu.monash.mymonashmate.activities.ProfileFragment.PostCreateViewListener;
import edu.monash.mymonashmate.client.BackgroundWorker.PostExecuteListener;
import edu.monash.mymonashmate.client.MonashApplication;
import edu.monash.mymonashmate.entities.Profile;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class ProfileActivity extends Activity {

	private Boolean flag = true;
	private MenuItem menuItem;
	private ProfileFragment fragment;
	private MonashApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		app = (MonashApplication)getApplication();
		
		if (savedInstanceState == null) {
			fragment = new ProfileFragment();
			fragment.setPostCreateViewListener(new PostCreateViewListener() {
				
				@Override
				public void OnCreateViewCompleted() {
					// TODO Auto-generated method stub
					fragment.updateInterface(app.getProfile());
				}
			});
			getFragmentManager().beginTransaction()
					.add(R.id.container, fragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_edit) {
			menuItem = item;
			if(flag){
				beginEdit();
			}
			else{
				endEdit();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void beginEdit(){
		fragment.toggleEditStatus();
		menuItem.setTitle(getString(R.string.txtDone));
		menuItem.setIcon(getResources().getDrawable(R.drawable.ic_action_save));
		flag = false;
	}
	private void endEdit(){
		Profile profile = fragment.collectInput();
		if(null == profile)
			return;
		
		menuItem.setActionView(R.layout.actionbar_progressbar);
		menuItem.expandActionView();
		app.updateProfile(profile, new PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				menuItem.setActionView(null);
				menuItem.collapseActionView();
				if(result instanceof Exception){
					Exception e = (Exception)result;
					Toast.makeText(ProfileActivity.this, e.getClass().toString() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
					return;
				}
				
				menuItem.setTitle(getString(R.string.txtEdit));
				menuItem.setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
				flag = true;
			}
		});
	}
}

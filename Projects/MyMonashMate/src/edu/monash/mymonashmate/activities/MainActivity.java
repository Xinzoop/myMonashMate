package edu.monash.mymonashmate.activities;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.R.id;
import edu.monash.mymonashmate.R.layout;
import edu.monash.mymonashmate.R.menu;
import edu.monash.mymonashmate.client.BackgroundWorker;
import edu.monash.mymonashmate.client.MonashApplication;
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
import android.widget.Button;
import android.os.Build;

public class MainActivity extends Activity {

	private MonashApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
		app = (MonashApplication)getApplication();
		if(app.getProfile() == null)
		{
			app.getCurProfile(new BackgroundWorker.PostExecuteListener() {
				
				@Override
				public void OnPostExecute(Object result) {
					// TODO Auto-generated method stub
					if(app.getProfile() != null){
						actionBar.setSubtitle(app.getProfile().getNickname());
					}
					else{
						// Jump to first sign up activity
						Intent intent = new Intent(MainActivity.this, FirstSignupActivity.class);
						startActivity(intent);
						finish();
					}
				}
			});

		}
		else{
			actionBar.setSubtitle(app.getProfile().getNickname());
		}
		app.getAllCourses(null);
		app.getAllUnits(null);
		app.getPrivacy(null);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_logout) {
			app.logout(new BackgroundWorker.PostExecuteListener() {
				
				@Override
				public void OnPostExecute(Object result) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			((Button)rootView.findViewById(R.id.btnProfile)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), ProfileActivity.class);
					startActivity(intent);
				}
			});
			((Button)rootView.findViewById(R.id.btnFindmates)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), FindMatesActivity.class);
					startActivity(intent);
				}
			});
			((Button)rootView.findViewById(R.id.btnPrivacy)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), PrivacyActivity.class);
					startActivity(intent);
				}
			});
			
			return rootView;
		}
	}

}

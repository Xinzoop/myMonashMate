package edu.monash.mymonashmate.activities;

import com.google.gson.Gson;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.R.id;
import edu.monash.mymonashmate.R.layout;
import edu.monash.mymonashmate.R.menu;
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
import android.widget.Toast;
import android.os.Build;

public class DetailActivity extends Activity implements ProfileFragment.PostCreateViewListener{

	private ProfileFragment frag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		if (savedInstanceState == null) {
			frag = new ProfileFragment();
			frag.setPostCreateViewListener(this);
			getFragmentManager().beginTransaction().add(R.id.container, frag).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == android.R.id.home){
			finish();
			return true;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void OnCreateViewCompleted() {
		// TODO Auto-generated method stub
		Profile profile = (Profile) getIntent().getSerializableExtra("profile");
		frag.updateInterface(profile);
	}
}

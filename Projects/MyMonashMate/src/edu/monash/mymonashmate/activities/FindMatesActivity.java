package edu.monash.mymonashmate.activities;

import java.util.ArrayList;
import java.util.List;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.client.BackgroundWorker.PostExecuteListener;
import edu.monash.mymonashmate.client.MonashApplication;
import edu.monash.mymonashmate.entities.MatchCriteria;
import edu.monash.mymonashmate.entities.MatchResult;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FindMatesActivity extends Activity {

	private Boolean fragFlag = true;
	private MatchCriterionFragment matchFragment;
	private MapFragment mapFragment;
	private FragmentManager manager;
	private MenuItem menuItem;
	
	private List<MatchResult> curResults = new ArrayList<MatchResult>();
	public List<MatchResult> getCurResults(){
		return curResults;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_mates);
		manager = getFragmentManager();
		
		if (savedInstanceState == null) {
			matchFragment = new MatchCriterionFragment();
			mapFragment = new MapFragment();
			manager.beginTransaction().add(R.id.container, matchFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_mates, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_search) {
			menuItem = item;
			if(fragFlag)
				executeSearch();
			else
				editMatchCriteria();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void editMatchCriteria(){
		fragFlag = true;
		manager.beginTransaction().replace(R.id.container, matchFragment).commit();
	}
	private void executeSearch(){
		final MatchCriteria criterion = matchFragment.getMatchCriteria();
		if(null == criterion)
			return;
		
		toggleProgress();
		fragFlag = false;
		manager.beginTransaction().replace(R.id.container, mapFragment).addToBackStack(null).commit();
		((MonashApplication)getApplication()).findMates(criterion, new PostExecuteListener() {
			
			@Override
			public void OnPostExecute(Object result) {
				// TODO Auto-generated method stub
				toggleProgress();
				if(result instanceof Exception){
					Exception e = (Exception)result;
					Toast.makeText(FindMatesActivity.this, e.getClass().toString() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
					return;
				}
				List<MatchResult> matches = (List<MatchResult>)result;
				if(matches.size() <= 0){
					Toast.makeText(FindMatesActivity.this, "No matched people found.", Toast.LENGTH_SHORT).show();
					return;
				}
				curResults = matches;
				mapFragment.OnPostExecute(result);
			}
		});
	}
	private void toggleProgress(){
		if(menuItem.getActionView() != null){
			menuItem.setActionView(null);
			menuItem.collapseActionView();
		}
		else{
			menuItem.setActionView(R.layout.actionbar_progressbar);
			menuItem.expandActionView();
		}
	}
}
package edu.monash.mymonashmate.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.client.MonashApplication;
import edu.monash.mymonashmate.entities.Course;
import edu.monash.mymonashmate.entities.MatchCriteria;
import edu.monash.mymonashmate.entities.MatchCriteriaItem;
import edu.monash.mymonashmate.entities.MatchResult;
import edu.monash.mymonashmate.entities.Profile;
import edu.monash.mymonashmate.entities.Unit;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class MatchCriterionFragment extends Fragment {

	private LinearLayout matchContainer;
	private CheckBox ckbSecondSearch;
	private HashMap<String, Integer> matchCates;
	private MonashApplication app;
	private FindMatesActivity act;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		matchCates = new HashMap<String, Integer>();
		matchCates.put("", Profile.ATTR_ID);
		matchCates.put(getString(R.string.course), Profile.ATTR_COURSE);
		matchCates.put(getString(R.string.units), Profile.ATTR_UNIT);
		matchCates.put(getString(R.string.nationality), Profile.ATTR_NATIONALITY);
		matchCates.put(getString(R.string.nativelang), Profile.ATTR_NATIVLANG);
		matchCates.put(getString(R.string.secondLang), Profile.ATTR_SECONDLANG);
		matchCates.put(getString(R.string.suburb), Profile.ATTR_SUBURB);
		matchCates.put(getString(R.string.favFood), Profile.ATTR_FAVFOOD);
		matchCates.put(getString(R.string.favMovie), Profile.ATTR_FAVMOVIE);
		matchCates.put(getString(R.string.favProgLang), Profile.ATTR_FAVPROGLANG);
		matchCates.put(getString(R.string.favUnit), Profile.ATTR_FAVUNIT);
		matchCates.put(getString(R.string.nickname), Profile.ATTR_NICKNAME);
		matchCates.put(getString(R.string.firstName), Profile.ATTR_FIRSTNAME);
		matchCates.put(getString(R.string.surname), Profile.ATTR_SURNAME);
		act = (FindMatesActivity)getActivity();
		app = (MonashApplication)act.getApplication();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_match, container, false);
		matchContainer = (LinearLayout)rootView.findViewById(R.id.match_container);
		ckbSecondSearch = (CheckBox)rootView.findViewById(R.id.ckbSecondLevel);
		if(act.getCurResults().size() > 0){
			ckbSecondSearch.setVisibility(View.VISIBLE);
			ckbSecondSearch.setChecked(true);
		}
		addNewCriteria();
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.match, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.menu_add){
			addNewCriteria();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private int getMatchCatID(Spinner spin){
		Object value = matchCates.get(spin.getSelectedItem().toString());
		if(value == null)
			return 0;
		return Integer.valueOf(value.toString());
	}
	
	private View addMatchValue(int cate, final TableLayout valueContainer){
		if(cate <= 0)
			return null;
		
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View valueView;
		ViewGroup rowView;
		if(cate == Profile.ATTR_COURSE || cate == Profile.ATTR_FAVUNIT || cate == Profile.ATTR_UNIT){
			rowView = (ViewGroup)inflater.inflate(R.layout.match_value_spinner, null);
			valueContainer.addView(rowView);
			List<String> lstValue = new ArrayList<String>();
			lstValue.add("");
			if(cate == Profile.ATTR_COURSE){
				for(Course course : app.getAllCourses()){
					lstValue.add(course.getName());
				}
			}
			else {
				for(Unit unit : app.getAllUnits()){
					lstValue.add(unit.getName());
				}
			}
			
			final Spinner spin = (Spinner)rowView.findViewById(R.id.spinMatchValue);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_spinner_item, lstValue);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin.setAdapter(adapter);
			spin.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					// TODO Auto-generated method stub
					spin.setTag(pos - 1);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			valueView = spin;
		}
		else{
			rowView = (ViewGroup)inflater.inflate(R.layout.match_value_string, null);
			valueContainer.addView(rowView);
			valueView = (EditText)rowView.findViewById(R.id.editMatchValue);
		}
		((Button)rowView.findViewById(R.id.btnRemoveValue)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				valueContainer.removeView((ViewGroup)view.getParent());
			}
		});
		
		return valueView;
	}
	
	private void setDefaultValues(int cate, TableLayout valueContainer){
		valueContainer.removeAllViews();
		if(cate <= 0)
			return;
		
		Profile profile = app.getProfile();
		if(cate == Profile.ATTR_UNIT){
			for(Unit unit : profile.getUnits()){
				int pos = 0;
				for(Unit item : app.getAllUnits()){
					if(item.getId() == unit.getId()){
						((Spinner)addMatchValue(cate, valueContainer)).setSelection(pos + 1);
						break;
					}
					++pos;
				}
			}
		}
		else if(cate == Profile.ATTR_FAVUNIT){
			Spinner spin = (Spinner)addMatchValue(cate, valueContainer);
			if(null != profile.getFavUnit()){
				int pos = 0;
				for(Unit item : app.getAllUnits()){
					if(item.getId() == profile.getFavUnit().getId()){
						spin.setSelection(pos + 1);;
						break;
					}
					++pos;
				}
			}
		}
		else if (cate == Profile.ATTR_COURSE) {
			Spinner spin = (Spinner)addMatchValue(cate, valueContainer);
			if(null != profile.getCourse()){
				int pos = 0;
				for(Course item : app.getAllCourses()){
					if(item.getId() == profile.getCourse().getId()){
						spin.setSelection(pos + 1);;
						break;
					}
					++pos;
				}
			}
		}
		else if(cate == Profile.ATTR_SURNAME){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getSurname());
		}
		else if(cate == Profile.ATTR_FIRSTNAME){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getFirstname());
		}
		else if(cate == Profile.ATTR_NICKNAME){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getNickname());
		}
		else if(cate == Profile.ATTR_NATIONALITY){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getNationality());
		}
		else if(cate == Profile.ATTR_NATIVLANG){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getNativLang());
		}
		else if(cate == Profile.ATTR_SECONDLANG){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getSecondLang());
		}
		else if(cate == Profile.ATTR_SUBURB){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getSuburb());
		}
		else if(cate == Profile.ATTR_FAVFOOD){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getFavFood());
		}
		else if(cate == Profile.ATTR_FAVMOVIE){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getFavMovie());
		}
		else if(cate == Profile.ATTR_FAVPROGLANG){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getFavProgLang());
		}
		else if(cate == Profile.ATTR_CURJOB){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getCurJob());
		}
		else if(cate == Profile.ATTR_PREVJOB){
			((EditText)addMatchValue(cate, valueContainer)).setText(profile.getPrevJob());
		}
	}

	private ViewGroup addNewCriteria() {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.match_criteria, null);
		matchContainer.addView(view);
		final Spinner spinCate = (Spinner)view.findViewById(R.id.spinCate);
		final TableLayout valueContainer = (TableLayout)view.findViewById(R.id.tblValueContainer);
		
		// remove criteria
		((Button) view.findViewById(R.id.btnRemove))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						matchContainer.removeView((ViewGroup) view.getParent());
					}
				});

		// add match value
		((Button) view.findViewById(R.id.btnAddValue))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						int catId = getMatchCatID(spinCate);
						if(catId > 0){
							addMatchValue(catId, valueContainer);
						}
					}
				});

		// set match values same as me
		((Button) view.findViewById(R.id.btnSameAsMe))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						int catId = getMatchCatID(spinCate);
						if(catId > 0){
							setDefaultValues(catId, valueContainer);
						}
					}
				});
		
		// match category selected listener
		ArrayList<String> cates = new ArrayList<>();
		cates.addAll(matchCates.keySet());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, cates);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinCate.setAdapter(adapter);
		spinCate.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				int cate = getMatchCatID(spinCate);
				if(spinCate.getTag() == null || !spinCate.getTag().toString().equalsIgnoreCase(String.valueOf(cate))){
					spinCate.setTag(cate);
					valueContainer.removeAllViews();
					if(cate > 0)
						setDefaultValues(cate, valueContainer);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		return view;
	}

	public MatchCriteria getMatchCriteria(){
		MatchCriteria criterion = new MatchCriteria();
		for(int i=0; i<matchContainer.getChildCount(); ++i){
			ViewGroup critView = (ViewGroup)matchContainer.getChildAt(i);
			int cate = getMatchCatID((Spinner)critView.findViewById(R.id.spinCate));
			if(cate > 0){
				MatchCriteriaItem item = new MatchCriteriaItem();
				item.setAttrID(cate);
				ViewGroup valueContainer = (ViewGroup)critView.findViewById(R.id.tblValueContainer);
				for(int j=0; j<valueContainer.getChildCount(); ++j){
					ViewGroup valueView = (ViewGroup)valueContainer.getChildAt(j);
					if(cate == Profile.ATTR_COURSE || cate == Profile.ATTR_UNIT || cate == Profile.ATTR_FAVUNIT){
						Spinner spin = (Spinner)valueView.findViewById(R.id.spinMatchValue);
						if(spin.getTag() != null){
							int pos = Integer.valueOf(spin.getTag().toString());
							if(cate == Profile.ATTR_COURSE){
								if(pos >= 0 && pos < app.getAllCourses().size()){
									item.getAttrValueList().add(String.valueOf(app.getAllCourses().get(pos).getId()));
								}
							}
							else{
								if(pos >= 0 && pos < app.getAllUnits().size()){
									item.getAttrValueList().add(String.valueOf(app.getAllUnits().get(pos).getId()));
								}
							}
						}
					}
					else{
						item.getAttrValueList().add(((EditText)valueView.findViewById(R.id.editMatchValue)).getText().toString());
					}
				}
				if(item.getAttrValueList().size() > 0)
					criterion.getCriteriaItems().add(item);
			}
		}
		
		if(criterion.getCriteriaItems().size() <= 0){
			Toast.makeText(getActivity(), "No valide match criterion.", Toast.LENGTH_SHORT).show();
			return null;
		}
		
		if(ckbSecondSearch.isChecked()){
			for(MatchResult res : act.getCurResults()){
				criterion.getSourceIDs().add(res.getMate().getId());
			}
		}
		return criterion;
	}
}

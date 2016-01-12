package edu.monash.mymonashmate.activities;

import java.util.ArrayList;
import java.util.List;

import edu.monash.mymonashmate.R;
import edu.monash.mymonashmate.client.*;
import edu.monash.mymonashmate.entities.Course;
import edu.monash.mymonashmate.entities.Profile;
import edu.monash.mymonashmate.entities.Unit;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class ProfileFragment extends Fragment{
	
	public interface PostCreateViewListener{
		public void OnCreateViewCompleted();
	}
	private PostCreateViewListener postCreateView;
	
	public void setPostCreateViewListener(PostCreateViewListener listener){
		postCreateView = listener;
	}
	
	MonashApplication app;
	
	private LinearLayout unitParent;
	private Button btnAddUnit;
	private TableLayout layoutProfile;
	private Boolean statusFlag = false;
	
	private EditText editSurname;
	private EditText editFirstname;
	private EditText editNickname;
	private Spinner spinCourse;
	private EditText editLatitude;
	private EditText editLongitude;
	private EditText editNationality;
	private EditText editNativeLang;
	private EditText editSecondLang;
	private EditText editSuburb;
	private EditText editFavFood;
	private EditText editFavMovie;
	private EditText editFavProgLang;
	private Spinner spinFavUnit;
	private EditText editCurJob;
	private EditText editPrevJob;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		
		app = (MonashApplication)getActivity().getApplication();
		
		layoutProfile = (TableLayout)rootView.findViewById(R.id.layout_profile);
		
		editSurname = (EditText)rootView.findViewById(R.id.editSurname);
		editFirstname = (EditText)rootView.findViewById(R.id.editFirstname);
		editNickname = (EditText)rootView.findViewById(R.id.editNickname);
		spinCourse = (Spinner)rootView.findViewById(R.id.spinCourse);
		editLatitude = (EditText)rootView.findViewById(R.id.editLatitude);
		editLongitude = (EditText)rootView.findViewById(R.id.editLongitude);
		editNationality = (EditText)rootView.findViewById(R.id.editNationality);
		editNativeLang = (EditText)rootView.findViewById(R.id.editNativelang);
		editSecondLang = (EditText)rootView.findViewById(R.id.editSecondLang);
		editSuburb = (EditText)rootView.findViewById(R.id.editSuburb);
		editFavFood = (EditText)rootView.findViewById(R.id.editFavFood);
		editFavMovie = (EditText)rootView.findViewById(R.id.editFavMovie);
		editFavProgLang = (EditText)rootView.findViewById(R.id.editFavProgLang);
		editCurJob = (EditText)rootView.findViewById(R.id.editCurJob);
		editPrevJob = (EditText)rootView.findViewById(R.id.editPrevJob);
		spinFavUnit = (Spinner)rootView.findViewById(R.id.spinFavUnit);
		unitParent = (LinearLayout)rootView.findViewById(R.id.layout_unit_parent);
		btnAddUnit = (Button)rootView.findViewById(R.id.addUnit);
		
		// Course
		List<String> listCourse = new ArrayList<String>();
		listCourse.add("");
		for(Course course : app.getAllCourses()){
			listCourse.add(course.getName());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
			android.R.layout.simple_spinner_item, listCourse);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinCourse.setAdapter(dataAdapter);
		spinCourse.setTag(0);
		spinCourse.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				spinCourse.setTag(pos - 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spinCourse.setEnabled(false);
		
		// Fav-Unit
		List<String> listUnit = new ArrayList<String>();
		listUnit.add("");
		for(Unit unit : app.getAllUnits()){
			listUnit.add(unit.getName());
		}
		ArrayAdapter<String> dataAdapterUnit = new ArrayAdapter<String>(getActivity(),
			android.R.layout.simple_spinner_item, listUnit);
		dataAdapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinFavUnit.setAdapter(dataAdapterUnit);
		spinFavUnit.setTag(0);
		spinFavUnit.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				spinFavUnit.setTag(pos - 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spinFavUnit.setEnabled(false);

		// Add Unit button
		btnAddUnit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addNewUnitView().setEnabled(true);
			}
		});
		
		if(postCreateView != null)
			postCreateView.OnCreateViewCompleted();
		
		return rootView;
	}

	private Spinner addNewUnitView(){
		final Spinner spin = new Spinner(getActivity());
		spin.setTag(0);
		List<String> listUnit = new ArrayList<String>();
		listUnit.add("");
		for(Unit item : app.getAllUnits()){
			listUnit.add(item.getName());
		}
		final String removeString ="<<" + getString(R.string.removeUnit) + ">>"; 
		listUnit.add(removeString);
		ArrayAdapter<String> dataAdapterUnit = new ArrayAdapter<String>(getActivity(),
			android.R.layout.simple_spinner_item, listUnit);
		dataAdapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(dataAdapterUnit);
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				if(parent.getItemAtPosition(pos).toString().equalsIgnoreCase(removeString)){
					removeUnit(spin);
					return;
				}
				spin.setTag(pos - 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		unitParent.addView(spin, unitParent.getChildCount());
		spin.setEnabled(false);
		return spin;
	}
	private void removeUnit(Spinner spin){
		unitParent.removeView(spin);
	}
	
	public void updateInterface(Profile profile) {
		// update interface
		if(null != profile){
			editSurname.setText(profile.getSurname());
			editFirstname.setText(profile.getFirstname());
			editNickname.setText(profile.getNickname());
			editLatitude.setText(String.valueOf(profile.getLatitude()));
			editLongitude.setText(String.valueOf(profile.getLongitude()));
			// Course
			if(app.getAllCourses().size() > 0 && profile.getCourse() != null){
				int pos = 0;
				for(Course course : app.getAllCourses()){
					if(course.getId() == profile.getCourse().getId()){
						spinCourse.setSelection(pos + 1);
						break;
					}
					++pos;
				}
			}
			
			// Units
			if (profile.getUnits().size() > 0) {
				for (int i = 0; i < profile.getUnits().size(); ++i) {
					Spinner spin = addNewUnitView();
					int pos = 0;
					for (Unit item : app.getAllUnits()) {
						if (profile.getUnits().get(i).getId() == item.getId()) {
							spin.setSelection(pos + 1);
							break;
						}
						++pos;
					}
				}
			}
			btnAddUnit.setEnabled(true);
			
			editNationality.setText(profile.getNationality());
			editNativeLang.setText(profile.getNativLang());
			editSecondLang.setText(profile.getSecondLang());
			editSuburb.setText(profile.getSuburb());
			editFavFood.setText(profile.getFavFood());
			editFavMovie.setText(profile.getFavMovie());
			editFavProgLang.setText(profile.getFavProgLang());
			editPrevJob.setText(profile.getPrevJob());
			editCurJob.setText(profile.getCurJob());
			
			// FavUnit
			if(app.getAllUnits().size() > 0 && profile.getFavUnit() != null){
				int pos = 0;
				for(Unit unit : app.getAllUnits()){
					if(unit.getId() == profile.getFavUnit().getId()){
						spinFavUnit.setSelection(pos + 1);
						break;
					}
					++pos;
				}
			}
		}
	}
	
	public void toggleEditStatus(){
		// Enable interface view
		statusFlag = !statusFlag;
		for(int i=0; i<layoutProfile.getChildCount(); ++i){
			TableRow row = (TableRow)layoutProfile.getChildAt(i);
			for(int j=0; j<row.getChildCount(); ++j){
				View cell = row.getChildAt(j);
				cell.setEnabled(statusFlag);
				if(cell instanceof LinearLayout){
					LinearLayout container = (LinearLayout) cell;
					for(int k=0; k<container.getChildCount(); ++k){
						container.getChildAt(k).setEnabled(statusFlag);
					}
				}
			}
		}
		if(statusFlag)
			btnAddUnit.setVisibility(View.VISIBLE);
		else
			btnAddUnit.setVisibility(View.GONE);
	}
	
	public Profile collectInput(){
	
		// validate input
		String surname = editSurname.getText().toString();
		if(surname.isEmpty()){
			editSurname.setError("Surname is empty.");
			editSurname.requestFocus();
			return null;
		}
		String firstname = editFirstname.getText().toString();
		if(firstname.isEmpty()){
			editFirstname.setError("First name is empty.");
			editFirstname.requestFocus();
			return null;
		}		
		if(spinCourse.getTag() == null || spinCourse.getTag().toString().isEmpty()){
			Toast.makeText(getActivity(), "Please select a course.", Toast.LENGTH_SHORT).show();
			spinCourse.requestFocus();
		}
		// latitude
		Double latitude = 0d;
		try {
			latitude = Double.valueOf(editLatitude.getText().toString());
		} catch (Exception e) {
			// TODO: handle exception
			editLatitude.setError("Incorrect latitude.");
			editLatitude.requestFocus();
			return null;
		}
		
		// longitude
		Double longitude = 0d;
		try {
			longitude = Double.valueOf(editLongitude.getText().toString());
		} catch (Exception e) {
			// TODO: handle exception
			editLongitude.setError("Incorrect longitude.");
			editLongitude.requestFocus();
			return null;
		}
		// disable interface view
		toggleEditStatus();
		
		// update to server
		Profile profile  = new Profile();
		profile.setId(app.getClient().getUserid());
		profile.setSurname(surname);
		profile.setFirstname(firstname);
		profile.setNickname(editNickname.getText().toString());
		
		// course
		if (spinCourse.getTag() != null) {
			int pos = Integer.valueOf(spinCourse.getTag().toString());
			if (pos >= 0 && pos < app.getAllCourses().size())
				profile.setCourse(app.getAllCourses().get(pos));
		}

		// units
		for (int i = 0; i < unitParent.getChildCount(); ++i) {
			View child = unitParent.getChildAt(i);
			if (child instanceof Spinner) {
				int pos = Integer.valueOf(child.getTag().toString());
				if (pos >= 0 && pos < app.getAllCourses().size()) {
					Unit unit = app.getAllUnits().get(pos);
					if (!profile.getUnits().contains(unit))
						profile.getUnits().add(unit);
				}
			}
		}
		profile.setLatitude(latitude);
		profile.setLongitude(longitude);
		profile.setNationality(editNationality.getText().toString());
		profile.setNativLang(editNativeLang.getText().toString());
		profile.setSecondLang(editSecondLang.getText().toString());
		profile.setSuburb(editSuburb.getText().toString());
		profile.setFavFood(editFavFood.getText().toString());
		profile.setFavMovie(editFavMovie.getText().toString());
		profile.setFavProgLang(editFavProgLang.getText().toString());
		// Fav unit
		if (spinFavUnit.getTag() != null) {
			int pos = Integer.valueOf(spinFavUnit.getTag().toString());
			if (pos >= 0 && pos < app.getAllUnits().size())
				profile.setFavUnit(app.getAllUnits().get(pos));
		}
		profile.setCurJob(editCurJob.getText().toString());
		profile.setPrevJob(editPrevJob.getText().toString());
		
		return profile;
	}
}

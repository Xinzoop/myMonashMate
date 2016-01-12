package edu.monash.mymonashmate.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.mapquest.android.maps.AnnotationView;
import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.ItemizedOverlay;
import com.mapquest.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.Overlay.OverlayTouchEventListener;
import com.mapquest.android.maps.OverlayItem;
import com.mapquest.android.maps.Overlay.OverlayTapListener;

import edu.monash.mymonashmate.client.BackgroundWorker;
import edu.monash.mymonashmate.client.MonashApplication;
import edu.monash.mymonashmate.entities.MatchResult;
import edu.monash.mymonashmate.entities.Profile;
import edu.monash.mymonashmate.R;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class MapFragment extends Fragment implements BackgroundWorker.PostExecuteListener{
	
	private MapView map;
	private AnnotationView anno;
	private Profile selProfile;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_find_mates,
				container, false);
		
		map = (MapView)rootView.findViewById(R.id.map);
		map.getController().setZoom(17);
		Profile curProfile = ((MonashApplication)getActivity().getApplication()).getProfile();
		map.getController().setCenter(new GeoPoint(curProfile.getLatitude(), curProfile.getLongitude()));
		map.setBuiltInZoomControls(true);
		anno = new AnnotationView(map);
		anno.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != selProfile){
					Intent intent = new Intent(getActivity(), DetailActivity.class);
					intent.putExtra("profile", selProfile);
					startActivity(intent);
				}
			}
		});
		
		return rootView;
	}

	@Override
	public void OnPostExecute(Object result) {
		// TODO Auto-generated method stub
		final List<MatchResult> matches = (List<MatchResult>) result;
		Drawable icon = getResources().getDrawable(R.drawable.marker);
		Drawable iconRed = getResources().getDrawable(R.drawable.marker_icon_red);
		icon.setBounds(0 - icon.getIntrinsicWidth() / 2, 0 - icon.getIntrinsicHeight(), 
			    icon.getIntrinsicWidth() / 2, 0);
		iconRed.setBounds(0 - icon.getIntrinsicWidth() / 2, 0 - icon.getIntrinsicHeight(), 
			    icon.getIntrinsicWidth() / 2, 0);
		final DefaultItemizedOverlay poiOverlay = new DefaultItemizedOverlay(icon);
		int index = 0;
		for (MatchResult match : matches) {
			Profile profile = match.getMate();
			String title = profile.getNickname();
			String snippet = profile.getFirstname() + " "
					+ profile.getSurname() + "("
					+ Math.round(match.getDistance()) + "m)";
			OverlayItem item = new OverlayItem(new GeoPoint(
					profile.getLatitude(), profile.getLongitude()), title,
					snippet);
			if(index  < matches.size() / 2){
				item.setMarker(iconRed);
			}
			poiOverlay.addItem(item);
			++index;
		}

		poiOverlay.setTapListener(new OverlayTapListener() {

			@Override
			public void onTap(GeoPoint pt, MapView mapView) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "on tap", Toast.LENGTH_SHORT).show();
				
				int lastTouchedIndex = poiOverlay.getLastFocusedIndex();
				if (lastTouchedIndex > -1) {
					OverlayItem tapped = poiOverlay.getItem(lastTouchedIndex);
					anno.showAnnotationView(tapped);
					selProfile = matches.get(lastTouchedIndex).getMate();
				}
				else{
					anno.showAnnotationView(null);
					anno.hide();
					Toast.makeText(getActivity(), "on click", Toast.LENGTH_SHORT).show();
				}
			}
		});
		poiOverlay.setTouchEventListener(new OverlayTouchEventListener() {
			
			@Override
			public void onTouch(MotionEvent arg0, MapView arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "on touch", Toast.LENGTH_SHORT).show();
			}
		});
		
		// map.getOverlays().clear();
		map.getOverlays().add(poiOverlay);
	}
}

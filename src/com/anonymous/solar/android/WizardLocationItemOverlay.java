package com.anonymous.solar.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class WizardLocationItemOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();

	private Context context;
	private WizardLocation locationWizard;

	public WizardLocationItemOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public WizardLocationItemOverlay(Drawable defaultMarker, Context context, WizardLocation locationWizard) {
		this(defaultMarker);
		this.context = context;
		this.locationWizard = locationWizard;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		Log.e("Tap", "Tap Performed");
		return true;
	}

	public void addOverlay(OverlayItem overlay) {
		mapOverlays.add(overlay);
		this.populate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {

		if (event.getAction() == 1) {
			GeoPoint geopoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
			// latitude
			double lat = geopoint.getLatitudeE6() / 1E6;
			// longitude
			double lon = geopoint.getLongitudeE6() / 1E6;
			//Toast.makeText(context, "Lat: " + lat + ", Lon: " + lon, Toast.LENGTH_SHORT).show();

			// Set location in main view.
			locationWizard.setMapLocation(lat, lon);
			
			// Display our little man.
			List<Overlay> mapOverlays = mapView.getOverlays();
			GeoPoint point = new GeoPoint((int)(lat * 1E6), (int)(lon* 1E6));
			OverlayItem overlayitem = new OverlayItem(point, "Location", "Overlay item");
			Drawable drawable = context.getResources().getDrawable(R.drawable.android);
			WizardLocationItemOverlay itemizedoverlay = new WizardLocationItemOverlay(drawable, context, locationWizard);
			// Clear all existing overlays, and add our new overlay.
			mapOverlays.clear();
			mapView.invalidate();
			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);

		}
		return false;
	}
}
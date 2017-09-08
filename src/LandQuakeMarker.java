package UnfoldingMaps;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for land earthquakes on an earthquake map
 *  Date: July 17, 2017
 */
public class LandQuakeMarker extends EarthquakeMarker {
	
	
	public LandQuakeMarker(PointFeature quake) {
		super(quake);
		isOnLand = true;
	}


	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.ellipse(x, y, 2*radius, 2*radius);
	}
	

	public String getCountry() {
		return (String) getProperty("country");
	}


	@Override
	public int compare(EarthquakeMarker arg0, EarthquakeMarker arg1) {
		return 0;
	}	
}
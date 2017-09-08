package UnfoldingMaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes) on a world map.
 *  Date: July 17, 2017
 */
public class AirportMap extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			//routeList.add(sl);
		}						
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved(){
		if(lastSelected != null){
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		selectMarkerIfHover(airportList);
	}
	
	// If there is a marker selected
	private void selectMarkerIfHover(List<Marker> markers){
		
		if(lastSelected != null) return;
		
		for (Marker m : markers) {
			CommonMarker marker = (CommonMarker) m;
			if (marker.isInside(map, mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** 
	 * Display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			checkAirportForClick();
		}
	}

	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : airportList) {
			marker.setHidden(false);
		}
		
		for(Marker marker : routeList){
			marker.setHidden(true);
		}
	}
	
	
	private void checkAirportForClick() {
		if(lastClicked != null) return;
		
		for(Marker m : airportList){
			//AirportMarker marker = (AirportMarker)m;
			if(!m.isHidden() && m.isInside(map, mouseX, mouseY)){
				lastClicked = (CommonMarker)m;
/*				for (Marker sl : AirportMarker.routes) {
					sl.setHidden(false);
					//int source = Integer.parseInt((String)sl.getProperty("source"));
					//int dest = Integer.parseInt((String)sl.getProperty("destination"));
					//notHiddenAirportMarker.add(airportsRoutes.get(source));
					//notHiddenAirportMarker.add(airportsRoutes.get(dest));
				}*/
				
				for(Marker mhide : airportList){
					if(mhide != lastClicked) mhide.setHidden(true);
				}
				return;
			}
		}		
	}

}

package org.hisrc.bahnmap.model;

import java.util.Map;

import org.onebusaway.gtfs.model.Trip;

public class TripsState {

	private final int time;
	
	private final Map<Trip, TripState> tripStates;

	public TripsState(int time, Map<Trip, TripState> tripStates) {
		super();
		this.time = time;
		this.tripStates = tripStates;
	}
	
	
	
}

package org.hisrc.bahnmap.model;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.onebusaway.gtfs.model.Trip;

public class TripsState {

	private final int time;

	private final Map<Trip, TripState> tripStates;

	public TripsState(int time, Map<Trip, TripState> tripStates) {
		Objects.requireNonNull(tripStates, "tripStates must not be null.");
		this.time = time;
		this.tripStates = Collections.unmodifiableMap(tripStates);
	}

	public int getTime() {
		return time;
	}

	public Map<Trip, TripState> getTripStates() {
		return tripStates;
	}

}

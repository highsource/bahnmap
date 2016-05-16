package org.hisrc.bahnmap.model;

import java.util.Objects;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

public class TripState {

	private final int time;
	private final Trip trip;
	private final Stop departureStop;
	private final Stop arrivalStop;
	private final LonLat lonLat;

	public TripState(int time, Trip trip, Stop departureStop, Stop arrivalStop, LonLat lonLat) {
		Objects.requireNonNull(trip, "trip must not be null.");
		Objects.requireNonNull(departureStop, "departureStop must not be null.");
		Objects.requireNonNull(arrivalStop, "arrivalStop must not be null.");
		Objects.requireNonNull(lonLat, "lonLat must not be null.");
		this.time = time;
		this.trip = trip;
		this.departureStop = departureStop;
		this.arrivalStop = arrivalStop;
		this.lonLat = lonLat;
	}

	public int getTime() {
		return time;
	}

	public Trip getTrip() {
		return trip;
	}

	public Stop getDepartureStop() {
		return departureStop;
	}

	public Stop getArrivalStop() {
		return arrivalStop;
	}

	public LonLat getLonLat() {
		return lonLat;
	}

	public String toString() {
		return getTime() + ":" + getTrip().getRoute().getLongName() + "=" + getDepartureStop().getName() + "->"
				+ getArrivalStop().getName() + "@" + getLonLat();
	}

}

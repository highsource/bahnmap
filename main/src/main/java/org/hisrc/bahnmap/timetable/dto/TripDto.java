package org.hisrc.bahnmap.timetable.dto;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.Trip;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TripDto {

	private final int index;
	private final int id;
	private final String headSign;
	private final RouteDto route;

	public TripDto(@JsonProperty("index") int index, @JsonProperty("id") int id,
			@JsonProperty("headSign") String headSign, @JsonProperty("route") RouteDto route
			) {
		Validate.notNull(headSign);
		Validate.notNull(route);
		this.index = index;
		this.id = id;
		this.headSign = headSign;
		this.route = route;
	}

	public int getIndex() {
		return index;
	}

	public int getId() {
		return id;
	}

	public String getHeadSign() {
		return headSign;
	}

	public RouteDto getRoute() {
		return route;
	}

	public static TripDto of(int tripIndex, Trip trip, RouteDto route) {
		return new TripDto(tripIndex, Integer.parseInt(trip.getId().getId()), trip.getTripHeadsign(), route);
	}
}

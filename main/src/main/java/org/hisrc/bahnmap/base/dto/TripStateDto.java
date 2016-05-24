package org.hisrc.bahnmap.base.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({ "tripIndex", "tripId", "departureStopIndex", "arrivalStopIndex", "lon", "lat" })
public class TripStateDto {

	private final int tripIndex;
	private final int tripId;
	private final int departureStopIndex;
	private final int arrivalStopIndex;
	private final double lon;
	private final double lat;

	public TripStateDto(@JsonProperty("tripIndex") int index, @JsonProperty("tripId") int tripId,
			@JsonProperty("departureStopIndex") int departureStopIndex,
			@JsonProperty("arrivalStopIndex") int arrivalStopIndex, @JsonProperty("lon") double lon,
			@JsonProperty("lat") double lat) {
		this.tripIndex = index;
		this.tripId = tripId;
		this.departureStopIndex = departureStopIndex;
		this.arrivalStopIndex = arrivalStopIndex;
		this.lon = lon;
		this.lat = lat;
	}

	public int getTripIndex() {
		return tripIndex;
	}

	public int getTripId() {
		return tripId;
	}

	public int getDepartureStopIndex() {
		return departureStopIndex;
	}

	public int getArrivalStopIndex() {
		return arrivalStopIndex;
	}

	public double getLon() {
		return lon;
	}

	public double getLat() {
		return lat;
	}
}

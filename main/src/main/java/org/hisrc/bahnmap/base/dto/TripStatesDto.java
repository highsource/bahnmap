package org.hisrc.bahnmap.base.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TripStatesDto {

	private final int time;

	private List<TripStateDto> tripStates;

	@JsonCreator
	public TripStatesDto(@JsonProperty("time") int time, @JsonProperty("tripStates") List<TripStateDto> tripStates) {
		this.time = time;
		this.tripStates = tripStates;
	}

	public int getTime() {
		return time;
	}

	public List<TripStateDto> getTripStates() {
		return tripStates;
	}

}

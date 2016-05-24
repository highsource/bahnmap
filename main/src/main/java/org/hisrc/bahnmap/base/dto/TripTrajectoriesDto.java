package org.hisrc.bahnmap.base.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TripTrajectoriesDto {

	private final int time;

	private List<TripTrajectoryDto> tripTrajectories;

	@JsonCreator
	public TripTrajectoriesDto(@JsonProperty("time") int time, @JsonProperty("tripTrajectories") List<TripTrajectoryDto> tripTrajectories) {
		this.time = time;
		this.tripTrajectories = tripTrajectories;
	}

	public int getTime() {
		return time;
	}

	public List<TripTrajectoryDto> getTripTrajectories() {
		return tripTrajectories;
	}

}

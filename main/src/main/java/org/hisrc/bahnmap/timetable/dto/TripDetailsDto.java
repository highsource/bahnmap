package org.hisrc.bahnmap.timetable.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TripDetailsDto extends TripDto {

	private final List<StopTimeDto> stopTimes;

	@JsonCreator
	public TripDetailsDto(@JsonProperty("index") int index, @JsonProperty("id") int id,
			@JsonProperty("headSign") String headSign, @JsonProperty("route") RouteDto route,
			@JsonProperty("stopTimes") List<StopTimeDto> stopTimes) {
		super(index, id, headSign, route);
		this.stopTimes = stopTimes;
	}

	public List<StopTimeDto> getStopTimes() {
		return stopTimes;
	}

	public static TripDetailsDto of(TripDto trip, List<StopTimeDto> stopTimes) {
		return new TripDetailsDto(trip.getIndex(), trip.getId(), trip.getHeadSign(), trip.getRoute(), stopTimes);
	}
}

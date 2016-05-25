package org.hisrc.bahnmap.timetable.dto;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.StopTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StopTimeDto {

	private final int arrivalTime;
	private final int departureTime;
	private final int stopIndex;

	@JsonCreator
	public StopTimeDto(@JsonProperty("arrivalTime") int arrivalTime, @JsonProperty("departureTime") int departureTime,
			@JsonProperty("stopIndex") int stopIndex) {
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.stopIndex = stopIndex;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getDepartureTime() {
		return departureTime;
	}

	public int getStopIndex() {
		return stopIndex;
	}
	
	public static StopTimeDto of(StopTime stopTime, int stopIndex)
	{
		Validate.notNull(stopTime);
		return new StopTimeDto(stopTime.getArrivalTime(), stopTime.getDepartureTime(), stopIndex);
	}

}

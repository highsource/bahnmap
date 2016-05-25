package org.hisrc.bahnmap.timetable.dto;

import java.time.LocalDate;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TripInstanceDto {

	private final TripDto trip;
	private final LocalDate serviceDate;
	private final int startTime;
	private final int endTime;

	@JsonCreator
	public TripInstanceDto(@JsonProperty("trip") TripDto trip, @JsonProperty("serviceDate") LocalDate serviceDate,
			@JsonProperty("startTime") int startTime, @JsonProperty("endTime") int endTime) {
		Validate.notNull(trip);
		Validate.notNull(serviceDate);
		this.trip = trip;
		this.serviceDate = serviceDate;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public LocalDate getServiceDate() {
		return serviceDate;
	}

	public TripDto getTrip() {
		return trip;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public static TripInstanceDto of(TripDto tripDto, LocalDate serviceDate, int startTime, int endTime) {
		return new TripInstanceDto(tripDto, serviceDate, startTime, endTime);
	}
}

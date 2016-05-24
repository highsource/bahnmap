package org.hisrc.bahnmap.base.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({ "tripIndex", "coordinates", "times" })
public class TripTrajectoryDto {

	private final int tripIndex;
	private final BigDecimal[] coordinates;
	private final int[] times;

	public TripTrajectoryDto(@JsonProperty("tripIndex") int tripIndex,
			@JsonProperty("coordinates") BigDecimal[] coordinates, @JsonProperty("times") int[] times) {
		this.tripIndex = tripIndex;
		this.coordinates = coordinates;
		this.times = times;
	}

	public int getTripIndex() {
		return tripIndex;
	}

	public BigDecimal[] getCoordinates() {
		return coordinates;
	}

	public int[] getTimes() {
		return times;
	}
}

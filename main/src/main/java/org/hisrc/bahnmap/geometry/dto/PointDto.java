package org.hisrc.bahnmap.geometry.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PointDto extends GeometryDto<double[]> {

	public static final String TYPE = "Point";

	@JsonCreator
	public PointDto(@JsonProperty("coordinates") double[] coordinates) {
		super(TYPE, coordinates);
	}

	public PointDto(double lon, double lat) {
		this(new double[] { lon, lat });
	}

	public double[] getCoordinates() {
		return super.getCoordinates();
	}

}

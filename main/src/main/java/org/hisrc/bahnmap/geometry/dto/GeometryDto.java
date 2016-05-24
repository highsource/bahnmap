package org.hisrc.bahnmap.geometry.dto;

import org.apache.commons.lang3.Validate;

public abstract class GeometryDto<C> {

	private final String type;
	private final C coordinates;

	public GeometryDto(String type, C coordinates) {
		Validate.notNull(type);
		Validate.notNull(coordinates);
		this.type = type;
		this.coordinates = coordinates;
	}

	public String getType() {
		return type;
	}

	public C getCoordinates() {
		return coordinates;
	}

}

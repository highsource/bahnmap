package org.hisrc.bahnmap.feature.dto;

import org.apache.commons.lang3.Validate;
import org.hisrc.bahnmap.geometry.dto.GeometryDto;

public class FeatureDto<G extends GeometryDto<C>, C, P> {

	public static final String TYPE = "Feature";

	private final String type = TYPE;
	private final G geometry;
	private final P properties;

	public FeatureDto(G geometry, P properties) {
		Validate.notNull(geometry);
		Validate.notNull(properties);
		this.geometry = geometry;
		this.properties = properties;
	}

	public String getType() {
		return type;
	}

	public G getGeometry() {
		return geometry;
	}

	public P getProperties() {
		return properties;
	}

}

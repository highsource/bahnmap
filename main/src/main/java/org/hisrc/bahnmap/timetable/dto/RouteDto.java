package org.hisrc.bahnmap.timetable.dto;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.Route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RouteDto {

	private final int index;
	private final int id;
	private final String name;

	@JsonCreator
	public RouteDto(@JsonProperty("index") int index, @JsonProperty("routeId") int routeId,
			@JsonProperty("name") String name) {
		Validate.notNull(name);
		this.index = index;
		this.id = routeId;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public static RouteDto of(int routeIndex, Route route) {
		return new RouteDto(routeIndex, Integer.parseInt(route.getId().getId()), route.getLongName());
	}

}

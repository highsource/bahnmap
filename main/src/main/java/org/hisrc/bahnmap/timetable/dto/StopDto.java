package org.hisrc.bahnmap.timetable.dto;

import org.apache.commons.lang3.Validate;
import org.hisrc.bahnmap.feature.dto.FeatureDto;
import org.hisrc.bahnmap.geometry.dto.PointDto;
import org.hisrc.bahnmap.timetable.dto.StopDto.Properties;
import org.onebusaway.gtfs.model.Stop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StopDto extends FeatureDto<PointDto, double[], Properties>{

	@JsonCreator
	public StopDto(@JsonProperty("geometry") 
	PointDto geometry,
	@JsonProperty("properties") 
	Properties properties) {
		super(geometry, properties);
	}

	@JsonIgnore
	public int getId() {
		return getProperties().getId();
	}

	public static class Properties {

		private final int index;
		private final int id;
		private final String name;

		public Properties(int index, int id, String name) {
			Validate.notNull(name);
			this.index = index;
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}
	}

	public static StopDto of(int stopIndex, Stop stop) {
		// final String agencyId = stop.getId().getAgencyId();
		final String id = stop.getId().getId();
		final double lon = stop.getLon();
		final double lat = stop.getLat();
		final String name = stop.getName();
		// final String timezone = stop.getTimezone();
		return new StopDto(
				new PointDto(lon, lat),
				new Properties(stopIndex, Integer.parseInt(id), name));
	}

}

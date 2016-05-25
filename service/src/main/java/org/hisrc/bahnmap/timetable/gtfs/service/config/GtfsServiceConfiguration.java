package org.hisrc.bahnmap.timetable.gtfs.service.config;

import java.io.File;
import java.io.IOException;

import org.hisrc.bahnmap.gtfs.serialization.ZipGtfsRelationalDaoDeserializer;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.gtfs.services.calendar.CalendarService;
import org.onebusaway.gtfs.services.calendar.CalendarServiceDataFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class GtfsServiceConfiguration {

	@Bean
	public GtfsRelationalDao gtfsRelationalDao(@Value("${gtfsFile:file:gtfs.zip}") Resource resource)
			throws IOException {
		final File gtfsFile = resource.getFile();
		final ZipGtfsRelationalDaoDeserializer deserializer = new ZipGtfsRelationalDaoDeserializer();
		return deserializer.deserialize(gtfsFile);
	}

	@Bean
	public CalendarService calendarService(GtfsRelationalDao gtfsRelationalDao) {
		return CalendarServiceDataFactoryImpl.createService(gtfsRelationalDao);
	}

}

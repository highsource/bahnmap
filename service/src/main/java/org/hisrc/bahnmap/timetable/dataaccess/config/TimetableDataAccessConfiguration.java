package org.hisrc.bahnmap.timetable.dataaccess.config;

import org.hisrc.bahnmap.timetable.dataaccess.TimetableDataAccess;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimetableDataAccessConfiguration {

	@Bean
	public TimetableDataAccess timetableDataAccess(GtfsRelationalDao gtfsRelationalDao) {
		return new TimetableDataAccess(gtfsRelationalDao);
	}
}

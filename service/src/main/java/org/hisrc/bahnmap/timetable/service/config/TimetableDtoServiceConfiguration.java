package org.hisrc.bahnmap.timetable.service.config;

import org.hisrc.bahnmap.timetable.dataaccess.TimetableDataAccess;
import org.hisrc.bahnmap.timetable.service.TimetableDtoService;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.gtfs.services.calendar.CalendarService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimetableDtoServiceConfiguration {

	@Bean
	public TimetableDtoService timetableDtoService(TimetableDataAccess timetableDataAccess,
			CalendarService calendarService, GtfsRelationalDao gtfsRelationalDao) {
		return new TimetableDtoService(timetableDataAccess, calendarService, gtfsRelationalDao);
	}
}

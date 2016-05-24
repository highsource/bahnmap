package org.hisrc.bahnmap.timetable.service.config;

import org.hisrc.bahnmap.timetable.dataaccess.TimetableDataAccess;
import org.hisrc.bahnmap.timetable.service.TimetableDtoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimetableDtoServiceConfiguration {

	@Bean
	public TimetableDtoService timetableDtoService(TimetableDataAccess timetableDataAccess) {
		return new TimetableDtoService(timetableDataAccess);
	}
}

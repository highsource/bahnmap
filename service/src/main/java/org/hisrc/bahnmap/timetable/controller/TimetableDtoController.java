package org.hisrc.bahnmap.timetable.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hisrc.bahnmap.timetable.dto.RouteDto;
import org.hisrc.bahnmap.timetable.dto.StopDto;
import org.hisrc.bahnmap.timetable.dto.TripDetailsDto;
import org.hisrc.bahnmap.timetable.dto.TripDto;
import org.hisrc.bahnmap.timetable.dto.TripInstanceDto;
import org.hisrc.bahnmap.timetable.service.TimetableDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimetableDtoController {

	@Autowired
	private TimetableDtoService timetableDtoService;

	@RequestMapping(path = "/stops")
	public List<StopDto> getStops() {
		return timetableDtoService.getStops();
	}

	@RequestMapping(path = "/stop/{id}")
	public StopDto getStopById(@PathVariable("id") int id) {
		return timetableDtoService.getStopById(id).orElseThrow(ItemNotFoundException::new);
	}

	@RequestMapping(path = "/routes")
	public List<RouteDto> getRoutes() {
		return timetableDtoService.getRoutes();
	}

	@RequestMapping(path = "/route/{id}")
	public RouteDto getRouteById(@PathVariable("id") int id) {
		return timetableDtoService.getRouteById(id).orElseThrow(ItemNotFoundException::new);
	}

	@RequestMapping(path = "/trips")
	public List<TripDto> getTrips() {
		return timetableDtoService.getTrips();
	}

	@RequestMapping(path = "/trip/{id}")
	public TripDetailsDto getTripDetailsById(@PathVariable("id") int id) {
		return timetableDtoService.getTripDetailsById(id).orElseThrow(ItemNotFoundException::new);
	}

	@RequestMapping(path = "/tripInstances/{year}/{month}/{day}")
	public List<TripInstanceDto> getTripInstancesByDate(@PathVariable("year") int year, @PathVariable("month") int month,
			@PathVariable("day") int day) {
		final LocalDate date = LocalDate.of(year, month, day);
		return timetableDtoService.getTripInstancesByDate(date);
	}
	
	@RequestMapping(path = "/tripInstances/{year}/{month}/{day}/{hour}/{minute}/{second}")
	public List<TripInstanceDto> getTripInstancesByDateTime(
			@PathVariable("year") int year, @PathVariable("month") int month,
			@PathVariable("day") int day,
			@PathVariable("hour") int hour, @PathVariable("minute") int minute,
			@PathVariable("second") int second) {
		final LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
		return timetableDtoService.getTripInstancesByDateTime(dateTime);
	}
	

}

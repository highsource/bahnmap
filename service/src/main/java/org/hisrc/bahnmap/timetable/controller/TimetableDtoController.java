package org.hisrc.bahnmap.timetable.controller;

import java.util.List;

import org.hisrc.bahnmap.timetable.dto.RouteDto;
import org.hisrc.bahnmap.timetable.dto.StopDto;
import org.hisrc.bahnmap.timetable.dto.TripDto;
import org.hisrc.bahnmap.timetable.dto.TripDetailsDto;
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
}

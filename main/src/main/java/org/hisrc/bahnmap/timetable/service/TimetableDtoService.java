package org.hisrc.bahnmap.timetable.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.hisrc.bahnmap.timetable.dataaccess.TimetableDataAccess;
import org.hisrc.bahnmap.timetable.dto.RouteDto;
import org.hisrc.bahnmap.timetable.dto.StopDto;
import org.hisrc.bahnmap.timetable.dto.StopTimeDto;
import org.hisrc.bahnmap.timetable.dto.TripDto;
import org.hisrc.bahnmap.timetable.dto.TripDetailsDto;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.gtfs.services.calendar.CalendarService;

public class TimetableDtoService {

	private final TimetableDataAccess timetableDataAccess;
	private final CalendarService calendarService;
	private final GtfsRelationalDao gtfsRelationalDao;
	private final List<StopDto> stops;
	private final Map<Integer, StopDto> stopsById;
	private final List<RouteDto> routes;
	private final Map<Integer, RouteDto> routesById;
	private final List<TripDto> trips;
	private final Map<Integer, TripDto> tripsById;

	public TimetableDtoService(TimetableDataAccess timetableDataAccess, CalendarService calendarService,
			GtfsRelationalDao gtfsRelationalDao) {
		Validate.notNull(timetableDataAccess);
		Validate.notNull(calendarService);
		Validate.notNull(gtfsRelationalDao);
		this.timetableDataAccess = timetableDataAccess;
		this.calendarService = calendarService;
		this.gtfsRelationalDao = gtfsRelationalDao;
		this.stops = list(timetableDataAccess::getStops, this::convertStop);
		this.stopsById = index(stops, StopDto::getId);
		this.routes = list(timetableDataAccess::getRoutes, this::convertRoute);
		this.routesById = index(routes, RouteDto::getId);
		this.trips = list(timetableDataAccess::getTrips, this::convertTrip);
		this.tripsById = index(trips, TripDto::getId);
	}

	private <I extends IdentityBean<K>, K extends Comparable<? super K> & Serializable, D> List<D> list(
			Supplier<List<I>> itemsSupplier, Function<? super I, ? extends D> mapper) {
		return Collections.unmodifiableList(itemsSupplier.get().stream().map(mapper).collect(Collectors.toList()));
	}

	private <D, K> Map<K, D> index(List<D> items, Function<? super D, ? extends K> keyExtractor) {
		return Collections.unmodifiableMap(items.stream().collect(Collectors.toMap(keyExtractor, i -> i)));
	}

	public List<StopDto> getStops() {
		return stops;
	}

	public Optional<StopDto> getStopById(int id) {
		return Optional.ofNullable(stopsById.get(id));
	}

	private StopDto convertStop(Stop stop) {
		return StopDto.of(timetableDataAccess.getStopIndex(stop), stop);
	}

	public List<RouteDto> getRoutes() {
		return routes;
	}

	public Optional<RouteDto> getRouteById(int id) {
		return Optional.ofNullable(routesById.get(id));
	}

	private RouteDto convertRoute(Route route) {
		return RouteDto.of(timetableDataAccess.getRouteIndex(route), route);
	}

	public List<TripDto> getTrips() {
		return trips;
	}

	public Optional<TripDto> getTripById(int id) {
		return Optional.ofNullable(tripsById.get(id));
	}

	public Optional<TripDetailsDto> getTripDetailsById(int id) {
		return getTripById(id).map(trip -> this.timetableDataAccess.getTripByIndex(trip.getIndex()))
				.map(this::convertTripDetails);
	}

	private TripDetailsDto convertTripDetails(Trip trip) {
		final List<StopTimeDto> stopTimes = gtfsRelationalDao.getStopTimesForTrip(trip).stream().map(this::convertStopTime)
				.collect(Collectors.toList());
		return TripDetailsDto.of(convertTrip(trip), stopTimes);
	}

	private TripDto convertTrip(Trip trip) {
		return TripDto.of(timetableDataAccess.getTripIndex(trip), trip,
				getRouteById(Integer.parseInt(trip.getRoute().getId().getId()))
						.orElseThrow(IllegalArgumentException::new));
	}

	private StopTimeDto convertStopTime(StopTime stopTime) {
		return StopTimeDto.of(stopTime, timetableDataAccess.getStopIndex(stopTime.getStop()));
	}

}

package org.hisrc.bahnmap.timetable.service;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.hisrc.bahnmap.timetable.dataaccess.TimetableDataAccess;
import org.hisrc.bahnmap.timetable.dto.RouteDto;
import org.hisrc.bahnmap.timetable.dto.StopDto;
import org.hisrc.bahnmap.timetable.dto.StopTimeDto;
import org.hisrc.bahnmap.timetable.dto.TripDetailsDto;
import org.hisrc.bahnmap.timetable.dto.TripDto;
import org.hisrc.bahnmap.timetable.dto.TripInstanceDto;
import org.onebusaway.gtfs.model.AgencyAndId;
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
	private final int maximumTripDurationInDays;
	private static final int SECONDS_IN_DAY = 24 * 60 * 60;

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
		this.maximumTripDurationInDays = calculateMaximumTripDurationInDays();
	}

	private int calculateMaximumTripDurationInDays() {
		final Optional<Integer> maximumDepartureTime = gtfsRelationalDao.getAllTrips().stream()
				.map(gtfsRelationalDao::getStopTimesForTrip).map(stopTimes -> stopTimes.get(stopTimes.size() - 1))
				.map(StopTime::getDepartureTime).max(Comparator.naturalOrder());
		return maximumDepartureTime.map(time -> Math.ceil((double) time / SECONDS_IN_DAY)).map(Math::round)
				.map(Long::intValue).orElse(1);
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
		final List<StopTimeDto> stopTimes = gtfsRelationalDao.getStopTimesForTrip(trip).stream()
				.map(this::convertStopTime).collect(Collectors.toList());
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

	public List<TripInstanceDto> getTripInstancesByDateTime(LocalDateTime dateTime) {
		final LocalDate date = dateTime.toLocalDate();
		return getTripInstancesByDate(date).stream().filter(tripInstance -> {
			int timeOffset = (int) Duration.between(tripInstance.getServiceDate().atStartOfDay(), dateTime).getSeconds();
			return tripInstance.getStartTime() <= timeOffset && tripInstance.getEndTime() >= timeOffset;
		}).sorted(Comparator.comparing(TripInstanceDto::getServiceDate)
				.thenComparing(tripInstance -> tripInstance.getTrip().getId())).collect(Collectors.toList());
	}

	public List<TripInstanceDto> getTripInstancesByDate(LocalDate date) {
		final Set<TripInstanceDto> tripInstances = new TreeSet<>(Comparator.comparing(TripInstanceDto::getServiceDate)
				.thenComparing(tripInstance -> tripInstance.getTrip().getId()));
		for (int tripDuration = 0; tripDuration < maximumTripDurationInDays; tripDuration++) {
			final LocalDate currentDate = date.minusDays(tripDuration);
			final ServiceDate currentServiceDate = new ServiceDate(currentDate.getYear(), currentDate.getMonthValue(),
					currentDate.getDayOfMonth());
			final Set<AgencyAndId> serviceIdsOnCurrentServiceDate = calendarService
					.getServiceIdsOnDate(currentServiceDate);
			for (AgencyAndId serviceIdOnCurrentServiceDate : serviceIdsOnCurrentServiceDate) {
				final List<Trip> tripsOnCurrentServiceDate = gtfsRelationalDao
						.getTripsForServiceId(serviceIdOnCurrentServiceDate);
				for (Trip tripOnCurrentServiceDate : tripsOnCurrentServiceDate) {
					final List<StopTime> stopTimes = gtfsRelationalDao.getStopTimesForTrip(tripOnCurrentServiceDate);
					int startTime = stopTimes.get(0).getArrivalTime();
					int endTime = stopTimes.get(stopTimes.size() - 1).getDepartureTime();
					if (startTime <= (tripDuration + 1) * SECONDS_IN_DAY && endTime > tripDuration * SECONDS_IN_DAY) {
						tripInstances.add(TripInstanceDto.of(convertTrip(tripOnCurrentServiceDate), currentDate,
								startTime, endTime));
					}
				}
			}
		}
		return new ArrayList<>(tripInstances);
	}
}

package org.hisrc.bahnmap.gtfs.service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hisrc.bahnmap.model.LonLat;
import org.hisrc.bahnmap.model.LonLatAtTime;
import org.hisrc.bahnmap.model.TripState;
import org.hisrc.bahnmap.model.TripTrajectory;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.gtfs.services.calendar.CalendarService;

public class TimetableService {

	private final GtfsRelationalDao dao;
	private final CalendarService calendarService;
	private final CoordinatesInterpolator interpolator = new CoordinatesInterpolator();

	public TimetableService(GtfsRelationalDao dao) {
		Objects.requireNonNull(dao, "dao must not be null.");
		this.dao = dao;
		this.calendarService = CalendarServiceDataFactoryImpl.createService(dao);
	}

	public Set<AgencyAndId> findServiceIdsOnDate(LocalDate date) {
		return this.calendarService.getServiceIdsOnDate(createServiceDate(date));
	}

	public Set<Trip> findTripsByDate(LocalDate date) {
		final ServiceDate serviceDate = createServiceDate(date);
		final Set<AgencyAndId> serviceIdsOnDate = this.calendarService.getServiceIdsOnDate(serviceDate);
		for (AgencyAndId serviceId : serviceIdsOnDate) {
			final List<Trip> tripForId = this.dao.getTripsForServiceId(serviceId);
			if (tripForId == null || tripForId.isEmpty()) {
				System.out.println(MessageFormat.format("Service with id {0} has no trips.", serviceId));
				this.dao.getTripsForServiceId(serviceId);
			}
		}
		final Set<Trip> trips = serviceIdsOnDate.stream().map(this.dao::getTripsForServiceId)
				.flatMap(Collection::stream).collect(Collectors.toSet());
		return trips;
	}

	public TimetableReference createTimetableReference(LocalDate date) {
		final Set<Trip> trips = findTripsByDate(date);

		final Map<Trip, List<StopTime>> stopTimesOfTrips = trips.stream()
				.collect(Collectors.toMap(trip -> trip, this.dao::getStopTimesForTrip));
		final Set<Stop> stops = stopTimesOfTrips.values().stream().flatMap(Collection::stream).map(StopTime::getStop)
				.collect(Collectors.toSet());
		final Set<Route> routes = trips.stream().map(Trip::getRoute).collect(Collectors.toSet());

		final List<TripTrajectory> tripTrajectories = trips.stream().map(this::createTrajectoryForTrip)
				.collect(Collectors.toList());

		return new TimetableReference(date, stops, routes, trips, stopTimesOfTrips, tripTrajectories);
	}

	private List<TripState> createTripStatesForTrip(Trip trip) {
		final List<StopTime> stopTimesForTrip = this.dao.getStopTimesForTrip(trip);
		int lastDepartureTime = Integer.MIN_VALUE;
		Stop lastStop = null;
		LonLat lastStopLonLat = null;
		final List<TripState> tripStates = new LinkedList<>();
		for (StopTime stopTime : stopTimesForTrip) {
			final Stop previousStop = lastStop;
			final Stop currentStop = stopTime.getStop();
			final int currentArrivalTime = stopTime.getArrivalTime();
			final int currentDepartureTime = stopTime.getDepartureTime();
			final LonLat currentStopLonLat = new LonLat(currentStop.getLon(), currentStop.getLat());
			if (previousStop != null) {
				final int previousDepartureTime = lastDepartureTime;
				final LonLat previousStopLonLat = lastStopLonLat;
				interpolator
						.interpolate(previousStopLonLat, currentStopLonLat, previousDepartureTime * 1000,
								currentArrivalTime * 1000)
						.stream().forEach(timedLonLat -> tripStates.add(new TripState(timedLonLat.getTime(), trip,
								previousStop, currentStop, timedLonLat.getLonLat())));
			}
			interpolator
					.interpolate(currentStopLonLat, currentStopLonLat, currentArrivalTime * 1000,
							currentDepartureTime * 100)
					.stream().forEach(timedLonLat -> tripStates.add(new TripState(timedLonLat.getTime(), trip,
							currentStop, currentStop, timedLonLat.getLonLat())));
			lastDepartureTime = currentDepartureTime;
			lastStop = currentStop;
			lastStopLonLat = currentStopLonLat;
		}
		return tripStates;
	}

	private ServiceDate createServiceDate(LocalDate date) {
		final ServiceDate serviceDate = new ServiceDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
		return serviceDate;
	}
	
	private TripTrajectory createTrajectoryForTrip(Trip trip) {
		final List<StopTime> stopTimesForTrip = this.dao.getStopTimesForTrip(trip);
		final Set<LonLatAtTime> positions = new LinkedHashSet<>(stopTimesForTrip.size()*2);
		for (StopTime stopTime : stopTimesForTrip) {
			final Stop currentStop = stopTime.getStop();
			final int currentArrivalTime = stopTime.getArrivalTime();
			final int currentDepartureTime = stopTime.getDepartureTime();
			final LonLat currentStopLonLat = new LonLat(currentStop.getLon(), currentStop.getLat());
			positions.add(new LonLatAtTime(currentArrivalTime, currentStopLonLat));
			positions.add(new LonLatAtTime(currentDepartureTime, currentStopLonLat));
		}
		return new TripTrajectory(trip, new ArrayList<>(positions));
	}
	
	
	
	
	
	

}

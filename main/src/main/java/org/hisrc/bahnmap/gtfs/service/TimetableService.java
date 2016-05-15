package org.hisrc.bahnmap.gtfs.service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hisrc.bahnmap.model.LonLat;
import org.hisrc.bahnmap.model.TripState;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
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

	public void process(LocalDate date) {
		final Set<Trip> trips = findTripsByDate(date);

		final int latestArrivalTime = trips.stream().map(this.dao::getStopTimesForTrip)
				.map(stopTimes -> stopTimes.get(stopTimes.size() - 1)).map(StopTime::getArrivalTime)
				.max(Comparator.naturalOrder()).orElse(0);
		final int arrayLength = latestArrivalTime + 1;

		final Map<Trip, TripState[]> tripsStates = new HashMap<>();
		trips.stream().forEach(trip -> {
			final TripState[] tripStatesForTrip = this.createTripStatesForTrip(trip, arrayLength);
			tripsStates.put(trip, tripStatesForTrip);
		});
		tripsStates.toString();
	}

	private TripState[] createTripStatesForTrip(Trip trip, int arrayLength) {
		final List<StopTime> stopTimesForTrip = this.dao.getStopTimesForTrip(trip);
		int lastDepartureTime = Integer.MIN_VALUE;
		Stop lastStop = null;
		LonLat lastStopLonLat = null;
		final TripState[] timedLonLats = new TripState[arrayLength];
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
						.stream()
						.forEach(timedLonLat -> timedLonLats[timedLonLat.getTime()] = new TripState(trip,
								previousStop, currentStop, timedLonLat.getLonLat()));
			}
			interpolator
					.interpolate(currentStopLonLat, currentStopLonLat, currentArrivalTime * 1000,
							currentDepartureTime * 100)
					.stream().forEach(timedLonLat -> timedLonLats[timedLonLat.getTime()] = new TripState(trip,
							currentStop, currentStop, timedLonLat.getLonLat()));
			lastDepartureTime = currentDepartureTime;
			lastStop = currentStop;
			lastStopLonLat = currentStopLonLat;
		}
		return timedLonLats;
	}

	private ServiceDate createServiceDate(LocalDate date) {
		final ServiceDate serviceDate = new ServiceDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
		return serviceDate;
	}

}

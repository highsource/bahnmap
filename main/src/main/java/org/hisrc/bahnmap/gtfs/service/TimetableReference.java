package org.hisrc.bahnmap.gtfs.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.hisrc.bahnmap.model.TripTrajectory;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

public class TimetableReference {

	public static final int TIME_PERIOD_IN_SECONDS = 10;
	public static final List<Integer> TICK_LENGTHS = Collections
			.unmodifiableList(Arrays.asList(720, 120, 24, 6, 3, 2, 1));

	private final List<Stop> stops;
	private final Map<Stop, Integer> stopIndices;
	private final List<Route> routes;
	private final Map<Route, Integer> routeIndices;
	private final List<Trip> trips;
	private final Map<Trip, Integer> tripIndices;
	private int earliestTime;
	private int latestTime;
	private final Map<Trip, TripTrajectory> tripTrajectoriesByTrip;
	private LocalDate date;

	public TimetableReference(LocalDate date, Collection<Stop> stops, Collection<Route> routes, Collection<Trip> trips,
			Map<Trip, List<StopTime>> stopTimesOfTrips, Collection<TripTrajectory> tripTrajectories) {
		Validate.notNull(date);
		Validate.noNullElements(stops);
		Validate.noNullElements(routes);
		Validate.noNullElements(trips);
		Validate.notNull(stopTimesOfTrips);
		Validate.noNullElements(tripTrajectories);
		this.date = date;
		this.stops = sort(stops);
		this.stopIndices = index(this.stops);
		this.routes = sort(routes);
		this.routeIndices = index(this.routes);
		this.trips = sort(trips);
		this.tripIndices = index(this.trips);
		earliestTime = stopTimesOfTrips.values().stream().flatMap(Collection::stream).map(StopTime::getArrivalTime)
				.min(Comparator.naturalOrder()).orElseThrow(IllegalArgumentException::new);
		latestTime = stopTimesOfTrips.values().stream().flatMap(Collection::stream).map(StopTime::getDepartureTime)
				.max(Comparator.naturalOrder()).orElseThrow(IllegalArgumentException::new);
		tripTrajectoriesByTrip = tripTrajectories.stream().collect(Collectors.toMap(TripTrajectory::getTrip, t -> t));
	}

	public LocalDate getDate() {
		return date;
	}

	public int getEarliestTime() {
		return earliestTime;
	}

	public int getLatestTime() {
		return latestTime;
	}

	private <I extends IdentityBean<K>, K extends Comparable<? super K> & Serializable> List<I> sort(
			Collection<I> items) {
		final List<I> sortedItems = new ArrayList<>(items);
		Collections.sort(sortedItems, Comparator.comparing(IdentityBean::getId));
		return Collections.unmodifiableList(sortedItems);
	}

	private <I> Map<I, Integer> index(List<I> items) {
		final Map<I, Integer> itemIndices = new HashMap<>();
		for (int index = 0; index < items.size(); index++) {
			itemIndices.put(items.get(index), index);
		}
		return Collections.unmodifiableMap(itemIndices);
	}

	public List<Stop> getStops() {
		return stops;
	}

	public int getStopIndex(Stop stop) {
		final Integer index = this.stopIndices.get(stop);
		if (index == null) {
			throw new IllegalArgumentException("Unknown stop.");
		}
		return index.intValue();
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public int getRouteIndex(Route route) {
		final Integer index = this.routeIndices.get(route);
		if (index == null) {
			throw new IllegalArgumentException("Unknown route.");
		}
		return index.intValue();
	}

	public List<Trip> getTrips() {
		return trips;
	}

	public int getTripIndex(Trip trip) {
		final Integer index = this.tripIndices.get(trip);
		if (index == null) {
			throw new IllegalArgumentException("Unknown trip.");
		}
		return index.intValue();
	}

	public TripTrajectory getTripTrajectory(Trip trip) {
		return this.tripTrajectoriesByTrip.get(trip);
	}

	public TripTrajectory getSubTripTrajectory(int time, Trip trip) {
		final TripTrajectory tripTrajectory = getTripTrajectory(trip);
		final int startTime = time;
		final int endTime = time + getTickLength(startTime)* TIME_PERIOD_IN_SECONDS;
		return tripTrajectory.subtrajectory(startTime, endTime);
	}

	public int getTickLength(int time) {
		for (int tickLength : TICK_LENGTHS) {
			if (time % tickLength == 0) {
				return tickLength;
			}
		}
		return 1;
	}
}

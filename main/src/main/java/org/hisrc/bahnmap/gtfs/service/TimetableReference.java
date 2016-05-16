package org.hisrc.bahnmap.gtfs.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hisrc.bahnmap.model.TripState;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

public class TimetableReference {

	private final List<Stop> stops;
	private final Map<Stop, Integer> stopIndices;
	private final List<Route> routes;
	private final Map<Route, Integer> routeIndices;
	private final List<Trip> trips;
	private final Map<Trip, Integer> tripIndices;
	private int earliestTime;
	private int latestTime;
	private final TripState[][] tripStatesByTripIndexByTime;

	public TimetableReference(LocalDate date, Collection<Stop> stops, Collection<Route> routes, Collection<Trip> trips,
			Map<Trip, List<StopTime>> stopTimesOfTrips, Collection<TripState> tripStates) {
		Objects.requireNonNull(stops, "stops must not be null.");
		Objects.requireNonNull(routes, "routes must not be null.");
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
		this.tripStatesByTripIndexByTime = new TripState[this.trips.size()][latestTime + 1];
		tripStates.stream().forEach(tripState -> {
			tripStatesByTripIndexByTime[getTripIndex(tripState.getTrip())][tripState.getTime()] = tripState;
		});
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

	public TripState getTripState(int time, Trip trip) {
		final TripState[] tripStates = tripStatesByTripIndexByTime[getTripIndex(trip)];
		return tripStates[time];
	}
}

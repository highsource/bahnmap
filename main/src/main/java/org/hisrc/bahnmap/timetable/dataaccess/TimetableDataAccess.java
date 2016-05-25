package org.hisrc.bahnmap.timetable.dataaccess;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

public class TimetableDataAccess {

	private final GtfsRelationalDao gtfsRelationalDao;
	private final List<Stop> stops;
	private final Map<Stop, Integer> indexByStop;
	private final List<Route> routes;
	private final Map<Route, Integer> indexByRoute;
	private final List<Trip> trips;
	private final Map<Trip, Integer> indexByTrip;
	private final Map<Integer, Trip> tripByIndex;

	public TimetableDataAccess(GtfsRelationalDao gtfsRelationalDao) {
		this.gtfsRelationalDao = gtfsRelationalDao;
		this.stops = sort(gtfsRelationalDao.getAllStops());
		this.indexByStop = index(this.stops);
		this.routes = sort(gtfsRelationalDao.getAllRoutes());
		this.indexByRoute = index(this.routes);
		this.trips = sort(gtfsRelationalDao.getAllTrips());
		this.indexByTrip = index(this.trips);
		this.tripByIndex = inverse(indexByTrip);
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

	private <I> Map<Integer, I> inverse(Map<I, Integer> indexByItem) {
		return indexByItem.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey));
	}

	private <I extends IdentityBean<K>, K extends Comparable<? super K> & Serializable> int getIndex(
			Map<I, Integer> itemIndices, I item) {
		Validate.notNull(itemIndices);
		Validate.notNull(item);
		final Integer index = itemIndices.get(item);
		if (index == null) {
			throw new IllegalArgumentException(MessageFormat.format("Unknown object [{0}].", item));
		}
		return index.intValue();
	}

	private <I extends IdentityBean<K>, K extends Comparable<? super K> & Serializable> I getItem(
			Map<Integer, I> itemByIndex, int index) {
		Validate.notNull(itemByIndex);
		final I item = itemByIndex.get(index);
		if (item == null) {
			throw new IllegalArgumentException(MessageFormat.format("Unknown index [{0}].", index));
		}
		return item;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public int getStopIndex(Stop stop) {
		return getIndex(this.indexByStop, stop);
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public int getRouteIndex(Route route) {
		return getIndex(this.indexByRoute, route);
	}

	public List<Trip> getTrips() {
		return trips;
	}

	public int getTripIndex(Trip trip) {
		return getIndex(this.indexByTrip, trip);
	}

	public Trip getTripByIndex(int index) {
		return getItem(this.tripByIndex, index);
	}
}

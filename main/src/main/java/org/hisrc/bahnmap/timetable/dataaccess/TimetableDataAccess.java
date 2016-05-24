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

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

public class TimetableDataAccess {

	private final GtfsRelationalDao gtfsRelationalDao;
	private final List<Stop> stops;
	private final Map<Stop, Integer> stopIndices;
	private final List<Route> routes;
	private final Map<Route, Integer> routeIndices;

	public TimetableDataAccess(GtfsRelationalDao gtfsRelationalDao) {
		this.gtfsRelationalDao = gtfsRelationalDao;
		this.stops = sort(gtfsRelationalDao.getAllStops());
		this.stopIndices = index(this.stops);
		this.routes = sort(gtfsRelationalDao.getAllRoutes());
		this.routeIndices = index(this.routes);
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

	public List<Stop> getStops() {
		return stops;
	}

	public int getStopIndex(Stop stop) {
		return getIndex(this.stopIndices, stop);
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public int getRouteIndex(Route route) {
		return getIndex(this.routeIndices, route);
	}
}

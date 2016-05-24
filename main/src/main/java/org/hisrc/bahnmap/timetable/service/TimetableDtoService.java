package org.hisrc.bahnmap.timetable.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.hisrc.bahnmap.timetable.dataaccess.TimetableDataAccess;
import org.hisrc.bahnmap.timetable.dto.RouteDto;
import org.hisrc.bahnmap.timetable.dto.StopDto;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;

public class TimetableDtoService {

	private final TimetableDataAccess timetableDataAccess;
	private final List<StopDto> stops;
	private final Map<Integer, StopDto> stopsById;
	private final List<RouteDto> routes;
	private final Map<Integer, RouteDto> routesById;

	public TimetableDtoService(TimetableDataAccess timetableDataAccess) {
		Validate.notNull(timetableDataAccess);
		this.timetableDataAccess = timetableDataAccess;
		this.stops = list(getTimetableDataAccess()::getStops, this::convertStop);
		this.stopsById = index(stops, StopDto::getId);
		this.routes = list(getTimetableDataAccess()::getRoutes, this::convertRoute);
		this.routesById = index(routes, RouteDto::getId);
	}

	private <I extends IdentityBean<K>, K extends Comparable<? super K> & Serializable, D> List<D> list(
			Supplier<List<I>> itemsSupplier, Function<? super I, ? extends D> mapper) {
		return Collections.unmodifiableList(itemsSupplier.get().stream().map(mapper).collect(Collectors.toList()));
	}

	private <D, K> Map<K, D> index(List<D> items, Function<? super D, ? extends K> keyExtractor) {
		return Collections.unmodifiableMap(items.stream().collect(Collectors.toMap(keyExtractor, i -> i)));
	}

	private TimetableDataAccess getTimetableDataAccess() {
		return timetableDataAccess;
	}

	public List<StopDto> getStops() {
		return stops;
	}

	public Optional<StopDto> getStopById(int id) {
		return Optional.ofNullable(stopsById.get(id));
	}

	private StopDto convertStop(Stop stop) {
		return StopDto.of(getTimetableDataAccess().getStopIndex(stop), stop);
	}

	public List<RouteDto> getRoutes() {
		return routes;
	}

	public Optional<RouteDto> getRouteById(int id) {
		return Optional.ofNullable(routesById.get(id));
	}

	private RouteDto convertRoute(Route route) {
		return RouteDto.of(getTimetableDataAccess().getRouteIndex(route), route);
	}

}

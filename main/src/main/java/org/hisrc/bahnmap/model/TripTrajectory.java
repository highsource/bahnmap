package org.hisrc.bahnmap.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.Trip;

public class TripTrajectory {

	private final Trip trip;
	private final int startTime;
	private final int endTime;
	private final List<LonLatAtTime> positions;
	private final NavigableMap<Integer, LonLatAtTime> positionsByTime;

	public TripTrajectory(Trip trip, List<LonLatAtTime> positions) {
		Validate.notNull(trip);
		Validate.notNull(positions);
		Validate.noNullElements(positions);
		Validate.isTrue(!positions.isEmpty());
		this.trip = trip;
		int startTime = Integer.MAX_VALUE;
		int endTime = Integer.MIN_VALUE;
		this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
		final NavigableMap<Integer, LonLatAtTime> positionsByTime = new TreeMap<>();

		for (LonLatAtTime position : positions) {
			final int positionTime = position.getTime();
			startTime = Math.min(startTime, positionTime);
			endTime = Math.max(endTime, positionTime);
			positionsByTime.put(positionTime, position);
		}
		this.startTime = startTime;
		this.endTime = endTime;
		this.positionsByTime = Collections.unmodifiableNavigableMap(positionsByTime);
	}

	public Trip getTrip() {
		return trip;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public List<LonLatAtTime> getPositions() {
		return positions;
	}

	public LonLatAtTime getPositionByTime(int time) {
		if (time < this.startTime || time > this.endTime) {
			return null;
		}
		final LonLatAtTime knownPositionAtTime = this.positionsByTime.get(time);
		if (knownPositionAtTime != null) {
			return knownPositionAtTime;
		}

		final Entry<Integer, LonLatAtTime> lowerEntry = this.positionsByTime.lowerEntry(time);
		final Entry<Integer, LonLatAtTime> higherEntry = this.positionsByTime.higherEntry(time);

		final int lowerTime = lowerEntry.getKey();
		final int higherTime = higherEntry.getKey();

		final LonLat lowerLonLat = lowerEntry.getValue().getLonLat();
		final LonLat higherLonLat = higherEntry.getValue().getLonLat();

		if (Objects.equals(lowerLonLat, higherLonLat)) {
			return new LonLatAtTime(time, higherLonLat);
		}

		final int deltaTime = higherTime - lowerTime;
		final int passedTime = time - lowerTime;
		final double lowerLon = lowerLonLat.getLon();
		final double lowerLat = lowerLonLat.getLat();
		final double higherLon = higherLonLat.getLon();
		final double higherLat = higherLonLat.getLat();
		final double deltaLon = higherLon - lowerLon;
		final double deltaLat = higherLat - lowerLat;
		final double lon = passedTime * deltaLon / deltaTime + lowerLon;
		final double lat = passedTime * deltaLat / deltaTime + lowerLat;

		return new LonLatAtTime(time, lon, lat);
	}

	public TripTrajectory subtrajectory(int startTime, int endTime) {
		if (endTime < this.startTime || startTime > this.endTime) {
			return null;
		}
		startTime = Math.max(startTime, this.startTime);
		endTime = Math.min(endTime, this.endTime);
		final Map<Integer, LonLatAtTime> subPositionsByTime = positionsByTime.subMap(startTime, endTime + 1);
		final List<LonLatAtTime> subPositions = new ArrayList<>(subPositionsByTime.size() + 2);
		if (!subPositionsByTime.containsKey(startTime)) {
			subPositions.add(getPositionByTime(startTime));
		}
		subPositions.addAll(subPositionsByTime.values());
		if (!subPositionsByTime.containsKey(endTime)) {
			subPositions.add(getPositionByTime(endTime));
		}
		return new TripTrajectory(trip, subPositions);
	}

}

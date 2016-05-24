package org.hisrc.bahnmap.gtfs.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hisrc.bahnmap.model.LonLat;
import org.hisrc.bahnmap.model.LonLatAtTime;

public class CoordinatesInterpolator {

	private static final int MILLISECONDS_IN_SECOND = 1000;

	public List<LonLatAtTime> interpolate(LonLat start, LonLat end, int startTimeInMillisecondsPastMidnight,
			int endTimeInMillisecondsPastMidnight) {
		final int startTimeInSecondsPastMidnight = (startTimeInMillisecondsPastMidnight / MILLISECONDS_IN_SECOND
				+ ((startTimeInMillisecondsPastMidnight % MILLISECONDS_IN_SECOND) == 0 ? 0 : 1));

		final int endTimeInSecondsPastMidnight = endTimeInMillisecondsPastMidnight / MILLISECONDS_IN_SECOND;

		final int durationInMilliseconds = endTimeInMillisecondsPastMidnight - startTimeInMillisecondsPastMidnight;

		double fullDeltaLon = end.getLon() - start.getLon();
		double fullDeltaLat = end.getLat() - start.getLat();

		final int startOffsetInMilliseconds = startTimeInSecondsPastMidnight * MILLISECONDS_IN_SECOND
				- startTimeInMillisecondsPastMidnight;
		final int endOffsetInMilliseconds = endTimeInSecondsPastMidnight * MILLISECONDS_IN_SECOND
				- startTimeInMillisecondsPastMidnight;
		double startLon = start.getLon() + (fullDeltaLon * startOffsetInMilliseconds) / durationInMilliseconds;
		double startLat = start.getLat() + (fullDeltaLat * startOffsetInMilliseconds) / durationInMilliseconds;
		double endLon = start.getLon() + (fullDeltaLon * endOffsetInMilliseconds) / durationInMilliseconds;
		double endLat = start.getLat() + (fullDeltaLat * endOffsetInMilliseconds) / durationInMilliseconds;

		double deltaLon = endLon - startLon;
		double deltaLat = endLat - startLat;
		int deltaTime = endOffsetInMilliseconds - startOffsetInMilliseconds;

		return IntStream.rangeClosed(startTimeInSecondsPastMidnight, endTimeInSecondsPastMidnight)
				.mapToObj(timeInSecondsPastMidnight -> {
					final int offsetInMilliseconds = (timeInSecondsPastMidnight - startTimeInSecondsPastMidnight) * MILLISECONDS_IN_SECOND;
					return new LonLatAtTime(timeInSecondsPastMidnight,
							new LonLat(startLon + (deltaLon * offsetInMilliseconds) / deltaTime,
									startLat + (deltaLat * offsetInMilliseconds) / deltaTime));
				}).collect(Collectors.toList());

	}
}

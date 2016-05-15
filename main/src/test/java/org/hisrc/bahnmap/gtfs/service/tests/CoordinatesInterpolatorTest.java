package org.hisrc.bahnmap.gtfs.service.tests;

import java.util.Arrays;
import java.util.List;

import org.hisrc.bahnmap.gtfs.service.CoordinatesInterpolator;
import org.hisrc.bahnmap.model.LonLat;
import org.hisrc.bahnmap.model.TimedLonLat;
import org.junit.Assert;
import org.junit.Test;

public class CoordinatesInterpolatorTest {

	private CoordinatesInterpolator interpolator = new CoordinatesInterpolator();

	@Test
	public void correctlyInterpolatesWithExactStartAndEndTimestampsInMillis() {
		final List<TimedLonLat> interpolated = interpolator.interpolate(new LonLat(10, 20), new LonLat(30, 50), 30000,
				32000);
		Assert.assertEquals(Arrays.asList(new TimedLonLat(30, 10, 20),
				new TimedLonLat(31, 20, 35), new TimedLonLat(32, 30, 50)), interpolated);

	}
	
	@Test
	public void correctlyInterpolatesWithNonExactStartAndEndTimestampsInMillis() {
		final List<TimedLonLat> interpolated = interpolator.interpolate(new LonLat(5, 12.5), new LonLat(35, 57.5), 29500,
				32500);
		Assert.assertEquals(Arrays.asList(new TimedLonLat(30, 10, 20),
				new TimedLonLat(31, 20, 35), new TimedLonLat(32, 30, 50)), interpolated);

	}
	
}

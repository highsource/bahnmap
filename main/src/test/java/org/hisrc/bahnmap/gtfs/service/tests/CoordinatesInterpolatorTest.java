package org.hisrc.bahnmap.gtfs.service.tests;

import java.util.Arrays;
import java.util.List;

import org.hisrc.bahnmap.gtfs.service.CoordinatesInterpolator;
import org.hisrc.bahnmap.model.LonLat;
import org.hisrc.bahnmap.model.LonLatAtTime;
import org.junit.Assert;
import org.junit.Test;

public class CoordinatesInterpolatorTest {

	private CoordinatesInterpolator interpolator = new CoordinatesInterpolator();

	@Test
	public void correctlyInterpolatesWithExactStartAndEndTimestampsInMillis() {
		final List<LonLatAtTime> interpolated = interpolator.interpolate(new LonLat(10, 20), new LonLat(30, 50), 30000,
				32000);
		Assert.assertEquals(Arrays.asList(new LonLatAtTime(30, 10, 20),
				new LonLatAtTime(31, 20, 35), new LonLatAtTime(32, 30, 50)), interpolated);

	}
	
	@Test
	public void correctlyInterpolatesWithNonExactStartAndEndTimestampsInMillis() {
		final List<LonLatAtTime> interpolated = interpolator.interpolate(new LonLat(5, 12.5), new LonLat(35, 57.5), 29500,
				32500);
		Assert.assertEquals(Arrays.asList(new LonLatAtTime(30, 10, 20),
				new LonLatAtTime(31, 20, 35), new LonLatAtTime(32, 30, 50)), interpolated);

	}
	
}

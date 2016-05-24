package org.hisrc.bahnmap.gtfs.service.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Set;

import org.hisrc.bahnmap.base.serialization.FileTimetableReferenceSerializer;
import org.hisrc.bahnmap.gtfs.Constants;
import org.hisrc.bahnmap.gtfs.serialization.ZipGtfsRelationalDaoDeserializer;
import org.hisrc.bahnmap.gtfs.service.TimetableReference;
import org.hisrc.bahnmap.gtfs.service.TimetableService;
import org.hisrc.bahnmap.model.LonLat;
import org.hisrc.bahnmap.model.LonLatAtTime;
import org.hisrc.bahnmap.model.TripState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

public class TimetableServiceTest {

	private TimetableService sut;

	@Before
	public void setUp() throws IOException, URISyntaxException {
		final ZipGtfsRelationalDaoDeserializer deserializer = new ZipGtfsRelationalDaoDeserializer();
		final File file = new File(
				getClass().getClassLoader().getResource(Constants.DEFAULT_GTFS_RESOURCE_NAME).toURI());
		final GtfsRelationalDao dao = deserializer.deserialize(file);
		sut = new TimetableService(dao);
	}

	@Test
	public void findsServiceIdsByDate() {
		final Set<AgencyAndId> serviceIdsOnDate = sut.findServiceIdsOnDate(Constants.DEFAULT_DATE);
		Assert.assertEquals(512, serviceIdsOnDate.size());
	}

	@Test
	public void findsTripsByDate() {
		final Set<Trip> trips = sut.findTripsByDate(Constants.DEFAULT_DATE);
		Assert.assertEquals(688, trips.size());
	}

	@Test
	public void createsTimetableReference() throws IOException {
		final TimetableReference timetableReference = sut.createTimetableReference(Constants.DEFAULT_DATE);
		final Stop stop0 = timetableReference.getStops().get(0);
		Assert.assertEquals("Bydgoszcz Glowna", stop0.getName());
		Assert.assertEquals(0, timetableReference.getStopIndex(stop0));
		final Trip trip0 = timetableReference.getTrips().get(0);
		Assert.assertEquals("Luxembourg", trip0.getTripHeadsign());
		Assert.assertEquals(0, timetableReference.getTripIndex(trip0));
		final Route route0 = timetableReference.getRoutes().get(0);
		Assert.assertEquals("Bus 115", route0.getLongName());
		Assert.assertEquals(0, timetableReference.getRouteIndex(route0));
		final LonLatAtTime position = timetableReference.getTripTrajectory(trip0).getPositionByTime(07 * 60 * 60 + 25 * 60);
		new FileTimetableReferenceSerializer(timetableReference).write(new File("."));
	}
}

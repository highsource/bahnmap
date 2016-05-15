package org.hisrc.bahnmap.gtfs.service.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Set;

import org.hisrc.bahnmap.gtfs.Constants;
import org.hisrc.bahnmap.gtfs.serialization.ZipGtfsRelationalDaoDeserializer;
import org.hisrc.bahnmap.gtfs.service.TimetableService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
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

		for (LocalDate date = LocalDate.of(2015, 12, 13); date
				.isBefore(LocalDate.of(2016, 12, 11)); date = date.plusDays(1)) {
			System.out.println(MessageFormat.format("{0},{1}", date.toString(), sut.findTripsByDate(date).size()));
		}
	}
	
	@Test
	public void p() {
		sut.process(Constants.DEFAULT_DATE);
	}
}

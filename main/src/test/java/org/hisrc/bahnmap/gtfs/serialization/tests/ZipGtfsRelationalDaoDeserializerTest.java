package org.hisrc.bahnmap.gtfs.serialization.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.hisrc.bahnmap.gtfs.Constants;
import org.hisrc.bahnmap.gtfs.serialization.ZipGtfsRelationalDaoDeserializer;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.services.GtfsRelationalDao;


public class ZipGtfsRelationalDaoDeserializerTest {
	
	@Test
	public void successfullyDeserializes() throws URISyntaxException, IOException
	{
		final ZipGtfsRelationalDaoDeserializer deserializer = new ZipGtfsRelationalDaoDeserializer();
		final File file = new File(getClass().getClassLoader().getResource(Constants.DEFAULT_GTFS_RESOURCE_NAME).toURI());
		final GtfsRelationalDao dao = deserializer.deserialize(file);
		Assert.assertEquals(dao.getAllRoutes().size(), 1283);
		Assert.assertEquals(dao.getAllAgencies().size(), 3);
		Assert.assertEquals(dao.getAllStops().size(), 563);
		Assert.assertEquals(dao.getAllTrips().size(), 5575);
	}
	

}

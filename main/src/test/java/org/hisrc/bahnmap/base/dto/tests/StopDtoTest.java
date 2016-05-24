package org.hisrc.bahnmap.base.dto.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.hisrc.bahnmap.gtfs.Constants;
import org.hisrc.bahnmap.gtfs.serialization.ZipGtfsRelationalDaoDeserializer;
import org.hisrc.bahnmap.timetable.dto.StopDto;
import org.junit.Before;
import org.junit.Test;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StopDtoTest {

	private Stop firstStop;

	@Before
	public void setUp() throws IOException, URISyntaxException {
		final ZipGtfsRelationalDaoDeserializer deserializer = new ZipGtfsRelationalDaoDeserializer();
		final File file = new File(
				getClass().getClassLoader().getResource(Constants.DEFAULT_GTFS_RESOURCE_NAME).toURI());
		final GtfsRelationalDao dao = deserializer.deserialize(file);
		firstStop = dao.getAllStops().iterator().next();
	}

	@Test
	public void serializesStopDto() throws JsonGenerationException, JsonMappingException, IOException {
		new ObjectMapper().writeValue(System.out, StopDto.of(0, firstStop));
	}
}

package org.hisrc.bahnmap.gtfs.serialization;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipFile;

import org.onebusaway.csv_entities.CsvInputSource;
import org.onebusaway.csv_entities.ZipFileCsvInputSource;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

public class ZipGtfsRelationalDaoDeserializer {

	public GtfsRelationalDao deserialize(File file) throws IOException {
		Objects.requireNonNull(file, "file must not be null.");
		final CsvInputSource csvInputSource = new ZipFileCsvInputSource(new ZipFile(file));

		final GtfsMutableRelationalDao store = new GtfsRelationalDaoImpl();

		final GtfsReader reader = new GtfsReader();
		reader.setInputSource(csvInputSource);
		reader.setEntityStore(store);
		reader.setInternStrings(true);

		for (Class<?> entityClass : reader.getEntityClasses()) {
			reader.readEntities(entityClass);
			store.flush();
		}
		store.close();
		return store;
	}
}

package org.hisrc.bahnmap.onebusaway.csv_entities;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.csv_entities.CsvInputSource;
import org.onebusaway.csv_entities.ZipFileCsvInputSource;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;

public class ZipTest {

	@Test
	public void readsZip() throws IOException {
		final String fileName = getClass().getClassLoader().getResource("2016.0.1.zip").getFile();
		final CsvInputSource csvInputSource = new ZipFileCsvInputSource(new ZipFile(fileName));
		Assert.assertNotNull(csvInputSource);
		
		GtfsMutableRelationalDao store = new GtfsRelationalDaoImpl();
		
        GtfsReader reader = new GtfsReader();
        reader.setInputSource(csvInputSource);
        reader.setEntityStore(store);
        reader.setInternStrings(true);
        
        Set<String> agencyIdsSeen = new HashSet<>();
        int nextAgencyId = 1; // used for generating agency IDs to resolve ID conflicts
        
        for (Class<?> entityClass : reader.getEntityClasses()) {
            reader.readEntities(entityClass);
            store.flush();
            // NOTE that agencies are first in the list and read before all other entity types, so it is effective to
            // set the agencyId here. Each feed ("bundle") is loaded by a separate reader, so there is no risk of
            // agency mappings accumulating.
            if (entityClass == Agency.class) {
                String defaultAgencyId = null;
                for (Agency agency : reader.getAgencies()) {
                    String agencyId = agency.getId();
                    // Somehow, when the agency's id field is missing, OBA replaces it with the agency's name.
                    // TODO Figure out how and why this is happening.
                    if (agencyId == null || agencyIdsSeen.contains(agencyId)) {
                        // Loop in case generated name is already in use.
                        String generatedAgencyId = null;
                        while (generatedAgencyId == null || agencyIdsSeen.contains(generatedAgencyId)) {
                            generatedAgencyId = "F" + nextAgencyId;
                            nextAgencyId++;
                        }
                        reader.addAgencyIdMapping(agencyId, generatedAgencyId); // NULL key should work
                        agency.setId(generatedAgencyId);
                        agencyId = generatedAgencyId;
                    }
                    if (agencyId != null) agencyIdsSeen.add(agencyId);
                    if (defaultAgencyId == null) defaultAgencyId = agencyId;
                }
                reader.setDefaultAgencyId(defaultAgencyId); // not sure this is a good idea, setting it to the first-of-many IDs.
            }
        }

        for (ShapePoint shapePoint : store.getAllEntitiesForType(ShapePoint.class)) {
            shapePoint.getShapeId().setAgencyId(reader.getDefaultAgencyId());
        }
        for (Route route : store.getAllEntitiesForType(Route.class)) {
            route.getId().setAgencyId(reader.getDefaultAgencyId());
//            generateRouteColor(route);
        }
        for (Stop stop : store.getAllEntitiesForType(Stop.class)) {
            stop.getId().setAgencyId(reader.getDefaultAgencyId());
        }
        for (Trip trip : store.getAllEntitiesForType(Trip.class)) {
            trip.getId().setAgencyId(reader.getDefaultAgencyId());
        }
        for (ServiceCalendar serviceCalendar : store.getAllEntitiesForType(ServiceCalendar.class)) {
            serviceCalendar.getServiceId().setAgencyId(reader.getDefaultAgencyId());
        }
        for (ServiceCalendarDate serviceCalendarDate : store.getAllEntitiesForType(ServiceCalendarDate.class)) {
            serviceCalendarDate.getServiceId().setAgencyId(reader.getDefaultAgencyId());
        }
        for (FareAttribute fareAttribute : store.getAllEntitiesForType(FareAttribute.class)) {
            fareAttribute.getId().setAgencyId(reader.getDefaultAgencyId());
        }
        for (Pathway pathway : store.getAllEntitiesForType(Pathway.class)) {
            pathway.getId().setAgencyId(reader.getDefaultAgencyId());
        }

        store.close();
        

	}

}

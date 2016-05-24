package org.hisrc.bahnmap.base.serialization;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hisrc.bahnmap.base.dto.TripStatesDto;
import org.hisrc.bahnmap.base.dto.TripTrajectoriesDto;
import org.hisrc.bahnmap.base.dto.TripTrajectoryDto;
import org.hisrc.bahnmap.gtfs.service.TimetableReference;
import org.hisrc.bahnmap.model.LonLatAtTime;
import org.hisrc.bahnmap.timetable.dto.StopDto;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileTimetableReferenceSerializer {

	private final ObjectMapper objectMapper;
	private final TimetableReference timetableReference;

	public FileTimetableReferenceSerializer(TimetableReference timetableReference) {
		Objects.requireNonNull(timetableReference, "timetableReference must not be null.");
		this.timetableReference = timetableReference;
		this.objectMapper = new ObjectMapper();
		// this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public void write(File mainDirectory) throws IOException {

		final File directory = new File(
				new File(new File(mainDirectory, String.format("%04d", timetableReference.getDate().getYear())),
						String.format("%02d", timetableReference.getDate().getMonthValue())),
				String.format("%02d", timetableReference.getDate().getDayOfMonth()));
		directory.mkdirs();
		writeStops(directory);
		writeTripStates(directory);
	}

	public void writeStops(File directory) throws IOException {
		final List<StopDto> stopDtos = timetableReference.getStops().stream()
				.map(stop -> StopDto.of(this.timetableReference.getStopIndex(stop), stop)).collect(Collectors.toList());
		objectMapper.writeValue(new File(directory, "stops"), stopDtos);
	}

	public void writeTripStates(File directory) throws IOException {
		final File tripStatesDirectory = new File(directory, "tripsStates");
		tripStatesDirectory.mkdirs();
		for (int time = timetableReference.getEarliestTime(); time <= timetableReference.getLatestTime(); time++) {
			writeTripStates(time, tripStatesDirectory);
		}
	}

	private void writeTripStates(int time, File directory) throws IOException {
		final File hourDirectory = new File(directory, String.format("%02d", time / 3600));
		final File minuteDirectory = new File(hourDirectory, String.format("%02d", (time % 3600) / 60));
		minuteDirectory.mkdirs();
		final File secondFile = new File(minuteDirectory, String.format("%02d", time % 60));

		final List<TripTrajectoryDto> tripTrajectoryDtos = timetableReference.getTrips().stream()
				.map(trip -> timetableReference.getSubTripTrajectory(time, trip)).filter(Objects::nonNull)
				.map(tripTrajectory -> {
					final int tripIndex = timetableReference.getTripIndex(tripTrajectory.getTrip());
					final List<LonLatAtTime> positions = tripTrajectory.getPositions();
					final BigDecimal[] coordinates = new BigDecimal[positions.size() * 2];
					final int[] times = new int[positions.size()];
					for (int index = 0; index < positions.size(); index++) {
						final LonLatAtTime position = positions.get(index);
						times[index] = position.getTime() - time;
						coordinates[index * 2] = new BigDecimal(position.getLonLat().getLon())
								.setScale(6, RoundingMode.HALF_EVEN).stripTrailingZeros();
						coordinates[index * 2 + 1] = new BigDecimal(position.getLonLat().getLat())
								.setScale(6, RoundingMode.HALF_EVEN).stripTrailingZeros();
					}
					return new TripTrajectoryDto(tripIndex, coordinates, times);
				}).filter(Objects::nonNull).collect(Collectors.toList());

		final TripTrajectoriesDto tripTrajectoriesDto = new TripTrajectoriesDto(time, tripTrajectoryDtos);
		objectMapper.writeValue(secondFile, tripTrajectoriesDto);
	}
}

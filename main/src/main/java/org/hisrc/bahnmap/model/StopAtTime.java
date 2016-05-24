package org.hisrc.bahnmap.model;

import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.Stop;

public class StopAtTime {

	private final Stop stop;
	private final int time;

	public StopAtTime(int time, Stop stop) {
		Validate.notNull(stop);
		this.time = time;
		this.stop = stop;
	}

	public Stop getStop() {
		return stop;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stop == null) ? 0 : stop.hashCode());
		result = prime * result + time;
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		final StopAtTime that = (StopAtTime) object;
		return Objects.equals(this.stop, that.stop) && this.time == that.time;
	}
	
	@Override
	public String toString() {
		return time + ":" + stop;
	}

}

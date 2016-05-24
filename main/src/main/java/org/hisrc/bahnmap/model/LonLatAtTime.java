package org.hisrc.bahnmap.model;

import java.util.Objects;

public class LonLatAtTime {

	private final int time;

	private final LonLat lonLat;

	public LonLatAtTime(int time, double lon, double lat) {
		this(time, new LonLat(lon, lat));
	}

	public LonLatAtTime(int time, LonLat lonLat) {
		this.time = time;
		this.lonLat = lonLat;
	}

	public int getTime() {
		return time;
	}

	public LonLat getLonLat() {
		return lonLat;
	}

	@Override
	public String toString() {
		return time + ":" + lonLat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lonLat == null) ? 0 : lonLat.hashCode());
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
		final LonLatAtTime that = (LonLatAtTime) object;
		return Objects.equals(this.lonLat, that.lonLat) && this.time == that.time;
	}

}

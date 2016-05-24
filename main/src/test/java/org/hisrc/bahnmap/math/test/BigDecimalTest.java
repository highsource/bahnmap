package org.hisrc.bahnmap.math.test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Test;

public class BigDecimalTest {

	@Test
	public void correctlyScalesBigDecimal() {
		Assert.assertEquals(new BigDecimal("12.1212"),
				new BigDecimal("12.12121212").setScale(4, RoundingMode.HALF_EVEN).stripTrailingZeros());
		Assert.assertEquals(new BigDecimal("0.1212"),
				new BigDecimal("0.12121212").setScale(4, RoundingMode.HALF_EVEN).stripTrailingZeros());
		Assert.assertEquals(new BigDecimal("1212.1212"),
				new BigDecimal("1212.12121212").setScale(4, RoundingMode.HALF_EVEN).stripTrailingZeros());
		Assert.assertEquals(new BigDecimal("1212.12"),
				new BigDecimal("1212.12001200").setScale(4, RoundingMode.HALF_EVEN).stripTrailingZeros());
	}

}

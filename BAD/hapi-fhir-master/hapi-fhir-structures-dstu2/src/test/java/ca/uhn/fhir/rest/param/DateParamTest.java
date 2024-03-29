package ca.uhn.fhir.rest.param;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;

public class DateParamTest {
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(DateParamTest.class);

	@Test
	public void testConstructors() {
		new DateParam();
		new DateParam("2011-01-02");
		new DateParam(ParamPrefixEnum.GREATERTHAN, new Date());
		new DateParam(ParamPrefixEnum.GREATERTHAN, new DateTimeDt("2011-01-02"));
		new DateParam(ParamPrefixEnum.GREATERTHAN, InstantDt.withCurrentTime());
		new DateParam(ParamPrefixEnum.GREATERTHAN, "2011-01-02");

	}

	@Test
	public void testParse() {
		Date date = new Date();

		DateParam param = new DateParam();
		param.setValueAsString("gt2016-06-09T20:38:14.591-05:00");

		assertEquals(ParamPrefixEnum.GREATERTHAN, param.getPrefix());
		assertEquals("2016-06-09T20:38:14.591-05:00", param.getValueAsString());

		ourLog.info("PRE:  " + param.getValue());
		ourLog.info("PRE:  " + param.getValue().getTime());
		InstantDt dt = new InstantDt(new Date(param.getValue().getTime()));
		dt.setTimeZone(TimeZone.getTimeZone("America/Toronto"));
		ourLog.info("POST: " + dt.getValue());
		assertEquals("2016-06-09T21:38:14.591-04:00", dt.getValueAsString());
	}

	@Test
	public void testParseMinutePrecision() {
		DateParam param = new DateParam();
		param.setValueAsString("2016-06-09T20:38Z");

		assertEquals(null, param.getPrefix());
		assertEquals("2016-06-09T20:38Z", param.getValueAsString());

		ourLog.info("PRE:  " + param.getValue());
		ourLog.info("PRE:  " + param.getValue().getTime());

		InstantDt dt = new InstantDt(new Date(param.getValue().getTime()));
		dt.setTimeZone(TimeZone.getTimeZone("America/Toronto"));
		ourLog.info("POST: " + dt.getValue());
		assertEquals("2016-06-09T16:38:00.000-04:00", dt.getValueAsString());
	}

	@Test
	public void testParseMinutePrecisionWithoutTimezone() {
		DateParam param = new DateParam();
		param.setValueAsString("2016-06-09T20:38");

		assertEquals(null, param.getPrefix());
		assertEquals("2016-06-09T20:38", param.getValueAsString());

		ourLog.info("PRE:  " + param.getValue());
		ourLog.info("PRE:  " + param.getValue().getTime());

		InstantDt dt = new InstantDt(new Date(param.getValue().getTime()));
		dt.setTimeZone(TimeZone.getTimeZone("America/Toronto"));
		ourLog.info("POST: " + dt.getValue());
		assertThat(dt.getValueAsString(), startsWith("2016-06-09T"));
		assertThat(dt.getValueAsString(), endsWith("8:00.000-04:00"));
	}

	@Test
	public void testParseMinutePrecisionWithPrefix() {
		DateParam param = new DateParam();
		param.setValueAsString("gt2016-06-09T20:38Z");

		assertEquals(ParamPrefixEnum.GREATERTHAN, param.getPrefix());
		assertEquals("2016-06-09T20:38Z", param.getValueAsString());

		ourLog.info("PRE:  " + param.getValue());
		ourLog.info("PRE:  " + param.getValue().getTime());

		InstantDt dt = new InstantDt(new Date(param.getValue().getTime()));
		dt.setTimeZone(TimeZone.getTimeZone("America/Toronto"));
		ourLog.info("POST: " + dt.getValue());
		assertEquals("2016-06-09T16:38:00.000-04:00", dt.getValueAsString());
	}
	
	@Test
	public void testParseLegacyPrefixes() {
		assertEquals(ParamPrefixEnum.APPROXIMATE, new DateParam("ap2012").getPrefix());
		assertEquals(ParamPrefixEnum.GREATERTHAN, new DateParam("gt2012").getPrefix());
		assertEquals(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, new DateParam("ge2012").getPrefix());
		assertEquals(ParamPrefixEnum.LESSTHAN, new DateParam("lt2012").getPrefix());
		assertEquals(ParamPrefixEnum.LESSTHAN_OR_EQUALS, new DateParam("le2012").getPrefix());
	}
	

}

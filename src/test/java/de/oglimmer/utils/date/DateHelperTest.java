package de.oglimmer.utils.date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DateHelperTest {

	@Test
	public void dayHours() {
		Date from = constructDate(2016, 8, 21, 9, 3, 35);
		Date to = constructDate(2016, 8, 22, 12, 13, 30);
		String result = DateHelper.formatDateDifference(from, to);
		assertThat(result, is("1 day(s), 3 hour(s)"));
	}

	@Test
	public void hoursMinutes() {
		Date from = constructDate(2016, 8, 22, 1, 3, 35);
		Date to = constructDate(2016, 8, 22, 4, 13, 30);
		String result = DateHelper.formatDateDifference(from, to);
		assertThat(result, is("3 hour(s), 9 minute(s)"));
	}

	@Test
	public void minutesSeconds() {
		Date from = constructDate(2016, 8, 22, 12, 3, 35);
		Date to = constructDate(2016, 8, 22, 12, 13, 30);
		String result = DateHelper.formatDateDifference(from, to);
		assertThat(result, is("9 minute(s), 55 second(s)"));
	}

	@Test
	public void seconds() {
		Date from = constructDate(2016, 8, 22, 12, 13, 17);
		Date to = constructDate(2016, 8, 22, 12, 13, 30);
		String result = DateHelper.formatDateDifference(from, to);
		assertThat(result, is("13 second(s)"));
	}

	private Date constructDate(int year, int month, int date, int hourOfDay, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date, hourOfDay, minute, second);
		return cal.getTime();
	}

}

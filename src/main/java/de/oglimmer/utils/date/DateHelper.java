package de.oglimmer.utils.date;

import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	private DateHelper() {

	}

	public static String formatDateDifference(Date d1, Date d2) {
		long[] td = DateHelper.getTimeDifference(d1, d2);
		if (td[0] > 0) {
			return td[0] + " day, " + td[1] + " hours";
		}
		if (td[1] > 0) {
			return td[1] + " hours, " + td[2] + " minutes";
		}
		if (td[2] > 0) {
			return td[2] + " minutes, " + td[3] + " seconds";
		}
		return td[3] + " seconds";
	}

	public static long[] getTimeDifference(Date d1, Date d2) {
		long[] result = new long[5];

		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		long t1 = cal.getTimeInMillis();

		cal.setTime(d2);

		long diff = Math.abs(cal.getTimeInMillis() - t1);
		final int ONE_DAY = 1000 * 60 * 60 * 24;
		final int ONE_HOUR = ONE_DAY / 24;
		final int ONE_MINUTE = ONE_HOUR / 60;
		final int ONE_SECOND = ONE_MINUTE / 60;

		long d = diff / ONE_DAY;
		diff %= ONE_DAY;

		long h = diff / ONE_HOUR;
		diff %= ONE_HOUR;

		long m = diff / ONE_MINUTE;
		diff %= ONE_MINUTE;

		long s = diff / ONE_SECOND;
		long ms = diff % ONE_SECOND;
		result[0] = d;
		result[1] = h;
		result[2] = m;
		result[3] = s;
		result[4] = ms;

		return result;
	}
}

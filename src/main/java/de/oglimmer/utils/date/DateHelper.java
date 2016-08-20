package de.oglimmer.utils.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Calculates a human readable representation of a date/time difference.
 * 
 * @author Oli Zimpasser
 *
 */
public class DateHelper {

	private DateHelper() {
		// no code here
	}

	/**
	 * Calculates a human readable date/time difference.
	 * 
	 * @param d1
	 *            a starting date
	 * @param d2
	 *            an end date
	 * @return a string which reads like x days, y hours, z minutes ...
	 */
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

	/**
	 * Calculates the date/time difference of two date object.
	 * 
	 * @param d1
	 *            a starting date
	 * @param d2
	 *            an end date
	 * @return an array where the 0th element has the number of days, the 1st element the number of hours, the 2nd
	 *         element the number of minutes, the 3rd element the number of seconds, the 4th element the number of
	 *         millis.
	 */
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

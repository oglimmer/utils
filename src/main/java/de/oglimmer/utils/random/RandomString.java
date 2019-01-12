package de.oglimmer.utils.random;

import java.util.Random;

/**
 * Creates various random strings while the user can define length and characters.
 * 
 * @author Oli Zimpasser
 */
final public class RandomString {

	private RandomString() {
		// no code here
	}

	private static final Random RAN = new Random(System.currentTimeMillis());

	/**
	 * Creates a size byte long unicode string. All codes are &gt; 32.
	 * 
	 * @param size
	 *            number of characters in the return string
	 * @return a random string build from unicode characters
	 */
	public static String getRandomStringUnicode(final int size) {
		final StringBuilder buff = new StringBuilder(size);

		for (int i = 0; i < size; i++) {
			buff.append((char) (RAN.nextInt(65503) + 32));
		}
		return buff.toString();
	}

	/**
	 * Creates a size byte long unicode string. All codes are between 32 and 255
	 * 
	 * @param size
	 *            number of characters in the return string
	 * @return a random string build from ASCII 32 ... 255
	 */
	public static String getRandomString8Bit(final int size) {
		final StringBuilder buff = new StringBuilder(size);

		for (int i = 0; i < size; i++) {
			buff.append((char) (RAN.nextInt(223) + 32));
		}
		return buff.toString();
	}

	/**
	 * Creates a size byte long unicode string. All codes are between a..z and A..Z and 0..9
	 * 
	 * @param size
	 *            number of characters in the return string
	 * @return a random string build from a..z and A..Z and 0..9
	 */
	public static String getRandomStringASCII(final int size) {
		return getRandomString(size, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
	}

	/**
	 * Creates a size byte long unicode string. All codes are from the set "stringSet"
	 * 
	 * @param size
	 *            number of characters in the return string
	 * @param stringSet
	 *            set of characters used for string creation
	 * @return a random string build from "stringSet"
	 */
	public static String getRandomString(final int size, final String stringSet) {

		final StringBuilder buff = new StringBuilder(size);

		for (int i = 0; i < size; i++) {
			final char nextChar = stringSet.charAt(RAN.nextInt(stringSet.length()));
			buff.append(nextChar);
		}
		return buff.toString();
	}

	/**
	 * Creates a size byte long unicode string. All codes are 0..9 and a..f.
	 * 
	 * @param size
	 *            number of characters in the return string
	 * @return a random string build from 0..9 and a..f (a valid hex)
	 */
	public static String getRandomStringHex(final int size) {
		return getRandomString(size, "0123456789ABCDEF");
	}

}
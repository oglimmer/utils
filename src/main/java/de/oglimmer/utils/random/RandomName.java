package de.oglimmer.utils.random;

/**
 * Creates a random name which is build upon n parts, each part is a nato-alphabet word.
 * 
 * @author Oli Zimpasser
 *
 */
public class RandomName {

	private static final String[] NATOALPHABET = { "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf",
			"Hotel", "India", "Juliet", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo",
			"Sierra", "Tango", "Uniform", "Victor", "Whiskey", "X-Ray", "Yankee", "Zulu", "Zero", "One", "Two", "Three",
			"Four", "Five", "Six", "Seven", "Eight", "Nine" };

	/**
	 * Creates a random name.
	 * 
	 * @param parts
	 *            number of words in this name
	 * @return the generated name
	 */
	public static String getName(int parts) {
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < parts; i++) {
			if (buff.length() != 0) {
				buff.append('-');
			}
			buff.append(NATOALPHABET[(int) (Math.random() * NATOALPHABET.length)]);
		}
		return buff.toString();
	}

}

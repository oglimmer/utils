package de.oglimmer.utils.random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.junit.jupiter.api.Test;

public class StringTest {

	@Test
	public void getRandomStringTest() {
		String rndName = RandomString.getRandomString(10, "abcde");
		assertThat(rndName, matchesPattern("^[abcde]{10}$"));
	}

	@Test
	public void getRandomString8BitTest() {
		String rndName = RandomString.getRandomString8Bit(12);
		assertThat(rndName.length(), is(12));
	}

	@Test
	public void getRandomStringASCIITest() {
		String rndName = RandomString.getRandomStringASCII(14);
		assertThat(rndName, matchesPattern("^[A-Za-z0-9]{14}$"));
	}

	@Test
	public void getRandomStringHexTest() {
		String rndName = RandomString.getRandomStringHex(16);
		assertThat(rndName, matchesPattern("^[0123456789ABCDEF]{16}$"));
	}

	@Test
	public void getRandomStringUnicodeTest() {
		String rndName = RandomString.getRandomStringUnicode(18);
		assertThat(rndName.length(), is(18));
	}
}

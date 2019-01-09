package de.oglimmer.utils.random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.junit.jupiter.api.Test;

public class NameTest {

	@Test
	public void simpleTest() {
		String rndName = RandomName.getName(4);
		assertThat(rndName, matchesPattern("^\\w*-\\w*-\\w*-\\w*$"));
	}

}

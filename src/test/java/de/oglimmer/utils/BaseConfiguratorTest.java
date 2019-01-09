package de.oglimmer.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.SneakyThrows;

public class BaseConfiguratorTest {

	private static String tmpFile = System.getProperty("java.io.tmpdir") + "/BaseConfiguratorTest-logback.xml";
	private String logFile = System.getProperty("java.io.tmpdir") + "/utils-test.log";

	@SneakyThrows(value = { IOException.class })
	@BeforeAll
	public static void setup() {
		InputStream is = BaseConfiguratorTest.class.getResourceAsStream("/logback-custom.xml");
		Files.copy(is, Paths.get(tmpFile), StandardCopyOption.REPLACE_EXISTING);
		System.setProperty("utils-test-logback", tmpFile);
	}

	@SneakyThrows(value = { InterruptedException.class, IOException.class })
	@Test
	public void test() {
		String testString = "test-entry-foobar-eucnsbjizrgtajknidubhuirtnsakb";
		Logger log = LoggerFactory.getLogger(BaseConfiguratorTest.class);
		log.debug(testString);
		TimeUnit.SECONDS.sleep(1);
		byte[] logFileContent = Files.readAllBytes(Paths.get(logFile));
		assertThat(new String(logFileContent), containsString(testString));
	}

}

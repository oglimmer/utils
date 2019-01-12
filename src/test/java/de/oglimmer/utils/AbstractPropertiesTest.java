package de.oglimmer.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.json.stream.JsonParsingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TestProperties extends AbstractProperties {

	protected TestProperties(String systemPropertiesKey) {
		super(systemPropertiesKey);
	}

	protected TestProperties(String systemPropertiesKey, String defaultPropertyFile) {
		super(systemPropertiesKey, defaultPropertyFile);
	}

	protected TestProperties(String systemPropertiesKey, String defaultPropertyFile, boolean reload) {
		super(systemPropertiesKey, defaultPropertyFile, reload);
	}

	public String getValue1() {
		return getJson().getString("value1");
	}

	public String getValue2() {
		return getJson().getString("value2");
	}

}

public class AbstractPropertiesTest {

	@AfterEach
	public void reset() {
		System.clearProperty("foo.parameter");
	}

	@Test
	public void simpleTest() {
		TestProperties tp = new TestProperties("foo.parameter");
		assertThat(tp.getValue1(), is("in-default-a-default-value"));
		tp.shutdown();
	}

	@Test
	public void simple2ParamConstructorTest() {
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json");
		assertThat(tp.getValue1(), is("in-default"));
		tp.shutdown();
	}

	@Test
	public void simple3ParamConstructorTest() {
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json", false);
		assertThat(tp.getValue1(), is("in-default"));
	}

	@Test
	public void loadExternalFileMergeTestFail() throws IOException {
		File f = new File("/this-is-an-impossible-filename/34877892392757903784/384348923495294");
		if (f.exists()) {
			throw new RuntimeException("The impossible file exists " + f);
		}
		System.setProperty("foo.parameter", f.getAbsolutePath());
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json", false);
		assertThat(tp.getValue1(), is("in-default"));
	}

	@Test
	public void loadExternalFileMergeTest() throws IOException {
		File file = File.createTempFile("TestProperties", ".json");
		try (PrintStream out = new PrintStream(new FileOutputStream(file, true))) {
			out.print("{ \"value1\": \"from-external-file\" } ");
		}
		System.setProperty("foo.parameter", file.getAbsolutePath());
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json", false);
		assertThat(tp.getValue1(), is("from-external-file"));
	}

	@Test
	public void loadExternalFileNewTest() throws IOException {
		File file = File.createTempFile("TestProperties", ".json");
		try (PrintStream out = new PrintStream(new FileOutputStream(file, true))) {
			out.print("{ \"value2\": \"from-external-file\" } ");
		}
		System.setProperty("foo.parameter", file.getAbsolutePath());
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json", false);
		assertThat(tp.getValue1(), is("in-default"));
		assertThat(tp.getValue2(), is("from-external-file"));
	}

	@Test
	public void loadMemoryConfigTest() throws IOException {
		System.setProperty("foo.parameter", "memory:{ \"value1\": \"from-memory\" }");
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json", false);
		assertThat(tp.getValue1(), is("from-memory"));
	}

	@Test
	public void loadMemoryConfigTestFail() throws IOException {
		System.setProperty("foo.parameter", "memory:{ broken ;) }");
		assertThrows(JsonParsingException.class, () -> {
			new TestProperties("foo.parameter", "/default-test.json", false);
		});
	}

	@Test
	public void loadExternalFileReloadTest() throws IOException {
		File file = File.createTempFile("TestProperties", ".json");
		try (PrintStream out = new PrintStream(new FileOutputStream(file, true))) {
			out.print("{ \"value1\": \"from-external-file-1\" } ");
		}
		System.setProperty("foo.parameter", file.getAbsolutePath());
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json", true);
		try {
			// Wait so watch thread is started properly
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(tp.getValue1(), is("from-external-file-1"));
		Object waitMonitor = new Object();
		tp.registerOnReload(new Runnable() {
			@Override
			public void run() {
				synchronized (waitMonitor) {
					waitMonitor.notify();
				}
			}
		});
		try (PrintStream out = new PrintStream(new FileOutputStream(file, false))) {
			out.print("{ \"value1\": \"from-external-file-2\" } ");
		}
		try {
			synchronized (waitMonitor) {
				waitMonitor.wait(60 * 1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(tp.getValue1(), is("from-external-file-2"));
		tp.shutdown();
	}

	@Test
	public void registerReloadFailed() {
		TestProperties tp = new TestProperties("foo.parameter", "/default-test.json", false);
		assertThrows(RuntimeException.class, () -> {
			tp.registerOnReload(new Runnable() {
				@Override
				public void run() {
				}
			});
		});

	}

}

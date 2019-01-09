package de.oglimmer.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class TestProperties extends AbstractProperties {

	protected TestProperties(boolean reload) {
		super("foo.parameter", "/default-test.json", reload);
	}

	public String getValue1() {
		return getJson().getString("value1");
	}

	public String getValue2() {
		return getJson().getString("value2");
	}

}

public class AbstractPropertiesTest {

	@Test
	public void simpleTest() {
		TestProperties tp = new TestProperties(false);
		assertThat(tp.getValue1(), is("in-default"));
	}

	@Test
	public void loadExternalFileMergeTest() throws IOException {
		File file = File.createTempFile("TestProperties", ".json");
		try (PrintStream out = new PrintStream(new FileOutputStream(file, true))) {
			out.print("{ \"value1\": \"from-external-file\" } ");
		}
		System.setProperty("foo.parameter", file.getAbsolutePath());
		TestProperties tp = new TestProperties(false);
		assertThat(tp.getValue1(), is("from-external-file"));

		System.clearProperty("foo.parameter");
	}

	@Test
	public void loadExternalFileNewTest() throws IOException {
		File file = File.createTempFile("TestProperties", ".json");
		try (PrintStream out = new PrintStream(new FileOutputStream(file, true))) {
			out.print("{ \"value2\": \"from-external-file\" } ");
		}
		System.setProperty("foo.parameter", file.getAbsolutePath());
		TestProperties tp = new TestProperties(false);
		assertThat(tp.getValue1(), is("in-default"));
		assertThat(tp.getValue2(), is("from-external-file"));

		System.clearProperty("foo.parameter");
	}

	@Test
	public void loadMemoryConfigTest() throws IOException {
		System.setProperty("foo.parameter", "memory:{ \"value1\": \"from-memory\" }");
		TestProperties tp = new TestProperties(false);
		assertThat(tp.getValue1(), is("from-memory"));

		System.clearProperty("foo.parameter");
	}

	@Test
	public void loadExternalFileReloadTest() throws IOException {
		File file = File.createTempFile("TestProperties", ".json");
		try (PrintStream out = new PrintStream(new FileOutputStream(file, true))) {
			out.print("{ \"value1\": \"from-external-file-1\" } ");
		}
		System.setProperty("foo.parameter", file.getAbsolutePath());
		TestProperties tp = new TestProperties(true);
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
		System.clearProperty("foo.parameter");
	}

}

package de.oglimmer.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractProperties {

	private static final boolean DEBUG = false;

	private String systemPropertiesKey;

	@Getter(value = AccessLevel.PROTECTED)
	private JsonObject json = Json.createObjectBuilder().build();
	private boolean running = true;
	private Thread propertyFileWatcherThread;
	private List<Runnable> reloadables = new ArrayList<>();
	private String sourceLocation;

	protected AbstractProperties(String systemPropertiesKey) {
		this.systemPropertiesKey = systemPropertiesKey;
		init();
	}

	protected JsonObject createExtraInitAttributes() {
		return json;
	}

	private void init() {
		loadDefaultProperties();
		sourceLocation = System.getProperty(systemPropertiesKey);
		if (sourceLocation != null) {
			try {
				if (sourceLocation.startsWith("memory:")) {
					String memoryConfigStr = sourceLocation.substring("memory:".length());
					try (InputStream is = new ByteArrayInputStream(memoryConfigStr.getBytes(StandardCharsets.UTF_8))) {
						mergeJson(is);
					}
				} else {
					try (InputStream fis = new FileInputStream(sourceLocation)) {
						mergeJson(fis);
					}
					if (propertyFileWatcherThread == null) {
						propertyFileWatcherThread = new Thread(new PropertyFileWatcher());
						propertyFileWatcherThread.start();
					}
				}
			} catch (IOException e) {
				log.error("Failed to load properties file " + sourceLocation, e);
			}
		}
		json = createExtraInitAttributes();
		if (DEBUG) {
			System.out.println("Used config: " + prettyPrint(json));
		}
	}

	private void loadDefaultProperties() {
		try (JsonReader rdr = Json.createReader(this.getClass().getResourceAsStream("/default.properties"))) {
			json = rdr.readObject();
			System.out.println("Successfully loaded properties from /default.properties");
		}
	}

	private String prettyPrint(JsonObject json) {
		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);

		StringWriter sw = new StringWriter();
		try (JsonWriter jsonWriter = writerFactory.createWriter(sw)) {
			jsonWriter.writeObject(json);

		}
		return sw.toString();
	}

	private void mergeJson(InputStream is) {
		try (JsonReader rdr = Json.createReader(is)) {
			JsonObject toBeMerged = rdr.readObject();
			json = merge(json, toBeMerged);
			System.out.println("Successfully loaded " + systemPropertiesKey + " from " + sourceLocation + " for merge");
		}
	}

	protected JsonObject merge(JsonObject base, JsonObject toOverwrite) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		for (Entry<String, JsonValue> entry : base.entrySet()) {
			String key = entry.getKey();
			if (toOverwrite.containsKey(key)) {
				job.add(key, toOverwrite.get(key));
			} else {
				job.add(key, entry.getValue());
			}
		}
		for (Entry<String, JsonValue> entry : toOverwrite.entrySet()) {
			String key = entry.getKey();
			// if JsonObjectBuilder would have a containsKey we could skip all
			// already existing keys
			job.add(key, toOverwrite.get(key));
		}
		return job.build();
	}

	public void registerOnReload(Runnable toCall) {
		reloadables.add(toCall);
	}

	void reload() {
		init();
		reloadables.forEach(Runnable::run);
	}

	public void shutdown() {
		running = false;
		if (propertyFileWatcherThread != null) {
			propertyFileWatcherThread.interrupt();
		}
	}

	class PropertyFileWatcher implements Runnable {

		public void run() {
			File toWatch = new File(sourceLocation);
			log.info("PropertyFileWatcher started");
			try {
				final Path path = FileSystems.getDefault().getPath(toWatch.getParent());
				try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
					path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
					while (running) {
						final WatchKey wk = watchService.take();
						for (WatchEvent<?> event : wk.pollEvents()) {
							// we only register "ENTRY_MODIFY" so the context is
							// always a Path.
							final Path changed = (Path) event.context();
							if (changed.endsWith(toWatch.getName())) {
								log.debug("{} changed => reload", toWatch.getAbsolutePath());
								reload();
							}
						}
						boolean valid = wk.reset();
						if (!valid) {
							log.warn("The PropertyFileWatcher's key has been unregistered.");
						}
					}
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
				log.error("PropertyFileWatcher failed", e);
			}
			log.info("PropertyFileWatcher ended");
		}
	}

}

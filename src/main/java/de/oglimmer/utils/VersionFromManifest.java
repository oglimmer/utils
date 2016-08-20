package de.oglimmer.utils;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.Getter;

public class VersionFromManifest {

	@Getter
	private String commit;
	@Getter
	private String version;
	@Getter
	private String creationDate;

	private String appName;

	public VersionFromManifest(String appName) {
		this.appName = appName;
	}

	public void init(InputStream inputStream) {
		try (InputStream is = inputStream) {
			Manifest mf = new Manifest(is);
			Attributes attr = mf.getMainAttributes();
			commit = attr.getValue("SVN-Revision-No");
			version = attr.getValue(appName + "-Version");
			long time = Long.parseLong(attr.getValue("Creation-Date"));
			creationDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(time));
		} catch (Exception e) {
			commit = "?";
			creationDate = "?";
			version = "?";
		}
	}

	public String getLongVersion() {
		return "V" + version + " [Commit#" + commit + "] build " + creationDate;
	}

}

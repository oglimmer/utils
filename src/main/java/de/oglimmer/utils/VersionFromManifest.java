package de.oglimmer.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.Getter;

public class VersionFromManifest {

	private static final int DATEFORMAT = DateFormat.FULL;

	@Getter
	private String commit;
	@Getter
	private String version;
	@Getter
	private String creationDate;
	@Getter
	private String gitUrl;

	public void init(InputStream inputStream) {
		try (InputStream is = inputStream) {
			Manifest mf = new Manifest(is);
			Attributes attr = mf.getMainAttributes();
			commit = attr.getValue("git-commit");
			gitUrl = attr.getValue("git-url");
			version = attr.getValue("project-version");
			long time = Long.parseLong(attr.getValue("creation-date"));
			creationDate = DateFormat.getDateTimeInstance(DATEFORMAT, DATEFORMAT).format(new Date(time));
		} catch (Exception e) {
			initBackToDefaults();
		}
	}

	public void initFromFile(String filename) {
		try (InputStream is = new FileInputStream(filename)) {
			init(is);
		} catch (IOException e) {
			initBackToDefaults();
		}
	}

	private void initBackToDefaults() {
		creationDate = DateFormat.getDateTimeInstance(DATEFORMAT, DATEFORMAT).format(new Date());
		commit = "?";
		version = "?";
		gitUrl = "?";
	}

	public String getLongVersion() {
		return "V" + version + " [<a href='" + gitUrl + "/commits/" + commit + "'>Commit#" + commit + "</a>] build "
				+ creationDate;
	}

}

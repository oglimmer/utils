package de.oglimmer.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.Getter;

/**
 * Helps to read attributes from a Manifest file and provides a uniform "version" string which has version, git-commit,
 * creation date and a html-link to git
 * 
 * @author Oli Zimpasser
 *
 */
public class VersionFromManifest {

	// CHECK: com.jcabi.manifests.Manifests

	private static final int DATEFORMAT = DateFormat.FULL;

	@Getter
	private String commit;
	@Getter
	private String version;
	@Getter
	private String creationDate;
	@Getter
	private String gitUrl;

	@Getter
	private boolean initFailed;

	/**
	 * Init with a given inputStream.
	 * 
	 * @param inputStream
	 *            to read the Manifest file from
	 */
	public void init(final InputStream inputStream) {
		try (final InputStream is = inputStream) {
			final Manifest mf = new Manifest(is);
			final Attributes attr = mf.getMainAttributes();
			commit = attr.getValue("git-commit");
			gitUrl = attr.getValue("git-url");
			version = attr.getValue("project-version");
			final long time = Long.parseLong(attr.getValue("creation-date"));
			creationDate = DateFormat.getDateTimeInstance(DATEFORMAT, DATEFORMAT).format(new Date(time));
		} catch (Exception e) {
			initBackToDefaults();
		}
	}

	/**
	 * Init with a given filename. This is usually called from ServletContextListener with
	 * sce.getServletContext().getRealPath("/META-INF/MANIFEST.MF")
	 * 
	 * @param filename
	 *            to read the Manifest file from
	 */
	public void initFromFile(final String filename) {
		try (final InputStream is = new FileInputStream(filename)) {
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
		initFailed = true;
	}

	/**
	 * Returns the version with all details
	 * 
	 * @return string with version, html-link to git, git commit hash and creation date
	 */
	public String getLongVersion() {
		return "V" + version + " [<a href='" + gitUrl + "/commits/" + commit + "'>Commit#" + commit + "</a>] build "
				+ creationDate;
	}

}

package de.oglimmer.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VersionFromManifestTest {

	@BeforeAll
	public static void setup() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private void checkSuccess(VersionFromManifest vfm) {
		assertThat(vfm.isInitFailed(), is(false));
		assertThat(vfm.getCreationDate(), is("Monday, November 26, 2018 at 8:40:45 PM Coordinated Universal Time"));
		assertThat(vfm.getCommit(), is("b88537541a84b2e76a37fcbbef7615bd06d0cc9c"));
		assertThat(vfm.getGitUrl(), is("https://github.com/oglimmer/utils"));
		assertThat(vfm.getVersion(), is("0.1-SNAPSHOT"));
		assertThat(vfm.getLongVersion(), is(
				"V0.1-SNAPSHOT [<a href='https://github.com/oglimmer/utils/commits/b88537541a84b2e76a37fcbbef7615bd06d0cc9c'>Commit#b88537541a84b2e76a37fcbbef7615bd06d0cc9c</a>] build Monday, November 26, 2018 at 8:40:45 PM Coordinated Universal Time"));
	}

	private void checkFailed(VersionFromManifest vfm) {
		assertThat(vfm.isInitFailed(), is(true));
		assertThat(vfm.getCommit(), is("?"));
		assertThat(vfm.getGitUrl(), is("?"));
		assertThat(vfm.getVersion(), is("?"));
		assertThat(vfm.getLongVersion(), startsWith("V? [<a href='?/commits/?'>Commit#?</a>] build "));
	}

	@Test
	public void testInit() {
		VersionFromManifest vfm = new VersionFromManifest();
		vfm.init(getClass().getResourceAsStream("/MANIFEST.MF"));
		checkSuccess(vfm);
	}

	@Test
	public void testInitFromFile() {
		VersionFromManifest vfm = new VersionFromManifest();
		File file = new File(getClass().getResource("/MANIFEST.MF").getPath());
		vfm.initFromFile(file.getAbsolutePath());
		checkSuccess(vfm);
	}

	@Test
	public void testInitFromFileFail() {
		VersionFromManifest vfm = new VersionFromManifest();
		File f = new File("/this-is-an-impossible-filename/34877892392757903784/384348923495294");
		if (f.exists()) {
			throw new RuntimeException("The impossible file exists " + f);
		}
		vfm.initFromFile("/this-is-an-impossible-filename/34877892392757903784/384348923495294");
		checkFailed(vfm);
	}

	@Test
	public void testInitFail() {
		VersionFromManifest vfm = new VersionFromManifest();
		vfm.init(null);
		checkFailed(vfm);
	}

}

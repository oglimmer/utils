package de.oglimmer.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.junit.jupiter.api.Test;

public class VersionFromManifestTest {

	private void check(VersionFromManifest vfm) {
		assertThat(vfm.isInitFailed(), is(false));
		assertThat(vfm.getCreationDate(), is("Monday, November 26, 2018 9:40:45 PM CET"));
		assertThat(vfm.getCommit(), is("b88537541a84b2e76a37fcbbef7615bd06d0cc9c"));
		assertThat(vfm.getGitUrl(), is("https://github.com/oglimmer/utils"));
		assertThat(vfm.getVersion(), is("0.1-SNAPSHOT"));
		assertThat(vfm.getLongVersion(), is(
				"V0.1-SNAPSHOT [<a href='https://github.com/oglimmer/utils/commits/b88537541a84b2e76a37fcbbef7615bd06d0cc9c'>Commit#b88537541a84b2e76a37fcbbef7615bd06d0cc9c</a>] build Monday, November 26, 2018 9:40:45 PM CET"));
	}

	@Test
	public void testInit() {
		VersionFromManifest vfm = new VersionFromManifest();
		vfm.init(getClass().getResourceAsStream("/MANIFEST.MF"));
		check(vfm);
	}

	@Test
	public void testInitFromFile() {
		VersionFromManifest vfm = new VersionFromManifest();
		File file = new File(getClass().getResource("/MANIFEST.MF").getPath());
		vfm.initFromFile(file.getAbsolutePath());
		check(vfm);
	}

}

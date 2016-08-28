package de.oglimmer.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.impl.StaticLoggerBinder;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * Implements SPI-based slf4j configuration. It changes the order and places where slf4j looks for configuration files.
 * The "APP_NAME" is given as a parameter in the constructor. Checks first the system parameter "APP_NAME"-logback for a
 * filename, if this parameter is not given or the file doesn't exist it looks in /etc/logback-custom.xml. If all this
 * doesn't exist it uses /logback-custom.xml in the $CLASSPATH. It also sets the slf4j variable "application-name" with
 * the "APP_NAME".
 *
 * @author Oli Zimpasser
 */
abstract public class BaseConfigurator extends ContextAwareBase implements ch.qos.logback.classic.spi.Configurator {

	private static final String CP_LOGBACK_CUSTOM_XML = "/logback-custom.xml";
	private static final String ETC_LOGBACK_CUSTOM_XML = "/etc/logback-custom.xml";

	final private String appName;

	/**
	 * @param appName
	 *            passed to the xml of a logfile as ${application-name} and used via System.getProperty as
	 *            "appName"-logback to define the app specific logback.xml
	 */
	public BaseConfigurator(final String appName) {
		this.appName = appName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.qos.logback.classic.spi.Configurator#configure(ch.qos.logback.classic.LoggerContext)
	 */
	@Override
	public void configure(final LoggerContext loggerContext) {
		addInfo("Setting up custom configuration.");
		final LoggerContext context = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
		final JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();
		context.putProperty("application-name", appName);
		openStream(jc);
	}

	private void openStream(final JoranConfigurator jc) {
		try (final ConfigurableInputStream is = new ConfigurableInputStream()) {
			if (is.isValid()) {
				is.configure(jc);
			} else {
				addError("NO LOGBACK-CUSTOM.XML FOUND! NO LOGGING INITIALIZED.");
			}
		}
	}

	class ConfigurableInputStream implements Closeable {

		private InputStream is;
		private File file;
		private boolean valid;

		public ConfigurableInputStream() {
			valid = false;
			lookSystemPropertyReferencedFile();
			if (!valid) {
				lookForEtcFile();
			}
			if (!valid) {
				lookForClasspathFile();
			}
		}

		public boolean isValid() {
			return valid;
		}

		@Override
		public void close() {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					addError("Failed to close InputStream.");
				}
			}
		}

		public void configure(JoranConfigurator jc) {
			try {
				if (file != null) {
					jc.doConfigure(file);
				} else if (is != null) {
					jc.doConfigure(is);
				}
			} catch (JoranException e) {
				addError("Failed to configure JoranConfigurator", e);
			}
		}

		/**
		 * 3rd level, last fall-back. This one cannot be reloadable.
		 * 
		 */
		private void lookForClasspathFile() {
			is = getClass().getResourceAsStream(CP_LOGBACK_CUSTOM_XML);
			if (is != null) {
				addInfo("Could find resource [CP:" + CP_LOGBACK_CUSTOM_XML + "]");
				valid = true;
			} else {
				addError("Could NOT find resource [CP:" + CP_LOGBACK_CUSTOM_XML + "]");
			}
		}

		/**
		 * 
		 */
		private void lookForEtcFile() {
			file = new File(ETC_LOGBACK_CUSTOM_XML);
			if (file.exists()) {
				addInfo("Could find resource [file:" + ETC_LOGBACK_CUSTOM_XML + "]");
				valid = true;
			} else {
				addInfo("Could NOT find resource [file:" + ETC_LOGBACK_CUSTOM_XML + "]");
				file = null;
			}
		}

		/**
		 * 1st level. Check if the System property is defined and points to a file.
		 */
		private void lookSystemPropertyReferencedFile() {
			final String systemProperty = System.getProperty(appName + "-logback");
			if (systemProperty != null) {
				addInfo("Could find resource reference [-D" + appName + "-logback = " + systemProperty + "]");
				file = new File(systemProperty);
				if (file.exists()) {
					addInfo("Could find resource file [file:" + systemProperty + "]");
					valid = true;
				} else {
					addError("File file:" + systemProperty + " does NOT exist.");
					file = null;
				}
			} else {
				addInfo("Could NOT find resource reference [-D" + appName + "-logback]");
			}
		}
	}
}

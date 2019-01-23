# Project: de.oglimmer.utils

The library helps to prevent boilerplate code for random strings and names, SPI-based slf4j configuration, sophisticated property files, human readable representation of date differences and getting attributes from MANIFEST.FM files.

# JavaDoc / Binaries

Download/use the latest binaries from [central maven repository](https://search.maven.org/artifact/de.oglimmer.utils/common-utils)

The javadoc can be found at [here](http://www.javadoc.io/doc/de.oglimmer.utils/common-utils/)

Travis-ci.org build: <a href="https://travis-ci.org/oglimmer/utils"><img src="https://travis-ci.org/oglimmer/utils.svg?branch=master"/></a>

CodeClimate: <a href="https://codeclimate.com/github/oglimmer/utils/maintainability"><img src="https://api.codeclimate.com/v1/badges/87f56eaead155a43aae7/maintainability" /></a> <a href="https://codeclimate.com/github/oglimmer/utils/test_coverage"><img src="https://api.codeclimate.com/v1/badges/87f56eaead155a43aae7/test_coverage" /></a>

License: <a href="https://app.fossa.io/projects/git%2Bgithub.com%2Foglimmer%2Futils?ref=badge_shield" alt="FOSSA Status"><img src="https://app.fossa.io/api/projects/git%2Bgithub.com%2Foglimmer%2Futils.svg?type=shield"/></a>

# Detailed information with examples

## Class: de.oglimmer.utils.VersionFromManifest

Helps to read attributes from a Manifest file and provides a uniform "version" string which has version, git-commit, creation date and a html-link to git

An [example project](https://github.com/oglimmer/sample-VersionFromManifest) is on Github.

1.) add this to your pom.xml, to add the information into the MANIFEST.FM file:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <version>2.6</version>
    <executions>
        <execution>
            <goals>
                <goal>manifest</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <archive>
            <manifestEntries>
                <git-commit>${buildNumber}</git-commit>
                <git-url>${project.scm.url}</git-url>
                <creation-date>${timestamp}</creation-date>
                <project-version>${project.version}</project-version>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>
```
         
2.) add this to your pom.xml to retrieve the git commit hash:

```
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>buildnumber-maven-plugin</artifactId>
    <version>1.4</version>
    <executions>
        <execution>
            <phase>validate</phase>
            <goals>
                <goal>create</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <shortRevisionLength>7</shortRevisionLength>
        <doCheck>false</doCheck>
        <doUpdate>false</doUpdate>
    </configuration>
</plugin>
```
         
3.) use a ServletContextListener.contextInitialized(ServletContextEvent) to get a proper version string

```
VersionFromManifest vfm = new VersionFromManifest();
vfm.initFromFile(sce.getServletContext().getRealPath("/META-INF/MANIFEST.MF"));
longVersion = vfm.getLongVersion();
```
         
## Class: de.oglimmer.utils.BaseConfigurator

Implements SPI-based slf4j configuration. It changes the order and places where slf4j looks for configuration files. The "APP\_NAME" is given as a parameter in the constructor. Checks first the system parameter "APP\_NAME"-logback for a filename, if this parameter is not given or the file doesn't exist it looks in /etc/logback-custom.xml. If all this doesn't exist it uses /logback-custom.xml in the $CLASSPATH. It also sets the slf4j variable "application-name" with the "APP\_NAME".

1. Create a class in your project and extend it from de.oglimmer.utils.BaseConfigurator
2. Have a parameter less constructor and set your application name in super("....");
3. Create META-INF/services/ch.qos.logback.classic.spi.Configurator under src/main/resources
4. Put the full qualified filename of your class from (1) into that file
5. make sure you have a logback-custom.xml in your project (and not logback.xml)

## Class: de.oglimmer.utils.AbstractProperties

A base class for application property classes. Supports json-based config files, automatic reloads when the file changes, merging in-classpath and out-of-classpath files and can take the whole configuration from a string to support unit test configurations.

1. Create a (singleton) class in your project and extends it from de.oglimmer.utils.AbstractProperties
2. Have a parameter less constructor and pass the name of the system parameter to your production config file to super("...."), e.g. super("my\_app")
3. create as many getters as you need like

	```
	public String getSmtpUser() {
	  return getJson().getString("smtp.user");
	}
	```
         
4. put a default.properties into src/main/resources. this needs to be a json file and should hold all default/development values for all properties
5. On your production host start the server with -Dmy\_app=/etc/my\_app.properties
Contents

--------

   1. Introduction
   2. Layout
   3. Prerequisites
   4. Getting Started
   5. Build Management
   6. Database
   7. Source Control
   8. Issue Tracking
   9. Developing with Eclipse
   10. Static Analysis
   11. Testing

Introduction
---------------------------

caArray2 is a microarray repository, allowing researchers to share microarray experiment annotations and data. It is a
J2EE web application, built with EJB3, Hibernate, Struts2 and several other open source technologies. It includes a
"Java API" (a set of remote EJBs), and a caGrid API.

Layout
------------------------

This section describes how the project source is laid out on disk, and the major components.

/ &lt;root directory&gt;

	software/			Contains source code and related artifacts for the caArray webapp and services

		caarray-common.jar   	Set of classes used by both the business (EJB) tier, the Web tier, and the Grid service implementation.
 								Contains the internal domain model classes, the DAO classes, the external (service) data classes, and various
 								utility classes. This also has the MAGE-TAB parsing framework.

		caarray-ejb.jar			The business tier. Contains local EJBs implementing the business functions used
 								by the web tier, as well as remote EJBs implementing the "Java API" that can be used by remote clients.

 		caarray.war				The web tier. Contains Struts2 action classes, JSP pages, and various configuration files implementing the
			 					web UI.

		caarray.ear				Contains various configuration files needed to assemble the caarray2 EAR out of the other components

 		caarraydb				Contains various database population scripts, as well as database migration scripts for upgrading from
 								previous versions of the application.

		caarray-client.jar		Supporting classes and configuration files for the caArray client JAR, which contains everything needed to
								access the caArray API (both grid and java).

		test					Contains selenium and API (both grid and java) tests.

		load-test				Contains JMeter load tests. These are currently out of date (06/30/09).

		grid/legacy				Contains the implementation of the "legacy" grid service. This directory is generated and managed by Introduce.

		grid/v1_0				Contains the implementation of version 1.0 of the "external" grid service.
								This directory is generated and managed by Introduce.

	docs/				Contains all the documentation for caArray. The general layout follows RUP practice. Selected important directories
						are highlighted below.

		analysis_and_design		Contains the architecture documentation. The models directory holds the EA UML models.

		requirements/use_cases	Contains the use cases and user stories. These are generally not maintained after initial implementation.

		end_user_documentation	Contains the end-user manuals. Both the sources (in Word), and finished copies (PDFs) are checked in here.

	test-data			Contains a number of sample data sets. These are used in some of the unit and integration tests, and can also be used
						for populating an application.

	api_client_examples Contains a set of example programs illustrating the use of the caArray API.

 	qa					Contains documentation of QA processes, QA test cases, and results of QA execution runs.

Prerequisites
------------------------

The following tools are needed for working on caArray2 code:

- JDK 1.6.0_20 (or the latest 1.6 release).
  - Set JAVA_HOME to your jdk location
  - Add JAVA_HOME/bin to your PATH variable
- Ant 1.7.0 or later.
  - Set ANT_OPTS to "-Xmx256m"
    Otherwise, you may get an OutOfMemoryException when running unit tests during report generation
  - Set ANT_HOME to your ant install directory
  - Add ANT_HOME/bin to your PATH variable
- MySQL 5.1.48.
  Should be downloaded and installed from the MySQL download site.
  - Set a root password (if not set during install). This will be referenced under the key database.system.password in
    /software/local.properties and your copy of /software/master_build/install.properties.  Steps to configure these
    files described in the next section. To set the password, you can execute:
    $ mysqladmin -u root password 'NEWPASSWORD'
- caGrid 1.5. This is optional, as it is needed only if you are working on grid services. This can be obtained from
  https://ncisvn.nci.nih.gov/svn/cagrid/branches/caGrid-1_5_release/Software/core/caGrid. Build from the source code
  distribution after patching per software/grid/legacy/core.patch.

Getting Started
------------------------

- Make sure you have installed and configured all of the prerequisites as described above.
- Check out caArray. Most likely, you will check out the trunk - see the Source Control section for URL locations.
  Below, we use $CAARRAY_HOME to refer to the location of the caArray checkout.
- Copy $CAARRAY_HOME/software/build/resources/my.cnf to the location from where MySQL reads its option files.
  This varies depending on OS, refer to http://dev.mysql.com/doc/refman/5.0/en/option-files.html.
  Alternatively, if you already have a MySQL my.cnf file, add the lines in the file above to it.
- Copy the "$CAARRAY_HOME/software/master_build/install.properties" file, rename it to whatever you wish, and configure
  it with values appropriate for your desired local deployment. You should at least edit:
    - application.base.path
    - dataStorage.fileSystem.baseDir (if any of the other dataStorage properties are set to file-system)
    - environment=local-dev
  The below options are optional to change for development builds but should be changed for all other builds:
    - database.system.user
    - database.system.password
    - database.name
    - database.user
    - database.password
- Create and configure a "$CAARRAY_HOME/software/build/local.properties" file. The purpose of this file is to allow
  developers to override property values from $CAARRAY_HOME/software/build/default.properties file and thus prevent
  accidental check-in of $CAARRAY_HOME/software/build/default.properties, so localize your environment in
  $CAARRAY_HOME/software/build/local.properties rather than $CAARRAY_HOME/software/build/default.properties.
  + Database setup (values should be same as in install.properties copy above):
    - application.base.path
    - dataStorage.fileSystem.baseDir
    - database.system.user
    - database.system.password
    - database.server
    - database.port
    - database.name
    - database.user
    - database.password
  + JBoss setup (values should be same as in install.properties copy above if they exist):
    - jboss.home set its value to be what is set for install.properties:
      ${application.path.path}/${jboss.relative.path}
       (e.g., application.base.path set to /usr/local/caarray, so jboss.home is set to
      /usr/local/caarray/app/jboss-5.1.0.GA-nci).
    - jboss.server.jndi.port
    - jboss.server.port
  + CAS Setup (if using Single Sign on)
    - single.sign.on.install
    - cas.server.hostname
    - cas.server.port
  In addition to the copied property values above, also look at the following properties for necessary changes:
  + Ivy Resolution. This will speed up ivy resolution.  Unset if dependencies change (or clean out your cache):
    - ivy.resolve.pessimistic=false
    - ivy.noclean=true
    - local.repo.dir (optional if you checked out the ivy repository. See Build Management below)
  + Grid Services
    - globoss.home
- Create the caArray DB schema (prerequisite for installation), copy and paste the below commands into the mysql terminal prompt.
  Make sure to replace db-user, db-password, and schema name with the values you set in local.properties and your copy of install.properties.
    $ mysql -u root -p
    mysql&gt; create schema caarraydb;
    mysql&gt; create user 'caarrayop';
    mysql&gt; grant all on caarraydb.* to 'db-user'@'%' identified by 'db-password' with grant option;
    mysql&gt; grant all on caarraydb.* to 'db-user'@'localhost' identified by 'db-password' with grant option;
    mysql&gt; flush privileges;
- To initialize the database, open a command prompt and execute
    $ cd $CAARRAY_HOME/software/build
    $ ant database:reinitialize
- To install the caArray application,
    $ cd $CAARRAY_HOME/software/master_build
    $ ant -Dproperties.file=&lt;absolute path to install.properties file copy&gt; deploy:local:install
  (replace "&lt;absolute path to install.properties file copy&gt;" with actual path)
  caArray will be installed locally and both caArray JBoss and grid service JBoss will be started automatically.

You can now access the application at http://${jboss.server.hostname}:${jboss.server.port}/caarray.
Default logins are
  caarrayuser/caArray2!
  caarrayadmin/caArray2!
*NOTE: Please change these passwords immediately for any non-development environment

The Grid services will be available at http://${grid.server.hostname}:${globoss.server.http.port}/wsrf/services/cagrid/CaArraySvc (Legacy)
and http://${grid.server.hostname}:${globoss.server.http.port}/wsrf/services/cagrid/CaArraySvc_v1_0 (External v. 1.0)

During your iterative development process for working on an issue, you can just deploy modified code to the caArray
installation by opening a command prompt at $CAARRAY_HOME/software/build and executing ant deploy, which will build the
caarray.ear file and copy it to your caArray JBoss server (defined by jboss.home property in
$CAARRAY_HOME/software/build/local.properties" file).

After deploying your code, you can restart jboss using:
  $ ant deploy:stop-jboss-servers
  $ ant deploy:start-jboss-servers


Build Management
-----------------------

caArray uses Ant for builds. There are two separate build scripts:

software/build/build.xml is used for local (development) builds, and deploying code while doing iterative development on an issue.
software/master_build/build.xml is used for creating the GUI and command-line installers, and deploying to the NCIA tiers, as well as remote and local installs and upgrades.

The local build script is documented here.

You can execute "ant -p" for a full list of ant targets to run. A few of the key ones are:

* clean - cleans up all generated artifacts (jar, war, ear files, etc).
* deploy - deploys the caarray.ear, grid services, and updates the database.
* deploy:caarray.ear - builds the EAR file for the caArray webapp, and deploys it into JBoss.
* deploy:caarray-grid-svc - builds and deploys all the caArray grid services (Legacy and External). depending on value of
  the "grid.useJBoss" build property, the grid services are deployed to either JBoss or Globus.
* deploy:copy-jsp - copies JSPs and other web assets (CSS, Javascript, etc) to the caArray deployment inside JBoss, without
  redeploying the overall webapp.
* database:dropAll - cleans out the database by dropping all tables and data
* database:update - brings the database up to date by running any necessary schema, population and update scripts. Can be
run on a database in any state, including a blank one
* database:reinitialize - resets the database to the initial state (up-to-date schema and initial dataset) by calling database:dropAll and database:update
* database:recreate-database - drops the caArray database and user and then creates them again. Should be run when changes
  to the caArray schema are made, because otherwise the database:reinitialize command may fail.
* test - runs the JUnit test suite.
* check - runs static analysis tools on the code base (currently, Checkstyle and FindBugs).
* continuous-integration - the defult target, it is invoked by just running ant without specifying a target from the software/build directory. Execution of this target cleans and builds the application,
  runs the static analysis checks, and runs the JUnit test suite.
* nightly-build - this target cleans and builds the application and runs the selenium and API test suite.

There are a number of build properties used by the build script. Some are defined in software/build/build.xml, and others in
software/build/default.properties. As a rule, those build properties which may need to be overridden are put in the latter.

The build script will also attempt to read properties from software/build/local.properties. This is the place where you should
put properties specific to your local checkout. They will take precedence over the defaults. This file should never be checked into
subversion, and is set to be svn:ignored.

Another important aspect is that caArray uses Ivy for dependency management. The ivy settings file is at software/ivy-caarray-settings.xml,
and the ivy project file is at software/ivy-caarray.xml. All dependencies should be configured in this latter file. caArray references the
NCICB ivy repository, at http://gforge.nci.nih.gov/svnroot/commonlibrary/trunk/ivy-repo.

It is recommended, for quicker builds, to check out this ivy repository to a local directory (this requires about 0.75 GB of hard drive space).
Then, set the "local.repo.dir" build property to point to this directory. This will make the build fetch dependencies from your local copy first,
but will fall back upon the canonical repository if it cannot find it there. Doing this (and ensuring your local copy is up to date) is required
in order to be able to build caArray without Internet connectivity.

Database
------------------------
We use MySQL as the database for caArray, Hibernate as the ORM tool to persist our domain model in the database, and
Liquibase to manage database upgrades.

The Liquibase paradigm is that database population always proceeds by starting from a baseline schema, and then applying
a set of upgrade scripts corresponding to changes made to the domain model and/or initial population scripts. The ant target
"database:update" will bring a database in any state up-to-date. Liquibase maintains a set of which upgrade scripts have been
run, so this target can be run multiple times and upgrade scripts will not be run twice.

The database upgrade scripts are under software/caarraydb/liquibase, organized by version. The db-upgrade.xml is the master
changelog file, which then references db-*.xml files for each version, which in turn reference individual changesets.
A changeset should correspond to a single GForge issue and should be named for that issue.

So, whenever making changes to the data model that will result in changes to the database schema, the process to do so is:
- make appropriate changes to the hibernate-annotated domain classes
- optionally, run "ant database:generate-schema-sql". This will use hibernate to generate a schema creation script corresponding to the
  current data model. This is not necessary but will probly make it easier to write the upgrade script
- create an upgrade script, named for the gforge issue, in the appropriate directory, and add a changeset to the appropriate
db-*.xml file referencing the script. You can use the schema file from the previous step to help with writing the script.
- run ant database:update to apply the script.
- deploy and run your code / unit tests as appropriate.

Source Control
------------------------

The git repository URL is https://github.com/NCIP/caarray. The mainline of development is under the "master" branch.

Branches are created as needed under https://github.com/NCIP/caarray/branches for work on previous or future versions of
caArray in parallel with mainline development. Also, developers should create their own private branches here for work that spans
more than 1-2 days.

Tags are created under https://github.com/NCIP/caarray/tags for each milestone, release candidate and GA release.

Use the following to download the repository and retrieve a specific tag or branch.
$ git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ed8a8499ad8a849985988fc38e8280">[email&#160;protected]</a>:NCIP/caarray.git
$ git checkout TagOrBranchName

Issue Tracking
------------------------

caArray uses JIRA for tracking bugs, feature requests, and project management.

https://tracker.nci.nih.gov/browse/ARRAY

Developing with Eclipse
------------------------

caArray includes configuration files making it easy to use Eclipse for development. The configuration files are for the Eclipse 3.5
series, but should work with other versions as well.

To import the caArray into Eclipse, simply invoke File-&gt;Import, then select General-&gt;Existing Projects into Workspace. In the resulting dialog
box, select the "choose root directory" option and point it at the base directory of the caArray checkout.

You must install the IvyDE plugin, which is required for Ivy integration.

We also recommend using the JEE distribution of Eclipse with the following plugins:
- Subversive or Subclipse (for Subversion integration)
- Checkstyle (v4.4.3. Current caarray_checks.xml is incompatible with v5+)
- PMD

The Checkstyle and PMD plugins should then be configured to use the caArray rulesets (see static analysis section).

To debug caArray from Eclipse, do the following:
- Edit [jboss.home]/bin/run.conf (run.conf.bat for Windows) and uncomment the line for remote socket debugging.  It will look something like
JAVA_OPTS="$JAVA_OPTS -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"
- Start jboss
- From Eclipse
  - Run &gt; Debug Configurations &gt; Remote Java Application
  - Click "New" button and set the following
    - Project = caArray2
    - Host = localhost
    - Port = 8787 (or whatever you specified in run.conf)
  - Click Debug

To run or debug one of the JUnit tests from Eclipse, do the following:
1. From ${project_home}/software/build run the ant target
    ant configure-eclipse-for-unit-testing
2. Open Eclipse or refresh the whole directory tree in the Project Explorer
3. Select File &gt; Import, then Run/Debug &gt; Launch Configurations, Next
4. Navigate to ${project_home}/software/test/eclipse, select "eclipse", Finish
5. From the Run &gt; Run Configurations... or Run &gt; Debug Configurations... navigate to JUnit &gt; ProjectDaoTest
6. Run this configuration and observe the test output. The important part of the launch configuration settings is specified under
the Classpath tab, all entries there and their order are important to make it work
7. If the different JUnit run configuration is needed Contlol-click on ProjectDaoTest and duplicate it, then change fields from the
first tab "Test": Name, Test class, and if needed Test method to reflect the test being set up.
Please note that the configuration being imported in steps 3..4 relies on the default caArray Eclipse project name of "caArray2".
If the project was renamed ${project_home}/software/test/eclipse/ProjectDaoTest.launch must be edited by performing global replacement of
string "caArray2" inside it with the new Eclipse project name. This must precede step 3 of this instructions.

Static Analysis
------------------------

caArray uses static analysis tools to ensure code quality and conformance to standards. Currently we use Checkstyle and PMD.

The rulesets for these are in software/build/resources/caarray_checks.xml and software/build/resources/pmd-ruleset.xml respectively.

Testing
------------------------

caArray uses JUnit for unit testing. To run the unit test, execute "ant test" from software/build.
If you want to run a specific module, you can use one of the following ant targets:
- test:junit-caarray-common.jar
- test:junit-caarray-ejb.jar,
- test:junit-caarray.war,
- test:junit-caarray-plugins,
- test:junit-cn2magetab,
- test:junit-report,

If you want to be even more specific and run only selected test files, you may edit local.properties and add:
- test.source.include (e.g. test.source.include=**/IlluminaFileImportIntegrationTest.java)
- test.source.exclude

Setting up Local CAS SSO Server
----------------------------------------
1.) Download the CAS SSO Source from http://www.jasig.org/cas
2.) Unzip the CAS archive to a working directory, ex. &lt;USER DIRECTORY&gt;/CAS
3.) Download the Tomact application server from http://tomcat.apache.org/ and unzip it to a working directory, ex. &lt;USER DIRECTORY&gt;/Tomcat
    -From now on we will refer to the &lt;Tomcat install dir&gt;/apache-tomcat-version (The base directory of the tomcat server) as $CATALINA_HOME
4.) Next we will want to generate an SSL certificate so that the caArray application can communicate to the cas server via https
    -Make sure the $JAVA_HOME/bin directory is in your PATH, otherwise all references to keytool must be prefixed with $JAVA_HOME/bin/
  1. keytool -genkey -alias tomcat -keyalg RSA [-keystore \path\to\my\keystore]
    * if keytool is already in your path you can leave off the full path to the utility
    * The keystore argument is optional and points to where you want to put this new keystore, all directories must exist. If you do not specify this argument the keystore will exist as .keystore in your User directory
    * Note: The first question you are asked is "What is your first and last name?" you MUST enter the localhost for this.
    * When creating the keystore you will be asked for a password, the default is "changeit"
  2. Now that we have our certificate created we want to export it from its keystore, we do this by executing:
  keytool -export -alias tomcat -file &lt;Insert Export File Here&gt; -keystore &lt;Path to Keystore here&gt;
    * You can leave off the keystore argument if you are using the default keystore location
    * the file argument seems to be any sort of file, I named mine cert.cer and it worked fine.
  3. Finally we want to import the created certificate into the main java certificates so the Jboss server has access to it. We do this by the following command:
  keytool -import -alias tomcat -keystore $JAVA_HOME/lib/security/cacerts -file &lt;Export File Name&gt;
    * You may have issues running this command as you might not have write permissions to the JDK certificate location, in that case you should run it preceeding with the sudo command (*Nix) or run your terminal as administrator (Windows).
    * The Export file name is what was indicated in step 2
5.) Open the server.xml file found at $CATALINA_HOME/conf/server.xml and add the following configuration:
    &lt;Connector port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           keystoreFile="&lt;PATH TO KEYSTORE&gt;/.keystore" keystorePass="&lt;PASSWORD from 4.1&gt;"
           clientAuth="false" sslProtocol="TLS"/&gt;
   -If you did not specify a directory for the keystore in 4.1 you can put ${user.home} instead.
6.) Next we want to set up an OpenDS LDAP so that our CAS server can authenticate. OpenDS can be found at http://opends.java.net/
7.) When installing, make note of the BaseDN selected, or you can set it to "dc=nci,dc=nih,dc=gov" to minimize changes needed.
8.) After OpenDS is installed, open the control panel by executing &lt;OPENDS_HOME&gt;/bin/control-panel
9.) Add new user accounts in the LDAP for users caarrayadmin and caarrayuser by
  1. Select the Manage Entries option from the left menu,
  2. Make sure your Base DN is selected in drop down menu and right click on it, Select "New User..."
  3. Enter user information for the new user
10.) Next we want to set up the CAS webapp to connect to our OpenDS LDAP, we do this by opening up the deployerConfigContext.xml found in the cas-server-webapp/src/main/WEB-INF directory.
  -  Replace the existing SimpleTestUsernamePasswordAuthenticationHandler definition with the following:
                &lt;bean class="org.jasig.cas.adaptors.ldap.BindLdapAuthenticationHandler"&gt;
                    &lt;property name="filter" value="cn=%u" /&gt;
                    &lt;property name="searchBase" value="dc=nci,dc=nih,dc=gov" /&gt;    &lt;!-- value attribute should be equal to the defined Base DN in OpenDS--&gt;
                    &lt;property name="contextSource" ref="contextSource" /&gt;
                &lt;/bean&gt;
  - Additionally we need to define the contextSource, the information for connecting to the OpenDS LDAP, this will be defined and added in a section at the bottom of the file:
     &lt;bean id="contextSource" class="org.springframework.ldap.core.support.LdapContextSource"&gt;
          &lt;property name="pooled" value="false" /&gt;
          &lt;property name="url" value="ldap://localhost:1389" /&gt; &lt;!-- Port should be equal to LDAP port of OpenDS --&gt;
          &lt;property name="baseEnvironmentProperties"&gt;
               &lt;map&gt;
                    &lt;entry key="com.sun.jndi.ldap.connect.timeout" value="3000" /&gt;
                    &lt;entry key="com.sun.jndi.ldap.read.timeout" value="3000" /&gt;
                    &lt;entry key="java.naming.security.authentication" value="simple" /&gt;
               &lt;/map&gt;
          &lt;/property&gt;
     &lt;/bean&gt;
11.) We need to make sure that the webapp build includes the necessary LDAP module, we do this by editing the pom.xml found at the root of the cas-server-webapp folder and add the following dependency:
        &lt;dependency&gt;
            &lt;groupId&gt;${project.groupId}&lt;/groupId&gt;
            &lt;artifactId&gt;cas-server-support-ldap&lt;/artifactId&gt;
            &lt;version&gt;${project.version}&lt;/version&gt;
        &lt;/dependency&gt;
12.) Edit cas-server-webapp/src/main/webapp/WEB-INF/spring-configuration/ticketExpirationPolicies.xml. caArray requires 2 validations for each service ticket from CAS.
  - Find the bean with id="serviceTicketExpirationPolicy" and change the value of the first constructer-arg to "2".
        &lt;constructor-arg
            index="0"
            value="2" /&gt;
13.) Now we need to build the CAS webapp, this is done by executing the "mvn install" command from the cas-server-webapp directory.
14.) Build should be successful, now copy the cas.war file from the target directory to $CATALINA_HOME/webapps
15.) Start the CAS Tomcat server by executing $CATALINA_HOME/bin/startup, after startup you should be able to go to https://localhost:8443/cas/login and see the CAS login page and logging in should give you a "Login Successful" page.
16.) Make sure that the following properties are set up correctly in your local.properties:
    single.sign.on.install=true
    cas.server.hostname=localhost
    cas.server.port=8443

Deciding Login Method when Building caArray
---------------------------------------------
caArray's build allows for configuring the application to log in via database, LDAP, or CAS Single sign on. Since you can only have 1
type of sign on capability active when deploying the application you will need to set certain properties within the build properties (both install and local preferably) to
make sure that the application is set up correctly.
Database Sign on:
    *Make sure that the following properties are empty or false:
        -single.sign.on.install
        -ldap.authentication.enabled
    *Make sure that the following properties are set (they should be for other database operations already):
        -database.url
        -database.user
        -database.password
        -database.driver
LDAP Sign On:
    *Make sure that the following property are empty or false:
        -single.sign.on.install
    *Make sure that the following property is set to true:
        -ldap.authentication.enabled
    *Make sure that the following properties are set to the correct values:
        -ldap.url
        -ldap.searchbase
        -ldap.searchprefix
CAS Single Sign On:
    *Make sure that the following property is set to true:
        -single.sign.on.install
    *Make sure that the following properties are set to the correct values:
        -cas.server.hostname
        -cas.server.port
        -ldap.url
        -ldap.searchbase
        -ldap.searchprefix

Necessary Configuration for different Login Methods post deployment
---------------------------------------------------------------------
Occasionally after deploying the caArray system you might find a time where you would like to change from the login method that was selected during build / install.
You will need to open the .ear file that is created during build / install to make the modifications to certain files. This ear file should be able to be found at
$JBOSS_HOME/server/&lt;server name&gt;/deploy. You will need to unarchive the ear (using winzip or another tool) as well as the caarray.war file within to access the files.
All paths are relative to the unarchived ear file. Below you will find the necessary configuration sections that must be added / removed to have the specific build types enabled,
replace any necessary parameters (indicated by surrounding '@') with actual values:
-In the security-config.xml the SessionFixationProtectionLoginModule and the DatabaseServerLoginModule must always be the first and last modules defined (respectivately)
CAS Single Sign On:
    *caarray.war/src/main/webapp/WEB-INF/web.xml
        -Add the following sections to the web.xml file:
        -At the top of the file under the &lt;display-name&gt; tag:
            &lt;context-param&gt;
                &lt;param-name&gt;serverName&lt;/param-name&gt;
                &lt;param-value&gt;http://@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a2c8c0cdd1d18cd1c7d0d4c7d08ccacdd1d6ccc3cfc7e2">[email&#160;protected]</a>:@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8de7efe2fefea3fee8fffbe8ffa3fde2fff9cd">[email&#160;protected]</a>&lt;/param-value&gt;
            &lt;/context-param&gt;
            &lt;context-param&gt;
                &lt;param-name&gt;casServerLoginUrl&lt;/param-name&gt;
                &lt;param-value&gt;https://@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="adceccde83dec8dfdbc8df83c5c2ded9c3ccc0c8ed">[email&#160;protected]</a>:@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0a696b7924796f787c6f78247a65787e4a">[email&#160;protected]</a>/cas/login&lt;/param-value&gt;
            &lt;/context-param&gt;
            &lt;context-param&gt;
                &lt;description&gt;This parameter indicates to the application that single sign on is enabled and it should not show login fields.&lt;/description&gt;
                &lt;param-name&gt;ssoEnabled&lt;/param-name&gt;
                &lt;param-value&gt;true&lt;/param-value&gt;
            &lt;/context-param&gt;
        -Add the following right above the other &lt;filter&gt; tags (ARRAY-2453):
            &lt;filter&gt;
                &lt;filter-name&gt;CASWebAuthenticationFilter&lt;/filter-name&gt;
                &lt;filter-class&gt;gov.nih.nci.caarray.web.filter.CasWebAuthenticationFilter&lt;/filter-class&gt;
            &lt;/filter&gt;
            &lt;filter&gt;
                &lt;filter-name&gt;CASAuthenticationFilterGateway&lt;/filter-name&gt;
                &lt;filter-class&gt;gov.nih.nci.caarray.web.filter.CasAuthenticationFilter&lt;/filter-class&gt;
                &lt;init-param&gt;
                    &lt;param-name&gt;gateway&lt;/param-name&gt;
                    &lt;param-value&gt;true&lt;/param-value&gt;
                &lt;/init-param&gt;
                &lt;init-param&gt;
                    &lt;param-name&gt;excludePatterns&lt;/param-name&gt;
                    &lt;param-value&gt;.*/ajax/.*&lt;/param-value&gt;
                &lt;/init-param&gt;
            &lt;/filter&gt;
            &lt;filter&gt;
                &lt;filter-name&gt;CASAuthenticationFilter&lt;/filter-name&gt;
                &lt;filter-class&gt;org.jasig.cas.client.authentication.AuthenticationFilter&lt;/filter-class&gt;
            &lt;/filter&gt;
        -Add the following right above the other &lt;filter-mapping&gt; tags:
            &lt;filter-mapping&gt;
                &lt;filter-name&gt;CASWebAuthenticationFilter&lt;/filter-name&gt;
                &lt;url-pattern&gt;*&lt;/url-pattern&gt;
            &lt;/filter-mapping&gt;
            &lt;filter-mapping&gt;
                &lt;filter-name&gt;CASAuthenticationFilterGateway&lt;/filter-name&gt;
                &lt;url-pattern&gt;*.action&lt;/url-pattern&gt;
            &lt;/filter-mapping&gt;
            &lt;filter-mapping&gt;
                &lt;filter-name&gt;CASAuthenticationFilter&lt;/filter-name&gt;
                &lt;url-pattern&gt;/protected/*&lt;/url-pattern&gt;
                &lt;url-pattern&gt;/login.action&lt;/url-pattern&gt;
            &lt;/filter-mapping&gt;
    *META_INF/security-config.xml
        -Replace &lt;login-module&gt; tags referring to the CommonsLDAPLoginModule and CommonsDBLoginModule with the following:
            &lt;login-module code="gov.nih.nci.caarray.authentication.PasswordStackingCasLoginModule" flag="required"&gt;
                &lt;module-option name="ticketValidatorClass"&gt;org.jasig.cas.client.validation.Cas20ServiceTicketValidator&lt;/module-option&gt;
                &lt;module-option name="casServerUrlPrefix"&gt;https://@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="afcccedc81dccaddd9cadd81c7c0dcdbc1cec2caef">[email&#160;protected]</a>:@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="127173613c6177606477603c627d606652">[email&#160;protected]</a>/cas&lt;/module-option&gt;
                &lt;module-option name="service"&gt;http://@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="caa0a8a5b9b9e4b9afb8bcafb8e4a2a5b9bea4aba7af8a">[email&#160;protected]</a>:@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="cba1a9a4b8b8e5b8aeb9bdaeb9e5bba4b9bf8b">[email&#160;protected]</a>/caarray&lt;/module-option&gt;
                &lt;module-option name="defaultRoles"&gt;UserRole&lt;/module-option&gt;
                &lt;module-option name="roleAttributeNames"&gt;groupMembership&lt;/module-option&gt;
                &lt;module-option name="principalGroupName"&gt;CallerPrincipal&lt;/module-option&gt;
                &lt;module-option name="roleGroupName"&gt;Roles&lt;/module-option&gt;
                &lt;module-option name="cacheAssertions"&gt;true&lt;/module-option&gt;
                &lt;module-option name="tolerance"&gt;20000&lt;/module-option&gt;
                &lt;module-option name="cacheTimeout"&gt;480&lt;/module-option&gt;
            &lt;/login-module&gt;
LDAP Login:
    -Remove All CAS related configuration (if moving from CAS install)
    *META_INF//security-config.xml:
        -LDAP install provides failover to DB if a user is not in the LDAP
        &lt;login-module code="com.fiveamsolutions.nci.commons.authentication.CommonsLDAPLoginModule" flag="optional"&gt;
            &lt;module-option name="ldapHost"&gt;@ldap <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ed85829e99ad">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="ldapSearchableBase"&gt;@ldap search <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="573536243217">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="ldapUserIdLabel"&gt;@ldap user id <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4b272a292e270b">[email&#160;protected]</a>&lt;/module-option&gt;
        &lt;/login-module&gt;

        &lt;login-module code="com.fiveamsolutions.nci.commons.authentication.CommonsDBLoginModule" flag="optional" &gt;
            &lt;module-option name="driver"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="98fceaf1eefdead8">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="url"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e194938da1">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="user"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d5a0a6b0a7bbb4b8b095">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="passwd"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c7b7a6b4b4b0a8b5a387">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="query"&gt;SELECT * FROM csm_user WHERE login_name=? and password=?&lt;/module-option&gt;
            &lt;module-option name="encryption-enabled"&gt;YES&lt;/module-option&gt;
        &lt;/login-module&gt;
DB Login:
    -Remove All CAS related configuration (if moving from CAS install)
    *META_INF//security-config.xml:
        -Remove the CommonsLDAPLoginModule definition if necessary
        &lt;login-module code="com.fiveamsolutions.nci.commons.authentication.CommonsDBLoginModule" flag="required" &gt;
            &lt;module-option name="driver"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7e1a0c17081b0c3e">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="url"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f287809eb2">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="user"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="82f7f1e7f0ece3efe7c2">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="passwd"&gt;@database <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="91e1f0e2e2e6fee3f5d1">[email&#160;protected]</a>&lt;/module-option&gt;
            &lt;module-option name="query"&gt;SELECT * FROM csm_user WHERE login_name=? and password=?&lt;/module-option&gt;
            &lt;module-option name="encryption-enabled"&gt;YES&lt;/module-option&gt;
        &lt;/login-module&gt;


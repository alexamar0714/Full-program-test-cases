INSTALL INSTRUCTIONS
Build an environment
  * Download distribution from https://gforge.nci.nih.gov/svnroot/lsd/trunk/dist/caarray_distribution_[latest_version].zip
  * Transfer to server
  * Login as user who applications belongs
  * Verify java and ant version
    * ant -version # should contain (version 1.7.0)
    * java -version # should contain (build 1.5.0_10-b03)
  * extract zip to a working folder
  * edit carray2-install.properties replace any propery with "REPLACE" or "replace" in it
  * Run installer
    * ant
      * The installer may fail if properties cannot be verified (like connectivity testing)
      * Check ./logs/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4920273a3d28252564092d283d2c">[email&#160;protected]</a>@.log for full log of build.
  * Browse to http://localhost:${jboss.server.port}/caarray and browse some data to test
  * Save a copy of your properties somewhere so it can be used in the future

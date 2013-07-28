GroupProject
============

* Create a new workspace.  I created GroupProjectWorkspace.
* Import an 'Existing Project into Workspace' project.  Select groupproject.
* Import an 'Existing Project into Workspace' project.  Select google-play-services_lib.
* Right click on groupproject and click Properties.  Go to 'Project References' and check 'google-play-services_lib2'.
* Goto AVD Manager, and create a new AVD.  Choose a name, I created GroupProjectAVD.  Choose a Device.  I chose Nexus S.  As Target, select 'Google APIs (Google Inc.) - API Level 17'
* Create a new Debug Configuration for grouproject, using GroupprojectAVD.
* Run it

There is a bug with the Emulator that causes the location to not properly updated.  A few steps are necessary to get it to update.  Each time you want to change the GPS coordinates, you need to:
* Open the DDMS view
* Go to 'Emulator Control'
* Set a GPS coordinate and click 'Send'
* Go to Google Maps.  If it prompts you to enable Location, do it.  This has been enough for me to start receiving location updates.  However, every time you send a new location with 'Emulator Control' in the DDMS view, you have to re-open Google Maps.

When trying to use the 'Schools' feature, there is a bug with the Google API 17 and using the Maps APIv2 with Google Play Services.  To use the Schools feature, the following steps need to be performed.  Note that if using the Android API, not the Google API, Location and Geocoder will probably not work in the emulator.
* Create an emulator using the Android API 16 (4.2.1) lib  (not the Google API) and manually install the newest required packages via adb. (com.android.vending-20130716 and com.google.android.gms-20130716)
* See: http://stackoverflow.com/questions/13691943/this-app-wont-run-unless-you-update-google-play-services-via-bazaar/13869332#13869332
* (Does not affect actual devices, only emulators)
* The build target for the project must also be Android API 16 (4.2.1) and not Google API 17(4.2.2) (Project -> Properties -> Android)
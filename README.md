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
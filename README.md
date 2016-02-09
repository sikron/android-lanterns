# "Laterne & Laterne 3D" for Android

Two lanterns apps for Android. The one is just a simple app with 2D pictures and a little lighting The other is the 3D version with OpenGL, more realistic lighting and a little physics.

The app was created with IntelliJ and Android Maven Plugin. 

The app can be found in the Playstore https://play.google.com/store/apps/details?id=com.skronawi.laterne and https://play.google.com/store/apps/details?id=com.skronawi.laterne3d

# Prerequisites

- open your Android SDK and download API level 17 SDK Platform and Google APIs
- download also the Maps and USB from the extras
- now clone the https://github.com/simpligility/maven-android-sdk-deployer
- go into the maven-android-sdk-deployer directory and execute a `mvn install -P 4.2`. this will install the android runtime as dependency into your local maven repo and the android-maven-plugin can access it (see the pom.xml files)
- the apps were built with API level 17, but you can of course try with another level...

# WclDemoSample
This is a sample app that shows how one can set up an application to use the
WearCompanionLibrary. It has both a phone and a wear component. On your mobile, select
a page from the drawer and there, you can open the corresponding page on your
wear device by pressing on the FAB button at the lower right corner of the page. On your
wear device, you can select an item form the list when you run the app. Features
shown here include exchanging data, launching remote app, making HTTP calls from the wear
device, showing variations of WearableListView activities and recording voice on the wear
and streaming that to the mobile device and playing it there in real-time. In order for
each feature to work properly, you have to be on that feature's page on both the mobile app
and the wear app at the same time.

## Dependencies
* [WearCompanionLibrary (WCL)](https://github.com/googlesamples/android-WearCompanionLibrary)
* The phone module also depends on the [design support library](http://android-developers.blogspot.com/2015/05/android-design-support-library.html).
* Note: if you follow the instructions below, the WCL library will bring in other dependencies that
  are needed. If you choose to use WCL in a different way (for example using an archive version of
  that library), then you may need to include the dependencies that are listed for WCL as well.


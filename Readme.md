# Test Intercom App for Android

- Uses Intercom's Android SDK https://github.com/intercom/intercom-android to easily test the SDK functionality
- Enables testing all features of the mobile SDK without needing any server side changes (everything is self contained in the app)
- Several things this app does is not meant to be done in production, e.g.
   - Client side secure mode hash generation. The secret key should be contained on your server and be kept a secret
   - GCM API Key. This is used for sending test pushes messages and push notifications. This should never be needed in your real mobile app


## Usage
- Fill in settings page with all necessary data (at minimum the App ID and SDK API Key)
- Settings needed would be in Intercom: https://app.intercom.io/a/apps/_/settings/android
- On first usage you will need to completely close the app by closing it from the multi tasking view (swipe right/left) and reopening it
- After opening it, specify user details and login
- To verify you have logged in, select "Show Conversations" (from the page / menu). It should show you the conversation list (if there is an error it will say "Unable to load conversations")
- Details of the app are logged (using `Log.i()`) and viewable via logcat


## Testing GCM
- Go to https://developers.google.com/cloud-messaging/android/client and click "Get a configuration file" and follow the instructions (It should bring you to  https://developers.google.com/mobile/add?platform=android&cntapi=gcm&cnturl=https:%2F%2Fdevelopers.google.com%2Fcloud-messaging%2Fandroid%2Fclient&cntlbl=Continue%20Adding%20GCM%20Support&%3Fconfigured%3Dtrue and the `google-services.json` configuration file can be used for your real app)
- Specify the following in the Settings page
   - Sender ID: needed to create a device token / registration ID for your device
   - GCM API Key: only needed if you wish to use the "Test Push Notification" and "Test Push Message" features
- To test using messages sent from Intercom you will need to add your GCM API Key in the "Intercom for Android" settings at https://app.intercom.io/a/apps/_/settings/android

package com.parse.tutorials.pushnotifications

import com.parse.{Parse, ParseInstallation, PushService}

class Application extends android.app.Application {

  override def onCreate() = {
    super.onCreate

	// Initialize the Parse SDK.
	Parse.initialize(this, "YOUR_APP_ID", "YOUR_CLIENT_KEY")

	// Specify a Activity to handle all pushes by default.
	PushService.setDefaultPushCallback(this, new SMainActivity().getClass)
	
	// Save the current installation.
	ParseInstallation.getCurrentInstallation().saveInBackground
  }
}

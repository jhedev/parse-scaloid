package com.parse.tutorials.pushnotifications

import android.content.Context
import android.os.Bundle;
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.{EditText, RadioButton, RadioGroup, Toast}

import com.parse.{ParseAnalytics, ParseException, 
                    ParseInstallation, ParseObject, RefreshCallback,
                    SaveCallback}


import org.scaloid.common._

object SMainActivity {
  final val GENDER_MALE = "male"
  final val GENDER_FEMALE = "female"
}

class SMainActivity extends SActivity {

  import SMainActivity._
  
  private lazy val genderFemaleButton = new SRadioButton(R.string.gender_female_button)
  private lazy val genderMaleButton = new SRadioButton(R.string.gender_male_button)
  private lazy val ageEditText = new SEditText()
  private lazy val genderRadioGroup = new SRadioGroup {
    this += genderFemaleButton 
    this += genderMaleButton
  }

  onCreate {

    contentView = new SVerticalLayout {
      STextView(R.string.hello_message)
      this += new SVerticalLayout {
        STextView(R.string.age_text_view)
        this += ageEditText
      }
      this += new SVerticalLayout {
        STextView(R.string.gender_text_view)
        this += genderRadioGroup
      }
      SButton(R.string.save_button, saveUserProfile(_))
    }.padding(16.dip)

    ParseAnalytics.trackAppOpened(getIntent())
  }

  override def onStart = {
    super.onStart

    displayUserProfile
    refreshUserProfile
  }

  def saveUserProfile(view: View) = {
    val ageTextString = ageEditText.getText().toString

    if(ageTextString.length > 0) {
      ParseInstallation.getCurrentInstallation().put("age", Integer.valueOf(ageTextString))
    }
    if (genderRadioGroup.getCheckedRadioButtonId() == genderFemaleButton.getId()) {
		ParseInstallation.getCurrentInstallation().put("gender", GENDER_FEMALE);
    } else if (genderRadioGroup.getCheckedRadioButtonId() == genderMaleButton.getId()) {
		ParseInstallation.getCurrentInstallation().put("gender", GENDER_MALE);
	} else {
		ParseInstallation.getCurrentInstallation().remove("gender");
	}

    val imm = getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]
    imm.hideSoftInputFromWindow(ageEditText.getWindowToken(), 0)

    ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
			override def done(e: ParseException) {
				if (e == null) {
                    toast(R.string.alert_dialog_success)
				} else {
					e.printStackTrace()

                    toast(R.string.alert_dialog_failed)
				}
			}
		});
  }

  private def displayUserProfile = {
    val gender = ParseInstallation.getCurrentInstallation().getString("gender")
    val age = ParseInstallation.getCurrentInstallation().getInt("age")

    if (gender != null) {
        genderMaleButton.setChecked(gender.equalsIgnoreCase(GENDER_MALE))
        genderFemaleButton.setChecked(gender.equalsIgnoreCase(GENDER_FEMALE))
    } else {
        genderMaleButton.setChecked(false)
        genderFemaleButton.setChecked(false)
    }
    
    if (age > 0) {
        ageEditText.setText(Integer.valueOf(age).toString())
    }

  }

  private def refreshUserProfile = {
    ParseInstallation.getCurrentInstallation().refreshInBackground(
      new RefreshCallback() {
        override def done(o: ParseObject, e: ParseException) {
          if(e == null) {
            displayUserProfile
          }
        }
      }
    )
  }
}

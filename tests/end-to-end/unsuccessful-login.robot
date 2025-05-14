*** Settings ***
Library                          SeleniumLibrary


*** Test Cases ***
User logs in with improper credentials (bad user)
  Open Browser                   http://localhost:80/    chrome
  Click Link                     Log in!
  Input Text                     //input[@formcontrolname="handle"]      unexistinguser
  Input Password                 //input[@formcontrolname="password"]    randompassword

  Click Button                   Sign In
  Wait Until Page Contains       Could not log in


Dismiss error snackbar
  Click Button                           Close
  Wait Until Page Does Not Contain       Could not log in


User logs in with improper credentials (bad password)
  Input Text                     //input[@formcontrolname="handle"]      admin
  Input Password                 //input[@formcontrolname="password"]    wrongpassword

  Click Button                   Sign In
  Wait Until Page Contains       Could not log in

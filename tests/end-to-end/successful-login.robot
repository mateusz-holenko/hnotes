*** Settings ***
Library                          SeleniumLibrary


*** Test Cases ***
Page loads
  Open Browser                   http://localhost:80/    chrome
  Title Should Be                HNotes


Log In link works
  Click Link                     Log in!

  Location Should Be             http://localhost/login


User logs in with proper credentials
  Input Text                     //input[@formcontrolname="handle"]      admin
  Input Password                 //input[@formcontrolname="password"]    admin

  Click Button                   Sign In

  Wait Until Location Is         http://localhost/notes
  Page Should Contain            Logged as admin, log out


No Notes message is presented
  Wait Until Element Is Visible  no-notes-message

*** Settings ***
Library   SeleniumLibrary
Resource  common.robot


*** Test Cases ***
User logs in and sees no messages
  Sign In admin Using admin
  # Wait Until Element Is Visible  no-notes-message


User creates note without title
  Input Text    //input[@formcontrolname="content"]    First note's content
  Click Button  Done


Snackbar with success message pops up
  Wait Until Page Contains  GOT a MSG: note
  Click Button  Close


Pages displays a new note
  Wait Until Page Contains Element    //input[contains(@placeholder, "New note...")]
  Page Should Contain                 note #
  Page Should Contain                 First note's content


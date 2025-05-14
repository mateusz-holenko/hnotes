*** Settings ***
Library                          SeleniumLibrary


*** Keywords ***
Load Main Page
  Open Browser                   http://localhost:80/    chrome


Load Log In Page
  Load Main Page
  Click Link                     Log in!
  Wait Until Page Contains       Sign In


Sign In ${username} Using ${password}
  Load Log In Page
  Input Text                     //input[@formcontrolname="handle"]      ${username}
  Input Password                 //input[@formcontrolname="password"]    ${password}

  Click Button                   Sign In
  Wait Until Page Contains       Logged as ${username}, log out

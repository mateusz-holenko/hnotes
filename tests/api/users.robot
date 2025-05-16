*** Settings ***
Library                          RequestsLibrary
Library                          jwtlib.py


*** Test Cases ***
Generates JWT for proper credentials
  &{data}=  Create dictionary  username=admin  password=admin

  ${resp}=  POST  http://localhost:8090/users/login  json=${data}
  Should Be Equal As Strings  admin  ${resp.json()}[username]

  ${jwt}=  Decode JWT  ${resp.json()}[jwt]
  Should Be Equal As Integers  100  ${jwt}[sub]
  Should Contain  ${jwt}  iat
  Should Contain  ${jwt}  exp


Does not generate JWT for wrong password
  &{data}=  Create dictionary  username=admin  password=improper_password
  ${resp}=  POST  http://localhost:8090/users/login  json=${data}  expected_status=bad_request
  # Should Be Equal As Strings  "User 'admin' not found or provided bad credentials"  ${resp.text}


Does not generate JWT for no password
  &{data}=  Create dictionary  username=admin
  ${resp}=  POST  http://localhost:8090/users/login  json=${data}  expected_status=bad_request
  # Should Be Equal As Strings  "User 'admin' not found or provided bad credentials"  ${resp.text}


Does not generate JWT for bad body
  ${resp}=  POST  http://localhost:8090/users/login  expected_status=bad_request
  # Should Be Equal As Strings  "Bad input format"  ${resp.text}

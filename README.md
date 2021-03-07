# full Api Authencation Authorization
This is a full authentication to protect an application, including register, login, confirmation token through email and bearer authentication for requests. It also supports authorization based on roles

![auth flow](https://github.com/sylleryum/fullApiAuthencationAuthorization/blob/main/Auth%20flow.jpeg)

## 1 - Register
Method: Post

Body:<br/>
firstName<br/>
lastName<br/>
email<br/>
password<br/>

Description:
endpoint to register new user, all key/values are mandatory

## 2 - Request confirmation token
Method: Get

Header: request-token = email-to-receive-confirmation-token

Description: after registering an account, it will be disabled, to enable it a request needs to be sent to this endpoint to receive a confirmation email with a confirmation-token and use it on below step (this implementation sends to the email provided the confirmation-token for illustration purposes, but can return only the confirmation-token easily as it is described in the code)

## 3 - Send confirmation token to enable user
Method: Get

Param: confirm-token

Description: confirm-token needs to be sent to this endpoint to enable the account (with this implementation it is done by clicking the link in the email received based on the above)

4 - Login to receive access and refresh token
5 - Use access token in requests
6 - use refresh token to receive new access token

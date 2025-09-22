# User API Spec
## Register User

- Endpoint : POST /api/register
- Desc : Registrasi user baru. 

Request Body :
```json
{
  "username": "String",
  "name": "String",
  "email": "ardhan@example.com",
  "password" : "admin123"
}
```

Response Body (Success) :
```json
{
  "data": "OK"
}
```

Response body (Failed):
```json
{
  "errors": "Usermane must not blank"
}
```

## Login User

- Endpoint : POST /api/auth/login
- Desc : Login User

Request Body :
```json
{
  "identifier": "username/email",
  "password" : "admin123"
}
```

Response Body :
```json
{
  "data": {
    "token": "TOKEN",
    "expiredAt": 123132 // milisecond 
  }
}
```

Response body (Failed, 401):
```json
{
  "errors": "Credential Wrong"
}
```

## Get User
- Endpoint : GET /api/users/me
- Header : Required (Authorization)

Response body (Success):
```json
{
  "data": {
    "username": "ardhan",
    "name": "Ardhani Ahlan" 
    "email": "ardhan@example.com" 
  }
}
```

Response body (Failed, 401):
```json
{
  "errors": "Unauthorize"
}
```

## Update User

Endpoint: PATCH /api/users/me

- Header : Required (Authorization)

Request body:
```json
{
  "name": "Ahlan Ardhani",
  "password": "new password"
}
```

Response body (Success):
```json
{
  "data": {
    "username": "ardhan",
    "name": "Ardhani Ahlan" 
  }
}
```

Response body (Failed, 401):
```json
{
  "errors": "Unauthorize"
}
```

## Logout User

- Endpoint: DELETE /api/auth/logout
- Header : Required (Authorization)

Response body (Success):
```json
{
  "data": "OK"
}
```
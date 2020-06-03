# Task 1: Send events

Implemented using Java 8. To run a demo:

```bash
mvn compile
mvn exec:java
```

## Structure

* `com.github.yuvallb.sendEventDemo.App`: Entry point. Demonstrating usage by generating and sending 20 events.
* `com.github.yuvallb.sendEventDemo.EventHandler<T extends IEvent>`: Main event handler.
  * Gets a sender and serializer using dependency injection from the constructor.
  * Contains a private queue for storing events before sending.
  * Starts a new background thread that asynchronously flushes the queue every given period, configured using `BackgroundFlushSeconds`.
  * Synchronously flushes the queue if number of pending events reaches some preset threashold, configured using  `HighWatermark`.
  * Messages are sent in bulk, a bulk limit should be set using `MaxMessageSize`.
* `com.github.yuvallb.sendEventDemo.IEvent`: Empty interface to annotate that a class can be sent as an event.
* `com.github.yuvallb.sendEventDemo.NewUserEvent`: An example implementation event class with private, protected and public fields.
* `com.github.yuvallb.sendEventDemo.serialize.IEventSerializer`: An interface for the ability to serialize event object according to the agreed json notation.
* `com.github.yuvallb.sendEventDemo.serialize.GsonEventSerializer`: Example implementation for serializing events using the Gson library.
* `com.github.yuvallb.sendEventDemo.send.IMessageSender`: An interface for the ability to send serialized messages using HTTP or any other mechanism.
* `com.github.yuvallb.sendEventDemo.send.ConsoleMessageSender`: An example implementation that only writes the messages to the console.

# Task 2: Design authentication module

## API endpoints

### Signup

```api
POST auth/register
```

#### Request body parameters

* `username`
* `password`
* `email`
* `first_name`
* `last_name`

#### Response - Successful registration

```json
StatusCode: 200
{
    "token": "token content..."
}
```

The token should be passed on all subsequent requests, in the "Authorization" header.

#### Response - Unsuccessful registration

```json
StatusCode: 400
{
    "reason": "some reason..."
}
```

### Sign in

```api
POST auth/login
```

#### Request body parameters

* `username`
* `password`

#### Response - Successful login

```json
StatusCode: 200
{
    "token": "token content..."
}
```

The token should be passed on all subsequent requests, in the "Authorization" header.

#### Response - Unsuccessful login

```json
StatusCode: 400
{
    "reason": "some reason..."
}
```

### Forgot Password: request password restore

```api
POST auth/restore
```

#### Request body parameters

* `username`
* `email`

#### Response - Successful

```json
StatusCode: 200
```

Password restore email was sent to the user.

#### Response - Unsuccessful

```json
StatusCode: 400
{
    "reason": "some reason..."
}
```

### Forgot Password: request reset

```api
GET auth/reset/{token}
```

#### Query string parameters

* `token` token that was sent by email

#### Response

```json
Status Code: 200 or 400
```

### Forgot Password: perform reset

```api
POST auth/reset/{token}
```

#### Query string parameters

* `token` token that was sent by email

#### Request body parameters

* `password`

#### Response - Successful reset

```json
StatusCode: 200
{
    "token": "token content..."
}
```

The token should be passed on all subsequent requests, in the "Authorization" header.

#### Response - Unsuccessful reset

```json
StatusCode: 400
{
    "reason": "some reason..."
}
```

## Business flows

### Signup

* Input parameters from POST
  * `username`
  * `password`
  * `email`
  * `first_name`
  * `last_name`
* Validation
  * All fields not empty
  * username should not exist in users_table ("User already exists")
  * email should not exist in user_claims table with claim_type='email' ("User already exists")
  * email should be in email structure ("Malformed email address")
  * password should pass some complexity check, and not be from a known dictionary ("Password does not conform to requirements")
  * If any validation failed: return response with code=400 and the reasons as specified in each field above.
* Create user to users table
  * user_name = input username
  * password_hash = SHA1(password)
  * is_verified=0
  * is_locked_out=0
* Get newly created user id
* Create claims to user claims table
  * claim_type='email', claim_issuer='user', claim_value=email
  * claim_type='first_name', claim_issuer='user', claim_value=first_name
  * claim_type='last_name', claim_issuer='user', claim_value=last_name
* Login user as detailed in the "sign in" section below
* (Future) Send email verification

### Sign in

* Input parameters from POST
  * `username`
  * `password`
* Validation
  * All fields not empty
  * Select from users table according to user_name and SHA1(password)
  * If not found: insert into user_logins table. user_name = input username, password_hash = SHA1(password), client_ip = CLIENT_IP, is_successful=0, user_id=null
  * If not found: return response code=400 and reason "Unknown username or password".
* If is_locked_out=1 and is_locked_until in future:
  * insert into user_logins table. user_name = input username, password_hash = SHA1(password), client_ip = CLIENT_IP, is_successful=0, user_id=user id
  * return response code=400 and reason "Unknown username or password".return response code=400 and reason "User locked out, try again later".
* Create session and insert to user_sessions table.
  * session_id randomly generated
  * user_id from users table
  * valid_from: now
  * valid_to: now + predefined logind period
* Select claims from user_claims table
* Insert claims to user_sessions_data table
* Construct JWT token from claims. Sign token with private key.
* Insert into user_logins table. user_name = input username, password_hash = SHA1(password), client_ip = CLIENT_IP, is_successful=1, user_id=user id
* Return respose code=200, body token=jwt_token

### Forgot Password: request password restore

* Input parameters from POST
  * `username`
  * `email`
* Validation
  * All fields not empty
  * Select from users join user_claims table according to user_name AND email
  * If not found: return response code=400 and reason "User or email not found"
  * generate a random token
  * insert into restore_password_requests: tokem user id, valid_from=now, valid_to=now+predefind period for password reset

### Forgot Password: request reset

* Input parameters from Query
  * `token`
* Select token from restore_password_requests where valid_from in the past and valid_to in the future.
* If found: return response code 200
* If not found: return response code 400





### Forgot Password: perform reset

* Input parameters from Query
  * `token`
* Input parameters from POST
  * `password`
* Select token from restore_password_requests where valid_from in the past and valid_to in the future.
* If not found: return response code 400, empty reason
* Validations:
  * password should pass some complexity check, and not be from a known dictionary ("Password does not conform to     requirements")
  * If any validation failed: return response with code=400 and the reasons as specified in each field above.
* Update users table:
  * set previous_password_hash=password_hash
  * set password_hash = SHA1(input password)

## Data model


```sql

-- Users table

CREATE TABLE users (
    id int AUTO_INCREMENT PRIMARY KEY,
    user_name varchar(100),
    registered timestamp DEFAULT CURRENT_TIMESTAMP,
    password_hash varchar(100),
    previous_password_hash varchar(100),
    is_verified bit,
    is_locked_out bit,
    locked_out_until timestamp DEFAULT CURRENT_TIMESTAMP,
    INDEX (user_name)
);

-- User Claims table

-- The claims table can be used for general information (i.e email, first and last name), for permissions and roles or other information regarding the user. It has no unique constraint - the same user can potentially have multiple values for the same claim issuer and type.

CREATE TABLE user_claims (
    user_id int not null REFERENCES users (id),
    claim_issuer varchar(100),
    claim_type varchar(100),
    claim_value varchar(1000),
    INDEX (user_id),
    INDEX (claim_issuer,claim_type)
);

-- User Logins table
--  Logs all login attempts. Can be used to detect brute force attacks and serves as an audit trail for user logins. (a separate log should be set if we also want to audit modifications of users and user claims).

CREATE TABLE user_logins (
    attempt timestamp DEFAULT CURRENT_TIMESTAMP,
    user_name varchar(100),
    password_hash varchar(100),
    client_ip varchar(100),
    is_successful bit,
    user_id int null REFERENCES users (id)
);

-- User Sessions tables

CREATE TABLE user_sessions (
    session_id varchar(100) PRIMARY KEY,
    user_id int not null REFERENCES users (id),
    valid_from timestamp DEFAULT CURRENT_TIMESTAMP,
    valid_to timestamp DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE user_session_data (
    session_id varchar(100),
    data_key varchar(100),
    data_value text,
    INDEX (session_id)
);

-- Password restore table

CREATE TABLE restore_password_requests (
    token varchar(100) PRIMARY KEY,
    user_id int not null REFERENCES users (id),
    valid_from timestamp DEFAULT CURRENT_TIMESTAMP,
    valid_to timestamp DEFAULT CURRENT_TIMESTAMP
);
```

# Play silhouette rest mongodb

=====================================

cv showcase

## Request examples

### Sign up:

```
POST /signUp HTTP/1.1
Host: localhost:9000
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 26c528b8-9b59-d92e-ba41-2eee6b21f05b

{"firstName":"asd","lastName":"asd","email":"asd","password":"asd"}
```

response:

```
{"token":"TOKEN"}
```

### Sing in

```
POST /signIn HTTP/1.1
Host: localhost:9000
Content-Type: application/json

{"email":"asd","password":"asd","rememberMe":true}
```

response:

```
{"token":"TOKEN"}
```

### Business add/update

```
POST /api/business HTTP/1.1
Host: localhost:9000
Content-Type: application/json
X-Auth-Token: TOKEN

{
  "businessID": "3e75e00f-45c8-4d6f-a10c-727e8d91b186",
  "name": "2String",
  "address": "String",
  "country": "String",
  "city": "String",
  "email": "String",
  "contry": "String"
}
```

response: OK (body is empty)

### Getting list of businesses

```
GET /api/businesses?nameSubstring=22 HTTP/1.1
Host: localhost:9000
Content-Type: application/json
X-Auth-Token: TOKEN
```

response:

```
[]
```

### Getting list of businesses (2)

```
GET /api/businesses?nameSubstring=ri HTTP/1.1
Host: localhost:9000
Content-Type: application/json
X-Auth-Token: TOKEN
```

response:

```
[{"businessID":"3e75e00f-45c8-4d6f-a10c-727e8d91b186","name":"2String","address":"String","country":"String","city":"String","email":"String","contry":"String"},{"businessID":"3e75e00f-45c8-4d6f-a10c-727e8d91b187","name":"String","address":"String","country":"String","city":"String","email":"String","contry":"String"}]
```

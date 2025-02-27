### Описание эндопоитов

#### Авторизация /auth

```
/auth/register
```
*Запрос*:\
Authorization: отсутствует \
Body: {"login": string, "password": string}\
*Ответ*: {"access_token": string, "refresh_token": string}

```
/auth/login
```
*Запрос*:\
Authorization: отсутствует \
Body: {"login": string, "password": string}\
*Ответ*: {"access_token": string, "refresh_token": string}

```
/auth/refresh
```
*Запрос*:\
Authorization: "Bearer refresh_token"
*Ответ*: {"access_token": string, "refresh_token": string}





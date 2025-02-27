## Описание эндопоитов

### Авторизация /auth

```
/auth/register
```
**Запрос**:\
Authorization: отсутствует \
Body: {"login": string, "password": string}\
**Ответ**: \
{"access_token": string, "refresh_token": string}

```
/auth/login
```
**Запрос**:\
Authorization: отсутствует \
Body: {"login": string, "password": string}\
**Ответ**: \
{"access_token": string, "refresh_token": string}

```
/auth/refresh
```
**Запрос**:\
Authorization: "Bearer refresh_token"
**Ответ**: \
{"access_token": string, "refresh_token": string}

### Игры и команды /teams

```
/teams/get_teams
```
**Запрос**:\
Authorization: "Bearer access_token"
**Ответ**: \
[\
    {\
        "team": {\
            "conference": "Конференция «Запад»",\
            "conference_key": "west",\
            "division": "Дивизион Тарасова",\
            "division_key": "tarasov",\
            "id": 26,\
            "image": "https://thumbs-cdn.webcaster.pro/rec-1-4.webcaster.pro/fc/sdl/team_pics/26/original/lokomotiv_2022__200x200.png",\
            "khl_id": 1,\
            "location": "Ярославль",\
            "name": "Локомотив"\
        }\
    }, ...\
]\





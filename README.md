# adfmp1h25-sport

flowchart TD
    A[Пользователь инициирует авторизацию (login/register)]
    B[MainViewModel: вызывает AuthViewModel.login/register]
    C{Результат авторизации успешен?}
    D[MainViewModel: вызывает TeamViewModel.fetchAndSaveTeamsAsync]
    E[TeamViewModel: пытается загрузить команды из кеша/с API] 
    F{Команды успешно загружены?}
    G[MainViewModel: вызывает EventViewModel.loadEventsAsync]
    H[EventViewModel: формирует диапазон дат и проверяет наличие команд]
    I[EventViewModel: получает события через API]
    J[EventViewModel: запрашивает прогнозы с API]
    K[EventViewModel: мапит события & прогнозы на EventPredictionItem]
    L[Завершение обработки, отображение событий]
    M[Обработка ошибки (неудачная авторизация или загрузка команд)]
    
    %% Пошаговые связи:
    A --> B
    B --> C
    C -- Да --> D
    C -- Нет --> M
    D --> E
    E --> F
    F -- Да --> G
    F -- Нет --> M
    G --> H
    H --> I
    I --> J
    J --> K
    K --> L

    %% Дополнительный вариант: проверка валидности токена
    P[MainViewModel: вызывает checkTokenValidity]
    P --> Q[AuthViewModel: проверка валидности токена через API]
    Q -- Токен валиден --> D
    Q -- Токен не валиден --> M
## Filmorate

Сервис для выбора фильмов, среди пользователей. Функциональность обеспечивает
пользователям добавлять друг друга в друзья, ставить фильмам лайки , оставлять отзывы, поиск
фильма по заданным фильтрам, настроена бизнес-логика рекомендаций на основании лайков
друзей. Возможность просматривать ленту событий пользователей и фильмов

Функциональность приложения эквивалентна API, посредством которого происходит взаимодействие.
## Реализованные эндпоинты:

<details>
  <summary><h3>Пользователи</h3></summary>
  
* **POST** /users - создание пользователя
* **PUT** /users - редактирование пользователя
* **GET** /users - получение списка всех пользователей
* **GET** /users/{userId} - получение информации о пользователе по его id
* **PUT** /users/{id}/friends/{friendId} — добавление в друзья
* **DELETE** /users/{id}/friends/{friendId} — удаление из друзей
* **GET** /users/{id}/friends — возвращает список пользователей, являющихся его друзьями
* **GET** /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем

</details>
<details>
  <summary><h3>Фильмы</h3></summary>
  
* **POST** /films - создание фильма
* **PUT** /films - редактирование фильма
* **GET** /films - получение списка всех фильмов
* **GET** /films/{filmId} - получение информации о фильме по его id
* **PUT** /films/{id}/like/{userId} — пользователь ставит лайк фильму
* **DELETE** /films/{id}/like/{userId} — пользователь удаляет лайк
* **GET** /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, возвращает первые 10

</details>
<details>
  <summary><h3>Жанры</h3></summary>
  
* **GET** /genres - получение списка всех жанров
* **GET** /genres/{id} - получение информации о жанре по его id

</details>
<details>
  <summary><h3>Рейтинги</h3></summary>
  
* **GET** /mpa - получение списка всех рейтингов
* **GET** /mpa/{id} - получение информации о рейтинге по его id

</details>

## Валидация

Входные данные, которые приходят в запросе на добавление нового фильма или пользователя, проходят проверку. Эти данные должны соответствовать следующим критериям:

<details>
  <summary><h3>Для пользователей:</h3></summary>
  
* электронная почта не может быть пустой и должна содержать символ @;
* логин не может быть пустым и содержать пробелы;
* имя для отображения может быть пустым — в таком случае будет использован логин;
* дата рождения не может быть в будущем.

</details>
<details>
  <summary><h3>Для фильмов:</h3></summary>
  
* название не может быть пустым;
* максимальная длина описания — 200 символов;
* дата релиза — не раньше 28 декабря 1895 года;
* продолжительность фильма должна быть положительной;
* рейтинг не может быть null.

</details>

## Схема БД и примеры запросов

![Diagramm SQL filmorate](https://github.com/sigmaclap/java-filmorate/blob/add-database/db.png)

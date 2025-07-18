MERGE INTO MPA (mpa_id, rating, description)
KEY (mpa_id)
VALUES
    (1, 'G', 'У фильма нет возрастных ограничений.'),
    (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями.'),
    (3, 'PG-13', 'Детям до 13 лет просмотр не желателен.'),
    (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого.'),
    (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён.');

MERGE INTO genre_type (genre_id, type)
KEY (genre_id)
VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

MERGE INTO users (user_id, name, login, email, birthday)
KEY (user_id)
VALUES
    (1, 'name-1', 'login-1', 'mail-1@mail.ru', '2000-10-11'),
    (2, 'name-2', 'login-2', 'mail-2@mail.ru', '2000-10-12'),
    (3, 'name-3', 'login-3 ', 'mail-3@mail.ru', '2000-10-13'),
    (4, 'name-4', 'login-4', 'mail-4@mail.ru', '2000-10-14');

MERGE INTO friends_status (id, user_id, friend_id)
KEY (id)
VALUES
    (1, 1, 2),
    (2, 1, 4),
    (3, 2, 3),
    (4, 4, 2);

MERGE INTO films (film_id, name, description, release_date, duration, mpa_id)
KEY (film_id)
VALUES
    (1, 'film-1', 'description-1', '2000-01-07', '120', 2),
    (2, 'film-2', 'description-2', '2000-01-05', '120', 5),
    (3, 'film-3', 'description-3', '2000-04-07', '120', 1);

MERGE INTO genre (id, film_id, genre_id)
KEY (id)
VALUES
    (1, 1, 3),
    (2, 1, 6),
    (3, 3, 4),
    (4, 2, 1),
    (5, 1, 1),
    (6, 3, 5),
    (7, 2, 5),
    (8, 2, 3);

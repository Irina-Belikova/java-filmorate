CREATE TABLE IF NOT EXISTS MPA (
    mpa_id INTEGER PRIMARY KEY,
    rating VARCHAR(20),
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS genre_type (
    genre_id INTEGER PRIMARY KEY,
    type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200) NOT NULL,
    release_date DATE CHECK (release_date > '1895-12-28'),
    duration INTEGER CHECK (duration > 0),
    mpa_id INTEGER NOT NULL,
    FOREIGN KEY (mpa_id) REFERENCES MPA(mpa_id)
);

CREATE TABLE IF NOT EXISTS genre (
    film_id BIGINT NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre_type(genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    login VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    birthday DATE CHECK (birthday <= CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    UNIQUE (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends_status (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    CHECK (user_id <> friend_id),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS files (
                                     id   SERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     path VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres (
                                      id   SERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS halls (
                                     id          SERIAL PRIMARY KEY,
                                     name        VARCHAR(255) NOT NULL,
                                     row_count   INT NOT NULL,
                                     place_count INT NOT NULL,
                                     description VARCHAR(1024) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
                                     id                  SERIAL PRIMARY KEY,
                                     name                VARCHAR(255) NOT NULL,
                                     description         VARCHAR(2048) NOT NULL,
                                     "year"              INT NOT NULL,
                                     genre_id            INT NOT NULL REFERENCES genres(id),
                                     minimal_age         INT NOT NULL,
                                     duration_in_minutes INT NOT NULL,
                                     file_id             INT NOT NULL REFERENCES files(id)
);

CREATE TABLE IF NOT EXISTS film_sessions (
                                             id         SERIAL PRIMARY KEY,
                                             film_id    INT NOT NULL REFERENCES films(id),
                                             halls_id   INT NOT NULL REFERENCES halls(id),
                                             start_time TIMESTAMP NOT NULL,
                                             end_time   TIMESTAMP NOT NULL,
                                             price      INT NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
                                     id        SERIAL PRIMARY KEY,
                                     full_name VARCHAR(255) NOT NULL,
                                     email     VARCHAR(255) NOT NULL UNIQUE,
                                     password  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets (
                                       id           SERIAL PRIMARY KEY,
                                       session_id   INT NOT NULL REFERENCES film_sessions(id),
                                       row_number   INT NOT NULL,
                                       place_number INT NOT NULL,
                                       user_id      INT NOT NULL REFERENCES users(id),
                                       CONSTRAINT unique_ticket UNIQUE (session_id, row_number, place_number)
);
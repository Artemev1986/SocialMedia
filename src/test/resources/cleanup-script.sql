DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS friendships (
                                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
                                           user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                           friend_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                           status VARCHAR(20) NOT NULL,
                                           CONSTRAINT uniq_friendships UNIQUE (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS messages (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
                                        sender_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
                                        recipient_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
                                        text VARCHAR(500) NOT NULL,
                                        created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS posts (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
                                     user_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
                                     title VARCHAR(100) NOT NULL,
                                     text VARCHAR(500) NOT NULL,
                                     created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                     updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS images (
                                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
                                      post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE NOT NULL,
                                      name VARCHAR(50) NOT NULL,
                                      content_type VARCHAR(20) NOT NULL,
                                      image_data BYTEA NOT NULL
);
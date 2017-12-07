# --- !Ups

CREATE TABLE users (
  "id" BIGSERIAL PRIMARY KEY,
  "username" VARCHAR(1024) NOT NULL UNIQUE ,
  "hashed_password" VARCHAR(256) NOT NULL,
  "salt" VARCHAR(256) NOT NULL
);

CREATE TABLE contacts (
  "id" BIGSERIAL PRIMARY KEY,
  "first_name" VARCHAR(1024),
  "last_name" VARCHAR(1024),
  "phones" VARCHAR(1024)[] DEFAULT '{}',
  "owner" BIGINT REFERENCES users (id)
);

# --- !Downs

DROP TABLE contacts;
DROP TABLE users;

# --- !Ups

CREATE TABLE "people" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  "age" INTEGER NOT NULL
);

CREATE TABLE users (
  "id" BIGSERIAL PRIMARY KEY,
  "username" VARCHAR(1024) NOT NULL,
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

DROP TABLE people;
DROP TABLE contacts;
DROP TABLE users;

# contacts-sample

This is a simple Playframework 2.6 application with Slick
module for accessing PostgreSQL database.

The server expects that there is Postgres DB is running
on `db` host on port 5432. To make it easier to run the app
there is `docker-compose.yml` file which 

## Requirements for building locally
* JDK 8
* [SBT](https://www.scala-sbt.org/)

## Configuration
Database access settings are stored in `conf/application.conf`.
By default `postgres:postgres` username/password with database name
`contacts` is used.

## Running locally
* Start Postgres server
* Either update `slick.dbs.default.db.url` to point to the host or
add a hostname mapping from `db` host to `127.0.0.1` in `/etc/hosts` (if Linux)
* type`sbt run` in terminal in the project root
* the app should be available at [http://localhost:9000](http://localhost:9000)

## Running with `docker-compose`
* type `docker-compose up` in terminal the project root
* the app should be available at [http://localhost:9000](http://localhost:9000)

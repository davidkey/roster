# Duty #
[![Build Status](https://travis-ci.org/davidkey/roster.svg?branch=master)](https://travis-ci.org/davidkey/roster)
[![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/davidkey/roster)
### What is this repository for? ###

Generating and managing duty rosters. A work in progress.

### How do I get set up? ###

* git clone
* mvn package
* Deploy ./target/dutyRoster.war to servlet 3.0 compatible container
	* A jdbc datasource is required (configured by Spring Boot args). By default, the application expects PostgreSQL dialect.
	* Application also expects a --spring.profiles.active=? argument (? being production or dev).
* Visit $WEB_ROOT/admin/ to configure
* Database schema changes & definition can be found at /src/main/resources/sql/generated.

See a (probably not up-to-date) demo @ <https://dak.rocks/duty/> (dev) or <https://roster.guru/> (stable build).

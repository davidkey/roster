# Duty #
[![Build Status](https://dak.rocks/jenkins/buildStatus/icon?job=BUILD_DUTY_ROSTER)](https://github.com/davidkey/roster)
### What is this repository for? ###

Generating and managing duty rosters. Primarily useful for churches.

### How do I get set up? ###

* git clone
* mvn package
* Deploy ./target/dutyRoster.war to servlet 3.0 compatible container
	* A jdbc datasource (see /src/main/resources/jdbc.properties) is required. By default, the application expects PostgreSQL dialect.
	* Application also expects a -Dspring.profiles.active=? jvm argument (? being prod or dev).
* Visit $WEB_ROOT/admin/ to configure
* Database schema changes & definitions can be found at /src/main/resources/sql/generated for your dialect of choice.

See a (probably not up-to-date) demo @ <https://dak.rocks/duty/admin/>.

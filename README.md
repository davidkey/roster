# Duty #

### What is this repository for? ###

Generating and managing duty rosters. Primarily useful for churches.

### How do I get set up? ###

* git clone
* mvn package
* Deploy ./target/dutyRoster.war to servlet 3.0 compatible container
	* A jdbc datasource (jndi: jdbc/DutyDB) is required. Currently the application expects PostgreSQL dialect.
	* Application also expects a -Dspring.profiles.active=prod jvm argument.
* Visit $WEB_ROOT/admin/ to configure

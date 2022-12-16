# Ref-Cursor

[sonar](https://sonarcloud.io/project/overview?id=ref-cursor)

0. Ensure that tests are disabled. (Comment them)
1. Build jar package
` mvn clean package `
2. Copy jar file to midpoint connectors directory. Run in root project dir:
` COPY target\connector-refcursor-1.0-SNAPSHOT.jar Docker\midpoint\icf-connectors\connector-refcursor-1.0-SNAPSHOT.jar `
3. To start docker container run from (/Docker/) folder this:
   `docker-compose -f ./docker-compose.yml up --build --force-recreate -d`
4. To stop containers run
   `docker-compose -f ./docker-compose.yml down --volumes`
5. To enable logging in midpoint go to `configuration -> system -> logging`. 
Then `Class loggers tab`. And then add 3 rows with such values:
- `com.refcursorconnector.RefCursorConnectorConnection`
- `com.refcursorconnector.RefCursorConnector`
- `com.refcursorconnector.MidpointClient`


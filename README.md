# Ref-Cursor

[sonar](https://sonarcloud.io/project/overview?id=ref-cursor)

1. Build jar package
` mvn clean package `
2. Copy jar file to midpoint connectors directory. Run in root project dir:
` COPY target\connector-refcursor-1.0-SNAPSHOT.jar Docker\midpoint\icf-connectors\connector-refcursor-1.0-SNAPSHOT.jar `
3. To start docker container. Run from (/Docker/) folder this:
   `docker-compose -f ./docker-compose.yml up --build --force-recreate -d`
5. To stop containers run
   `docker-compose -f ./docker-compose.yml down`

# Ref-Cursor

[sonar](https://sonarcloud.io/project/overview?id=ref-cursor)

1. To start docker container.

   Run from (/Docker/) folder this:
   `docker-compose -f ./docker-compose.yml up --build --force-recreate -d`

2. To stop run
   `docker-compose -f ./docker-compose.yml down`

3. To install maven dependencies `mvn clean install --settings .mvn/custom-settings.xml` in project directory.

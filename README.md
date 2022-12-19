# Ref-Cursor

## [sonar](https://sonarcloud.io/project/overview?id=ref-cursor)
## [jira](https://ref-cursor.atlassian.net/jira/projects?selectedProjectType=software)

## How to run (Windows OS)

1. Ensure docker is running on your system.
2. ```winget install GnuWin32.Make```
3. Add to path C:\Program Files (x86)\GnuWin32\bin
4. Download maven from https://maven.apache.org/. Unzip to C:\Program Files (x86)
5. Add to path C:\Program Files (x86)\apache-maven-3.8.6\bin
6. `make run` root folder of the repo.
7. Use ```docker-compose -f ./Docker/docker-compose.yml down --volumes``` to stop container.

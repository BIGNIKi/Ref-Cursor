run: build destroy-containers deploy up-containers

build:
	mvn clean package

deploy:
	echo y | COPY target\connector-refcursor-1.0-SNAPSHOT.jar Docker\midpoint\icf-connectors\connector-refcursor-1.0-SNAPSHOT.jar

up-containers:
	docker-compose -f ./Docker/docker-compose.yml up --build --force-recreate -d

destroy-containers:
	docker-compose -f ./Docker/docker-compose.yml down --volumes

# nginx-redundant | nginx 로드밸런싱으로 WAS 이중화

nginx의 로드밸런싱 기능과 도커를 이용하여 하나의 웹 어플리케이션 서버를 이중화합니다.
<br />
<br />

업로드된 웹 어플리케이션은 간단한 CRUD 기능을 수행합니다.   
- Build Tool: Maven
- Language: Java 11
- Framework: SpringBoot
- Database: MariaDB
<br />
<br />


빌드된 Jar 파일을 이중화된 각 컨테이너에 복사하여 배포합니다.   
<br />
nginx 서버 컨테이너는 클라이언트의 요청 시 라운드 로빈 방식으로 로드밸런싱하여 각 WAS 컨테이너에 번갈아가면서 요청을 하게 됩니다.   
<br />

<div align="center">
  <img src="https://user-images.githubusercontent.com/81132541/205433740-75d311ee-66ad-4dfc-b805-bc7c2eedfa26.png" width="450px">
</div>
<br />
<br />

## 실습

실습 전 Docker와 CentOS 7이 설치된 가상 머신이 필요합니다.   
<br />

다음 링크를 참고하여 Docker와 CentOS 7이 설치된 가상 머신을 준비합니다.   
https://github.com/jeongwon201/docker/tree/main/docs/docker-1-env
<br />

가상 머신이 준비되었다면, 다음 작업을 수행합니다.   
<br />

현재 OS에 설치된 Java 버전을 확인합니다.
```
yum list installed|grep java
```

<div align="center">
  <img src="https://user-images.githubusercontent.com/81132541/205434373-1dfbbee4-a001-47ed-af5d-f62ed5595052.png" width="500px">
</div>
<br />
<br />

웹 어플리케이션은 Java 11을 사용하므로, Java 11 버전을 제외한 다른 버전을 모두 삭제합니다.
```
yum remove -y java-1.7.0-openjdk.x86_64 java-1.7.0-openjdk-headless.x86_64 java-1.8.0-openjdk.x86_64 java-1.8.0-openjdk-headless.x86_64
```
<br />
<br />

Git, Docker Compose 를 설치합니다.
```
yum install -y git docker-compose-plugin
```
<br />
<br />


작업 폴더를 생성 후 폴더로 이동합니다.
```
mkdir standard
cd standard
```
<br />
<br />


환경 변수 파일 ```00_SET_ENV``` 을 vi 편집기로 생성 후 다음 내용을 입력니다.
```
#! /usr/bin/bash

# docker-compose network, nginx, springboot, mariadb 가 엮이는 네트워크 이름
export APP_NETWORK=standard_net

# DOCKER 상에서 WAS 호출할 수 있는 이름
export DOCKER_WAS_NAME1=dockerWAS1
export DOCKER_WAS_NAME2=dockerWAS2

# DOCKER 상에서 DB 호출할 수 있는 이름
export DOCKER_DB_NAME=dockerDB


############### BUILD AREA ###############
# 로컬 머신에 어플리케이션 설치 위치
export APP_BASE=/app

# 로컬 머신 메이븐 설치 위치
export MAVEN=${APP_BASE}/build/maven

# 소스 URL (github)
export SOURCE_URL=https://github.com/jeongwon201/nginx-redundant

# PROJECT_NAME
export PROJECT_NAME=spring-cloud
export APP_NAME=standard

# 디렉토리
export PROJECT_DIR=/app/$PROJECT_NAME
export APP_DIR=/app/$PROJECT_NAME/$APP_NAME

# 도커 이미지 이름
export IMAGE_NAME=jeongwon201/cloud:springboot_v1


############### SPRINGBOOT AREA ###############
# VERSION
export VERSION=1
export JAR_NAME=$APP_NAME-$VERSION
export SPRINGBOOT_PORT=8000

# 스프링 부트 런타임 옵션
export PARAMETER=spring.profiles.active=prod,db-mariadb-docker
export ACTIVE_PROFILE=prod,db-maria-docker


############### DB AREA ###############
export SPRINGBOOT_PORT=8000
export MYSQL_DATABASE=prod
export MYSQL_USER=user01
export MYSQL_PASSWORD=user01
export MYSQL_ROOT_PASSWORD=password
export LOCAL_DB_DIR=/app/mariadb
```
<br />
<br />


편의를 위해 Bash Shell Script를 사용하여 작업 파일을 생성합니다.   
<br />

### Java 11, Maven 빌드 도구 설치
작업 파일 ```01_SET_BUILD.sh``` 을 vi 편집기로 생성 후 다음 내용을 입력하여 저장합니다.
```
#! /usr/bin/bash
. ./00_SET_ENV

yum -y install yum

yum -y install unzip

yum -y install java-11-openjdk java-11-openjdk-devel

yum -y install wget

mkdir -p $MAVEN

mkdir -p $APP_BASE

wget --directory-prefix=$MAVEN https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip

unzip -d $MAVEN $MAVEN/apache-maven-3.6.3-bin.zip
```
<br />

권한 부여 후 파일을 실행합니다.
```
chmod +x 01_SET_BUILD.sh

./01_SET_BUILD.sh
```

<br />
<br />

### GitHub에 업로드 된 웹 어플리케이션을 Jar 파일로 빌드 후 작업 폴더에 복사
작업 파일 ```02_BUILD.sh``` 을 vi 편집기로 생성 후 다음 내용을 입력하여 저장합니다.
```
#! /usr/bin/bash
. ./00_SET_ENV

export PATH=$MAVEN/apache-maven-3.6.3/bin:$PATH

# 
rm -rf $PROJECT_DIR

git clone $SOURCE_URL $PROJECT_DIR

mvn -DfinalName=$JAR_NAME install -f $APP_DIR

cp $APP_DIR/target/$JAR_NAME.jar ./
```
<br />

권한 부여 후 파일을 실행합니다.
```
chmod +x 02_BUILD.sh

./02_BUILD.sh
```
<br />

Java 11 버전 외 다른 버전을 삭제하지 않으면 오류가 발생합니다.   
오류가 발생하면 앞서 수행했던 Java 11 외 모든 버전을 삭제하는 내용을 참고하세요.
<br />
<br />

### 컨테이너 이미지를 위한 Dockerfile 생성
Dockerfile ```Dockerfile``` 을 vi 편집기로 생성 후 다음 내용을 입력하여 저장합니다.
```
FROM centos:7

MAINTAINER jeongwon201@naver.com

ENV TZ=Asia/Seoul

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV LC_ALL=C.UTF-8

RUN yum -y install java-11-openjdk java-11-openjdk-devel

RUN mkdir -p /app/spring-cloud/standard/target/

COPY ./standard-1.jar /app/spring-cloud/standard/target/

EXPOSE 8000
```
<br />
<br />

### 컨테이너 이미지 생성
작업 파일 ```03_MAKE_DOCKER_IMAGE.sh``` 을 vi 편집기로 생성 후 다음 내용을 입력하여 저장합니다.
```
#! /usr/bin/bash
. ./00_SET_ENV

docker build -t $IMAGE_NAME ./
```
<br />

권한 부여 후 파일을 실행합니다.
```
chmod +x 03_MAKE_DOCKER_IMAGE.sh

./03_MAKE_DOCKER_IMAGE.sh
```
<br />
<br />

※ 04 스크립트 파일은 아래 파일을 템플릿으로 아래 파일을 정의하는데, 템플릿이 존재하지 않아 직접 작성하는 것으로 대체합니다.
<br />
<br />

### 이중화를 위한 nginx conf 파일 정의
conf 파일 ```default.conf``` 을 vi 편집기로 생성 후 다음 내용을 입력하여 저장합니다.
```
upstream my_upstream {
	server dockerWAS1:8000;
	server dockerWAS2:8000;
}

server {
	listen 80;
	listen [::]:80;

	server_name localhost;

	location / {
		proxy_pass http://my_upstream;
		proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header Host $http_host;
	}
}
```
<br />
<br />

### 네트워크, 컨테이너 생성을 위한 Docker Compose YML 파일 정의
YML 파일 ```docker-compose.yml``` 을 vi 편집기로 생성 후 다음 내용을 입력 저장합니다.
```
version: '3.4'
services:

  web:
    image: nginx
    networks:
      - standard_net
    ports:
      - "80:80"
    links:
      - was1:dockerWAS1
      - was2:dockerWAS2
    entrypoint:
      - "nginx"
      - "-g"
      - "daemon off;"
    depends_on:
      - was1
      - was2
    volumes:
      - ./default.conf:/etc/nginx/conf.d/default.conf

  was1:
    image: jeongwon201/cloud:springboot_v1
    networks:
      - standard_net
    links:
      - mariadb:dockerDB
    environment:
      - JAR_NAME=standard-1
      - SOURCE_DIR=/app/spring-cloud/standard
    entrypoint: java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=prod,db-maria-docker /app/spring-cloud/standard/target/standard-1.jar
    depends_on:
      - mariadb

  was2:
    image: jeongwon201/cloud:springboot_v1
    networks:
      - standard_net
    links:
      - mariadb:dockerDB
    environment:
      - JAR_NAME=standard-1
      - SOURCE_DIR=/app/spring-cloud/standard
    entrypoint: java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=prod,db-maria-docker /app/spring-cloud/standard/target/standard-1.jar
    depends_on:
      - mariadb

  mariadb:
    image: mariadb
    networks:
      - standard_net
    environment:
      - MYSQL_DATABASE=prod
      - MYSQL_USER=user01
      - MYSQL_PASSWORD=user01
      - MYSQL_ROOT_PASSWORD=password
    volumes:
      - /app/mariadb:/var/lib/mysql

networks:
  standard_net:
```
<br />
<br />

### Docker Compose UP
작업 파일 ```05_UP_DOCKER_COMPOSE.sh``` 을 vi 편집기로 생성 후 다음 내용을 입력 저장합니다.
```
#! /usr/bin/bash
. ./00_SET_ENV

docker compose -f docker-compose.yml up -d
```
<br />

권한 부여 후 파일을 실행합니다.
```
chmod +x 05_UP_DOCKER_COMPOSE.sh

./05_UP_DOCKER_COMPOSE.sh
```
<br />
<br />

## 기타 Bash Shell Script
위 스크립트 외 종료, 삭제, 리빌드, 이미지 삭제 등의 스크립트 파일에 대한 설명입니다.   
<br />

vi 편집기로 생성 후 권한을 빌드하여 실행하는 방법은 동일하므로, 파일 내용만을 설명합니다.
<br />
<br />

```06_STOP_DOCKER_COMPOSE.sh```
```
#! /usr/bin/bash
. ./00_SET_ENV

docker compose -f docker-compose.yml stop
```
Docker Compose를 종료합니다.
<br />
<br />

```07_DOWN_DOCKER_COMPOSE.sh```
```
#! /usr/bin/bash
. ./00_SET_ENV

docker compose -f docker-compose.yml down
```
Docker Compose를 종료하고 삭제합니다.
<br />
<br />

```08_REBUILD_DOCKER_COMPOSE.sh```
```
#! /usr/bin/bash
. ./00_SET_ENV

./06_STOP_DOCKER_COMPOSE.sh
./02_BUILD.sh
./05_UP_DOCKER_COMPOSE.sh
```
웹 어플리케이션의 수정이 있을 경우 사용하는 스크립트입니다.   
내용을 보면 Docker Compose를 종료하고 빌드를 수행하여 Docker Compose를 UP 하는 스크립트를 차례대로 수행합니다.
<br />
<br />

```09_DOCKER_COMPOSE.sh```
```
#! /usr/bin/bash
. ./00_SET_ENV

./07_DOWN_DOCKER_COMPOSE.sh

docker rmi $IMAGE_NAME
```
Docker Compose를 종료하고, 컨테이너 이미지를 삭제합니다.
<br />
<br />
<br />
<br />

이중화 구성의 모든 과정은 유튜브 강의 영상을 참고하였습니다.
https://www.youtube.com/playlist?list=PLogzC_RPf25E9qprqOIDTzwZ24PuEf-1v

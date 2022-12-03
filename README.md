# nginx-redundant | nginx 로드밸런싱을 이용한 WAS 이중화

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


편의를 위해 Bash Shell Script를 사용하여 작업 파일을 생성합니다.   
<br />

환경 변수 파일 ```00_SET_ENV``` 을 vi 편집기로 생성 후 다음 내용을 입력합니다.
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

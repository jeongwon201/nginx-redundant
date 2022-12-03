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



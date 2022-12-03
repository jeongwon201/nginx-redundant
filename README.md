# nginx-redundant | nginx 로드밸런싱을 이용한 WAS 이중화

nginx의 로드밸런싱 기능과 도커를 이용하여 하나의 웹 어플리케이션 서버를 이중화합니다.
<br />
<br />

업로드된 웹 어플리케이션은 간단한 CRUD 기능을 수행합니다.   
- Build Tool: Maven
- Language: Java
- Framework: SpringBoot
- Database: MariaDB
<br />
<br />


빌드된 Jar 파일을 이중화된 각 컨테이너에 복사하여 배포합니다.   
<br />
nginx 서버 컨테이너는 클라이언트의 요청 시 라운드 로빈 방식으로 로드밸런싱하여 각 WAS 컨테이너에 번갈아가면서 요청을 하게 됩니다.
<div align="center">
  <img src="https://user-images.githubusercontent.com/81132541/205433740-75d311ee-66ad-4dfc-b805-bc7c2eedfa26.png" width="450px">
</div>
<br />

인증 서버
===========
노바북 모든 인증 과정을 담당하는 서버입니다
로그인시 인증서버에서 검증하고 JWT 토큰을 발급합니다 JWT토큰은 사용자의 권한을 나타내는 토큰으로 UUID값으로 토큰을 발급합니다



아키텍쳐
========

![노바북아키텍처 (1)](https://github.com/user-attachments/assets/b6ad1505-66b0-438d-98fd-e63e73f58207)




로그인 인증 과정
=============

<p align="center">
  <img src="https://github.com/user-attachments/assets/6c6e4597-0dd4-435c-bf1a-11a3256b9aaa" alt="스크린샷 2024-07-21 오전 4 31 20">
</p>


로그인 후
==========
### 쿠키
* 브라우저의 쿠키에 토큰 저장
![스크린샷 2024-07-21 오전 4 09 29](https://github.com/user-attachments/assets/34614045-0d1c-49d3-a1a6-a206ef812fff)


토큰 내부 정보
============
### 토큰
* UUID를 활용한 유저 정보 암호화
* 레디스에 정보 저장



![스크린샷 2024-07-21 오전 4 11 07](https://github.com/user-attachments/assets/11d4a5c0-ab04-4e3e-8a0a-6c58995002ad)




토큰 만료
==========
### 리프레시 토큰

* 액세스 토큰 만료시 리프레시 토큰으로 재발급
  
<p align="center">
  <img src="https://github.com/user-attachments/assets/fb11b674-2d04-4ce2-9cff-de53c18511ea" alt="스크린샷 2024-07-21 오전 4 39 48">
</p>

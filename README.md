# Location-Recording-App
💡 [ Protfolio Project 008] 위치 기록 앱 제작 프로젝트 

## 📌 프로젝트 소개
이 프로젝트는 사용자의 위치를 백그라운드에서 주기적으로 추적하여 데이터를 기록하고, 구글 맵을 통해 이동 경로를 시각화하는 안드로이드 앱입니다. 사용자는 캘린더를 통해 특정 날짜의 이동 기록을 조회할 수 있으며, 전체 경로 보기와 경로 검색 기능을 통해 위치 정보를 손쉽게 확인할 수 있습니다. 위치 데이터는 SQLite를 이용해 로컬에 저장되며, MariaDB 서버를 통해 원격으로 백업 및 동기화가 가능합니다.

![회원가입](https://github.com/user-attachments/assets/4267129e-8fc1-4ba5-a1b8-f0662c9d835c) &emsp; 
![웹-동작-동영상](https://github.com/user-attachments/assets/08d846d2-96e6-46ee-9fed-163d8b40e6fe)

## 📌 폴더 구조
        📂 src/main
        ┣ 📂 ../java/com/example/b2203098_1030
        ┃ ┣ 📜 Constants.java             # 정의 및 서비스 관리
        ┃ ┣ 📜 ListViewAdapter.java       # 리스트뷰 어댑터 설정
        ┃ ┣ 📜 ListViewItem.java          # 리스트뷰 항목 데이터 정의
        ┃ ┣ 📜 LocationService.java       # 백그라운드 위치 추적 및 DB 삽입, 수정
        ┃ ┣ 📜 LogInView.java             # 사용자 로그인 처리 및 웹 DB 연동
        ┃ ┣ 🔎 MainActivity.java          # 메인 화면 구성 및 캘린더 뷰 관리
        ┃ ┣ 📜 Member.java                # 회원 데이터 클래스
        ┃ ┣ 📜 RouteView.java             # 구글 맵을 통한 경로 시각화
        ┃ ┣ 📜 SearchView.java            # 날짜 및 경로 검색 기능
        ┃ ┣ 📜 SignUpView.java            # 회원가입 기능 구현
        ┣ 📂 /res/layout
        ┃ ┣ 🔎 activity_main.xml          # 메인 화면 레이아웃 설정
        ┃ ┣ 📜 listview_item.xml          # 리스트뷰 항목 레이아웃
        ┃ ┣ 📜 login.xml                  # 로그인 화면 레이아웃
        ┃ ┣ 📜 route_view.xml             # 구글 맵 경로 시각화 레이아웃
        ┃ ┣ 📜 search_data.xml            # 검색 화면 레이아웃
        ┃ ┣ 📜 signup.xml                 # 회원가입 화면 레이아웃
        
        📂 web
        ┣ 🔎 login.php          # 로그인 페이지
        ┣ 📜 loginCheck.php     # 로그인 인증 처리
        ┣ 📜 logout.php         # 로그아웃 처리
        ┣ 📜 insert.php         # 데이터 삽입 페이지
        ┣ 📜 insert_ok.php      # 데이터 삽입 처리
        ┣ 📜 delete_ok.php      # 데이터 삭제 처리
        ┣ 📜 modify.php         # 데이터 수정 페이지
        ┣ 📜 modify_ok.php      # 데이터 수정 처리
        ┣ 📜 json.php           # JSON 형태로 데이터 제공
        ┣ 📜 phonebook.php      # 사용자 전화번호부 조회 페이지

 ## 📌 데이터베이스 구조
- numID: 고유 번호
- locDate: 날짜
- locTime: 시간
- latitude: 위도
- longitude: 경도
- place: 도로명 주소
- timeSpent: 머문 시간
- phoneNum: 사용자 전화번호
   
![db](https://github.com/user-attachments/assets/67778e69-6a0d-431d-808e-a9b6a484352d)

## 📌 주요 기능
### - 사용자 로그인 및 회원가입:
- LogInView.java를 통해 사용자는 웹 서버에 저장된 회원 정보를 바탕으로 로그인할 수 있습니다.  
- JSON 파싱을 사용하여 서버에 있는 데이터를 불러오고 사용자 입력값을 검증합니다.  
- SignUpView.java에서는 회원가입을 진행할 수 있으며, 입력받은 id와 pw는 웹 DB에 저장됩니다.  

### - 위치 추적 및 기록:
- LocationService.java는 1분마다 사용자의 위치를 추적하며, 50미터 이상 이동하거나 10분 이상 머무른 경우 위치 데이터를 기록합니다.
- 위치 데이터는 SQLite에 우선 저장되고, 그 후 MariaDB 서버로 백업 및 동기화됩니다.

### - 캘린더 기반 이동 기록 조회:
- 사용자는 MainActivity.java에서 캘린더를 통해 특정 날짜의 기록을 조회할 수 있습니다.
- 각 날짜의 기록은 리스트뷰로 표시되며, 선택된 항목을 클릭하면 해당 위치의 좌표가 구글 맵에 표시됩니다.

### - 구글 맵 경로 시각화:
RouteView.java를 통해 사용자의 특정 날짜의 이동 경로를 구글 맵에 표시하며, 마커를 클릭하면 해당 위치에서 머문 시간과 주소가 표시됩니다. 경로는 하얀색 실선으로 연결됩니다.

### - 검색 기능:
SearchView.java는 날짜 또는 장소 이름으로 이동 기록을 검색할 수 있습니다. 검색 결과는 리스트뷰에 표시되며, 클릭 시 해당 경로가 구글 맵에 표시됩니다. 

### - 데이터베이스 관리 및 동기화:
- 앱은 SQLite를 이용한 로컬 데이터베이스를 통해 위치 기록을 관리하고, MariaDB를 이용해 원격 서버에 데이터를 백업합니다.  
- 데이터는 날짜별 또는 장소 이름으로 검색할 수 있으며, 데이터의 삽입, 수정, 삭제가 가능합니다.  

## 📌 구현 상세
### - MainActivity.java:
앱의 메인 화면을 구성하며, 캘린더 뷰와 리스트뷰를 통해 날짜별 이동 기록을 조회할 수 있습니다. 기록을 클릭하면 구글 맵에 해당 좌표가 표시되고, 전체 경로 보기 기능을 통해 해당 날짜의 전체 이동 경로를 시각화합니다.  

### - LocationService.java:
백그라운드에서 위치 추적을 담당하며, 사용자가 일정 위치에서 10분 이상 머물렀는지 또는 50미터 이상 이동했는지를 확인합니다. 위치 기록은 SQLite에 저장된 후 MariaDB 서버로 동기화됩니다.  

### - RouteView.java:
구글 맵을 사용해 특정 날짜의 이동 경로를 시각화하며, 경로마다 마커를 배치하여 해당 위치에서 머문 시간과 주소를 보여줍니다.  

### - LogInView.java
사용자가 웹 DB의 회원 정보를 바탕으로 로그인할 수 있는 기능을 제공합니다. JSON 데이터를 파싱하여 사용자 입력값을 검증하고, 로그인 성공 시 MainActivity로 이동합니다.  

### - SignUpView.java:
사용자가 회원가입을 할 수 있도록 하며, 입력된 데이터는 웹 DB에 저장됩니다.  

### - SearchView.java:
특정 날짜나 장소 이름으로 검색할 수 있으며, 검색 결과는 리스트뷰로 표시됩니다. 해당 기록을 클릭하면 구글 맵에서 경로를 확인할 수 있습니다.  

## 📌 개발 환경
### - 언어 및 환경
- Java  
- Android Studio

### - 데이터 처리 및 DB
- iwinv 웹 호스팅 (MariaDB 10.X)  
- phpMyAdmin  
- SQLite  

### - 개발 도구
- Google Maps API  
- Geocoder  
- PHP  

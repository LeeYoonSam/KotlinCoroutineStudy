# 코루틴 핸즈 온(코드랩)

[Kotlin Playground: Edit, Run, Share Kotlin Code Online](https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/01_Introduction)

[https://github.com/kotlin-hands-on/intro-coroutines](https://github.com/kotlin-hands-on/intro-coroutines)

1. [코드랩 레파지토리 복제](https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/01_Introduction)
2. [깃허브 개발자 토큰 생성](https://github.com/settings/tokens/new)
    - 노트만 입력하고 Generate
    - 30일 기한
3. 프로젝트 열고 main 함수 실행
    - src/contributors/main.kt 에서 main 함수 실행
    - 깃허브 컨트리뷰터 창이 나옴 (스윙)
        - UserName / 발급받은 Token 입력 후 `Load Contributors` 클릭
        - kotlin 이라는 Organization 에서 누가 얼만큼의 기여를 했는지 보는것
    - 조회할때 Blocking 방식으로 조회하는데 이것을 리팩토링하면서 프로젝트를 완성
4. 코드랩에 나와있는대로 실습


# 안드로이드 코루틴 핸즈 온

### 스타트 코드
[https://github.com/dalinaum/fastcampus-coroutines](https://github.com/dalinaum/fastcampus-coroutines)

### 완성 코드
[https://github.com/LeeYoonSam/FastCampus-KotlinCoroutineFlow](https://github.com/LeeYoonSam/FastCampus-KotlinCoroutineFlow)

### 커밋 로그
- 즐겨찾기 adapter 추가 및 ViewModelStoreOwner 공통으로 변경
- 즐겨찾기 프래그먼트 추가 및 섹션 페이지 어댑터 프래그먼트 연결 수정
- 탭 네이밍 변경(이미지 검색, 즐겨찾기)
- 이미지 검색 결과로 어댑터 업데이트
- PagerAdapter 프레그먼트 생성 추가
- ImageSearchAdapter - DiffUtil.ItemCallback 구현
- ImageSearchViewHolder bind 에서 Glide 를 사용해서 이미지 로드 및 이미지 클릭시 like 함수 실행
- 검색 기능 추가(버튼 클릭시 뷰모델 검색 호출)
- 네이버 API 사용시 필요한 헤더 추가, 응답값 일부 SerializedName 으로 받아서 네이밍 변경

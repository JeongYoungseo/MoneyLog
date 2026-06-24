# MoneyLog - Android 가계부 애플리케이션

## 프로젝트 소개

MoneyLog는 사용자의 수입과 지출을 기록하고 관리할 수 있는 안드로이드 가계부 애플리케이션입니다. 거래 내역 등록, 통계 확인, 설정 관리 기능을 제공하여 효율적인 자산 관리를 지원합니다.

## 개발 환경

* Language : Java
* IDE : Android Studio
* Platform : Android
* Data Storage : SharedPreferences

## 주요 기능

### 1. 거래 내역 관리

* 수입 및 지출 내역 등록
* 거래 금액, 카테고리, 메모 입력
* 거래 내역 목록 조회

### 2. 통계 기능

* 수입 및 지출 현황 분석
* 카테고리별 소비 통계 확인
* 월별 지출 현황 확인

### 3. 설정 기능

* 사용자 설정 관리
* 애플리케이션 환경설정 제공

## Activity 구성

| Activity               | 설명               |
| ---------------------- | ---------------- |
| MainActivity           | 메인 화면 및 거래 내역 조회 |
| AddTransactionActivity | 수입/지출 내역 등록      |
| StatisticsActivity     | 통계 정보 확인         |
| SettingsActivity       | 앱 설정 관리          |

## Activity 간 데이터 전송

본 프로젝트는 Android Intent를 활용하여 Activity 간 데이터를 전달합니다.

### 사용 기술

* Intent
* putExtra()
* getSerializableExtra()
* ActivityResultLauncher

### 데이터 전달 예시

* 거래 정보(Transaction) 전달
* 거래 목록(TransactionList) 전달
* 등록 완료 후 결과 데이터 반환

## 기대 효과

* 개인 수입·지출 관리의 편의성 향상
* 소비 패턴 분석을 통한 효율적인 자산 관리
* 직관적인 UI를 통한 쉬운 사용 경험 제공

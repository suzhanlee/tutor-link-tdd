### 1. 시퀀스 정리
```mermaid
sequenceDiagram
    participant Student
    participant Teacher
    participant TeacherService
    participant TeachingClassService
    participant StudentService
    participant PaymentService
    participant ReviewService

    Teacher ->> TeacherService: 선생님 등록 요청
    TeacherService -->> Teacher: 등록 완료

    Teacher ->> TeachingClassService: 클래스 등록 요청
    TeachingClassService -->> Teacher: 등록 완료

    Student ->> StudentService: 클래스 신청
    StudentService ->> TeachingClassService: 신청 요청 전달
    TeachingClassService -->> StudentService: 신청 접수 완료
    StudentService -->> Student: 신청 완료

    Teacher ->> TeacherService: 신청 승인 요청
    TeacherService ->> TeachingClassService: 신청 승인 처리
    TeachingClassService -->> TeacherService: 승인 완료
    TeacherService -->> StudentService: 승인 알림
    StudentService -->> Student: 승인 알림 전달

    Student ->> PaymentService: 결제 요청
    PaymentService -->> Student: 결제 완료

    TeachingClassService ->> TeachingClassService: 수강 기간 만료 확인
    TeachingClassService -->> Student: 수강 완료 처리

    Student ->> ReviewService: 리뷰 작성 요청
    ReviewService -->> Student: 리뷰 등록 완료

```


### 2. 초기 연관관계 구조 정리

```
Teacher <-1 : N-> TeachingClass
TeachingClass <-1 : N-> Review 
Student <-1 : N-> ClassRegistration
ClassRegistration <-1 : 1-> Payment
```

### 3. 유스케이스 책임 나누기

```
- TeacherService
  - 선생님을 등록할 수 있다.
  - 학생의 클래스 신청을 승인할 수 있다.
- TeachingClassService
  - 클래스를 등록할 수 있다.
  - 클래스 신청 승인 여부를 학생에게 알려줄 수 있다.
  - 클래스의 마감 여부를 확인해 클래스 상태(-> 수강 완료)를 변경할 수 있다.
- StudentService
  - 학생은 클래스를 신청할 수 있다.
- ReviewService
  - 클래스의 리뷰를 작성할 수 있다.
- PaymentService
  - 클래스 등록 정보를 통해 결제를 할 수 있다.
```
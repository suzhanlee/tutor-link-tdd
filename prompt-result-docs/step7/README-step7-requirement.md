---

## 🎯 Step 7: Student 도메인 설계 – 학습자 등록/조회, 중복 제한 및 식별자 기반 모델링

> “과외 신청의 핵심 주체인 학생(Student)을 도메인/엔티티 분리 구조로 모델링하고, 이메일 중복 제한, 식별자 도입, 상태 기반 책임 분리를 명확히 테스트 기반으로 구현한다.”

---

### 🧭 STICC 기반 미션 세트

* **Situation**: 서비스에는 학생 유저가 존재하며, 이들은 클래스에 신청하거나 리뷰를 작성하는 주체로 등장한다.
* **Task**: Student 도메인을 정의하고, 이메일 중복을 방지하며, 가입 유효성을 검증하고, 도메인 객체 ↔ Entity 구조를 테스트 기반으로 구현한다.
* **Intent**: 모든 유스케이스의 기반이 되는 사용자 모델을 견고하게 설계하고, 조회/검증/식별 전략을 명확히 한다.
* **Concerns**:

    * 동일 이메일 중복 가입 방지
    * 학생 ID는 별도 식별자 객체로 관리
    * 상태: `ACTIVE`, `INACTIVE` 상태 기반 행위 제한
    * 이름/이메일 유효성 검증
* **Calibration**: 이후 ApplyClass, Review 작성, Payment 흐름 등 모든 유스케이스에서 이 도메인이 핵심 주체로 작동

---

### 🛠 \[작업 지시] Instruction

#### 📁 1. 도메인 객체 정의

```java
public class Student {
    private final StudentId id;
    private final String name;
    private final Email email;
    private final StudentStatus status;

    public Student(StudentId id, String name, Email email, StudentStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }

    // getter, equals, hashCode 등
}
```

```java
public enum StudentStatus {
    ACTIVE, INACTIVE
}
```

```java
public class StudentId {
    private final Long value;

    public StudentId(Long value) {
        this.value = value;
    }

    public Long value() {
        return value;
    }
    // equals, hashCode
}
```

```java
public class Email {
    private final String value;

    public Email(String value) {
        if (!value.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            throw new InvalidEmailException(value);
        }
        this.value = value;
    }

    public String value() {
        return value;
    }
}
```

```java
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email) {
        super("유효하지 않은 이메일 형식입니다: " + email);
    }
}
```

---

#### 📁 2. JPA Entity 정의

```java

@Entity
@Table(name = "students")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    protected StudentEntity() {
    }

    public StudentEntity(String name, String email, StudentStatus status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }

    // getter
}
```

---

#### 📁 3. Mapper 정의

```java
public class StudentMapper {

    public static Student toDomain(StudentEntity entity) {
        return new Student(
                new StudentId(entity.getId()),
                entity.getName(),
                new Email(entity.getEmail()),
                entity.getStatus()
        );
    }

    public static StudentEntity toEntity(Student domain) {
        return new StudentEntity(
                domain.getName(),
                domain.getEmail().value(),
                domain.getStatus()
        );
    }
}
```

---

#### 🧪 4. 테스트 케이스 예시

```java
class StudentTest {

    @Test
    void 유효한_이메일로_Student_생성_성공() {
        Student student = new Student(
                new StudentId(1L),
                "김철수",
                new Email("chulsoo@example.com"),
                StudentStatus.ACTIVE
        );

        assertTrue(student.isActive());
    }

    @Test
    void 잘못된_이메일은_예외_발생() {
        assertThrows(InvalidEmailException.class, () -> {
            new Email("no-at-symbol.com");
        });
    }
}
```

---

### ✅ Clear Completion Criteria

* [x] Student 도메인 + Entity 분리 정의
* [x] Email 값 객체 + 유효성 검증
* [x] StudentId 식별자 객체 적용
* [x] 상태 기반 메서드 (`isActive`) 포함
* [x] Mapper 테스트 기반 변환

---

### 💡 Strategic Hint

* Email은 값 객체로 처리하면 추후 비교/정렬/중복 검증에 큰 도움이 됩니다.
* 상태 패턴은 이후 리뷰 작성, 클래스 신청에서 유효성 검증으로 재사용됩니다.
* Student는 Entity 기준으로 조회하지만, 도메인에서는 행동 가능 여부를 상태로 명시적으로 제어합니다.

---

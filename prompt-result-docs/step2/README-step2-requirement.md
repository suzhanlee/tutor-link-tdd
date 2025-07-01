---

## 🎯 Step 2: Teacher 도메인 객체 및 JPA Entity 분리 설계

> “과외 플랫폼의 핵심 주체인 ‘선생님’을 도메인 객체와 JPA Entity로 분리 설계하고, 이 구조를 기반으로 TDD의 뼈대를 형성한다.”

---

### 🧭 STICC 기반 미션 세트

* **Situation**: 멀티 모듈 환경에서 Domain과 Persistence(Entity)가 분리되어 있고, Teacher는 시스템의 핵심 사용자다.
* **Task**: `Teacher`를 도메인 객체와 JPA Entity로 나누어 설계하고, 이 두 계층 간 변환을 위한 패턴과 테스트 기반 구조를 만든다.
* **Intent**: 도메인 로직은 Entity가 아닌 도메인 객체에만 존재하도록 하여, 설계 분리를 명확히 하고 추후 테스트 및 유지보수를 용이하게 한다.
* **Concerns**: Entity ↔ Domain 간 ID 참조, 클래스 목록 연관관계 설계, 그리고 변환 로직에서 실수하기 쉬운 부분을 학습한다.
* **Calibration**: 이후의 모든 시나리오(클래스 등록 등)의 기반이 되므로, 불변성과 구조 설계를 신중히 해야 한다.

---

### 🛠 \[작업 지시] Instruction

이번 Step에서는 아래를 구현합니다:

#### 1. 도메인 모델 설계

```java
// 도메인 객체
public class Teacher {
    private final TeacherId id;
    private final String name;
    private final String subject;

    public Teacher(TeacherId id, String name, String subject) {
        this.id = id;
        this.name = name;
        this.subject = subject;
    }

    // Getter, equals, hashCode
}
```

```java
// 값 객체
public class TeacherId {
    private final Long value;

    public TeacherId(Long value) {
        this.value = value;
    }

    public Long value() {
        return value;
    }

    // equals, hashCode
}
```

#### 2. JPA Entity 설계

```java
@Entity
@Table(name = "teachers")
public class TeacherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String subject;

    // JPA를 위한 protected 생성자
    protected TeacherEntity() {}

    public TeacherEntity(String name, String subject) {
        this.name = name;
        this.subject = subject;
    }

    // Getter
}
```

#### 3. Mapper 설계 (Entity ↔ Domain)

```java
public class TeacherMapper {
    public static Teacher toDomain(TeacherEntity entity) {
        return new Teacher(
            new TeacherId(entity.getId()),
            entity.getName(),
            entity.getSubject()
        );
    }

    public static TeacherEntity toEntity(Teacher domain) {
        return new TeacherEntity(
            domain.getName(),
            domain.getSubject()
        );
    }
}
```

#### 4. TDD로 명세할 테스트 클래스 구조

```java
class TeacherMapperTest {

    @Test
    void entity_to_domain_변환_성공() {
        TeacherEntity entity = new TeacherEntity("이상훈", "수학");
        ReflectionTestUtils.setField(entity, "id", 1L);

        Teacher teacher = TeacherMapper.toDomain(entity);

        assertEquals(new TeacherId(1L), teacher.getId());
        assertEquals("이상훈", teacher.getName());
        assertEquals("수학", teacher.getSubject());
    }

    @Test
    void domain_to_entity_변환_성공() {
        Teacher teacher = new Teacher(new TeacherId(2L), "홍길동", "영어");

        TeacherEntity entity = TeacherMapper.toEntity(teacher);

        assertEquals("홍길동", entity.getName());
        assertEquals("영어", entity.getSubject());
    }
}
```

---

### ✅ Clear Completion Criteria

* [x] `Teacher` 도메인 객체와 `TeacherEntity`를 각각 정의했다
* [x] 값 객체(`TeacherId`)를 사용해 ID를 감쌌다
* [x] Mapper를 통해 도메인 ↔ 엔티티 간 변환을 명시적으로 구현했다
* [x] 테스트 코드로 두 방향 변환을 명확히 검증했다

---

### 💡 Strategic Hint

* Entity는 JPA에서 관리되는 **변경 가능한 객체**, Domain은 불변성과 책임 중심으로 간결하게 설계합니다.
* ID 매핑은 초기에는 단순히 `Long`으로 처리하기 쉬우나, **값 객체로 감쌀 경우 타입 안정성과 테스트 용이성**이 향상됩니다.
* 변환 책임을 Mapper로 분리하면, 이후 연관관계가 복잡해져도 테스트를 각각 독립시킬 수 있습니다.

---

### 🧠 Expert Micro-Sequence

시니어 개발자들은 다음 흐름을 통해 분리 구조를 만듭니다:

1. **도메인 먼저 설계**: "Teacher는 어떤 정보를 갖는가?"로부터 시작
2. **JPA Entity는 기술적 보존용**: 도메인에 맞춰 유연하게 변경될 수 있어야 함
3. **ID를 감싸기**: Long → `TeacherId`로 추상화 → 테스트/리팩터링 유리
4. **Mapper 분리**: 서비스 계층 로직과 격리

---

### 🔁 자기 피드백 루틴

* “이 테스트를 작성하며 가장 헷갈린 부분은?”
* “이 구현에서 책임 분리를 점검하려면 어떤 질문을 해보면 좋을까?”
* “이 테스트가 실패했을 때, 그 원인을 어떻게 추적했는가?”

---

이제 `Step 3: Teacher 등록 서비스 (Outside-in TDD)`로 넘어가, 위 구조를 기반으로 실제 등록 흐름을 TDD로 만들어보겠습니다. 계속 진행할까요?

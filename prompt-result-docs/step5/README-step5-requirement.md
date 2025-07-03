---

## 🎯 Step 5 (강화): 클래스 등록 TDD – 연관관계, 정책, 상태 유효성 기반 설계

> “TeacherService를 중심으로, 클래스 등록 시 클래스 수 제한, 제목 길이 제한, 등록 가능 시간 검증, 연관 Teacher 유효성 검증 등의 도메인 정책을 테스트 우선으로 명세하고 구현한다.”

---

### 🧭 STICC 기반 미션 세트

* **Situation**: Teacher와 TeachingClass 간 1\:N 관계가 정의되어 있으며, 클래스 등록 시 다양한 제약 조건이 주어진다.
* **Task**: 등록 요청 커맨드를 받아, 정책 조건을 점검하고, Teacher에 새로운 클래스를 추가한 후, 영속화한다.
* **Intent**: 실제 서비스에서 발생하는 제약을 TDD로 명세하고, 서비스/도메인 계층을 분리된 책임 하에 구현하는 훈련이다.
* **Concerns**:

    * Teacher당 등록 가능 클래스 수는 최대 10개
    * 등록 가능한 시간은 오전 6시 \~ 오후 10시
    * 클래스 제목은 10자 이상 100자 이하
    * Teacher 존재 여부 및 활성 상태 확인
* **Calibration**: 이후 클래스 조회, 수강 신청, 결제 연계까지 확장되는 중심 흐름이므로, 설계의 견고함이 필수적이다.

---

### 🛠 \[작업 지시] Instruction

#### 📁 1. DTO 정의

```java
public record RegisterClassCommand(
    Long teacherId,
    String title,
    String description,
    int price,
    LocalDateTime registeredAt
) {}

public record RegisterClassResult(Long classId) {}
```

---

#### 📁 2. 도메인 정책/예외 정의

```java
public class ClassRegistrationPolicy {
    public static final int MAX_CLASS_PER_TEACHER = 10;
    public static final int MIN_TITLE_LENGTH = 10;
    public static final int MAX_TITLE_LENGTH = 100;

    public static void validate(Teacher teacher, RegisterClassCommand command) {
        if (teacher.getClasses().size() >= MAX_CLASS_PER_TEACHER) {
            throw new ClassRegistrationException("최대 클래스 개수를 초과했습니다.");
        }
        if (command.title().length() < MIN_TITLE_LENGTH || command.title().length() > MAX_TITLE_LENGTH) {
            throw new ClassRegistrationException("클래스 제목은 10자 이상 100자 이하여야 합니다.");
        }
        LocalTime time = command.registeredAt().toLocalTime();
        if (time.isBefore(LocalTime.of(6, 0)) || time.isAfter(LocalTime.of(22, 0))) {
            throw new ClassRegistrationException("등록 가능한 시간은 오전 6시부터 오후 10시까지입니다.");
        }
    }
}
```

```java
public class ClassRegistrationException extends RuntimeException {
    public ClassRegistrationException(String message) {
        super(message);
    }
}
```

---

#### 📁 3. TeacherServiceImpl 구현 흐름

```java
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeachingClassRepository teachingClassRepository;

    @Override
    public RegisterClassResult registerClass(RegisterClassCommand command) {
        Teacher teacher = teacherRepository.findById(new TeacherId(command.teacherId()))
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 선생님입니다."));

        ClassRegistrationPolicy.validate(teacher, command);

        TeachingClass teachingClass = new TeachingClass(
            null,
            command.title(),
            command.description(),
            command.price()
        );

        teacher.addClass(teachingClass); // 도메인 관계 연결

        TeachingClass saved = teachingClassRepository.save(teachingClass);

        return new RegisterClassResult(saved.getId().value());
    }
}
```

---

#### 🧪 4. 테스트 코드 설계 (전략적 TDD 명세)

```java
class TeacherServiceTest {

    @Test
    void 선생님이_정상적으로_클래스를_등록한다() {
        // Given
        Teacher teacher = teacherFixtureWithClasses(3);
        RegisterClassCommand command = new RegisterClassCommand(
            teacher.getId().value(),
            "심화 수학반 A",
            "중2 수학 집중반",
            70000,
            LocalDateTime.of(2025, 6, 25, 9, 0)
        );

        // When
        RegisterClassResult result = teacherService.registerClass(command);

        // Then
        assertNotNull(result.classId());
    }

    @Test
    void 선생님이_클래스를_10개_초과_등록하면_예외발생() {
        Teacher teacher = teacherFixtureWithClasses(10);
        RegisterClassCommand command = validCommand();

        assertThrows(ClassRegistrationException.class, () ->
            teacherService.registerClass(command)
        );
    }

    @Test
    void 클래스_제목이_너무짧으면_예외() {
        RegisterClassCommand command = new RegisterClassCommand(..., "짧음", ..., ...);

        assertThrows(ClassRegistrationException.class, () ->
            teacherService.registerClass(command)
        );
    }

    @Test
    void 등록시간이_밤11시면_예외() {
        RegisterClassCommand command = new RegisterClassCommand(..., ..., ..., ..., LocalDateTime.of(2025, 6, 25, 23, 0));

        assertThrows(ClassRegistrationException.class, () ->
            teacherService.registerClass(command)
        );
    }
}
```

---

### ✅ Clear Completion Criteria

* [x] 도메인 정책 3종 이상 명세 및 검증
* [x] 정상/예외/경계 테스트 포함
* [x] 연관관계 조작 (Teacher → TeachingClass 리스트 추가)
* [x] 예외 메시지 명세화
* [x] 도메인 중심 유효성 분리

---

### 💡 Strategic Hint

* `ClassRegistrationPolicy`는 단순 정책뿐 아니라 추후 교체/확장 가능한 전략 패턴으로 발전할 수 있음
* Teacher 객체의 `addClass(...)`는 도메인 중심 구조 설계에서 매우 중요함
* 테스트는 단일 책임 테스트 + 예외 메시지까지 명세하는 구조를 취함

---
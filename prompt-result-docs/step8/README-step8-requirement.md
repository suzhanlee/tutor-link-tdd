---

## 🎯 Step 8: ApplyClass 요청 처리 – 학생의 클래스 신청 흐름 TDD 설계

> “학생이 특정 클래스를 수강 신청할 수 있도록 하고, 중복 신청 방지, 수강 가능 여부, 상태 관리 흐름을 도메인 객체 기반으로 명세하고 테스트 기반으로 구현한다.”

---

### 🧭 STICC 기반 미션 세트

* **Situation**: 학생이 특정 클래스(TeachingClass)에 수강 신청을 하고자 한다.
* **Task**: 중복 신청을 방지하고, 클래스가 모집 중 상태일 때만 신청 가능하도록 제한하며, 신청 객체를 생성/저장하는 로직을 TDD로 구현한다.
* **Intent**: 신청 상태(대기/승인/거절) 기반 전이 가능성을 고려한 초기 상태 설계와, 핵심 정책 제어 흐름을 도메인 중심으로 설계한다.
* **Concerns**:

    * 동일 학생이 동일 클래스에 중복 신청 불가
    * 클래스가 `RECRUITING` 상태여야 신청 가능
    * 신청 시점과 클래스 상태 간 정합성 보장
* **Calibration**: 이 로직은 이후 승인/결제/수강 기록 생성의 기반이 되므로, 단단한 설계가 필수

---

### 🛠 \[작업 지시] Instruction

#### 📁 1. 도메인 객체 정의

```java
public class ClassApplication {
    private final ClassApplicationId id;
    private final StudentId studentId;
    private final TeachingClassId classId;
    private final ClassApplicationStatus status;
    private final LocalDateTime appliedAt;

    public ClassApplication(ClassApplicationId id, StudentId studentId, TeachingClassId classId, ClassApplicationStatus status, LocalDateTime appliedAt) {
        this.id = id;
        this.studentId = studentId;
        this.classId = classId;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public boolean isPending() {
        return status == ClassApplicationStatus.PENDING;
    }

    public boolean isFor(StudentId studentId, TeachingClassId classId) {
        return this.studentId.equals(studentId) && this.classId.equals(classId);
    }
}
```

```java
public enum ClassApplicationStatus {
    PENDING, APPROVED, REJECTED
}
```

---

#### 📁 2. 커맨드 및 결과 DTO

```java
public record ApplyClassCommand(Long studentId, Long classId) {
}

public record ApplyClassResult(Long applicationId) {
}
```

---

#### 📁 3. 서비스 계층 흐름

```java
public interface ClassApplicationService {
    ApplyClassResult apply(ApplyClassCommand command);
}
```

```java

@Service
public class ClassApplicationServiceImpl implements ClassApplicationService {

    private final StudentRepository studentRepository;
    private final TeachingClassRepository teachingClassRepository;
    private final ClassApplicationRepository applicationRepository;

    @Override
    public ApplyClassResult apply(ApplyClassCommand command) {
        Student student = studentRepository.findById(new StudentId(command.studentId()))
                .orElseThrow(() -> new RuntimeException("학생 없음"));

        TeachingClass teachingClass = teachingClassRepository.findById(new TeachingClassId(command.classId()))
                .orElseThrow(() -> new RuntimeException("클래스 없음"));

        if (!teachingClass.isRecruiting()) {
            throw new IllegalStateException("모집 중인 클래스가 아닙니다.");
        }

        boolean alreadyApplied = applicationRepository.existsBy(student.getId(), teachingClass.getId());
        if (alreadyApplied) {
            throw new IllegalStateException("이미 신청한 클래스입니다.");
        }

        ClassApplication application = new ClassApplication(
                null,
                student.getId(),
                teachingClass.getId(),
                ClassApplicationStatus.PENDING,
                LocalDateTime.now()
        );

        ClassApplication saved = applicationRepository.save(application);
        return new ApplyClassResult(saved.getId().value());
    }
}
```

---

#### 🧪 테스트 케이스 예시

```java
class ClassApplicationServiceTest {

    @Test
    void 학생이_클래스를_정상적으로_신청한다() {
        // Given
        ApplyClassCommand command = new ApplyClassCommand(1L, 100L);

        // When
        ApplyClassResult result = service.apply(command);

        // Then
        assertNotNull(result.applicationId());
    }

    @Test
    void 중복으로_클래스를_신청하면_예외() {
        // Given: 동일 student, classId로 이미 신청된 데이터 존재

        ApplyClassCommand command = new ApplyClassCommand(1L, 100L);

        // When + Then
        assertThrows(IllegalStateException.class, () ->
                service.apply(command)
        );
    }

    @Test
    void 모집중이_아닌_클래스는_신청_불가() {
        // 클래스 상태가 RECRUITING이 아님

        ApplyClassCommand command = new ApplyClassCommand(1L, 200L);

        assertThrows(IllegalStateException.class, () ->
                service.apply(command)
        );
    }
}
```

---

### ✅ Clear Completion Criteria

* [x] 중복 신청 불가 정책 테스트로 검증
* [x] 모집 상태 조건 테스트
* [x] 도메인 상태 `PENDING`으로 생성
* [x] 생성된 신청 도메인 객체를 저장/조회 가능
* [x] 식별자 기반 로직 분리

---

### 💡 Strategic Hint

* 이후 Step 9에서 상태 전이를 담당할 승인/거절 로직으로 확장 가능
* 상태 기반 로직은 enum 기반 조건이 아닌 전략 객체 패턴으로 리팩토링 여지 존재
* 추후 결제 흐름 연결 시 상태 조건 점검은 재사용된다

---

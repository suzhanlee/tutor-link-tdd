---

## 🎯 Step 4: TeachingClass 도메인 모델 정의 및 Teacher 연관 설계

> “클래스(TeachingClass) 도메인 모델을 설계하고, 선생님(Teacher) 도메인과 1\:N 관계를 구성하여 이후의 클래스 등록 기능을 위한 기반을 마련한다.”

---

### 🧭 STICC 기반 미션 세트

* **Situation**: 선생님은 여러 개의 클래스를 등록할 수 있어야 하며, `TeachingClass` 도메인을 새로 설계해야 합니다.
* **Task**: TeachingClass 도메인을 도메인 객체와 JPA Entity로 분리해서 설계하고, Teacher와의 연관관계를 도메인/엔티티 모두에 반영합니다.
* **Intent**: 복잡한 1\:N 관계에서 도메인 중심 설계를 유지하며, 상태 불변성과 Entity 연관관계를 동시에 만족하는 구조를 만들기 위함입니다.
* **Concerns**: JPA 연관관계와 도메인 불변성의 충돌, 리스트 갱신 시 책임 분리, 변경감지에 의존하지 않고 갱신하는 방법을 염두에 둬야 합니다.
* **Calibration**: 이후 Step에서 클래스 등록/조회 흐름이 이 모델 위에서 진행되므로, 확장성과 테스트 가능성을 중심으로 설계합니다.

---

### 🛠 \[작업 지시] Instruction

#### 1. 도메인 모델 정의

```java
public class TeachingClass {
    private final TeachingClassId id;
    private final String title;
    private final String description;
    private final int price;

    public TeachingClass(TeachingClassId id, String title, String description, int price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // getter, equals, hashCode
}
```

```java
public class TeachingClassId {
    private final Long value;

    public TeachingClassId(Long value) {
        this.value = value;
    }

    public Long value() {
        return value;
    }

    // equals, hashCode
}
```

#### 2. `Teacher` 도메인에 클래스 리스트 추가

```java
public class Teacher {
    private final TeacherId id;
    private final String name;
    private final String subject;
    private final List<TeachingClass> classes;

    public Teacher(TeacherId id, String name, String subject, List<TeachingClass> classes) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.classes = classes;
    }

    public List<TeachingClass> getClasses() {
        return classes;
    }

    // 기타 getter, equals 등
}
```

---

#### 3. JPA Entity 설계

```java

@Entity
@Table(name = "teaching_classes")
public class TeachingClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    protected TeachingClassEntity() {
    } // JPA 기본 생성자

    public TeachingClassEntity(String title, String description, int price, TeacherEntity teacher) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.teacher = teacher;
    }
}
```

```java

@Entity
@Table(name = "teachers")
public class TeacherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String subject;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeachingClassEntity> classes = new ArrayList<>();

    protected TeacherEntity() {
    }

    public TeacherEntity(String name, String subject) {
        this.name = name;
        this.subject = subject;
    }

    public void addClass(TeachingClassEntity teachingClass) {
        classes.add(teachingClass);
        teachingClass.setTeacher(this);
    }

    public void clearClasses() {
        for (TeachingClassEntity c : classes) {
            c.setTeacher(null);
        }
        classes.clear();
    }

    public List<TeachingClassEntity> getClasses() {
        return classes;
    }
}
```

---

#### 4. Mapper 구조

* 추후 Step 6에서 `List<TeachingClassEntity>` ↔ `List<TeachingClass>` 변환 구현 예정
* 지금은 양쪽 모델에서 구조만 명확히 갖추는 게 목적

---

### ✅ Clear Completion Criteria

* [x] TeachingClass 도메인, TeachingClassId 정의
* [x] Teacher 도메인에 클래스 리스트 필드 추가
* [x] TeachingClassEntity, TeacherEntity 설계
* [x] Entity 간 연관관계 및 setter 조절 로직 구현
* [x] 리스트 재구성 방식(명시적 추가/제거) 설계됨

---

### 💡 Strategic Hint

* 엔티티 간의 연관관계는 `orphanRemoval = true`를 통해 안전하게 관리
* 도메인에서는 List를 직접 수정하지 않도록 **불변 구조 기반으로 설계**할 예정
* 도메인 ↔ Entity 변환 시에는 기존 리스트를 `clear()` 후 `addAll()` 혹은 **재구성 방식**이 추천됨

---

### 🧠 Expert Micro-Sequence

1. 도메인 먼저 정의: TeachingClass = ID + 제목 + 설명 + 가격
2. Teacher → TeachingClass 리스트 관계 정의
3. Entity로 옮기되, 양방향 연관관계 유지
4. Entity 간 setTeacher 등 명시적 메서드 정의
5. 도메인에서는 리스트 불변 + 테스트 중심 리팩터링 전제

---

### 🔁 자기 피드백 루틴

* 이 구조에서 `TeachingClass`를 수정하거나 재등록할 때 책임은 누구에게 있어야 할까?
* 리스트 전체를 재구성 vs 개별 add/remove 방식 중 어떤 전략이 안전할까?
* 도메인 객체의 클래스 리스트는 왜 직접 수정하지 않도록 만들었을까?

---
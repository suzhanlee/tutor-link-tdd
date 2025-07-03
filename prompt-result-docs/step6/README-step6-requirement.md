---

## 🎯 Step 6: 클래스 목록 조회 – Teacher 기준 TeachingClass 목록, 조건 필터링 포함

> “Teacher가 등록한 모든 클래스를 조회하고, 정렬/필터 조건(예: 가격순, 제목 포함 등)을 적용하는 로직을 테스트 기반으로 설계하고 구현한다.”

---

### 🧭 STICC 기반 미션 세트

* **Situation**: 한 명의 Teacher는 여러 TeachingClass를 등록할 수 있으며, 이를 웹/앱에서 목록으로 보여줘야 한다.
* **Task**: Teacher ID 기준으로 TeachingClass 리스트를 조회하되, 필터링/정렬 조건을 지원하는 Repository + Service + Mapper 체계를 구축한다.
* **Intent**: JPA 엔티티 연관관계를 도메인 객체로 안전하게 변환하고, 페이징/필터/정렬 옵션을 유연하게 처리할 수 있는 테스트 가능한 구조를 설계한다.
* **Concerns**: 연관관계 지연로딩 성능, N+1 방지, Mapper 성능, 정렬 조건 누락 등
* **Calibration**: 이 구조는 이후 학생이 보는 수업 목록, 리뷰 목록과도 동일한 패턴으로 재사용된다.

---

### 🛠 \[작업 지시] Instruction

#### 📁 1. 요청/응답 DTO 정의

```java
public record ClassSearchCondition(
        Long teacherId,
        String keyword,
        SortOption sortOption
) {
    public enum SortOption {
        CREATED_DATE_DESC, PRICE_ASC, PRICE_DESC
    }
}

public record TeachingClassSummary(
        Long classId,
        String title,
        int price
) {
}
```

---

#### 📁 2. 서비스 인터페이스

```java
public interface TeachingClassQueryService {
    List<TeachingClassSummary> findByTeacher(ClassSearchCondition condition);
}
```

---

#### 📁 3. 구현 클래스

```java

@Service
public class TeachingClassQueryServiceImpl implements TeachingClassQueryService {

    private final TeachingClassRepository teachingClassRepository;

    public List<TeachingClassSummary> findByTeacher(ClassSearchCondition condition) {
        List<TeachingClass> classes = teachingClassRepository.findByCondition(condition);
        return classes.stream()
                .map(c -> new TeachingClassSummary(c.getId().value(), c.getTitle(), c.getPrice()))
                .toList();
    }
}
```

---

#### 📁 4. Repository 쿼리 정의 (Querydsl or JPQL)

```java
public interface TeachingClassRepository {
    List<TeachingClass> findByCondition(ClassSearchCondition condition);
}
```

→ Querydsl 기반 예시:

```java
public List<TeachingClass> findByCondition(ClassSearchCondition condition) {
    QTeachingClassEntity c = QTeachingClassEntity.teachingClassEntity;

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(c.teacher.id.eq(condition.teacherId()));

    if (condition.keyword() != null) {
        builder.and(c.title.containsIgnoreCase(condition.keyword()));
    }

    OrderSpecifier<?> order = switch (condition.sortOption()) {
        case PRICE_ASC -> c.price.asc();
        case PRICE_DESC -> c.price.desc();
        default -> c.createdAt.desc();
    };

    return queryFactory.selectFrom(c)
            .where(builder)
            .orderBy(order)
            .fetch()
            .stream()
            .map(TeachingClassMapper::toDomain)
            .toList();
}
```

---

#### 🧪 테스트 명세

```java
class TeachingClassQueryServiceTest {

    @Test
    void 선생님이_등록한_클래스를_최신순으로_조회한다() {
        ClassSearchCondition cond = new ClassSearchCondition(1L, null, CREATED_DATE_DESC);

        List<TeachingClassSummary> result = service.findByTeacher(cond);

        assertThat(result).isSortedAccordingTo(Comparator.comparing(TeachingClassSummary::classId).reversed());
    }

    @Test
    void 키워드가_포함된_클래스만_조회한다() {
        ClassSearchCondition cond = new ClassSearchCondition(1L, "심화", CREATED_DATE_DESC);

        List<TeachingClassSummary> result = service.findByTeacher(cond);

        assertThat(result).allMatch(c -> c.title().contains("심화"));
    }

    @Test
    void 가격순으로_정렬하여_조회할_수_있다() {
        ClassSearchCondition cond = new ClassSearchCondition(1L, null, PRICE_ASC);

        List<TeachingClassSummary> result = service.findByTeacher(cond);

        assertThat(result).isSortedAccordingTo(Comparator.comparing(TeachingClassSummary::price));
    }
}
```

---

### ✅ Clear Completion Criteria

* [x] 조회 요청 조건 + 응답 DTO 정의
* [x] Teacher 기준 연관 클래스 조회 쿼리 정의
* [x] 키워드 필터 / 가격 정렬 / 최신순 정렬 동작 확인
* [x] 도메인 객체로 변환 후 Response DTO로 압축
* [x] N+1 발생 없이 성능 문제 없이 동작

---

### 💡 Strategic Hint

* 도메인 객체로 변환할 때 Lazy 로딩 주의: Entity → Domain 변환은 조회 시점에 모두 완료되도록 fetch join 고려
* Querydsl 사용 시 페이징 적용도 동일 패턴으로 확장 가능
* TeachingClass ↔ Review, TeachingClass ↔ Application에서도 동일 쿼리 패턴 재사용 가능

---

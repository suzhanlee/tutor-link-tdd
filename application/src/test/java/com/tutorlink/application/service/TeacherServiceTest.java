package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.ActiveStatus;
import com.tutorlink.teacher.domain.ClassPolicy;
import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ClassPolicy classPolicy;

    @Test
    @DisplayName("선생님 정보로 선생님을 등록할 수 있다.")
    void registerTeacher() {
        // given
        when(teacherRepository.save(Mockito.any(Teacher.class))).thenReturn(new Teacher(1L, "suchan", null, null));

        // when
        RegisterTeacherResult result = teacherService.registerTeacher(new CreateTeacherCommand("suchan"));

        // then
        ArgumentCaptor<Teacher> teacherArgumentCaptor = ArgumentCaptor.forClass(Teacher.class);
        verify(teacherRepository).save(teacherArgumentCaptor.capture());
        Teacher teacher = teacherArgumentCaptor.getValue();
        assertThat(teacher.name()).isEqualTo("suchan");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("선생님이 클래스를 등록할 수 있다.")
    void registerClass() {
        // given
        Long teacherId = 1L;
        String title = "프로그래밍 기초 클래스";
        String description = "자바 프로그래밍 기초를 배우는 클래스입니다.";
        int price = 50000;
        LocalDateTime registeredAt = LocalDateTime.of(2023, 6, 8, 8, 0); // 오전 8시

        Teacher teacher = new Teacher(teacherId, "suchan", new ArrayList<>(), ActiveStatus.ACTIVE);
        RegisterClassCommand command = new RegisterClassCommand(teacherId, title, description, price, registeredAt);

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Long classId = teacherService.registerClass(command);

        // then
        verify(classPolicy).validate(teacher, command);

        ArgumentCaptor<Teacher> teacherCaptor = ArgumentCaptor.forClass(Teacher.class);
        verify(teacherRepository).save(teacherCaptor.capture());

        Teacher savedTeacher = teacherCaptor.getValue();
        assertThat(savedTeacher.teachingClasses()).hasSize(1);
        TeachingClass registeredClass = savedTeacher.teachingClasses().get(0);
        assertThat(registeredClass.title()).isEqualTo(title);
        assertThat(registeredClass.description()).isEqualTo(description);
        assertThat(registeredClass.price()).isEqualTo(price);
        assertThat(registeredClass.teacherId()).isEqualTo(teacherId);
        assertThat(classId).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 선생님은 클래스를 등록할 수 없다.")
    void registerClass_teacherNotFound() {
        // given
        Long teacherId = 999L;
        RegisterClassCommand command = new RegisterClassCommand(
                teacherId,
                "프로그래밍 기초 클래스",
                "자바 프로그래밍 기초를 배우는 클래스입니다.",
                50000,
                LocalDateTime.of(2023, 6, 8, 8, 0)
        );

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teacherService.registerClass(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("선생님이 존재하지 않습니다");
    }

    @Test
    @DisplayName("클래스 등록 시 정책 검증에 실패하면 예외가 발생한다.")
    void registerClass_policyValidationFails() {
        // given
        Long teacherId = 1L;
        RegisterClassCommand command = new RegisterClassCommand(
                teacherId,
                "짧은제목", // 10자 미만으로 정책 위반
                "자바 프로그래밍 기초를 배우는 클래스입니다.",
                50000,
                LocalDateTime.of(2023, 6, 8, 8, 0)
        );

        Teacher teacher = new Teacher(teacherId, "suchan", new ArrayList<>(), ActiveStatus.ACTIVE);
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        Mockito.doThrow(new IllegalArgumentException("클래스 제목은 10자 이상이어야 합니다."))
                .when(classPolicy).validate(teacher, command);

        // when & then
        assertThatThrownBy(() -> teacherService.registerClass(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("클래스 제목은 10자 이상이어야 합니다");
    }

    @Test
    @DisplayName("선생님이 등록한 클래스 목록을 조회할 수 있다.")
    void getClassesByTeacherId() {
        // given
        Long teacherId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);

        // Create a list of teaching classes
        List<TeachingClass> teachingClasses = new ArrayList<>();
        teachingClasses.add(new TeachingClass(1L, teacherId, "프로그래밍 기초 클래스", "자바 프로그래밍 기초를 배우는 클래스입니다.", 50000, yesterday));
        teachingClasses.add(new TeachingClass(2L, teacherId, "알고리즘 마스터 클래스", "알고리즘 문제 해결 능력을 키우는 클래스입니다.", 70000, now));

        // Create a teacher with the teaching classes
        Teacher teacher = new Teacher(teacherId, "suchan", teachingClasses, ActiveStatus.ACTIVE);

        // Create expected content
        List<ClassMetadataDto> expectedContent = teachingClasses.stream()
                .map(tc -> new ClassMetadataDto(
                        tc.id(),
                        tc.teacherId(),
                        tc.title(),
                        tc.description(),
                        tc.price(),
                        tc.registeredAt()
                ))
                .toList();
        PageResponse<ClassMetadataDto> expectedPage = PageResponse.of(
                expectedContent,
                0,
                expectedContent.size(),
                expectedContent.size(),
                1
        );

        // Mock the repository to return the teacher and the page
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findClassesByTeacherId(eq(teacherId), any(ClassSearchCondition.class)))
                .thenReturn(expectedPage);

        // when
        List<ClassMetadataDto> result = teacherService.getClassesByTeacherId(teacherId, null);

        // then
        assertThat(result).hasSize(2);

        // Verify first class
        ClassMetadataDto firstClass = result.get(0);
        assertThat(firstClass.id()).isEqualTo(1L);
        assertThat(firstClass.teacherId()).isEqualTo(teacherId);
        assertThat(firstClass.title()).isEqualTo("프로그래밍 기초 클래스");
        assertThat(firstClass.description()).isEqualTo("자바 프로그래밍 기초를 배우는 클래스입니다.");
        assertThat(firstClass.price()).isEqualTo(50000);
        assertThat(firstClass.registeredAt()).isEqualTo(yesterday);

        // Verify second class
        ClassMetadataDto secondClass = result.get(1);
        assertThat(secondClass.id()).isEqualTo(2L);
        assertThat(secondClass.teacherId()).isEqualTo(teacherId);
        assertThat(secondClass.title()).isEqualTo("알고리즘 마스터 클래스");
        assertThat(secondClass.description()).isEqualTo("알고리즘 문제 해결 능력을 키우는 클래스입니다.");
        assertThat(secondClass.price()).isEqualTo(70000);
        assertThat(secondClass.registeredAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("존재하지 않는 선생님의 클래스 목록을 조회하면 예외가 발생한다.")
    void getClassesByTeacherId_teacherNotFound() {
        // given
        Long nonExistentTeacherId = 999L;
        when(teacherRepository.findById(nonExistentTeacherId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teacherService.getClassesByTeacherId(nonExistentTeacherId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("선생님이 존재하지 않습니다");
    }

    @ParameterizedTest
    @DisplayName("제목 키워드로 클래스를 필터링할 수 있다.")
    @CsvSource({
            "프로그래밍, 2", // 키워드가 두 클래스 모두에 포함됨
            "기초, 1",      // 키워드가 첫 번째 클래스에만 포함됨
            "알고리즘, 1",   // 키워드가 두 번째 클래스에만 포함됨
            "마스터, 1",     // 키워드가 두 번째 클래스에만 포함됨
            "자바, 0",      // 키워드가 어떤 클래스에도 포함되지 않음
            ", 2"           // 키워드가 없으면 모든 클래스 반환
    })
    void getClassesByTeacherIdWithTitleKeyword(String keyword, int expectedCount) {
        // given
        Long teacherId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);

        // Create a list of teaching classes
        List<TeachingClass> teachingClasses = new ArrayList<>();
        teachingClasses.add(new TeachingClass(1L, teacherId, "프로그래밍 기초 클래스", "자바 프로그래밍 기초를 배우는 클래스입니다.", 50000, yesterday));
        teachingClasses.add(new TeachingClass(2L, teacherId, "알고리즘 마스터 프로그래밍", "알고리즘 문제 해결 능력을 키우는 클래스입니다.", 70000, now));

        // Create a teacher with the teaching classes
        Teacher teacher = new Teacher(teacherId, "suchan", teachingClasses, ActiveStatus.ACTIVE);

        // Filter classes based on keyword
        List<TeachingClass> filteredClasses = teachingClasses.stream()
                .filter(tc -> keyword == null || keyword.isEmpty() || tc.title().contains(keyword))
                .toList();

        // Create expected content
        List<ClassMetadataDto> expectedContent = filteredClasses.stream()
                .map(tc -> new ClassMetadataDto(
                        tc.id(),
                        tc.teacherId(),
                        tc.title(),
                        tc.description(),
                        tc.price(),
                        tc.registeredAt()
                ))
                .toList();
        PageResponse<ClassMetadataDto> expectedPage = PageResponse.of(
                expectedContent,
                0,
                expectedContent.size(),
                expectedContent.size(),
                1
        );

        // Mock the repository to return the teacher and the page
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findClassesByTeacherId(eq(teacherId), any(ClassSearchCondition.class)))
                .thenReturn(expectedPage);

        // when
        List<ClassMetadataDto> result = teacherService.getClassesByTeacherIdWithTitleKeyword(teacherId, keyword);

        // then
        assertThat(result).hasSize(expectedCount);

        // Verify that all returned classes contain the keyword in their title
        if (keyword != null && !keyword.isEmpty()) {
            for (ClassMetadataDto dto : result) {
                assertThat(dto.title().contains(keyword)).isTrue();
            }
        }
    }

    @Test
    @DisplayName("클래스를 최신순으로 정렬할 수 있다.")
    void getClassesByTeacherIdSortedByLatest() {
        // given
        Long teacherId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime twoDaysAgo = now.minusDays(2);

        // Create a list of teaching classes with different registration dates
        List<TeachingClass> teachingClasses = new ArrayList<>();
        teachingClasses.add(new TeachingClass(1L, teacherId, "프로그래밍 기초 클래스", "자바 프로그래밍 기초를 배우는 클래스입니다.", 50000, twoDaysAgo));
        teachingClasses.add(new TeachingClass(2L, teacherId, "알고리즘 마스터 클래스", "알고리즘 문제 해결 능력을 키우는 클래스입니다.", 70000, yesterday));
        teachingClasses.add(new TeachingClass(3L, teacherId, "웹 개발 클래스", "웹 개발 기초를 배우는 클래스입니다.", 60000, now));

        // Create a teacher with the teaching classes
        Teacher teacher = new Teacher(teacherId, "suchan", teachingClasses, ActiveStatus.ACTIVE);

        // Sort classes by latest (newest first)
        List<TeachingClass> sortedClasses = new ArrayList<>(teachingClasses);
        sortedClasses.sort((c1, c2) -> c2.registeredAt().compareTo(c1.registeredAt()));

        // Create expected content
        List<ClassMetadataDto> expectedContent = sortedClasses.stream()
                .map(tc -> new ClassMetadataDto(
                        tc.id(),
                        tc.teacherId(),
                        tc.title(),
                        tc.description(),
                        tc.price(),
                        tc.registeredAt()
                ))
                .toList();
        PageResponse<ClassMetadataDto> expectedPage = PageResponse.of(
                expectedContent,
                0,
                expectedContent.size(),
                expectedContent.size(),
                1
        );

        // Mock the repository to return the teacher and the page
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findClassesByTeacherId(eq(teacherId), any(ClassSearchCondition.class)))
                .thenReturn(expectedPage);

        // when
        List<ClassMetadataDto> result = teacherService.getClassesByTeacherId(teacherId, ClassSearchCondition.withSortType(SortType.LATEST));

        // then
        assertThat(result).hasSize(3);

        // Verify that classes are sorted by registration date (newest first)
        assertThat(result.get(0).id()).isEqualTo(3L); // 가장 최근에 등록된 클래스
        assertThat(result.get(1).id()).isEqualTo(2L); // 어제 등록된 클래스
        assertThat(result.get(2).id()).isEqualTo(1L); // 이틀 전에 등록된 클래스
    }

    @Test
    @DisplayName("클래스를 가격순으로 정렬할 수 있다.")
    void getClassesByTeacherIdSortedByPrice() {
        // given
        Long teacherId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // Create a list of teaching classes with different prices
        List<TeachingClass> teachingClasses = new ArrayList<>();
        teachingClasses.add(new TeachingClass(1L, teacherId, "중급 프로그래밍 클래스", "자바 프로그래밍 중급 과정입니다.", 70000, now));
        teachingClasses.add(new TeachingClass(2L, teacherId, "초급 프로그래밍 클래스", "자바 프로그래밍 초급 과정입니다.", 50000, now));
        teachingClasses.add(new TeachingClass(3L, teacherId, "고급 프로그래밍 클래스", "자바 프로그래밍 고급 과정입니다.", 90000, now));

        // Create a teacher with the teaching classes
        Teacher teacher = new Teacher(teacherId, "suchan", teachingClasses, ActiveStatus.ACTIVE);

        // Sort classes by price (lowest first)
        List<TeachingClass> sortedClasses = new ArrayList<>(teachingClasses);
        sortedClasses.sort((c1, c2) -> Integer.compare(c1.price(), c2.price()));

        // Create expected content
        List<ClassMetadataDto> expectedContent = sortedClasses.stream()
                .map(tc -> new ClassMetadataDto(
                        tc.id(),
                        tc.teacherId(),
                        tc.title(),
                        tc.description(),
                        tc.price(),
                        tc.registeredAt()
                ))
                .toList();
        PageResponse<ClassMetadataDto> expectedPage = PageResponse.of(
                expectedContent,
                0,
                expectedContent.size(),
                expectedContent.size(),
                1
        );

        // Mock the repository to return the teacher and the page
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findClassesByTeacherId(eq(teacherId), any(ClassSearchCondition.class)))
                .thenReturn(expectedPage);

        // when
        List<ClassMetadataDto> result = teacherService.getClassesByTeacherId(teacherId, ClassSearchCondition.withSortType(SortType.PRICE));

        // then
        assertThat(result).hasSize(3);

        // Verify that classes are sorted by price (lowest first)
        assertThat(result.get(0).id()).isEqualTo(2L); // 가장 저렴한 클래스 (50000)
        assertThat(result.get(1).id()).isEqualTo(1L); // 중간 가격 클래스 (70000)
        assertThat(result.get(2).id()).isEqualTo(3L); // 가장 비싼 클래스 (90000)
    }

    @Test
    @DisplayName("클래스 목록을 페이징하여 조회할 수 있다.")
    void getClassesByTeacherIdWithPagination() {
        // given
        Long teacherId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // Create a list of teaching classes
        List<TeachingClass> teachingClasses = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            teachingClasses.add(new TeachingClass(
                    (long) i,
                    teacherId,
                    "프로그래밍 클래스 " + i,
                    "프로그래밍 클래스 " + i + " 설명",
                    50000 + (i * 1000),
                    now.minusDays(i)
            ));
        }

        // Create a teacher with the teaching classes
        Teacher teacher = new Teacher(teacherId, "suchan", teachingClasses, ActiveStatus.ACTIVE);

        // Create page request for first page with 5 items
        PageRequest pageRequest = PageRequest.of(0, 5);
        ClassSearchCondition condition = ClassSearchCondition.withPageRequest(pageRequest);

        // Create expected page response
        List<ClassMetadataDto> expectedContent = teachingClasses.subList(0, 5).stream()
                .map(tc -> new ClassMetadataDto(
                        tc.id(),
                        tc.teacherId(),
                        tc.title(),
                        tc.description(),
                        tc.price(),
                        tc.registeredAt()
                ))
                .toList();
        PageResponse<ClassMetadataDto> expectedPage = PageResponse.of(
                expectedContent,
                0,
                5,
                20,
                4
        );

        // Mock the repository to return the teacher and the page
        when(teacherRepository.findById(eq(teacherId))).thenReturn(Optional.of(teacher));
        when(teacherRepository.findClassesByTeacherId(eq(teacherId), eq(condition)))
                .thenReturn(expectedPage);

        // when
        PageResponse<ClassMetadataDto> result = teacherService.getClassesByTeacherIdPaginated(teacherId, condition);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(5);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.totalElements()).isEqualTo(20);
        assertThat(result.totalPages()).isEqualTo(4);

        // Verify first item in the page
        ClassMetadataDto firstClass = result.content().get(0);
        assertThat(firstClass.id()).isEqualTo(1L);
        assertThat(firstClass.title()).isEqualTo("프로그래밍 클래스 1");
    }
}

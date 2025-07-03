package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.ActiveStatus;
import com.tutorlink.teacher.domain.ClassPolicy;
import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ClassPolicy classPolicy;

    public TeacherService(TeacherRepository teacherRepository, ClassPolicy classPolicy) {
        this.teacherRepository = teacherRepository;
        this.classPolicy = classPolicy;
    }

    public RegisterTeacherResult registerTeacher(CreateTeacherCommand command) {
        Teacher teacher = new Teacher(0L, command.name(), new ArrayList<>(), ActiveStatus.ACTIVE);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return new RegisterTeacherResult(savedTeacher.id());
    }

    public Long registerClass(RegisterClassCommand command) {
        Teacher teacher = teacherRepository.findById(command.teacherId())
                .orElseThrow(() -> new IllegalArgumentException("선생님이 존재하지 않습니다."));

        classPolicy.validate(teacher, command);

        TeachingClass teachingClass = new TeachingClass(
                0L,
                command.teacherId(),
                command.title(),
                command.description(),
                command.price(),
                command.registeredAt()
        );

        Teacher updatedTeacher = teacher.registerClass(teachingClass);
        Teacher savedTeacher = teacherRepository.save(updatedTeacher);

        return savedTeacher.teachingClasses().get(savedTeacher.teachingClasses().size() - 1).id();
    }

    /**
     * 선생님이 등록한 클래스 목록을 조회한다.
     *
     * @param teacherId 선생님 ID
     * @param condition 검색 조건 (null이면 모든 클래스 반환)
     * @return 클래스 메타데이터 DTO 목록
     * @throws IllegalArgumentException 선생님이 존재하지 않는 경우
     */
    public List<ClassMetadataDto> getClassesByTeacherId(Long teacherId, ClassSearchCondition condition) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("선생님이 존재하지 않습니다."));

        // 검색 조건이 null이면 빈 조건으로 초기화
        final ClassSearchCondition finalCondition = condition != null ? condition : ClassSearchCondition.empty();

        // 필터링 및 정렬 적용
        return teacher.teachingClasses().stream()
                // 제목 키워드 필터링
                .filter(teachingClass -> finalCondition.titleKeyword() == null || finalCondition.titleKeyword().isEmpty() || 
                        teachingClass.title().contains(finalCondition.titleKeyword()))
                // 정렬 적용
                .sorted((c1, c2) -> {
                    if (finalCondition.sortType() == null) {
                        return 0; // 정렬 없음
                    }

                    return switch (finalCondition.sortType()) {
                        case LATEST -> c2.registeredAt().compareTo(c1.registeredAt()); // 최신순 (내림차순)
                        case PRICE -> Integer.compare(c1.price(), c2.price()); // 가격순 (오름차순)
                    };
                })
                // DTO 변환
                .map(teachingClass -> new ClassMetadataDto(
                        teachingClass.id(),
                        teachingClass.teacherId(),
                        teachingClass.title(),
                        teachingClass.description(),
                        teachingClass.price(),
                        teachingClass.registeredAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 선생님이 등록한 클래스 목록을 제목 키워드로 필터링하여 조회한다.
     * 
     * @param teacherId 선생님 ID
     * @param titleKeyword 제목 키워드 (null이거나 빈 문자열이면 모든 클래스 반환)
     * @return 필터링된 클래스 메타데이터 DTO 목록
     * @throws IllegalArgumentException 선생님이 존재하지 않는 경우
     * @deprecated Use {@link #getClassesByTeacherId(Long, ClassSearchCondition)} instead
     */
    @Deprecated
    public List<ClassMetadataDto> getClassesByTeacherIdWithTitleKeyword(Long teacherId, String titleKeyword) {
        return getClassesByTeacherId(teacherId, ClassSearchCondition.withTitleKeyword(titleKeyword));
    }
}

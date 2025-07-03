package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.ActiveStatus;
import com.tutorlink.teacher.domain.ClassPolicy;
import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.ClassMetadataDto;
import com.tutorlink.teacher.dto.CreateTeacherCommand;
import com.tutorlink.teacher.dto.RegisterClassCommand;
import com.tutorlink.teacher.dto.RegisterTeacherResult;

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
                command.price()
        );

        Teacher updatedTeacher = teacher.registerClass(teachingClass);
        Teacher savedTeacher = teacherRepository.save(updatedTeacher);

        return savedTeacher.teachingClasses().get(savedTeacher.teachingClasses().size() - 1).id();
    }

    /**
     * 선생님이 등록한 클래스 목록을 조회한다.
     *
     * @param teacherId 선생님 ID
     * @return 클래스 메타데이터 DTO 목록
     * @throws IllegalArgumentException 선생님이 존재하지 않는 경우
     */
    public List<ClassMetadataDto> getClassesByTeacherId(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("선생님이 존재하지 않습니다."));

        return teacher.teachingClasses().stream()
                .map(teachingClass -> new ClassMetadataDto(
                        teachingClass.id(),
                        teachingClass.teacherId(),
                        teachingClass.title(),
                        teachingClass.description(),
                        teachingClass.price()
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
     */
    public List<ClassMetadataDto> getClassesByTeacherIdWithTitleKeyword(Long teacherId, String titleKeyword) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("선생님이 존재하지 않습니다."));

        return teacher.teachingClasses().stream()
                .filter(teachingClass -> titleKeyword == null || titleKeyword.isEmpty() || 
                        teachingClass.title().contains(titleKeyword))
                .map(teachingClass -> new ClassMetadataDto(
                        teachingClass.id(),
                        teachingClass.teacherId(),
                        teachingClass.title(),
                        teachingClass.description(),
                        teachingClass.price()
                ))
                .collect(Collectors.toList());
    }
}

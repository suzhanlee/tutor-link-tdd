package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.ActiveStatus;
import com.tutorlink.teacher.domain.ClassPolicy;
import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.CreateTeacherCommand;
import com.tutorlink.teacher.dto.RegisterClassCommand;
import com.tutorlink.teacher.dto.RegisterTeacherResult;

import java.util.ArrayList;

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
}

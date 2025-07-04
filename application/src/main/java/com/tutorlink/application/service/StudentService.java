package com.tutorlink.application.service;

import com.tutorlink.student.dto.ApplyClassCommand;
import com.tutorlink.student.dto.ApplyClassResult;
import com.tutorlink.teacher.domain.ClassApplication;
import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.ClassApplicationRepository;
import com.tutorlink.teacher.domain.repository.TeachingClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for student-related operations.
 */
@Service
public class StudentService {

    private final TeachingClassRepository teachingClassRepository;
    private final ClassApplicationRepository classApplicationRepository;

    public StudentService(TeachingClassRepository teachingClassRepository, ClassApplicationRepository classApplicationRepository) {
        this.teachingClassRepository = teachingClassRepository;
        this.classApplicationRepository = classApplicationRepository;
    }

    /**
     * Apply for a class.
     *
     * @param command the command to apply for a class
     * @return the result of applying for the class
     */
    @Transactional
    public ApplyClassResult applyClass(ApplyClassCommand command) {
        // Find the class
        TeachingClass teachingClass = teachingClassRepository.findById(command.classId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클래스입니다: " + command.classId()));

        // Create a class application
        ClassApplication classApplication = ClassApplication.createFromTeachingClass(teachingClass, command.studentId());

        // Save the class application
        ClassApplication savedClassApplication = classApplicationRepository.save(classApplication);

        // Return the result
        return new ApplyClassResult(
                savedClassApplication.id(),
                savedClassApplication.studentId(),
                savedClassApplication.teachingClassId(),
                savedClassApplication.classTitle(),
                savedClassApplication.classDescription(),
                savedClassApplication.classPrice(),
                savedClassApplication.appliedAt()
        );
    }
}
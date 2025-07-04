package com.tutorlink.teacher.domain.repository;

import com.tutorlink.teacher.domain.ClassApplication;

/**
 * Repository for class applications.
 */
public interface ClassApplicationRepository {
    
    /**
     * Save a class application.
     *
     * @param classApplication the class application to save
     * @return the saved class application
     */
    ClassApplication save(ClassApplication classApplication);
}
package com.tutorlink.teacher.domain.repository;

import com.tutorlink.teacher.domain.TeachingClass;

import java.util.Optional;

/**
 * Repository for teaching classes.
 */
public interface TeachingClassRepository {

    /**
     * Find a teaching class by ID.
     *
     * @param id the ID of the teaching class
     * @return the teaching class, or empty if not found
     */
    Optional<TeachingClass> findById(Long id);

    /**
     * Save a teaching class.
     *
     * @param teachingClass the teaching class to save
     * @return the saved teaching class
     */
    TeachingClass save(TeachingClass teachingClass);
}
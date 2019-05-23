package com.lambdaschool.school.repository;

import com.lambdaschool.school.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentRepository extends PagingAndSortingRepository<Student, Long>
{
    List<Student> findByStudnameContainingIgnoreCase(String name, Pageable pageable);

    @Transactional
	@Modifying
	@Query(value = "INSERT INTO studcourses(studid, courseid) values (:studid, :courseid)", nativeQuery = true)
	void addCourseToStudent(long studid, long courseid);
}

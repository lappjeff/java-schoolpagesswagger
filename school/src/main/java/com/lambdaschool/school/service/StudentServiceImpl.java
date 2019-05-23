package com.lambdaschool.school.service;

import com.lambdaschool.school.handler.ResourceNotFoundException;
import com.lambdaschool.school.model.Course;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.repository.CourseRepository;
import com.lambdaschool.school.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service(value = "studentService")
public class StudentServiceImpl implements StudentService
{
    @Autowired
    private StudentRepository studrepos;

    @Autowired
	private CourseRepository courseRepository;

    @Override
    public List<Student> findAll(Pageable pageable)
    {
        List<Student> list = new ArrayList<>();
        studrepos.findAll(pageable).iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public Student findStudentById(long id) throws ResourceNotFoundException
    {
        return studrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id " + id + " not found."));
    }

    @Override
    public List<Student> findStudentByNameLike(String name, Pageable pageable) throws ResourceNotFoundException
    {
        List<Student> list = new ArrayList<>();
        studrepos.findByStudnameContainingIgnoreCase(name, pageable).iterator().forEachRemaining(list::add);

        if(list.size() == 0)
        {
            throw new ResourceNotFoundException("Could not find any students with " + name + " in student name.");
        }

        return list;
    }

    @Override
    public void delete(long id) throws ResourceNotFoundException
    {
        if (studrepos.findById(id).isPresent())
        {
            studrepos.deleteById(id);
        } else
        {
            throw new ResourceNotFoundException("Student with id " + id + " not found");
        }
    }

    @Transactional
    @Override
    public Student save(Student student)
    {
        Student newStudent = new Student();

        newStudent.setStudname(student.getStudname());

        return studrepos.save(newStudent);
    }

    @Override
    public Student update(Student student, long id) throws ResourceNotFoundException
    {
        Student currentStudent = findStudentById(id);

        if (student.getStudname() != null)
        {
            currentStudent.setStudname(student.getStudname());
        }

        return studrepos.save(currentStudent);
    }


	@Override
	public void addCourseToStudent(long studentid, long courseid) throws ResourceNotFoundException
	{
		if(findStudentById(studentid) == null)
		{
		    throw new ResourceNotFoundException("Student with id " + studentid + " not found.");
		} else if(courseRepository.findById(courseid).isEmpty()) {
		    throw new ResourceNotFoundException("Course with id " + courseid + " not found.");
        }

        studrepos.addCourseToStudent(studentid, courseid);

    }
}

package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.ErrorDetail;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.StudentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{
    @Autowired
    private StudentService studentService;

    // Please note there is no way to add students to course yet!

	@ApiOperation(value = "Returns a list of all restaurants, supports pagination", response = Student.class,
				  responseContainer = "List")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Specifies the page " +
					"you want to access"),
			@ApiImplicitParam(name = "pageSize", dataType = "integer", paramType = "query", value = "Specifies the " +
					"amount of items per page"),
			@ApiImplicitParam(name = "sort", dataType = "string", allowMultiple =true, paramType = "query", value =
					"Sorts results by variable of choice")

					   })

    @GetMapping(value = "/students", produces = {"application/json"})
    public ResponseEntity<?> listAllStudents(@PageableDefault Pageable pageable)
    {
        List<Student> myStudents = studentService.findAll(pageable);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }


    @ApiOperation(value = "Returns a single student by id", response = Student.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Student found", response = Student.class),
			@ApiResponse(code = 404, message = "Student not found", response = ErrorDetail.class)

	})
    @GetMapping(value = "/Student/{StudentId}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentById(@ApiParam(value = "student id", required = true, example = "1")
            @PathVariable
                    Long StudentId)
    {
        Student r = studentService.findStudentById(StudentId);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @ApiOperation(value = "Returns a single student or list of students with names containing the value given, " +
			"supports pagination",
				  response = Student.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Specifies the page " +
					"you want to access"),
			@ApiImplicitParam(name = "pageSize", dataType = "integer", paramType = "query", value = "Specifies the " +
					"amount of items per page"),
			@ApiImplicitParam(name = "sort", dataType = "string", allowMultiple =true, paramType = "query", value =
					"Sorts results by variable of choice")
					   })
    @GetMapping(value = "/student/namelike/{name}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentByNameContaining(@ApiParam(value = "Student name or name fragment", required =
			true, example = "John") @PageableDefault Pageable pageable,
            @PathVariable String name)
    {
        List<Student> myStudents = studentService.findStudentByNameLike(name, pageable);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

	@ApiOperation(value = "Creates a new student", response = void.class, notes = "URI for new student is found in " +
			"location header")
	@ApiResponses({
			@ApiResponse(code = 201, message = "Student created successfully", response = void.class),
			@ApiResponse(code = 500, message = "error creating restaurant", response = URISyntaxException.class)
				  })

    @PostMapping(value = "/Student",
                 consumes = {"application/json"},
                 produces = {"application/json"})
    public ResponseEntity<?> addNewStudent(@ApiParam(value = "Student Name", required = true)
											   @Valid @RequestBody Student newStudent) throws URISyntaxException
    {
        newStudent = studentService.save(newStudent);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newStudentURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{Studentid}").buildAndExpand(newStudent.getStudid()).toUri();
        responseHeaders.setLocation(newStudentURI);

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

	@ApiOperation(value = "Adds a new course to a student", response = void.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "courseid", required = true, value = "Id of course being added to student"),
			@ApiImplicitParam(name = "studentid", required = true, value = "Id of student to add course to")
					   })
	@PostMapping(value = "addstudentcourse/{studentid}/{courseid}",
				 consumes = "application/json",
				 produces = "application/json")
	public ResponseEntity<?> addCourseToStudent(@PathVariable long studentid,
												@PathVariable long courseid)
	{
		studentService.addCourseToStudent(studentid, courseid);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	@ApiOperation(value = "Update an existing student", response = void.class)
	@ApiResponses({
			@ApiResponse(code = 404, message = "Student not found", response = EntityNotFoundException.class)
				  })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Studentid", value = "Id of student being targeted", required = true),
			@ApiImplicitParam(name = "updateStudent", value = "Body of changes being made to targeted student",
							  required = true)
					   })

    @PutMapping(value = "/Student/{Studentid}", consumes = "application/json")
    public ResponseEntity<?> updateStudent(
            @RequestBody Student updateStudent,
            @PathVariable long Studentid)
    {
        studentService.update(updateStudent, Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	@ApiOperation(value = "Delete an existing student", response = void.class)
	@ApiResponses({
			@ApiResponse(code = 404, message = "Student not found", response = EntityNotFoundException.class)
				  })
	@ApiImplicitParam(name = "Studentid", required = true, value = "Id of student being deleted")
    @DeleteMapping("/Student/{Studentid}")
    public ResponseEntity<?> deleteStudentById(
            @PathVariable
                    long Studentid)
    {
        studentService.delete(Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}

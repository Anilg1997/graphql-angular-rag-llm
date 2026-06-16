package com.example.courseservice.tool;

import com.example.courseservice.model.Course;
import com.example.courseservice.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseTools {

    private final CourseService courseService;

    public List<Course> getAllCourses() {
        return courseService.getAll();
    }

    public Course getCourse(String id) {
        return courseService.getById(id);
    }

    public Course createCourse(String title, String description, String instructor, int credits) {
        Course c = new Course();
        c.setTitle(title);
        c.setDescription(description);
        c.setInstructor(instructor);
        c.setCredits(credits);
        return courseService.create(c);
    }

    public Course updateCourse(String id, String title, String description, String instructor, int credits) {
        Course c = new Course();
        c.setId(id);
        c.setTitle(title);
        c.setDescription(description);
        c.setInstructor(instructor);
        c.setCredits(credits);
        return courseService.update(c);
    }

    public String deleteCourse(String id) {
        return courseService.delete(id);
    }
}

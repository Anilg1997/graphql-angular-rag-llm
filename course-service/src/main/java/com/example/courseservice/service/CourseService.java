package com.example.courseservice.service;

import com.example.courseservice.model.Course;
import com.example.courseservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repository;

    public Course create(Course course) {
        return repository.save(course);
    }

    public List<Course> getAll() {
        return repository.findAll();
    }

    public Course getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public Course update(Course course) {
        return repository.save(course);
    }

    public String delete(String id) {
        repository.deleteById(id);
        return "Deleted Successfully";
    }
}

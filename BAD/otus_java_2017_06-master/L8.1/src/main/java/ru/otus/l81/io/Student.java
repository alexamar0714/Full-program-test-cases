package ru.otus.l81.io;

import java.util.Collections;
import java.util.List;

/**
 * Created by tully.
 */
public class Student extends Person{

    private final List<String> courses;

    public Student(int age, String name, String course) {
        super(age, name);
        this.courses = Collections.singletonList(course);
    }

    public String getCourse() {
        return courses.get(0);
    }

    public String toString() {
        return super.toString() + " course: " + getCourse();
    }
}

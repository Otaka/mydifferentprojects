package com.testentities;

import com.sqlprocessor.buffers.SqlBuffer;
import com.sqlprocessor.utils.FluentArrayList;

/**
 * @author sad
 */
public class TestDataFactory {

    public static SqlBuffer createHouseBuffer() {
        SqlBuffer<House> houseBuffer = new SqlBuffer<House>("houses", House.class, new FluentArrayList<String>("id", "name"));
        houseBuffer.setData(new FluentArrayList<House>(
                new House(1, "Gryffindor"),
                new House(2, "Hufflepuff"),
                new House(3, "Ravenclaw"),
                new House(4, "Slytherin")
        ));

        return houseBuffer;
    }

    public static SqlBuffer createStudentBuffer() {
        SqlBuffer<Student> studentBuffer = new SqlBuffer<Student>("students", Student.class, new FluentArrayList<String>("id", "name", "houseId"));
        int id = 0;
        studentBuffer.setData(new FluentArrayList<Student>(
                new Student(id++, "Hermione Granger", 1),
                new Student(id++, "Harry Potter", 1),
                new Student(id++, "Neville Longbottom", 1),
                new Student(id++, "Lavender Brown", 1),
                new Student(id++, "Albus Dumbledore", 1),
                new Student(id++, "Cedric Diggory", 2),
                new Student(id++, "Ernie Macmillan", 2),
                new Student(id++, "Hannah Abbott", 2),
                new Student(id++, "Justin Finch-Fletchley", 2),
                new Student(id++, "Susan Bones", 2),
                new Student(id++, "Zacharias Smith", 2),
                new Student(id++, "Leanne", 2),
                new Student(id++, "Luna Lovegood", 3),
                new Student(id++, "Sybill Trelawney", 3),
                new Student(id++, "Marcus Belby", 3),
                new Student(id++, "Cho Chang", 3),
                new Student(id++, "Myrtle Warren", 3),
                new Student(id++, "Padma Patil", 3),
                new Student(id++, "Terry Boot", 3),
                new Student(id++, "Michael Corner", 3),
                new Student(id++, "Draco Malfoy", 4),
                new Student(id++, "Vincent Crabbe", 4),
                new Student(id++, "Gregory Goyle", 4),
                new Student(id++, "Pansy Parkinson", 4),
                new Student(id++, "Blaise Zabini", 4),
                new Student(id++, "Marcus Flint", 4),
                new Student(id++, "Terence Higgs", 4),
                new Student(id++, "Tracey Davis", 4)
        ));

        return studentBuffer;
    }
}

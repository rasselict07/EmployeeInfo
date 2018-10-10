package com.employeeinfo

class Employee {

    long id
    String fullName
    int age
    double salary

    static constraints = {
        id(unique: true)
    }
}

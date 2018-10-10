package com.employeeinfo

import com.employeeinfo.utility.Tools
import grails.converters.JSON
import grails.core.GrailsApplication
import groovy.json.JsonSlurper

import static org.springframework.http.HttpStatus.*

class EmployeeController {
    GrailsApplication grailsApplication

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        String fileDir = grailsApplication.config.getProperty('app.filedir')
        def folder = new File(fileDir)
        if (!folder.exists()) {
            log.error("Directory not found")
            return
        }

        String fileName = grailsApplication.config.getProperty('app.filename')
        def file = new File(fileDir + Tools.SLASH + fileName)

        if (!file.exists()) {
            log.error("File not exists")
            return
        }

        List<Employee> employeeList = []
        def employeeListJSON = new JsonSlurper().parse(file)
        employeeListJSON.each {
            Employee employee = new Employee()
            employee.id = it.id
            employee.age = it.age
            employee.salary = it.salary
            employee.fullName = it.fullName

            employeeList.add(employee)
        }

        params.max = Math.min(max ?: 10, 100)
        respond employeeList, model: [employeeDemoCount: employeeList.size()]
    }

    def show() {

        String fileDir = grailsApplication.config.getProperty('app.filedir')
        String fileName = grailsApplication.config.getProperty('app.filename')
        def file = new File(fileDir + Tools.SLASH + fileName)
        if (!file.exists()) {
            log.error("File not exists")
            return
        }
        Employee employee = new Employee()
        def employeeInfoList = new JsonSlurper().parse(file)
        employeeInfoList.each {
            if (Long.parseLong(params.id) == it.id) {
                employee.id = it.id
                employee.age = it.age
                employee.salary = it.salary
                employee.fullName = it.fullName
            }
        }

        respond employee
    }

    def create() {
        respond new Employee(params)
    }

    def save() {

        String fileDir = grailsApplication.config.getProperty('app.filedir')
        String fileName = grailsApplication.config.getProperty('app.filename')
        def file = new File(fileDir + Tools.SLASH + fileName)
        if (!file.exists()) {
            log.error("File not exists")
            return
        }
        def employeeInfoList = new JsonSlurper().parse(file)

        List ids = employeeInfoList ? employeeInfoList.collect { it.id } : []
        long currentMax = (ids.size() > 0) ? ids.max() : 0

        Employee employee = new Employee()
        employee.id = currentMax + 1
        employee.age = Integer.parseInt(params.age)
        employee.salary = Double.parseDouble(params.salary)
        employee.fullName = params.fullName

        employeeInfoList = employeeInfoList << employee as JSON

        file.delete()
        new File(fileDir, fileName).withWriterAppend { writer ->
            writer << employeeInfoList
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'employee.label', default: 'Employee'), employee.id])
                redirect employee
            }
            '*' { respond employee, [status: CREATED] }
        }
    }

    def edit() {

        String fileDir = grailsApplication.config.getProperty('app.filedir')
        String fileName = grailsApplication.config.getProperty('app.filename')
        def file = new File(fileDir + Tools.SLASH + fileName)
        if (!file.exists()) {
            log.error("File not exists")
            return
        }
        Employee employee = new Employee()
        def employeeInfoList = new JsonSlurper().parse(file)
        employeeInfoList.each {
            if (Long.parseLong(params.id) == it.id) {
                employee.id = it.id
                employee.age = it.age
                employee.salary = it.salary
                employee.fullName = it.fullName
            }
        }

        respond employee
    }

    def update() {
        String fileDir = grailsApplication.config.getProperty('app.filedir')
        String fileName = grailsApplication.config.getProperty('app.filename')
        def file = new File(fileDir + Tools.SLASH + fileName)
        if (!file.exists()) {
            log.error("File not exists")
            return
        }
        Employee employee = new Employee()
        def employeeInfoList = new JsonSlurper().parse(file)
        for (int i = 0; i < employeeInfoList.size(); i++) {
            if (Long.parseLong(params.id) == employeeInfoList.get(i).id) {
                employee.id = Long.parseLong(params.id)
                employee.age = Integer.parseInt(params.age)
                employee.salary = Double.parseDouble(params.salary)
                employee.fullName = params.fullName

                employeeInfoList.remove(employeeInfoList.get(i))
                employeeInfoList = employeeInfoList << employee as JSON
                break
            }
        }

        file.delete()
        new File(fileDir, fileName).withWriterAppend { writer ->
            writer << employeeInfoList
        }


        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'employee.label', default: 'Employee'), employee.id])
                redirect employee
            }
            '*' { respond employee, [status: OK] }
        }
    }

    def delete() {

        String fileDir = grailsApplication.config.getProperty('app.filedir')
        String fileName = grailsApplication.config.getProperty('app.filename')
        def file = new File(fileDir + Tools.SLASH + fileName)
        if (!file.exists()) {
            log.error("File not exists")
            return
        }
        Employee employee = new Employee()
        def employeeInfoList = new JsonSlurper().parse(file)
        for (int i = 0; i < employeeInfoList.size(); i++) {
            if (Long.parseLong(params.id) == employeeInfoList.get(i).id) {
                employee.id = employeeInfoList.get(i).id
                employee.age = employeeInfoList.get(i).age
                employee.salary = employeeInfoList.get(i).salary
                employee.fullName = employeeInfoList.get(i).fullName

                employeeInfoList.remove(employeeInfoList.get(i))
                break
            }
        }

        def employeeInfoListJson = employeeInfoList as JSON
        file.delete()
        new File(fileDir, fileName).withWriterAppend { writer ->
            writer << employeeInfoListJson
        }

        log.println(employee.fullName + " deleted successfully")

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'employee.label', default: 'Employee'), employee.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

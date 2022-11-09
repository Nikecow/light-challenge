package org.light.challenge.data.repository

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.light.challenge.data.CompanyTable
import org.light.challenge.data.EmployeeTable
import org.light.challenge.data.domain.Company
import org.light.challenge.data.domain.Employee

class EmployeeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmployeeEntity>(EmployeeTable)

    var companyId by EmployeeTable.companyId
    var name by EmployeeTable.name
    var email by EmployeeTable.email
    var slackId  by EmployeeTable.slackId
    var manager  by EmployeeTable.manager
    var departmentId by EmployeeTable.departmentId

    fun toEmployee() = Employee(id.value, companyId.value, name, email, slackId, manager, departmentId)
}

class CompanyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CompanyEntity>(CompanyTable)

    var name by CompanyTable.name
    var employees = emptyList<EmployeeEntity>()

    fun toCompany() = Company(id.value, name, employees.map { e -> e.toEmployee() })
}

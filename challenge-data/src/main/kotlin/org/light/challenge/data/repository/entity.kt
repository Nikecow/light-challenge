package org.light.challenge.data.repository

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.light.challenge.data.CompanyTable
import org.light.challenge.data.DepartmentTable
import org.light.challenge.data.EmployeeTable
import org.light.challenge.data.RuleTable
import org.light.challenge.data.WorkflowTable
import org.light.challenge.data.domain.Company
import org.light.challenge.data.domain.Department
import org.light.challenge.data.domain.DepartmentName
import org.light.challenge.data.domain.Employee
import org.light.challenge.data.domain.NotifyMethod
import org.light.challenge.data.domain.Rule
import org.light.challenge.data.domain.Workflow

class CompanyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CompanyEntity>(CompanyTable)

    var name by CompanyTable.name
    val employees by EmployeeEntity referrersOn EmployeeTable.companyId
    val workflows by WorkflowEntity referrersOn WorkflowTable.companyId
    val departments by DepartmentEntity referrersOn DepartmentTable.companyId

    fun toCompany() = Company(
        id.value,
        name,
        employees.map { it.toEmployee() },
        workflows.firstOrNull()?.toWorkflow(),
        departments.map { it.toDepartment() })
}


class WorkflowEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WorkflowEntity>(WorkflowTable)

    var companyId by WorkflowTable.companyId
    var chiefThreshold by WorkflowTable.chiefThreshold
    val rules by RuleEntity referrersOn RuleTable.workflowId

    fun toWorkflow() = Workflow(id.value, companyId.value, chiefThreshold?.toBigDecimal(), rules.map { it.toRule() })
}

class RuleEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RuleEntity>(RuleTable)

    var workflowId by RuleTable.workflowId
    val department by DepartmentEntity referencedOn RuleTable.departmentId
    var cutoffAmount by RuleTable.cutoffAmount
    var requiresManager by RuleTable.requiresManager
    var notifyMethod by RuleTable.notifyMethod

    fun toRule() = Rule(
        id.value,
        workflowId.value,
        department.toDepartment(),
        cutoffAmount?.toBigDecimal(),
        requiresManager,
        NotifyMethod.valueOf(notifyMethod)
    )
}

class DepartmentEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DepartmentEntity>(DepartmentTable)

    var name by DepartmentTable.name
    var headEmployeeId by DepartmentTable.headEmployeeId

    fun toDepartment() = Department(id.value, DepartmentName.valueOf(name), headEmployeeId?.value)
}

class EmployeeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmployeeEntity>(EmployeeTable)

    var companyId by EmployeeTable.companyId
    var name by EmployeeTable.name
    var email by EmployeeTable.email
    var slackId by EmployeeTable.slackId
    var manager by EmployeeTable.manager
    val department by DepartmentEntity referencedOn EmployeeTable.departmentId

    fun toEmployee() =
        Employee(id.value, companyId.value, name, email, slackId, manager, department.toDepartment())
}

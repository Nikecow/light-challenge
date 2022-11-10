package org.light.challenge.data

import org.jetbrains.exposed.dao.IntIdTable

object CompanyTable : IntIdTable() {
    val name = varchar("name", 50)
}

object EmployeeTable : IntIdTable() {
    val companyId = reference("company_id", CompanyTable)
    val name = varchar("name", 50)
    val email = varchar("email", 50).uniqueIndex()
    val slackId = varchar("slackId", 50).uniqueIndex()
    val manager = bool("manager")
    val departmentId = reference("department_id", DepartmentTable)
}

object DepartmentTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val headEmployeeId = reference("head_employee_id", EmployeeTable).nullable()
    val companyId = reference("company_id", CompanyTable)
}

object WorkflowTable : IntIdTable() {
    val companyId = reference("company_id", CompanyTable)
    val chiefThreshold = decimal("chief_threshold", 4, 4).nullable()
}

object RuleTable : IntIdTable() {
    val workflowId = reference("workflow_id", WorkflowTable)
    val departmentId = reference("department_id", DepartmentTable)
    val cutoffAmount = decimal("cutoff_amount", 4, 4).nullable()
    val requiresManager = bool("requires_manager").nullable()
    val notifyMethod = varchar("notify_method", 50)
}
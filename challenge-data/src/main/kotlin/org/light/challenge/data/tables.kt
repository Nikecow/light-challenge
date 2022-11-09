package org.light.challenge.data

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

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
}

object WorkflowTable : IntIdTable() {
    val companyId = reference("company_id", CompanyTable)
    val chiefThreshold = varchar("chief_threshold", 50).nullable()
}

object RuleTable : IntIdTable() {
    val workflowId = reference("workflow_id", WorkflowTable)
}

object ConditionTable : IntIdTable() {
    val ruleId = reference("rule_id", RuleTable)
    val departmentId = reference("department_id", DepartmentTable)
    val cutoffAmount = varchar("cutoff_amount", 50).nullable()
    val requiresManager = bool("requires_manager").nullable()
}

object ActionTable : IntIdTable() {
    val ruleId = reference("rule_id", RuleTable)
    val notifyMethod = varchar("notify_method", 50)
}

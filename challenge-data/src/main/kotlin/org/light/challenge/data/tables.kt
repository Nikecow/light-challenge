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
    val departmentId = integer("department_id")
}

object DepartmentTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val headEmployeeId = integer("head_employee_id").nullable()
}

object WorkflowTable : IntIdTable() {
    val companyId = integer("company_id")
    val chiefThreshold = varchar("chief_threshold", 50).nullable()
}

object RuleTable : IntIdTable() {
    val workflowId = integer("workflow_id")
}

object ConditionTable : IntIdTable() {
    val ruleId = integer("rule_id")
    val departmentId = integer("department_id")
    val cutoffAmount = varchar("cutoff_amount", 50).nullable()
    val requiresManager = bool("requires_manager").nullable()
}

object ActionTable : IntIdTable() {
    val ruleId = integer("rule_id")
    val notifyMethod = varchar("notify_method", 50)
}

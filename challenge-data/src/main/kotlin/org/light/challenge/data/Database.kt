package org.light.challenge.data

import mu.KotlinLogging
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.light.challenge.data.domain.DepartmentName
import org.light.challenge.data.domain.NotifyMethod

// todo: make this static?
class Database {
    private val db: Database
    private val logger = KotlinLogging.logger {}

    init {
        logger.info { "Connecting to database" }

        db = Database.connect("jdbc:sqlite::memory:test?mode=memory&cache=shared", "org.sqlite.JDBC")

        transaction {
            addLogger(StdOutSqlLogger)

            logger.info { "Initializing database tables" }

            SchemaUtils.drop(CompanyTable, WorkflowTable, EmployeeTable, RuleTable, DepartmentTable, ConditionTable, ActionTable)
            SchemaUtils.create(CompanyTable, WorkflowTable, EmployeeTable, RuleTable, DepartmentTable, ConditionTable, ActionTable)

            logger.info { "Populating database with entries" }

            val cId = insertCompany("Big Corporation Inc")
            val finDeptId = insertDepartment(DepartmentName.FINANCE)
            val mktDeptId = insertDepartment(DepartmentName.MARKETING)

            // Marketing Employees
            val cmoId = insertEmployee(
                cId = cId,
                fullName = "Jack Dorsey",
                mail = "jack.smith@gmail.com",
                slack = "234567",
                mngr = true,
                deptId = mktDeptId.value
            )

            val mktEmployeeId1 = insertEmployee(
                cId = cId,
                fullName = "Rico Vlieger",
                mail = "rico.vlieger@gmail.com",
                slack = "10345678",
                mngr = false,
                deptId = mktDeptId.value
            )

            val mktEmployeeId2 = insertEmployee(
                cId = cId,
                fullName = "Michael Rodriguez",
                mail = "michael.rodriguez@gmail.com",
                slack = "11345678",
                mngr = false,
                deptId = mktDeptId.value
            )

            val mktManagerId = insertEmployee(
                cId = cId,
                fullName = "Chang Lee",
                mail = "chang.lee@gmail.com",
                slack = "12345678",
                mngr = true,
                deptId = mktDeptId.value
            )

            // Finance Employees
            val cfoId = insertEmployee(
                cId = cId,
                fullName = "William Smith",
                mail = "william.smith@gmail.com",
                slack = "123456",
                mngr = true,
                deptId = finDeptId.value
            )

            val finEmployeeId1 = insertEmployee(
                cId = cId,
                fullName = "Mark Bouwer",
                mail = "mark.bouwer@gmail.com",
                slack = "345678",
                mngr = false,
                deptId = finDeptId.value
            )

            val finEmployeeId2 = insertEmployee(
                cId = cId,
                fullName = "Mike Garcia",
                mail = "mike.garcia@gmail.com",
                slack = "456789",
                mngr = false,
                deptId = finDeptId.value
            )

            val finManagerId = insertEmployee(
                cId = cId,
                fullName = "James Johnson",
                mail = "james.johnson@gmail.com",
                slack = "567891",
                mngr = true,
                deptId = finDeptId.value
            )

            promoteEmployeeToHead(deptId = finDeptId.value, empId = cfoId.value)
            promoteEmployeeToHead(deptId = mktDeptId.value, empId = cmoId.value)

            val wId = insertWorkflow(cId.value, "10000")

            // RuleTable 1
            insertRule(
                flowId = wId.value,
                deptId = mktDeptId.value,
                method = NotifyMethod.EMAIL,
                cutoff = "10000",
                manager = null
            )

            // RuleTable 2
            insertRule(
                flowId = wId.value,
                deptId = finDeptId.value,
                method = NotifyMethod.SLACK,
                cutoff = "10000",
                manager = null
            )

            // RuleTable 3
            insertRule(
                flowId = wId.value,
                deptId = finDeptId.value,
                method = NotifyMethod.SLACK,
                cutoff = "5000",
                manager = true
            )

            // RuleTable 4 (fallback rule)
            insertRule(
                flowId = wId.value,
                deptId = finDeptId.value,
                method = NotifyMethod.SLACK,
                cutoff = null,
                manager = null
            )

            logger.info { "Finished populating database" }
        }
    }

    private fun insertCompany(cmpName: String) = CompanyTable.insertAndGetId {
        it[name] = cmpName
    }

    private fun insertEmployee(cId: EntityID<Int>, fullName: String, mail: String, slack: String, mngr: Boolean, deptId: Int) =
        EmployeeTable.insertAndGetId {
            it[companyId] = cId
            it[name] = fullName
            it[email] = mail
            it[slackId] = slack
            it[manager] = mngr
            it[departmentId] = deptId
        }

    private fun promoteEmployeeToHead(deptId: Int, empId: Int) {
        DepartmentTable.update({ DepartmentTable.id eq deptId }) {
            it[headEmployeeId] = empId
        }
    }

    private fun insertDepartment(department: DepartmentName) = DepartmentTable.insertAndGetId {
        it[name] = department.name
    }

    private fun insertWorkflow(cId: Int, threshold: String?) = WorkflowTable.insertAndGetId {
        it[companyId] = cId
        it[chiefThreshold] = threshold
    }

    private fun insertRule(
        flowId: Int,
        deptId: Int,
        method: NotifyMethod,
        cutoff: String?,
        manager: Boolean?,
    ): Int {
        val rId = RuleTable.insertAndGetId {
            it[workflowId] = flowId
        }

        ConditionTable.insert {
            it[ruleId] = rId.value
            it[departmentId] = deptId
            it[cutoffAmount] = cutoff
            it[requiresManager] = manager
        }

        ActionTable.insert {
            it[ruleId] = rId.value
            it[notifyMethod] = method.name
        }

        return rId.value
    }

    fun getDB() = db
}
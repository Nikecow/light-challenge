package org.light.challenge.data

import mu.KotlinLogging
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.light.challenge.data.domain.DepartmentName
import org.light.challenge.data.domain.NotifyMethod
import java.math.BigDecimal

object CompanyDAO {
    private var database: Database? = null
    private val logger = KotlinLogging.logger {}

    fun connect() =
        Database.connect("jdbc:sqlite::memory:test?mode=memory&cache=shared", "org.sqlite.JDBC").also {
            database = it
        }

    fun resetTables() {
        transaction {
            SchemaUtils.drop(
                CompanyTable,
                WorkflowTable,
                EmployeeTable,
                RuleTable,
                DepartmentTable,
            )
            SchemaUtils.create(
                CompanyTable,
                WorkflowTable,
                EmployeeTable,
                RuleTable,
                DepartmentTable,
            )
        }
    }

    fun initDefaults() {
        logger.info { "Connecting to database" }

        connect()

        transaction {
            addLogger(StdOutSqlLogger)

            logger.info { "Initializing database tables" }

            resetTables()

            logger.info { "Populating database with entries" }

            val cId = insertCompany("Big Corporation Inc")
            val finDeptId = insertDepartment(DepartmentName.FINANCE, cId)
            val mktDeptId = insertDepartment(DepartmentName.MARKETING, cId)

            // Marketing EmployeeTable
            val cmoId = insertEmployee(
                cId = cId,
                fullName = "Jack Dorsey",
                mail = "jack.smith@gmail.com",
                slack = "234567",
                mngr = true,
                deptId = mktDeptId
            )

            val mktEmployeeId1 = insertEmployee(
                cId = cId,
                fullName = "Rico Vlieger",
                mail = "rico.vlieger@gmail.com",
                slack = "10345678",
                mngr = false,
                deptId = mktDeptId
            )

            val mktEmployeeId2 = insertEmployee(
                cId = cId,
                fullName = "Michael Rodriguez",
                mail = "michael.rodriguez@gmail.com",
                slack = "11345678",
                mngr = false,
                deptId = mktDeptId
            )

            val mktManagerId = insertEmployee(
                cId = cId,
                fullName = "Chang Lee",
                mail = "chang.lee@gmail.com",
                slack = "12345678",
                mngr = true,
                deptId = mktDeptId
            )

            // Finance EmployeeTable
            val cfoId = insertEmployee(
                cId = cId,
                fullName = "William Smith",
                mail = "william.smith@gmail.com",
                slack = "123456",
                mngr = true,
                deptId = finDeptId
            )

            val finEmployeeId1 = insertEmployee(
                cId = cId,
                fullName = "Mark Bouwer",
                mail = "mark.bouwer@gmail.com",
                slack = "345678",
                mngr = false,
                deptId = finDeptId
            )

            val finEmployeeId2 = insertEmployee(
                cId = cId,
                fullName = "Mike Garcia",
                mail = "mike.garcia@gmail.com",
                slack = "456789",
                mngr = false,
                deptId = finDeptId
            )

            val finManagerId = insertEmployee(
                cId = cId,
                fullName = "James Johnson",
                mail = "james.johnson@gmail.com",
                slack = "567891",
                mngr = true,
                deptId = finDeptId
            )

            promoteEmployeeToHead(deptId = finDeptId.value, empId = cfoId)
            promoteEmployeeToHead(deptId = mktDeptId.value, empId = cmoId)

            val wId = insertWorkflow(cId, "10000".toBigDecimal())

            // Rule 1
            insertRule(
                flowId = wId,
                deptId = mktDeptId,
                method = NotifyMethod.EMAIL,
                cutoff = "10000".toBigDecimal(),
                manager = null
            )

            // Rule 2
            insertRule(
                flowId = wId,
                deptId = finDeptId,
                method = NotifyMethod.SLACK,
                cutoff = "10000".toBigDecimal(),
                manager = null
            )

            // Rule 3
            insertRule(
                flowId = wId,
                deptId = finDeptId,
                method = NotifyMethod.EMAIL,
                cutoff = "5000".toBigDecimal(),
                manager = true
            )

            // Rule 4 (fallback rule)
            insertRule(
                flowId = wId,
                deptId = finDeptId,
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

    private fun insertEmployee(
        cId: EntityID<Int>,
        fullName: String,
        mail: String,
        slack: String,
        mngr: Boolean,
        deptId: EntityID<Int>
    ) =
        EmployeeTable.insertAndGetId {
            it[companyId] = cId
            it[name] = fullName
            it[email] = mail
            it[slackId] = slack
            it[manager] = mngr
            it[departmentId] = deptId
        }

    private fun promoteEmployeeToHead(deptId: Int, empId: EntityID<Int>) {
        DepartmentTable.update({ DepartmentTable.id eq deptId }) {
            it[headEmployeeId] = empId
        }
    }

    private fun insertDepartment(department: DepartmentName, cId: EntityID<Int>) = DepartmentTable.insertAndGetId {
        it[name] = department.name
        it[companyId] = cId
    }

    private fun insertWorkflow(cId: EntityID<Int>, threshold: BigDecimal?) = WorkflowTable.insertAndGetId {
        it[companyId] = cId
        it[chiefThreshold] = threshold
    }

    private fun insertRule(
        flowId: EntityID<Int>,
        deptId: EntityID<Int>,
        method: NotifyMethod,
        cutoff: BigDecimal?,
        manager: Boolean?,
    ): EntityID<Int> {
        val rId = RuleTable.insertAndGetId {
            it[workflowId] = flowId
            it[departmentId] = deptId
            it[cutoffAmount] = cutoff
            it[requiresManager] = manager
            it[notifyMethod] = method.name
        }

        return rId
    }
}
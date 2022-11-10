package org.light.challenge.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.light.challenge.data.CompanyDAO
import org.light.challenge.data.CompanyTable
import org.light.challenge.data.DepartmentTable
import org.light.challenge.data.WorkflowTable
import java.math.BigDecimal

class WorkflowRepositoryIT {
    private val db = CompanyDAO.connect()
    private val subject = WorkflowRepository()

    @BeforeEach
    internal fun init() {
        CompanyDAO.resetTables()
    }

    @Test
    internal fun `should get a workflow by company id`() {

        transaction {
            // given
            val cId = CompanyTable.insertAndGetId {
                it[id] = EntityID(123456, CompanyTable)
                it[DepartmentTable.name] = "A Company"
            }

            val wId = WorkflowTable.insertAndGetId {
                it[id] = EntityID(4567, WorkflowTable)
                it[companyId] = cId
                it[chiefThreshold] = BigDecimal.TEN
            }

            // when
            val actual = subject.getByCompanyId(cId.value)

            // then
            assertThat(actual?.id).isEqualTo(4567)
            assertThat(actual?.companyId).isEqualTo(123456)
        }

    }

    @Test
    internal fun `should get null for non-existing workflow`() {

        transaction {
            // when
            val actual = subject.getByCompanyId(1234)

            // then
            assertThat(actual).isEqualTo(null)
        }
    }
}
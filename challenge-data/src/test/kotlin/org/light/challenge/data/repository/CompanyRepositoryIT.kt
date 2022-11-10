package org.light.challenge.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.light.challenge.data.CompanyTable
import org.light.challenge.data.DatabaseFactory
import org.light.challenge.data.DepartmentTable

class CompanyRepositoryIT {
    private val db = DatabaseFactory.connect()
    private val subject = CompanyRepository()

    @BeforeEach
    @Test
    internal fun init() {
        DatabaseFactory.resetTables()
    }

    @Test
    internal fun `should get a company by id`() {

        transaction {
            // given
            val cId = CompanyTable.insertAndGetId {
                it[id] = EntityID(6789, CompanyTable)
                it[DepartmentTable.name] = "A Company"
            }

            // when
            val actual = subject.getById(cId.value)

            // then
            assertThat(actual?.id).isEqualTo(6789)
        }
    }

    @Test
    internal fun `should get null for non-existing company`() {

        transaction {
            // when
            val actual = subject.getById(1234)

            // then
            assertThat(actual).isEqualTo(null)
        }
    }
}
package org.light.challenge.data.repository


import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.light.challenge.data.domain.Company

class CompanyRepository {

    fun getById(id: Int): Company? = transaction {
        addLogger(StdOutSqlLogger)

        val company = CompanyEntity.findById(id)?.toCompany()

        company
    }

    fun getAll(): List<Company> = transaction {
        CompanyEntity.all().map { it.toCompany() }
    }
}




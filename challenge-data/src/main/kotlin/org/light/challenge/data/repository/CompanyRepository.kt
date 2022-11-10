package org.light.challenge.data.repository

import org.jetbrains.exposed.sql.transactions.transaction
import org.light.challenge.data.domain.Company

class CompanyRepository {

    fun getById(id: Int): Company? = transaction {
        val entity = CompanyEntity.findById(id)

        entity?.toCompany()
    }
}




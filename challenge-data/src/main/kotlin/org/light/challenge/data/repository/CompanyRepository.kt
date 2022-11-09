package org.light.challenge.data.repository


import org.jetbrains.exposed.sql.transactions.transaction
import org.light.challenge.data.EmployeeTable
import org.light.challenge.data.domain.Company

// todo: add interface ?
class CompanyRepository {

    fun getCompany(id: Int): Company? = transaction {
        val company = CompanyEntity.findById(id)
        val employees = EmployeeEntity.find { EmployeeTable.companyId eq id }

        company?.let {
            CompanyEntity.new {
                this.name = it.name
                this.employees = employees.map{e -> EmployeeEntity[e.id]}
            }.toCompany()}
    }
}



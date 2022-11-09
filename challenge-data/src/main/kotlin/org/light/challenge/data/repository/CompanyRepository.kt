package org.light.challenge.data.repository


import mu.KotlinLogging
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import org.light.challenge.data.EmployeeTable
import org.light.challenge.data.WorkflowTable
import org.light.challenge.data.domain.Company
import org.light.challenge.data.domain.Workflow


class CompanyRepository {
    //private val workflowRepo = WorkflowRepository()

    fun getById(id: Int): Company? = transaction {
        val company = CompanyEntity.findById(id)
        val employees = EmployeeEntity.find { EmployeeTable.companyId eq id }
        val workflow = WorkflowEntity.find { WorkflowTable.companyId eq id }.first()

        company?.let {
            CompanyEntity.new {
                this.name = it.name
                this.employees = employees.map{e -> EmployeeEntity[e.id]}
                this.workflow = workflow
            }.toCompany()}
    }

    fun getAll(): List<Company> = transaction {
        CompanyEntity.all().map{c -> c.toCompany()}
    }
}




package org.light.challenge.logic.core

import mu.KotlinLogging
import org.light.challenge.data.DatabaseFactory
import org.light.challenge.data.repository.CompanyRepository


class WorkflowService {
    private val db = DatabaseFactory.init()
    private val companyRepo = CompanyRepository()
    //private val workflowRepository = CompanyRepository()

    private val logger = KotlinLogging.logger {}
    fun handleInvoice (invoice: Invoice) {

        logger.info { "Handling invoice with for companyId ${invoice.companyId}" }
        val company = companyRepo.getById(invoice.companyId)

       // companyRepo.getAll().forEach{c ->  { logger.info { "name: ${c.name}" } }}
        logger.info { "Retrieved companies ${company?.name }}" }
        logger.info { "Retrieved company with employee ${company?.employees?.first()?.email }}" }


        // input rules and invoice, return single rule and get actions based of that

        // input company id and get all employees

    }
}

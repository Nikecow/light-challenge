package org.light.challenge.app

import mu.KotlinLogging
import org.light.challenge.data.repository.CompanyRepository
import org.light.challenge.data.repository.WorkflowRepository
import org.light.challenge.logic.core.DepartmentName
import org.light.challenge.logic.core.Invoice
import org.light.challenge.logic.core.NotifyService
import org.light.challenge.logic.core.WorkflowService

private val logger = KotlinLogging.logger {}

private val companyRepo = CompanyRepository()
private val workflowRepository = WorkflowRepository()
private val notifyService = NotifyService()

fun main(args: Array<String>) {

    val service = WorkflowService(companyRepo, workflowRepository, notifyService)

    logger.info { "Called main app with args: ${args.joinToString()} " }

    val (id, amount, department, manager) = args

    val invoice = Invoice(
        companyId = id.toInt(),
        amount = amount.toBigDecimal(),
        department = DepartmentName.valueOf(department.uppercase()),
        requiresManager = manager.toBoolean()
    )

    service.handleInvoice(invoice)
}
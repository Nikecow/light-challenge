package org.light.challenge.app

import mu.KotlinLogging
import org.light.challenge.data.domain.DepartmentName
import org.light.challenge.data.repository.CompanyRepository
import org.light.challenge.data.repository.WorkflowRepository
import org.light.challenge.logic.core.EmailService
import org.light.challenge.logic.core.NotifyService
import org.light.challenge.logic.core.SlackService
import org.light.challenge.logic.core.WorkflowService
import org.light.challenge.logic.core.domain.Invoice
import java.lang.RuntimeException

private val companyRepo = CompanyRepository()
private val workflowRepository = WorkflowRepository()
private val emailService = EmailService()
private val slackService = SlackService()
private val notifyService = NotifyService(emailService, slackService)
private val workflowService = WorkflowService(companyRepo, workflowRepository, notifyService)

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Called main app with args: ${args.joinToString()} " }

    if (args.toList().size !== 4) {
        throw RuntimeException("Not enough arguments passed to form an invoice. Exiting...")
    }

    val (id, amount, department, manager) = args

    val invoice = Invoice(
        companyId = id.toInt(),
        amount = amount.toBigDecimal(),
        department = DepartmentName.valueOf(department.uppercase()),
        requiresManager = manager.toBoolean()
    )

    workflowService.handleInvoice(invoice)
}
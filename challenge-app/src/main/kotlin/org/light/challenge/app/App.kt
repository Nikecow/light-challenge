package org.light.challenge.app

import mu.KotlinLogging
import org.light.challenge.logic.core.DepartmentName
import org.light.challenge.logic.core.Invoice
import org.light.challenge.logic.core.WorkflowService

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val service = WorkflowService()

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
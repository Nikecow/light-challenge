package org.light.challenge.logic.core

import mu.KotlinLogging
import org.light.challenge.data.DatabaseFactory
import org.light.challenge.data.domain.Company
import org.light.challenge.data.domain.Employee
import org.light.challenge.data.domain.Rule
import org.light.challenge.data.domain.Workflow
import org.light.challenge.data.repository.CompanyRepository
import org.light.challenge.data.repository.WorkflowRepository


class WorkflowService {
    private val db = DatabaseFactory.init()
    private val companyRepo = CompanyRepository()
    private val workflowRepository = WorkflowRepository()
    private val notifyService = NotifyService()

    private val logger = KotlinLogging.logger {}

    fun handleInvoice(invoice: Invoice): NotifyStatus {

        val companyId = invoice.companyId

        logger.info { "Handling invoice for companyId $companyId" }

        val company = companyRepo.getById(companyId)
            ?: throw MissingDataException("No such company with id $companyId")

        logger.info { "Retrieved company with name ${company.name}" }

        val workflow = workflowRepository.getByCompanyId(companyId)
            ?: throw MissingDataException("Company \"${company.name}\" has no attached workflow to it!")

        logger.info { "Retrieved workflow with id ${workflow.id} and chiefThreshold ${workflow.chiefThreshold}" }

        val rule = calculateRule(workflow, invoice)

        val targetEmployee = determineEmployee(rule, company.employees, workflow)

        val status = notifyService.notifyEmployee(targetEmployee, rule.notifyMethod)

        return status.also { logger.info { "Notified employee with status: ${it.name}" } }
    }

    fun calculateRule(workflow: Workflow, invoice: Invoice): Rule {
        val invoiceAmount = invoice.amount

        logger.info { "Determining rule for invoice with amount $invoiceAmount" }

        // Rules are ordered on descending cutoffAmount
        val rules = workflow.rules

        rules.forEach { r -> logger.info("Rule with id ${r.id} and cutoff ${r.cutoffAmount} dollars") }

        val rule = rules.firstOrNull { r -> r.cutoffAmount?.compareTo(invoiceAmount) == -1 } ?: rules.last()

        logger.info { "Using rule ${rule.id} with cutoff ${rule.cutoffAmount}" }

        return rule
    }

    fun determineEmployee(rule: Rule, employees: List<Employee>, workflow: Workflow): Employee {
        val headEmployeeId = rule.department.headEmployeeId
        val chiefThreshold = workflow.chiefThreshold
        val departmentName = rule.department.name

        logger.info { "Determining employee to notify based on rule with id ${rule.id}" }

        val employee = when {
            rule.cutoffAmount?.compareTo(chiefThreshold) == 1 -> employees.firstOrNull { it.id == rule.department.headEmployeeId }
                ?: throw MissingDataException("No head employee found in the department $departmentName with id $headEmployeeId")

            rule.requiresManager == true -> employees.firstOrNull { it.manager && it.department.id == rule.department.id }
                ?: throw MissingDataException("No manager found in the department $departmentName")

            else -> employees.firstOrNull { it.department.id == rule.department.id }
                ?: throw MissingDataException("No employees found in the department $departmentName")
        }

        return employee.also { logger.info { "Target employee is \"${it.name}\" with id ${it.id}" } }
    }
}

class MissingDataException(msg: String) : RuntimeException(msg)

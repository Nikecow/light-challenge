package org.light.challenge.logic.core

import mu.KotlinLogging
import org.light.challenge.data.CompanyDAO
import org.light.challenge.data.domain.Company
import org.light.challenge.data.domain.Employee
import org.light.challenge.data.domain.Rule
import org.light.challenge.data.domain.Workflow
import org.light.challenge.data.repository.CompanyRepository
import org.light.challenge.data.repository.WorkflowRepository
import org.light.challenge.logic.core.domain.Invoice
import org.light.challenge.logic.core.domain.NotifyStatus
import java.math.BigDecimal


class WorkflowService(
    private val companyRepository: CompanyRepository,
    private val workflowRepository: WorkflowRepository,
    private val notifyService: NotifyService
) {
    init {
        CompanyDAO.initDefaults()
    }

    private val logger = KotlinLogging.logger {}

    fun handleInvoice(invoice: Invoice): NotifyStatus {

        val companyId = invoice.companyId

        logger.info { "Handling invoice for companyId $companyId" }

        val company = companyRepository.getById(companyId)
            ?: throw MissingDataException("No such company with id $companyId")

        logger.info { "Retrieved company with name ${company.name}" }

        val workflow = workflowRepository.getByCompanyId(companyId)
            ?: throw MissingDataException("Company \"${company.name}\" has no attached workflow to it!")

        logger.info { "Retrieved workflow with id ${workflow.id} and chiefThreshold ${workflow.chiefThreshold}" }

        val rule = calculateRule(workflow, invoice)

        val targetEmployee = determineEmployee(company, rule, invoice, workflow.chiefThreshold)

        val status = notifyService.notifyEmployee(targetEmployee, rule.notifyMethod)

        return status.also {
            logger.info { "Notified the ${targetEmployee.department.name} department, returned status: ${it.name}" }
        }
    }

    fun calculateRule(workflow: Workflow, invoice: Invoice): Rule {
        val invoiceAmount = invoice.amount

        logger.info { "Determining rule for invoice with amount $invoiceAmount" }

        val rules = workflow.rules

        val rulesByCutoff = rules.filter { r -> r.cutoffAmount?.compareTo(invoiceAmount) == -1 }
        val ruleByDepartment = rulesByCutoff.filter { r -> r.department.name.toString() == invoice.department.name }
        val ruleByApproval = ruleByDepartment.filter { r -> r.requiresManager == invoice.requiresManager }

        val filteredRule =
            ruleByApproval.firstOrNull() ?: ruleByDepartment.firstOrNull() ?: rulesByCutoff.firstOrNull()
            ?: rules.lastOrNull()
            ?: throw MissingDataException("No rules found in the workflow with id ${workflow.id}")

        logger.info {
            "Using rule with id ${filteredRule.id} " +
                    "cutoff ${filteredRule.cutoffAmount} " +
                    "department ${filteredRule.department.name} " +
                    "requiresManager ${filteredRule.requiresManager} " +
                    "and notifyMethod ${filteredRule.notifyMethod}"
        }

        return filteredRule
    }

    private fun determineEmployee(
        company: Company,
        rule: Rule,
        invoice: Invoice,
        chiefThreshold: BigDecimal?
    ): Employee {
        val departmentName = rule.department.name
        val deptManagersOnly =
            company.employees.filter { it.department.id == rule.department.id && rule.department.headEmployeeId != it.id }

        logger.info { "Determining employee to notify based on rule with id ${rule.id}" }

        val employee = when {
            chiefThreshold !== null && invoice.amount.compareTo(chiefThreshold) == 1 -> {
                val department = company.departments.firstOrNull { it.name.toString() == invoice.department.toString() }
                val headEmployeeId = department?.headEmployeeId
                company.employees.firstOrNull { it.id == headEmployeeId }
                    ?: throw MissingDataException("No Chief found in the department ${department?.name} with id $headEmployeeId")
            }

            rule.requiresManager == true && invoice.requiresManager == true ->
                deptManagersOnly.firstOrNull { it.manager && it.department.id == rule.department.id }
                    ?: throw MissingDataException("No manager found in the department $departmentName")

            else -> company.employees.firstOrNull { it.department.id == rule.department.id && !it.manager }
                ?: throw MissingDataException("No employees found in the department $departmentName")
        }

        return employee.also { logger.info { "Target is ${employee.getTitle()} named \"${it.name}\" with id ${it.id}" } }
    }

    fun Employee.getTitle() = when {
        department.headEmployeeId == id -> "chief of ${department.name}"
        manager -> "manager of ${department.name}"
        else -> "employee of ${department.name}"
    }
}

class MissingDataException(msg: String) : RuntimeException(msg)

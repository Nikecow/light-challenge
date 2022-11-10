package org.light.challenge.logic.core

import assertk.all
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.messageContains
import org.junit.jupiter.api.Test
import org.light.challenge.data.domain.Company
import org.light.challenge.data.domain.Department
import org.light.challenge.data.domain.DepartmentName.FINANCE
import org.light.challenge.data.domain.DepartmentName.MARKETING
import org.light.challenge.data.domain.Employee
import org.light.challenge.data.domain.NotifyMethod
import org.light.challenge.data.domain.Rule
import org.light.challenge.data.domain.Workflow
import org.light.challenge.data.repository.CompanyRepository
import org.light.challenge.data.repository.WorkflowRepository
import org.light.challenge.logic.core.domain.Invoice
import org.light.challenge.logic.core.domain.NotifyStatus
import org.mockito.Mockito.verify
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal

internal class WorkflowServiceTest {
    private val companyRepository: CompanyRepository = mock()
    private val workflowRepository: WorkflowRepository = mock()
    private val notifyService: NotifyService = mock()

    private val subject = WorkflowService(companyRepository, workflowRepository, notifyService)

    val invoice = Invoice(1, 5000.toBigDecimal(), FINANCE, false)
    val department = Department(1, FINANCE, null)
    val finEmp = Employee(10, 1, "Jack Smith", "jack@mail.com", "1234", false, department)
    val finManager = Employee(11, 1, "Max Payne", "max@mail.com", "4567", true, department)
    val company = Company(1, "SomeCompany", listOf(finManager, finEmp), listOf(department))
    val rules = listOf(Rule(1, 1, department, 5000.toBigDecimal(), true, NotifyMethod.SLACK))
    val workflow = Workflow(1, 1, 10000.toBigDecimal(), rules)

    @Test
    internal fun `should process an invoice and send request on Slack`() {
        // given
        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(finEmp, NotifyMethod.SLACK)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(finEmp, NotifyMethod.SLACK)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should process an invoice and send request by Email`() {
        // given
        val rules = listOf(Rule(1, 1, department, 5000.toBigDecimal(), true, NotifyMethod.EMAIL))
        val workflow = Workflow(1, 1, 10000.toBigDecimal(), rules)

        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(finEmp, NotifyMethod.EMAIL)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(finEmp, NotifyMethod.EMAIL)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    /*
    * Even though the rule says go to Finance and that it
    * does not require manager, for invoices with an amount
    * higher than the chiefThreshold the invoice should go to
    * the head of the invoice related department, which is Marketing in this case
    * */
    internal fun `should send an email to the CMO when invoice amount higher than chiefThreshold related to Marketing`() {
        // given
        val chiefThreshold = 600000.toBigDecimal()
        val invoice = Invoice(1, chiefThreshold.add(BigDecimal.ONE), MARKETING, true)

        val chiefOfFinanceId = 100
        val chiefOfMarketingId = 200

        val departmentFinance = Department(1, FINANCE, chiefOfFinanceId)
        val departmentMarketing = Department(2, MARKETING, chiefOfMarketingId)

        val managerOfMarketing =
            Employee(5, 1, "Sean Seagal", "marketing-sean@mail.com", "71234", true, departmentMarketing)
        val chiefOfFinance =
            Employee(chiefOfFinanceId, 1, "Will Smith", "cfo@mail.com", "51234", true, departmentFinance)
        val chiefOfMarketing =
            Employee(chiefOfMarketingId, 1, "Michael Dorsey", "cmo@mail.com", "61234", true, departmentMarketing)
        val employee = Employee(10, 1, "Jack Smith", "some@mail.com", "1234", true, departmentFinance)

        val company = company.copy(
            employees = listOf(managerOfMarketing, employee, chiefOfFinance, chiefOfMarketing),
            departments = listOf(departmentFinance, departmentMarketing)
        )
        val rules = listOf(Rule(1, 1, departmentFinance, 5000.toBigDecimal(), false, NotifyMethod.EMAIL))
        val workflow = Workflow(1, 1, chiefThreshold, rules)

        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(chiefOfMarketing, NotifyMethod.EMAIL)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(chiefOfMarketing, NotifyMethod.EMAIL)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should send a Slack to a marketing manager with workflow with chiefThreshold is NULL`() {
        // given
        val invoice = Invoice(1, 1000000.toBigDecimal(), MARKETING, true)

        val chiefOfFinanceId = 100
        val chiefOfMarketingId = 200

        val departmentFinance = Department(1, FINANCE, chiefOfFinanceId)
        val departmentMarketing = Department(2, MARKETING, chiefOfMarketingId)

        val managerOfMarketing =
            Employee(5, 1, "Sean Seagal", "marketing-sean@mail.com", "71234", true, departmentMarketing)
        val chiefOfFinance =
            Employee(chiefOfFinanceId, 1, "Will Smith", "cfo@mail.com", "51234", true, departmentFinance)
        val chiefOfMarketing =
            Employee(chiefOfMarketingId, 1, "Michael Dorsey", "cmo@mail.com", "61234", true, departmentMarketing)
        val employee = Employee(10, 1, "Jack Smith", "some@mail.com", "1234", true, departmentFinance)

        val company = company.copy(
            employees = listOf(managerOfMarketing, employee, chiefOfFinance, chiefOfMarketing),
            departments = listOf(departmentFinance, departmentMarketing)
        )
        val rules = listOf(Rule(1, 1, departmentMarketing, 5000.toBigDecimal(), true, NotifyMethod.SLACK))
        val workflow = Workflow(1, 1, null, rules)

        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(managerOfMarketing, NotifyMethod.SLACK)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(managerOfMarketing, NotifyMethod.SLACK)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should send a Slack to a marketing employee with invoice amount exactly the chiefThreshold`() {
        // given
        val chiefThreshold = 600000.toBigDecimal()
        val invoice = Invoice(1, chiefThreshold, MARKETING, false)

        val departmentMarketing = Department(2, MARKETING, 200)
        val marketingEmployee = Employee(10, 1, "Jack Smith", "some@mail.com", "1234", false, departmentMarketing)

        val company = company.copy(
            employees = listOf(marketingEmployee),
            departments = listOf(departmentMarketing)
        )
        val rules = listOf(Rule(1, 1, departmentMarketing, 5000.toBigDecimal(), true, NotifyMethod.SLACK))
        val workflow = Workflow(1, 1, chiefThreshold, rules)

        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(marketingEmployee, NotifyMethod.SLACK)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(marketingEmployee, NotifyMethod.SLACK)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should send a Slack to employee of department of rule with invoice amount NOT higher than any threshold and which does NOT require manager`() {
        // given
        val requiresManager = false
        val invoice = Invoice(1, 20.toBigDecimal(), MARKETING, requiresManager)

        val departmentFinance = Department(50, FINANCE, 100)
        val financeEmployee = Employee(10, 1, "Jack Smith", "some@mail.com", "1234", false, departmentFinance)
        val managerOfFinance =
            Employee(5, 1, "Sean Seagal", "marketing-sean@mail.com", "71234", true, departmentFinance)

        val company = company.copy(
            employees = listOf(financeEmployee, managerOfFinance),
            departments = listOf(departmentFinance)
        )
        val rules = listOf(Rule(1, 1, departmentFinance, 5000.toBigDecimal(), requiresManager, NotifyMethod.SLACK))
        val workflow = Workflow(1, 1, 100000.toBigDecimal(), rules)

        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(financeEmployee, NotifyMethod.SLACK)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(financeEmployee, NotifyMethod.SLACK)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should send a Slack to a Finance employee with invoice which does not require manager with rule which does require manager`() {
        // given
        val invoice = Invoice(1, 20.toBigDecimal(), FINANCE, false)

        val departmentFinance = Department(50, FINANCE, 100)
        val financeEmployee = Employee(10, 1, "Jack Smith", "some@mail.com", "1234", false, departmentFinance)
        val managerOfFinance =
            Employee(5, 1, "Sean Seagal", "marketing-sean@mail.com", "71234", true, departmentFinance)

        val company = company.copy(
            employees = listOf(financeEmployee, managerOfFinance),
            departments = listOf(departmentFinance)
        )
        val rules = listOf(Rule(1, 1, departmentFinance, 5000.toBigDecimal(), true, NotifyMethod.SLACK))
        val workflow = Workflow(1, 1, 100000.toBigDecimal(), rules)

        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(financeEmployee, NotifyMethod.SLACK)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(financeEmployee, NotifyMethod.SLACK)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should send a Slack to a Finance employee with invoice amount which does require manager but not higher than chief threshold with rule which does NOT require manager`() {
        // given
        val invoice = Invoice(1, 20.toBigDecimal(), FINANCE, true)

        val departmentFinance = Department(50, FINANCE, 100)
        val managerOfFinance =
            Employee(5, 1, "Sean Seagal", "marketing-sean@mail.com", "71234", true, departmentFinance)
        val financeEmployee = Employee(10, 1, "Jack Smith", "some@mail.com", "1234", false, departmentFinance)

        val company = company.copy(
            employees = listOf(financeEmployee, managerOfFinance),
            departments = listOf(departmentFinance)
        )
        val rules = listOf(Rule(1, 1, departmentFinance, 5000.toBigDecimal(), false, NotifyMethod.SLACK))
        val workflow = Workflow(1, 1, 100000.toBigDecimal(), rules)

        whenever(companyRepository.getById(1)).thenReturn(company)
        whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)
        whenever(notifyService.notifyEmployee(financeEmployee, NotifyMethod.SLACK)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.handleInvoice(invoice)

        // then
        verify(companyRepository).getById(1)
        verify(workflowRepository).getByCompanyId(1)
        verify(notifyService).notifyEmployee(financeEmployee, NotifyMethod.SLACK)

        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should take the last rule if invoice amount is not higher than any cutoff rule`() {
        // given
        val invoice = Invoice(1, 100.toBigDecimal(), FINANCE, false)

        val rule1 = Rule(1, 1, department, 5000.toBigDecimal(), true, NotifyMethod.SLACK)
        val rule2 = Rule(2, 1, department, 3000.toBigDecimal(), true, NotifyMethod.SLACK)
        val rule3 = Rule(3, 1, department, 1000.toBigDecimal(), false, NotifyMethod.EMAIL)
        val rules = listOf(rule1, rule2, rule3)

        val workflow = Workflow(1, 1, 10000.toBigDecimal(), rules)

        // when
        val actual = subject.calculateRule(workflow, invoice)

        // then
        assertThat(actual).isEqualTo(rule3)
    }

    @Test
    internal fun `should take rule based on all matching properties`() {
        // given
        val invoice = Invoice(1, 6000.toBigDecimal(), FINANCE, false)

        val rule1 = Rule(1, 1, department, 5000.toBigDecimal(), true, NotifyMethod.SLACK)
        val rule2 = Rule(2, 1, department, 3000.toBigDecimal(), true, NotifyMethod.SLACK)
        val rule3 = Rule(3, 1, department, 1000.toBigDecimal(), false, NotifyMethod.EMAIL)
        val rules = listOf(rule1, rule2, rule3)

        val workflow = Workflow(1, 1, 10000.toBigDecimal(), rules)

        // when
        val actual = subject.calculateRule(workflow, invoice)

        // then
        assertThat(actual).isEqualTo(rule3)
    }

    @Test
    internal fun `should take rule based on matching cutoff and department`() {
        // given
        val invoice = Invoice(1, 5000.toBigDecimal(), FINANCE, true)

        val rule1 = Rule(1, 1, department, 5000.toBigDecimal(), true, NotifyMethod.SLACK)
        val rule2 = Rule(2, 1, department, 3000.toBigDecimal(), true, NotifyMethod.SLACK)
        val rule3 = Rule(3, 1, department, 1000.toBigDecimal(), false, NotifyMethod.EMAIL)
        val rules = listOf(rule1, rule2, rule3)

        val workflow = Workflow(1, 1, null, rules)

        // when
        val actual = subject.calculateRule(workflow, invoice)

        // then
        assertThat(actual).isEqualTo(rule2)
    }

    @Test
    internal fun `should not process an invoice when there are no employees`() {
        assertThat {
            whenever(companyRepository.getById(1)).thenReturn(company.copy(employees = emptyList()))
            whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow)

            subject.handleInvoice(invoice)
        }.isFailure().all {
            hasClass(MissingDataException::class.java)
            messageContains("No employees found in the department FINANCE")
        }
    }

    @Test
    internal fun `should not process an invoice when there are no rules`() {
        assertThat {
            whenever(companyRepository.getById(1)).thenReturn(company)
            whenever(workflowRepository.getByCompanyId(1)).thenReturn(workflow.copy(rules = emptyList()))

            subject.handleInvoice(invoice)
        }.isFailure().all {
            hasClass(MissingDataException::class.java)
            messageContains("No rules found in the workflow with id 1")
        }
    }
}

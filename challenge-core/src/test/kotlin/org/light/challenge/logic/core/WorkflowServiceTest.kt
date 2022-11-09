package org.light.challenge.logic.core

import org.junit.jupiter.api.Test

internal class WorkflowServiceTest {
    private val subject = WorkflowService()

    @Test
    internal fun `should process an invoice`() {
        // given
        val invoice = Invoice(1, 10.toBigDecimal(), DepartmentName.FINANCE, false)

        // when
        subject.handleInvoice(invoice)


    }
}

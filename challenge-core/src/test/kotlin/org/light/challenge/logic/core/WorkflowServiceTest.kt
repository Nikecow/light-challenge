package org.light.challenge.logic.core

import org.junit.jupiter.api.Test

internal class WorkflowServiceTest {
    private val subject = WorkflowService()

    @Test
    internal fun `should process an invoice`() {
        // given
        val invoice = Invoice(1, 5001.toBigDecimal(), DepartmentName.FINANCE, false)

        // when
        subject.handleInvoice(invoice)

        // then
//        argumentCaptor<MeterReport>().apply {
//            verify(customObjectMapper).writeToFile(
//                eq(File("target/Green_Button_Usage_Feed_1a46b097-b80a-4e25-8852-44f88b9179ae.json")),
//                capture()
//            )
//
//            assertThat(firstValue.id).isEqualTo(UUID.fromString("1a46b097-b80a-4e25-8852-44f88b9179ae"))
//            assertThat(firstValue.title).isEqualTo("Green Button Usage Feed")
//            assertThat(firstValue.meterInfo.flowDirection).isEqualTo(FlowDirection.UP)
//            assertThat(firstValue.meterInfo.unitPrice).isEqualByComparingTo("0.07")
//            assertThat(firstValue.meterInfo.readingUnit).isEqualTo(ReadingUnit.KWH)
//            assertThat(firstValue.priceSum).isEqualByComparingTo("45500.00")
//            assertThat(firstValue.usageSum).isEqualByComparingTo("650000.00")
//            assertThat(firstValue.hourlyData[ZonedDateTime.parse("2019-04-17T07:00:00Z")]!!.usage).isEqualByComparingTo("340000.00")
//            assertThat(firstValue.hourlyData[ZonedDateTime.parse("2019-04-17T07:00:00Z")]!!.price).isEqualByComparingTo("23800.00")
//            assertThat(firstValue.hourlyData[ZonedDateTime.parse("2019-04-17T08:00:00Z")]!!.usage).isEqualByComparingTo("260000.000")
//            assertThat(firstValue.hourlyData[ZonedDateTime.parse("2019-04-17T08:00:00Z")]!!.price).isEqualByComparingTo("18200.00")
//            assertThat(firstValue.hourlyData[ZonedDateTime.parse("2019-04-17T09:00:00Z")]!!.usage).isEqualByComparingTo("50000.00")
//            assertThat(firstValue.hourlyData[ZonedDateTime.parse("2019-04-17T09:00:00Z")]!!.price).isEqualByComparingTo("3500.00")
//        }
//    }
    }
}

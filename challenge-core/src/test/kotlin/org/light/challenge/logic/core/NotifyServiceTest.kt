package org.light.challenge.logic.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.light.challenge.data.domain.Department
import org.light.challenge.data.domain.DepartmentName
import org.light.challenge.data.domain.Employee
import org.light.challenge.data.domain.NotifyMethod
import org.light.challenge.logic.core.domain.NotifyStatus
import org.mockito.kotlin.mock

import org.mockito.kotlin.whenever

internal class NotifyServiceTest {
    private val emailService: EmailService = mock()
    private val slackService: SlackService = mock()

    private val subject = NotifyService(emailService, slackService)

    val employee =
        Employee(1, 1, "Jack Dorsey", "some@mail.com", "12345", false, Department(1, DepartmentName.FINANCE, null))

    @Test
    internal fun `should notify employee by Email `() {
        // given
        whenever(emailService.sendMail(employee.email)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.notifyEmployee(employee, NotifyMethod.EMAIL)

        // then
        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should fail to notify employee by Email `() {
        // given
        whenever(emailService.sendMail(employee.email)).thenReturn(NotifyStatus.FAILURE)

        // when
        val actual = subject.notifyEmployee(employee, NotifyMethod.EMAIL)

        // then
        assertThat(actual).isEqualTo(NotifyStatus.FAILURE)
    }

    @Test
    internal fun `should notify employee by Slack `() {
        // given
        whenever(slackService.notify(employee.slackId)).thenReturn(NotifyStatus.SUCCESS)

        // when
        val actual = subject.notifyEmployee(employee, NotifyMethod.SLACK)

        // then
        assertThat(actual).isEqualTo(NotifyStatus.SUCCESS)
    }

    @Test
    internal fun `should fail to notify employee by Slack `() {
        // given
        whenever(slackService.notify(employee.slackId)).thenReturn(NotifyStatus.FAILURE)

        // when
        val actual = subject.notifyEmployee(employee, NotifyMethod.SLACK)

        // then
        assertThat(actual).isEqualTo(NotifyStatus.FAILURE)
    }


}

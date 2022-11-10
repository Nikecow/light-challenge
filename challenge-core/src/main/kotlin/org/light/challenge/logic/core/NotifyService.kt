package org.light.challenge.logic.core

import mu.KotlinLogging
import org.light.challenge.data.domain.Employee
import org.light.challenge.data.domain.NotifyMethod
import org.light.challenge.logic.core.domain.NotifyStatus

class NotifyService(
    private val emailService: EmailService,
    private val slackService: SlackService
) {

    private val logger = KotlinLogging.logger {}

    fun notifyEmployee(employee: Employee, method: NotifyMethod) =
        if (method == NotifyMethod.EMAIL) notifyEmail(employee) else notifySlack(employee)

    private fun notifyEmail(employee: Employee): NotifyStatus {
        logger.info { "Attempting to notify employee with id ${employee.id} by Email" }

        val emailAddress = employee.email

        val result = emailService.sendMail(emailAddress)

        return result
    }

    private fun notifySlack(employee: Employee): NotifyStatus {
        logger.info { "Attempting to notify employee with id ${employee.id} on Slack" }

        val slackId = employee.slackId

        val result = slackService.notify(slackId)

        return result
    }
}
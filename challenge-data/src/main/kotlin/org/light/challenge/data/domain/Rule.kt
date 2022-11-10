package org.light.challenge.data.domain

import java.math.BigDecimal

data class Rule (
    val id: Int,
    val workflowId: Int,
    val departmentId: Department?,
    val cutoffAmount: BigDecimal?,
    val requiresManager: Boolean?,
    val notifyMethod: NotifyMethod
)

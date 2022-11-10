package org.light.challenge.data.domain

import java.math.BigDecimal

data class Rule (
    val id: Int,
    val workflowId: Int,
    val department: Department,
    val cutoffAmount: BigDecimal?,
    val requiresManager: Boolean?,
    val notifyMethod: NotifyMethod
)

package org.light.challenge.data.domain

import java.math.BigDecimal

data class Condition(
    val id: Int,
    val ruleId: Int,
    val departmentId: Department?,
    val cutoffAmount: BigDecimal?,
    val requiresManager: Boolean?,
)

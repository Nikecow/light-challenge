package org.light.challenge.data.domain

import java.math.BigDecimal

data class Workflow (
    val id: Int,
    val companyId: Int,
    val chiefThreshold: BigDecimal?,
    val rules: List<Rule>,
)

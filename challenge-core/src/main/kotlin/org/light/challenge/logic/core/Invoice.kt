package org.light.challenge.logic.core

import java.math.BigDecimal

data class Invoice(
    val companyId: Int,
    val amount: BigDecimal,
    val department: DepartmentName,
    val requiresManager: Boolean
)

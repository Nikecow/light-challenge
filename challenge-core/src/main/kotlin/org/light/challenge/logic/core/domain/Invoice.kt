package org.light.challenge.logic.core.domain

import org.light.challenge.data.domain.DepartmentName
import java.math.BigDecimal

data class Invoice(
    val companyId: Int,
    val amount: BigDecimal,
    val department: DepartmentName,
    val requiresManager: Boolean
)

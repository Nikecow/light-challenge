package org.light.challenge.data.domain

import java.math.BigDecimal

data class Department(
    val id: Int,
    val name: DepartmentName,
    val headEmployeeId: Int?
)

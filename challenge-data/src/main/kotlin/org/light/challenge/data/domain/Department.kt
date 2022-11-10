package org.light.challenge.data.domain

data class Department(
    val id: Int,
    val name: DepartmentName,
    val headEmployeeId: Int?
)

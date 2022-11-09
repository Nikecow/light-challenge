package org.light.challenge.data.domain

data class Employee(
    val id: Int,
    val companyId: Int,
    val name: String,
    val email: String,
    val slackId: String,
    val manager: Boolean,
    val department: Department?
)

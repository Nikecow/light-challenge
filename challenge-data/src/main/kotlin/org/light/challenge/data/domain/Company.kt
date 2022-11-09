package org.light.challenge.data.domain

data class Company (
    val id: Int,
    val name: String,
    val employees: List<Employee>,
    val workflow: Workflow?
)

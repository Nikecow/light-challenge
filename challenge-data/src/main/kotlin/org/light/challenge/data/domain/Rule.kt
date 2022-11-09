package org.light.challenge.data.domain

data class Rule (
    val id: Int,
    val workflowId: Int,
    val conditions: Condition?,
    val action: Action?
)

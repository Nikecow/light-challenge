package org.light.challenge.data.domain

data class Action(
    val id: Int,
    val ruleId: Int,
    val notifyMethod: NotifyMethod
)

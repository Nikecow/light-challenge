package org.light.challenge.data.repository


import mu.KotlinLogging
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import org.light.challenge.data.EmployeeTable
import org.light.challenge.data.RuleTable
import org.light.challenge.data.domain.Company
import org.light.challenge.data.domain.Rule
import org.light.challenge.data.domain.Workflow

// todo: add interface ? https://stackoverflow.com/questions/8550124/what-is-the-difference-between-dao-and-repository-patterns
class WorkflowRepository {
    private val logger = KotlinLogging.logger {}

    fun getById(id: Int): Workflow? = transaction {
        val workflow = WorkflowEntity.findById(id)
        val rules = RuleEntity.find { RuleTable.workflowId eq id }

        workflow?.let {
            WorkflowEntity.new {
                this.chiefThreshold = it.chiefThreshold
                this.rules = rules.map{r -> RuleEntity[r.id]}
            }.toWorkflow()}
    }

    fun getAll(): List<Workflow> = transaction {
        WorkflowEntity.all().map{c -> c.toWorkflow()}
    }
}




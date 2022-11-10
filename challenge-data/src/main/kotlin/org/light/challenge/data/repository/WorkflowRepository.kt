package org.light.challenge.data.repository

import org.jetbrains.exposed.sql.transactions.transaction
import org.light.challenge.data.WorkflowTable
import org.light.challenge.data.domain.Workflow

class WorkflowRepository {

    fun getByCompanyId(id: Int): Workflow? = transaction {
        val entity = WorkflowEntity.find {WorkflowTable.companyId eq id}.firstOrNull()

        entity?.toWorkflow()
    }

}




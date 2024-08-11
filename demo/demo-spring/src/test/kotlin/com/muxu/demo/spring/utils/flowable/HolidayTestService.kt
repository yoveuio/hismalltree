package com.muxu.demo.spring.utils.flowable

import org.flowable.engine.ProcessEngineConfiguration
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration
import org.flowable.engine.repository.Deployment
import org.junit.Test
import java.util.*


class HolidayTestService {

    @Test
    fun test() {
        val configuration = StandaloneProcessEngineConfiguration()
            .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
            .setJdbcUsername("sa")
            .setJdbcPassword("")
            .setJdbcDriver("org.h2.Driver")
            .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)

        val processEngine = configuration.buildProcessEngine()
        val repositoryService = processEngine.repositoryService
        val deployment: Deployment = repositoryService.createDeployment()
            .addClasspathResource("holiday-request.bpmn20.xml")
            .deploy()
        val processDefinition = repositoryService.createProcessDefinitionQuery()
            .deploymentId(deployment.id)
            .singleResult()
        println("Found process definition : " + processDefinition.name)

    }

}
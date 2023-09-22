package org.kotsuite.intellij.runConfiguration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class KotsuiteRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String {
        return KotsuiteRunConfigurationType.ID
    }

    override fun createTemplateConfiguration(project: Project): KotsuiteRunConfiguration {
        return KotsuiteRunConfiguration(project, this, "KotSuite")
    }

    override fun getOptionsClass(): Class<out BaseState> {
        return KotsuiteRunConfigurationOptions::class.java
    }
}
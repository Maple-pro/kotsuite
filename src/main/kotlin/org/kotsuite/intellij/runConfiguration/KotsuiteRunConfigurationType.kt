package org.kotsuite.intellij.runConfiguration

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons

class KotsuiteRunConfigurationType :
    ConfigurationTypeBase(ID, "KotSuite", "KotSuite run configuration type", AllIcons.RunConfigurations.Junit) {

    init {
        addFactory(KotsuiteRunConfigurationFactory(this))
    }

    companion object {
        const val ID = "KotsuiteRunConfiguration"
    }
}
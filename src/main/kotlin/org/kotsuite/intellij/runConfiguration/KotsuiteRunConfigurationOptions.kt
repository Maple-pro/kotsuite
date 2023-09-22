package org.kotsuite.intellij.runConfiguration

import com.intellij.execution.configurations.RunConfigurationOptions

class KotsuiteRunConfigurationOptions : RunConfigurationOptions() {
    private val javaPath = string("java").provideDelegate(this, "kotsuite-java")
    private val kotsuiteLocation = string("").provideDelegate(this, "kotsuite-kotsuite")
    private val projectPath = string("").provideDelegate(this, "kotsuite-project")
    private val modulePath = string("").provideDelegate(this, "kotsuite-module")
    private val moduleClassPath = string("").provideDelegate(this, "kotsuite-classpath")
    private val moduleSourcePath = string("").provideDelegate(this, "kotsuite-source")
    private val includeRules = string("").provideDelegate(this, "kotsuite-includes")
    private val libraryLocation = string("").provideDelegate(this, "kotsuite-libs")
    private val strategy = string("").provideDelegate(this, "kotsuite-strategy")
    private val dependency = string("").provideDelegate(this, "kotsuite-dependency")

    fun getJavaPath(): String? {
        return javaPath.getValue(this)
    }
    fun setJavaPath(p: String) {
        javaPath.setValue(this, p)
    }

    fun getKotsuiteLocation(): String? {
        return kotsuiteLocation.getValue(this)
    }
    fun setKotsuiteLocation(p: String) {
        kotsuiteLocation.setValue(this, p)
    }

    fun getProjectPath(): String? {
        return projectPath.getValue(this)
    }
    fun setProjectPath(p: String) {
        projectPath.setValue(this, p)
    }

    fun getModulePath(): String? {
        return modulePath.getValue(this)
    }
    fun setModulePath(p: String) {
        modulePath.setValue(this, p)
    }

    fun getModuleClassPath(): String? {
        return moduleClassPath.getValue(this)
    }
    fun setModuleClassPath(p: String) {
        moduleClassPath.setValue(this, p)
    }

    fun getModuleSourcePath(): String? {
        return moduleSourcePath.getValue(this)
    }
    fun setModuleSourcePath(p: String) {
        moduleSourcePath.setValue(this, p)
    }

    fun getIncludeRules(): String? {
        return includeRules.getValue(this)
    }
    fun setIncludeRules(p: String) {
        includeRules.setValue(this, p)
    }

    fun getLibraryLocation(): String? {
        return libraryLocation.getValue(this)
    }
    fun setLibraryLocation(p: String) {
        libraryLocation.setValue(this, p)
    }

    fun getStrategy(): String? {
        return strategy.getValue(this)
    }
    fun setStrategy(p: String) {
        strategy.setValue(this, p)
    }

    fun getDependency(): String? {
        return dependency.getValue(this)
    }
    fun setDependency(p: String) {
        dependency.setValue(this, p)
    }
}
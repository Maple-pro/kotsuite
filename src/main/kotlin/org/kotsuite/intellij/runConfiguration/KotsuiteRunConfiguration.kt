package org.kotsuite.intellij.runConfiguration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.io.File

class KotsuiteRunConfiguration(project: Project, factory: KotsuiteRunConfigurationFactory, name: String) :
    RunConfigurationBase<KotsuiteRunConfigurationOptions>(project, factory, name) {

    override fun getOptions(): KotsuiteRunConfigurationOptions{
        return super.getOptions() as KotsuiteRunConfigurationOptions
    }

    fun getJavaPath(): String? {
        return options.getJavaPath()
    }
    fun setJavaPath(p: String) {
        options.setJavaPath(p)
    }
    fun getKotsuiteLocation(): String? {
        return options.getKotsuiteLocation()
    }
    fun setKotsuiteLocation(p: String) {
        options.setKotsuiteLocation(p)
    }
    fun getProjectPath(): String? {
        return options.getProjectPath()
    }
    fun setProjectPath(p: String) {
        options.setProjectPath(p)
    }
    fun getModulePath(): String? {
        return options.getModulePath()
    }
    fun setModulePath(p: String) {
        options.setModulePath(p)
    }
    fun getModuleClassPath(): String? {
        return options.getModuleClassPath()
    }
    fun setModuleClassPath(p: String) {
        options.setModuleClassPath(p)
    }
    fun getModuleSourcePath(): String? {
        return options.getModuleSourcePath()
    }
    fun setModuleSourcePath(p: String) {
        options.setModuleSourcePath(p)
    }
    fun getIncludeRules(): String? {
        return options.getIncludeRules()
    }
    fun setIncludeRules(p: String) {
        options.setIncludeRules(p)
    }
    fun getLibraryLocation(): String? {
        return options.getLibraryLocation()
    }
    fun setLibraryLocation(p: String) {
        options.setLibraryLocation(p)
    }
    fun getStrategy(): String? {
        return options.getStrategy()
    }
    fun setStrategy(p: String) {
        options.setStrategy(p)
    }
    fun getDependency(): String? {
        return options.getDependency()
    }
    fun setDependency(p: String) {
        options.setDependency(p)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        // The better solution is to use `JavaCommandLineState` instead of `CommandLineState`,
        // but I don't find the documentation to use it
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                // Save Java argument to a file, and use `@argument` to pass the argument to the Java process
                val argumentFile = File(options.getModulePath(), "kotsuite-arguments.txt")
                val args = listOf(
                    "-jar", options.getKotsuiteLocation(),
                    "--project", options.getProjectPath(),
                    "--module", options.getModulePath(),
                    "--classpath", options.getModuleClassPath(),
                    "--source", options.getModuleSourcePath(),
                    "--includes", options.getIncludeRules(),
                    "--libs", options.getLibraryLocation(),
                    "--strategy", options.getStrategy(),
                    "--dependency", options.getDependency(),
                )
                argumentFile.writeText(args.joinToString(" ").replace("""\""", """\\"""))

                val generalCommandLine = GeneralCommandLine(
                    options.getJavaPath(),
                    "@${argumentFile.absolutePath}",
                )
                val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(generalCommandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return KotsuiteSettingsEditor()
    }
}
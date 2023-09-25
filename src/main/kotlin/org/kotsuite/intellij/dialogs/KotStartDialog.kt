package org.kotsuite.intellij.dialogs

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.kotlin.konan.file.File
import org.kotsuite.intellij.config.KotSuiteGlobalState
import org.kotsuite.intellij.config.utils.Utils
import org.kotsuite.intellij.runConfiguration.KotsuiteRunConfiguration
import org.kotsuite.intellij.runConfiguration.KotsuiteRunConfigurationType
import java.awt.BorderLayout
import javax.swing.*
import kotlin.reflect.full.declaredMemberProperties

data class Parameters(
    val javaHome: String,
    val projectPath: String,
    val modulePath: String,
    val moduleClassPath: String,
    val moduleSourcePath: String,
    val kotSuiteLocation: String,
    val libraryLocation: String,
    val strategy: String,
    val includeRules: String,
    val dependencyClassPaths: String,
)

class KotStartDialog(
    private val project: Project,
    private val module: Module,
    private val selectedPath: String,
) : DialogWrapper(true) {

    private var moduleRootPath: String
    private var moduleClassPath: String
    private var moduleSourcePath: String
    private var includeRules: String
    private var dependencyClassPaths: String

    private var moduleRootPathField: JBTextField
    private var moduleClassPathField: JBTextField
    private var moduleSourcePathField: JBTextField
    private var includeRulesField: JBTextField

    init {
        title = "KotSuite Options"

        moduleRootPath = getModuleRootPath()
        moduleClassPath = getModuleClassPath()
        moduleSourcePath = getModuleSourcePath()
        includeRules = getIncludeRules()
        dependencyClassPaths = getModuleDependencyClassPaths()

        exportModuleDependencyClassPath(dependencyClassPaths, moduleRootPath)

        moduleRootPathField = Utils.createTextField(moduleRootPath, width = 1200)
        moduleClassPathField = Utils.createTextField(moduleClassPath, width = 1200)
        moduleSourcePathField = Utils.createTextField(moduleSourcePath, width = 1200)
        includeRulesField = Utils.createTextField(includeRules, width = 1200)
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(
                Utils.createJBLabel("Selected Module Path: ", width = 250),
                moduleRootPathField,
            )
            .addLabeledComponent(
                Utils.createJBLabel("Module Class Path: ", width = 250),
                moduleClassPathField,
            )
            .addLabeledComponent(
                Utils.createJBLabel("Module Source Path: ", width = 250),
                moduleSourcePathField,
            )
            .addLabeledComponent(
                Utils.createJBLabel("Include Rules: ", width = 250),
                includeRulesField,
            )
            .panel

        dialogPanel.add(formPanel, BorderLayout.CENTER)

        return dialogPanel
    }

    override fun doValidate(): ValidationInfo? {
        val parameters = getParameters()

        if (!parameters.allPropertiesNotEmpty()) {
            return ValidationInfo("Text fields cannot be empty.")
        }

        return null
    }

    private fun Parameters.allPropertiesNotEmpty(): Boolean {
        return this::class.declaredMemberProperties.all { prop ->
            val value = prop.getter.call(this) as String
            value.isNotEmpty()
        }
    }

    private fun getParameters(): Parameters {
        val kotSuiteGlobalState = KotSuiteGlobalState.getInstance()

        val javaHome = if (kotSuiteGlobalState.javaHome == "") "java" else kotSuiteGlobalState.javaHome

        return Parameters(
            javaHome,
            project.basePath!!,
            moduleRootPathField.text,
            moduleClassPathField.text,
            moduleSourcePathField.text,
            kotSuiteGlobalState.kotSuiteLocation,
            kotSuiteGlobalState.libraryLocation,
            kotSuiteGlobalState.strategy,
            includeRulesField.text,
            dependencyClassPaths,
        )
    }

    override fun doOKAction() {
        close(CANCEL_EXIT_CODE)
        val parameters = getParameters()

        val configurationName = createKotsuiteRunConfiguration(project, parameters)
        runKotsuiteRunConfiguration(project, configurationName)
    }

    private fun getModuleRootPath(): String {
        val contentRoots = ModuleRootManager.getInstance(module).contentRoots.map { it.path }

        if (contentRoots.isEmpty()) {
            return ""
        }

        var moduleRootPath = contentRoots.first()

        contentRoots.forEach {
            while (!it.startsWith(moduleRootPath)) {
                moduleRootPath = moduleRootPath.substringBeforeLast("/")
            }
        }

        return moduleRootPath
    }

    private fun getIncludeRules(): String {

        val rules = selectedPath.replace(moduleRootPath, "")
            .substringAfter("main/")
            .substringAfter("java/")
            .substringAfter("kotlin/")
            .replace("/", ".")
            .removeSuffix(".kt")
            .removeSuffix(".java")

        return rules.ifEmpty { "*" }
    }

    private fun getModuleClassPath(): String {
        val classesRoots = ModuleRootManager.getInstance(module)
            .orderEntries().classes().roots.map { it.path }
        val results = classesRoots.filter { it.contains(moduleRootPath) }

        return results.joinToString(File.pathSeparator)
    }

    private fun getModuleSourcePath(): String {
        val sourceRoots = ModuleRootManager.getInstance(module).sourceRoots.map { it.path }
        return sourceRoots.joinToString(File.pathSeparator)
    }

    private fun getModuleDependencyClassPaths(): String {
        val classPaths = mutableListOf<String>()

        val testModules = ModuleManager.getInstance(project).modules
            .filter { it.name.contains("Test") || it.name.contains("test") }

        testModules.forEach { module ->
            val classesRoots = OrderEnumerator.orderEntries(module).recursively()
                .classesRoots.map { it.path }
            classesRoots.forEach { classPaths.add(it) }
        }

        module.run {
            val classesRoots = OrderEnumerator.orderEntries(module).recursively()
                .classesRoots.map { it.path }
            classesRoots.forEach { classPaths.add(it) }
        }

        return classPaths.distinct().joinToString(File.pathSeparator)
    }

    private fun exportModuleDependencyClassPath(moduleDependencyClassPath: String, moduleRootPath: String) {
        val parts = moduleDependencyClassPath.split(File.pathSeparator)
        val transformedParts = parts.map { "\"${it}\"," }
        val transformedString = transformedParts.joinToString("\n")
        try {
            val file = File(moduleRootPath, "kotsuite-dependency-classpath.txt")
            file.writeText(transformedString)
        } catch (e: Exception) {
            println("An error occurred while exporting module dependency class path: ${e.message}")
            return
        }

    }

    private fun createKotsuiteRunConfiguration(project: Project, parameters: Parameters): String {
        val configurationName = "KotSuite-${parameters.includeRules}"

        val runnerAndConfigurationSettings = RunManager.getInstance(project)
            .createConfiguration(configurationName, KotsuiteRunConfigurationType::class.java)

        with(runnerAndConfigurationSettings.configuration as KotsuiteRunConfiguration) {
            this.setJavaPath(if (parameters.javaHome == "java") "java" else "${parameters.javaHome}/bin/java")
            this.setKotsuiteLocation(parameters.kotSuiteLocation)
            this.setProjectPath(parameters.projectPath)
            this.setModulePath(parameters.modulePath)
            this.setModuleClassPath(parameters.moduleClassPath)
            this.setModuleSourcePath(parameters.moduleSourcePath)
            this.setIncludeRules(parameters.includeRules)
            this.setLibraryLocation(parameters.libraryLocation)
            this.setStrategy(parameters.strategy)
            this.setDependency(parameters.dependencyClassPaths)
        }
        RunManager.getInstance(project).addConfiguration(runnerAndConfigurationSettings)

        return runnerAndConfigurationSettings.name
    }

    private fun runKotsuiteRunConfiguration(project: Project, configurationName: String) {
        val runnerAndConfigurationSettings = RunManager.getInstance(project).findConfigurationByTypeAndName(KotsuiteRunConfigurationType.ID, configurationName)
        val executor = DefaultRunExecutor.getRunExecutorInstance()
        ProgramRunnerUtil.executeConfiguration(runnerAndConfigurationSettings!!, executor!!)
    }
}
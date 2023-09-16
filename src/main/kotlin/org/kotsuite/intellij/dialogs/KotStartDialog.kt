package org.kotsuite.intellij.dialogs

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.kotlin.konan.file.File
import org.kotsuite.intellij.actions.KotSuiteProcess
import org.kotsuite.intellij.config.KotSuiteGlobalState
import org.kotsuite.intellij.config.utils.Utils
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

    private val toolWindowManager = ToolWindowManager.getInstance(project)
    private val toolWindow = toolWindowManager.getToolWindow("kotsuite")
    private val notifier = IntelliJNotifier.getNotifier(project)

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

        toolWindow?.show()

        notifier?.clearConsole()

        notifier?.printOnConsole("Start KotSuite ...\n")

        val parameters = getParameters()
        notifier?.printOnConsole(
            "Java Home: ${parameters.javaHome}\n" +
                    "Project Location: ${parameters.projectPath}\n" +
                    "Module Root Path: ${parameters.modulePath}\n" +
                    "KotSuite Location: ${parameters.kotSuiteLocation}\n" +
                    "Library Location: ${parameters.libraryLocation}\n" +
                    "Strategy: ${parameters.strategy}\n" +
                    "Include Rules: ${parameters.includeRules}\n" +
                    "Module Class Path: ${parameters.moduleClassPath}\n"
//                    "Dependency class paths: ${parameters.dependencyClassPaths}\n"
        )

        startKotSuite(parameters)
    }

    private fun startKotSuite(parameters: Parameters) {

        val javaPath = if (parameters.javaHome == "java") "java" else "${parameters.javaHome}/bin/java"

        val args = arrayOf(
            javaPath,
            "-jar",
            parameters.kotSuiteLocation,
            "--project", parameters.projectPath,
            "--module", parameters.modulePath,
            "--classpath", parameters.moduleClassPath,
            "--source", parameters.moduleSourcePath,
            "--includes", parameters.includeRules,
            "--libs", parameters.libraryLocation,
            "--strategy", parameters.strategy,
            "--dependency", parameters.dependencyClassPaths,
        )

//        notifier?.printOnConsole("Run command: ${args.joinToString(" ")}\n")

        KotSuiteProcess.process = Runtime.getRuntime().exec(args)
        if (KotSuiteProcess.process != null) {
            notifier?.attachProcess(KotSuiteProcess.process!!)
            KotSuiteProcess.process!!.waitFor()
        }

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
//            classPaths.add(classesRoots.joinToString(":"))
            classesRoots.forEach { classPaths.add(it) }
        }

        module.run {
            val classesRoots = OrderEnumerator.orderEntries(module).recursively()
                .classesRoots.map { it.path }
//            classPaths.add(classesRoots.joinToString(":"))
            classesRoots.forEach { classPaths.add(it) }
        }

        return classPaths.distinct().joinToString(File.pathSeparator)
    }
}
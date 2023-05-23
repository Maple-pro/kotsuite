package org.kotsuite.intellij.dialogs

import com.intellij.openapi.module.Module
import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
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
    val kotSuiteLocation: String,
    val libraryLocation: String,
    val strategy: String,
    val includeRules: String,
    val moduleClassPath: String,
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
    private var includeRules: String

    private var moduleRootPathField: JBTextField
    private var moduleClassPathField: JBTextField
    private var includeRulesField: JBTextField

    init {
        title = "KotSuite Options"

        moduleRootPath = getModuleRootPath(module)
        moduleClassPath = getModuleClassPath(module)
        includeRules = getIncludeRules(selectedPath)

        moduleRootPathField = Utils.createTextField(moduleRootPath, width = 1200)
        moduleClassPathField = Utils.createTextField(moduleClassPath, width = 1200)
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
            kotSuiteGlobalState.kotSuiteLocation,
            kotSuiteGlobalState.libraryLocation,
            kotSuiteGlobalState.strategy,
            includeRulesField.text,
            moduleClassPathField.text,
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
                    "Project Location: ${parameters.projectPath}" +
                    "Module Root Path: ${parameters.modulePath}" +
                    "KotSuite Location: ${parameters.kotSuiteLocation}\n" +
                    "Library Location: ${parameters.libraryLocation}\n" +
                    "Strategy: ${parameters.strategy}\n" +
                    "Include Rules: ${parameters.includeRules}\n" +
                    "Module Class Path: ${parameters.moduleClassPath}"
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
            "--modules", parameters.modulePath,
            "--includes", parameters.includeRules,
            "--libs", parameters.libraryLocation,
            "--strategy", parameters.strategy,
            "--classpath", parameters.moduleClassPath,
        )

        notifier?.printOnConsole("Run command: ${args.joinToString(" ")}\n")

        KotSuiteProcess.process = Runtime.getRuntime().exec(args)
        if (KotSuiteProcess.process != null) {
            notifier?.attachProcess(KotSuiteProcess.process!!)
            KotSuiteProcess.process!!.waitFor()
        }

    }

    private fun getModuleRootPath(module: Module): String {
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

    private fun getIncludeRules(selectedPath: String): String {

        val rules = selectedPath.replace(moduleRootPath, "")
            .substringAfter("main/")
            .substringAfter("java/")
            .substringAfter("kotlin/")
            .replace("/", ".")

        return rules.ifEmpty { "*" }
    }

    private fun getModuleClassPath(module: Module): String {
        val classesRoots = ModuleRootManager.getInstance(module)
            .orderEntries().classes().roots.map { it.path }

        return classesRoots.first {
            it.contains("build/intermediates/javac/")
                    || it.contains("build/tmp/kotlin-classes/debug")
        }
    }
}
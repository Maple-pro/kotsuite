package org.kotsuite.intellij.dialogs

import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.ui.FormBuilder
import org.jetbrains.kotlin.tools.projectWizard.core.parseAs
import org.kotsuite.intellij.config.KotSuiteGlobalState
import org.kotsuite.intellij.config.utils.Utils
import java.awt.BorderLayout
import javax.swing.*

data class Parameters(
    val javaHome: String,
    val kotSuiteLocation: String,
    val libraryLocation: String,
    val strategy: String,
    val includeRules: String,
)

class KotStartDialog(private val project: Project) : DialogWrapper(true) {

    private val toolWindowManager = ToolWindowManager.getInstance(project)
    private val toolWindow = toolWindowManager.getToolWindow("kotsuite")
    private val notifier = IntelliJNotifier.getNotifier(project)

    private val includeRules = "*.kt"

    private val includeRulesField = Utils.createTextField(includeRules)

    init {
        title = "KotSuite Options"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())


        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(
                Utils.createJBLabel("Include Rules: "),
                includeRulesField
            )
            .panel

        dialogPanel.add(formPanel, BorderLayout.CENTER)

        return dialogPanel
    }

    override fun doValidate(): ValidationInfo? {
        val parameters = getParameters()

        if (parameters.javaHome == ""
            || parameters.kotSuiteLocation == ""
            || parameters.libraryLocation == ""
            || parameters.strategy == ""
            || parameters.includeRules == ""
        ) {
            return ValidationInfo("Text fields cannot be empty.")
        }

        return null
    }

    private fun getParameters(): Parameters {
        val kotSuiteGlobalState = KotSuiteGlobalState.getInstance()

        val javaHome = if (kotSuiteGlobalState.javaHome == "") "java" else kotSuiteGlobalState.javaHome

        return Parameters(
            javaHome,
            kotSuiteGlobalState.kotSuiteLocation,
            kotSuiteGlobalState.libraryLocation,
            kotSuiteGlobalState.strategy,
            includeRulesField.text,
        )
    }

    override fun doOKAction() {
        close(CANCEL_EXIT_CODE)

        toolWindow?.show()

        Thread.sleep(1000)

        notifier?.clearConsole()

        notifier?.printOnConsole("Start KotSuite ...\n")

        val parameters = getParameters()
        notifier?.printOnConsole("Java Home: ${parameters.javaHome}\n" +
                "KotSuite Location: ${parameters.kotSuiteLocation}\n" +
                "Library Location: ${parameters.libraryLocation}\n" +
                "Strategy: ${parameters.strategy}\n" +
                "Include Rules: ${parameters.includeRules}\n"
        )

        startKotSuite(parameters)
    }

    private fun startKotSuite(parameters: Parameters) {

        val javaPath = if (parameters.javaHome == "java") "java" else "${parameters.javaHome}/bin/java"
        val projectPath = project.basePath

        val args = arrayOf(
            javaPath,
            "-jar",
            parameters.kotSuiteLocation,
            "--project", projectPath,
            "--includes", parameters.includeRules,
            "--libs", parameters.libraryLocation,
            "--strategy", parameters.strategy,
        )

        notifier?.printOnConsole("Run command: ${args.joinToString(" ")}\n")

//        val ps = Runtime.getRuntime().exec(args)
//        notifier?.attachProcess(ps)
//        ps.waitFor()

    }

}
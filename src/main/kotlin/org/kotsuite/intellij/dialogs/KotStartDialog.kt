package org.kotsuite.intellij.dialogs

import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.TextFieldWithHistory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

enum class ParameterFieldType {
    DIRECTORY,
    TEXT,
    SELECT,
}

data class ParameterField(
    val id: String,
    val labelText: String,
    var field: JComponent?,
    val defaultField: String?,
    val type: ParameterFieldType,
)

data class TextFields(
    val project: String,
    val include: String,
    val strategy: String,
    val kotsuite: String,
    val library: String,
    val java: String,
)

class KotStartDialog(private val project: Project) : DialogWrapper(true) {

    private val strategies = arrayOf("random", "ga")

    private val javaHome = System.getenv("JAVA_HOME")

    private val parameterFields = listOf(
        ParameterField(
            "PROJECT",
            "Project Location: ",
            null,
            project.basePath,
            ParameterFieldType.DIRECTORY,
        ),
        ParameterField(
            "INCLUDE",
            "Include Classes or packages: ",
            null,
            null,
            ParameterFieldType.TEXT,
        ),
        ParameterField(
            "STRATEGY",
            "Strategy: ",
            null,
            null,
            ParameterFieldType.SELECT,
        ),
        ParameterField(
            "KOTSUITE",
            "KotSuite Location: ",
            null,
            null,
            ParameterFieldType.DIRECTORY,
        ),
        ParameterField(
            "LIBRARY",
            "Library Location: ",
            null,
            null,
            ParameterFieldType.DIRECTORY,
        ),
        ParameterField(
            "JAVA",
            "JAVA HOME: ",
            null,
            javaHome,
            ParameterFieldType.DIRECTORY,
        ),
    )

    private val toolWindowManager = ToolWindowManager.getInstance(project)
    private val toolWindow = toolWindowManager.getToolWindow("kotsuite")
    private val notifier = IntelliJNotifier.getNotifier(project)

    init {
        title = "KotSuite Options"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel()
        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.Y_AXIS)

        parameterFields.forEach {
            dialogPanel.add(createParameterField(it))
        }

        return dialogPanel
    }

    override fun doValidate(): ValidationInfo? {
        val textFields = getAllTextFields()

        if (textFields.project == ""
            || textFields.include == ""
            || textFields.strategy == ""
            || textFields.kotsuite == ""
            || textFields.java == ""
        ) {
            return ValidationInfo("Text fields cannot be empty.")
        }

        return null
    }

    private fun createParameterField(parameterField: ParameterField): JComponent {
        return when (parameterField.type) {
            ParameterFieldType.TEXT -> createTextParameterField(parameterField)
            ParameterFieldType.DIRECTORY -> createDirectoryParameterField(parameterField)
            ParameterFieldType.SELECT -> {
                createSelectParameterField(parameterField, strategies)
            }
        }
    }

    private fun createTextParameterField(parameterField: ParameterField): JComponent {
        val parameterPanel = JPanel(BorderLayout())

        val label = JLabel(parameterField.labelText)
        label.preferredSize = Dimension(150, 30)

        val textField = TextFieldWithHistory()
        textField.preferredSize = Dimension(450, 30)

        parameterPanel.add(label, BorderLayout.WEST)
        parameterPanel.add(textField, BorderLayout.EAST)

        parameterField.field = textField

        return parameterPanel
    }

    private fun createDirectoryParameterField(parameterField: ParameterField): JComponent {
        val parameterPanel = JPanel(BorderLayout())

        val label = JLabel(parameterField.labelText)
        label.preferredSize = Dimension(150, 30)

        val textFieldWithBrowseButton = TextFieldWithBrowseButton()
        with(textFieldWithBrowseButton) {
            preferredSize = Dimension(450, 30)
            addBrowseFolderListener(
                "Choose File...",
                "",
                project,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )

            if (parameterField.defaultField != null) {
                text = parameterField.defaultField
            }
        }

        parameterPanel.add(label, BorderLayout.WEST)
        parameterPanel.add(textFieldWithBrowseButton, BorderLayout.EAST)

        parameterField.field = textFieldWithBrowseButton

        return parameterPanel
    }

    private fun createSelectParameterField(
        parameterField: ParameterField, values: Array<String>
    ): JComponent {
        val parameterPanel = JPanel(BorderLayout())

        val label = JLabel(parameterField.labelText)
        label.preferredSize = Dimension(150, 30)

        val comboBox = ComboBox(values)
        comboBox.preferredSize = Dimension(450, 30)

        parameterPanel.add(label, BorderLayout.WEST)
        parameterPanel.add(comboBox, BorderLayout.EAST)

        parameterField.field = comboBox

        return parameterPanel
    }

    private fun getAllTextFields(): TextFields {
        var project = ""
        var include = ""
        var strategy = ""
        var kotsuite = ""
        var library = ""
        var java = ""

        parameterFields.forEach {
            when (it.id) {
                "PROJECT" -> {
                    project = (it.field as TextFieldWithBrowseButton).text
                }
                "INCLUDE" -> {
                    include = (it.field as TextFieldWithHistory).text
                }
                "STRATEGY" -> {
                    strategy = (it.field as ComboBox<*>).selectedItem?.toString() ?: "error"
                }
                "KOTSUITE" -> {
                    kotsuite = (it.field as TextFieldWithBrowseButton).text
                }
                "LIBRARY" -> {
                    library = (it.field as TextFieldWithBrowseButton).text
                }
                "JAVA" -> {
                    java = (it.field as TextFieldWithBrowseButton).text
                }
            }
        }

        return TextFields(project, include, strategy, kotsuite, library, java)
    }

    override fun doOKAction() {
        close(CANCEL_EXIT_CODE)

        toolWindow?.show()
        notifier?.clearConsole()

        notifier?.printOnConsole("Start KotSuite ...\n")

        val textFields = getAllTextFields()
        notifier?.printOnConsole("project: ${textFields.project}\n" +
                "include: ${textFields.include}\n" +
                "strategy: ${textFields.strategy}\n" +
                "kotsuite: ${textFields.kotsuite}\n" +
                "library: ${textFields.library}\n" +
                "java: ${textFields.java}\n"
        )

        startKotSuite(textFields)
    }

    private fun startKotSuite(textFields: TextFields) {
        val args = arrayOf(
            "${textFields.java}/bin/java",
//            "java",
            "-jar",
            textFields.kotsuite,
            "--project", textFields.project,
            "--includes", textFields.include,
            "--libs", textFields.library,
            "--strategy", textFields.strategy,
        )

        notifier?.printOnConsole("Run command: ${args.joinToString(" ")}\n")

        val ps = Runtime.getRuntime().exec(args)
        notifier?.attachProcess(ps)
        ps.waitFor()

    }

}
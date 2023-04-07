package org.kotsuite.intellij.dialogs

import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.wm.ToolWindowManager
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class KotStartDialog(private val project: Project) : DialogWrapper(true) {

    private val label2Text = mapOf(
        "EXPORT_FOLDER0" to "Export Folder: ",
        "KOTSUITE_LOCATION" to "KotSuite Location: ",
    )

    private val toolWindowManager = ToolWindowManager.getInstance(project)
    private val toolWindow = toolWindowManager.getToolWindow("kotsuite")
    private val notifier = IntelliJNotifier.getNotifier(project)
    private val label2Field = mutableMapOf<String, TextFieldWithBrowseButton>()

    init {
        title = "KotSuite Options"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel()
        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.Y_AXIS)

        label2Text.forEach {
            dialogPanel.add(createParameterPanel(it.key, it.value))
        }

        return dialogPanel
    }

    override fun doValidate(): ValidationInfo? {
        label2Field.forEach {
            val text = it.value.text
            if (text.isBlank()) {
                return ValidationInfo("Field can't be empty.")
            }
        }

        return null
    }

    private fun createParameterPanel(labelId: String, labelText: String): JComponent {
        val parameterPanel = JPanel(BorderLayout())

        val label = JLabel(labelText)
        label.preferredSize = Dimension(100, 16)

        val textFieldWithBroseButton = TextFieldWithBrowseButton()
        with (textFieldWithBroseButton) {
            preferredSize = Dimension(300, 30)
            addBrowseFolderListener(
                "Choose File...",
                "",
                project,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        parameterPanel.add(label, BorderLayout.WEST)
        parameterPanel.add(textFieldWithBroseButton, BorderLayout.EAST)

        label2Field[labelId] = textFieldWithBroseButton

        return parameterPanel
    }

    override fun doOKAction() {
        close(CANCEL_EXIT_CODE)

        toolWindow?.show()
        notifier?.clearConsole()

        notifier?.printOnConsole("Start KotSuite ...\n")

        label2Field.forEach {
            notifier?.printOnConsole("${it.value.text}\n")
        }


    }

}
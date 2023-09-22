package org.kotsuite.intellij.config.utils

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import java.awt.Dimension

object Utils {

    private const val LABEL_WIDTH = 200
    private const val FIELD_WIDTH = 500
    private const val HEIGHT = 50

    fun createJBLabel(
        labelText: String,
        width: Int = LABEL_WIDTH, height: Int = HEIGHT,
        setPreferredSize: Boolean = true,
    ): JBLabel {

        val label = JBLabel(labelText)
        if (setPreferredSize) {
            label.preferredSize = Dimension(width, height)
        }

        return label
    }

    fun createTextFieldWithBrowserButton(
        defaultFieldText: String? = null,
        width: Int = FIELD_WIDTH, height: Int = HEIGHT,
        setPreferredSize: Boolean = true,
    ): TextFieldWithBrowseButton {

        val textFieldWithBrowseButton = TextFieldWithBrowseButton()

        with(textFieldWithBrowseButton) {
            if (setPreferredSize) {
                preferredSize = Dimension(width, height)
            }
            addBrowseFolderListener(
                "Choose File...",
                "",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )

            if (defaultFieldText != null) {
                text = defaultFieldText
            }
        }

        return textFieldWithBrowseButton
    }

    fun createComboBox(
        values: Array<String>,
        width: Int = FIELD_WIDTH, height: Int = HEIGHT,
        setPreferredSize: Boolean = true,
    ): ComboBox<String> {

        val comboBox = ComboBox(values)
        if (setPreferredSize) {
            comboBox.preferredSize = Dimension(width, height)
        }

        return comboBox
    }

    fun createTextField(
        defaultFieldText: String?,
        width: Int = FIELD_WIDTH, height: Int = HEIGHT,
        setPreferredSize: Boolean = true,
    ): JBTextField {
        val textField = JBTextField()
        if (setPreferredSize) {
            textField.preferredSize = Dimension(width, height)
        }

        if (defaultFieldText != null) {
            textField.text = defaultFieldText
        }

        return textField
    }
}
package org.kotsuite.intellij.config

import com.intellij.util.ui.FormBuilder
import org.kotsuite.intellij.config.utils.Utils
import java.awt.BorderLayout
import javax.swing.JPanel

class KotSuiteGlobalComponent {

    private val strategy = arrayOf("random", "ga")

    val javaHomeField = Utils.createTextFieldWithBrowserButton()
    val kotSuiteLocationField = Utils.createTextFieldWithBrowserButton()
    val libraryLocationField = Utils.createTextFieldWithBrowserButton()
    val strategyField = Utils.createComboBox(strategy)

    private var mainPanel: JPanel

    init {
        mainPanel = createMainPanel()
    }

    fun getPanel(): JPanel {
        return mainPanel
    }

    private fun createMainPanel(): JPanel {
        val rootPanel = JPanel(BorderLayout())

        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(
                Utils.createJBLabel("Java Home: "),
                javaHomeField,
                1,
            )
            .addLabeledComponent(
                Utils.createJBLabel("KotSuite Location: "),
                kotSuiteLocationField,
                2,
            )
            .addLabeledComponent(
                Utils.createJBLabel("Library Location: "),
                libraryLocationField,
                3,
            )
            .addLabeledComponent(
                Utils.createJBLabel("Generation Strategy: "),
                strategyField,
                4,
            )
            .panel

        rootPanel.add(formPanel, BorderLayout.NORTH)

        return rootPanel
    }

    fun getJavaHome(): String {
        return javaHomeField.text
    }

    fun getKotSuiteLocation(): String {
        return kotSuiteLocationField.text
    }

    fun getLibraryLocation(): String {
        return libraryLocationField.text
    }

    fun getStrategy(): String {
        return strategyField.item
    }

}
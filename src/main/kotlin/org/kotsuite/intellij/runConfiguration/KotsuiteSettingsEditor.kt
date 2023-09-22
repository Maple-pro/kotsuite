package org.kotsuite.intellij.runConfiguration

import com.intellij.openapi.options.SettingsEditor
import com.intellij.util.ui.FormBuilder
import org.kotsuite.intellij.config.utils.Utils
import javax.swing.JComponent
import javax.swing.JPanel

class KotsuiteSettingsEditor : SettingsEditor<KotsuiteRunConfiguration>() {
    private var myPanel: JPanel
    private val javaPathField = Utils.createTextFieldWithBrowserButton("java")
    private val kotsuiteLocationField = Utils.createTextFieldWithBrowserButton()
    private val projectPathField = Utils.createTextFieldWithBrowserButton()
    private val modulePathField = Utils.createTextFieldWithBrowserButton()
    private val moduleClassPathField = Utils.createTextField(null)
    private val moduleSourcePathField = Utils.createTextField(null)
    private val includeRulesField = Utils.createTextField(null)
    private val libraryLocationField = Utils.createTextFieldWithBrowserButton()
    private val strategyField = Utils.createComboBox(arrayOf("random", "ga"))
//    private val dependencyField = Utils.createTextField(null)

    init {
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Java home", javaPathField)
            .addLabeledComponent("KotSuite location", kotsuiteLocationField)
            .addLabeledComponent("Project path", projectPathField)
            .addLabeledComponent("Module path", modulePathField)
            .addLabeledComponent("Module classpath", moduleClassPathField)
            .addLabeledComponent("Module source path", moduleSourcePathField)
            .addLabeledComponent("Include rules", includeRulesField)
            .addLabeledComponent("Library location", libraryLocationField)
            .addLabeledComponent("Strategy", strategyField)
//            .addLabeledComponent("Dependency", dependencyField)
            .panel
    }

    override fun resetEditorFrom(configuration: KotsuiteRunConfiguration) {
        javaPathField.text = configuration.getJavaPath()!!
        kotsuiteLocationField.text = configuration.getKotsuiteLocation()!!
        projectPathField.text = configuration.getProjectPath()!!
        modulePathField.text = configuration.getModulePath()!!
        moduleClassPathField.text = configuration.getModuleClassPath()
        moduleSourcePathField.text = configuration.getModuleSourcePath()
        includeRulesField.text = configuration.getIncludeRules()
        libraryLocationField.text = configuration.getLibraryLocation()!!
        strategyField.item = configuration.getStrategy()
//        dependencyField.text  = configuration.getDependency()
    }

    override fun applyEditorTo(configuration: KotsuiteRunConfiguration) {
        configuration.setJavaPath(javaPathField.text)
        configuration.setKotsuiteLocation(kotsuiteLocationField.text)
        configuration.setProjectPath(projectPathField.text)
        configuration.setModulePath(modulePathField.text)
        configuration.setModuleClassPath(moduleClassPathField.text)
        configuration.setModuleSourcePath(moduleSourcePathField.text)
        configuration.setIncludeRules(includeRulesField.text)
        configuration.setLibraryLocation(libraryLocationField.text)
        configuration.setStrategy(strategyField.item)
//        configuration.setDependency(dependencyField.text)
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}
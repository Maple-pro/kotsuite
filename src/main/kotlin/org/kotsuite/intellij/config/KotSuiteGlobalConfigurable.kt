package org.kotsuite.intellij.config

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.options.Configurable
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JComponent

object KotSuiteGlobalConfigurable: Configurable {

    private var kotSuiteGlobalComponent: KotSuiteGlobalComponent? = null

    private val kotSuiteCacheLocation = "${PathManager.getSystemPath()}/kotsuite-cache/"
    val kotSuiteLocation = "$kotSuiteCacheLocation/kotsuite.jar"
    val libraryLocation = "$kotSuiteCacheLocation/libs/"

    override fun createComponent(): JComponent {
        kotSuiteGlobalComponent = KotSuiteGlobalComponent()
        return kotSuiteGlobalComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val kotSuiteGlobalState = KotSuiteGlobalState.getInstance()

        with(kotSuiteGlobalComponent!!) {
            return !(getJavaHome() == kotSuiteGlobalState.javaHome &&
                    getKotSuiteLocation() == kotSuiteGlobalState.kotSuiteLocation &&
                    getLibraryLocation() == kotSuiteGlobalState.libraryLocation &&
                    getStrategy() == kotSuiteGlobalState.strategy)
        }
    }

    override fun apply() {
        val kotSuiteGlobalState = KotSuiteGlobalState.getInstance()

        createCachedDirectories()

        val component = checkNotNull(kotSuiteGlobalComponent)

        if (component.getKotSuiteLocation() != kotSuiteLocation) {
            File(component.getKotSuiteLocation()).copyTo(
                File(kotSuiteLocation),
                true,
            )
        }

        if (component.getLibraryLocation() != libraryLocation) {
            File(component.getLibraryLocation()).copyRecursively(
                File(libraryLocation),
                true,
            )
        }


        with(component) {
            kotSuiteGlobalState.javaHome = getJavaHome()
            kotSuiteGlobalState.strategy = getStrategy()
            kotSuiteGlobalState.kotSuiteLocation = kotSuiteLocation
            kotSuiteGlobalState.libraryLocation = libraryLocation
        }
    }

    override fun getDisplayName(): String {
        return "KotSuite"
    }

    override fun reset() {
        val kotSuiteGlobalState = KotSuiteGlobalState.getInstance()
        val component = checkNotNull(kotSuiteGlobalComponent)
        with(kotSuiteGlobalState) {
            component.javaHomeField.text = javaHome
            component.kotSuiteLocationField.text = kotSuiteLocation
            component.libraryLocationField.text = libraryLocation
            component.strategyField.item = strategy
        }
    }

    override fun disposeUIResources() {
        kotSuiteGlobalComponent = null
    }

    private fun createCachedDirectories() {
        Files.createDirectories(Paths.get(kotSuiteCacheLocation))
        Files.createDirectories(Paths.get(libraryLocation))
    }

}
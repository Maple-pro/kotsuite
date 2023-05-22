package org.kotsuite.intellij.config

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.options.Configurable
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JComponent

object KotSuiteGlobalConfigurable: Configurable {

    private val kotSuiteCacheLocation = "${PathManager.getSystemPath()}/kotsuite-cache/"
    val kotSuiteLocation = "$kotSuiteCacheLocation/kotsuite.jar"
    val libraryLocation = "$kotSuiteCacheLocation/libs/"

    override fun createComponent(): JComponent {
        return KotSuiteGlobalComponent.getPanel()
    }

    override fun isModified(): Boolean {
        val kotSuiteGlobalState = KotSuiteGlobalState.getInstance()

        with(KotSuiteGlobalComponent) {
            return !(getJavaHome() == kotSuiteGlobalState.javaHome &&
                    getKotSuiteLocation() == kotSuiteGlobalState.kotSuiteLocation &&
                    getLibraryLocation() == kotSuiteGlobalState.libraryLocation &&
                    getStrategy() == kotSuiteGlobalState.strategy)
        }
    }

    override fun apply() {
        val kotSuiteGlobalState = KotSuiteGlobalState.getInstance()

        createCachedDirectories()

        if (KotSuiteGlobalComponent.getKotSuiteLocation() != kotSuiteLocation) {
            File(KotSuiteGlobalComponent.getKotSuiteLocation()).copyTo(
                File(kotSuiteLocation),
                true,
            )
        }

        if (KotSuiteGlobalComponent.getLibraryLocation() != libraryLocation) {
            File(KotSuiteGlobalComponent.getLibraryLocation()).copyRecursively(
                File(libraryLocation),
                true,
            )
        }


        with(KotSuiteGlobalComponent) {
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
        with(kotSuiteGlobalState) {
            KotSuiteGlobalComponent.javaHomeField.text = javaHome
            KotSuiteGlobalComponent.kotSuiteLocationField.text = kotSuiteLocation
            KotSuiteGlobalComponent.libraryLocationField.text = libraryLocation
            KotSuiteGlobalComponent.strategyField.item = strategy
        }
    }

    private fun createCachedDirectories() {
        Files.createDirectories(Paths.get(kotSuiteCacheLocation))
        Files.createDirectories(Paths.get(libraryLocation))
    }

}
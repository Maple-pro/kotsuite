package org.kotsuite.intellij.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import java.io.File

@State (
    name = "org.kotsuite.intellij.config.KotSuiteGlobalState",
    storages = [Storage("KotSuiteGlobalSetting.xml")]
)
class KotSuiteGlobalState: PersistentStateComponent<KotSuiteGlobalState> {

    var javaHome = ""
    var kotSuiteLocation = ""
    var libraryLocation = ""
    var strategy = ""

    companion object {
        @JvmStatic
        fun getInstance(): KotSuiteGlobalState {
            return ApplicationManager.getApplication().getService(KotSuiteGlobalState::class.java)
        }
    }

    override fun getState(): KotSuiteGlobalState {
        return this
    }

    override fun loadState(state: KotSuiteGlobalState) {
        XmlSerializerUtil.copyBean(state, this)
        if (!checkKotSuiteExist()) {
            state.kotSuiteLocation = ""
        }
        if (!checkLibraryExist()) {
            state.libraryLocation = ""
        }
    }

    private fun checkKotSuiteExist(): Boolean {
        if (kotSuiteLocation == KotSuiteGlobalConfigurable.kotSuiteLocation) {
            return File(kotSuiteLocation).exists()
        }

        return true
    }

    private fun checkLibraryExist(): Boolean {
        if (libraryLocation == KotSuiteGlobalConfigurable.libraryLocation) {
            return File(libraryLocation).exists() && File(libraryLocation).isDirectory
        }

        return true
    }
}
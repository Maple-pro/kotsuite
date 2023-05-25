package org.kotsuite.intellij.actions

import org.kotsuite.intellij.dialogs.KotStartDialog
import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.wm.ToolWindowManager

class KotAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT)
        val selectedPath = e.getData(PlatformDataKeys.VIRTUAL_FILE)?.path

//        val modules = ModuleManager.getInstance(project!!).modules
        val module = ModuleUtil.findModuleForFile(e.getData(PlatformDataKeys.VIRTUAL_FILE)!!, project!!)

        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("kotsuite")
//        val notifier = IntelliJNotifier.getNotifier(project)

        toolWindow?.show()
        KotStartDialog(project, module!!, selectedPath!!).showAndGet()

    }
}
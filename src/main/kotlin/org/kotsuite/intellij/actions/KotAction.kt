package org.kotsuite.intellij.actions

import org.kotsuite.intellij.dialogs.KotStartDialog
import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.wm.ToolWindowManager

class KotAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT)

        val toolWindowManager = ToolWindowManager.getInstance(project!!)
        val toolWindow = toolWindowManager.getToolWindow("kotsuite")
        val notifier = IntelliJNotifier.getNotifier(project)

        KotStartDialog(project).showAndGet()

    }
}
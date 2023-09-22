package org.kotsuite.intellij.actions

import org.kotsuite.intellij.util.AsyncGUINotifier
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.kotsuite.intellij.dialogs.KotStartDialog

class StopKotAction(
    notifier: AsyncGUINotifier,
    text: String
) : AnAction(text, "", null) {

    private var notifier: AsyncGUINotifier? = null

    init {
        this.notifier = notifier
        templatePresentation.icon = AllIcons.Actions.Suspend
        templatePresentation.hoveredIcon = AllIcons.Actions.Suspend
    }

    override fun actionPerformed(p0: AnActionEvent) {
        if (KotSuiteProcess.process != null) {
            KotSuiteProcess.process!!.destroy()
        }

        notifier?.printOnConsole("\n\n\nKotSuite run has been cancelled.\n")
    }
}
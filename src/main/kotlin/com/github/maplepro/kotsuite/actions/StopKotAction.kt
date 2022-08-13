package com.github.maplepro.kotsuite.actions

import com.github.maplepro.kotsuite.util.AsyncGUINotifier
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

class StopKotAction(notifier: AsyncGUINotifier, text: String) : AnAction(text, "", null) {

    private var notifier: AsyncGUINotifier? = null

    init {
        this.notifier = notifier
        templatePresentation.icon = AllIcons.Actions.Close
        templatePresentation.hoveredIcon = AllIcons.Actions.CloseHovered
    }

    override fun actionPerformed(p0: AnActionEvent) {
        // TODO: Stop KotSuite executor

        notifier?.printOnConsole("\n\n\nKotSuite run has been cancelled.\n")
    }
}
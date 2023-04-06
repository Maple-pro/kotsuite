package org.kotsuite.intellij.toolwindows

import org.kotsuite.intellij.actions.StopKotAction
import org.kotsuite.intellij.util.IntelliJNotifier
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowType
import java.awt.BorderLayout
import javax.swing.JPanel

class KotSuiteToolWindow(project: Project, toolWindow: ToolWindow) {

    private var console: ConsoleViewImpl? = null

    private var content: JPanel = JPanel()

    init {
        toolWindow.title = "KotSuite Console Output"
        toolWindow.setType(ToolWindowType.DOCKED, null)

        val actionManager = ActionManager.getInstance()

        // Create console panel
        console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console as ConsoleViewImpl
        val consolePanel = console!!.component

        val notifier = IntelliJNotifier.registerNotifier(project, "KotSuite Plugin", console!!)

        // Create left-toolbar with stop button
        val buttonGroup = DefaultActionGroup()
        buttonGroup.add(StopKotAction(notifier, "Stop KotSuite"))
        val viewToolbar = actionManager.createActionToolbar("KotSuite.ConsoleToolbar", buttonGroup, false)
        viewToolbar.setTargetComponent(toolWindow.component)
        val toolBarPanel = viewToolbar.component

        content.layout = BorderLayout()
        content.add(toolBarPanel, BorderLayout.WEST)
        content.add(consolePanel, BorderLayout.CENTER)

    }

    fun getContent(): JPanel {
        return content
    }

}
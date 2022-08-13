package com.github.maplepro.kotsuite.toolwindows

import com.github.maplepro.kotsuite.actions.StopKotAction
import com.github.maplepro.kotsuite.util.IntelliJNotifier
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.ui.content.ContentFactory
import icons.KotIcons
import org.jetbrains.annotations.NotNull
import java.awt.BorderLayout
import javax.swing.JPanel

class KotSuiteToolWindowFactory: ToolWindowFactory {

    private var project: Project? = null



    override fun createToolWindowContent(@NotNull project: Project, @NotNull toolWindow: ToolWindow) {

        this.project = project

        // Create tool window
        val kotSuiteToolWindow = KotSuiteToolWindow(project, toolWindow)

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(kotSuiteToolWindow.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
package org.kotsuite.intellij.toolwindows

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.jetbrains.annotations.NotNull

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
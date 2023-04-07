package org.kotsuite.intellij.util

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.SwingUtilities

class IntelliJNotifier(
    project: Project,
    title: String,
    console: ConsoleViewImpl
) : AsyncGUINotifier {
    companion object {

        private var map: MutableMap<Project, IntelliJNotifier> = LinkedHashMap()
        @JvmStatic
        fun getNotifier(project: Project): IntelliJNotifier? {
            return map[project]
        }
        fun registerNotifier(
            project: Project,
            title: String,
            console: ConsoleViewImpl
        ): IntelliJNotifier {

            val n = IntelliJNotifier(project, title, console)
            map[project] = n
            return n
        }
    }

    private var title: String = ""

    private var project: Project? = null

    private var console: ConsoleViewImpl? = null

    @Volatile private var processHandler: OSProcessHandler? = null
    init {
        this.project = project
        this.title = title
        this.console = console

        this.printOnConsole("KotSuite output ...")
    }
    override fun success(message: String) {
        SwingUtilities.invokeLater {
            fun run() {
                Messages.showMessageDialog(project, message, title, Messages.getInformationIcon())
            }
        }
    }
    override fun failed(message: String) {
        SwingUtilities.invokeLater {
            fun run() {
                Messages.showMessageDialog(project, message, title, Messages.getWarningIcon())
            }
        }
    }
    override fun attachProcess(process: Process) {
        if (processHandler != null) {
            detachLastProcess()
        }
        processHandler = OSProcessHandler(process, null)
        console?.attachToProcess(processHandler!!)
        processHandler!!.startNotify()
    }
    override fun detachLastProcess() {
        processHandler?.destroyProcess()
        processHandler = null
    }
    override fun printOnConsole(message: String) {
        console?.print(message, ConsoleViewContentType.NORMAL_OUTPUT)
    }
    override fun clearConsole() {
        console?.clear()
    }
}
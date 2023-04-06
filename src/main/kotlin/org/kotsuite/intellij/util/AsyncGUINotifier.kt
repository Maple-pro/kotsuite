package org.kotsuite.intellij.util

interface AsyncGUINotifier {

    fun success(message: String)

    fun failed(message: String)

    fun attachProcess(process: Process)

    fun detachLastProcess()

    fun printOnConsole(message: String)

    fun clearConsole()

}

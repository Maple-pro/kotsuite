package org.kotsuite.intellij.util

import java.io.BufferedReader
import java.io.InputStreamReader

object LoggerUtils {
    fun logCommandOutput(notifier: IntelliJNotifier, ps: Process) {
        val stdInput = BufferedReader(InputStreamReader(ps.inputStream))
        val stdError = BufferedReader(InputStreamReader(ps.errorStream))

        var s: String? = stdInput.readLine()
        while (s != null) {
            notifier.printOnConsole(s)
            s = stdInput.readLine()
        }

        s = stdError.readLine()
        while (s != null) {
            notifier.printOnConsole("[Error] $s")
            s = stdError.readLine()
        }
    }
}
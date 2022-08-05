package com.github.maplepro.kotsuite.actions

import com.github.maplepro.kotsuite.ConversionException
import com.github.maplepro.kotsuite.logger
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
//import com.intellij.openapi.project.guessProjectDir

class ConvertJavaToKotlinAction : AnAction() {

    companion object {
        private const val CONVERT_JAVA_TO_KOTLIN_ACTION_ID = "ConvertJavaToKotlin"
//        private const val JAVA_EXTENSION = ".java"
//        private const val KOTLIN_EXTENSION = ".kt"
    }

    override fun actionPerformed(e: AnActionEvent) {
//        val project = e.project ?: return
//        val projectBase = project.guessProjectDir()

        try {
            val fileArray = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: emptyArray()
            fileArray.forEach { logger.info("Preparing to convert file: $it") }

            if (fileArray.isEmpty()) {
                return
            }

            val overrideEvent = AnActionEvent(
                e.inputEvent,
                e.dataContext(fileArray),
                e.place,
                e.presentation,
                e.actionManager,
                e.modifiers
            )
            ActionManager.getInstance().getAction(CONVERT_JAVA_TO_KOTLIN_ACTION_ID)?.actionPerformed(overrideEvent)

        } catch (e: ConversionException) {
            if (e.isError) {
                logger.error("Problem running conversion plugin: ${e.message}\n" +
                        "${e.stackTrace.joinToString("\n")}\n" +
                        "----------")
            } else {
                logger.info(e.message, e.cause)
            }
        }
    }
}
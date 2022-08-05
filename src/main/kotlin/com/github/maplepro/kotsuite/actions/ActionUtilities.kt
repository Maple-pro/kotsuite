package com.github.maplepro.kotsuite.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile

fun AnActionEvent.dataContext(fileArray: Array<VirtualFile>): DataContext = DataContext { data ->
    when (data) {
        PlatformDataKeys.VIRTUAL_FILE_ARRAY.name -> fileArray
        else -> dataContext.getData(data)
    }
}
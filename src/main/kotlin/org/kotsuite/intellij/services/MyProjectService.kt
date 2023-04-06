package org.kotsuite.intellij.services

import com.intellij.openapi.project.Project
import org.kotsuite.intellij.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}

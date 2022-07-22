package com.github.maplepro.kotsuite.services

import com.intellij.openapi.project.Project
import com.github.maplepro.kotsuite.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}

package org.kotsuite.intellij.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import org.kotsuite.intellij.MyBundle

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}

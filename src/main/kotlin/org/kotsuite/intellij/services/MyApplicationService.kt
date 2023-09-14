package org.kotsuite.intellij.services

import com.intellij.openapi.components.Service
import org.kotsuite.intellij.MyBundle

@Service
class MyApplicationService {

    init {
        println(MyBundle.message("applicationService"))
    }
}

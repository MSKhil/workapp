package org.project.workapp.feature.main

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.project.workapp.feature.admin.AdminDashboard
import org.project.workapp.feature.employee.EmployeeDashboard
import org.project.workapp.feature.regional.RegionalDashboard

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MainScreenModel() }
        val navigator = LocalNavigator.currentOrThrow

        when (screenModel.userRole) {
            "admin" -> AdminDashboard(screenModel = screenModel, navigator = navigator)
            "regional" -> RegionalDashboard(screenModel = screenModel, navigator = navigator)
            else -> EmployeeDashboard(screenModel = screenModel, navigator = navigator)
        }
    }
}
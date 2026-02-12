package com.sleepguardian.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sleepguardian.features.alarms.ui.AlarmListScreen
import com.sleepguardian.features.settings.ui.SettingsScreen
import com.sleepguardian.features.statistics.ui.StatisticsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "alarms",
        modifier = modifier
    ) {
        composable("alarms") {
            AlarmListScreen(
                onAddAlarm = {
                    navController.navigate("add_alarm")
                }
            )
        }

        composable("add_alarm") {
            AddEditAlarmPlaceholder(
                onBack = { navController.popBackStack() }
            )
        }

        composable("statistics") {
            StatisticsScreen()
        }

        composable("settings") {
            SettingsScreen()
        }
    }
}

@Composable
private fun AddEditAlarmPlaceholder(onBack: () -> Unit) {
    com.sleepguardian.features.alarms.ui.AddEditAlarmScreen(onBack = onBack)
}

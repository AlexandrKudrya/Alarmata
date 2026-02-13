package com.sleepguardian.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sleepguardian.features.alarms.ui.AddEditAlarmScreen
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
                },
                onEditAlarm = { alarmId ->
                    navController.navigate("edit_alarm/$alarmId")
                }
            )
        }

        composable("add_alarm") {
            AddEditAlarmScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit_alarm/{alarmId}",
            arguments = listOf(
                navArgument("alarmId") { type = NavType.LongType }
            )
        ) {
            AddEditAlarmScreen(
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

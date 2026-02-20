package com.sleepguardian.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute(
    val route: String,
    @StringRes val nameResId: Int,
    val icon: ImageVector
)

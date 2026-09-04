package com.habitstakes.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habitstakes.app.ui.create.CreateHabitScreen
import com.habitstakes.app.ui.create.CreateHabitViewModel
import com.habitstakes.app.ui.habit.HabitDetailScreen
import com.habitstakes.app.ui.habit.HabitDetailViewModel
import com.habitstakes.app.ui.home.HomeScreen
import com.habitstakes.app.ui.home.HomeViewModel
import com.habitstakes.app.ui.navigation.AppNavHost
import com.habitstakes.app.ui.profile.ProfileScreen
import com.habitstakes.app.ui.profile.ProfileViewModel
import com.habitstakes.app.ui.theme.HabitStakesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HabitStakesTheme {
                AppNavHost()
            }
        }
    }
}

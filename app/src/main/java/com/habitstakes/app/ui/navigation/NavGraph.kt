package com.habitstakes.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habitstakes.app.ui.create.CreateHabitScreen
import com.habitstakes.app.ui.create.CreateHabitViewModel
import com.habitstakes.app.ui.habit.HabitDetailScreen
import com.habitstakes.app.ui.habit.HabitDetailViewModel
import com.habitstakes.app.ui.home.HomeScreen
import com.habitstakes.app.ui.home.HomeViewModel
import com.habitstakes.app.ui.profile.ProfileScreen
import com.habitstakes.app.ui.profile.ProfileViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "home"
) {
    NavHost(navController, startDestination) {
        composable("home") {
            HomeScreen(
                viewModel = hiltViewModel<HomeViewModel>(),
                onHabitClick = { habit ->
                    navController.navigate("habit/${habit.habit.id}")
                },
                onCreateHabit = {
                    navController.navigate("create")
                }
            )
        }

        composable(
            route = "habit/{habitId}",
            arguments = listOf(androidx.navigation.navArgument("habitId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.getLong() ?: -1L
            HabitDetailScreen(
                habitId = habitId,
                viewModel = hiltViewModel<HabitDetailViewModel>(),
                onBack = { navController.popBackStack() },
                onEdit = { habit ->
                    navController.navigate("create?editId=${habit.id}")
                }
            )
        }

        composable(
            route = "create?editId={editId}",
            arguments = listOf(androidx.navigation.navArgument("editId") { type = androidx.navigation.NavType.LongType; defaultValue = "-1" })
        ) { backStackEntry ->
            val editId = backStackEntry.getLong() ?: -1L
            CreateHabitScreen(
                viewModel = hiltViewModel<CreateHabitViewModel>(),
                onBack = { navController.popBackStack() },
                onHabitCreated = { id ->
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }

        composable("create") {
            CreateHabitScreen(
                viewModel = hiltViewModel<CreateHabitViewModel>(),
                onBack = { navController.popBackStack() },
                onHabitCreated = { id ->
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                viewModel = hiltViewModel<ProfileViewModel>(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    AppNavHost(navController)
}

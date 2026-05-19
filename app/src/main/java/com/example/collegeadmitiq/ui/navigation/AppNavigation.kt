package com.example.collegeadmitiq.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import com.example.collegeadmitiq.ui.advisor.AdvisorScreen
import com.example.collegeadmitiq.ui.gap.GapScreen
import com.example.collegeadmitiq.ui.home.HomeScreen
import com.example.collegeadmitiq.ui.onboarding.OnboardingScreen
import com.example.collegeadmitiq.ui.portfolio.AddActivityScreen
import com.example.collegeadmitiq.ui.portfolio.PortfolioScreen
import com.example.collegeadmitiq.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

// ── Routes ────────────────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Onboarding   : Screen("onboarding")
    object Home         : Screen("home")
    object Portfolio    : Screen("portfolio")
    object AddActivity  : Screen("add_activity")
    object Advisor      : Screen("advisor")
    object GapAnalysis  : Screen("gap_analysis")
    object Settings     : Screen("settings")
}

// ── Navigation Host ───────────────────────────────────────────────────────────
@Composable
fun AppNavigation(repository: CollegeAdmitRepository) {
    val navController  = rememberNavController()
    val scope          = rememberCoroutineScope()
    var startDest      by remember { mutableStateOf<String?>(null) }

    // Check if onboarding is complete
    LaunchedEffect(Unit) {
        scope.launch {
            val profile = repository.getProfileOnce()
            startDest   = if (profile?.isOnboardingComplete == true)
                Screen.Home.route
            else
                Screen.Onboarding.route
        }
    }

    // Wait until we know where to start
    if (startDest == null) return

    NavHost(
        navController    = navController,
        startDestination = startDest!!
    ) {
        // ── Onboarding ────────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Settings ──────────────────────────────────────────────────────────────
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onAddActivity   = {
                    navController.navigate(Screen.AddActivity.route)
                },
                onViewPortfolio = {
                    navController.navigate(Screen.Portfolio.route)
                },
                onViewAdvisor   = {
                    navController.navigate(Screen.Advisor.route)
                },
                onViewGapAnalysis = {
                    navController.navigate(Screen.GapAnalysis.route)
                },
                onSettings      = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // ── Portfolio ─────────────────────────────────────────────────────────
        composable(Screen.Portfolio.route) {
            PortfolioScreen(
                onAddActivity = {
                    navController.navigate(Screen.AddActivity.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Add Activity ──────────────────────────────────────────────────────
        composable(Screen.AddActivity.route) {
            AddActivityScreen(
                onSaved = { navController.popBackStack() },
                onBack  = { navController.popBackStack() }
            )
        }

        // ── AI Advisor ────────────────────────────────────────────────────────
        composable(Screen.Advisor.route) {
            AdvisorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Gap Analysis ──────────────────────────────────────────────────────
        composable(Screen.GapAnalysis.route) {
            GapScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
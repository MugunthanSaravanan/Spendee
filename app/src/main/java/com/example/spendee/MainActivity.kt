package com.example.spendee

import SpendeeTheme
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spendee.core.presentation.navigation.BottomNavigationBar
import com.example.spendee.core.presentation.navigation.generateBottomNavItems
import com.example.spendee.core.presentation.util.AnimatedVisibilityComposable
import com.example.spendee.core.presentation.util.HandleNotificationPermission
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.feature_budget.presentation.add_edit_budget.AddEditBudgetScreen
import com.example.spendee.feature_budget.presentation.budget.BudgetScreen
import com.example.spendee.feature_current_balance.presentation.current_balance.CurrentBalanceScreen
import com.example.spendee.feature_expenses.presentation.add_edit_expense.AddEditExpenseScreen
import com.example.spendee.feature_expenses.presentation.expenses.ExpensesScreen
import com.example.spendee.feature_goals.presentation.add_edit_goal.AddEditGoalScreen
import com.example.spendee.feature_goals.presentation.goals.GoalsScreen
import com.example.spendee.PieChartActivity // Import your PieChartScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if user is logged in
        val isLoggedIn = checkIfLoggedIn()
        if (!isLoggedIn) {
            // Redirect to LoginActivity if not logged in
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish() // Finish MainActivity to prevent it from appearing when the user presses back
            return
        }

        // If logged in, continue to set up the main content
        val intentRoute = intent.getStringExtra("route")
        setContent {
            SpendeeTheme {
                MainScreen(initialRoute = intentRoute, onLogout = ::onLogout)
            }
        }
    }

    // Function to check login status (using SharedPreferences to check if logged in)
    private fun checkIfLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    // Function to log out
    private fun onLogout() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", false)
            apply()
        }
        // Redirect to LoginActivity after logging out
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(initialRoute: String? = null, onLogout: () -> Unit) {
    var hasNotificationPermission by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
    }

    HandleNotificationPermission(onPermissionResult = { isGranted ->
        hasNotificationPermission = isGranted
    })

    val navController = rememberNavController()
    val items = generateBottomNavItems()

    var selectedItem by remember { mutableStateOf(items.first().route) }

    val onNavigate: (String) -> Unit = { route ->
        val itemExists = items.any { it.route == route }

        if (itemExists) {
            selectedItem = route
        }
        navController.navigate(route)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Spendee") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedItem = selectedItem,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AnimatedVisibilityComposable {
                SetupNavHost(
                    navController = navController,
                    initialRoute = initialRoute,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
fun SetupNavHost(
    navController: NavHostController,
    initialRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.CURRENT_BALANCE) {
        composable(Routes.CURRENT_BALANCE) {
            CurrentBalanceScreen(
                onNavigate = { onNavigate(it) },
                onShowMoreClick = {
                    onNavigate(Routes.EXPENSES)
                }
            )
        }
        composable(Routes.EXPENSES) {
            ExpensesScreen(
                onNavigate = { onNavigate(it) },
            )
        }
        composable(Routes.BUDGET) {
            BudgetScreen(
                onNavigate = { onNavigate(it) }
            )
        }
        composable(Routes.GOALS) {
            GoalsScreen(onNavigate = { onNavigate(it) })
        }
        composable(Routes.PIE_CHART) {
            PieChartActivity()  // Navigate to the Pie Chart Screen
        }
        composable(
            route = Routes.ADD_EDIT_EXPENSE + "?expenseId={expenseId}",
            arguments = listOf(navArgument("expenseId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            AddEditExpenseScreen(
                onNavigate = { onNavigate(it) },
                onPopBackStack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.ADD_EDIT_BUDGET + "?isCreated={isCreated}",
            arguments = listOf(navArgument("isCreated") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) {
            AddEditBudgetScreen(
                onNavigate = { onNavigate(it) },
                onPopBackStack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.ADD_EDIT_GOAL + "?goalId={goalId}",
            arguments = listOf(navArgument("goalId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            AddEditGoalScreen(
                onNavigate = { onNavigate(it) },
                onPopBackStack = { navController.popBackStack() }
            )
        }
    }

    initialRoute?.let { route ->
        LaunchedEffect(route) {
            onNavigate(route)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendeeTheme {
        MainScreen(onLogout = {})
    }
}

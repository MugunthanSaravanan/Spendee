package com.example.spendee.feature_current_balance.presentation.current_balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spendee.R
import com.example.spendee.feature_current_balance.presentation.current_balance.components.BalanceAlertDialog
import com.example.spendee.feature_current_balance.presentation.current_balance.components.CurrentBalanceTexts
import com.example.spendee.feature_current_balance.presentation.current_balance.components.LatestExpensesColumn
import com.example.spendee.core.presentation.util.UiEvent
import kotlinx.coroutines.flow.collect

@Composable
fun CurrentBalanceScreen(
    onShowMoreClick: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CurrentBalanceViewModel = hiltViewModel()
) {
    val state = viewModel.viewState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(context.getString(event.message))
                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Display balance amount without currency symbol
                    CurrentBalanceTexts(currentBalance = state.balance.amount.toString())
                    Button(
                        onClick = {
                            viewModel.onEvent(CurrentBalanceEvent.OnSetBalanceClick)
                        }
                    ) {
                        Text(text = stringResource(R.string.set_balance))
                    }
                }
            }
            if (state.isDialogOpen) {
                BalanceAlertDialog(onEvent = viewModel::onEvent, currentAmount = state.currentAmount)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(320.dp) // Set as a static size since animation was removed
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                LatestExpensesColumn(
                    latestExpenses = state.latestExpenses,
                    onShowMoreClick = {
                        onShowMoreClick()
                        viewModel.onEvent(CurrentBalanceEvent.OnShowMoreClick) },
                    onExpenseClick = { expense -> viewModel.onEvent(CurrentBalanceEvent.OnExpenseClick(expense)) },
                    modifier = modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentBalanceScreenPreview() {
    CurrentBalanceScreen(
        onNavigate = {},
        onShowMoreClick = {}
    )
}

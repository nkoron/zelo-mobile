import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.activity.MovementsUiState
import com.example.zelo.activity.MovementsViewModel
import com.example.zelo.components.ZeloSearchBar
import com.example.zelo.network.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIncomes: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovementsViewModel = viewModel(factory = MovementsViewModel.provideFactory(
        LocalContext.current.applicationContext as MyApplication
    ))
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    // Filtrar movimientos según el texto de búsqueda
    val filteredMovements = remember(searchQuery, uiState.movements) {
        if (searchQuery.isBlank()) {
            uiState.movements
        } else {
            uiState.movements.filter { movement ->
                val you = if (movement.receiver.id == uiState.user?.id) {
                    movement.payer
                } else {
                    movement.receiver
                }
                // Verifica si el nombre del usuario o el monto coinciden con la búsqueda
                you?.firstName?.contains(searchQuery, ignoreCase = true) == true ||
                        you?.lastName?.contains(searchQuery, ignoreCase = true) == true ||
                        movement.amount.toString().contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (isTablet) 32.dp else 16.dp)
    ) {
        if (isTablet) {
            TabletLayout(
                uiState = uiState.copy(movements = filteredMovements), // Usa la lista filtrada
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onNavigateToIncomes = onNavigateToIncomes,
                onNavigateToExpenses = onNavigateToExpenses
            )
        } else {
            PhoneLayout(
                uiState = uiState.copy(movements = filteredMovements), // Usa la lista filtrada
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onNavigateToIncomes = onNavigateToIncomes,
                onNavigateToExpenses = onNavigateToExpenses
            )
        }
    }
}


@Composable
fun TabletLayout(
    uiState: MovementsUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToIncomes: () -> Unit,
    onNavigateToExpenses: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
        ) {
            MonthSummaryCard(
                month = stringResource(R.string.last_month),
                income = uiState.totalIncome,
                expenses = uiState.totalExpense,
                onIncomeClick = onNavigateToIncomes,
                onExpensesClick = onNavigateToExpenses
            )
        }

        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
        ) {
            Text(
                stringResource(R.string.recent_activity),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ZeloSearchBar(searchQuery = searchQuery, valueChange = onSearchQueryChange)

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn {
                items(uiState.movements) { payment ->
                    FormatItems(uiState, uiState.movements.indexOf(payment))
                }
            }
        }
    }
}

@Composable
fun PhoneLayout(
    uiState: MovementsUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToIncomes: () -> Unit,
    onNavigateToExpenses: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        MonthSummaryCard(
            month = stringResource(R.string.last_month),
            income = uiState.totalIncome,
            expenses = uiState.totalExpense,
            onIncomeClick = onNavigateToIncomes,
            onExpensesClick = onNavigateToExpenses
        )

        Text(
            stringResource(R.string.recent_activity),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        ZeloSearchBar(searchQuery = searchQuery, valueChange = onSearchQueryChange)

        Spacer(modifier = Modifier.height(24.dp))
        if(uiState.movements.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_recent_transactions),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {

            LazyColumn {
                items(uiState.movements) { payment ->
                    FormatItems(uiState, uiState.movements.indexOf(payment))
                }
            }
        }
    }
}

@Composable
fun FormatItems(uiState: MovementsUiState, it: Int) {
    val payment = uiState.movements[it]
    val me: User?;
    val you: User?;
    val receive: Boolean
    if (payment.receiver.id == uiState.user?.id) {
        receive = true
        me = payment.receiver
        you = payment.payer
    } else {
        receive = false
        me = payment.payer
        you = payment.receiver
    }
    TransactionItem(
        name = "${you?.firstName} ${you?.lastName}",
        description = "${if (receive) stringResource(R.string.transferred) else stringResource(R.string.sent)}: ${payment.amount}",
        time = payment.createdAt,
        showAvatar = true,
        movements = uiState.movements,
        id = it,
        isPayer = !receive
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun SummaryCard(
    title: String,
    amount: Double?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = stringResource(R.string.details),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "$${String.format("%,.2f", amount)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun MonthSummaryCard(
    month: String,
    income: Double?,
    expenses: Double?,
    onIncomeClick: () -> Unit,
    onExpensesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = month,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                title = stringResource(R.string.incomes),
                amount = income,
                onClick = onIncomeClick,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = stringResource(R.string.spent),
                amount = expenses,
                onClick = onExpensesClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


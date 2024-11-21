import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zelo.R
import com.example.zelo.components.ZeloSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIncomes: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Action Buttons
            MonthSummaryCard(
                month = "Agosto",
                income = 21500.00,
                expenses = 50000.00,
                onIncomeClick = onNavigateToIncomes,
                onExpensesClick = onNavigateToExpenses
            )

            Text(
                stringResource(R.string.recent_activity),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            ZeloSearchBar(searchQuery = searchQuery, valueChange = { searchQuery = it })

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn {
                items(5) { index ->
                    when (index) {
                        0 -> TransactionItem(
                            name = "Jose",
                            description = stringResource(R.string.sent) + " $10,000",
                            time = stringResource(R.string.now),
                            showAvatar = true
                        )
                        1 -> TransactionItem(
                            name = "Open 25",
                            description = stringResource(R.string.sent) + " $1000",
                            time = "15m",
                            showLogo = true
                        )
                        2 -> TransactionItem(
                            name = "Fer Galan",
                            description = stringResource(R.string.sent) + " $3,000",
                            time = "6h",
                            showAvatar = true
                        )
                        3 -> TransactionItem(
                            name = "Carlos",
                            description = stringResource(R.string.sent) + " $3,000",
                            time = "2h",
                            showAvatar = true
                        )
                        4 -> TransactionItem(
                            name = "Miguel Cero",
                            description = stringResource(R.string.transferred) + " $3,000",
                            time = "Ahora",
                            showAvatar = true
                        )
                    }
                }
            }
        }
    }
@SuppressLint("DefaultLocale")
@Composable
fun SummaryCard(
    title: String,
    amount: Double,
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
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%,.2f", amount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = stringResource(R.string.details),
                    tint = Color(0xFF6C63FF),
                )
            }
        }
    }
}

@Composable
fun MonthSummaryCard(
    month: String,
    income: Double,
    expenses: Double,
    onIncomeClick: () -> Unit,
    onExpensesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Month Title
        Text(
            text = month,
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Summary Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Income Card
            SummaryCard(
                title = stringResource(R.string.incomes),
                amount = income,
                onClick = onIncomeClick,
                modifier = Modifier.weight(1f)
            )

            // Expenses Card
            SummaryCard(
                title = stringResource(R.string.spent),
                amount = expenses,
                onClick = onExpensesClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
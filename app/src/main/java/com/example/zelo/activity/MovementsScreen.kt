import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

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
                    contentDescription = "Ver detalles",
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
                title = "Ingresado",
                amount = income,
                onClick = onIncomeClick,
                modifier = Modifier.weight(1f)
            )

            // Expenses Card
            SummaryCard(
                title = "Gastado",
                amount = expenses,
                onClick = onExpensesClick,
                modifier = Modifier.weight(1f)
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    val contacts = listOf("Jose", "Martin", "Miguel", "Juan")

    Scaffold(
        topBar = {
            TopAppBar(modifier= Modifier.padding(5.dp), title = { Text("Movimientos") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home")}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Action Buttons
            MonthSummaryCard(
                month = "Agosto",
                income = 21500.00,
                expenses = 50000.00,
                onIncomeClick = { /* Handle income click */ },
                onExpensesClick = { /* Handle expenses click */ }
            )

            Text(
                "Tu actividad reciente",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )


            // Search Bar
            val containerColor = Color(0xFFF3F0F7)
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Buscar") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = { Icon(Icons.Default.FilterList, "Filter") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn {
                items(5) { index ->
                    when (index) {
                        0 -> TransactionItem(
                            name = "Jose",
                            description = "Te transfirió $10,000",
                            time = "Ahora",
                            showAvatar = true
                        )
                        1 -> TransactionItem(
                            name = "Open 25",
                            description = "Pagaste $1000",
                            time = "15m",
                            showLogo = true
                        )
                        2 -> TransactionItem(
                            name = "Fer Galan",
                            description = "Enviaste $3,000",
                            time = "6h",
                            showAvatar = true
                        )
                        3 -> TransactionItem(
                            name = "Carlos GPT",
                            description = "Enviaste $3,000",
                            time = "2h",
                            showAvatar = true
                        )
                        4 -> TransactionItem(
                            name = "Miguel Cero",
                            description = "Enviaste $3,000",
                            time = "Ahora",
                            showAvatar = true
                        )
                    }
                }
            }
        }
    }

    }

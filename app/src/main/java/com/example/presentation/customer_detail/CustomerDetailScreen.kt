package com.example.presentation.customer_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.OilChangeApp
import com.example.R
import com.example.domain.model.CustomerStatus
import com.example.worker.WorkScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    onNavigateUp: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as OilChangeApp
    val viewModel: CustomerDetailViewModel = viewModel(
        factory = CustomerDetailViewModelFactory(
            app.customerRepository,
            app.messageHistoryRepository
        )
    )

    LaunchedEffect(customerId) {
        viewModel.loadCustomerData(customerId)
    }

    val customer by viewModel.customer.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "تفاصيل العميل") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(customerId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (customer == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("جاري التحميل...")
            }
            return@Scaffold
        }

        val c = customer!!
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    if (c.oilImageUri != null) {
                        AsyncImage(
                            model = c.oilImageUri,
                            contentDescription = "Oil Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.img_oil_placeholder_1785863455666),
                            error = painterResource(id = R.drawable.img_oil_placeholder_1785863455666)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_oil_placeholder_1785863455666),
                            contentDescription = "Placeholder",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "بيانات العميل",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "الاسم: ${c.name}")
                    Text(text = "رقم الهاتف: ${c.phone}")
                    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    val dateStr = sdf.format(Date(c.nextReminderDate))
                    Text(text = "موعد التذكير القادم: $dateStr")
                    Text(text = "الحالة: ${c.status.displayName}")
                    if (c.notes.isNotBlank()) {
                        Text(text = "الملاحظات: ${c.notes}")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { /* Handle call */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = "Call")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("اتصال")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { /* Handle manual SMS */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "SMS")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("رسالة يدوية")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val nextDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 // 30 days dummy
                            // Cancel old
                            WorkScheduler.cancelSmsReminder(context, c.id)
                            // Update customer and schedule new
                            viewModel.markOilChanged(c.id, nextDate)
                            WorkScheduler.scheduleSmsReminder(context, c.id, nextDate)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Oil Changed")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تم تغيير الزيت (تحديد موعد جديد)")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "سجل الرسائل",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(history, key = { it.id }) { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val date = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(msg.sendTime))
                        Text(text = "التاريخ: $date", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "الحالة: ${msg.status}",
                            color = if (msg.status == "نجاح") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (msg.failureReason != null) {
                            Text(text = "السبب: ${msg.failureReason}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

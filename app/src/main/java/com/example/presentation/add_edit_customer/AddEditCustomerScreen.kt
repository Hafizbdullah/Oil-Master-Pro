package com.example.presentation.add_edit_customer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.OilChangeApp
import com.example.R
import com.example.worker.WorkScheduler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerScreen(
    customerId: Long,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as OilChangeApp
    val viewModel: AddEditCustomerViewModel = viewModel(
        factory = AddEditCustomerViewModelFactory(app.customerRepository)
    )

    LaunchedEffect(customerId) {
        viewModel.loadCustomer(customerId)
    }

    val uiState by viewModel.uiState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onEvent(AddEditCustomerEvent.OilImageChanged(it.toString())) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id != null) "تعديل بيانات العميل" else "إضافة عميل جديد") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.oilImageUri != null) {
                    AsyncImage(
                        model = uiState.oilImageUri,
                        contentDescription = "Oil Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_oil_placeholder_1785863455666),
                        contentDescription = "Placeholder",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "اضغط لاختيار صورة عبوة الزيت",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onEvent(AddEditCustomerEvent.NameChanged(it)) },
                label = { Text("اسم العميل") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = { viewModel.onEvent(AddEditCustomerEvent.PhoneChanged(it)) },
                label = { Text("رقم الهاتف") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date selection dummy for simplicity, using a simple text field placeholder or a real date picker if we add one.
            // A simple implementation without full material3 DatePicker due to constraints.
            OutlinedTextField(
                value = uiState.nextReminderDate?.toString() ?: "", // Convert timestamp to readable in real app
                onValueChange = { /* Handle date parsing */ 
                    val millis = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 // 30 days later as dummy
                    viewModel.onEvent(AddEditCustomerEvent.DateChanged(millis))
                },
                label = { Text("موعد التذكير (اضغط لتحديد 30 يوم من الان كتجربة)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onEvent(AddEditCustomerEvent.NotesChanged(it)) },
                label = { Text("ملاحظات") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.onEvent(AddEditCustomerEvent.Save { id ->
                        if (uiState.nextReminderDate != null) {
                            WorkScheduler.scheduleSmsReminder(
                                context,
                                id,
                                uiState.nextReminderDate!!
                            )
                        }
                        onNavigateUp()
                    })
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("حفظ بيانات العميل")
            }
        }
    }
}

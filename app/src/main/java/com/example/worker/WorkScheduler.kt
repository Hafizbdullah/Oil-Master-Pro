package com.example.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {
    fun scheduleSmsReminder(context: Context, customerId: Long, reminderTimeMillis: Long) {
        val delay = reminderTimeMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val inputData = Data.Builder()
            .putLong(SmsWorker.KEY_CUSTOMER_ID, customerId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SmsWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("sms_$customerId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "sms_$customerId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelSmsReminder(context: Context, customerId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("sms_$customerId")
    }
}

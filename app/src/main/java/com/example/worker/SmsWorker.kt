package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.OilChangeApp
import com.example.domain.model.CustomerStatus
import com.example.domain.model.MessageHistory

class SmsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val customerId = inputData.getLong(KEY_CUSTOMER_ID, -1L)
        if (customerId == -1L) return Result.failure()

        val app = applicationContext as OilChangeApp
        val customer = app.customerRepository.getCustomerByIdSync(customerId) ?: return Result.failure()
        
        val message = """
            السلام عليكم
            نذكرك بأن موعد تغيير زيت سيارتك قد حان.
            يسعدنا زيارتك في أي وقت.
            شكراً لثقتك بنا.
        """.trimIndent()

        var status = "فشل"
        var failureReason: String? = null

        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                applicationContext.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(customer.phone, null, message, null, null)
            status = "نجاح"
            app.customerRepository.updateCustomerStatus(customer.id, CustomerStatus.SENT)
            showNotification(
                title = "تم إرسال التذكير",
                content = "تم إرسال رسالة تذكير إلى ${customer.name} بنجاح."
            )
        } catch (e: Exception) {
            status = "فشل"
            failureReason = e.localizedMessage
            app.customerRepository.updateCustomerStatus(customer.id, CustomerStatus.LATE)
            showNotification(
                title = "فشل إرسال التذكير",
                content = "تعذر إرسال رسالة إلى ${customer.name}. يرجى المحاولة يدوياً."
            )
        }

        val history = MessageHistory(
            customerId = customer.id,
            sendTime = System.currentTimeMillis(),
            status = status,
            failureReason = failureReason
        )
        app.messageHistoryRepository.insertMessageHistory(history)

        return if (status == "نجاح") Result.success() else Result.failure()
    }

    private fun showNotification(title: String, content: String) {
        val channelId = "sms_notifications"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SMS Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        try {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            // Missing POST_NOTIFICATIONS permission
        }
    }

    companion object {
        const val KEY_CUSTOMER_ID = "CUSTOMER_ID"
    }
}

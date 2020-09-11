package jp.co.cyberagent.dojo2020.util

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import jp.co.cyberagent.dojo2020.R

object TimerNotification {
    val CHANNEL_ID = "CA_TECH_STUDY"

    fun makeBuilder(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }
    }

    fun updateNotification(id: Int, customView: RemoteViews, context: Context, pendingIntent: PendingIntent) {
        with(NotificationManagerCompat.from(context)) {
            val notificationId = id

            val builder = makeBuilder(context, pendingIntent).apply {
                setCustomContentView(customView)
            }

            notify(notificationId, builder.build())
        }
    }

    fun buildCustomView(packageName: String, title: String, time: Long): RemoteViews =
        RemoteViews(packageName, R.layout.custom_notification).apply {
            setTextViewText(R.id.title, title)
            setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            setChronometer(
                R.id.chronometer_on_notification,
                time,
                null,
                true
            )
        }

}
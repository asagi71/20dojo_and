package jp.co.cyberagent.dojo2020.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.remote.auth.FirebaseAuthentication
import jp.co.cyberagent.dojo2020.databinding.ActivityMainBinding
import jp.co.cyberagent.dojo2020.util.TimerNotification
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationController: NavController
    private lateinit var googleSignInClient: GoogleSignInClient
    private val CHANNEL_ID = "CA_TECH_STUDY"

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationController = findNavController(R.id.navigation_host_fragment)

        createNotificationChannel()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navigation_host_fragment).navigateUp()
    }

    override fun onStart() {
        super.onStart()

        if (Firebase.auth.currentUser == null) {
            googleSignInClient = FirebaseAuthentication.getClient(
                this,
                getString(R.string.default_web_client_id)
            )

            showSignInIntent()
        }
    }

    override fun onStop() {
        super.onStop()

        val remoteView = TimerNotification.buildCustomView(
            packageName,
            CHANNEL_ID,
            SystemClock.elapsedRealtime()
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        TimerNotification.updateNotification(
            1,
            remoteView,
            applicationContext,
            pendingIntent
        )

    }

    private fun showSignInIntent() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, FirebaseAuthentication.GOOGLE_AUTH_INTENT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FirebaseAuthentication.GOOGLE_AUTH_INTENT_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken ?: return

                FirebaseAuthentication.signInWithGoogle(idToken)
            } catch (e: ApiException) {

            }
        }
    }

    private fun createNotificationChannel() {
        val name = CHANNEL_ID
        val descriptionText = "for testing"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
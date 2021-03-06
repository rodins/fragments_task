package com.sergeyrodin.fragmentstask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.viewpager2.widget.ViewPager2

private const val EXTRA_FRAGMENT_NUMBER = "extra_fragment_number"
private const val SAVED_FRAGMENTS_COUNT = "save_fragments_count"

class MainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var pager: ViewPager2

    private var fragmentsCount = 0
    private var notificationId = 0
    private val savedNotificationIds = mutableMapOf<Int, MutableList<Int>?>()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val numberFromNotification = intent?.getIntExtra(EXTRA_FRAGMENT_NUMBER, 1) ?: 1
        pager.currentItem = numberFromNotification - 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        pager = findViewById(R.id.pager)
        pagerAdapter = ViewPagerAdapter(this)

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val savedFragmentsCount = sharedPref.getInt(SAVED_FRAGMENTS_COUNT, 1)

        repeat (savedFragmentsCount) {
            addFragment()
        }

        pager.adapter = pagerAdapter
    }

    override fun onStop() {
        super.onStop()

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(SAVED_FRAGMENTS_COUNT, fragmentsCount)
            apply()
        }
    }

    fun addFragment() {
        fragmentsCount++
        pagerAdapter.addFragment(PagerFragment.newInstance(fragmentsCount))
        pager.currentItem = fragmentsCount-1
    }

    fun removeLastFragment() {
        if (fragmentsCount > 1) {
            pagerAdapter.removeLastFragment()

            with(NotificationManagerCompat.from(this)) {
                savedNotificationIds[fragmentsCount]?.forEach {
                    cancel(it)
                }
            }
            savedNotificationIds[fragmentsCount] = null
            fragmentsCount--
        }
    }

    fun createNewNotification(number: Int) {

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(EXTRA_FRAGMENT_NUMBER, number)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder =
            NotificationCompat.Builder(applicationContext, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text, number))
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_avatar
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }

        if (savedNotificationIds[number] == null) {
            savedNotificationIds[number] = mutableListOf(notificationId)
        } else {
            savedNotificationIds[number]?.add(notificationId)
        }

        notificationId++
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                    description = descriptionText
                }

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}

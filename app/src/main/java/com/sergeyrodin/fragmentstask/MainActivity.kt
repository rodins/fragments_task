package com.sergeyrodin.fragmentstask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var pager: ViewPager2

    private var fragmentNumber = 1
    private var notificationId = 0
    private val savedNotificationIds = mutableMapOf<Int, MutableList<Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        pager = findViewById(R.id.pager)

        pagerAdapter = ViewPagerAdapter(this)

        addFragment()

        pager.adapter = pagerAdapter
    }

    fun addFragment() {
        pagerAdapter.addFragment(PagerFragment.newInstance(fragmentNumber++))
        pager.currentItem = fragmentNumber - 1
    }

    fun removeLastFragment() {
        if (fragmentNumber > 1) {
            fragmentNumber--
            if (pager.currentItem == fragmentNumber) {
                pager.currentItem = fragmentNumber - 1
            }
            pagerAdapter.removeLastFragment()

            with(NotificationManagerCompat.from(this)) {
                savedNotificationIds[fragmentNumber]?.forEach {
                    cancel(it)
                }
            }
        }
    }

    fun createNewNotification(number: Int) {
        val builder =
            NotificationCompat.Builder(this, getString(R.string.channel_id))
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
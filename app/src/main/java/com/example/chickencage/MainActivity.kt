package com.example.chickencage

import android.content.ClipData
import android.content.ClipDescription
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.DragEvent
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AnimationUtils
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var lastDroppedX = 0f
    private var lastDroppedY = 0f
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var countTime : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView: ImageView = findViewById(R.id.imageView)
        val cage: View = findViewById(R.id.cage)
        val countDown: TextView = findViewById(R.id.countDownTimer)
        val shake: Animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        mediaPlayer = MediaPlayer.create(this, R.raw.rooster_crowing)

        imageView.setOnLongClickListener { v ->
            val item = ClipData.newPlainText("image", "Draggable Image")
            val shadowBuilder = View.DragShadowBuilder(v)
            v.startDragAndDrop(item, shadowBuilder, v, 0)
            true
        }



        val mainLayout: View = findViewById(R.id.main)
        mainLayout.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DROP -> {
                    val x = event.x
                    val y = event.y
                    imageView.x = x - imageView.width / 2
                    imageView.y = y - imageView.height / 2

                    if (x < cage.x || x > cage.x + cage.width || y < cage.y || y > cage.y + cage.height) {
                        imageView.startAnimation(shake)
                        if (!mediaPlayer.isPlaying) {
                            mediaPlayer.isLooping = true
                            mediaPlayer.start()
                            countDown.setText("Chicken is out!")
                            countTime.cancel()
                        }
                        cage.setBackgroundResource(R.drawable.cage_outline)

                    } else {
                        imageView.clearAnimation()
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                        }
                        cage.setBackgroundResource(R.drawable.cage_outline_open)

                        // to stop doubling of the timer
                        if (this@MainActivity::countTime.isInitialized) {
                            countTime.cancel()
                        }

                        countTime = object : CountDownTimer(30000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                countDown.setText("Second remaining: " + millisUntilFinished / 1000)
                            }
                            override fun onFinish() {
                                val parentWidth = mainLayout.width
                                val parentHeight = mainLayout.height
                                val randomX = Random.nextFloat() * (parentWidth - imageView.width)
                                val randomY = Random.nextFloat() * (parentHeight - imageView.height)
                                imageView.x = randomX
                                imageView.y = randomY
                                cage.setBackgroundResource(R.drawable.cage_outline)
                                imageView.startAnimation(shake)
                                countDown.setText("Chicken is out!")
                                mediaPlayer.start()
                            }
                        }
                        countTime.start()
                    }
                    lastDroppedX = x
                    lastDroppedY = y
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> true
                else -> false
            }
        }
    }
}
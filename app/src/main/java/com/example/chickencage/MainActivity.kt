package com.example.chickencage

import android.content.ClipData
import android.content.ClipDescription
import android.content.IntentSender
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView: ImageView = findViewById(R.id.imageView)
        val cage: View = findViewById(R.id.cage)
        val countDown: TextView = findViewById(R.id.countDownTimer)

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
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        true
                    } else {
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED,
                DragEvent.ACTION_DRAG_LOCATION,
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DROP -> {
                    val x = event.x
                    val y = event.y
                    imageView.x = x - imageView.width / 2
                    imageView.y = y - imageView.height / 2

                    if (x < cage.x || x > cage.x + cage.width || y < cage.y || y > cage.y + cage.height) {
                        val shake: Animation = AnimationUtils.loadAnimation(this, R.anim.shake)
                        imageView.startAnimation(shake)

                        mediaPlayer = MediaPlayer.create(this, R.raw.rooster_crowing)
                        if (!mediaPlayer.isPlaying) {
                            mediaPlayer.isLooping = true
                            mediaPlayer.start()
                        }
                        cage.setBackgroundResource(R.drawable.cage_outline)

                    } else {
                        imageView.clearAnimation()
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                        }
                        cage.setBackgroundResource(R.drawable.cage_outline_open)

                        object: CountDownTimer(30000, 1000) {
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
                            }
                        }.start()
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
package com.example.radialprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*

class MainActivity : AppCompatActivity() {

    //private val seekBar : SeekBar = findViewById(R.id.seekBar)
    //private val rpb1 : RadialProgressBar = findViewById(R.id.radialProgressBar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBarAnimation = ProgressBarAnimation(
            findViewById<RadialProgressBar>(R.id.radialProgressBar),
            0f,
            findViewById<RadialProgressBar>(R.id.radialProgressBar).getProgress().toFloat()
        )
        progressBarAnimation.setInterpolator(AccelerateDecelerateInterpolator())
        progressBarAnimation.start()
        val progressBarAnimation2 = ProgressBarAnimation(
            findViewById<RadialProgressBar>(R.id.radialProgressBar2),
            0f,
            findViewById<RadialProgressBar>(R.id.radialProgressBar2).getProgress().toFloat()
        )
        progressBarAnimation2.setInterpolator(AccelerateDecelerateInterpolator())
        progressBarAnimation2.start()
        val progressBarAnimation3 = ProgressBarAnimation(
            findViewById<RadialProgressBar>(R.id.radialProgressBar3),
            0f,
            findViewById<RadialProgressBar>(R.id.radialProgressBar3).getProgress().toFloat()
        )
        progressBarAnimation3.setInterpolator(AccelerateDecelerateInterpolator())
        progressBarAnimation3.start()

        findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //findViewById<RadialProgressBar>(R.id.radialProgressBar).setProgress(progress)
                progressBarAnimation.setTo(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                progressBarAnimation.cancel()
                progressBarAnimation.setFrom(findViewById<RadialProgressBar>(
                    R.id.radialProgressBar
                ).getProgress().toFloat())
                //System.out.println("Started tracking touch!")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                progressBarAnimation.start()
                //System.out.println("Stopped tracking touch!")
            }
        })
        findViewById<SeekBar>(R.id.seekBar2).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //findViewById<RadialProgressBar>(R.id.radialProgressBar).setProgress(progress)
                progressBarAnimation2.setTo(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                progressBarAnimation2.cancel()
                progressBarAnimation2.setFrom(findViewById<RadialProgressBar>(
                    R.id.radialProgressBar2
                ).getProgress().toFloat())
                //System.out.println("Started tracking touch!")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                progressBarAnimation2.start()
                //System.out.println("Stopped tracking touch!")
            }
        })
        findViewById<SeekBar>(R.id.seekBar3).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //findViewById<RadialProgressBar>(R.id.radialProgressBar).setProgress(progress)
                progressBarAnimation3.setTo(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                progressBarAnimation3.cancel()
                progressBarAnimation3.setFrom(findViewById<RadialProgressBar>(
                    R.id.radialProgressBar3
                ).getProgress().toFloat())
                //System.out.println("Started tracking touch!")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                progressBarAnimation3.start()
                //System.out.println("Stopped tracking touch!")
            }
        })
    }
}

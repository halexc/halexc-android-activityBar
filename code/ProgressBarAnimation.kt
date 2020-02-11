package com.example.radialprogress

import android.os.CountDownTimer
import android.view.animation.*

class ProgressBarAnimation constructor(rpb : RadialProgressBar, from : Float, to : Float){

    private var progressBar : RadialProgressBar
    private var from : Float
    private var to : Float
    private var duration : Long = 500
    private var interpolator : Interpolator = LinearInterpolator()
    private var timer : CountDownTimer

     init {

         this.from = from
         this.to = to

         progressBar = rpb
         timer = object : CountDownTimer(duration, 50L){
             override fun onTick(millisUntilFinished : Long){
                 applyTransformation(interpolator.getInterpolation(1f-(millisUntilFinished.toFloat()/duration.toFloat())))
             }
             override fun onFinish(){
                 applyTransformation(1f)
             }
         }

    }
    fun setDuration(dur: Long){
        duration = dur
        timer = object : CountDownTimer(this.duration, 50L){
            override fun onTick(millisUntilFinished : Long){
                applyTransformation(interpolator.getInterpolation(1f-(millisUntilFinished.toFloat()/duration.toFloat())))
            }
            override fun onFinish(){
                applyTransformation(1f)
            }
        }
    }

    fun start(){
        timer.start()
    }

    fun cancel(){
        timer.cancel()
    }

    fun setInterpolator(ip : Interpolator){
        interpolator = ip
    }

    fun setFrom (from: Float){
        this.from = from
    }

    fun setTo (to: Float){
        this.to = to
    }

    fun setRange(from: Float, to: Float) {
        setFrom(from)
        setTo(to)
    }

    protected fun applyTransformation(interpolatedTime: Float) {

        progressBar.setProgress((from + (to-from) * interpolatedTime).toInt())
        //System.out.println("Applying Transformation: From " + from + " to " + to + " @" + interpolatedTime)
    }
}
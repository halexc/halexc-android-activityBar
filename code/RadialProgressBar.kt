package com.example.radialprogress

import android.content.*
import android.graphics.*
import android.util.*
import android.view.*
import kotlin.math.*

class RadialProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val pathBackground: Path

    private var pathProgress: Path
    private var pathProgressQ2: Path
    private var pathProgressQ3: Path
    private var pathProgressQ4: Path
    private var pathOverrun: Path

    private var init = false

    private var maximum: Int
    private var progress: Int

    private val paintPathQ1 : Paint
    private val paintPathQ2 : Paint
    private val paintPathQ3 : Paint
    private val paintPathQ4 : Paint
    private val paintOverrun : Paint
    private val paintBackground : Paint

    private var shaderGradientQ1 : LinearGradient
    private var shaderGradientQ2 : LinearGradient
    private var shaderGradientQ3 : LinearGradient
    private var shaderGradientQ4 : LinearGradient

    private val colorStart : Int
    private val colorQ1 : Int
    private val colorQ2 : Int
    private val colorQ3 : Int
    private val colorEnd : Int
    private val colorBackground : Int

    private val thickness : Float

    private val dir : Int

    init {
        context.theme.obtainStyledAttributes(attrs,
            R.styleable.RadialProgressBar, 0, 0).apply {
            try {
                colorStart = getInteger(R.styleable.RadialProgressBar_colorStart, (0xff00c000).toInt())
                colorEnd = getInteger(R.styleable.RadialProgressBar_colorEnd, (0xff808080).toInt())
                colorBackground = getInteger(R.styleable.RadialProgressBar_colorBackground, (0x80C0C0C0).toInt())

                colorQ1 = getInteger(R.styleable.RadialProgressBar_colorQ1, colorStart + (colorEnd-colorStart) / 4)
                colorQ2 = getInteger(R.styleable.RadialProgressBar_colorQ2, colorQ1 + (colorEnd-colorQ1) / 3)
                colorQ3 = getInteger(R.styleable.RadialProgressBar_colorQ3, colorQ2 + (colorEnd-colorQ2) / 2)

                maximum = getInteger(R.styleable.RadialProgressBar_progressMaximum, 100)
                progress = getInteger(R.styleable.RadialProgressBar_progressInitial, 0)
                thickness = getFloat(R.styleable.RadialProgressBar_circleWidth, .375f)
                dir = if (getBoolean(R.styleable.RadialProgressBar_CW, true)){
                    1
                } else {
                    -1
                }
            } finally {
                recycle()
            }
        }
        pathBackground = Path()

        pathProgress = Path()
        pathProgressQ2 = Path()
        pathProgressQ3 = Path()
        pathProgressQ4 = Path()
        pathOverrun = Path()

        shaderGradientQ1 = LinearGradient(measuredWidth.toFloat()/2, 0f, measuredWidth.toFloat(), measuredHeight.toFloat()/2, colorStart, colorQ1, Shader.TileMode.CLAMP)
        shaderGradientQ2 = LinearGradient(measuredWidth.toFloat(), measuredHeight.toFloat()/2, measuredWidth.toFloat()/2, measuredHeight.toFloat(), colorQ1, colorQ2, Shader.TileMode.CLAMP)
        shaderGradientQ3 = LinearGradient(measuredWidth.toFloat()/2, measuredHeight.toFloat(), 0f, measuredHeight.toFloat()/2, colorQ2, colorQ3, Shader.TileMode.CLAMP)
        shaderGradientQ4 = LinearGradient(0f, measuredHeight.toFloat()/2, measuredWidth.toFloat()/2, 0f, colorQ3, colorEnd, Shader.TileMode.CLAMP)

        paintPathQ1 = Paint()
        paintPathQ1.style = Paint.Style.FILL
        paintPathQ1.isAntiAlias = true
        paintPathQ1.shader = shaderGradientQ1

        paintPathQ2 = Paint()
        paintPathQ2.style = Paint.Style.FILL
        paintPathQ2.isAntiAlias = true
        paintPathQ2.shader = shaderGradientQ2

        paintPathQ3 = Paint()
        paintPathQ3.style = Paint.Style.FILL
        paintPathQ3.isAntiAlias = true
        paintPathQ3.shader = shaderGradientQ3

        paintPathQ4 = Paint()
        paintPathQ4.style = Paint.Style.FILL
        paintPathQ4.isAntiAlias = true
        paintPathQ4.shader = shaderGradientQ4

        paintOverrun = Paint()
        paintOverrun.color = colorEnd
        paintOverrun.style = Paint.Style.FILL
        paintOverrun.isAntiAlias = true


        paintBackground = Paint()
        paintBackground.isAntiAlias = true
        paintBackground.color = colorBackground


    }

    private fun genPath(){
        pathProgress.reset()
        pathProgressQ2.reset()
        pathProgressQ3.reset()
        pathProgressQ4.reset()
        pathOverrun.reset()

        // Adding paths to later draw the progress bar. Each quarter is drawn individually in order
        // to make it possible to use linear color gradients. Each quarter consists of an inner and
        // an outer line segment. Straight lines at the ends connect these arcs. Should the progress
        // require a quarter to end before the beginning of the next quarter, a semicircle connect-
        // ing the inner and outer arc is added for a nicer finish. In case of overrunning, this
        // semicircle is always drawn.
        val p = 360f * progress.toFloat()/maximum
        val cx = xOnCircle(measuredWidth/2f, measuredWidth/2 * (1-thickness/2), -90f + dir * p)
        val cy = yOnCircle(measuredHeight/2f, measuredHeight/2 * (1-thickness/2), -90f + dir * p)
        pathProgress.addArc(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), -90f, dir * min(p, 90f))
        pathProgress.lineTo(xOnCircle(measuredWidth/2f, measuredWidth/2 * (1-thickness), -90 + dir * min(p, 90f)),
            yOnCircle(measuredHeight/2f, measuredHeight/2 * (1-thickness), -90 + dir * min(p, 90f)))
        if(p <= 90){
            pathProgress.addArc(cx - measuredWidth/2 * thickness/2, cy - measuredHeight/2 * thickness/2,
                cx + measuredWidth/2 * thickness/2, cy + measuredHeight/2 * thickness/2,
                -90f + dir * p, dir * 180f)
        }
        pathProgress.addArc(0f + measuredWidth/2f * thickness, 0f + measuredHeight/2f * thickness,
                            measuredWidth.toFloat() - measuredWidth/2f * thickness, measuredHeight.toFloat() - measuredHeight/2f * thickness,
                         -90f + dir * min(p, 90f), -dir * min(p, 90f))
        pathProgress.lineTo(measuredWidth/2f, 0f)
        pathProgress.addArc(measuredWidth/2 * (1-thickness/2), 0f,
            measuredWidth/2 * (1+thickness/2), measuredHeight/2 * thickness,
            -90f, -dir * 180f)
        if (p > 90){
            pathProgressQ2.addArc(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), -90f + dir * 90, dir * min(p - 90, 90f))
            pathProgressQ2.lineTo(xOnCircle(measuredWidth/2f, measuredWidth/2 * (1-thickness), -90 + dir * min(p, 180f)),
                yOnCircle(measuredHeight/2f, measuredHeight/2 * (1-thickness), -90 + dir * min(p, 180f)))
            if(p <= 180){
                pathProgressQ2.addArc(cx - measuredWidth/2 * thickness/2, cy - measuredHeight/2 * thickness/2,
                    cx + measuredWidth/2 * thickness/2, cy + measuredHeight/2 * thickness/2,
                    -90f + dir * p, dir * 180f)
            }
            pathProgressQ2.addArc(0f + measuredWidth/2f * thickness, 0f + measuredHeight/2f * thickness,
                measuredWidth.toFloat() - measuredWidth/2f * thickness, measuredHeight.toFloat() - measuredHeight/2f * thickness,
                -90f + dir * min(p, 180f), -dir * min(p-90, 90f))
            pathProgressQ2.lineTo(measuredWidth.toFloat() * (1+dir)/2, measuredHeight.toFloat()/2f)
            if (p > 180){
                pathProgressQ3.addArc(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), -90f + dir * 180, dir * min(p - 180, 90f))
                pathProgressQ3.lineTo(xOnCircle(measuredWidth/2f, measuredWidth/2 * (1-thickness), -90 + dir * min(p, 270f)),
                    yOnCircle(measuredHeight/2f, measuredHeight/2 * (1-thickness), -90 + dir * min(p, 270f)))
                if(p <= 270){
                    pathProgressQ3.addArc(cx - measuredWidth/2 * thickness/2, cy - measuredHeight/2 * thickness/2,
                        cx + measuredWidth/2 * thickness/2, cy + measuredHeight/2 * thickness/2,
                        -90f + dir * p, dir * 180f)
                }
                pathProgressQ3.addArc(0f + measuredWidth/2f * thickness, 0f + measuredHeight/2f * thickness,
                    measuredWidth.toFloat() - measuredWidth/2f * thickness, measuredHeight.toFloat() - measuredHeight/2f * thickness,
                    -90f + dir * min(p, 270f), -dir * min(p-180, 90f))
                pathProgressQ3.lineTo(measuredWidth.toFloat()/2f, measuredHeight.toFloat())
                if (p > 270){
                    pathProgressQ4.addArc(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), -90f + dir * 270, dir * min(p - 270, 90f))
                    pathProgressQ4.lineTo(xOnCircle(measuredWidth/2f, measuredWidth/2 * (1-thickness), -90 + dir * min(p, 360f)),
                        yOnCircle(measuredHeight/2f, measuredHeight/2 * (1-thickness), -90 + dir * min(p, 360f)))
                    if(p <= 360){
                        pathProgressQ4.addArc(cx - measuredWidth/2 * thickness/2, cy - measuredHeight/2 * thickness/2,
                            cx + measuredWidth/2 * thickness/2, cy + measuredHeight/2 * thickness/2,
                            -90f + dir * p, dir * 180f)
                    }
                    pathProgressQ4.addArc(0f + measuredWidth/2f * thickness, 0f + measuredHeight/2f * thickness,
                        measuredWidth.toFloat() - measuredWidth/2f * thickness, measuredHeight.toFloat() - measuredHeight/2f * thickness,
                        -90f + dir * min(p, 360f), -dir * min(p-270, 90f))
                    pathProgressQ4.lineTo(measuredWidth.toFloat() * (1-dir)/2, measuredHeight.toFloat()/2f)
                    if (p > 360) {
                        pathOverrun.addArc(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), -90f, dir * (p-360))
                        pathOverrun.lineTo(xOnCircle(measuredWidth/2f, measuredWidth/2 * (1-thickness), -90 + dir * (p-360)),
                            yOnCircle(measuredHeight/2f, measuredHeight/2 * (1-thickness), -90 + dir * (p-360)))

                        pathOverrun.addArc(cx - measuredWidth/2 * thickness/2, cy - measuredHeight/2 * thickness/2,
                            cx + measuredWidth/2 * thickness/2, cy + measuredHeight/2 * thickness/2,
                            -90f + dir * p, dir * 180f)

                        pathOverrun.addArc(0f + measuredWidth/2f * thickness, 0f + measuredHeight/2f * thickness,
                            measuredWidth.toFloat() - measuredWidth/2f * thickness, measuredHeight.toFloat() - measuredHeight/2f * thickness,
                            -90f + dir * (p-360), -dir * (p-360))
                        pathOverrun.lineTo(measuredWidth/2f, 0f)
                    }
                }
            }
        }


        // If the shaders have not yet been set up, do so
        if (!init){
            //paintPathQ1.shader = LinearGradient(0f,0f,measuredWidth.toFloat(), measuredHeight.toFloat(), colorStart, colorEnd, Shader.TileMode.REPEAT)
            if (dir > 0) {
                paintPathQ1.shader = LinearGradient(measuredWidth.toFloat()/2, measuredHeight.toFloat() * (thickness/2f), measuredWidth.toFloat() * (1-thickness/2f), measuredHeight.toFloat()/2, colorStart, colorQ1, Shader.TileMode.CLAMP)
                paintPathQ2.shader = LinearGradient(measuredWidth.toFloat() * (1-thickness/2f), measuredHeight.toFloat()/2, measuredWidth.toFloat()/2, measuredHeight.toFloat() * (1-thickness/2f), colorQ1, colorQ2, Shader.TileMode.CLAMP)
                paintPathQ3.shader = LinearGradient(measuredWidth.toFloat()/2, measuredHeight.toFloat() * (1-thickness/2f), measuredWidth.toFloat() * (thickness/2f), measuredHeight.toFloat()/2, colorQ2, colorQ3, Shader.TileMode.CLAMP)
                paintPathQ4.shader = LinearGradient(measuredWidth.toFloat() * (thickness/2f), measuredHeight.toFloat()/2, measuredWidth.toFloat()/2, measuredHeight.toFloat() * (thickness/2f), colorQ3, colorEnd, Shader.TileMode.CLAMP)
            } else {
                paintPathQ1.shader = LinearGradient(measuredWidth.toFloat()/2, measuredHeight.toFloat() * (thickness/2f), measuredWidth.toFloat() * (thickness/2f), measuredHeight.toFloat()/2, colorStart, colorQ1, Shader.TileMode.CLAMP)
                paintPathQ2.shader = LinearGradient(measuredWidth.toFloat() * (thickness/2f), measuredHeight.toFloat()/2, measuredWidth.toFloat()/2, measuredHeight.toFloat() * (1-thickness/2f), colorQ1, colorQ2, Shader.TileMode.CLAMP)
                paintPathQ3.shader = LinearGradient(measuredWidth.toFloat()/2, measuredHeight.toFloat() * (1-thickness/2f), measuredWidth.toFloat() * (1-thickness/2f), measuredHeight.toFloat()/2, colorQ2, colorQ3, Shader.TileMode.CLAMP)
                paintPathQ4.shader = LinearGradient(measuredWidth.toFloat() * (1-thickness/2f), measuredHeight.toFloat()/2, measuredWidth.toFloat()/2, measuredHeight.toFloat() * (thickness/2f), colorQ3, colorEnd, Shader.TileMode.CLAMP)
            }

            pathBackground.reset()
            pathBackground.addArc(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), 0f, 360f)
            pathBackground.lineTo(measuredWidth.toFloat() * (1-thickness/2), measuredHeight.toFloat()/2)
            pathBackground.addArc(measuredWidth * thickness/2, measuredHeight * thickness/2,
                measuredWidth * (1-thickness/2), measuredHeight * (1-thickness/2), 0f, -360f)
            pathBackground.lineTo(measuredWidth.toFloat()/2, measuredHeight.toFloat())

            init = true
        }



    }

    private fun xOnCircle(centerX: Float, r: Float, angle: Float) : Float{
        return centerX + r * sin(-angle * PI/180 + PI/2).toFloat()
    }
    private fun yOnCircle(centerY: Float, r: Float, angle: Float) : Float{
        return centerY + r * cos(-angle * PI/180 + PI/2).toFloat()
    }

    fun setProgress(progress : Int) {
        this.progress = progress

        invalidate()
        requestLayout()
    }

    fun setMaximum(maximum: Int) {
        this.maximum = maximum

        invalidate()
        requestLayout()
    }

    fun getProgress() : Int {
        return progress
    }

    fun getMaximum() : Int {
        return maximum
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        genPath()
        canvas?.drawPath(pathBackground, paintBackground)

        canvas?.drawPath(pathProgress, paintPathQ1)
        if(progress > maximum/4f)        canvas?.drawPath(pathProgressQ2, paintPathQ2)
        if(progress > maximum/2f)        canvas?.drawPath(pathProgressQ3, paintPathQ3)
        if(progress > maximum * 3f/4)    canvas?.drawPath(pathProgressQ4, paintPathQ4)
        if(progress > maximum)           canvas?.drawPath(pathOverrun, paintOverrun)
    }
}
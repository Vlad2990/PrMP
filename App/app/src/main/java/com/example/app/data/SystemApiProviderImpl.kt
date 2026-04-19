package com.example.app.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.app.domain.interfaces.SystemApiProviderInterface
import kotlin.math.sqrt

class SystemApiProviderImpl(private  val context: Context) : SystemApiProviderInterface,
    SensorEventListener {
    override fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("CalcResult", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    private var sensorManager: SensorManager? = null
    private var onShakeCallback: (() -> Unit)? = null
    private var lastShakeTime: Long = 0

    override fun startShakingListener(onShake: () -> Unit) {
        onShakeCallback = onShake
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun stopShakingListener() {
        sensorManager?.unregisterListener(this)
        onShakeCallback = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val gForce = sqrt((x * x + y * y + z * z).toDouble()) / SensorManager.GRAVITY_EARTH
        if (gForce > 2.5) {
            val now = System.currentTimeMillis()
            if (lastShakeTime + 500 > now) return
            lastShakeTime = now
            onShakeCallback?.invoke()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
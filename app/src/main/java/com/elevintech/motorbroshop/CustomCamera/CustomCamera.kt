package com.elevintech.motorbroshop.CustomCamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.R
import com.wonderkiln.camerakit.CameraView
import kotlinx.android.synthetic.main.activity_custom_camera.*

class CustomCamera : AppCompatActivity() {

    private var cameraView: CameraView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_camera)

        cameraView = camera
    }

    override fun onStart() {
        super.onStart()
        cameraView!!.start()
    }


    override fun onResume() {
        super.onResume()
        cameraView!!.start()

    }

    override fun onPause() {
        super.onPause()
        cameraView!!.stop()
    }

    override fun onStop() {
        super.onStop()
        cameraView!!.stop()
    }
}

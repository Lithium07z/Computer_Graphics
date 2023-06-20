package kr.ac.hallym.prac02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DrawColorCube : AppCompatActivity() {

    private lateinit var mainSurfaceView: MainGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainSurfaceView = MainGLSurfaceView(this)
        setContentView(mainSurfaceView)
    }
}
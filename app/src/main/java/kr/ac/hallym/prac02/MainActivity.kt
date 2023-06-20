package kr.ac.hallym.prac02

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.prac02.databinding.ActivityMainBinding

enum class DrawMode {
    NONE, CUBE, COLORCUBE, LITCUBE, LITCOLORCUBE, TEXCUBE, LITEXCUBE
}

var drawMode = DrawMode.NONE

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            drawMode = DrawMode.CUBE
            startActivity(Intent(this@MainActivity, DrawCube::class.java))
        }

        binding.button2.setOnClickListener {
            drawMode = DrawMode.COLORCUBE
            startActivity(Intent(this@MainActivity, DrawColorCube::class.java))
        }

        binding.button3.setOnClickListener {
            drawMode = DrawMode.LITCUBE
            startActivity(Intent(this@MainActivity, DrawLitCube::class.java))
        }

        binding.button4.setOnClickListener {
            drawMode = DrawMode.LITCOLORCUBE
            startActivity(Intent(this@MainActivity, DrawLitColorCube::class.java))
        }

        binding.button5.setOnClickListener {
            drawMode = DrawMode.TEXCUBE
            startActivity(Intent(this@MainActivity, DrawTexCube::class.java))
        }

        binding.button6.setOnClickListener {
            drawMode = DrawMode.LITEXCUBE
            startActivity(Intent(this@MainActivity, DrawLitTexCube::class.java))
        }
    }
}
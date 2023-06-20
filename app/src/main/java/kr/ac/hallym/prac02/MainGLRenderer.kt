package kr.ac.hallym.prac02

import android.content.Context
import android.opengl.GLES30
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

const val COORDS_PER_VERTEX = 3

var eyePos = floatArrayOf(2.0f, 2.0f, 2.0f)

class MainGLRenderer (val context: Context): GLSurfaceView.Renderer{

    private lateinit var mCube: MyCube
    private lateinit var mColorCube: MyColorCube
    private lateinit var mLitCube: MyLitCube
    private lateinit var mLitColorCube: MyLitColorCube
    private lateinit var myArcball: MyArcball
    private lateinit var mTexCube: MyTexCube
    private lateinit var mLitTexCube: MyLitTexCube

    private var modelMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    private var projectionMatrix = FloatArray(16)
    private var vpMatrix = FloatArray(16)
    private var mvpMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.2f, 0.2f, 0.2f, 1.0f)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(vpMatrix, 0)

        when (drawMode) {
            DrawMode.CUBE, DrawMode.NONE -> mCube = MyCube(context)
            DrawMode.COLORCUBE -> mColorCube = MyColorCube(context)
            DrawMode.LITCUBE -> mLitCube = MyLitCube(context)
            DrawMode.LITCOLORCUBE -> mLitColorCube = MyLitColorCube(context)
            DrawMode.TEXCUBE -> mTexCube = MyTexCube(context)
            DrawMode.LITEXCUBE -> mLitTexCube = MyLitTexCube(context)
        }

        mCube = MyCube(context)
        myArcball = MyArcball()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        myArcball.resize(width, height)

        if (width > height) {
            val ratio = width.toFloat() / height.toFloat()
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0f, 1000f)
        } else {
            val ratio = height.toFloat() / width.toFloat()
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, 0f, 1000f)
        }

        Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], 0f, 0f, 0f, 0f, 1f, 0f)

        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, myArcball.rotationMatrix, 0)

        when (drawMode) {
            DrawMode.CUBE, DrawMode.NONE -> mCube.draw(mvpMatrix)
            DrawMode.COLORCUBE -> mColorCube.draw(mvpMatrix)
            DrawMode.LITCUBE -> mLitCube.draw(mvpMatrix, myArcball.rotationMatrix)
            DrawMode.LITCOLORCUBE -> mLitColorCube.draw(mvpMatrix, myArcball.rotationMatrix)
            DrawMode.TEXCUBE -> mTexCube.draw(mvpMatrix)
            DrawMode.LITEXCUBE -> mLitTexCube.draw(mvpMatrix, myArcball.rotationMatrix)
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> myArcball.start(x, y)
            MotionEvent.ACTION_MOVE -> myArcball.end(x, y)
        }
        return true;
    }
}

fun loadShader(type: Int, filename: String, myContext: Context): Int {

    // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
    return GLES30.glCreateShader(type).also { shader ->

        val inputStream = myContext.assets.open(filename)
        val inputBuffer = ByteArray(inputStream.available())
        inputStream.read(inputBuffer)
        val shaderCode = String(inputBuffer)

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)

        // log the compile error
        val compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled)
        if (compiled.get(0) == 0) {
            GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled)
            if (compiled.get(0) > 1) {
                Log.e("Shader", "$type shader: " + GLES30.glGetShaderInfoLog(shader))
            }
            GLES30.glDeleteShader(shader)
            Log.e("Shader", "$type shader compile error.")
        }
    }
}

fun loadBitmap(filename: String, myContext: Context): Bitmap {
    val manager = myContext.assets
    val inputStream = BufferedInputStream(manager.open(filename))
    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
    return bitmap!!
}
package kr.ac.hallym.prac02

import android.content.Context
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class MyCube(val myContext: Context) {

    private val vertexCoords = floatArrayOf( // in counterclockwise order: 반시계 방향으로
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f
    )

    private val color = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)

    private val drawOrder = shortArrayOf(
        0, 3, 2, 0, 2, 1, // back
        2, 3, 7, 2, 7, 6, // right-side
        1, 2, 6, 1, 6, 5, // bottom
        4, 0, 1, 4, 1, 5, // left-side
        3, 0, 4, 3, 4, 7, // top
        5, 6, 7, 5, 7, 4  // front
    )

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float) | 정점 갯수 * 4바이트
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            // use the device hardware's native byte order | 디바이스 하드웨어의 기본 바이트 순서 사용
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer | 바이트 버퍼로부터 floating pont buffer를 생성
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer | 정점들을 FloatBuffer에 추가
                put(vertexCoords)
                // set the buffer to read the first coordinate | 버퍼가 첫번째 정점을 읽도록 설정
                position(0)
            }
        }

    private val indexBuffer: ShortBuffer =
        // (number of index values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private var mProgram: Int = -1
    private var mColorHandle: Int = -1

    private var mvpMatrixHandle: Int = -1

    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, "uniform_color_vert.glsl", myContext)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, "uniform_color_frag.glsl", myContext)

        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram().also {

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }

        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        //mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(0)

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            0,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "fColor").also {

            GLES30.glUniform4fv(it, 1, color, 0)
        }

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    fun draw(mvpMatrix: FloatArray) {

        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}
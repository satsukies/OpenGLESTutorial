package net.ddns.satsukies.openglestutorial;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by satsukies on 2016/12/08.
 */

public class GameRenderer implements GLSurfaceView.Renderer {
    public static final String sVertexShaderSource =
            "uniform mat4 vpMatrix;" +
                    "uniform mat4 wMatrix;" +
                    "attribute vec3 position;" +
                    "void main() {" +
                    "   gl_Position = vpMatrix * wMatrix * vec4(position, 1.0);" +
                    "}";

    public static final String sFragmentShaderSource =
            "precision mediump float;" +
                    "void main() {" +
                    "   gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                    "}";

    public static final String tVertexShaderCode =
            "attribute vec4 a_Position;" +
                    "attribute vec4 a_UV;" +
                    "varying vec2 v_UV;" +
                    "void main() {" +
                    "   gl_Position = a_Position;" +
                    "   v_UV = a_UV;" +
                    "}";

    public static final String tFragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 v_UV;" +
                    "uniform sampler2D u_Tex;" +
                    "void main() {" +
                    "   gl_FragColor = texture2D(u_Tex, v_UV);" +
                    "}";

    private Context mContext;

    private int mProgramId;
    private int mProgramId2;
    private float[] mViewAndProjectionMatrix = new float[16];
    private long mFrameCount = 0;

    private FloatBuffer vertexBuffer0;
    private FloatBuffer vertexBuffer1;
    private FloatBuffer uvBuffer;

    private int textureId;

    public GameRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //clear color with black
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        //compile shader
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, sVertexShaderSource);
        GLES20.glCompileShader(vertexShader);

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, sFragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);

        int vertexShader2 = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader2, tVertexShaderCode);
        GLES20.glCompileShader(vertexShader2);

        int fragmentShader2 = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader2, tFragmentShaderCode);
        GLES20.glCompileShader(fragmentShader2);

        //linking shader to program
        mProgramId = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramId, vertexShader);
        GLES20.glAttachShader(mProgramId, fragmentShader);
        GLES20.glLinkProgram(mProgramId);
        GLES20.glUseProgram(mProgramId);

        mProgramId2 = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramId2, vertexShader2);
        GLES20.glAttachShader(mProgramId2, fragmentShader2);
        GLES20.glLinkProgram(mProgramId2);
        GLES20.glUseProgram(mProgramId2);

        //enable texture
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        //generate texture from bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sample);
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        textureId = textureIds[0];

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        //texture filter
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        float[] uvBuf = new float[] {
                0f, 0f, //left-top
                0f, 1f, //left-bottom
                1f, 0f, //right-top
                1f, 1f //right-bottom
        };

        uvBuffer = BufferUtil.convert(uvBuf);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //set viewport with full-size
        GLES20.glViewport(0, 0, width, height);

        //generate matrix(view, projection)
        float[] projectionMatrix = new float[16];
        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.orthoM(projectionMatrix, 0, -width / 2f, width / 2f, -height / 2, height / 2, 0f, 2f);

        //integrate matrix
        Matrix.multiplyMM(mViewAndProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        //vertex buffer (for texture)
        vertexBuffer0 = makeVertexBuffer(width, height, 50 ,50, 200 ,200);
        vertexBuffer1 = makeVertexBuffer(width, height, 50 ,300, 300, 300);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //textures
        int posHandle = GLES20.glGetAttribLocation(mProgramId2, "a_Position");
        int uvHandle = GLES20.glGetAttribLocation(mProgramId2, "a_UV");
        int texHandle = GLES20.glGetUniformLocation(mProgramId2, "u_Tex");
        GLES20.glEnableVertexAttribArray(posHandle);
        GLES20.glEnableVertexAttribArray(uvHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(texHandle, 0);

        GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //square size
        float length = 100f;
        float left = -length / 2f;
        float right = length / 2f;
        float top = -length / 2f;
        float bottom = length / 2f;

        float[] vertices = new float[] {
                left, top, 0f,
                left, bottom, 0f,
                right, bottom, 0f,

                right, bottom, 0f,
                right, top, 0f,
                left, top, 0f
        };

        short[] indices = new short[] {
                0, 1, 2,
                3, 4, 5
        };

        FloatBuffer vertexBuffer = BufferUtil.convert(vertices);
        ShortBuffer indexBuffer = BufferUtil.convert(indices);

        float[] worldMatrix = new float[16];
        Matrix.setIdentityM(worldMatrix, 0);
        Matrix.rotateM(worldMatrix, 0, (float)mFrameCount / 2f, 0, 0, 1);

        int attLoc1 = GLES20.glGetAttribLocation(mProgramId, "position");
        int uniLoc1 = GLES20.glGetUniformLocation(mProgramId, "vpMatrix");
        int uniLoc2 = GLES20.glGetUniformLocation(mProgramId, "wMatrix");
        GLES20.glEnableVertexAttribArray(attLoc1);

        GLES20.glVertexAttribPointer(attLoc1, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glUniformMatrix4fv(uniLoc1, 1, false, mViewAndProjectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uniLoc2, 1, false, worldMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisableVertexAttribArray(attLoc1);

        mFrameCount++;
    }

    public FloatBuffer makeVertexBuffer(int windowWidth, int windowHeight, int x, int y, int w, int h) {
        float left = ((float)x / (float)windowWidth) * 2f - 1f;
        float top = ((float)y / (float)windowHeight) * 2f - 1f;
        float right = ((float)(x + w) / (float)windowWidth) * 2f - 1f;
        float bottom = ((float)(y + h) / (float)windowHeight) * 2f - 1f;
        top = -top;
        bottom = -bottom;

        float[] vertexes = {
                left, top, 0f,
                left, bottom, 0f,
                right, top, 0f,
                right, bottom, 0f
        };

        return BufferUtil.convert(vertexes);
    }
}

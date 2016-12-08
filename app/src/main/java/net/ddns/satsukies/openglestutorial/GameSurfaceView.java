package net.ddns.satsukies.openglestutorial;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by satsukies on 2016/12/08.
 */

public class GameSurfaceView extends GLSurfaceView {
    private static final int OPENGL_ES_VERSION = 2;

    public GameSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(new GameRenderer());
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}

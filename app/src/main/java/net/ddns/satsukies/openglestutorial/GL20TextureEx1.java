package net.ddns.satsukies.openglestutorial;

/**
 * Created by satsukies on 2016/12/08.
 */

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

//テクスチャの表示
public class GL20TextureEx1 extends Activity {
    private GLSurfaceView glView;

    //アクティビティ生成時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //GLサーフェイスビュー
        glView=new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(new GLRenderer(this));
        setContentView(glView);
    }

    //アクティビティレジューム時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
    }

    //アクティビティポーズ時に呼ばれる
    @Override
    public void onPause() {
        super.onPause();
        glView.onPause();
    }
}
package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class WorldRenderer implements Disposable {

    private static final String TAG = WorldRenderer.class.getName();

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private WorldController worldController;

    public WorldRenderer(WorldController worldController){
        this.worldController = worldController;
        init();
    }

    private void init(){

    }

    public void render(){

    }

    public void resize(int width, int height){

    }

    @Override
    public void dispose() {

    }
}

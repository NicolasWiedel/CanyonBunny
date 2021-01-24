package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import de.javadevblog.canyonbunny.util.CameraHelper;
import de.javadevblog.canyonbunny.util.Constants;

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();

    public CameraHelper cameraHelper;

    public Level level;
    public int lives;
    public int score;

    public WorldController(){
        init();
    }

    private void initiLevel(){
        score = 0;
        level = new Level(Constants.LEVEL_01);
    }

    private void init(){
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        initiLevel();
    }

    public void update(float deltaTime){
        handleDebugIput(deltaTime);
        cameraHelper.update(deltaTime);
    }

    private void handleDebugIput(float deltaTime){
        if(Gdx.app.getType() != Application.ApplicationType.Desktop){
            return;
        }

        // Camera Kontrolle (move)
        float camMoveSpeed = 5 * deltaTime;
        float camMoveSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) moveCamera(0, camMoveSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveCamera(0, -camMoveSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            cameraHelper.setPosition(0, 0);
            cameraHelper.setZoom(1.0f);
        }

        // Camera Kontrolle (zoom)
        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Input.Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.SLASH)) cameraHelper.setZoom(1);
    }

    private void moveCamera (float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }
    @Override
    public boolean keyUp(int keycode) {
        // Gameworld reset
        if(keycode == Input.Keys.R){
            init();
            Gdx.app.debug(TAG, "Gameworld zurücgesetzt!");
        }
        return false;
    }
}

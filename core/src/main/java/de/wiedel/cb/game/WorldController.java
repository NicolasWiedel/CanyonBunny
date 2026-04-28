package de.wiedel.cb.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.bcel.internal.Const;
import de.wiedel.cb.utils.CameraHelper;
import de.wiedel.cb.utils.Constants;

public class WorldController extends InputAdapter {

    /** eindeutiger Bezeichner der Klasse */
    public static final String TAG = WorldController.class.getSimpleName();

    public Level level;
    public int lives;
    public int score;

    /** Debug Kamera */
    public CameraHelper cameraHelper;

    /** Konstruktor */
    public WorldController(){
        init();
    }

    /** Initialisierungsmethode */
    private void init(){
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        initLevel();
    }

    private void initLevel(){
        score = 0;
        level = new Level(Constants.LEVEL_01);
    }

    /** Spielelogik */
    public void update(float delta){
        handleDebugInput(delta);
        cameraHelper.update(delta);
    }

    /** InputAdapter Methoden */
    @Override
    public boolean keyUp(int keycode) {
        // Spielewelt zurücksetzen
        if (keycode == Input.Keys.R){
            init();
            Gdx.app.debug(TAG, "Game world zurückgesetzt!");
        }
        return false;
    }

    private Pixmap createProceduralPixmap(int width, int height){
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.fill();
        pixmap.setColor(1, 1, 0, 1f);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);
        return pixmap;
    }

    private void handleDebugInput(float delta){
        if (Gdx.app.getType() != Application.ApplicationType.Desktop){
            return;
        }

        // Kamera Kontrolle (bewegen)
        float camMoveSpeed = 5f * delta;
        float camMoveSpeedAccelerator = 5f;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            camMoveSpeed *= camMoveSpeedAccelerator;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            moveCamera(-camMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            moveCamera(camMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            moveCamera(0, camMoveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            moveCamera(0, -camMoveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)){
            cameraHelper.setPosition(0, 0);
        }

        // Kamera Kontrolle (Zoom)
        float camZoomSpeed = 1f * delta;
        float camZoomSpeedAccelerator = 5f;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            camZoomSpeed *= camZoomSpeedAccelerator;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.COMMA)){
            cameraHelper.addZoom(camZoomSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PERIOD)){
            cameraHelper.addZoom(-camZoomSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
            cameraHelper.setZoom(1f);
        }
    }
    private void moveCamera(float x, float y){
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }
}

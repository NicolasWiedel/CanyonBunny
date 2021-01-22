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

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();

    public Sprite[] testSprites;
    public int selectedSprite;

    public CameraHelper cameraHelper;

    public WorldController(){
        init();
    }

    private void init(){
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        initTestObjects();
    }

    private void initTestObjects(){
        // array mit 5 sprites
        testSprites = new Sprite[5];
        // kreiert eienListe von TextureRegions
        Array<TextureRegion> regions = new Array<>();
        regions.add(Assets.INSTANCE.bunny.head);
        regions.add(Assets.INSTANCE.feather.feather);
        regions.add(Assets.INSTANCE.goldCoin.goldCoin);
        // kreiert ein neues sprite mit der erstellten Texture
        for (int i = 0; i < testSprites.length; i++) {
            Sprite spr = new Sprite(regions.random());
            // sprite size 1m x 1m in game world
            spr.setSize(1, 1);
            // Sezt origin to sprite's center
            spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
            // kalkuliert zufällige Position de ´Srietes
            float randomX = MathUtils.random(-2.0f, 2.0f);
            float randomY = MathUtils.random(-2.0f, 2.0f);
            spr.setPosition(randomX, randomY);
            // fügt Sprite zum Array hinzu
            testSprites[i] = spr;
        }
        // selektiert das erst Spriteim Array
        selectedSprite = 0;
    }

    /*private Pixmap createProceduralPixmap (int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        // füllt das Quadrat mit Farbe 50% opacity
        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.fill();
        // zeichnet ein gelbes X in das Quadrat
        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        // zeichnet eine cyan farbene Umrandung
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);
        return pixmap;
    }*/

    public void update(float deltaTime){
        handleDebugIput(deltaTime);
        updateTestObjects(deltaTime);
        cameraHelper.update(deltaTime);
    }

    private void handleDebugIput(float deltaTime){
        if(Gdx.app.getType() != Application.ApplicationType.Desktop){
            return;
        }

        // Sprite Kontrolle
        float sprMoveSpeed = 5 * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            moveSelectedSprite(-sprMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            moveSelectedSprite(sprMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            moveSelectedSprite(0, sprMoveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            moveSelectedSprite(0, -sprMoveSpeed);
        }

        // Camera Kontrolle (move)
        float camMoveSpeed = 5 * deltaTime;
        float camMoveSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) moveCamera(0, camMoveSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveCamera(0, -camMoveSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);

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

    private void moveSelectedSprite(float x, float y){
        testSprites[selectedSprite].translate(x, y);
    }

    private void updateTestObjects(float deltaTime) {
        // Rotation des ausgewählten Sprite
        float rotation = testSprites[selectedSprite].getRotation();
        // dreht das Sprite um 90°
        rotation += 90 * deltaTime;
        // dret um 360°
        rotation %= 360;
        // setzt die neue Rotation
        testSprites[selectedSprite].setRotation(rotation);
    }

    @Override
    public boolean keyUp(int keycode) {
        // Gameworld reset
        if(keycode == Input.Keys.R){
            init();
            Gdx.app.debug(TAG, "Gameworld zurücgesetzt!");
        }
        // nächstes Sprite auswählen!
        else if (keycode == Input.Keys.SPACE){
            selectedSprite = (selectedSprite +1) % testSprites.length;
            // Camera auf das nächste selektiert Sprite ausrichten
            if(cameraHelper.hasTarget()){
                cameraHelper.setTarget(testSprites[selectedSprite]);
            }
            Gdx.app.debug(TAG, "Sprite #" + selectedSprite + " ausgewaehlt!");
        }
        // folge der Camera
        else if (keycode == Input.Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : testSprites[selectedSprite]);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }
        return false;
    }
}

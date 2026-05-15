package de.wiedel.cb.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import de.wiedel.cb.game.objects.BunnyHead;
import de.wiedel.cb.game.objects.BunnyHead.JUMP_STATE;
import de.wiedel.cb.game.objects.Feather;
import de.wiedel.cb.game.objects.GoldCoin;
import de.wiedel.cb.game.objects.Rock;
import de.wiedel.cb.utils.CameraHelper;
import de.wiedel.cb.utils.Constants;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;

public class WorldController extends InputAdapter {

    /** eindeutiger Bezeichner der Klasse */
    public static final String TAG = WorldController.class.getSimpleName();

    public Level level;
    public int lives;
    public int score;

    /** Vierecke für die Kollisionserkennung */
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

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
        level.update(delta);
        testCollision();
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

    /** Kollisionsmethoden */
    private void onCollisionBunnyHeadWithRoch(Rock rock){
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y +rock.bounds.height));
        if(heightDifference > 0.25f){
            boolean hitRightEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitRightEdge){
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            } else {
                bunnyHead.position.x = rock.position.x - rock.bounds.width;
            }
            return;
        }
        switch (bunnyHead.jumpState) {
            case GROUNDED:
                break;
            case FALLING:
            case JUMP_FALLING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                bunnyHead.jumpState = JUMP_STATE.GROUNDED;
                break;
            case JUMP_RISING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                break;

        }
    }
    private void onCollisionBunnyHeadWithGoldCoin(GoldCoin goldCoin){
        goldCoin.collected = true;
        score += goldCoin.getScore();
        Gdx.app.log(TAG, "GoldCoin aufgesammelt!");
    }
    private void onCollisionBunnyHeadWithFeather(Feather feather){
        feather.collected = true;
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerUp(true);
        Gdx.app.log(TAG, "Feder aufgesammelt!");
    }
    private void testCollision(){
        r1.set(level.bunnyHead.position.x,
            level.bunnyHead.position.y,
            level.bunnyHead.bounds.width,
            level.bunnyHead.bounds.height);
        //Kollision zwischen Bunny und Rock
        for (Rock rock : level.rocks){
            r2.set(rock.position.x, rock.position.y,
                rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2)){
                continue;
            }
            onCollisionBunnyHeadWithRoch(rock);
        }
        //Kollision zwischen Bunny und GoldCoin
        for (GoldCoin goldCoin : level.goldCoins){
            if (goldCoin.collected){
                continue;
            }
            r2.set(goldCoin.position.x, goldCoin.position.y,
                goldCoin.bounds.width, goldCoin.bounds.height);
            if (!r1.overlaps(r2)){
                continue;
            }
            onCollisionBunnyHeadWithGoldCoin(goldCoin);
            break;
        }
        //Kollision zwischen Bunny und Feather
        for (Feather feather : level.feathers){
            if (feather.collected){
                continue;
            }
            r1.set(feather.position.x, feather.position.y,
                feather.bounds.width, feather.bounds.height);
            if (!r1.overlaps(r2)){
                continue;
            }
            onCollisionBunnyHeadWithFeather(feather);
            break;
        }
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

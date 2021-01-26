package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import de.javadevblog.canyonbunny.game.objects.BunnyHead;
import de.javadevblog.canyonbunny.game.objects.Feather;
import de.javadevblog.canyonbunny.game.objects.GoldCoin;
import de.javadevblog.canyonbunny.game.objects.Rock;
import de.javadevblog.canyonbunny.util.CameraHelper;
import de.javadevblog.canyonbunny.util.Constants;

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();

    public CameraHelper cameraHelper;

    public Level level;
    public int lives;
    public int score;

    // Bounding Boxen zur Kollisionserkennung
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

    public WorldController(){
        init();
    }

    private void initiLevel(){
        score = 0;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead);
    }

    private void init(){
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        initiLevel();
    }

    public void update(float deltaTime){
        handleDebugIput(deltaTime);
        level.update(deltaTime);
        testCollision();
        cameraHelper.update(deltaTime);
    }

    private void handleDebugIput(float deltaTime){
        if(Gdx.app.getType() != Application.ApplicationType.Desktop){
            return;
        }

        if(!cameraHelper.hasTarget(level.bunnyHead)){
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

    private void onCollisionBunnyHeadwithRock(Rock rock){
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifferencce = Math.abs(bunnyHead.position.y -
                (rock.position.y + rock.bounds.height));
        if (heightDifferencce > 0.25f){
            boolean hitRightEdge = bunnyHead.position.x > (rock.position.y + rock.bounds.width / 2f);
           if(hitRightEdge){
               bunnyHead.position.x = rock.position.x + rock.bounds.width;
           }
           else {
               bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
           }
           return;
        }

        switch (bunnyHead.jumpState){
            case GROUNDED:
                break;
            case FALLING:
            case JUMP_FALLIMG:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height  + bunnyHead.origin.y;
                bunnyHead.jumpState = BunnyHead.JumpState.GROUNDED;
                break;
            case JUMP_RISING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                break;
        }
    }

    private void onCollisionBunnyWithGoldCoin(GoldCoin goldCoin){
        goldCoin.collected = true;
        score += goldCoin.getScore();
        Gdx.app.log(TAG, "GoldCoin collected!");
    }

    private void onCollisionBunnyWithFeather(Feather feather){
        feather.collected = true;
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerup(true);
        Gdx.app.log(TAG, "Feather collected!!");
    }

    private void testCollision(){
        r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y,
                level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);

        // Kollision BunnyHead mit Rock
        for (Rock rock : level.rocks){
            r2.set(rock.position.x, rock.position.y,
                    rock.bounds.width, rock.bounds.height);
            if(!r1.overlaps(r2)){
                continue;
            }
            onCollisionBunnyHeadwithRock(rock);
        }

        // Kollision BunnyHead mit GoldCoins
        for (GoldCoin goldCoin : level.goldCoins){
            if (goldCoin.collected){
                continue;
            }
            r2.set(goldCoin.position.x, goldCoin.position.y,
                    goldCoin.bounds.width, goldCoin.bounds.height);
            if (!r1.overlaps(r2)){
                continue;
            }
            onCollisionBunnyWithGoldCoin(goldCoin);
            break;
        }

        // Kollision BunnyHead mit Feather
        for (Feather feather : level.feathers){
            if (feather.collected){
                continue;
            }
            r2 .set(feather.position.x, feather.position.y,
                    feather.bounds.width, feather.bounds.height);
            if (!r1.overlaps(r2)){
                continue;
            }
            onCollisionBunnyWithFeather(feather);
            break;
        }
    }
}

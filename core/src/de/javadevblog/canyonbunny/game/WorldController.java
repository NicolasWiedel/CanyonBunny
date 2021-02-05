package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import de.javadevblog.canyonbunny.game.objects.BunnyHead;
import de.javadevblog.canyonbunny.game.objects.Feather;
import de.javadevblog.canyonbunny.game.objects.GoldCoin;
import de.javadevblog.canyonbunny.game.objects.Rock;
import de.javadevblog.canyonbunny.screens.DirectedGame;
import de.javadevblog.canyonbunny.screens.MenuScreen;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransition;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransitionSlide;
import de.javadevblog.canyonbunny.util.AudioManager;
import de.javadevblog.canyonbunny.util.CameraHelper;
import de.javadevblog.canyonbunny.util.Constants;

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();
    // Verzögerung nach Game4 Over
    public static final float TIME_DELAY_GAME_OVER = 3;

    private DirectedGame game;

    public CameraHelper cameraHelper;

    public float livesVisual;
    public float scoreVisual;

    public Level level;
    public int lives;
    public int score;

    // Bounding Boxen zur Kollisionserkennung
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

    private float timeLeftGameOverDelay;

    public WorldController(DirectedGame game){
        this.game = game;
        init();
    }

    private void initiLevel(){
        score = 0;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead);
    }

    private void init(){
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        livesVisual = lives;
        timeLeftGameOverDelay = 0;
        initiLevel();
    }

    public void update(float deltaTime){
        handleDebugIput(deltaTime);
        if(isGameOver()){
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0){
                backToMenu();
            }
        }
        else {
            handleInpuGame(deltaTime);
        }

        level.update(deltaTime);
        testCollision();
        cameraHelper.update(deltaTime);
        if(!isGameOver() && isPlayerInWater()){
            AudioManager.INSTANCE.play(Assets.INSTANCE.sounds.liveLost);
            lives--;
            if(isGameOver()){
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            } else {
                initiLevel();
            }
        }
        level.mountains.updateScrollPosition(cameraHelper.getPosition());
        if(livesVisual > lives){
            livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
        }
        if (scoreVisual < score){
            scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);
        }
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
        // toggle Kamera
        else if (keycode == Input.Keys.ENTER){
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }
        // zurück ins Menu
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
            backToMenu();
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
        AudioManager.INSTANCE.play(Assets.INSTANCE.sounds.pickupCoin);
        score += goldCoin.getScore();
        Gdx.app.log(TAG, "GoldCoin collected!");
    }

    private void onCollisionBunnyWithFeather(Feather feather){
        feather.collected = true;
        AudioManager.INSTANCE.play(Assets.INSTANCE.sounds.pickupFeather);
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

    private void handleInpuGame(float deltaTime){
        if (cameraHelper.hasTarget(level.bunnyHead)){
            // Spieler Bewegung
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
               level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
            }
            else {
                // für nicht desktop
                if (Gdx.app.getType() != Application.ApplicationType.Desktop){
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
            }

            // Bunny springt
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                level.bunnyHead.setJumping(true);
            } else {
                level.bunnyHead.setJumping(false);
            }
        }
    }

    public boolean isGameOver(){
        return lives < 0;

    }

    public boolean isPlayerInWater(){
        return level.bunnyHead.position.y < -5;
    }

    private void backToMenu(){
// switch to menu screen
        ScreenTransition transition = ScreenTransitionSlide.init(0.75f,
                ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);
        game.setScreen(new MenuScreen(game), transition);
    }
}

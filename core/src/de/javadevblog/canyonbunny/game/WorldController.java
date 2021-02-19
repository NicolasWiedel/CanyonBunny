package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import de.javadevblog.canyonbunny.game.objects.*;
import de.javadevblog.canyonbunny.screens.DirectedGame;
import de.javadevblog.canyonbunny.screens.MenuScreen;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransition;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransitionSlide;
import de.javadevblog.canyonbunny.util.AudioManager;
import de.javadevblog.canyonbunny.util.CameraHelper;
import de.javadevblog.canyonbunny.util.Constants;

public class WorldController extends InputAdapter implements Disposable {

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

    private boolean goalReached;
    public World b2dWorld;

    public WorldController(DirectedGame game){
        this.game = game;
        init();
    }

    private void initLevel(){
        score = 0;
        scoreVisual = score;
        goalReached = false;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead);
        initPhysics();
    }

    private void initPhysics(){
        if(b2dWorld != null){
            b2dWorld.dispose();
        }

        b2dWorld = new World(new Vector2(0, 9.81f), true);
        // Rocks
        Vector2 origin = new Vector2();
        for (Rock rock : level.rocks) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(rock.position);
            Body body = b2dWorld.createBody(bodyDef);
            rock.body = body;
            PolygonShape polygonShape = new PolygonShape();
            origin.x = rock.bounds.width / 2.0f;
            origin.y = rock.bounds.height / 2.0f;
            polygonShape.setAsBox(rock.bounds.width / 2.0f, rock.bounds.height / 2.0f, origin, 0);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
    }

    private void init(){
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        livesVisual = lives;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    public void update(float deltaTime){
        handleDebugIput(deltaTime);
        if(isGameOver() || goalReached){
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
        b2dWorld.step(deltaTime, 8, 3);
        cameraHelper.update(deltaTime);
        if(!isGameOver() && isPlayerInWater()){
            AudioManager.INSTANCE.play(Assets.INSTANCE.sounds.liveLost);
            lives--;
            if(isGameOver()){
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            } else {
                initLevel();
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

    private void onCollisionBunnyWithGoal () {
        goalReached = true;
        timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
        Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position);
        centerPosBunnyHead.x += level.bunnyHead.bounds.width;
        spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX, Constants.CARROTS_SPAWN_RADIUS);
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

        // Kollision BunnyHead mit Goal
        if (!goalReached) {
            r2.set(level.goal.bounds);
            r2.x += level.goal.position.x;
            r2.y += level.goal.position.y;
            if (r1.overlaps(r2)) onCollisionBunnyWithGoal();
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

    private void spawnCarrots (Vector2 pos, int numCarrots, float radius) {
        float carrotShapeScale = 0.5f;
        // create carrots with box2d body and fixture
        for (int i = 0; i < numCarrots; i++) {
            Carrot carrot = new Carrot();
            // calculate random spawn position, rotation, and scale
            float x = MathUtils.random(-radius, radius);
            float y = MathUtils.random(5.0f, 15.0f);
            float rotation = MathUtils.random(0.0f, 360.0f)
                    * MathUtils.degreesToRadians;
            float carrotScale = MathUtils.random(0.5f, 1.5f);
            carrot.scale.set(carrotScale, carrotScale);
            // create box2d body for carrot with start position// and angle of rotation
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(pos);
            bodyDef.position.add(x, y);
            bodyDef.angle = rotation;
            Body body = b2dWorld.createBody(bodyDef);
            body.setType(BodyDef.BodyType.DynamicBody);
            carrot.body = body;
            // create rectangular shape for carrot to allow// interactions (collisions) with other objects
            PolygonShape polygonShape = new PolygonShape();
            float halfWidth = carrot.bounds.width / 2.0f * carrotScale;
            float halfHeight = carrot.bounds.height /2.0f * carrotScale;
            polygonShape.setAsBox(halfWidth * carrotShapeScale, halfHeight * carrotShapeScale);
            // set physics attributes
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.density = 50;
            fixtureDef.restitution = 0.5f;
            fixtureDef.friction = 0.5f;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
            // finally, add new carrot to list for updating/rendering
            level.carrots.add(carrot);
        }
    }

    @Override
    public void dispose() {
        if (b2dWorld != null){
            b2dWorld.dispose();
        }
    }
}

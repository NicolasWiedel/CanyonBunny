package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import de.javadevblog.canyonbunny.util.Constants;
import de.javadevblog.canyonbunny.util.GamePreferences;

import javax.swing.*;

public class WorldRenderer implements Disposable {

    private static final String TAG = WorldRenderer.class.getName();

    private OrthographicCamera camera;
    private OrthographicCamera cameraGUI;
    private SpriteBatch batch;
    private WorldController worldController;
    private static final boolean DEBUG_DRAW_BOX2D_WORLD = true;
    private Box2DDebugRenderer b2dDebugRenderer;
    private ShaderProgram shaderMonochrome;

    public WorldRenderer(WorldController worldController){
        this.worldController = worldController;
        init();
    }

    private void init(){
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0,0,0);
        camera.update();
        cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        cameraGUI.position.set(0, 0,0);
        cameraGUI.setToOrtho(true);
        cameraGUI.update();
        b2dDebugRenderer = new Box2DDebugRenderer();
        shaderMonochrome = new ShaderProgram(Gdx.files.internal(Constants.shaderMonochromeVertex), Gdx.files.internal(Constants.shaderMonochromeFragment));
        if (!shaderMonochrome.isCompiled()) {
            String msg = "Could not compile shader program: "+ shaderMonochrome.getLog();
            throw new GdxRuntimeException(msg);
        }
    }



    public void render(){
        renderWorld(batch);
        renderGUI(batch);
    }

    private void renderWorld(SpriteBatch batch){
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (GamePreferences.INSTANCE.useMonochromeShader) {
            batch.setShader(shaderMonochrome);
            shaderMonochrome.setUniformf("u_amount", 1.0f);
        }
        worldController.level.render(batch);
        batch.setShader(null);
        batch.end();
        if(DEBUG_DRAW_BOX2D_WORLD){
            b2dDebugRenderer.render(worldController.b2dWorld, camera.combined);
        }
    }

    private void renderGuiScore(SpriteBatch batch){
        float x = -15;
        float y = -15;
        float offsetX = 50;
        float offsetY = 50;
        if (worldController.scoreVisual<worldController.score) {
            long shakeAlpha = System.currentTimeMillis() % 360;
            float shakeDist = 1.5f;
            offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist;
            offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist;
        }
        batch.draw(Assets.INSTANCE.goldCoin.goldCoin,
                x, y, offsetX, offsetY,
                100, 100, 0.35f, -0.35f, 0);
        Assets.INSTANCE.fonts.defaultBig.draw(batch,
                "" + (int)worldController.scoreVisual, x + 75, y + 37);
    }

    private void renderGuiExtraLive(SpriteBatch batch){
        float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
        float y = -15;
        for (int i = 0; i < Constants.LIVES_START; i++){
            if(worldController.lives <= i) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(Assets.INSTANCE.bunny.head,
                    x + i * 50, y, 50, 50,
                    120, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
        }
        if (worldController.lives>= 0 &&worldController.livesVisual>worldController.lives) {
            int i = worldController.lives;
            float alphaColor = Math.max(0, worldController.livesVisual - worldController.lives - 0.5f);
            float alphaScale = 0.35f * (2 + worldController.lives - worldController.livesVisual) * 2;
            float alphaRotate = -45 * alphaColor;
            batch.setColor(1.0f, 0.7f, 0.7f, alphaColor);
            batch.draw(Assets.INSTANCE.bunny.head,
                    x + i * 50, y, 50, 50,
                    120, 100, alphaScale, -alphaScale, alphaRotate);
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void renderGuiFpsCounter(SpriteBatch batch){
        float x = cameraGUI.viewportWidth - 55;
        float y = cameraGUI.viewportHeight - 15;
        int fps = Gdx.graphics.getFramesPerSecond();
        BitmapFont fpsFont = Assets.INSTANCE.fonts.defaultNormal;
        if(fps >= 45){
            // grün
            fpsFont.setColor(0,1,0,1);
        }else if(fps >= 30){
            // gelb
            fpsFont.setColor(1,1,0,1);
        }else{
            // rot bei FPS kleiner 30
            fpsFont.setColor(1,0,0,1);
        }
        fpsFont.draw(batch, "FPS: " + fps, x, y);
        fpsFont.setColor(1,1,1,1); // weiß
    }

    private void renderGUI(SpriteBatch batch){
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        renderGuiScore(batch);
        renderGuiFeatherPowerup(batch);
        renderGuiExtraLive(batch);
        renderGuiFpsCounter(batch);
        if(GamePreferences.INSTANCE.showFpsCounter){
            renderGuiFpsCounter(batch);
        }
        renderGuiGameOverMessage(batch);
        batch.end();
    }

    public void resize(int width, int height){
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();

        cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
        cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float) height) * (float)width;
        cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
        cameraGUI.update();
    }

    private void renderGuiGameOverMessage(SpriteBatch batch){
        float x = cameraGUI.viewportWidth / 2;
        float y = cameraGUI.viewportHeight / 2;
        if (worldController.isGameOver()) {
            BitmapFont fontGameOver = Assets.INSTANCE.fonts.defaultBig;
            fontGameOver.setColor(1, 0.75f, 0.25f, 1);
            fontGameOver.draw(batch, "Game Over", x, y, 0, Align.center, false);
            fontGameOver.setColor(1, 1, 1, 1);
        }
    }

    private  void renderGuiFeatherPowerup(SpriteBatch batch){
        float x = -15;
        float y = 30;
        float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;
        if (timeLeftFeatherPowerup > 0) {
            // blinkendes Icon bei weniger als 5 Ssekunden Powerup Zeit
            if (timeLeftFeatherPowerup < 4) {
                if (((int)(timeLeftFeatherPowerup * 5) % 2) != 0) {
                    batch.setColor(1, 1, 1, 0.5f);
                }
            }
            batch.draw(Assets.INSTANCE.feather.feather, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
            Assets.INSTANCE.fonts.defaultSmall.draw(batch, "" + (int)timeLeftFeatherPowerup, x + 60, y + 57);
        }
    }

    @Override
    public void dispose() {
//        batch.dispose();
        shaderMonochrome.dispose();
    }
}

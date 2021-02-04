package de.javadevblog.canyonbunny.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import de.javadevblog.canyonbunny.game.WorldController;
import de.javadevblog.canyonbunny.game.WorldRenderer;
import de.javadevblog.canyonbunny.util.GamePreferences;

public class GameScreen extends AbstractGameScreen {

    private static final String TAG = GameScreen.class.getName();

    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private boolean paused;

    public GameScreen(DirectedGame game){
        super(game);
    }

    @Override
    public void render(float delta) {
        // Gameworld nur updaten, wenn aktiv
        if (!paused){
            // update der Gameworld mit der Zeit,
            // seit dem letzten Frame
            worldController.update(Gdx.graphics.getDeltaTime());
        }
        // setzt die Farbe des gellerten Screens auf Cornflowerblue
        Gdx.gl.glClearColor(0x64/255.0f, 0x95/255.0f, 0xed/255.0f, 0xff/255.0f);
        // dezimale Schreibweise
        // Gdx.gl.glClearColor(100/255.0f, 149/255.0f, 237/255.0f, 255/255.0f);
        // leert den Bildschirm
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // zeichnet die Gameworld
        worldRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.resize(width, height);
    }

    @Override
    public void show() {
        GamePreferences.INSTANCE.load();
        worldController = new WorldController(game);
        worldRenderer = new WorldRenderer(worldController);
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void hide () {
        worldRenderer.dispose();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        super.resume();
        // wird nur auf Android aufgerufen
        paused = false;
    }

    @Override
    public InputProcessor getInputProcessor () {
        return worldController;
    }
}

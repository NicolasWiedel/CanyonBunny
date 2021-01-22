package de.javadevblog.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import de.javadevblog.canyonbunny.game.Assets;
import de.javadevblog.canyonbunny.game.WorldController;
import de.javadevblog.canyonbunny.game.WorldRenderer;

public class CanyonBunnyMain implements ApplicationListener {

    private static final String TAG = CanyonBunnyMain.class.getName();

    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private boolean paused;

    @Override
    public void create() {
        // setzt den LibGDX Loglevel aud Debug
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // lade Assets
        Assets.INSTANCE.init(new AssetManager());
        // initialisiert Controller und Renderer
        worldController = new WorldController();
        worldRenderer = new WorldRenderer(worldController);
        // Gameworld wird aktiviert
        paused = false;
    }

     @Override
    public void render() {
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
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        Assets.INSTANCE.init(new AssetManager());
        paused = false;
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
        Assets.INSTANCE.dispose();
    }
}

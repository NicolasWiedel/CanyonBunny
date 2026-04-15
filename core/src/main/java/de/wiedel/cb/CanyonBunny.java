package de.wiedel.cb;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.ScreenUtils;
import de.wiedel.cb.game.Assets;
import de.wiedel.cb.game.WorldController;
import de.wiedel.cb.game.WorldRenderer;
import de.wiedel.cb.utils.Constants;

public class CanyonBunny implements ApplicationListener {

    /** eindeutiger Bezeichner der Klasse */
    public static final String TAG = CanyonBunny.class.getSimpleName();

    /** Verweis auf den WorldController*/
    private WorldController worldController;
    /** Verweis auf den WorldRenderer */
    private WorldRenderer worldRenderer;

    /** Flag für das pausieren des Spiels */
    private boolean paused;

    @Override
    public void create() {
        // LibGDX Log Level auf debug setzen
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        // Lade die Assets des Spiels
        Assets.INSTANCE.init(new AssetManager());

        // Initialiesierung
        worldController = new WorldController();
        worldRenderer = new WorldRenderer(worldController);

        // Das Spiel ist aktiv.
        paused = false;
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.reseize(width, height);
    }

    @Override
    public void render() {

        // Die Spilewelt nur updaten, wenn das Spiel aktiv
        if (!paused) {
            // Update der Spielelogik auf Basis der vergangenen Zeit seit dem letzte Frame
            worldController.update(Gdx.graphics.getDeltaTime());

            // leert den Screen und zeichnet den Hintergrund mit der Farbe Cornflowerblue neu
            ScreenUtils.clear(Constants.CORNFLOWER_BLUE);

            // Zeichnet die aktuelle Scene
            worldRenderer.render();
        }
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

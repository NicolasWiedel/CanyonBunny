package de.javadevblog.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import de.javadevblog.canyonbunny.game.Assets;
import de.javadevblog.canyonbunny.screens.MenuScreen;

public class CanyonBunnyMain extends Game {

    private static final String TAG = CanyonBunnyMain.class.getName();

    @Override
    public void create() {
        // setzt den LibGDX Loglevel aud Debug
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // lade Assets
        Assets.INSTANCE.init(new AssetManager());
        // starte den Menuscreen
        setScreen(new MenuScreen(this));
    }
}

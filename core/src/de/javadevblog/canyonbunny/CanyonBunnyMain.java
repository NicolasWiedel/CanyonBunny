package de.javadevblog.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;
import de.javadevblog.canyonbunny.game.Assets;
import de.javadevblog.canyonbunny.screens.DirectedGame;
import de.javadevblog.canyonbunny.screens.MenuScreen;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransition;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransitionSlice;
import de.javadevblog.canyonbunny.util.AudioManager;
import de.javadevblog.canyonbunny.util.GamePreferences;

public class CanyonBunnyMain extends DirectedGame {

    private static final String TAG = CanyonBunnyMain.class.getName();

    @Override
    public void create() {
        // setzt den LibGDX Loglevel aud Debug
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // lade Assets
        Assets.INSTANCE.init(new AssetManager());
        // Load preferences für audio settings und starte die Musik
        GamePreferences.INSTANCE.load();
        AudioManager.INSTANCE.play(Assets.INSTANCE.music.song01);
        // starte den Menuscreen
        ScreenTransition transition = ScreenTransitionSlice.init(5,
                ScreenTransitionSlice.UP_DOWN, 10,
                Interpolation.pow5Out);
        setScreen(new MenuScreen(this), transition);
    }
}

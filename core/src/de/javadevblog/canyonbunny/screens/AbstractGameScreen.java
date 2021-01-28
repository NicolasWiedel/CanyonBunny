package de.javadevblog.canyonbunny.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import de.javadevblog.canyonbunny.game.Assets;

public abstract class AbstractGameScreen implements Screen {

    protected Game game;

    public AbstractGameScreen(Game game){
        this.game = game;
    }

    public abstract void render(float delta);
    public abstract void resize(int width, int height);
    public abstract void show();
    public abstract void hide();
    public abstract void pause();

    @Override
    public void resume() {
        Assets.INSTANCE.init(new AssetManager());
    }

    @Override
    public void dispose() {
        Assets.INSTANCE.dispose();
    }
}

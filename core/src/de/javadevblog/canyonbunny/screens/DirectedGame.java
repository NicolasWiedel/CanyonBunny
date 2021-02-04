package de.javadevblog.canyonbunny.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransition;

public abstract class DirectedGame implements ApplicationListener {

    private boolean init;
    private AbstractGameScreen currScreen;
    private AbstractGameScreen nextScreen;
    private FrameBuffer currFbo;
    private FrameBuffer nextFbo;
    private SpriteBatch batch;
    private float t;
    private ScreenTransition screenTransition;

    public void setScreen(AbstractGameScreen screen){
        setScreen(screen, null);
    }

    public void setScreen(AbstractGameScreen screen,
                          ScreenTransition screenTransition){

    }
}

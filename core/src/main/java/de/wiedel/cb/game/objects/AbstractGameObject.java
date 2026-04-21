package de.wiedel.cb.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractGameObject {

    public Vector2 position;
    public Vector2 dimension;
    protected Vector2 origin;
    protected Vector2 scale;
    protected float rotation;

    public AbstractGameObject(){
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1, 1);
        rotation = 0;
    }

    public void update(float delta){

    }

    public abstract void render(SpriteBatch batch);
}

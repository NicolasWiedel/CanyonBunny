package de.javadevblog.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class AbstractGameObject {

    public Vector2 position;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public float rotation;
    public Vector2 velocity;
    public Vector2 terminalVelocity;
    public Vector2 friction;
    public Vector2 acceleration;
    public Rectangle bounds;
    public Body body;

    public float stateTime;
    public Animation<TextureAtlas.AtlasRegion> animation;

    public AbstractGameObject() {position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1, 1);
        rotation = 0;
        velocity = new Vector2();
        terminalVelocity = new Vector2(1, 1);
        friction = new Vector2();
        acceleration = new Vector2();
        bounds = new Rectangle();
    }

    public void setAnimation (Animation<TextureAtlas.AtlasRegion> animation) {
        this.animation = animation;
        stateTime = 0;
    }

    public void update(float deltaTime){
        stateTime += deltaTime;
        if(body == null){
            updateMotionX(deltaTime);
            updateMotionY(deltaTime);
            // zur neuen Position bewegen
            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;
        }
        else {
            position.set(body.getPosition());
            rotation = body.getAngle() * MathUtils.radiansToDegrees;
        }
    }

    protected void updateMotionX(float deltaTime) {
        if(velocity.x != 0){
            // natürliche Bremse
            if (velocity.x > 0){
                velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
            } else {
                velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
            }
        }
        // Beschleunigungn berücksichtigen
        velocity.x += acceleration.x * deltaTime;
        // stop bei maximaler Geschwindigkeit
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
    }

    protected void updateMotionY(float deltaTime){
        if (velocity.y != 0){
            // natürliche Bremse
            if (velocity.y > 0){
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
            } else {
                velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
            }
        }
        // Beschleunigungn berücksichtigen
        velocity.y += acceleration.y * deltaTime;
        // stop bei maximaler Geschwindigkeit
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }

    public abstract void render (SpriteBatch batch);
}
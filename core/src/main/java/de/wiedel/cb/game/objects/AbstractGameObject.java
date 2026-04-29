package de.wiedel.cb.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractGameObject {

    public Vector2 position;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public float rotation;

    /** die Geschwindigkeit des Spielobjektes in m/s */
    public Vector2 velocity;
    /** die maximale Geschwindigkeit des Spielobjektes in m/s */
    public Vector2 terminalVelocity;
    /** die Verzögerungskraft die auf das Objekt einwirkt */
    public Vector2 friction;
    /** die Beschleunigung des Spielobjektes  in m/s2 */
    public Vector2 acceleration;
    /** die Kollisionsbox um das Spielobjekt */
    public Rectangle bounds;

    public AbstractGameObject(){
        position = new Vector2();
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

    public void update(float delta){
        updateMotionX(delta);
        updateMotionY(delta);
        // zur nächsten Position bewegen
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }

    public abstract void render(SpriteBatch batch);

    protected void updateMotionX(float delta){
        if (velocity.x != 0){
            // friction anwenden
            if (velocity.x > 0){
                velocity.x = Math.max(velocity.x - friction.x * delta, 0);
            } else {
                velocity.x = Math.min(velocity.x + friction.x * delta, 0);
            }
        }
        // Beschleunigung anwenden
        velocity.x += acceleration.x * delta;
        // die Geschwindigkeit geringer als die maximale halten
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
    }

    protected void updateMotionY(float delta){
        if (velocity.y != 0){
            // friction anwenden
            if (velocity.y > 0){
                velocity.y = Math.max(velocity.y - friction.y * delta, 0);
            } else {
                velocity.y = Math.min(velocity.y + friction.y * delta, 0);
            }
        }
        // Beschleunigung anwenden
        velocity.y += acceleration.y * delta;
        // die Geschwindigkeit geringer als die maximale halten
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }
}

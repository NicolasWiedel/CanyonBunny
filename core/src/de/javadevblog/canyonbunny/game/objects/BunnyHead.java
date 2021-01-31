package de.javadevblog.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.javadevblog.canyonbunny.game.Assets;
import de.javadevblog.canyonbunny.util.CharacterSkin;
import de.javadevblog.canyonbunny.util.Constants;
import de.javadevblog.canyonbunny.util.GamePreferences;

public class BunnyHead extends AbstractGameObject {

    public static final String TAG = BunnyHead.class.getName();

    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.18f;

    public enum ViewDirection{
        LEFT,
        RIGHT
    }

    public enum JumpState{
        GROUNDED,
        FALLING,
        JUMP_RISING,
        JUMP_FALLIMG
    }

    private TextureRegion regHead;

    public ViewDirection viewDirection;
    public float timeJumping;
    public JumpState jumpState;
    public boolean hasFeatherPowerup;
    public float timeLeftFeatherPowerup;

    public BunnyHead(){
        init();
    }

    public void init(){
        dimension.set(1, 1);
        regHead = Assets.INSTANCE.bunny.head;
        // Bild auf Objekt zentrieren
        origin.set(dimension.x / 2, dimension.y / 2);
        // Boundingbox zur Kollisionserkennung
        bounds.set(0, 0, dimension.x, dimension.y);
        // setzen der physikalischen Werte
        terminalVelocity.set(3.0f, 4.0f);
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f, -25.0f);
        // Blickrichtung
        viewDirection = ViewDirection.RIGHT;
        // JumpState
        jumpState = JumpState.FALLING;
        timeJumping = 0;
        // Power-Ups
        hasFeatherPowerup = false;
        timeLeftFeatherPowerup = 0;
    }

    public void setJumping(boolean jumKeyPressed){
        switch (jumpState){
            case GROUNDED: // Bunny steht auf einer Platform
                if (jumKeyPressed){
                    // jumTime wird gezählt
                    timeJumping = 0;
                    jumpState = JumpState.JUMP_RISING;
                }
                break;
            case JUMP_RISING: // aufsteigend
                if(!jumKeyPressed){
                    jumpState = JumpState.JUMP_FALLIMG;
                }
                break;
            case FALLING:
                if (jumKeyPressed && hasFeatherPowerup){
                    timeJumping = JUMP_TIME_OFFSET_FLYING;
                    jumpState = JumpState.JUMP_RISING;
                }
                break;
        }
    }

    public void setFeatherPowerup(boolean pickedUp){
        hasFeatherPowerup = pickedUp;
        if (pickedUp){
            timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
        }
    }

    public boolean hasFeatherPowerup() {
        return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(velocity.x != 0) {
            viewDirection = velocity.x < 0 ? ViewDirection.LEFT : ViewDirection.RIGHT;
        }

        if(timeLeftFeatherPowerup > 0){
            timeLeftFeatherPowerup -= deltaTime;
            if(timeLeftFeatherPowerup < 0){
                // Powerup beenden
                timeLeftFeatherPowerup = 0;
                setFeatherPowerup(false);
            }
        }
    }

    @Override
    protected void updateMotionY(float deltaTime) {
        switch (jumpState){
            case GROUNDED:
                jumpState = JumpState.FALLING;
                break;
            case JUMP_RISING:
                // Sprungzeit berechnen
                timeJumping += deltaTime;
                // verbleibende Sprungzeit
                if(timeJumping <= JUMP_TIME_MAX){
                    // während des Sprungs
                    velocity.y = terminalVelocity.y;
                }
                break;
            case FALLING:
                break;
            case JUMP_FALLIMG:
                // Sprungzeit berechnen
                timeJumping += deltaTime;
                if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN){
                    velocity.y = terminalVelocity.y;
                }
        }
        if (jumpState != JumpState.GROUNDED){
            super.updateMotionY(deltaTime);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = null;

        // Färbung während Powerup
        if(hasFeatherPowerup){
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
        }

        // Skin Farbe wählen
        batch.setColor(CharacterSkin.values()[GamePreferences.INSTANCE.charSkin] .getColor());

        // zeichnen
        reg = regHead;
        batch.draw(reg.getTexture(),
                position.x, position.y, origin.x, origin.y,
                dimension.x, dimension.y, scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                viewDirection == ViewDirection.LEFT, false);

        // Farbe zurücksetzen
        batch.setColor(1,1,1,1);
    }
}

package de.wiedel.cb.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.wiedel.cb.game.Assets;
import de.wiedel.cb.utils.Constants;

public class BunnyHead extends AbstractGameObject {

    public static final String TAG = BunnyHead.class.getSimpleName();

    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

    public enum VIEW_DIRECTION { LEFT, RIGHT}

    public enum JUMP_STATE {
        GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
    }

    private TextureRegion regHead;

    private VIEW_DIRECTION viewDirection;
    public float timeJumping;
    public JUMP_STATE jumpState;
    public boolean hasFeatherPowerUp;
    public float timeLeftFeatherPowerUp;

    public BunnyHead(){
        init();
    }

    public void init(){
        dimension.set(1, 1);
        regHead = Assets.INSTANCE.bunny.head;
        // das Bild auf der Mitte des Objects mitteln
        origin.set(dimension.x / 2, dimension.y / 2);
        // Kollisionsbox
        bounds.set(0, 0, dimension.x, dimension.y);
        // physikalische Werte
        terminalVelocity.set(3.0f, 4.0f);
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f, -25.0f);
        // Sichtrichtung und Zustand
        viewDirection = VIEW_DIRECTION.RIGHT;
        jumpState = JUMP_STATE.JUMP_FALLING;
        timeJumping = 0;
        // PowerUps
        hasFeatherPowerUp = false;
        timeLeftFeatherPowerUp = 0;
    }

    public void setJumping(boolean jumKeyPressed){
        switch (jumpState){
            case GROUNDED:
                if(jumKeyPressed){
                    timeJumping = 0;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
            case JUMP_RISING:
                if(!jumKeyPressed){
                    jumpState = JUMP_STATE.JUMP_FALLING;
                }
                break;
            case FALLING:
            case JUMP_FALLING:
                if(jumKeyPressed && hasFeatherPowerUp){
                    timeJumping = JUMP_TIME_OFFSET_FLYING;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
        }
    }

    public void setFeatherPowerUp(boolean pickedUp){
        hasFeatherPowerUp = pickedUp;
        if (pickedUp){
            timeLeftFeatherPowerUp = Constants.ITEM_FEATHER_POWERUP_DURATION;
        }
    }

    public boolean hasFeatherPowerUp(){
        return hasFeatherPowerUp && timeLeftFeatherPowerUp > 0;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (velocity.x != 0){
            viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
        }
        if (timeLeftFeatherPowerUp > 0){
            timeLeftFeatherPowerUp -= delta;
            if(timeLeftFeatherPowerUp < 0){
                timeLeftFeatherPowerUp = 0;
                setFeatherPowerUp(false);
            }
        }
    }

    @Override
    protected void updateMotionY(float delta) {
        switch (jumpState){
            case GROUNDED:
                jumpState = JUMP_STATE.JUMP_FALLING;
                break;
            case JUMP_RISING:
                timeJumping += delta;
                if (timeJumping <= JUMP_TIME_MAX){
                    velocity.y = terminalVelocity.y;
                }
                break;
            case FALLING:
                break;
            case JUMP_FALLING:
                timeJumping += delta;
                if (timeJumping > 0 && timeJumping < JUMP_TIME_MIN){
                    velocity.y = terminalVelocity.y;
                }
        }

        if (jumpState != JUMP_STATE.GROUNDED){
            super.updateMotionY(delta);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = null;

        if (hasFeatherPowerUp){
            batch.setColor(1.0f, 0.8f, 0.0f,1.0f);
        }

        reg = regHead;
        batch.draw(reg.getTexture(),
            position.x, position.y,
            origin.x, origin.y,
            dimension.x, dimension.y,
            scale.x, scale.y,
            rotation,
            reg.getRegionX(), reg.getRegionY(),
            reg.getRegionWidth(), reg.getRegionHeight(),
            viewDirection == VIEW_DIRECTION.LEFT, false);

        batch.setColor(1, 1, 1, 1);
    }
}

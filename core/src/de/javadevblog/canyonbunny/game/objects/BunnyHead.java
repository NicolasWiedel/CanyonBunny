package de.javadevblog.canyonbunny.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import de.javadevblog.canyonbunny.game.Assets;
import de.javadevblog.canyonbunny.util.AudioManager;
import de.javadevblog.canyonbunny.util.CharacterSkin;
import de.javadevblog.canyonbunny.util.Constants;
import de.javadevblog.canyonbunny.util.GamePreferences;

public class BunnyHead extends AbstractGameObject {

    public static final String TAG = BunnyHead.class.getName();

    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.18f;

    private Animation<TextureAtlas.AtlasRegion> animNormal;
    private Animation<TextureAtlas.AtlasRegion> animCopterTransform;
    private Animation<TextureAtlas.AtlasRegion> animCopterTransformBack;
    private Animation<TextureAtlas.AtlasRegion> animCopterRotate;

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

    public ParticleEffect dustParticles = new ParticleEffect();

    public BunnyHead(){
        init();
    }

    public void init(){
        dimension.set(1, 1);

        animNormal = Assets.INSTANCE.bunny.animNormal;
        animCopterTransform = Assets.INSTANCE.bunny.animCopterTransform;
        animCopterTransformBack = Assets.INSTANCE.bunny.animCopterTransformBack;
        animCopterRotate = Assets.INSTANCE.bunny.animCopterRotate;
        setAnimation(animNormal);

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
        // Particles
        dustParticles.load(Gdx.files.internal("particles/dust.pfx"),
                Gdx.files.internal("particles"));
    }

    public void setJumping(boolean jumKeyPressed){
        switch (jumpState){
            case GROUNDED: // Bunny steht auf einer Platform
                if (jumKeyPressed){
                    AudioManager.INSTANCE.play(Assets.INSTANCE.sounds.jump);
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
                    AudioManager.INSTANCE.play(Assets.INSTANCE.sounds.jumpWithFeather, 1,
                            MathUtils.random(1.0f, 1.1f));
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
            if (animation == animCopterTransformBack) {
                // Restart "Transform" animation if another feather power-up
                // was picked up during "TransformBack" animation. Otherwise,
                // the "TransformBack" animation would be stuck while the
                // power-up is still active.
                setAnimation(animCopterTransform);
            }
            timeLeftFeatherPowerup -= deltaTime;
            if(timeLeftFeatherPowerup < 0){
                // Powerup beenden
                timeLeftFeatherPowerup = 0;
                setFeatherPowerup(false);
                setAnimation(animCopterTransformBack);
            }
        }
        dustParticles.update(deltaTime);

        // Change animation state according to feather power-up
        if (hasFeatherPowerup) {
            if (animation == animNormal) {
                setAnimation(animCopterTransform);
            } else if (animation == animCopterTransform) {
                if (animation.isAnimationFinished(stateTime))
                    setAnimation(animCopterRotate);
            }
        } else {
            if (animation == animCopterRotate) {
                if (animation.isAnimationFinished(stateTime))
                    setAnimation(animCopterTransformBack);
            } else if (animation == animCopterTransformBack) {
                if (animation.isAnimationFinished(stateTime))
                    setAnimation(animNormal);
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
            dustParticles.allowCompletion();
            super.updateMotionY(deltaTime);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = null;

        // Particles zeichnen
        dustParticles.draw(batch);

        // Färbung während Powerup
        if(hasFeatherPowerup){
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
        }

        // Skin Farbe wählen
        batch.setColor(CharacterSkin.values()[GamePreferences.INSTANCE.charSkin] .getColor());

        float dimCorrectionX = 0;
        float dimCorrectionY = 0;
        if (animation != animNormal) {
            dimCorrectionX = 0.05f;
            dimCorrectionY = 0.2f;
        }

        // zeichnen
//        reg = regHead;
        reg = animation.getKeyFrame(stateTime, true);
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

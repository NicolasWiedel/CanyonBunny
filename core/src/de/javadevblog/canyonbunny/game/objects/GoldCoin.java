package de.javadevblog.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import de.javadevblog.canyonbunny.game.Assets;

public class GoldCoin extends AbstractGameObject {

    private TextureRegion regGoldCoin;

    public boolean collected;

    public GoldCoin(){
        init();
    }

    private void init(){
        dimension.set(0.5f, 0.5f);

//        regGoldCoin = Assets.INSTANCE.goldCoin.goldCoin;
        setAnimation(Assets.INSTANCE.goldCoin.animGoldCoin);
        stateTime = MathUtils.random(0.0f, 1.0f);

        // setzt die Boundingbox für Kollisionserkennung
        bounds.set(0, 0, dimension.x, dimension.y);

        collected = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        if(collected){
            return;
        }
        TextureRegion reg = null;
//        reg = regGoldCoin;
        reg = animation.getKeyFrame(stateTime, true);
        batch.draw(reg.getTexture(),
                position.x, position.y,origin.x, origin.y,
                dimension.x, dimension.y, scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),reg.getRegionHeight(),
                false, false);
    }

    public int getScore(){
        return 100;
    }
}

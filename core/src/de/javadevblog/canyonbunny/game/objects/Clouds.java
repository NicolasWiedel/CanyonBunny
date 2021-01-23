package de.javadevblog.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.javadevblog.canyonbunny.game.Assets;

public class Clouds extends AbstractGameObject{

    private float length;

    private Array<TextureRegion> regClouds;
    private Array<Cloud> clouds;

    public Clouds(float length){
        this.length = length;
        init();
    }

    private void init(){
        dimension.set(3.0f, 1.5f);
        regClouds = new Array<TextureRegion>();
        regClouds.add(Assets.INSTANCE.levelDecoration.cloud01);
        regClouds.add(Assets.INSTANCE.levelDecoration.cloud02);
        regClouds.add(Assets.INSTANCE.levelDecoration.cloud02);

        int distFac = 5;
        int numClouds = (int)(length /distFac);

        clouds = new Array<Cloud>();
        for (int i =0; i < numClouds; i++){
            Cloud cloud =spawnCloud();
            cloud.position.x = i * distFac;
            clouds.add(cloud);
        }
    }

    private Cloud spawnCloud(){
        Cloud cloud = new Cloud();
        cloud.dimension.set(dimension);
        // wählt zufälliges Cloudimage aus
        cloud.setRegion(regClouds.random());
        // Position
        Vector2 pos = new Vector2();
        pos.x = length + 10;
        pos.y += 1.75f;
        // zufällige add oder sub auf die Position
        pos.y += MathUtils.random(0.0f, 0.2f) * (MathUtils.randomBoolean() ? 1 : -1);
        cloud.position.set(pos);
        return cloud;
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Cloud cloud : clouds){
            cloud.render(batch);
        }
    }

    private  class Cloud extends AbstractGameObject {

        private TextureRegion regCloud;

        public Cloud(){ }

        public void setRegion (TextureRegion regCloud){
            this.regCloud = regCloud;
        }

        @Override
        public void render(SpriteBatch batch) {
            TextureRegion reg = regCloud;
            batch.draw(reg.getTexture(),
                    position.x + origin.x, position.y + origin.y,
                    origin.x, origin.y,
                    dimension.x, dimension.y,
                    scale.x, scale.y,
                    rotation,
                    reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(),
                    false, false);

        }
    }
}

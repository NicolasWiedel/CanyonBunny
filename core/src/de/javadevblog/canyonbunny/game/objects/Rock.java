package de.javadevblog.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.javadevblog.canyonbunny.game.Assets;

public class Rock extends AbstractGameObject{

    private TextureRegion regEdge;
    private TextureRegion regMiddle;

    private int length;

    public Rock(){
        init();
    }

    private void init(){

        dimension.set(1, 1.5f);

        regEdge = Assets.INSTANCE.rock.edge;
        regMiddle = Assets.INSTANCE.rock.middle;;

        // Startlänge des Rocks
        setLength(1);
    }

    public void setLength(int length){
        this.length = length;
        bounds.set(0, 0, dimension.x * length, dimension.y);
    }

    public void increaseLength(int amount){
        setLength(length + amount);
    }

    @Override
    public void render(SpriteBatch batch) {

        TextureRegion reg = null;

        float relX = 0;
        float relY = 0;

        // linke Seite zeichnen
        reg = regEdge;
        relX -= dimension.x / 4;
        batch.draw(reg.getTexture(),
                position.x + relX, position.y + relY,
                origin.x, origin.y,
                dimension.x / 4, dimension.y,
                scale.x, scale.y,
                rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);

        // Mitte zeichnen
        relX = 0;
        reg = regMiddle;
        for (int i = 0; i < length; i++) {
            batch.draw(reg.getTexture(),
                    position.x + relX, position.y +  relY,
                    origin.x, origin.y,
                    dimension.x, dimension.y,
                    scale.x, scale.y,
                    rotation, reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(),
                    false, false);
            relX += dimension.x;
        }

        // rechte Seite zeichnen
        reg = regEdge;
        batch.draw(reg.getTexture(),
                position.x + relX, position.y + relY,
                origin.x + dimension.x / 8, origin.y,
                dimension.x / 4, dimension.y,
                scale.x, scale.y,
                rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                true, false);

    }
}

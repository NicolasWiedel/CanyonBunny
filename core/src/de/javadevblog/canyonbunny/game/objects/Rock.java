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
    }

    public void increaseLength(int amount){
        setLength(length + amount);
    }

    @Override
    public void render(SpriteBatch batch) {

    }
}

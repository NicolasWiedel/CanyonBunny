package de.wiedel.cb.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.wiedel.cb.game.Assets;

public class Mountains extends AbstractGameObject {

    private TextureRegion regMountainLeft;
    private TextureRegion regMountainRight;

    private int length;

    public Mountains(int length){
        this.length = length;
        init();
    }

    private void init(){
        dimension.set(10, 2);

        regMountainLeft = Assets.INSTANCE.levelDecoration.mountainLeft;
        regMountainRight = Assets.INSTANCE.levelDecoration.mountainRight;

        // verschieben und Länge anpassen
        origin.x = -dimension.x * 2;
        length += dimension.x * 2;
    }

    @Override
    public void render(SpriteBatch batch) {

    }
}

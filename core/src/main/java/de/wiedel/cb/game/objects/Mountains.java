package de.wiedel.cb.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

    private void drawMountain(SpriteBatch batch, float offsetX, float offsetY, float tintColor){
        TextureRegion reg = null;
        batch.setColor(tintColor, tintColor, tintColor, 1);
        float xRel = dimension.x * offsetX;
        float yRel = dimension.y * offsetY;

        // Die Berge nehmen den ganzen Level ein
        int mountainLength = 0;
        mountainLength += MathUtils.ceil(length / (2 * dimension.x));
        mountainLength += MathUtils.ceil(0.5f + offsetX);
        for (int i = 0; i < mountainLength; i++){
            // MountainLeft
            batch.draw(reg.getTexture(),
                origin.x + xRel, position.y + origin.y + yRel,
                origin.x, origin.y,
                dimension.x, dimension.y,
                scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
            xRel += dimension.x;

            // Mountain Right
            reg = regMountainRight;
            batch.draw(reg.getTexture(),
                origin.x + xRel, position.y + origin.y + yRel,
                origin.x, origin.y,
                dimension.x, dimension.y,
                scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
            xRel += dimension.x;
        }

        // Farbe zurücksetzen
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void render(SpriteBatch batch) {
        // hinterste Berge (dunkelgrau)
        drawMountain(batch, 0.5f, 0.5f, 0.5f);
        // mittlere Berge (grau)
        drawMountain(batch, 0.25f, 0.25f, 0.7f);
        // vorderste Berge (hell grau)
        drawMountain(batch, 0.0f, 0.0f, 0.9f);

    }
}

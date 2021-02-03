package de.javadevblog.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.javadevblog.canyonbunny.game.Assets;

public class Mountains extends AbstractGameObject{

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

        origin.x = -dimension.x * 2;
        length += dimension.x * 2;
    }

    private void drawMountain(SpriteBatch batch, float offsetX, float offsetY,
                              float titnColor, float parallaxSpeedX){
        TextureRegion reg = null;
        batch.setColor(titnColor, titnColor, titnColor, 1);
        float xRel = dimension.x * offsetX;
        float yRel = dimension.y * offsetY;

        // Berge über den ganzen Level
        int mountainLength = 0;
        mountainLength += MathUtils.ceil(length / (2 * dimension.x) * (1 - parallaxSpeedX));
        mountainLength += MathUtils.ceil(0.5f + offsetX);

        for (int i = 0; i < mountainLength; i++){
            // Berge links
            reg = regMountainLeft;
            batch.draw(reg.getTexture(),
                    origin.x + xRel + position.x * parallaxSpeedX, origin.y + yRel + position.y,
                    origin.x, origin.y,
                    dimension.x, dimension.y,
                    scale.x, scale.y,
                    rotation,
                    reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(),
                    false, false);
            xRel += dimension.x;

            // mountain right
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

    public void updateScrollPosition (Vector2 camPosition) {
        position.set(camPosition.x, position.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        // dunkelgrau
        drawMountain(batch, 0.5f, 0.5f, 0.5f, 0);
        // grau
        drawMountain(batch, 0.25f, 0.25f, 0.7f, 0);
        // hellgrau
        drawMountain(batch, 0.0f, 0.0f, 0.9f, 0);
    }
}

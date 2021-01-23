package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import de.javadevblog.canyonbunny.game.objects.*;

public class Level {

    public static final String TAG = Level.class.getName();

    // Objekte
    public Array<Rock> rocks;

    // Deko
    public Clouds clouds;
    public Mountains mountains;
    public WaterOverlay waterOverlay;

    public Level(String fileName){
        init(fileName);
    }

    private void init(String fileName) {
        // objects
        rocks = new Array<Rock>();

        // Laden der Level Datei
        Pixmap pixmap = new Pixmap(Gdx.files.internal(fileName));
        // scannen der Pixel von oben links nach unten rechts
        int lastPixel = -1;
        for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
                AbstractGameObject obj = null;
                float offsetHeight = 0;
                // Höhe wächst von unten nach oben
                float baseHeight = pixmap.getHeight() - pixelY;
                // bestimmt Farbe des aktuellen Pixels
                int currentPixel = pixmap.getPixel(pixelX, pixelY);

                // Farben zuordnen
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    // nichts tun
                }
                // rock
                else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
                    if (lastPixel != currentPixel) {
                        obj = new Rock();
                        float heightIncreaseFacktor = 0.25f;
                        offsetHeight = -2.5f;
                        obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFacktor + offsetHeight);
                        rocks.add((Rock)obj);
                    } else {
                        rocks.get(rocks.size - 1).increaseLength(1);
                    }
                }
                // Player
                else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)){
                    //TODO
                }
                // Feder
                else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)){
                    //TODO
                }
                // Münze
                else if (BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)){
                    // TODO
                }
                else {
                    int r = 0xff & (currentPixel >>> 24); //red color channel
                    int g = 0xff & (currentPixel >>> 16); //green color channel
                    int b = 0xff & (currentPixel >>> 8);  //blue color channel
                    int a = 0xff & currentPixel;  //alpha channel
                    Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" +
                            pixelY + ">: r<" + r+ "> g<" + g + "> b<" + b + "> a<" + a + ">");
                }
                lastPixel = currentPixel;
            }
        }

        // decoration
        clouds = new Clouds(pixmap.getWidth());
        clouds.position.set(0, 2);
        mountains = new Mountains(pixmap.getWidth());
        mountains.position.set(-1, -1);
        waterOverlay = new WaterOverlay(pixmap.getWidth());
        waterOverlay.position.set(0, -3.75f);

        // free memory
        pixmap.dispose();
        Gdx.app.debug(TAG, "level '" + fileName + "' loaded");
    }

    public void render(SpriteBatch batch){
        //Berge zeichnen
        mountains.render(batch);

        // Rocks zeichnen
        for (Rock rock : rocks)
            rock.render(batch);

        // Wasser zeichnen
        waterOverlay.render(batch);
        // Wolken zeichnen
        clouds.render(batch);
    }

    public enum BLOCK_TYPE {
        EMPTY(0, 0, 0), // black
        ROCK(0, 255, 0), // green
        PLAYER_SPAWNPOINT(255, 255, 255), // white
        ITEM_FEATHER(255, 0, 255), // purple
        ITEM_GOLD_COIN(255, 255, 0); // yellow

        private int color;
        private BLOCK_TYPE (int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        public boolean sameColor (int color) {
            return this.color == color;
        }

        public int getColor () {
            return color;
        }
    }
}

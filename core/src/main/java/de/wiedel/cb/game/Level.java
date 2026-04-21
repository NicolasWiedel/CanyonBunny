package de.wiedel.cb.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import de.wiedel.cb.game.objects.Clouds;
import de.wiedel.cb.game.objects.Mountains;
import de.wiedel.cb.game.objects.Rock;
import de.wiedel.cb.game.objects.WaterOverlay;

public class Level {

    public static final String TAG = Level.class.getSimpleName();

    public enum BLOCK_TYPE {
        EMPTY(0, 0, 0), // schwarz
        ROCK(0, 255, 0), // grün
        PLAYER_SPAWNPOINT(255, 255, 255), // weiß
        ITEM_FEATHER(255, 0, 255), // rosa
        ITEM_GOLD_COIN(255, 255, 0); // gelb

        private int color;
        private BLOCK_TYPE(int r, int g, int b){
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        public boolean sameColor(int color){
            return this.color == color;
        }

        public int getColor() {
            return color;
        }
    }

    // Objekte
    public Array<Rock> rocks;

    // Deko
    public Clouds clouds;
    public Mountains mountains;
    public WaterOverlay waterOverlay;

    public Level(String fileName){
        init(fileName);
    }

    private void init(String fileName){}
    public void render(SpriteBatch batch){}
}

package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.utils.Array;
import de.javadevblog.canyonbunny.game.objects.Rock;

public class Level {

    public static final String TAG = Level.class.getName();

    public enum BlockType {

        EMPTY(0, 0, 0), // schwarz
        ROCK(0, 255, 0), // grün
        PLAYER_SPAWN_POINT(255, 255, 255), // weiß
        ITEM_FEATHER(255, 0, 255), //purple
        ITEM_GOLD_COIN(255,255,0); // gelb

        private int color;

        private BlockType(int r, int g, int b){
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        public boolean sameColor(int color){
            return this.color == color;
        }

        public int getColor(){
            return color;
        }
    }

    // Objekte
    public Array<Rock> rocks;
    // Deko

}

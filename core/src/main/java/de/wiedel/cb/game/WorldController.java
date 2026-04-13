package de.wiedel.cb.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class WorldController {

    /** eindeutiger Bezeichner der Klasse */
    public static final String TAG = WorldController.class.getSimpleName();

    /** Code zu Testzwecken (2 Members) */
    public Sprite[] testSprites;
    public int selectedSprite;

    /** Konstruktor */
    public WorldController(){
        init();
    }

    /** Initialisierungsmethode */
    private void init(){
        initTestObjects();
    }

    /** Spielelogik */
    public void update(float delta){
        updateTestObjects(delta);
    }


    /** Code zu Testzwecken */
    private void initTestObjects(){
        // erstellt ein Array mit Testobjekten
        testSprites = new Sprite[5];

        int width = 32;
        int height = 32;
        Pixmap pixmap = createProceduralPixmap(width, height);
        Texture texture = new Texture(pixmap);
        for (int i = 0; i < testSprites.length; i++){
            Sprite sprite = new Sprite(texture);
            sprite.setSize(1, 1);
            sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
            float randomX = MathUtils.random(-2f, 2f);
            float randomY = MathUtils.random(-2f, 2f);
            sprite.setPosition(randomX, randomY);
            testSprites[i] = sprite;
        }
        selectedSprite = 0;
    }
    private Pixmap createProceduralPixmap(int width, int height){
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);
        return pixmap;
    }
    private void updateTestObjects(float delta){
        float rotation = testSprites[selectedSprite].getRotation();
        rotation += 90 * delta;
        rotation %= 360;
        testSprites[selectedSprite].setRotation(rotation);
    }
}

package de.wiedel.cb.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class WorldRenderer implements Disposable {

    /** eindeutiger Bezeichner der Klasse */
    public static final String TAG = WorldRenderer.class.getSimpleName();

    /** Die Kamera zur Darstellung der Spielscene */
    private OrthographicCamera camera;
    /** Der "Pinsel" zum zeichnen */
    private SpriteBatch batch;
    /** Verweis auf den WorldController */
    private WorldController worldController;

    /** Konstruktor mit Referenz auf den WorldController */
    public WorldRenderer(WorldController worldController){
        this.worldController = worldController;
        init();
    }

    /** Initialisierung der Klassen-Member */
    private void init(){

    }

    /** Zeichenmethode */
    public void render(){

    }

    /** was geschieht, wenn die Fenstergröße verändert wird */
    public void reseize(int width, int height){

    }

    /** Freigabe von Ressourcen */
    @Override
    public void dispose() {

    }
}

package de.wiedel.cb.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import de.wiedel.cb.utils.Constants;

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
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
    }

    /** Zeichenmethode */
    public void render(){
        renderTestObjects();
    }

    /** was geschieht, wenn die Fenstergröße verändert wird */
    public void reseize(int width, int height){
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();
    }

    /** Freigabe von Ressourcen */
    @Override
    public void dispose() {
        batch.dispose();
    }

    /** Code zu Testzwecken */
    private void renderTestObjects(){
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Sprite sprite : worldController.testSprites){
            sprite.draw(batch);
        }
        batch.end();
    }
}

package de.wiedel.cb.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.wiedel.cb.game.objects.AbstractGameObject;

/** Hilfsklasse für Kamerafunktionen */
public class CameraHelper {
    /** eindeutiger Bezeichner der Klasse */
    private static final String TAG = CameraHelper.class.getSimpleName();

    /** maximale Faktoren zu rein- und rauszoomen */
    private final float MAX_ZOOM_IN = 0.25f;
    private final float MAX_ZOOM_OUT = 10f;

    /** speichert Position und Zoom der Kamera */
    private Vector2 position;
    private float zoom;
    /** Das Spielobjekt, dem die Kamera folgen soll */
    private AbstractGameObject target;

    public CameraHelper() {
        position = new Vector2();
        zoom = 1f;
    }

    /** wird bei jedem Fram aufgerufen */
    public void update(float delta){
        if(!hasTarget()){
            return;
        }

        position.x = target.position.x + target.origin.x;
        position.y = target.position.y + target.origin.y;
    }

    /** getter und setter */
    public void setPosition(float x, float y){
        this.position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void addZoom(float amount){
        setZoom(zoom + amount);
    }

    public void setZoom(float zoom){
        this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
    }

    public float getZoom() {
        return zoom;
    }

    public void setTarget(AbstractGameObject target) {
        this.target = target;
    }

    public AbstractGameObject getTarget() {
        return target;
    }

    public boolean hasTarget(){
        return target != null;
    }

    public boolean hasTarget(AbstractGameObject target){
        return hasTarget() && this.target.equals(target);
    }

    /** wird vor dem Zeichnen aufgerufen, um die Kamera zu aktualisieren */
    public void applyTo(OrthographicCamera camera){
        camera.position.x = position.x;
        camera.position.y = position.y;
        camera.zoom = zoom;
        camera.update();
    }
}

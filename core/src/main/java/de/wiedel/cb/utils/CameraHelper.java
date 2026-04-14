package de.wiedel.cb.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/** Hilfsklasse für Kamerafunktionen */
public class CameraHelper {
    /** eindeutiger Bezeichner der Klasse */
    private static final String TAG = CameraHelper.class.getSimpleName();

    /** maximale Faktoren zu rein- und rauszoomen */
    private final float MAX_ZOOM_IN = 0.25f;
    private final float MAX_ZOOM_OUT = 10f;

    private Vector2 position;
    private float zoom;
    private Sprite target;

    public CameraHelper() {
        position = new Vector2();
        zoom = 1f;
    }
}

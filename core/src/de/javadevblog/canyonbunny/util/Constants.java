package de.javadevblog.canyonbunny.util;

public class Constants {

    // die sichtbare GameWorld ist 5 Meter breit
    public static final float VIEWPORT_WIDTH = 5.0f;

    // die sichtbare GameWorld ist 5 Meter breit
    public static final float VIEWPORT_HEIGHT = 5.0f;

    // GUI Breite
    public static final float VIEWPORT_GUI_WIDTH = 800.0f;

    // GUI Höhe
    public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

    // Ort und Name der Atlas - Datei für die Assets
    public static final String TEXTURE_ATLAS_OBJECTS = "images/canyonbunny.atlas";

    // Ort für die Image-Datei de Level 01
    public static final String LEVEL_01 = "levels/level-01.png";

    // Menge der Extraleben beim Start eines Levels
    public static final int LIVES_START = 3;

    // Dauer der Feather Powe ups
    public static final float ITEM_FEATHER_POWERUP_DURATION = 9;

    // Verzögerung nach Game Over
    public static final float TIME_DELAY_GAME_OVER = 3;

    // Ort und Name der Atlas - Datei für die Assets der UI
    public static final String TEXTURE_ATLAS_UI = "images/canyonbunny-ui.atlas";
    public static final String SKIN_CANYONBUNNY_UI = "images/canyonbunny-ui.json";

    // LibGDX UI
    public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
    public static final String SKIN_LIBGDX_UI = "images/uiskin.json";

    // GamePrefs Datei
    public static final String PREFERENCES = "canyonbunny.prefs";

    // Anzahl der Karotten
    public static final int CARROTS_SPAWN_MAX = 100;

    // Radius in dem die Karotten spawnen
    public static final float CARROTS_SPAWN_RADIUS = 3.5f;

    // Abschlussverzögerung
    public static final float TIME_DELAY_GAME_FINISHED = 6;
}

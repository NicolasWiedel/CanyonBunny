package de.javadevblog.canyonbunny.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import de.javadevblog.canyonbunny.game.Assets;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransition;
import de.javadevblog.canyonbunny.screens.transitions.ScreenTransitionFade;
import de.javadevblog.canyonbunny.util.AudioManager;
import de.javadevblog.canyonbunny.util.CharacterSkin;
import de.javadevblog.canyonbunny.util.Constants;
import de.javadevblog.canyonbunny.util.GamePreferences;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.math.Interpolation;

public class MenuScreen extends AbstractGameScreen {

    private Stage stage;
    private Skin skinCanyonBunny;
    private Skin skinLibgdx;

    //Menu
    private Image imgBackground;
    private Image imgLogo;
    private Image imginfo;
    private Image imgCoins;
    private Image imgBunny;
    private Button btnMenuPlay;
    private Button btnMenuOptions;

    //Optionen
    private Window winOptions;
    private TextButton btnWinOptSave;
    private TextButton btnWinOptCancel;
    private CheckBox chkSound;
    private Slider sldSound;
    private CheckBox chkMusic;
    private Slider sldMusic;
    private SelectBox<CharacterSkin> selCharSkin;
    private Image imgCharSkin;
    private CheckBox chkShowFpsCounter;
    private CheckBox chkUseMonochromeShader;

    // debug
    private final float DEBUG_REBUILD_INTERVAL = 5.0f;
    private boolean debugEnabled = false;
    private float debugRebuildStage;

    public MenuScreen(DirectedGame game){
        super(game);
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (debugEnabled){
            debugRebuildStage -= deltaTime;
            if(debugRebuildStage <= 0){
                debugRebuildStage = DEBUG_REBUILD_INTERVAL;
                rebuildStage();
            }
        }
        stage.act();
        stage.draw();
        stage.setDebugAll(false);
    }

    private void rebuildStage() {
        skinCanyonBunny = new Skin(
                Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
        skinLibgdx = new Skin(
                Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

        // Aufbau derLayer
        Table layerBackground = buildBackgroundLayer();
        Table layerObjects = buildObjectsLayer();
        Table layerLogos = buildLogosLayer();
        Table layerControls = buildControlsLayer();
        Table layerOptionsWindow = buildOptionsWindowLayer();

        // Stage zusammensetzen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerObjects);
        stack.add(layerLogos);
        stack.add(layerControls);
        stage.addActor(layerOptionsWindow);
    }

    private Table buildBackgroundLayer(){
        Table layer = new Table();
        // + Hintergrund
        imgBackground = new Image(skinCanyonBunny, "background");
        layer.add(imgBackground);
        return layer;
    }

    private Table buildObjectsLayer(){
        Table layer = new Table();
        // + Münzen
        imgCoins = new Image(skinCanyonBunny, "coins");
        layer.add(imgCoins);
//        imgCoins.setPosition(135, 80);
        imgCoins.setOrigin(imgCoins.getWidth() / 2,imgCoins.getHeight() / 2);
        imgCoins.addAction(sequence(moveTo(135, -20),scaleTo(0, 0),fadeOut(0),delay(2.5f),
                parallel(moveBy(0, 100, 0.5f, Interpolation.swingOut),
                        scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
                        alpha(1.0f, 0.5f))));
        // + Bunny
        imgBunny = new Image(skinCanyonBunny, "bunny");
        layer.add(imgBunny);
//        imgBunny.setPosition(455, 40);
        imgBunny.addAction(sequence(moveTo(655, 510), delay(4.0f),
                moveBy(-70, -100, 0.5f, Interpolation.fade),
                moveBy(-100, -50, 0.5f, Interpolation.fade),
                moveBy(-150, -300, 1.0f, Interpolation.elasticIn)));
        return layer;
    }

    private Table buildLogosLayer(){
        Table layer = new Table();
        layer.left().top();
        // + Logo
        imgLogo = new Image(skinCanyonBunny, "logo");
        layer.add(imgLogo);
        layer.row().expandY();
        // + Info Logos
        imginfo = new Image(skinCanyonBunny, "info");
        layer.add(imginfo).bottom();
        if(debugEnabled){
            layer.debug();
        }
        return layer;
    }

    private Table buildControlsLayer(){
        Table layer = new Table();
        layer.right().bottom();
        // + Playbuttom
        btnMenuPlay = new Button(skinCanyonBunny, "play");
        layer.add(btnMenuPlay);
        btnMenuPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        layer.row();
        // + Optionsbutton
        btnMenuOptions = new Button(skinCanyonBunny, "options");
        layer.add(btnMenuOptions);
        btnMenuOptions.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onOptionsClicked();
            }
        });
        if(debugEnabled){
            layer.debug();
        }
        return layer;
    }

    private void onPlayClicked(){
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new GameScreen(game), transition);
    }

    private void onOptionsClicked(){
        loadSettings();
//        btnMenuPlay.setVisible(false);
//        btnMenuPlay.setVisible(false);
//        winOptions.setVisible(true);
        showMenuButtons(false);
        showOptionsWindow(true, true);
    }

    private Table buildOptionsWindowLayer(){
        winOptions = new Window("Options", skinLibgdx);
        // + Audio Settings: Sound/Music CheckBox and Volume Slider
        winOptions.add(buildOptWinAudioSettings()).row();
        // + Character Skin: Selection Box (White, Gray, Brown)
        winOptions.add(buildOptWinSkinSelection()).row();
        // + Debug: Show FPS Counter
        winOptions.add(buildOptWinDebug()).row();
        // + Separator and Buttons (Save, Cancel)
        winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

        // Make options window slightly transparent
        winOptions.setColor(1, 1, 1, 0.8f);
        // Hide options window by default
        showOptionsWindow(false, false);
        winOptions.setVisible(false);
        if (debugEnabled) winOptions.debug();
        // Let TableLayout recalculate widget sizes and positions
        winOptions.pack();
        // Move options window to bottom right corner
        winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
        return winOptions;
    }

    private void loadSettings(){
        GamePreferences prefs = GamePreferences.INSTANCE;
        prefs.load();
        chkSound.setChecked(prefs.sound);
        sldSound.setValue(prefs.volSound);
        chkMusic.setChecked(prefs.music);
        sldMusic.setValue(prefs.volMusic);
        selCharSkin.setSelectedIndex(prefs.charSkin);
        onCharSkinSelected(prefs.charSkin);
        chkShowFpsCounter.setChecked(prefs.showFpsCounter);
        chkUseMonochromeShader.setChecked(prefs.useMonochromeShader);
    }

    private void saveSettings() {
        GamePreferences prefs = GamePreferences.INSTANCE;
        prefs.sound = chkSound.isChecked();
        prefs.volSound = sldSound.getValue();
        prefs.music = chkMusic.isChecked();
        prefs.volMusic = sldMusic.getValue();
        prefs.charSkin = selCharSkin.getSelectedIndex();
        prefs.showFpsCounter = chkShowFpsCounter.isChecked();
        prefs.useMonochromeShader = chkUseMonochromeShader.isChecked();
        prefs.save();
    }

    private void onCharSkinSelected(int index) {
        CharacterSkin skin = CharacterSkin.values()[index];
        imgCharSkin.setColor(skin.getColor());
    }

    private void onSaveClicked() {
        saveSettings();
        onCancelClicked();
        AudioManager.INSTANCE.onSettingsUpdated();
    }

    private void onCancelClicked() {
//        btnMenuPlay.setVisible(true);
//        btnMenuOptions.setVisible(true);
//        winOptions.setVisible(false);
        showMenuButtons(true);
        showOptionsWindow(false, true);
        AudioManager.INSTANCE.onSettingsUpdated();
    }

    private Table buildOptWinAudioSettings(){
        Table tbl = new Table();
        // + Title: Audio
        tbl.pad(10,10,0,10);
        tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        // + Checkbox, "Sound" label, sound volume slider
        chkSound = new CheckBox("", skinLibgdx);
        tbl.add(chkSound);
        tbl.add(new Label("Sound", skinLibgdx));
        sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldSound);
        tbl.row();
        // + Checkbox, "Music" label, music volume slider
        chkMusic = new CheckBox("", skinLibgdx);
        tbl.add(chkMusic);
        tbl.add(new Label("Music", skinLibgdx));
        sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldMusic);
        tbl.row();
        return tbl;
    }

    private Table buildOptWinSkinSelection () {
        Table tbl = new Table();
        // + Title: "Character Skin"
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Character Skin", skinLibgdx,
                "default-font", Color.ORANGE)).colspan(2);
        tbl.row();
        // + Drop down box filled with skin items
        selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);

        selCharSkin.setItems(CharacterSkin.values());

        selCharSkin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onCharSkinSelected(((SelectBox<CharacterSkin>) actor).getSelectedIndex());
            }
        });
        tbl.add(selCharSkin).width(120).padRight(20);
        // + Skin preview image
        imgCharSkin = new Image(Assets.INSTANCE.bunny.head);
        tbl.add(imgCharSkin).width(50).height(50);
        return tbl;
    }

    private Table buildOptWinDebug () {
        Table tbl = new Table();
        // + Title: "Debug"
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        // + Checkbox, "Show FPS Counter" label
        chkShowFpsCounter = new CheckBox("", skinLibgdx);
        tbl.add(new Label("Show FPS Counter", skinLibgdx));
        tbl.add(chkShowFpsCounter);
        tbl.row();
        // + Checkbox, "Use Monochrome Shader" label
        chkUseMonochromeShader = new CheckBox("", skinLibgdx);
        tbl.add(new Label("Use Monochrome Shader", skinLibgdx));
        tbl.add(chkUseMonochromeShader);
        tbl.row();
        return tbl;
    }

    private Table buildOptWinButtons () {
        Table tbl = new Table();
        // + Separator
        Label lbl = null;
        lbl = new Label("", skinLibgdx);
        lbl.setColor(0.75f, 0.75f, 0.75f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
        tbl.row();
        lbl = new Label("", skinLibgdx);
        lbl.setColor(0.5f, 0.5f, 0.5f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
        tbl.row();
        // + Save Button with event handler
        btnWinOptSave = new TextButton("Save", skinLibgdx);
        tbl.add(btnWinOptSave).padRight(30);
        btnWinOptSave.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onSaveClicked();
            }
        });
        // + Cancel Button with event handler
        btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
        tbl.add(btnWinOptCancel);
        btnWinOptCancel.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });
        return tbl;
    }

    private void showMenuButtons (boolean visible) {
        float moveDuration = 1.0f;
        Interpolation moveEasing = Interpolation.swing;
        float delayOptionsButton = 0.25f;

        float moveX = 300 * (visible ? -1 : 1);
        float moveY = 0 * (visible ? -1 : 1);
        final Touchable touchEnabled = visible ? Touchable.enabled
                : Touchable.disabled;
        btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));

        btnMenuOptions.addAction(sequence(delay(delayOptionsButton), moveBy(moveX, moveY, moveDuration, moveEasing)));

        SequenceAction seq = sequence();
        if (visible)
            seq.addAction(delay(delayOptionsButton + moveDuration));
        seq.addAction(run(new Runnable() {
            public void run () {
                btnMenuPlay.setTouchable(touchEnabled);
                btnMenuOptions.setTouchable(touchEnabled);
            }
        }));
        stage.addAction(seq);
    }

    private void showOptionsWindow (boolean visible, boolean animated) {
        float alphaTo = visible ? 0.8f : 0.0f;
        float duration = animated ? 1.0f : 0.0f;
        Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
        winOptions.addAction(sequence(touchable(touchEnabled), alpha(alphaTo, duration)));
        winOptions.setVisible(true);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        rebuildStage();
    }

    @Override
    public void hide() {
        stage.dispose();
        skinCanyonBunny.dispose();
        skinLibgdx.dispose();
    }

    @Override
    public void pause() { }

    @Override
    public InputProcessor getInputProcessor () {
        return stage;
    }
}

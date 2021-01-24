package de.javadevblog.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;
import de.javadevblog.canyonbunny.util.Constants;

public class Assets implements Disposable, AssetErrorListener {

    public static final String TAG = Assets.class.getName();

    public static final Assets INSTANCE = new Assets();

    private AssetManager assetManager;

    public AssetBunny bunny;
    public AssetRock rock;
    public AssetGoldCoin goldCoin;
    public AssetFeather feather;
    public AssetLevelDecoration levelDecoration;
    public AssetFonts fonts;

    // Singleton Konstruktor
    private Assets(){}

    public void init(AssetManager assetManager){
        this.assetManager = assetManager;

        // Error Handler
        assetManager.setErrorListener(this);
        // lade Texture Atlas
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
        // bis zum Ende des Ladevorgangs warten
        assetManager.finishLoading();

        Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames()){
            Gdx.app.debug(TAG, "asset: " + a);
        }

        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

        // texture filtering
        for (Texture t : atlas.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        // Game Rescourcen erstellen
        fonts = new AssetFonts();
        bunny = new AssetBunny(atlas);
        rock = new AssetRock(atlas);
        goldCoin = new AssetGoldCoin(atlas);
        feather = new AssetFeather(atlas);
        levelDecoration = new AssetLevelDecoration(atlas);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        fonts.defaultSmall.dispose();
        fonts.defaultNormal.dispose();
        fonts.defaultBig.dispose();
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Could not load asset: " + asset.fileName, (Exception) throwable);
    }

    // innere Klassen

    public class AssetBunny {

        public final AtlasRegion head;

        public AssetBunny(TextureAtlas atlas){
            head = atlas.findRegion("bunny_head");
        }
    }

    public class AssetRock {

        public final AtlasRegion edge;
        public final AtlasRegion middle;

        public AssetRock(TextureAtlas atlas){
            edge = atlas.findRegion("rock_edge");
            middle = atlas.findRegion("rock_middle");
        }
    }

    public class AssetGoldCoin {

        public final AtlasRegion goldCoin;

        public AssetGoldCoin(TextureAtlas atlas){
            goldCoin = atlas.findRegion("item_gold_coin");
        }
    }

    public class AssetFeather {

        public final AtlasRegion feather;

        public AssetFeather(TextureAtlas atlas) {
            feather = atlas.findRegion("item_feather");
        }
    }

    public class AssetLevelDecoration {

        public final AtlasRegion cloud01;
        public final AtlasRegion cloud02;
        public final AtlasRegion cloud03;
        public final AtlasRegion mountainLeft;
        public final AtlasRegion mountainRight;
        public final AtlasRegion waterOverlay;

        public AssetLevelDecoration(TextureAtlas atlas) {
            cloud01 = atlas.findRegion("cloud01");
            cloud02 = atlas.findRegion("cloud02");
            cloud03 = atlas.findRegion("cloud03");
            mountainLeft = atlas.findRegion("mountain_left");
            mountainRight = atlas.findRegion("mountain_right");
            waterOverlay = atlas.findRegion("water_overlay");
        }
    }

    public class AssetFonts {

        public final BitmapFont defaultSmall;
        public final BitmapFont defaultNormal;
        public final BitmapFont defaultBig;

        public AssetFonts(){
            // drei verschieden Fonts kreieren
            defaultSmall = new BitmapFont(Gdx.files.internal(
                    "images/arial-15.fnt"), true);
            defaultNormal = new BitmapFont(Gdx.files.internal(
                    "images/arial-15.fnt"), true);
            defaultBig = new BitmapFont(Gdx.files.internal(
                    "images/arial-15.fnt"), true);
            //Größe setzen
            defaultSmall.getData().setScale(0.75f);
            defaultNormal.getData().setScale(1.0f);
            defaultBig.getData().setScale(2.0f);
            // linear texture filtering für weichere Darstellung
            defaultSmall.getRegion().getTexture().setFilter(
                    Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultNormal.getRegion().getTexture().setFilter(
                    Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultBig.getRegion().getTexture().setFilter(
                    Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        }
    }
}

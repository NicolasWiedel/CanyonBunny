package de.wiedel.cb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import de.wiedel.cb.utils.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Assets implements Disposable, AssetErrorListener {

    /** eindeutiger Bezeichner der Klasse */
    public static final String TAG = Assets.class.getSimpleName();

    /** Singleton Instanz */
    public static final Assets INSTANCE = new Assets();

    /** Verweis auf den AssetManager */
    private AssetManager assetManager;

    /** Instanzen der inneren Asset Klassen */
    public AssetBunny bunny;
    public AssetRock rock;
    public AssetGoldCoin goldCoin;
    public AssetFeather feather;
    public AssetLevelDecoration levelDecoration;

    private Assets(){}

    public void init(AssetManager assetManager){
        this.assetManager = assetManager;
        assetManager.setErrorListener(this);

        // lade die notwendigen Assets
        assetManager.load(Constants.CANYONBUNNY_ATLAS, TextureAtlas.class);
        // warten bis das Laden beendet ist
        assetManager.finishLoading();

        Gdx.app.debug(TAG, "# der Assets die geladen wurden: " + assetManager.getAssetNames().size);
        for (String asset : assetManager.getAssetNames()){
            Gdx.app.debug(TAG, "Assetname: " + asset);
        }

        TextureAtlas atlas = assetManager.get(Constants.CANYONBUNNY_ATLAS);

        // Texture filtering aktivieren. Ergibt weichere Pixel
        for (Texture texture : atlas.getTextures()){
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        // Die Spieleressourcen instanziieren
        bunny = new AssetBunny(atlas);
        rock = new AssetRock(atlas);
        goldCoin = new AssetGoldCoin(atlas);
        feather = new AssetFeather(atlas);
        levelDecoration = new AssetLevelDecoration(atlas);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    public void error(String filename, Class type,Throwable throwable){
        Gdx.app.error(TAG, "Kann die Datei nicht laden: " + filename, (Exception)throwable);
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "kann die Datei nicht laden: " + asset.fileName, (Exception)throwable);
    }

    /** innere Klassen für alle Assets aus dem Atlas */
    public class AssetBunny{
        public final AtlasRegion head;

        public AssetBunny(TextureAtlas atlas){
            head = atlas.findRegion("bunny_head");
        }
    }
    public class AssetRock{
        public final AtlasRegion edge;
        public final AtlasRegion middle;

        public AssetRock(TextureAtlas atlas){
            edge = atlas.findRegion("rock_edge");
            middle = atlas.findRegion("rock_middle");
        }
    }
    public class AssetGoldCoin{
        public final AtlasRegion goldCoin;

        public AssetGoldCoin(TextureAtlas atlas){
            goldCoin = atlas.findRegion("item_gold_coin");
        }
    }
    public class AssetFeather{
        public final AtlasRegion feather;

        public AssetFeather(TextureAtlas atlas){
            feather = atlas.findRegion("item_feather");
        }
    }
    public class AssetLevelDecoration{
        public final AtlasRegion cloud01;
        public final AtlasRegion cloud02;
        public final AtlasRegion cloud03;
        public final AtlasRegion mountainLeft;
        public final AtlasRegion mountainRight;
        public final AtlasRegion waterOverlay;

        public AssetLevelDecoration(TextureAtlas atlas){
            cloud01 = atlas.findRegion("cloud01");
            cloud02 = atlas.findRegion("cloud02");
            cloud03 = atlas.findRegion("cloud03");
            mountainLeft = atlas.findRegion("mountain_left");
            mountainRight = atlas.findRegion("mountain_right");
            waterOverlay = atlas.findRegion("water_overlay");
        }
    }
}

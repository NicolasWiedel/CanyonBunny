package de.javadevblog.canyonbunny.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    public static final AudioManager INSTANCE = new AudioManager();

    private Music playingMusic;

    // singleton
    private AudioManager () { }

    public void play(Sound sound){
        play(sound, 1);
    }

    public void play(Sound sound, float volume){
        play(sound, volume, 1);
    }

    public void play (Sound sound, float volume, float pitch) {
        play(sound, volume, pitch, 0);
    }

    public void play (Sound sound, float volume, float pitch, float pan) {
        if (!GamePreferences.INSTANCE.sound) return;
        sound.play(GamePreferences.INSTANCE.volSound * volume, pitch, pan);
    }

    public void play (Music music) {
        stopMusic();
        playingMusic = music;
        if (GamePreferences.INSTANCE.music) {
            music.setLooping(true);
            music.setVolume(GamePreferences.INSTANCE.volMusic);
            music.play();
        }
    }

    public void stopMusic () {
        if (playingMusic != null) {
            playingMusic.stop();
        }
    }

    public void onSettingsUpdated () {
        if (playingMusic == null) {
            return;
        }
        playingMusic.setVolume(GamePreferences.INSTANCE.volMusic);
        if (GamePreferences.INSTANCE.music) {
            if (!playingMusic.isPlaying()) playingMusic.play();
        } else {
            playingMusic.pause();
        }
    }
}

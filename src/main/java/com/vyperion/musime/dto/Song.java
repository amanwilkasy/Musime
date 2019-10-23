package com.vyperion.musime.dto;
    /*
    artist
    playlist
            "acousticness": 0.201,
        "analysisUrl": "https://api.spotify.com/v1/audio-analysis/27a1mYSG5tYg7dmEjWBcmL",
        "danceability": 0.702,
        "durationMs": 198408,
        "energy": 0.708,
        "id": "27a1mYSG5tYg7dmEjWBcmL",
        "instrumentalness": 0.0,
        "key": 10,
        "liveness": 0.118,
        "loudness": -5.366,
        "mode": "MINOR",
        "speechiness": 0.0547,
        "tempo": 147.98,
        "timeSignature": 4,
        "trackHref": "https://api.spotify.com/v1/tracks/27a1mYSG5tYg7dmEjWBcmL",
        "type": "AUDIO_FEATURES",
        "uri": "spotify:track:27a1mYSG5tYg7dmEjWBcmL",
        "valence": 0.713
     */

import com.wrapper.spotify.enums.Modality;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    private String id;
    private ArtistSimplified[] artists;
    private String songName;
    private Modality mode;
    private int popularity;
    private float acousticness;
    private float danceability;
    private float durationMs;
    private float energy;
    private float instrumentalness;
    private float key;
    private float liveness;
    private float loudness;
    private float speechiness;
    private float tempo;
    private float valence;

    public Song(Track track, AudioFeatures audioFeature) {
        this.id = track.getId();
        this.artists = track.getArtists();
        this.songName = track.getName();
        this.mode = audioFeature.getMode();
        this.acousticness = audioFeature.getAcousticness();
        this.danceability = audioFeature.getDanceability();
        this.durationMs = audioFeature.getDurationMs();
        this.energy = audioFeature.getEnergy();
        this.instrumentalness = audioFeature.getInstrumentalness();
        this.key = audioFeature.getKey();
        this.liveness = audioFeature.getLiveness();
        this.loudness = audioFeature.getLoudness();
        this.speechiness = audioFeature.getSpeechiness();
        this.tempo = audioFeature.getTempo();
        this.valence = audioFeature.getValence();
        this.popularity = track.getPopularity();

    }
}









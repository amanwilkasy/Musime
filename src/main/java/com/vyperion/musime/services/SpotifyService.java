package com.vyperion.musime.services;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
import com.vyperion.musime.dto.FeaturesGraph;
import com.vyperion.musime.dto.Song;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsTracksRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SpotifyService {

    private final SpotifyApi spotifyApi;

    public SpotifyService(@Qualifier("SpotifyApi") SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public User getCurrentUser() {
        Optional<User> user = Optional.empty();
        try {
            user = Optional.ofNullable(spotifyApi.getCurrentUsersProfile().build().execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:5 " + e.getMessage());
        }
        return user.orElseThrow(RuntimeException::new);
    }

    public List<PlaylistSimplified> getAllPlaylists() {
        List<PlaylistSimplified> playlists = new LinkedList<>();
        Optional<Paging<PlaylistSimplified>> playlistPage;
        final int maxLimit = 50;
        int offset = 0;
        try {
            playlistPage = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists().limit(maxLimit).build().execute());
            playlistPage.ifPresent(playlistPaging -> playlists.addAll(Arrays.asList(playlistPaging.getItems())));

            while (playlistPage.isPresent() && playlistPage.get().getNext() != null) {
                throttle(1, "getAllPlaylists");

                offset += playlistPage.get().getLimit();

                playlistPage = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists()
                        .offset(offset).limit(maxLimit).build().execute());
                playlistPage.ifPresent(playlistPaging -> playlists.addAll(Arrays.asList(playlistPaging.getItems())));
            }
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:1 " + e.getMessage());
        }
//        List<PlaylistSimplified> tempForTest = new ArrayList<>(playlists.subList(0, 10));
        return playlists;
    }

    //need to workout paging
    //37i9dQZF1DWUgX5cUT0GbU
    public List<PlaylistTrack> getTracksFromPlaylist(String playlistId) {
        List<PlaylistTrack> playlistTrack = new ArrayList<>();
        Optional<Paging<PlaylistTrack>> tracksPage;
        int offset = 0;
        int maxLimit = 100;
        try {
            tracksPage = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
            tracksPage.ifPresent(playlistTrackPaging -> playlistTrack.addAll(Arrays.asList(playlistTrackPaging.getItems())));

            while (tracksPage.isPresent() && tracksPage.get().getNext() != null) {
                throttle(1, "getTracksFromPlaylist");

                offset += tracksPage.get().getLimit();

                tracksPage = Optional.ofNullable(spotifyApi.getPlaylistsTracks(playlistId).offset(offset).limit(maxLimit)
                        .market(CountryCode.US).build().execute());

                tracksPage.ifPresent(playlistTrackPaging -> playlistTrack.addAll(Arrays.asList(playlistTrackPaging.getItems())));
            }
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:2 " + e.getMessage());
        }
        return playlistTrack;
    }

    public List<PlaylistTrack> getAllTracksFromAllPlaylists() {
        List<PlaylistTrack> allTracksMap = new ArrayList<>();
        getAllPlaylists().forEach(playlistSimplified -> allTracksMap.addAll(getTracksFromPlaylist(playlistSimplified.getId())));
        return allTracksMap;
    }


    public List<Song> getAllAudioFeaturesFromPlaylist(String id) {
        final int batchSize = 100;
        Map<String, PlaylistTrack> allTracks = convertPlaylistToMap(getTracksFromPlaylist(id));
        List<Song> allSongs = new ArrayList<>();
        Lists.partition(new ArrayList<>(allTracks.keySet()), batchSize).forEach(batch -> {
            AudioFeatures[] audioFeatures = getAudioFeaturesForSeveralTracks(batch.toArray(new String[0]));
            for (AudioFeatures features : audioFeatures) {
                if (features != null && allTracks.containsKey(features.getId())) {
                    allSongs.add(new Song(allTracks.get(features.getId()).getTrack(), features));
                }
            }
        });
        return allSongs;
    }

    public List<Song> getAllAudioFeaturesForAllSongs() {
        List<Song> allSongs = new ArrayList<>();
        getAllPlaylists().forEach(playlist -> allSongs.addAll(getAllAudioFeaturesFromPlaylist(playlist.getId())));
        return allSongs;
    }

    public List<FeaturesGraph> generateFeaturesGraphForAllSongs() {
        return convertSongsToFeaturesGraph(getAllAudioFeaturesForAllSongs());
    }




    public AudioFeatures[] getAudioFeaturesForSeveralTracks(String[] ids) {
        Optional<AudioFeatures[]> audioFeatures = Optional.empty();
        try {
            throttle(1, "getAudioFeaturesForSeveralTracks");
            audioFeatures = Optional.ofNullable(spotifyApi.getAudioFeaturesForSeveralTracks(ids).build().execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:4 " + e.getMessage());
        }

        return audioFeatures.orElse(new AudioFeatures[0]);
    }

    private GetPlaylistsTracksRequest getTracksFromPlaylistRequest(String playlistId) {
//      .fields("track") .limit(10) .offset(0) .market(CountryCode.SE)
        return spotifyApi.getPlaylistsTracks(playlistId).limit(100).market(CountryCode.US).build();
    }

    private static Map<String, PlaylistTrack> convertPlaylistToMap(List<PlaylistTrack> playlistTrackList) {
        Map<String, PlaylistTrack> map = new HashMap<>();
        playlistTrackList.forEach(playlistTrack -> {
            map.put(playlistTrack.getTrack().getId(), playlistTrack);
        });
        return map;
    }

    private static void throttle(int seconds, String message) {
        try {
            Thread.sleep(seconds * 1000);
            System.out.println(message.concat(" is waiting ".concat(String.valueOf(seconds))));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("System got interrupted in thread sleep");
            System.exit(0);
        }
    }


    private static TreeMap<Integer, Integer> consolidate(TreeMap<Integer, Integer> map) {
        int subarray = map.size() < 9 ? 1 : map.size() / 9;
        int mapCount = 0;
        int graphCount = 0;
        TreeMap<Integer, Integer> graph = new TreeMap<>();
        List<List<Integer>> batch = Lists.partition(new ArrayList<>(map.keySet()), subarray);

        for (List<Integer> s : batch) {
            int startOfBatch = s.get(0);
            int maxOfBatch = s.get(s.size() - 1);
            for (Map.Entry<Integer, Integer> entry : map.subMap(startOfBatch, true, maxOfBatch, true).entrySet()) {
                Integer value = entry.getValue();
                mapCount += value;
                if (!graph.containsKey(startOfBatch)) {
                    graph.put(startOfBatch, value);
                } else if (graph.containsKey(startOfBatch)) {
                    graph.put(startOfBatch, graph.get(startOfBatch) + value);
                }
            }
        }
        for (Map.Entry<Integer, Integer> some : graph.entrySet()) {
            graphCount += some.getValue();
        }
        if (mapCount != graphCount) {
            throw new RuntimeException("counts not equal");
        }
        return graph;
    }

    private List<FeaturesGraph> convertSongsToFeaturesGraph(List<Song> songs) {
        String userId = getCurrentUser().getId();
        List<FeaturesGraph> consolidated = new ArrayList<>();
        TreeMap<Integer, Integer> getPopularityMap = new TreeMap<>();
        TreeMap<Integer, Integer> getEnergyMap = new TreeMap<>();
        TreeMap<Integer, Integer> getInstrumentalnessMap = new TreeMap<>();
        TreeMap<Integer, Integer> getKeyMap = new TreeMap<>();
        TreeMap<Integer, Integer> getLivenessMap = new TreeMap<>();
        TreeMap<Integer, Integer> getLoudnessMap = new TreeMap<>();
        TreeMap<Integer, Integer> getSpeechinessMap = new TreeMap<>();
        TreeMap<Integer, Integer> getValenceMap = new TreeMap<>();
        TreeMap<Integer, Integer> getTempoMap = new TreeMap<>();
        TreeMap<Integer, Integer> getDurationMsMap = new TreeMap<>();
        TreeMap<Integer, Integer> getAcousticnessMap = new TreeMap<>();
        TreeMap<Integer, Integer> getDanceabilityMap = new TreeMap<>();

        songs.forEach(song -> {
            int getPopularityMapKey = song.getPopularity();
            int getAcousticnessMapKey = Math.round(song.getAcousticness() * 100);
            int getDanceabilityMapKey = Math.round(song.getDanceability() * 10);
            int getDurationMsMapKey = Math.round(song.getDurationMs() / 10);
            int getEnergyMapKey = Math.round(song.getEnergy() * 100);
            int getInstrumentalnessMapKey = Math.round(song.getInstrumentalness() * 100);
            int getKeyMapKey = (int) song.getKey();
            int getLivenessMapKey = Math.round(song.getLiveness() * 100);
            int getLoudnessMapKey = Math.round(song.getLoudness() * -100);
            int getSpeechinessMapKey = Math.round(song.getSpeechiness() * 100);
            int getTempoMapKey = (int) song.getTempo();
            int getValenceMapKey = Math.round(song.getValence() * 100);
            getPopularityMap.put(getPopularityMapKey, getPopularityMap.containsKey(getPopularityMapKey) ? getPopularityMap.get(getPopularityMapKey) + 1 : 1);
            getEnergyMap.put(getEnergyMapKey, getEnergyMap.containsKey(getEnergyMapKey) ? getEnergyMap.get(getEnergyMapKey) + 1 : 1);
            getInstrumentalnessMap.put(getInstrumentalnessMapKey, getInstrumentalnessMap.containsKey(getInstrumentalnessMapKey) ? getInstrumentalnessMap.get(getInstrumentalnessMapKey) + 1 : 1);
            getKeyMap.put(getKeyMapKey, getKeyMap.containsKey(getKeyMapKey) ? getKeyMap.get(getKeyMapKey) + 1 : 1);
            getLivenessMap.put(getLivenessMapKey, getLivenessMap.containsKey(getLivenessMapKey) ? getLivenessMap.get(getLivenessMapKey) + 1 : 1);
            getLoudnessMap.put(getLoudnessMapKey, getLoudnessMap.containsKey(getLoudnessMapKey) ? getLoudnessMap.get(getLoudnessMapKey) + 1 : 1);
            getSpeechinessMap.put(getSpeechinessMapKey, getSpeechinessMap.containsKey(getSpeechinessMapKey) ? getSpeechinessMap.get(getSpeechinessMapKey) + 1 : 1);
            getValenceMap.put(getValenceMapKey, getValenceMap.containsKey(getValenceMapKey) ? getValenceMap.get(getValenceMapKey) + 1 : 1);
            getTempoMap.put(getTempoMapKey, getTempoMap.containsKey(getTempoMapKey) ? getTempoMap.get(getTempoMapKey) + 1 : 1);
            getDurationMsMap.put(getDurationMsMapKey, getDurationMsMap.containsKey(getDurationMsMapKey) ? getDurationMsMap.get(getDurationMsMapKey) + 1 : 1);
            getAcousticnessMap.put(getAcousticnessMapKey, getAcousticnessMap.containsKey(getAcousticnessMapKey) ? getAcousticnessMap.get(getAcousticnessMapKey) + 1 : 1);
            getDanceabilityMap.put(getDanceabilityMapKey, getDanceabilityMap.containsKey(getDanceabilityMapKey) ? getDanceabilityMap.get(getDanceabilityMapKey) + 1 : 1);

        });


        consolidated.add(new FeaturesGraph(userId,"Tempo", tempoDescription, consolidate(getTempoMap)));
        consolidated.add(new FeaturesGraph(userId,"Popularity", popularityDescription, consolidate(getPopularityMap)));
        consolidated.add(new FeaturesGraph(userId,"Energy", energyDescription, consolidate(getEnergyMap)));
        consolidated.add(new FeaturesGraph(userId,"Acousticness", acousticnessDescription, consolidate(getAcousticnessMap)));
        consolidated.add(new FeaturesGraph(userId,"Key", keyDescription, consolidate(getKeyMap)));
        consolidated.add(new FeaturesGraph(userId,"Valence", valanceDescription, consolidate(getValenceMap)));
        consolidated.add(new FeaturesGraph(userId,"Liveness", livenessDescription, consolidate(getLivenessMap)));
        consolidated.add(new FeaturesGraph(userId,"Instrumentalness", instrumentalnessDescription, consolidate(getInstrumentalnessMap)));
        consolidated.add(new FeaturesGraph(userId,"Loudness", loudnessDescription, consolidate(getLoudnessMap)));
        consolidated.add(new FeaturesGraph(userId,"Speechiness", speechinessDescription, consolidate(getSpeechinessMap)));
        consolidated.add(new FeaturesGraph(userId,"Danceability", danceabilityDescription, consolidate(getDanceabilityMap)));
        consolidated.add(new FeaturesGraph(userId,"Duration", durationDescription, consolidate(getDurationMsMap)));
        return consolidated;
    }

    private static final String instrumentalnessDescription = "Predicts whether a track contains no vocals. “Ooh” and “aah” sounds are treated as instrumental in this context. Rap or spoken word tracks are clearly “vocal”. The closer the instrumentalness value is to 1.0, the greater likelihood the track contains no vocal content. Values above 0.5 are intended to represent instrumental tracks, but confidence is higher as the value approaches 1.0.";
    private static final String acousticnessDescription = " A confidence measure from 0.0 to 1.0 of whether the track is acoustic. 1.0 represents high confidence the track is acoustic.";
    private static final String livenessDescription = "Detects the presence of an audience in the recording. Higher liveness values represent an increased probability that the track was performed live. A value above 0.8 provides strong likelihood that the track is live.";
    private static final String energyDescription = "Energy is a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity. Typically, energetic tracks feel fast, loud, and noisy. For example, death metal has high energy, while a Bach prelude scores low on the scale. Perceptual features contributing to this attribute include dynamic range, perceived loudness, timbre, onset rate, and general entropy.";
    private static final String danceabilityDescription = "Danceability describes how suitable a track is for dancing based on a combination of musical elements including tempo, rhythm stability, beat strength, and overall regularity. A value of 0.0 is least danceable and 1.0 is most danceable.";
    private static final String valanceDescription = "A measure from 0.0 to 1.0 describing the musical positiveness conveyed by a track. Tracks with high valence sound more positive (e.g. happy, cheerful, euphoric), while tracks with low valence sound more negative (e.g. sad, depressed, angry).";
    private static final String durationDescription = "The duration of the track in milliseconds";
    private static final String keyDescription = "The estimated overall key of the track. Integers map to pitches using standard Pitch Class notation.";
    private static final String loudnessDescription = "The overall loudness of a track in decibels (dB). Loudness values are averaged across the entire track and are useful for comparing relative loudness of tracks. Loudness is the quality of a sound that is the primary psychological correlate of physical strength (amplitude). Values typical range between -60 and 0 db.";
    private static final String speechinessDescription = "Speechiness detects the presence of spoken words in a track. The more exclusively speech-like the recording (e.g. talk show, audio book, poetry), the closer to 1.0 the attribute value. Values above 0.66 describe tracks that are probably made entirely of spoken words. Values between 0.33 and 0.66 describe tracks that may contain both music and speech, either in sections or layered, including such cases as rap music. Values below 0.33 most likely represent music and other non-speech-like tracks.";
    private static final String tempoDescription = "The overall estimated tempo of a track in beats per minute (BPM). In musical terminology, tempo is the speed or pace of a given piece and derives directly from the average beat duration. ";
    private static final String popularityDescription = "The popularity of a track is a value between 0 and 100, with 100 being the most popular. The popularity is calculated by algorithm and is based, in the most part, on the total number of plays the track has had and how recent those plays are.\n" +
            "Generally speaking, songs that are being played a lot now will have a higher popularity than songs that were played a lot in the past.";


}

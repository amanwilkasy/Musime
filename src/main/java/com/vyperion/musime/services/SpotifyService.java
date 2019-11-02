package com.vyperion.musime.services;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
import com.vyperion.musime.dto.FeatureDescriptions;
import com.vyperion.musime.dto.FeaturesGraph;
import com.vyperion.musime.dto.Song;
import com.vyperion.musime.dto.UserFeatureGraph;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsTracksRequest;
import org.hibernate.internal.util.SerializationHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
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
                throttle("getAllPlaylists");

                offset += playlistPage.get().getLimit();

                playlistPage = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists()
                        .offset(offset).limit(maxLimit).build().execute());
                playlistPage.ifPresent(playlistPaging -> playlists.addAll(Arrays.asList(playlistPaging.getItems())));
            }
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:1 " + e.getMessage());
        }
        return playlists;
    }

    public List<PlaylistTrack> getTracksFromPlaylist(String playlistId) {
        List<PlaylistTrack> playlistTrack = new ArrayList<>();
        Optional<Paging<PlaylistTrack>> tracksPage;
        int offset = 0;
        int maxLimit = 100;
        try {
            tracksPage = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
            tracksPage.ifPresent(playlistTrackPaging -> playlistTrack.addAll(Arrays.asList(playlistTrackPaging.getItems())));

            while (tracksPage.isPresent() && tracksPage.get().getNext() != null) {
                throttle("getTracksFromPlaylist");

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

    public UserFeatureGraph generateFeaturesGraphForAllSongs() {
        return convertSongsToUserFeatureGraph(getAllAudioFeaturesForAllSongs());
    }


    public AudioFeatures[] getAudioFeaturesForSeveralTracks(String[] ids) {
        Optional<AudioFeatures[]> audioFeatures = Optional.empty();
        try {
            throttle("getAudioFeaturesForSeveralTracks");
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
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            if (playlistTrack != null){
                map.put(playlistTrack.getTrack().getId(), playlistTrack);
            }
        }
        return map;
    }

    private static void throttle(String message) {
        try {
            Thread.sleep(1000);
            System.out.println(message.concat(" is waiting ".concat(String.valueOf(1))));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("System got interrupted in thread sleep");
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

    private UserFeatureGraph convertSongsToUserFeatureGraph(List<Song> songs) {
        User user = getCurrentUser();
        List<FeaturesGraph> features = new ArrayList<>();
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
            int getDurationMsMapKey = Math.round(song.getDurationMs() / 1000);
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

        features.add(new FeaturesGraph("Tempo", FeatureDescriptions.tempo, consolidate(getTempoMap)));
        features.add(new FeaturesGraph("Popularity", FeatureDescriptions.popularity, consolidate(getPopularityMap)));
        features.add(new FeaturesGraph("Energy", FeatureDescriptions.energy, consolidate(getEnergyMap)));
        features.add(new FeaturesGraph("Acousticness", FeatureDescriptions.acousticness, consolidate(getAcousticnessMap)));
        features.add(new FeaturesGraph("Key", FeatureDescriptions.key, consolidate(getKeyMap)));
        features.add(new FeaturesGraph("Valence", FeatureDescriptions.valance, consolidate(getValenceMap)));
        features.add(new FeaturesGraph("Liveness", FeatureDescriptions.liveness, consolidate(getLivenessMap)));
        features.add(new FeaturesGraph("Instrumentalness", FeatureDescriptions.instrumentalness, consolidate(getInstrumentalnessMap)));
        features.add(new FeaturesGraph("Loudness", FeatureDescriptions.loudness, consolidate(getLoudnessMap)));
        features.add(new FeaturesGraph("Speechiness", FeatureDescriptions.speechiness, consolidate(getSpeechinessMap)));
        features.add(new FeaturesGraph("Danceability", FeatureDescriptions.danceability, consolidate(getDanceabilityMap)));
        features.add(new FeaturesGraph("Duration", FeatureDescriptions.duration, consolidate(getDurationMsMap)));

        return new UserFeatureGraph(user.getId(), user.getEmail(), SerializationHelper.serialize((Serializable) features));

    }

}

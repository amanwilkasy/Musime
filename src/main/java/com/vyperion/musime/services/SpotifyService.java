package com.vyperion.musime.services;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
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
        throttle(1, "getAllPlaylists");
        try {
            playlistPage = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists().limit(maxLimit).build().execute());
            playlistPage.ifPresent(playlistPaging -> playlists.addAll(Arrays.asList(playlistPaging.getItems())));

            while (playlistPage.isPresent() && playlistPage.get().getNext() != null) {
                throttle(1, "getAllPlaylists");

//                playlists.addAll(Arrays.asList(playlistPage.get().getItems()));
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

    //need to workout paging
    //37i9dQZF1DWUgX5cUT0GbU
    public List<PlaylistTrack> getTracksFromPlaylist(String playlistId) {
        List<PlaylistTrack> playlistTrack = new ArrayList<>();
        Optional<Paging<PlaylistTrack>> tracksPage;
        int offset = 0;
        int maxLimit = 100;
        throttle(1, "getTracksFromPlaylist");
        try {
            tracksPage = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
            tracksPage.ifPresent(playlistTrackPaging -> playlistTrack.addAll(Arrays.asList(playlistTrackPaging.getItems())));

            while (tracksPage.isPresent() && tracksPage.get().getNext() != null) {
                throttle(1, "getTracksFromPlaylist");
                System.out.println("###");

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
        Map<String, PlaylistTrack> allTracks = convertPlaylistToMap(getTracksFromPlaylist(id));
        List<Song> allSongs = new ArrayList<>();
        throttle(1, "getAllAudioFeaturesFromPlaylist");
        Lists.partition(new ArrayList<>(allTracks.keySet()), 10).forEach(batch -> {
            throttle(1, "getAllAudioFeaturesFromPlaylist");
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

    public AudioFeatures[] getAudioFeaturesForSeveralTracks(String[] ids) {
        Optional<AudioFeatures[]> audioFeatures = Optional.empty();
        try {
            throttle(1, "getAudioFeaturesForSeveralTracks");
            audioFeatures = Optional.ofNullable(spotifyApi.getAudioFeaturesForSeveralTracks(ids).build().execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:4 " + e.getMessage());
        }
        return audioFeatures.orElse(null);
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
}

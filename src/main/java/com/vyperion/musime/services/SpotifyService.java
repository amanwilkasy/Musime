package com.vyperion.musime.services;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
import com.vyperion.musime.dto.Song;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
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

    public List<PlaylistSimplified> getAllPlaylistss() {
        List<PlaylistSimplified> playlists = new LinkedList<>();
        Optional<Paging<PlaylistSimplified>> playlistPage;
        final int maxLimit = 50;
        int offset = 0;
        try {
            playlistPage = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists().limit(maxLimit).build().execute());

            while (playlistPage.isPresent() && playlistPage.get().getNext() != null) {
                throttle(1, "new playlist get");
                playlists.addAll(Arrays.asList(playlistPage.get().getItems()));
                offset += playlistPage.get().getLimit();


                playlistPage = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists()
                        .offset(offset).limit(maxLimit).build().execute());

                if (playlistPage.isPresent()) {
                    playlistPage.ifPresent(playlistPaging -> playlists.addAll(Arrays.asList(playlistPaging.getItems())));
                }
            }


        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:1 " + e.getMessage());
        }
        return playlists;
    }

    public List<PlaylistSimplified> getAllPlaylists() {
        List<PlaylistSimplified> playlists = new LinkedList<>();
        Optional<Paging<PlaylistSimplified>> playlistPage;
        final int maxLimit = 50;
        try {
            playlistPage = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists().
                    limit(maxLimit).build().execute());
            if (playlistPage.isPresent()) {
                playlists.addAll(Arrays.asList(playlistPage.get().getItems()));
                while (playlistPage.isPresent() && Optional.ofNullable(playlistPage.get().getNext()).isPresent()) {
                    throttle(3, "getAllPlaylists");
                    playlistPage = Optional.ofNullable(
                            getUsersPlaylistsRequest(
                                    playlistPage.get().getOffset(),
                                    playlistPage.get().getLimit())
                                    .execute());
                    playlistPage.ifPresent(playlistPaging -> playlists.addAll(Arrays.asList(playlistPaging.getItems())));
                }
            }

        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:1 " + e.getMessage());
        }
        return playlists;
    }

    //37i9dQZF1DWUgX5cUT0GbU
    public Map<String, PlaylistTrack> getTracksFromPlaylistMap(String playlistId) {
        Map<String, PlaylistTrack> playlistTrackMap = new HashMap<>();
        Optional<Paging<PlaylistTrack>> tracksPage;
        throttle(1, "getTracksFromPlaylistMap");
        try {
            tracksPage = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
            tracksPage.ifPresent(playlistTrackPaging -> playlistTrackMap.putAll(convertPlaylistToMap(playlistTrackPaging.getItems())));
//            if (tracksPage.isPresent()) {
//                playlistTrackMap.putAll(convertPlaylistToMap(tracksPage.get().getItems()));
//                while (tracksPage.isPresent() && Optional.ofNullable(tracksPage.get().getNext()).isPresent()) {
//                    System.out.println("next is avilable ".concat(tracksPage.get().getNext()));
//                    tracksPage = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
//                    tracksPage.ifPresent(playlistTrackPaging -> playlistTrackMap.putAll(convertPlaylistToMap(playlistTrackPaging.getItems())));
//                }
//            }
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:2 " + e.getMessage());
        }
        return playlistTrackMap;
    }

    public Map<String, PlaylistTrack> getAllTracksFromAllPlaylists() {
        List<PlaylistSimplified> allPlaylists = getAllPlaylists();
        Map<String, PlaylistTrack> allTracksMap = new HashMap<>();
        for (PlaylistSimplified playlistSimplified : allPlaylists) {
            allTracksMap.putAll(getTracksFromPlaylistMap(playlistSimplified.getId()));
        }
        return allTracksMap;
    }

    //37i9dQZF1DWUgX5cUT0GbU
    public List<PlaylistTrack> getTracksFromPlaylistList(String playlistId) {
        List<PlaylistTrack> tracksList = new LinkedList<>();
        Optional<Paging<PlaylistTrack>> tracksPage;
        try {
            tracksPage = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
            if (tracksPage.isPresent()) {
                tracksList.addAll(Arrays.asList(tracksPage.get().getItems()));
                while (tracksPage.isPresent() && Optional.ofNullable(tracksPage.get().getNext()).isPresent()) {
                    throttle(3, "getTracksFromPlaylist");
                    tracksPage = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
                    tracksPage.ifPresent(tracksPaging -> tracksList.addAll(Arrays.asList(tracksPaging.getItems())));
                }
            }
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:2 " + e.getMessage());
        }
        return tracksList;
    }


    public List<Song> getAllAudioFeaturesFromPlaylist(String id) {
        Map<String, PlaylistTrack> allTracks = getTracksFromPlaylistMap(id);
        List<List<String>> allTrackIdsList = Lists.partition(new ArrayList<>(allTracks.keySet()), 10);
        List<Song> allSongs = new ArrayList<>();
        for (List<String> batch : allTrackIdsList) {
            AudioFeatures[] audioFeatures = getAudioFeaturesForSeveralTracks(batch.toArray(new String[0]));
            for (AudioFeatures features : audioFeatures) {
                if (features != null && allTracks.containsKey(features.getId())) {
                    allSongs.add(new Song(allTracks.get(features.getId()).getTrack(), features));
                }
            }
        }
        return allSongs;
    }

    public List<Song> getAllAudioFeaturesForAllSongs() {
        List<Song> allSongs = new ArrayList<>();
        List<PlaylistSimplified> allPlaylists = getAllPlaylists();
        for (PlaylistSimplified playlist : allPlaylists) {
            allSongs.addAll(getAllAudioFeaturesFromPlaylist(playlist.getId()));
        }
        return allSongs;
    }

    //2uDTi1PlpSpvAv7IRAoAEU
    public AudioFeatures getAudioFeatures(String id) {
        Optional<AudioFeatures> audioFeatures = Optional.empty();
        try {
            throttle(3, "getAudioFeatures");
            audioFeatures = Optional.ofNullable(spotifyApi.getAudioFeaturesForTrack(id).build().execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error:3 " + e.getMessage());
        }
        return audioFeatures.orElse(null);
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

    private GetListOfCurrentUsersPlaylistsRequest getUsersPlaylistsRequest(int currentOffset, int limit) {
        int offset = currentOffset + limit;
        return spotifyApi.getListOfCurrentUsersPlaylists().offset(offset).limit(limit).build();
    }

    private GetPlaylistsTracksRequest getTracksFromPlaylistRequest(String playlistId) {
//      .fields("track") .limit(10) .offset(0) .market(CountryCode.SE)
        return spotifyApi.getPlaylistsTracks(playlistId).market(CountryCode.US).build();
    }

    public static Map<String, PlaylistTrack> convertPlaylistToMap(List<PlaylistTrack> playlistTrackList) {
        Map<String, PlaylistTrack> map = new HashMap<>();
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            map.put(playlistTrack.getTrack().getId(), playlistTrack);
        }
        return map;
    }

    private static Map<String, PlaylistTrack> convertPlaylistToMap(PlaylistTrack[] playlistTrackList) {
        Map<String, PlaylistTrack> map = new HashMap<>();
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            map.put(playlistTrack.getTrack().getId(), playlistTrack);
        }
        return map;
    }

    public static void throttle(int seconds, String message) {
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

package com.vyperion.musime.services;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class SpotifyService {

    private final SpotifyApi spotifyApi;

    public SpotifyService(@Qualifier("SpotifyApi") SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<Paging<PlaylistSimplified>> getAllPlaylist() {
        List<Paging<PlaylistSimplified>> pagingList = new LinkedList<>();
        Optional<Paging<PlaylistSimplified>> playList;
        final int maxLimit = 50;
        try {
            playList = Optional.ofNullable(spotifyApi.getListOfCurrentUsersPlaylists().
                    limit(maxLimit).build().execute());

            while (playList.isPresent() && Optional.ofNullable(playList.get().getNext()).isPresent()) {
                pagingList.add(playList.get());
                playList = Optional.ofNullable(
                        getUsersPlaylistsRequest(
                                playList.get().getOffset(),
                                playList.get().getLimit())
                                .execute());
                playList.ifPresent(pagingList::add);
            }
//            if (playList.isPresent()){
//                pagingList.add(playList.get());
//                while (playList.isPresent() && Optional.ofNullable(playList.get().getNext()).isPresent()) {
//                    playList = Optional.ofNullable(
//                            getUsersPlaylistsRequest(
//                                    playList.get().getOffset(),
//                                    playList.get().getLimit())
//                                    .execute());
//                    playList.ifPresent(pagingList::add);
//                }
//            }

        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return pagingList;
    }

    public Paging<PlaylistSimplified> getAllPlaylistsPagination() {
        Optional<Paging<PlaylistSimplified>> playList = Optional.empty();
        try {
            playList = Optional.ofNullable(getUsersPlaylistsRequestPagination().execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return playList.orElse(null);
    }

    public Paging<PlaylistSimplified> getAllPlaylistsPagination(int offset, int limit) {
        Optional<Paging<PlaylistSimplified>> playList = Optional.empty();
        try {
            playList = Optional.ofNullable(getUsersPlaylistsRequestPagination(offset, limit).execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return playList.orElse(null);
    }

    private GetListOfCurrentUsersPlaylistsRequest getUsersPlaylistsRequestPagination() {
        return spotifyApi.getListOfCurrentUsersPlaylists().build();
    }

    private GetListOfCurrentUsersPlaylistsRequest getUsersPlaylistsRequestPagination(int offset, int limit) {
        return spotifyApi.getListOfCurrentUsersPlaylists().offset(offset).limit(limit).build();
    }

    private GetListOfCurrentUsersPlaylistsRequest getUsersPlaylistsRequest(int currentOffset, int limit) {
        int offset = currentOffset + limit;
        return spotifyApi.getListOfCurrentUsersPlaylists().offset(offset).limit(limit).build();
    }

    public List<Paging<PlaylistTrack>> getTracksFromPlaylist(String playlistId) {
        List<Paging<PlaylistTrack>> pagingList = new LinkedList<>();
        Optional<Paging<PlaylistTrack>> playlistTrack;
        try {
            playlistTrack = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
            if (playlistTrack.isPresent()){
                pagingList.add(playlistTrack.get());
                while (playlistTrack.isPresent() && Optional.ofNullable(playlistTrack.get().getNext()).isPresent()) {
                    playlistTrack = Optional.ofNullable(getTracksFromPlaylistRequest(playlistId).execute());
                    playlistTrack.ifPresent(pagingList::add);
                }
            }

        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return pagingList;
    }


    private GetPlaylistsTracksRequest getTracksFromPlaylistRequest(String playlistId) {
//      .fields("track") .limit(10) .offset(0) .market(CountryCode.SE)
        return spotifyApi.getPlaylistsTracks(playlistId).market(CountryCode.US).build();
    }

    //2uDTi1PlpSpvAv7IRAoAEU
    public AudioFeatures getAudioFeatures(String id) {
        Optional<AudioFeatures> audioFeatures = Optional.empty();
        try {
            audioFeatures =  Optional.ofNullable(getAudioFeaturesForTrackRequest(id).execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return audioFeatures.orElse(null);
    }

    private GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest(String id) {
        return spotifyApi.getAudioFeaturesForTrack(id).build();
    }

    public AudioFeatures[] getAudioFeaturesForSeveralTracks(String[] ids) {
        Optional<AudioFeatures[]> audioFeatures = Optional.empty();
        try {
            audioFeatures = Optional.ofNullable(getAudioFeaturesForTrackRequest(ids).execute());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return audioFeatures.get();
    }

    private GetAudioFeaturesForSeveralTracksRequest getAudioFeaturesForTrackRequest(String[] ids) {
        return spotifyApi.getAudioFeaturesForSeveralTracks(ids).build();
    }


}

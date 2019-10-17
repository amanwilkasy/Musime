package com.vyperion.musime.services;

import org.springframework.stereotype.Service;

@Service
public class SpotifyUtility {

    private final SpotifyService spotifyService;

    public SpotifyUtility(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

//    public List<Song> allPlaylistSongsToFeatures() {
//        Map<String, Song> allSongInPlaylists = new HashMap<>();
//        List<Paging<PlaylistSimplified>> allPlaylist = spotifyService.getAllPlaylist();
//        Set<String> allTrackIds = new HashSet<>();
//
//        Map<String, Track> tempTracks = new HashMap<>();
//
//        for (Paging<PlaylistSimplified> playPage : allPlaylist) {
//            for (PlaylistSimplified playList : playPage.getItems()) {
//
//                if (playList.getName().toLowerCase().contains("this is")) {
//                    String playListId = playList.getId();
//
//                    List<Paging<PlaylistTrack>> tracksPage = spotifyService.getTracksFromPlaylist(playListId);
//
//                    for (Paging<PlaylistTrack> tracks : tracksPage) {
//
//                        for (PlaylistTrack track : tracks.getItems()) {
//                            Track trackObj = track.getTrack();
//                            tempTracks.put(trackObj.getId(), trackObj);
//                        }
//                    }
//                }
//            }
//        }
//
////        String[] allTrackIdsArray = tempTracks.keySet().toArray(new String[0]);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//
//        List<List<String>> allTrackIdsList = Lists.partition(new ArrayList<>(tempTracks.keySet()), 10);
//
//        for (int i = 0; i < allTrackIdsList.size(); i++) {
//
//            String[] batchList = allTrackIdsList.get(i).toArray(new String[0]);
//
//            try
//            {
//                System.out.println("sleeping for 5 seconds");
//                Thread.sleep(5000);
//            }
//            catch(InterruptedException ex)
//            {
//                System.out.println("hit thread sleep error, now exiting");
//                System.exit(0);
//                Thread.currentThread().interrupt();
//
//            }
//
//            AudioFeatures[] allAudioFeatures = spotifyService.getAudioFeaturesForSeveralTracks(batchList);
//            allSongInPlaylists.clear();
//
//            for (AudioFeatures audioFeatures : allAudioFeatures) {
//
//                String audioTrackId = audioFeatures.getId();
//
//                if (tempTracks.containsKey(audioTrackId)) {
//                    allSongInPlaylists.put(audioTrackId,
//                            trackAndFeatureToSong(tempTracks.get(audioTrackId), audioFeatures));
//                }
//            }
//            File filePath = new File("src/main/resources/songdata/batch".concat(String.valueOf(i)).concat(".json"));
//            try {
//                if (filePath.createNewFile()) {
//                    System.out.println("New File created! ".concat(filePath.getName()));
//                } else {
//                    System.out.println("File already exists. ".concat(filePath.getName()));
//                }
//                mapper.writeValue(filePath,  new ArrayList<>(allSongInPlaylists.values()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        return new ArrayList<>(allSongInPlaylists.values());
//    }
//
//
//    private Song trackAndFeatureToSong(Track track, AudioFeatures audioFeatures) {
//        Song song = new Song();
//        song.setSongName(track.getName());
//        song.setId(track.getId());
//        song.setPopularity(track.getPopularity());
//        song.setArtist(track.getArtists());
//
//        if (audioFeatures != null) {
//            if (Optional.ofNullable(audioFeatures.getAcousticness()).isPresent()) {
//                song.setAcousticness(audioFeatures.getAcousticness());
//            }
//            if (Optional.ofNullable(audioFeatures.getDanceability()).isPresent()) {
//                song.setDanceability(audioFeatures.getDanceability());
//            }
//            if (Optional.ofNullable(audioFeatures.getEnergy()).isPresent()) {
//                song.setEnergy(audioFeatures.getEnergy());
//            }
//            if (Optional.ofNullable(audioFeatures.getInstrumentalness()).isPresent()) {
//                song.setInstrumentalness(audioFeatures.getInstrumentalness());
//            }
//            if (Optional.ofNullable(audioFeatures.getLiveness()).isPresent()) {
//                song.setLiveness(audioFeatures.getLiveness());
//            }
//            if (Optional.ofNullable(audioFeatures.getLoudness()).isPresent()) {
//                song.setLoudness(audioFeatures.getLoudness());
//            }
//            if (Optional.ofNullable(audioFeatures.getSpeechiness()).isPresent()) {
//                song.setSpeechiness(audioFeatures.getSpeechiness());
//            }
//            if (Optional.ofNullable(audioFeatures.getTempo()).isPresent()) {
//                song.setTempo(audioFeatures.getTempo());
//            }
//            if (Optional.ofNullable(audioFeatures.getValence()).isPresent()) {
//                song.setValence(audioFeatures.getValence());
//            }
//            if (Optional.ofNullable(audioFeatures.getDurationMs()).isPresent()) {
//                song.setDurationMs(audioFeatures.getDurationMs());
//            }
//            if (Optional.ofNullable(audioFeatures.getKey()).isPresent()) {
//                song.setKey(audioFeatures.getKey());
//            }
//
//            if (Optional.ofNullable(audioFeatures.getTimeSignature()).isPresent()) {
//                song.setTimeSignature(audioFeatures.getTimeSignature());
//            }
//        }
//        return song;
//    }
//



}





















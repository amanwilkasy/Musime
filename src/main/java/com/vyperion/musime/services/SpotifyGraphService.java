package com.vyperion.musime.services;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.vyperion.musime.dto.FeaturesGraph;
import com.vyperion.musime.dto.Song;
import com.vyperion.musime.dto.UserDataState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyGraphService {

    private UserDataStateService userDataStateService;

    private SpotifyService spotifyService;

    public SpotifyGraphService(SpotifyService spotifyService, UserDataStateService userDataStateService) {
        this.userDataStateService = userDataStateService;
        this.spotifyService = spotifyService;
    }


    public List<FeaturesGraph> generateUserGraphFeatures() {

        userDataStateService.updateState(UserDataState.State.CREATING);
        List<Song> songs = spotifyService.getAllAudioFeaturesForAllSongs();
        SpotifyGraphService.writeAllSongFeaturesToFile(songs, userDataStateService.getUserDataState().getId());
        userDataStateService.updateState(UserDataState.State.READY);

        return convertSongsToFeaturesGraph(songs);
    }

    public List<FeaturesGraph> getUserGraphFeatures() {
        String path = UserDataState.songDirectory.concat(userDataStateService.getUserDataState().getId()).concat(".json");
        List<Song> songs = SpotifyGraphService.readFileIntoSongs(path);
        return convertSongsToFeaturesGraph(songs);
    }


    public static TreeMap<Integer, Integer> consolidate(TreeMap<Integer, Integer> map) {
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

    public static List<FeaturesGraph> convertSongsToFeaturesGraph(List<Song> songs) {
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
        TreeMap<Integer, Integer> getTimeSignatureMap = new TreeMap<>();

        songs.forEach(song -> {
            int getPopularityMapKey = song.getPopularity();
            int getAcousticnessMapKey = Math.round(song.getAcousticness() * 100);
            int getDanceabilityMapKey = Math.round(song.getDanceability() * 10);
            int getDurationMsMapKey = Math.round(song.getDurationMs() / 100);
            int getEnergyMapKey = Math.round(song.getEnergy() * 100);
            int getInstrumentalnessMapKey = Math.round(song.getInstrumentalness() * 100);
            int getKeyMapKey = (int) song.getKey();
            int getLivenessMapKey = Math.round(song.getLiveness() * 100);
            int getLoudnessMapKey = Math.round(song.getLoudness() * -100);
            int getSpeechinessMapKey = Math.round(song.getSpeechiness() * 100);
            int getTempoMapKey = (int) song.getTempo();
            int getTimeSignatureMapKey = Math.round(song.getTimeSignature());
            int getValenceMapKey = Math.round(song.getValence() * 100);
            getPopularityMap.put(getPopularityMapKey, getPopularityMap.containsKey(getPopularityMapKey) ? getPopularityMap.get(getPopularityMapKey) + 1 : 1);
            getEnergyMap.put(getEnergyMapKey, getEnergyMap.containsKey(getEnergyMapKey) ? getEnergyMap.get(getEnergyMapKey) + 1 : 1);
            getInstrumentalnessMap.put(getInstrumentalnessMapKey, getInstrumentalnessMap.containsKey(getInstrumentalnessMapKey) ? getInstrumentalnessMap.get(getInstrumentalnessMapKey) + 1 : 1);
            getKeyMap.put(getKeyMapKey, getKeyMap.containsKey(getKeyMapKey) ? getKeyMap.get(getKeyMapKey) + 1 : 1);
            getLivenessMap.put(getLivenessMapKey, getLivenessMap.containsKey(getLivenessMapKey) ? getLivenessMap.get(getLivenessMapKey) + 1 : 1);
            getLoudnessMap.put(getLoudnessMapKey, getLoudnessMap.containsKey(getLoudnessMapKey) ? getLoudnessMap.get(getLoudnessMapKey) + 1 : 1);
            getTimeSignatureMap.put(getTimeSignatureMapKey, getTimeSignatureMap.containsKey(getTimeSignatureMapKey) ? getTimeSignatureMap.get(getTimeSignatureMapKey) + 1 : 1);
            getSpeechinessMap.put(getSpeechinessMapKey, getSpeechinessMap.containsKey(getSpeechinessMapKey) ? getSpeechinessMap.get(getSpeechinessMapKey) + 1 : 1);
            getValenceMap.put(getValenceMapKey, getValenceMap.containsKey(getValenceMapKey) ? getValenceMap.get(getValenceMapKey) + 1 : 1);
            getTempoMap.put(getTempoMapKey, getTempoMap.containsKey(getTempoMapKey) ? getTempoMap.get(getTempoMapKey) + 1 : 1);
            getDurationMsMap.put(getDurationMsMapKey, getDurationMsMap.containsKey(getDurationMsMapKey) ? getDurationMsMap.get(getDurationMsMapKey) + 1 : 1);
            getAcousticnessMap.put(getAcousticnessMapKey, getAcousticnessMap.containsKey(getAcousticnessMapKey) ? getAcousticnessMap.get(getAcousticnessMapKey) + 1 : 1);
            getDanceabilityMap.put(getDanceabilityMapKey, getDanceabilityMap.containsKey(getDanceabilityMapKey) ? getDanceabilityMap.get(getDanceabilityMapKey) + 1 : 1);

        });

        consolidated.add(new FeaturesGraph("getPopularityMap", consolidate(getPopularityMap)));
        consolidated.add(new FeaturesGraph("getEnergyMap", consolidate(getEnergyMap)));
        consolidated.add(new FeaturesGraph("getInstrumentalnessMap", consolidate(getInstrumentalnessMap)));
        consolidated.add(new FeaturesGraph("getAcousticnessMap", consolidate(getAcousticnessMap)));
        consolidated.add(new FeaturesGraph("getKeyMap", consolidate(getKeyMap)));
        consolidated.add(new FeaturesGraph("getLivenessMap", consolidate(getLivenessMap)));
        consolidated.add(new FeaturesGraph("getLoudnessMap", consolidate(getLoudnessMap)));
        consolidated.add(new FeaturesGraph("getSpeechinessMap", consolidate(getSpeechinessMap)));
        consolidated.add(new FeaturesGraph("getValenceMap", consolidate(getValenceMap)));
        consolidated.add(new FeaturesGraph("getTempoMap", consolidate(getTempoMap)));
        consolidated.add(new FeaturesGraph("getDurationMsMap", consolidate(getDurationMsMap)));
        consolidated.add(new FeaturesGraph("getAcousticnessMap", consolidate(getAcousticnessMap)));
        consolidated.add(new FeaturesGraph("getDanceabilityMap", consolidate(getDanceabilityMap)));
        return consolidated;
    }

    public static List<Song> readFileIntoSongs(String path) {
        Gson gson = new Gson();
        Path resource = Paths.get(path);
        List<Song> songList = new ArrayList<>();
        try {
            songList = Arrays.asList(gson.fromJson(new FileReader(resource.toString()), Song[].class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return songList;
    }

    public static String writeAllSongFeaturesToFile(List<Song> songs, String fileName) {

        Gson gson = new Gson();
//        String fileName = UUID.randomUUID().toString();
        File file = new File("src/main/resources/songdata/".concat(fileName).concat(".json"));

        try {
            if (file.createNewFile()) {
                System.out.println("File created");
            } else {
                System.out.println("File already exists");
            }
            Writer writer = new FileWriter(file);
            gson.toJson(songs, writer);
            writer.flush(); //flush data to file   <---
            writer.close(); //close write
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getPath();
    }


}

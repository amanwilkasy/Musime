package com.vyperion.musime.services;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.vyperion.musime.dto.ConsolidatedFeatures;
import com.vyperion.musime.dto.Song;
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
public class SpotifyUtility {

    //    private Gson gson = new Gson();
    private List<Song> songs;

//    public SpotifyUtility(List<Song> songs) {
//        this.songs = songs;
//    }

    public List<ConsolidatedFeatures> getConsolidatedFeatures() {
        List<ConsolidatedFeatures> consolidated = new ArrayList<>();

        Map<Integer, Integer> getPopularityMap = consolidate(getPopularityMap());
        Map<Integer, Integer> getEnergyMap = consolidate(getEnergyMap());
        Map<Integer, Integer> getInstrumentalnessMap = consolidate(getInstrumentalnessMap());
        Map<Integer, Integer> getKeyMap = consolidate(getKeyMap());
        Map<Integer, Integer> getLivenessMap = consolidate(getLivenessMap());
        Map<Integer, Integer> getLoudnessMap = consolidate(getLoudnessMap());
        Map<Integer, Integer> getSpeechinessMap = consolidate(getSpeechinessMap());
        Map<Integer, Integer> getValenceMap = consolidate(getValenceMap());
        Map<Integer, Integer> getTempoMap = consolidate(getTempoMap());
        Map<Integer, Integer> getDurationMsMap = consolidate(getDurationMsMap());
        Map<Integer, Integer> getAcousticnessMap = consolidate(getAcousticnessMap());
        Map<Integer, Integer> getDanceabilityMap = consolidate(getDanceabilityMap());
        Map<Integer, Integer> getTimeSignatureMap = consolidate(getTimeSignatureMap());


        consolidated.add(new ConsolidatedFeatures("getPopularityMap",
                getPopularityMap.keySet(), new ArrayList<>(getPopularityMap.values())));
        consolidated.add(new ConsolidatedFeatures("getEnergyMap",
                getEnergyMap.keySet(), new ArrayList<>(getEnergyMap.values())));
        consolidated.add(new ConsolidatedFeatures("getInstrumentalnessMap",
                getInstrumentalnessMap.keySet(), new ArrayList<>(getAcousticnessMap.values())));
        consolidated.add(new ConsolidatedFeatures("getAcousticnessMap",
                getAcousticnessMap.keySet(), new ArrayList<>(getAcousticnessMap.values())));
        consolidated.add(new ConsolidatedFeatures("getKeyMap",
                getKeyMap.keySet(), new ArrayList<>(getKeyMap.values())));
        consolidated.add(new ConsolidatedFeatures("getLivenessMap",
                getLivenessMap.keySet(), new ArrayList<>(getLivenessMap.values())));
        consolidated.add(new ConsolidatedFeatures("getLoudnessMap",
                getLoudnessMap.keySet(), new ArrayList<>(getLoudnessMap.values())));
        consolidated.add(new ConsolidatedFeatures("getSpeechinessMap",
                getSpeechinessMap.keySet(), new ArrayList<>(getSpeechinessMap.values())));
        consolidated.add(new ConsolidatedFeatures("getValenceMap",
                getValenceMap.keySet(), new ArrayList<>(getValenceMap.values())));
        consolidated.add(new ConsolidatedFeatures("getTempoMap",
                getTempoMap.keySet(), new ArrayList<>(getTempoMap.values())));
        consolidated.add(new ConsolidatedFeatures("getDurationMsMap",
                getDurationMsMap.keySet(), new ArrayList<>(getDurationMsMap.values())));
        consolidated.add(new ConsolidatedFeatures("getAcousticnessMap",
                getAcousticnessMap.keySet(), new ArrayList<>(getAcousticnessMap.values())));
        consolidated.add(new ConsolidatedFeatures("getDanceabilityMap",
                getDanceabilityMap.keySet(), new ArrayList<>(getDanceabilityMap.values())));
        consolidated.add(new ConsolidatedFeatures("name",
                getTimeSignatureMap.keySet(), new ArrayList<>(getTimeSignatureMap.values())));


        return consolidated;

    }

    public static List<Song> readFileIntoSongs(String path){
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


    public static String writeAllSongFeaturesToFile(List<Song> songs) {

        Gson gson = new Gson();
        String fileName = UUID.randomUUID().toString();
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

    public static Map<Integer, Integer> consolidate(TreeMap<Integer, Integer> map) {
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

    TreeMap<Integer, Integer> getPopularityMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = song.getPopularity();
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }


    TreeMap<Integer, Integer> getAcousticnessMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getAcousticness() * 100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getDanceabilityMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getDanceability() * 10);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getDurationMsMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getDurationMs() / 100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getEnergyMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getEnergy() * 100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getInstrumentalnessMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getInstrumentalness() * 100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getKeyMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = (int) song.getKey();
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getLivenessMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getLiveness() * 100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getLoudnessMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getLoudness() * -100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getSpeechinessMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getSpeechiness() * 100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getTempoMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = (int) song.getTempo();
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getTimeSignatureMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getTimeSignature());
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

    TreeMap<Integer, Integer> getValenceMap() {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        songs.forEach(song -> {
            int key = Math.round(song.getValence() * 100);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        });
        return map;
    }

}

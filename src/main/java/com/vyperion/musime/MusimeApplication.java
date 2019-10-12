package com.vyperion.musime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vyperion.musime.dto.Song;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class MusimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusimeApplication.class, args);

        ObjectMapper mapper = new ObjectMapper();
        Gson gson = new Gson();
//        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        List<Song> songList = new ArrayList<>();

        for (int i = 0; i <= 88; i++) {
            File filePath = new File("src/main/resources/songdata/batch".concat(String.valueOf(i)).concat(".json"));
            try{
//                Song[] currentSongFile = mapper.readValue(filePath, Song[].class);
                Song[] currentSongFile = gson.fromJson(new FileReader(filePath), Song[].class);

                System.out.println("number of files is " + currentSongFile.length + " file name is " + filePath.getName());

                for (Song some : currentSongFile) {
                    System.out.println( some.getSongName());
                }

                songList.addAll(Arrays.asList(currentSongFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File masterFile = new File("src/main/resources/songdata/0master.json");

        try {
            masterFile.createNewFile();
            System.out.println(songList.get(songList.size() - 1).getSongName() + " final song");
            Writer writer =  new FileWriter(masterFile);
            gson.toJson(songList,writer);
            writer.flush(); //flush data to file   <---
            writer.close(); //close write
            System.out.println("finished adding all files " + songList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

//
//        Song song1 = new Song();
//        Song song2 = new Song();
//
//        song1.setLoudness(123);
//        song1.setTimeSignature(2);
//        song1.setMode("as");
//        song1.setKey(123);
//        song1.setValence(123);
//        song1.setTempo(123);
//        song1.setLiveness(123);
//        song1.setSpeechiness(123);
//        song1.setDurationMs(123);
//        song1.setDanceability(123);
//        song1.setAcousticness(123);
//        song1.setPopularity(123);
//        song1.setId("asdf");
//        song1.setPopularity(123);
////        song1.setArtist("trav");
//        song1.setInstrumentalness(24);
//        song1.setSongName("hi");
//
//        song2.setLoudness(123);
//        song2.setTimeSignature(2);
//        song2.setMode("asdf");
//        song2.setKey(3);
//        song2.setValence(4);
//        song2.setTempo(2);
//        song2.setLiveness(2);
//        song2.setSpeechiness(2);
//        song2.setDurationMs(2);
//        song2.setDanceability(2);
//        song2.setAcousticness(2);
//        song2.setPopularity(2);
//        song2.setId("asdf");
//        song2.setPopularity(123);
////        song2.setArtist("trav");
//        song2.setInstrumentalness(24);
//        song2.setSongName("hi");
//
//        File filePath = new File("src/main/resources/songdata/song1.json");
//        File filePath2 = new File("src/main/resources/songdata/song2.json");
//        File filePath3 = new File("src/main/resources/songdata/song3.json");
//
//
//        try {
//            filePath.createNewFile();
//            filePath2.createNewFile();
//            filePath3.createNewFile();
//
//            mapper.writeValue(filePath, song1);
//            mapper.writeValue(filePath2, song2);
//            mapper.writeValue(filePath3, new Song[]{song1, song2});
//
////            mapper.writeValueAsString(filePath);
////            mapper.writeValueAsString(filePath2);
//
////            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(filePath);
////            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(filePath2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }



}

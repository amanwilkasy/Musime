package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userDataState")
public class UserDataState {

    public enum State {
        NONE,
        CREATING,
        READY
    }

    public final static String songDirectory = "src/main/resources/songdata/";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "state", nullable = false)
    private State state;

    @Column(name = "path", nullable = false)
    private String path;


}

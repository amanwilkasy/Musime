package com.vyperion.musime.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ProcessState")
public class ProcessState {

    public enum State{
        NONE,
        PROCESSING,
        DONE
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, columnDefinition = "varchar(255) default 'NONE'")
    private State state;

    public ProcessState(String userId, State state) {
        this.userId = userId;
        this.state = state;
    }
}

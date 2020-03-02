package com.example.babyfoot;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "userMatch")
public class UserMatch {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idMatch")
    public int idMatch;

    @ColumnInfo(name = "opponent_id")
    public int oppId;

    @ColumnInfo(name = "self_score")
    public int sscore;

    @ColumnInfo(name = "opponent_score")
    public int oscore;

    @ColumnInfo(name = "duration")
    public int duration;

    public UserMatch(int _oppId, int _sscore, int _oscore, int _duration){
        sscore = _sscore;
        oscore = _oscore;
        duration = _duration;
        oppId = _oppId;
    }

    public UserMatch(){

    }
}
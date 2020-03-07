package com.example.babyfoot;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface UserMatchDao {
    @Query("SELECT * FROM userMatch ORDER BY idMatch DESC")
    List<UserMatch> getAll();

    @Query("SELECT * FROM userMatch WHERE idMatch = :id LIMIT 1")
    UserMatch findMatchById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(UserMatch match);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserMatch... matchs);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(UserMatch match);

    @Query("DELETE FROM userMatch")
    void deleteAll();

}

package com.example.babyfoot;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class UserMatchDao_Impl implements UserMatchDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfUserMatch;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfUserMatch;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UserMatchDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserMatch = new EntityInsertionAdapter<UserMatch>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `userMatch`(`idMatch`,`opponent_id`,`self_score`,`opponent_score`,`duration`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, UserMatch value) {
        stmt.bindLong(1, value.idMatch);
        stmt.bindLong(2, value.oppId);
        stmt.bindLong(3, value.sscore);
        stmt.bindLong(4, value.oscore);
        stmt.bindLong(5, value.duration);
      }
    };
    this.__updateAdapterOfUserMatch = new EntityDeletionOrUpdateAdapter<UserMatch>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR IGNORE `userMatch` SET `idMatch` = ?,`opponent_id` = ?,`self_score` = ?,`opponent_score` = ?,`duration` = ? WHERE `idMatch` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, UserMatch value) {
        stmt.bindLong(1, value.idMatch);
        stmt.bindLong(2, value.oppId);
        stmt.bindLong(3, value.sscore);
        stmt.bindLong(4, value.oscore);
        stmt.bindLong(5, value.duration);
        stmt.bindLong(6, value.idMatch);
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM userMatch";
        return _query;
      }
    };
  }

  @Override
  public long insert(UserMatch match) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfUserMatch.insertAndReturnId(match);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insert(UserMatch... matchs) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfUserMatch.insert(matchs);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(UserMatch match) {
    __db.beginTransaction();
    try {
      __updateAdapterOfUserMatch.handle(match);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<UserMatch> getAll() {
    final String _sql = "SELECT * FROM userMatch ORDER BY idMatch DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfIdMatch = _cursor.getColumnIndexOrThrow("idMatch");
      final int _cursorIndexOfOppId = _cursor.getColumnIndexOrThrow("opponent_id");
      final int _cursorIndexOfSscore = _cursor.getColumnIndexOrThrow("self_score");
      final int _cursorIndexOfOscore = _cursor.getColumnIndexOrThrow("opponent_score");
      final int _cursorIndexOfDuration = _cursor.getColumnIndexOrThrow("duration");
      final List<UserMatch> _result = new ArrayList<UserMatch>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final UserMatch _item;
        _item = new UserMatch();
        _item.idMatch = _cursor.getInt(_cursorIndexOfIdMatch);
        _item.oppId = _cursor.getInt(_cursorIndexOfOppId);
        _item.sscore = _cursor.getInt(_cursorIndexOfSscore);
        _item.oscore = _cursor.getInt(_cursorIndexOfOscore);
        _item.duration = _cursor.getInt(_cursorIndexOfDuration);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public UserMatch findMatchById(int id) {
    final String _sql = "SELECT * FROM userMatch WHERE idMatch = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfIdMatch = _cursor.getColumnIndexOrThrow("idMatch");
      final int _cursorIndexOfOppId = _cursor.getColumnIndexOrThrow("opponent_id");
      final int _cursorIndexOfSscore = _cursor.getColumnIndexOrThrow("self_score");
      final int _cursorIndexOfOscore = _cursor.getColumnIndexOrThrow("opponent_score");
      final int _cursorIndexOfDuration = _cursor.getColumnIndexOrThrow("duration");
      final UserMatch _result;
      if(_cursor.moveToFirst()) {
        _result = new UserMatch();
        _result.idMatch = _cursor.getInt(_cursorIndexOfIdMatch);
        _result.oppId = _cursor.getInt(_cursorIndexOfOppId);
        _result.sscore = _cursor.getInt(_cursorIndexOfSscore);
        _result.oscore = _cursor.getInt(_cursorIndexOfOscore);
        _result.duration = _cursor.getInt(_cursorIndexOfDuration);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}

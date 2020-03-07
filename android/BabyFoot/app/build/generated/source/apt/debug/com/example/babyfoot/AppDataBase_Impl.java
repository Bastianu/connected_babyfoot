package com.example.babyfoot;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class AppDataBase_Impl extends AppDataBase {
  private volatile UserMatchDao _userMatchDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `userMatch` (`idMatch` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `opponent_id` INTEGER NOT NULL, `self_score` INTEGER NOT NULL, `opponent_score` INTEGER NOT NULL, `duration` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"432dc4209f95a21a51e374eea23908d8\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `userMatch`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsUserMatch = new HashMap<String, TableInfo.Column>(5);
        _columnsUserMatch.put("idMatch", new TableInfo.Column("idMatch", "INTEGER", true, 1));
        _columnsUserMatch.put("opponent_id", new TableInfo.Column("opponent_id", "INTEGER", true, 0));
        _columnsUserMatch.put("self_score", new TableInfo.Column("self_score", "INTEGER", true, 0));
        _columnsUserMatch.put("opponent_score", new TableInfo.Column("opponent_score", "INTEGER", true, 0));
        _columnsUserMatch.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserMatch = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserMatch = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserMatch = new TableInfo("userMatch", _columnsUserMatch, _foreignKeysUserMatch, _indicesUserMatch);
        final TableInfo _existingUserMatch = TableInfo.read(_db, "userMatch");
        if (! _infoUserMatch.equals(_existingUserMatch)) {
          throw new IllegalStateException("Migration didn't properly handle userMatch(com.example.babyfoot.UserMatch).\n"
                  + " Expected:\n" + _infoUserMatch + "\n"
                  + " Found:\n" + _existingUserMatch);
        }
      }
    }, "432dc4209f95a21a51e374eea23908d8", "663114b76331c526fb2e251d9acd3e3c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "userMatch");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `userMatch`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public UserMatchDao UserMatchDao() {
    if (_userMatchDao != null) {
      return _userMatchDao;
    } else {
      synchronized(this) {
        if(_userMatchDao == null) {
          _userMatchDao = new UserMatchDao_Impl(this);
        }
        return _userMatchDao;
      }
    }
  }
}

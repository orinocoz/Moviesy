package com.kpstv.yts.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kpstv.yts.data.converters.TMdbConverter
import com.kpstv.yts.data.db.localized.RecommendDao
import com.kpstv.yts.data.models.data.data_tmdb

@Database(
    entities = [data_tmdb::class],
    version = 1
)
@TypeConverters(
    TMdbConverter::class
)
abstract class RecommendDatabase : RoomDatabase() {
    abstract fun getTMdbDao(): RecommendDao
}
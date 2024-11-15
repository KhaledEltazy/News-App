package com.example.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.model.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract  class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao() : ArticleDao

    companion object{
        @Volatile
        private var Instance : ArticleDatabase? = null

        val LOCK = Any()
        operator fun invoke(context: Context) : ArticleDatabase{
            return Instance?: synchronized(LOCK){ createDatabase(context)}
        }
        private fun createDatabase(context: Context) : ArticleDatabase{
            return Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "articleDB.db"
            ).build()
        }
    }
}
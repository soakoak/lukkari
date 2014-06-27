package fi.sokira.lukkari.provider

import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import android.content.ContentValues

abstract class BaseDao {

   def query(readableDb: SQLiteDatabase, projection: Array[String],
         selection: String, selectionArgs: Array[String], 
         sortOrder: String): Cursor
         
   def insert(writeableDb: SQLiteDatabase, values: ContentValues): Long
   
   def update(writeableDb: SQLiteDatabase, values: ContentValues,
         selection: String, selectionArgs: Array[String]): Int
         
   def delete(writeableDb: SQLiteDatabase, 
         selection: String, selectionArgs: Array[String]): Int
}
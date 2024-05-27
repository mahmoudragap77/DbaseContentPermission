package com.training.dbasecontentpermission

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context : Context) :SQLiteOpenHelper(context, DBCONSTATNT.DB_NAME, null, DBCONSTATNT.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE  ${DBCONSTATNT.TABLE_NAME} ( " +
                "${DBCONSTATNT.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${DBCONSTATNT.COLUMN_NAME} TEXT," +
                "${DBCONSTATNT.COLUMN_PHONE} TEXT," +
                "${DBCONSTATNT.COLUMN_EMAIL} TEXT )"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${DBCONSTATNT.TABLE_NAME}")
        onCreate(db)
    }

}
package com.example.kuracoin_wallet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kuracoin_wallet.models.Wallet
import com.example.kuracoin_wallet.models.WalletDao


@Database(entities = [Wallet::class], version = 1)
//@TypeConverters(Converter::class)
abstract class WalletDatabase :RoomDatabase(){

    abstract fun walletDao(): WalletDao


    companion object{
        private var instance: WalletDatabase?=null

        @Synchronized
        fun getInstance(context:Context): WalletDatabase {
            if(instance ==null){
                instance = Room.databaseBuilder(context.applicationContext, WalletDatabase::class.java,
                    "notes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }

            return instance!!
        }

        private val roomCallback = object :Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

            }
        }

        private fun populateDatabase(db: WalletDatabase){
            val walletDao = db.walletDao()
//            val folderDao = db.folderDao()
        }
    }
}

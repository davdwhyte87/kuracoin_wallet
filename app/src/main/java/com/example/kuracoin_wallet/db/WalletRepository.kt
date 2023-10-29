package com.example.kuracoin_wallet.db

import android.app.Application
import com.example.kuracoin_wallet.models.Wallet
import com.example.kuracoin_wallet.models.WalletDao


class WalletRepository(application: Application) {
    private var walletDao:WalletDao

    private val database= WalletDatabase.getInstance(application)
//    lateinit var allNotes: Flow<List<Note>>
    init {
        walletDao=database.walletDao()
//        allNotes = noteDao.getAllNotes()
//        allNotes= noteDao.getAllNotes()

    }

    suspend fun insert(note:Wallet){
        walletDao.insert(note)
    }

    suspend fun getAll(): List<Wallet> {
        return walletDao.getAllNotes()
    }

    fun getSingle(id:Int): Wallet {
        return walletDao.getSingle(id)
    }

    fun update(wallet: Wallet){
        return walletDao.update(wallet)
    }

    fun deleteSingle(wallet:Wallet){
        return walletDao.delete(wallet)
    }

//    fun getFolderFiles(id:Int): Flow<List<Note>> {
//        return noteDao.getFolderFiles(id)
//    }
//
//    suspend fun update(note:Note){
//        noteDao.update(note)
//    }
//    fun updatex(id:Int, body:String): Flow<Int> {
//        return noteDao.updatex(id, body)
//    }

}
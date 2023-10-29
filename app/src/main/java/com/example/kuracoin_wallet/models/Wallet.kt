package com.example.kuracoin_wallet.models

import androidx.room.*

@Entity(tableName = "wallet_table")
class Wallet(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val address: String,
    var balance: Double,
    val walletName: String? = null
)

@Dao
interface WalletDao {

    @Insert
    fun insert(note: Wallet)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(wallet: Wallet)

//    @Query("UPDATE note_table SET Note=:noteBody WHERE id=:id")
//    fun updatex(id:Int, noteBody:String): Flow<Int> = MutableStateFlow(id)

    @Delete
    fun delete(note: Wallet)

//    @Query("delete from wallet_table Where id=:id")
//    fun deleteAllNotes(id:Int)


    @Query("select * from wallet_table ")
    fun getAllNotes(): List<Wallet>
//
    @Query("select * from wallet_table where id=:id")
    fun getSingle(id:Int): Wallet
//
//    @Query("select * from note_table where FolderID=:id")
//    fun getFolderFiles(id:Int): Flow<List<Note>>
}
package com.example.kuracoin_wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kuracoin_wallet.adapters.ChainAdapter
import com.example.kuracoin_wallet.db.WalletRepository
import com.example.kuracoin_wallet.models.GetChainResponse
import com.example.kuracoin_wallet.models.ResponseData
import com.example.kuracoin_wallet.models.Wallet
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class SingleWallet : AppCompatActivity() {
    private lateinit var wallet:Wallet
    private lateinit var deleteBtn:Button
    private lateinit var chainError:TextView
    private lateinit var amount:TextView
    private lateinit var sender:TextView
    private lateinit var receiverAddressEdit:EditText
    private lateinit var privateKeyEdit:EditText
    private lateinit var amountEdit:EditText
    private lateinit var date:TextView
    private lateinit var transferButton: Button
    lateinit var chainRecyclerView: RecyclerView
    private var viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_wallet)

        getWallet()
        deleteBtn = findViewById(R.id.delete_btn)
        deleteBtn.setOnClickListener {
            showDefaultDialog()

        }

        chainError = findViewById(R.id.chain_error)
        transferButton = findViewById(R.id.transfer_btn)
        receiverAddressEdit = findViewById(R.id.receiver_address_edit)
        privateKeyEdit = findViewById(R.id.private_key_edit)
        amountEdit = findViewById(R.id.amount_edit)

        // transfer button
        transferButton.setOnClickListener {
            if (privateKeyEdit.text ==null &&
                receiverAddressEdit.text == null ){
                Toast.makeText(this,"Nothing here", Toast.LENGTH_SHORT).show()
            }

            transfer()
        }
        chainRecyclerView = findViewById(R.id.chain_recycler)
        chainRecyclerView.layoutManager = viewManager
    }

    fun deleteWallet(){
        lifecycleScope.launch{
            deleteBtn.text = "...."
            withContext(Dispatchers.IO){
                var repository = WalletRepository(application)
                if (wallet.id !=null && wallet.id!=0){
                    repository.deleteSingle(wallet)
                }
            }
            withContext(Dispatchers.Main){
                deleteBtn.text ="Delete"
            }

            finish()
        }
    }

    private fun showDefaultDialog() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.apply {

            setTitle("Hello")
            setMessage("Are you sure you want to delete?")
            setPositiveButton("Yes") { _, _ ->
               deleteWallet()
            }
            setNegativeButton("No") { _, _ ->

            }

        }.create().show()
    }


    fun getWallet(){
       val id = this.intent.getIntExtra("id",0)
        Log.i("XXXXXX", id.toString())
        lifecycleScope.launch{
            withContext(Dispatchers.IO){
                var walletRepository = WalletRepository(application)
                wallet = walletRepository.getSingle(id)
            }

            withContext(Dispatchers.Main){
                findViewById<TextView>(R.id.wallet_name).text = wallet.walletName
                findViewById<TextView>(R.id.wallet_balance).text = wallet.balance.toString()+" DAu"
                findViewById<TextView>(R.id.wallet_address).text = wallet.address
            }
        }
    }

    fun updateWalletLocal(){
        lifecycleScope.launch{
            withContext(Dispatchers.IO){
                var walletRepository = WalletRepository(application)
                walletRepository.update(wallet)
            }

        }
    }


    override fun onResume() {

        super.onResume()
        getChain()
        getWallet()
    }
    fun getChain(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                try {


                    val message ="{'action':'GET_WALLET_CHAIN','walletAddress':'${wallet.address}'}"

                    val client = Socket("172.28.48.1", 100)
                    client.outputStream.write(message.toByteArray())
                    val input = BufferedReader(InputStreamReader(client.inputStream))


                    //get data from req string
                    val gson = Gson()
                    val responseData = gson.fromJson(input.readLine(), GetChainResponse::class.java)

                    Log.i("XXXXXXXXXX", responseData.respCode.toString())
                    client.close()
                    // update wallet
                    val lastBlock = responseData.chain[responseData.chain.size-1]
                    wallet.balance = lastBlock.Balance.toDouble()
                    updateWalletLocal()
                    withContext(Dispatchers.Main) {
                        chainRecyclerView.adapter = ChainAdapter(responseData.chain, application, ChainAdapter.OnClickListener{

                        })
                        getWallet()
                    }
                } catch (ex:Exception){
//                       val toast = Toast.makeText(this, ex.toString(), 0)
                    withContext(Dispatchers.Main) {
                        chainError.text = ex.message.toString()
//                        createWalletButton.text = "Create Wallet"
//                        createWalletButton.isEnabled = true
                    }
                }
            }

        }
    }

    fun transfer(){
        transferButton.text ="......"
        transferButton.isEnabled = false
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                try {


                    val message ="{'action':'TRANSFER','transfer':{'senderAddress':'${wallet.address}'" +
                            ",'receiverAddress':'${receiverAddressEdit.text}', 'senderPrivateKey':'${privateKeyEdit.text}', 'amount':'${amountEdit.text}'}}"

                    val client = Socket("172.28.48.1", 100)
                    client.outputStream.write(message.toByteArray())
                    val input = BufferedReader(InputStreamReader(client.inputStream))


                    //get data from req string
                    val gson = Gson()
                    val responseData = gson.fromJson(input.readLine(), ResponseData::class.java)

                    Log.i("XXXXXXXXXX", responseData.respCode.toString())
                    client.close()


                    withContext(Dispatchers.Main) {
                        transferButton.text ="transfer"
                        transferButton.isEnabled = true
                        if (responseData.respCode == 200){
                            Toast.makeText(applicationContext, "Transfer done!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(applicationContext, "Error! ${responseData.respMessage.toString()}", Toast.LENGTH_SHORT).show()
                            Log.i("XXXX", responseData.respMessage.toString())
                        }

                    }
                    getChain()
                } catch (ex:Exception){
//                       val toast = Toast.makeText(this, ex.toString(), 0)
                    withContext(Dispatchers.Main) {
                        chainError.text = ex.message.toString()
//                        createWalletButton.text = "Create Wallet"
//                        createWalletButton.isEnabled = true
                    }
                }
            }

        }
    }
}
package com.example.kuracoin_wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.kuracoin_wallet.db.WalletRepository
import com.example.kuracoin_wallet.models.ResponseData
import com.example.kuracoin_wallet.models.Wallet
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class CreateWallet : AppCompatActivity() {
    private lateinit var createWalletButton: Button
    private lateinit var walletNameEdit: EditText
    private lateinit var privateKeyEdit: EditText
    private lateinit var errorText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        createWalletButton = findViewById(R.id.create_wallet_btn)
        walletNameEdit = findViewById(R.id.wallet_name_edit)
        privateKeyEdit = findViewById(R.id.private_key_edit)
        errorText = findViewById(R.id.error_text)

        createWalletButton.setOnClickListener{
//            Log.i("DDDDDD", walletNameEdit.text.toString())
//            Log.i("XXXXXXX", message)
            createWalletButton.text = "....."
            createWalletButton.isEnabled = false
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    try {


                        val message =
                            "{\"action\":\"CREATE_WALLET\",\"createWallet\":{\"walletName\":\"${walletNameEdit.text.toString()}\",\"password\":\"${privateKeyEdit.text.toString()}\"}}"
                        val client = Socket("172.28.48.1", 100)
                        client.outputStream.write(message.toByteArray())
                        val input = BufferedReader(InputStreamReader(client.inputStream))


                        //get data from req string
                        val gson = Gson()
                        val responseData = gson.fromJson(input.readLine(), ResponseData::class.java)

                        Log.i("XXXXXXXXXX", responseData.respCode.toString())
                        client.close()
                        val walletRepository = WalletRepository(application)
                        val address = responseData.data?.get("WalletAddress")?.toString()
                        Log.i("XXXXXXXXXX", address.toString())
                        if (address!=null && responseData.respCode==200){
                            val wallet = Wallet(
                                null,
                                address,
                                0.00,
                                walletNameEdit.text.toString()
                            )
                            walletRepository.insert(wallet)
                        }




                        withContext(Dispatchers.Main) {
                            createWalletButton.text = "Create Wallet"
                            createWalletButton.isEnabled = true
                        }
                    } catch (ex:Exception){
//                       val toast = Toast.makeText(this, ex.toString(), 0)
                        withContext(Dispatchers.Main) {
                           errorText.text = ex.message.toString()
                            createWalletButton.text = "Create Wallet"
                            createWalletButton.isEnabled = true
                        }
                    }
                }

            }

        }
    }
}
package com.example.kuracoin_wallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kuracoin_wallet.adapters.WalletsAdapter
import com.example.kuracoin_wallet.databinding.ActivityMainBinding
import com.example.kuracoin_wallet.db.WalletRepository
import com.example.kuracoin_wallet.models.TotalBalance
import com.example.kuracoin_wallet.models.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding
    private lateinit var createWalletButton: Button
    private lateinit var receiverAddressEdit:EditText
    private lateinit var privateKeyEdit:EditText
    private lateinit var walletsRecycler:RecyclerView
    private lateinit var totalBalance: TextView
    private var viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        walletsRecycler = findViewById(R.id.wallet_recycler)
        walletsRecycler.layoutManager = viewManager

        // load wallet data from local
        loadWalletsFromLocal()

        createWalletButton = findViewById(R.id.create_wallet_btn)
        totalBalance = findViewById(R.id.total_balance)


        createWalletButton.setOnClickListener{
            val intent = Intent(this, CreateWallet::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {

        super.onResume()
        loadWalletsFromLocal()
        totalBalance.text = TotalBalance.totalBalance.toString() +" DAu"
    }


    fun loadWalletsFromLocal(){
        val walletRepository = WalletRepository(this.application)
        var wallets:List<Wallet> = listOf<Wallet>()
        lifecycleScope.launch{
            withContext(Dispatchers.IO){
                wallets = walletRepository.getAll()
                TotalBalance.totalBalance = 0.00
                wallets.forEach {
                    Log.i("XXXXX Wallet",it.address)

                    TotalBalance.totalBalance = TotalBalance.totalBalance + it.balance
                }

            }

            withContext(Dispatchers.Main){
                walletsRecycler.adapter = WalletsAdapter(wallets.toTypedArray(), applicationContext,WalletsAdapter.OnClickListener{
                    if(it.id!=null){
                        openSingleWalletView(it.id)
                    }

                })
            }
        }
    }

    fun openSingleWalletView(id:Int){
        val intent = Intent(this, SingleWallet::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}
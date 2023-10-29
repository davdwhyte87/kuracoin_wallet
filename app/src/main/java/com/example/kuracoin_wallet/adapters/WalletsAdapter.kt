package com.example.kuracoin_wallet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kuracoin_wallet.R
import com.example.kuracoin_wallet.models.Wallet


class WalletsAdapter(
    val arrayList: Array<Wallet>,
    val context: Context,
    val onClickListener: OnClickListener
)
    : RecyclerView.Adapter<WalletsAdapter.NotesViewHolder>(){

    var onItemClick:((Wallet)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletsAdapter.NotesViewHolder {
        var root = LayoutInflater.from(parent.context).inflate(R.layout.wallets_list_item, parent,false)
        return NotesViewHolder(root)
    }

    override fun onBindViewHolder(holder: WalletsAdapter.NotesViewHolder, position: Int) {
        holder.bind(arrayList.get(position))
        holder.xbinding.setOnClickListener {
            onClickListener.onClick(arrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    inner class NotesViewHolder( val xbinding: View): RecyclerView.ViewHolder(xbinding){
        init {
            xbinding.setOnClickListener {
                onItemClick?.invoke(arrayList[adapterPosition])
            }
        }
        fun bind(wallet:Wallet){
            xbinding.findViewById<TextView>(R.id.wallet_balance).text = wallet.balance.toString() +" DAu"
            xbinding.findViewById<TextView>(R.id.wallet_address).text = wallet.address
            xbinding.findViewById<TextView>(R.id.wallet_name).text = wallet.walletName

        }



    }

    class OnClickListener(val clickListener: (wallet: Wallet) -> Unit) {
        fun onClick(wallet: Wallet) = clickListener(wallet)
    }
}
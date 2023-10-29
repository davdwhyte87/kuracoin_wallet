package com.example.kuracoin_wallet.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kuracoin_wallet.R
import com.example.kuracoin_wallet.models.BlockData


class ChainAdapter(
    val arrayList: Array<BlockData>,
    val context: Context,
    val onClickListener: OnClickListener
)
    : RecyclerView.Adapter<ChainAdapter.ViewHolder>(){

    var onItemClick:((BlockData)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChainAdapter.ViewHolder {
        var root = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_item, parent,false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ChainAdapter.ViewHolder, position: Int) {
        holder.bind(arrayList.get(position))
        holder.xbinding.setOnClickListener {
            onClickListener.onClick(arrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    inner class ViewHolder( val xbinding: View): RecyclerView.ViewHolder(xbinding){
        init {
            xbinding.setOnClickListener {
                onItemClick?.invoke(arrayList[adapterPosition])
            }
        }
        fun bind(blockData:BlockData){
            xbinding.findViewById<TextView>(R.id.amount).text = "Amount: "+blockData.Amount.toString()+" DAu"
            xbinding.findViewById<TextView>(R.id.sender).text = "Sender: "+blockData.Sender
            xbinding.findViewById<TextView>(R.id.receiver).text = "Receiver: "+blockData.Receiver
            xbinding.findViewById<TextView>(R.id.date).text = "Date: "+blockData.CreatedAt


        }



    }

    class OnClickListener(val clickListener: (blockData: BlockData) -> Unit) {
        fun onClick(blockData: BlockData) = clickListener(blockData)
    }
}
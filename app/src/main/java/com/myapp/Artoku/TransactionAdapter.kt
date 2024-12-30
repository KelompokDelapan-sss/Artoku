package com.myapp.Artoku

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val transactionList: ArrayList<TransactionModel>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    // Interface untuk listener klik
    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    // Menetapkan listener klik
    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    // Fungsi untuk format angka dengan pemisah ribuan
    private fun formatNumber(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return formatter.format(value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTransaction = transactionList[position]

        holder.tvTransactionTitle.text = currentTransaction.title // Judul transaksi

        // Menentukan warna berdasarkan jenis transaksi
        if (currentTransaction.type == 1) {
            holder.tvTransactionAmount.setTextColor(Color.parseColor("#ff9f1c")) // Warna untuk pengeluaran
        } else {
            holder.tvTransactionAmount.setTextColor(Color.parseColor("#2ec4b6")) // Warna untuk pemasukan
        }

        // Format angka amount dengan pemisah ribuan
        val formattedAmount = formatNumber(currentTransaction.amount?.toDouble() ?: 0.0)
        holder.tvTransactionAmount.text = formattedAmount // Menampilkan jumlah yang diformat

        holder.tvCategory.text = currentTransaction.category

        // Format tanggal transaksi
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID"))
        val result = Date(currentTransaction.date!!)
        holder.tvDate.text = simpleDateFormat.format(result)

        // Menentukan ikon berdasarkan jenis transaksi
        if (currentTransaction.type == 1) {
            holder.typeIcon.setImageResource(R.drawable.ic_moneyout_svgrepo_com) // Ikon untuk pengeluaran
        } else {
            holder.typeIcon.setImageResource(R.drawable.ic_moneyin_svgrepo_com) // Ikon untuk pemasukan
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    // ViewHolder yang menyimpan referensi ke item
    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionTitle: TextView = itemView.findViewById(R.id.tvTransactionTitle)
        val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val typeIcon: ImageView = itemView.findViewById(R.id.typeIcon)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}


package com.myapp.Artoku

object CategoryOptions {

    fun expenseCategory(): ArrayList<String> {
        val listExpense = ArrayList<String>()
        listExpense.add("Makanan/Minuman")
        listExpense.add("Transportasi")
        listExpense.add("Hiburan")
        listExpense.add("Pendidikan")
        listExpense.add("Hutang")
        listExpense.add("Belanja")
        listExpense.add("Komunikasi")
        listExpense.add("Investasi")
        listExpense.add("Kesehatan")
        listExpense.add("Pengeluaran yang lain")

        return listExpense
    }

    fun incomeCategory(): ArrayList<String> {
        val listIncome = ArrayList<String>()
        listIncome.add("Gaji")
        listIncome.add("Penghargaan")
        listIncome.add("Hadiah")
        listIncome.add("Pengembalian Investasi")
        listIncome.add("Pemasukan yang lain")

        return listIncome
    }
}
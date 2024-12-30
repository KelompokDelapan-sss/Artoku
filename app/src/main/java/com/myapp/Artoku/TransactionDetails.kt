package com.myapp.Artoku
import java.text.NumberFormat
import java.util.Locale

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import android.os.Build
import android.view.View

class TransactionDetails : AppCompatActivity() {

    private lateinit var tvAmountDetails: TextView
    private lateinit var tvTypeDetails: TextView
    private lateinit var tvTitleDetails: TextView
    private lateinit var tvDateDetails: TextView
    private lateinit var tvNoteDetails: TextView
    private lateinit var tvCategoryDetails: TextView
    private lateinit var detailsTitle: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        //---back button---
        val backButton: ImageButton = findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }
        //--------

        //--update data--
        val updateData: ImageButton = findViewById(R.id.updateData)
        updateData.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("title").toString()
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        //------

        deleteData()

        initView() //call method for initialized each ui item
        setValuesToViews() //call method for output the value on db
    }

    private fun deleteData() {
        val deleteData: ImageButton = findViewById(R.id.deleteData)
        val alertBox = AlertDialog.Builder(this)
        deleteData.setOnClickListener {
            alertBox.setTitle("Apa lu yakin?")
            alertBox.setMessage("Apa lu mau hapus transaksi ini?")
            alertBox.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                deleteRecord(
                    intent.getStringExtra("transactionID").toString()
                )
            }
            alertBox.setNegativeButton("No") { _: DialogInterface, _: Int -> }
            alertBox.show()
        }
    }

    private fun deleteRecord(transactionID: String) {
        // --Initialize Firebase Auth and firebase database--
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        if (uid != null) {
            val dbRef = FirebaseDatabase.getInstance().getReference(uid).child(transactionID) //initialize database with uid as the parent
            val mTask = dbRef.removeValue()

            mTask.addOnSuccessListener {
                Toast.makeText(this, "Data Transaksi dihapus", Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { error ->
                Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initView() { //initialized ui item id
        tvAmountDetails = findViewById(R.id.tvAmountDetails)
        tvTypeDetails = findViewById(R.id.tvTypeDetails)
        tvTitleDetails = findViewById(R.id.tvTitleDetails)
        tvDateDetails = findViewById(R.id.tvDateDetails)
        tvNoteDetails = findViewById(R.id.tvNoteDetails)
        tvCategoryDetails = findViewById(R.id.tvCategoryDetails)
        detailsTitle = findViewById(R.id.transactionDetailsTitle)
    }

    private fun setValuesToViews(){

        tvTitleDetails.text = intent.getStringExtra("title")
        val type: Int = intent.getIntExtra("type", 0)
        val amount: Double = intent.getDoubleExtra("amount", 0.0)

        // Format jumlah transaksi sebelum ditampilkan
        tvAmountDetails.text = formatNumber(amount)

        if (type == 1) {
            tvTypeDetails.text = "Transaksi Pengeluaran"
            tvAmountDetails.setTextColor(Color.parseColor("#AD1B1E"))
            detailsTitle.setBackgroundResource(R.drawable.expanse)
            window.statusBarColor = ContextCompat.getColor(this, R.color.marun)
        } else {
            tvTypeDetails.text = "Transaksi Pemasukan"
            tvAmountDetails.setTextColor(Color.parseColor("#2ec4b6"))
            detailsTitle.setBackgroundResource(R.drawable.income)
            window.statusBarColor = ContextCompat.getColor(this, R.color.toska)
        }

        val date: Long = intent.getLongExtra("date", 0)
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
        val result = Date(date)
        tvDateDetails.text = simpleDateFormat.format(result)

        tvCategoryDetails.text = intent.getStringExtra("category")
        tvNoteDetails.text = intent.getStringExtra("note")
    }

    private fun openUpdateDialog(title: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog, null)

        mDialog.setView(mDialogView)

        //---Initialize item---
        val etTitle = mDialogView.findViewById<EditText>(R.id.titleUpdate)
        val etCategory = mDialogView.findViewById<AutoCompleteTextView>(R.id.categoryUpdate)
        val etAmount = mDialogView.findViewById<EditText>(R.id.amountUpdate)
        val etDate = mDialogView.findViewById<EditText>(R.id.dateUpdate)
        val btnUpdateData = mDialogView.findViewById<Button>(R.id.updateButton)
        val etNote = mDialogView.findViewById<EditText>(R.id.noteUpdate)
        //--------

        etTitle.setText(intent.getStringExtra("title").toString())
        etAmount.setText(formatNumber(intent.getDoubleExtra("amount", 0.0)))
        etNote.setText(intent.getStringExtra("note")).toString()

        val type: Int = intent.getIntExtra("type", 0)
        val transactionID = intent.getStringExtra("transactionID") //store transaction id

        val categoryOld = intent.getStringExtra("category")
        etCategory.setText(categoryOld)

        //---Set category options based on type (Expense or Income)---
        val listCategory: List<String> = if (type == 1) {
            // Expense categories
            CategoryOptions.expenseCategory()
        } else {
            // Income categories
            CategoryOptions.incomeCategory()
        }

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listCategory)
        etCategory.setAdapter(categoryAdapter)

        //---Date initialization and picker---
        val date: Long = intent.getLongExtra("date", 0)
        val cal = Calendar.getInstance()
        val getDate = Date(date)
        cal.time = getDate

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID"))
        val resultParse = simpleDateFormat.format(getDate)
        etDate.setText(resultParse)

        var dateUpdate: Long = intent.getLongExtra("date", 0)
        etDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = "$day/${month + 1}/$year"
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID"))
                val theDate = sdf.parse(selectedDate)
                dateUpdate = theDate!!.time
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }
        //-------

        mDialog.setTitle("Updating $title's Transaction")
        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateTransactionData(
                transactionID.toString(),
                type,
                etTitle.text.toString(),
                etCategory.text.toString(),
                etAmount.text.toString().replace(".", "").replace(",", ".").toDouble(),
                dateUpdate,
                etNote.text.toString(),
                dateUpdate * -1
            )
            Toast.makeText(applicationContext, "Transaction Data Updated", Toast.LENGTH_LONG).show()

            tvTitleDetails.text = etTitle.text.toString()
            tvAmountDetails.text = formatNumber(etAmount.text.toString().replace(".", "").replace(",", ".").toDouble())
            tvNoteDetails.text = etNote.text.toString()
            tvCategoryDetails.text = etCategory.text.toString()

            val dateFormatted = Date(dateUpdate)
            tvDateDetails.text = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID")).format(dateFormatted)

            alertDialog.dismiss()
        }
    }


    private fun formatNumber(amount: Double): String {
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        return formatter.format(amount)
    }

    private fun updateTransactionData(
        transactionID:String,
        type: Int,
        title: String,
        category: String,
        amount: Double,
        date: Long,
        note: String,
        invertedDate: Long
    ){
        // --Initialize Firebase Auth and firebase database--
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        if (uid != null) {
            val dbRef = FirebaseDatabase.getInstance().getReference(uid) //initialize database with uid as the parent
            val transactionInfo = TransactionModel(transactionID, type, title, category, amount, date, note, invertedDate)
            dbRef.child(transactionID).setValue(transactionInfo)
        }
    }
}

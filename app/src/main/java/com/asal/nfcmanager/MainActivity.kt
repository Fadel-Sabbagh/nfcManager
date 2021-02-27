package com.asal.nfcmanager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NdefRecord.TNF_MIME_MEDIA
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.google.gson.Gson
import com.asal.nfcmanager.database.AppDatabase
import com.asal.nfcmanager.database.models.AppTransaction
import com.asal.nfcmanager.helpers.Constants
import com.asal.nfcmanager.models.Balance
import com.asal.nfcmanager.network.ExchangeRatesApi
import com.asal.nfcmanager.network.models.ExchangeRates
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.nio.charset.StandardCharsets


class MainActivity : BaseActivity() {

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null
    lateinit var db: AppDatabase
    var balance1 = Balance(1, "Food", 10.0)
    var balance2 = Balance(2, "Clothes", 20.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDatabase()
        initNfcAdapter()
        initViews()
        AsyncTask.execute { // Insert Data
            var transactions = db.transactionDao().getAll()
            runOnUiThread {
                Toast.makeText(this,"Transactions count in db : " + transactions.size.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initDatabase() {
        db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, Constants.DATABASE_NAME
        ).build()
    }

    private fun callEcchangeRatesApi(balance: Balance,amount: Double) {
        hud.show()
        ExchangeRatesApi.instance.GetRates().enqueue(object : Callback<ExchangeRates> {
            override fun onResponse(call: Call<ExchangeRates>, response: Response<ExchangeRates>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity,"EUR to USD rate : " + response.body()!!.rates.usd.toString(), Toast.LENGTH_SHORT).show()
                    AsyncTask.execute { // Insert Data
                        var transaction = AppTransaction(0, balance.balanceNo, balance.balanceAmount,balance.balanceAmount - amount,(balance.balanceAmount - amount) * response.body()!!.rates.usd,response.body()!!.rates.usd)
                        db.transactionDao().insertAll(transaction)
                        runOnUiThread {
                            Toast.makeText(this@MainActivity,"Transaction has beend added successfully", Toast.LENGTH_SHORT).show()
                            balance.balanceAmount -= amount
                            updateBalancesOnCard()
                        }
                    }

                } else {
                    Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
                hud.dismiss()
            }

            override fun onFailure(call: Call<ExchangeRates>, t: Throwable) {
                hud.dismiss()
                Toast.makeText(this@MainActivity, R.string.network_error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter
        if (adapter == null) {
            Toast.makeText(this,R.string.nfc_not_found,Toast.LENGTH_SHORT).show()
        }else if (!adapter!!.isEnabled())
        {
            Toast.makeText(this,R.string.nfc_not_enabled,Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        reset_balances.setOnClickListener {
            resetAccountBalances()
        }
        cut_5.setOnClickListener {
            //TODO Better to check from the card
            if (balance2.balanceAmount >= 5) {
                callEcchangeRatesApi(balance2,5.0)
            } else {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        }
        cut_2.setOnClickListener {
            //TODO Better to check from the card
            if (balance1.balanceAmount >= 2) {
                callEcchangeRatesApi(balance1,2.0)
            } else {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetAccountBalances() {
        resetBalances()
        updateBalancesOnCard()
    }

    private fun updateBalancesOnCard() {
        val message = NfcUtils.prepareBalancesToWrite(balance1, balance2, this)
        val writeResult = tag!!.writeData(tagId!!, message)
        if (writeResult) {
            showToast("Balances Write succeeded")
        } else {
            showToast("Balances Write failed!")
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            adapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e(getTag(), "Error enabling NFC foreground dispatch", ex)
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e(getTag(), "Error disabling NFC foreground dispatch", ex)
        }
    }

    private fun getTag() = "MainActivity"

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val gson = Gson()
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = WritableTag(tagFromIntent)
            if (tag != null) {
                tagId = tag!!.tagId
                if (tag!!.ndefMessage != null) {
                    for (record in tag!!.ndefMessage!!.records) {
                        if (record.tnf == TNF_MIME_MEDIA) {
                            var payLoadGson = String(record.payload, StandardCharsets.UTF_8)
                            try
                            {
                                val balance: Balance = gson.fromJson(payLoadGson, Balance::class.java)
                                Toast.makeText(this, balance.balanceName + " : " + balance.balanceAmount.toString(), Toast.LENGTH_SHORT).show()
                            }catch (ex: Exception)
                            {
                                Toast.makeText(this, R.string.parsing_error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }else
                {
                    resetBalances()
                    Toast.makeText(this, R.string.ndef_formatable_card, Toast.LENGTH_SHORT).show()
                }
            }else
            {
                Toast.makeText(this, R.string.unsupported_tag_tapped, Toast.LENGTH_SHORT).show()
            }
        } catch (e: FormatException) {
            Log.e(getTag(), getString(R.string.unsupported_tag_tapped), e)
            return
        }
    }

    fun resetBalances()
    {
        balance1.balanceAmount = 10.0
        balance2.balanceAmount = 20.0
    }
}

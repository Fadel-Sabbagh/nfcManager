package com.asal.nfcmanager

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Parcelable
import android.util.Log
import com.google.common.io.BaseEncoding
import com.asal.nfcmanager.models.Balance

class NfcUtils {
    companion object {
        private val mimeType: String = ""

        fun getUID(intent: Intent): String {
            val myTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            return BaseEncoding.base16().encode(myTag.id)
        }

        fun getData(rawMsgs: Array<Parcelable>): String {
            val msgs = arrayOfNulls<NdefMessage>(rawMsgs.size)
            for (i in rawMsgs.indices) {
                msgs[i] = rawMsgs[i] as NdefMessage
            }

            val records = msgs[0]!!.records

            var recordData = ""

            for (record in records) {
                recordData += record.toString() + "\n"
            }

            return recordData
        }

        fun getIntentFilters(): Array<IntentFilter> {
            val ndefFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            try {
                ndefFilter.addDataType("application/vnd.com.tickets")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                Log.e("NfcUtils", "Problem in parsing mime type for nfc reading", e)
            }

            return arrayOf(ndefFilter)
        }

        fun prepareBalancesToWrite(balance1: Balance, balance2: Balance, context: Context): NdefMessage {
            val message: NdefMessage
            val typeBytes = mimeType.toByteArray()
            val records = ArrayList<NdefRecord>()
            val payload1 = balance1.toString().toByteArray()
            val record1 = NdefRecord(NdefRecord.TNF_MIME_MEDIA, typeBytes, null, payload1)
            val payload2 = balance2.toString().toByteArray()
            val record2 = NdefRecord(NdefRecord.TNF_MIME_MEDIA, typeBytes, null, payload2)
            val record3 = NdefRecord.createApplicationRecord(context.packageName)
            records.add(record3)
            message = NdefMessage(arrayOf(record1, record2, record3))
            return message
        }
    }
}
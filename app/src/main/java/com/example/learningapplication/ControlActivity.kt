package com.example.learningapplication

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException



class ControlActivity: AppCompatActivity() {

    val control_led_on = findViewById<Button>(R.id.control_led_on)
    val control_led_off = findViewById<Button>(R.id.control_led_off)
    val control_led_disconnect = findViewById<Button>(R.id.control_led_disconnect)

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS).toString()

        ConnectToDevice(this).execute()

        control_led_on.setOnClickListener { sendCommand("a") }
        control_led_off.setOnClickListener { sendCommand("b") }
        control_led_disconnect.setOnClickListener { disconnect() }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(var context: Context) : AsyncTask<Void, Void, String>() {
        private var connectSucces: Boolean = true
        //private val context: Context

        /*init {
            this.context = c
        }*/

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "Please wait")
        }

        override fun doInBackground(vararg p0: Void?): String {
            //Check if bluetooth permissions have been granted
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            )
            //{ Toast.makeText(this, "First enable BLUETOOTH in settings.", Toast.LENGTH_LONG).show()
                try {
                    if (m_bluetoothSocket == null || !m_isConnected) {
                        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                        val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                        m_bluetoothSocket =
                            device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                        m_bluetoothSocket!!.connect()
                    }
                } catch (e: IOException) {
                    connectSucces = false
                    e.printStackTrace()
                }
            return null.toString()
        }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (!connectSucces) {
                    Log.i("data","Couldn't connect")
                }
                else {
                    m_isConnected = true
                }
                m_progress.dismiss()
        }
    }
}
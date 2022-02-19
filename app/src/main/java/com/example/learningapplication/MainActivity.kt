package com.example.learningapplication


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    lateinit var bAdapter:BluetoothAdapter
    lateinit var pairedDevices: Set<BluetoothDevice>

    private val REQUEST_CODE_ENABLE_BT:Int = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val tvBluetoothState = findViewById<TextView>(R.id.tvBluetoothState)
        val ivBluetoothConnection = findViewById<ImageView>(R.id.ivBluetoothConnection)
        //val btnBluetoothOn = findViewById<Button>(R.id.btnBluetoothOn)
        //val btnBluetoothOff = findViewById<Button>(R.id.btnBluetoothOff)
        //val btnPaired = findViewById<Button>(R.id.btnPaired)
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)

        bAdapter = BluetoothAdapter.getDefaultAdapter()

        //Check if bluetooth is available or not
        Toast.makeText(this, "This devices supports bluetooth", Toast.LENGTH_SHORT).show()

        //set image according to bluetooth state (on/off)
        if (bAdapter.isEnabled) {
            //Bluetooth is on
            ivBluetoothConnection.setImageResource(R.drawable.ic_bluetooth_on)
        } else {
            //Bluetooth is off
            ivBluetoothConnection.setImageResource((R.drawable.ic_bluetooth_off))
        }

        //Check if bluetooth permissions have been granted
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "First enable BLUETOOTH in settings.", Toast.LENGTH_LONG)
                .show()
            return
        }

        //Turn bluetooth on
        if (bAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth is already on", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
        }

        btnRefresh.setOnClickListener { pairedDeviceList() }
    }

    private fun pairedDeviceList() {
        val lvDevices = findViewById<ListView>(R.id.lvDevices)
        //Check if bluetooth permissions have been granted
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "First enable BLUETOOTH in settings.", Toast.LENGTH_LONG)
                .show()
            return
        }
        pairedDevices = bAdapter.bondedDevices
        //val deviceName = bAdapter.name
        val list: ArrayList<BluetoothDevice> = ArrayList()
        if (pairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in pairedDevices) {
                list.add(device)
                Log.i("device", "" + device)
            }
        } else {
            Toast.makeText(this, "No paired bluetooth devices found", Toast.LENGTH_SHORT).show()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lvDevices.adapter = adapter
        lvDevices.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ivBluetoothConnection = findViewById<ImageView>(R.id.ivBluetoothConnection)

        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    if (bAdapter.isEnabled) {
                        ivBluetoothConnection.setImageResource(R.drawable.ic_bluetooth_on)
                        Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
                    } else if (!bAdapter.isEnabled) {
                        //User denied to turn bluetooth on from confirmation dialogue
                        Toast.makeText(this, "Couldn't enable bluetooth", Toast.LENGTH_SHORT).show()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth enabling has been canceled", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }*/
}
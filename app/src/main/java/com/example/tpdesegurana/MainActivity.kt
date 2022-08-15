package com.example.tpdesegurana

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    private fun getCurrentCoordinates() {
        Toast.makeText(this@MainActivity, "Salvando dado", Toast.LENGTH_SHORT).show()
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
        val isNetworkEnabled = locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
        if (!isGPSEnabled && !isNetworkEnabled) {
            Log.d("Permissao", "Ative os serviços necessários")
        } else {
            if (isGPSEnabled) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        30000L, 0f, locationListener
                    )
                } catch (ex: SecurityException) {
                    Log.d("Permissao", "Erro de permissão")
                }
            } else if (isNetworkEnabled) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L, 0f, locationListener
                    )
                } catch (ex: SecurityException) {
                    Log.d("Permissao", "Erro de permissão")
                }
            }
        }
    }

    private val locationListener: LocationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val data = Calendar.getInstance().time
                val fmtdata = SimpleDateFormat("HH_mm_ss-dd_MM_yyyy")

                try {
                    if (
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED){
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                            (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE))
                        ){
                            callDialog(
                                "É preciso liberar WRITE_EXTERNAL_STORAGE",
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                            callDialog(
                                "É preciso liberar READ_EXTERNAL_STORAGE",
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                        } else {
                            ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                REQUEST_PERMISSIONS_CODE)
                            ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_PERMISSIONS_CODE)
                        }
                    } else {
                        if (isExternalStorageWritable() and isExternalStorageReadable()) {
                            val file = File(getExternalFilesDir(null), fmtdata.format(data) + ".crd")
                            BufferedWriter(FileWriter(file)).use {
                                it.write("${location.latitude} ${location.longitude}")
                            }
                        }
                    }
                }catch (e: Exception){
                    Log.e("File Error", "Não foi possível ler ou escrever o arquivo")
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

    val REQUEST_PERMISSIONS_CODE = 666

    fun callAccessLocation(view: View?) {
        val permissionAFL = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionACL = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissionAFL != PackageManager.PERMISSION_GRANTED &&
            permissionACL != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                callDialog(
                    "Não é possível pegar sua localização",
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_CODE
                )
            }
        } else {
            getCurrentCoordinates()
        }
    }

    private fun callDialog(
        mensagem: String,
        permissions: Array<String>
    ) {
        var mDialog = AlertDialog.Builder(this)
            .setTitle("Permissão")
            .setMessage(mensagem)
            .setPositiveButton("Ok")
            { dialog, id ->
                ActivityCompat.requestPermissions(
                    this@MainActivity, permissions,
                    REQUEST_PERMISSIONS_CODE
                )
                dialog.dismiss()
            }
            .setNegativeButton("Cancela")
            { dialog, id ->
                dialog.dismiss()
            }
        mDialog.show()
    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    fun segundaActivity(view: View) {
        val intent = Intent (this, ListActivity::class.java)
        startActivity(intent)
    }

    override fun onLocationChanged(p0: Location) {
        Toast.makeText(this, "Localização ativdada", Toast.LENGTH_LONG).show()
    }
}
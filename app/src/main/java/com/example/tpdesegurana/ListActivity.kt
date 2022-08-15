package com.example.tpdesegurana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val lista = getExternalFilesDir(null)!!.list()
        listaFile.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista!!.toList())
    }
}
package com.app.notesappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import com.app.notesappfirebase.databinding.ActivityNoteDetailBinding

class NoteDetail : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarofnotedetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent
        binding.run {
            gotoeditnote.setOnClickListener {
                val intent = Intent(it.context, EditNoteActivity::class.java)
                intent.putExtra("title", data.getStringExtra("title"))
                intent.putExtra("body", data.getStringExtra("body"))
                intent.putExtra("noteId", data.getStringExtra("noteId"))
                it.context.startActivity(intent)
            }
            titleofnotedetail.text = data.getStringExtra("title")
            contentofnotedetail.text = data.getStringExtra("body")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
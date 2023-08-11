package com.app.notesappfirebase

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.notesappfirebase.databinding.ActivityCreateNoteBinding
import com.app.notesappfirebase.home.NoteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class CreateNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbarAddNote)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.run {
            btnSaveNote.setOnClickListener {
                val title = etNoteTitle.text.toString()
                val body = etNodeBody.text.toString()
                if (title.isEmpty() || body.isEmpty()) {
                    Toast.makeText(
                        this@CreateNoteActivity,
                        "Both fields are required",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    val documentReference =
                        firebaseFirestore.collection("notes").document(firebaseUser.uid)
                            .collection("mynotes").document()
                    val note: MutableMap<String, Any> = mutableMapOf()
                    note.put("title", title)
                    note.put("body", body)
                    documentReference.set(note).addOnCompleteListener {
                        Toast.makeText(this@CreateNoteActivity, "Note created", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                        val noteActivityIntent =
                            Intent(this@CreateNoteActivity, NoteActivity::class.java)
                        noteActivityIntent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(noteActivityIntent)
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@CreateNoteActivity,
                            "Failed to create note",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
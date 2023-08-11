package com.app.notesappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.app.notesappfirebase.databinding.ActivityEditNoteBinding
import com.app.notesappfirebase.home.NoteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var data: Intent
    private lateinit var noteTitle: String
    private lateinit var noteBody: String

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarofeditnote)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = intent

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser as FirebaseUser

        noteTitle = data.getStringExtra("title").toString()
        noteBody = data.getStringExtra("body").toString()

        binding.run {
            edittitleofnote.setText(noteTitle)
            editcontentofnote.setText(noteBody)

            saveeditnote.setOnClickListener {
//                Toast.makeText(this@EditNoteActivity, "clicked", Toast.LENGTH_SHORT).show()
                val newTitle = edittitleofnote.text.toString()
                val newBody = editcontentofnote.text.toString()

                if (newTitle.isEmpty() || newBody.isEmpty()) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Both fields must not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val documentReference = data.getStringExtra("noteId")?.let { it1 ->
                        firebaseFirestore.collection("notes").document(firebaseUser.uid)
                            .collection("mynotes").document(
                                it1
                            )
                    }

                    val note = mutableMapOf<String, Any>()
                    note["title"] = newTitle
                    note["body"] = newBody
                    documentReference?.set(note)?.addOnSuccessListener {
                        Toast.makeText(
                            this@EditNoteActivity,
                            "Note Updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                        val noteActivityIntent =
                            Intent(this@EditNoteActivity, NoteActivity::class.java)
                        noteActivityIntent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(noteActivityIntent)
                    }?.addOnFailureListener {
                        Toast.makeText(
                            this@EditNoteActivity,
                            "Update failed",
                            Toast.LENGTH_SHORT
                        ).show()
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
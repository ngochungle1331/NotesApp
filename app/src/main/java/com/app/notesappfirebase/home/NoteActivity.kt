package com.app.notesappfirebase.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.notesappfirebase.*
import com.app.notesappfirebase.authentication.LoginActivity
import com.app.notesappfirebase.databinding.ActivityNoteBinding
import com.app.notesappfirebase.model.Note
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Random


class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var noteAdapter: FirestoreRecyclerAdapter<Note, NoteViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "All Notes"
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
        firebaseFirestore = FirebaseFirestore.getInstance()

        if (isConnectedToInternet()) {
            setData()
            setupViews()
        } else {
            val snackbar = Snackbar.make(
                binding.root,
                "No internet connection available.",
                Snackbar.LENGTH_LONG
            )
            snackbar.show()
        }
    }

    private fun setupViews() {
        binding.run {
            btnAddNewNote.setOnClickListener {
                startActivity(Intent(this@NoteActivity, CreateNoteActivity::class.java))
            }
        }
    }

    private fun setData() {
        val query =
            firebaseFirestore.collection("notes").document(firebaseUser.uid).collection("mynotes")
                .orderBy("title", Query.Direction.ASCENDING)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val note = document.toObject(Note::class.java)
                    Log.d("NoteActivity", "Note: $note")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("NoteActivity", "Error getting documents: ", exception)
            }

        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        noteAdapter = object : FirestoreRecyclerAdapter<Note, NoteViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.custom_note_layout, parent, false)
                return NoteViewHolder(view)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: Note) {

                val popupButton = holder.itemView.findViewById<ImageView>(R.id.menupopbutton)

                val colorCode = getRandomColor()
                holder.mnote.setBackgroundColor(holder.itemView.resources.getColor(colorCode, null))
                holder.noteTitle.text = model.title
                holder.noteContent.text = model.body

                val docId = noteAdapter.snapshots.getSnapshot(position).id

                holder.itemView.setOnClickListener {
                    val intent = Intent(it.context, NoteDetail::class.java)
                    intent.putExtra("title", model.title)
                    intent.putExtra("body", model.body)
                    intent.putExtra("noteId", docId)
                    it.context.startActivity(intent)
                }

                popupButton.setOnClickListener { v ->
                    val popupMenu = PopupMenu(v.context, v)
                    popupMenu.gravity = Gravity.END
                    popupMenu.menu.add("Edit").setOnMenuItemClickListener {
                        val intent = Intent(v.context, EditNoteActivity::class.java)
                        intent.putExtra("title", model.title)
                        intent.putExtra("body", model.body)
                        intent.putExtra("noteId", docId)
                        v.context.startActivity(intent)
                        return@setOnMenuItemClickListener false
                    }
                    popupMenu.menu.add("Delete").setOnMenuItemClickListener {
                        val documentReference =
                            firebaseFirestore.collection("notes").document(firebaseUser.uid)
                                .collection("mynotes").document(docId)
                        documentReference.delete().addOnSuccessListener {
                            Toast.makeText(v.context, "Note Deleted", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(v.context, "Delete failed", Toast.LENGTH_SHORT).show()
                        }
                        return@setOnMenuItemClickListener false
                    }
                    popupMenu.show()
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                noteAdapter.notifyDataSetChanged()
            }
        }

        binding.run {
            val staggeredGridLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rvNotes.layoutManager = staggeredGridLayoutManager
            rvNotes.adapter = noteAdapter
        }
    }

    private fun getRandomColor(): Int {
        val colorCode = arrayListOf<Int>()
        colorCode.add(R.color.gray)
        colorCode.add(R.color.pink)
        colorCode.add(R.color.green)
        colorCode.add(R.color.lightgreen)
        colorCode.add(R.color.skyblue)
        colorCode.add(R.color.color1)
        colorCode.add(R.color.color2)
        colorCode.add(R.color.color3)
        colorCode.add(R.color.color4)
        colorCode.add(R.color.color5)

        val random = Random()
        val number = random.nextInt(colorCode.size)
        return colorCode[number]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            firebaseAuth.signOut()
            finish()
            startActivity(Intent(this@NoteActivity, LoginActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitle: TextView
        val noteContent: TextView
        var mnote: LinearLayout

        init {
            noteTitle = itemView.findViewById(R.id.tvNoteName)
            noteContent = itemView.findViewById(R.id.tvNoteBody)
            mnote = itemView.findViewById(R.id.note)
        }
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onStart() {
        super.onStart()
        if (isConnectedToInternet()) {
            noteAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isConnectedToInternet()) {
            noteAdapter.stopListening()
        }
    }
}
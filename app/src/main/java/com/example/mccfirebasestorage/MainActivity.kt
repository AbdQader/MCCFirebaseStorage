package com.example.mccfirebasestorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.mccfirebasestorage.adapters.ImageAdapter
import com.example.mccfirebasestorage.models.Image
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ImageAdapter
    private lateinit var data: ArrayList<Image>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the database, adapter, and data
        db = FirebaseFirestore.getInstance()
        data = ArrayList()
        adapter = ImageAdapter(this, data)

        // give the adapter to recycler view adapter
        rv_images.adapter = adapter
        rv_images.setHasFixedSize(true)

        // call "fetchImages" function
        fetchImages()

        // when user click on "add" floating action button go to "AddImage" activity
        fab.setOnClickListener {
            startActivity(Intent(this, AddImage::class.java))
        }

    }

    // this function to get all images from firebase storage
    private fun fetchImages() {
        db.collection("images")
            .addSnapshotListener { querySnapshot, error ->
                if (error == null)
                {
                    // clear the "data" array before fetching the images
                    data.clear()
                    querySnapshot!!.documents.forEach { document ->
                        data.add(Image(document["imageUri"].toString()))
                    }
                    // notify the adapter
                    adapter.notifyDataSetChanged()

                    // if the array is empty show "txtNoUsers" text view
                    if (data.isEmpty())
                    {
                        txtNoUsers.visibility = View.VISIBLE // show the txtNoUsers
                        progressBar.visibility = View.GONE   // hide the progressBar
                    } else {
                        progressBar.visibility = View.GONE // hide the progressBar
                        txtNoUsers.visibility = View.GONE  // hide the txtNoUsers
                    }
                } else {
                    Toast.makeText(this, "An error occurs", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
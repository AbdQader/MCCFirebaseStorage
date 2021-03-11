package com.example.mccfirebasestorage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_image.*
import java.io.ByteArrayOutputStream

class AddImage : AppCompatActivity() {

    private lateinit var storageRef: StorageReference
    private lateinit var db: FirebaseFirestore
    private var imageURI: Uri? = null

    private val PICK_IMAGE_REQUEST = 7
    private val TAG = "abd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        // initialize the firestore database
        db = FirebaseFirestore.getInstance()

        // initialize the storage and get the reference and create child images inside the reference
        storageRef = Firebase.storage.reference.child("images")
        // OR => storageRef = FirebaseStorage.getInstance().reference.child("images")

        // to display back arrow
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // when click on choose image button, open gallery and choose image
        btnChooseImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // when user click on "btnUploadImage" button call "uploadImageToStorage" function
        btnUploadImage.setOnClickListener {
            uploadImageToStorage()
        }
    }

    // when click on back arrow finish the current activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK)
        {
            if (data != null && data.data != null)
            {
                imageURI = data.data
                Log.e(TAG, imageURI.toString())
                // set the selected image in "imageViewChoose" image view
                Glide.with(this).load(imageURI).into(imageViewChoose)
            }
        }
    }

    // this function to upload the selected image to firebase storage
    private fun uploadImageToStorage() {
        // to check if the user chosen an image or not
        if (imageURI != null)
        {
            // this code to make the image smaller size
            val bitmap = (imageViewChoose.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // to give the image a unique name
            val childRef = storageRef.child(System.currentTimeMillis().toString() + "_image.png")

            // to upload the image
            childRef.putBytes(data)
                .addOnSuccessListener {

                    // hide the progress ratio text and horizontal progress bar
                    txtProgressRatio.visibility = View.GONE
                    horizontalProgressBar.visibility = View.GONE

                    // show a message for the user
                    Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()

                    // to get the image link and store it in the database
                    childRef.downloadUrl.addOnSuccessListener { uri ->
                        addImageToDB(uri.toString())
                    }

                    // finish this activity after uploading the image
                    finish()
                }
                .addOnProgressListener { taskSnapshot ->
                    // show the progress ratio text and horizontal progress bar
                    txtProgressRatio.visibility = View.VISIBLE
                    horizontalProgressBar.visibility = View.VISIBLE
                    // to get the progress ratio
                    val progress = (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    // give the progress ratio to "txtProgressRatio" & "horizontalProgressBar"
                    txtProgressRatio.text = "$progress %"
                    horizontalProgressBar.progress = progress.toInt()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message!!)
                }
        } else {
            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show()
        }

    }

    // this function adds the image uri to the database
    private fun addImageToDB(imageUri: String) {
        db.collection("images")
            .add(mapOf("imageUri" to imageUri))
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "Image Added Successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message!!)
            }
    }

}
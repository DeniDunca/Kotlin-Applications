package com.example.drawingapp

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawingView = findViewById<DrawingView>(R.id.drawingView)
        drawingView.setSizeForBrush(20.toFloat())

        var llColors = findViewById<LinearLayout>(R.id.llColors)
        mImageButtonCurrentPaint = llColors[2] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_selected))

        val ibBrush = findViewById<ImageButton>(R.id.ibBrush)
        ibBrush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        val ibGallery = findViewById<ImageButton>(R.id.ibGallery)
        ibGallery.setOnClickListener {
            if(isReadStorageAllowed()){
                val pickPhotoIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(pickPhotoIntent, GALLERY)

            }else{
                requestStoragePermission()
            }
        }

        val ibUndo = findViewById<ImageButton>(R.id.ibUndo)
        ibUndo.setOnClickListener{
            drawingView.onClickUndo()
        }

        val ibSave = findViewById<ImageButton>(R.id.ibSave)
        val flDrawingView = findViewById<FrameLayout>(R.id.fl_drawing_view_container)
        ibSave.setOnClickListener{
            if(isReadStorageAllowed()){
                BitmapAsyncTask(getBitmapFromView(flDrawingView)).execute()
            }else{
                requestStoragePermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ivBackground = findViewById<ImageView>(R.id.ivBackground)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                try{
                    if(data!!.data != null){
                        ivBackground.visibility = View.VISIBLE
                        ivBackground.setImageURI(data.data)
                    }else{
                        Toast.makeText(this@MainActivity, " Error", Toast.LENGTH_SHORT).show()
                    }
                }catch(e: java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        val drawingView = findViewById<DrawingView>(R.id.drawingView)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallButton = brushDialog.findViewById<ImageButton>(R.id.ibSmallBrush)
        smallButton.setOnClickListener {
            drawingView.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        val mediumButton = brushDialog.findViewById<ImageButton>(R.id.ibMediumBrush)
        mediumButton.setOnClickListener {
            drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        val largeButton = brushDialog.findViewById<ImageButton>(R.id.ibLargeBrush)
        largeButton.setOnClickListener {
            drawingView.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()

    }

    fun paintClicked(view: View){
        if(view !== mImageButtonCurrentPaint){
            val imageButton = view as ImageButton

            val colorTag = imageButton.tag.toString()

            val drawingView = findViewById<DrawingView>(R.id.drawingView)
            drawingView.setColor(colorTag)

            imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_selected))
            mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet))

            mImageButtonCurrentPaint = view
        }
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){

                Toast.makeText(this, "Need permission to add a background image.", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@MainActivity, "Permission granted now you can read the storage files.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isReadStorageAllowed():Boolean{
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun getBitmapFromView(view: View) : Bitmap{
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if(bgDrawable != null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }

        view.draw(canvas)

        return returnedBitmap
    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap):AsyncTask<Any, Void, String>(){

        private lateinit var mProgressDialog : Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result = ""
            if(mBitmap != null){
                try{
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val f = File(externalCacheDir!!.absoluteFile.toString() + File.separator + "DrawingApp_" + System.currentTimeMillis() / 1000 + ".png")

                    val fos = FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    fos.close()
                    result = f.absolutePath

                }catch(e: Exception){
                    result = ""
                    e.printStackTrace()
                }
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            if(!result!!.isEmpty()){
                Toast.makeText(this@MainActivity, "File saved successfully : $result",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity, " Something went wrong while saving the file.", Toast.LENGTH_SHORT).show()
            }

            MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result), null){
                path,  uri -> val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.type = "image/png"

                startActivity(
                    Intent.createChooser(
                        shareIntent, "Share"
                    )
                )
            }
        }

        private fun showProgressDialog(){
            mProgressDialog = Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.dialog_custom_progress)
            mProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            mProgressDialog.dismiss()
        }

    }

    companion object{
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
}
package com.muchi.muchiscanner.activity

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.app.ActivityCompat.checkSelfPermission
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import com.labters.documentscanner.ImageCropActivity
import com.labters.documentscanner.helpers.ScannerConstants.selectedImageBitmap
import com.muchi.muchiscanner.R
import com.muchi.muchiscanner.helper.RequestPermissionHelper.Companion.requestAllPermission
import com.muchi.muchiscanner.helper.customDirectory
import com.muchi.muchiscanner.util.CustomActivity
import com.muchi.muchiscanner.util.PermissionUtil
import com.muchi.muchiscanner.util.PermissionUtil.PermissionResultCallback
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : CustomActivity(), OnRequestPermissionsResultCallback,
    PermissionResultCallback {

    companion object {
        private const val TAG = "MainActivity"
    }

    @BindView(R2.id.scan)
    lateinit var scan: FloatingActionButton
    @BindView(R2.id.image)
    lateinit var image: ImageView
    @BindView(R2.id.save)
    lateinit var save: ImageView
    @BindView(R2.id.imagePlaceholder)
    lateinit var imagePlaceholder: ImageView
    @BindView(R2.id.progressBar)
    lateinit var progressBar: ProgressBar

    lateinit var mCurrentPhotoPath: String
    private var selectedImage: Uri? = null
    private var permissionUtil: PermissionUtil? = null
    private var isPermissionGranted = false
    private var typeScan = 0

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        permissionUtil = PermissionUtil(this)
        permissionUtil?.checkPermission(requestAllPermission(), 1)

        scan.setOnClickListener {
            if(checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ){
                bottomSheetScan()
            } else {
                permissionUtil?.checkPermission(requestAllPermission(), 1)
            }
        }

        image.setOnClickListener {
            if(typeScan == 1){
                try {
                    val inputStream = selectedImage?.let { contentResolver.openInputStream(it) }
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    selectedImageBitmap = bitmap
                    startActivityForResult(Intent(this, ImageCropActivity::class.java), 1234)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if(typeScan == 2){
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    Uri.parse(mCurrentPhotoPath)
                )

                startActivityForResult(Intent(this, ImageCropActivity::class.java), 1234)
            }
        }

        save.setOnClickListener {
            ConvertPDF().execute("")
        }
    }

    override fun NeverAskAgain(request_code: Int) {
        Log.i(TAG, "PERMISSION NEVER ASK AGAIN")
    }

    override fun PartialPermissionGranted(
        request_code: Int,
        granted_permissions: ArrayList<String>?
    ) {
        Log.i(TAG, "PERMISSION PARTIAL GRANTED")
    }

    override fun PermissionGranted(request_code: Int) {
        Log.i(TAG, "PERMISSION GRANTED")
        isPermissionGranted = true
    }

    override fun PermissionDenied(request_code: Int) {
        Log.i(TAG, "PERMISSION DENIED")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        permissionUtil?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1111 && resultCode == RESULT_OK && data != null) {
            selectedImage = data.data

            try {
                val inputStream = selectedImage?.let { contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                selectedImageBitmap = bitmap
                startActivityForResult(Intent(this, ImageCropActivity::class.java), 1234)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            typeScan = 1
        } else if (requestCode == 1231 && resultCode == RESULT_OK) {
            selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                Uri.parse(mCurrentPhotoPath)
            )

            startActivityForResult(Intent(this, ImageCropActivity::class.java), 1234)

            typeScan = 2
        } else if (requestCode == 1234 && resultCode == RESULT_OK) {
            if (selectedImageBitmap != null) {
                image.setImageBitmap(selectedImageBitmap)
                SaveToInternalStorage().execute(selectedImageBitmap)
            } else
                showToast("Not OK")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun bottomSheetScan(){
        @SuppressLint("InflateParams") val view: View = layoutInflater.inflate(
            R.layout.bs_scan,
            null
        )
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val imgClose: ImageView = view.findViewById(R.id.imgClose)
        val btnKamera: Button = view.findViewById(R.id.btnKamera)
        val btnGallery: Button = view.findViewById(R.id.btnGallery)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)

        dialog.show()

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnKamera.setOnClickListener {
            dialog.dismiss()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(packageManager) != null) {
                var photoFile: File? = null

                try {
                    val timeStamp = SimpleDateFormat("yyyyMMdd").format(Date())
                    val imageFileName = resources.getString(R.string.app_name).replace(" ", "")+"_" + timeStamp + "_"
                    val image = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        customDirectory(1)
                    )

                    photoFile = image
                    mCurrentPhotoPath = "file:" + image.absolutePath
                } catch (ex: IOException) {
                    Log.i(TAG, "IOException")
                }

                if (photoFile != null) {
                    @Suppress("NAME_SHADOWING") val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    startActivityForResult(cameraIntent, 1231)
                }
            }
        }

        btnGallery.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1111)
        }
    }

    @SuppressLint("StaticFieldLeak") // Harus Dicari solusi kebocoran memory
    inner class ConvertPDF : AsyncTask<String?, Void?, Boolean>() {
        @SuppressLint("SimpleDateFormat")
        override fun doInBackground(vararg string: String?): Boolean {
            val document = Document()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val fileName = resources.getString(R.string.app_name).replace(" ", "")+"_" + timeStamp

            PdfWriter.getInstance(
                document,
                FileOutputStream("${customDirectory(2)}/${fileName}.pdf")
            )
            document.open()

            val image = Image.getInstance("${customDirectory(0)}/image_temp.jpg")
            val scaler: Float = (document.pageSize.width - document.leftMargin()
                    - document.rightMargin() - 0) / image.width * 100 // 0 means you have no indentation. If you have any, change it.

            image.scalePercent(scaler)
            image.alignment = Image.ALIGN_CENTER or Image.ALIGN_TOP

            document.add(image)
            document.close()

            return true
        }

        override fun onPostExecute(boolean: Boolean) {
            super.onPostExecute(boolean)
            progressBar.visibility = GONE
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = VISIBLE
        }
    }


    @SuppressLint("StaticFieldLeak") // Harus Dicari solusi kebocoran memory
    inner class SaveToInternalStorage : AsyncTask<Bitmap?, Void?, Boolean>() {
        override fun doInBackground(vararg bitmap: Bitmap?): Boolean {
            val path = File(customDirectory(0), "image_temp.jpg")
            var fos: FileOutputStream? = null

            return try {
                fos = FileOutputStream(path)
                bitmap[0]?.compress(CompressFormat.JPEG, 50, fos)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        override fun onPostExecute(boolean: Boolean) {
            super.onPostExecute(boolean)
            progressBar.visibility = GONE
            imagePlaceholder.visibility = GONE
            image.visibility = VISIBLE
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = VISIBLE
        }
    }

    private fun shadowRemoval(bitmap: Bitmap){
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 50, stream)
        stream.toByteArray()

        val dst = Mat()
        val src = Mat()

        val imgProc = Imgproc.cvtColor(src, dst, Imgproc.COLOR_YCrCb2BGR)
        val binaryMask = imgProc
//        val yMean = Core.mean(Core.split(imgProc))
    }
}
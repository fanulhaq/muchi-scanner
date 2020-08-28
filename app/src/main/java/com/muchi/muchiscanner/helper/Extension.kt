/*
 * Copyright (c) 2020 - Irfanul Haq
 */

package com.muchi.muchiscanner.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DateFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun String.toCurrency() : String?{
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(this.toLong())
}

//fun isNetworkAvailable(context: Context?): Boolean {
//    val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
//    return activeNetworkInfo != null && activeNetworkInfo.isConnected
//}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("SimpleDateFormat")
fun String.dateSqlToFullDate() : String? {
    var date: Date? = null
    val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    val returnFormatter = SimpleDateFormat("d MMMM yyyy")

    try {
        date = formatter.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return returnFormatter.format(date)
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("SimpleDateFormat")
fun String.timestampToFullDate() : String? {
    var date: Date? = null
    val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val returnFormatter = SimpleDateFormat("d MMMM yyyy")

    try {
        date = formatter.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return returnFormatter.format(date)
}

fun getCurrentTime() : String {
    return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
}

fun getCurrentDate() : String {
    return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
}

fun getCurrentDateTime() : String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}

@SuppressLint("SimpleDateFormat")
fun getCurrentTimeZone() : String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"))
    val currentLocalTime = cal.time
    val date: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    date.timeZone = TimeZone.getTimeZone("GMT+7:00")

    return date.format(currentLocalTime)
}

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("SimpleDateFormat")
fun String.dateTimeToMilli() : Long {
    val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    val date = sdf.parse(this)
    return date.time
}

fun String.removeSeconds() : String {
    return this.replaceRange(5, 8, "")
}

fun handlerErrorResponse(context: Context, code: Int){
    when (code) {
        400 ->  Toast.makeText(context, "Bad Request (400)", Toast.LENGTH_SHORT).show()
        401 ->  Toast.makeText(context, "Unauthorized (401)", Toast.LENGTH_SHORT).show()
        402 ->  Toast.makeText(context, "Payment Required (402)", Toast.LENGTH_SHORT).show()
        403 ->  Toast.makeText(context, "Forbidden (403)", Toast.LENGTH_SHORT).show()
        404 ->  Toast.makeText(context, "Not Found (404)", Toast.LENGTH_SHORT).show()
        405 ->  Toast.makeText(context, "Method Not Allowed (405)", Toast.LENGTH_SHORT).show()
        406 ->  Toast.makeText(context, "Not Acceptable (406)", Toast.LENGTH_SHORT).show()
        407 ->  Toast.makeText(context, "Proxy Authentication Required (407)", Toast.LENGTH_SHORT).show()
        408 ->  Toast.makeText(context, "Request Timeout (408)", Toast.LENGTH_SHORT).show()
        409 ->  Toast.makeText(context, "Conflict (409)", Toast.LENGTH_SHORT).show()
        410 ->  Toast.makeText(context, "Gone (410)", Toast.LENGTH_SHORT).show()
        411 ->  Toast.makeText(context, "Length Required (411)", Toast.LENGTH_SHORT).show()
        412 ->  Toast.makeText(context, "Precondition Failed (412)", Toast.LENGTH_SHORT).show()
        413 ->  Toast.makeText(context, "Payload Too Large (413)", Toast.LENGTH_SHORT).show()
        414 ->  Toast.makeText(context, "UriTooLong (414)", Toast.LENGTH_SHORT).show()
        415 ->  Toast.makeText(context, "Unsupported MediaType (415)", Toast.LENGTH_SHORT).show()
        416 ->  Toast.makeText(context, "Range Not Satisfiable (416)", Toast.LENGTH_SHORT).show()
        417 ->  Toast.makeText(context, "Expectation Failed (417)", Toast.LENGTH_SHORT).show()
        418 ->  Toast.makeText(context, "ImATeapot (418)", Toast.LENGTH_SHORT).show()
        421 ->  Toast.makeText(context, "Misdirected Request (421)", Toast.LENGTH_SHORT).show()
        422 ->  Toast.makeText(context, "Unprocessable Entity (422)", Toast.LENGTH_SHORT).show()
        423 ->  Toast.makeText(context, "Locked (423)", Toast.LENGTH_SHORT).show()
        424 ->  Toast.makeText(context, "Failed Dependency (424)", Toast.LENGTH_SHORT).show()
        426 ->  Toast.makeText(context, "Upgrade Required (426)", Toast.LENGTH_SHORT).show()
        428 ->  Toast.makeText(context, "Precondition Required (428)", Toast.LENGTH_SHORT).show()
        429 ->  Toast.makeText(context, "TooManyRequests (429)", Toast.LENGTH_SHORT).show()
        431 ->  Toast.makeText(context, "Request Header Fields Too Large (431)", Toast.LENGTH_SHORT).show()
        451 ->  Toast.makeText(context, "Unavailable For Legal Reasons (451)", Toast.LENGTH_SHORT).show()

        500 ->  Toast.makeText(context, "Internal Server Error (500)", Toast.LENGTH_SHORT).show()
        501 ->  Toast.makeText(context, "Not Implemented (501)", Toast.LENGTH_SHORT).show()
        502 ->  Toast.makeText(context, "Bad Gateway (502)", Toast.LENGTH_SHORT).show()
        503 ->  Toast.makeText(context, "Service Unavailable (503)", Toast.LENGTH_SHORT).show()
        504 ->  Toast.makeText(context, "Gateway Timeout (504)", Toast.LENGTH_SHORT).show()
        505 ->  Toast.makeText(context, "Http Version Not Supported (505)", Toast.LENGTH_SHORT).show()
        506 ->  Toast.makeText(context, "Variant Also Negates (506)", Toast.LENGTH_SHORT).show()
        507 ->  Toast.makeText(context, "Insufficient Storage (507)", Toast.LENGTH_SHORT).show()
        508 ->  Toast.makeText(context, "Loop Detected (508)", Toast.LENGTH_SHORT).show()
        510 ->  Toast.makeText(context, "Not Extended (510)", Toast.LENGTH_SHORT).show()
        511 ->  Toast.makeText(context, "Network Authentication Required (511)", Toast.LENGTH_SHORT).show()

        else -> Toast.makeText(context, "unknown error", Toast.LENGTH_SHORT).show()

    }
}

fun String.imageToBase64() : String {
    val bm = BitmapFactory.decodeFile(this)
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos)

    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun Bitmap.bitmapToBase64() : String {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 50, baos)

    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun String.base64ToBitmap() : Bitmap {
    val decodedString: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun customDirectory(type: Int): File {
    val dir = when (type) {
        1 -> File(Environment.getExternalStorageDirectory(), "MuchiScanner/Camera")
        2 -> File(Environment.getExternalStorageDirectory(), "MuchiScanner/File")
        3 -> File(Environment.getExternalStorageDirectory(), "MuchiScanner/Image")
        else -> File(Environment.getExternalStorageDirectory(), "MuchiScanner/Temp")
    }

    if (!dir.exists()) {
        dir.mkdirs()
    }

    return dir
}

// call fanction before load image
fun isValidContextForGlide(context: Context?): Boolean {
    if (context == null) {
        return false
    }
    if (context is Activity) {
        if (context.isDestroyed || context.isFinishing) {
            return false
        }
    }
    return true
}
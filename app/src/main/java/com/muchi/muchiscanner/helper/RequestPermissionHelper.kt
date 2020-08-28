/*
 * Copyright (c) 2020 - Irfanul Haq
 */

package com.muchi.muchiscanner.helper

import android.Manifest
import java.util.*

class RequestPermissionHelper {
    companion object{
        fun requestAllPermission() : ArrayList<String> {
            val permissions = ArrayList<String>()
            permissions.add(Manifest.permission.CAMERA)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            return permissions
        }

        fun requestCameraPermission() : ArrayList<String> {
            val permissions = ArrayList<String>()
            permissions.add(Manifest.permission.CAMERA)
            return permissions
        }
        fun requestStoragePermission() : ArrayList<String> {
            val permissions = ArrayList<String>()
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            return permissions
        }

    }
}
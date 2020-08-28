/*
 * Copyright (c) 2020 - Irfanul Haq
 */

package com.muchi.muchiscanner.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionUtil {

    Context context;
    Activity current_activity;

    PermissionResultCallback permissionResultCallback;


    ArrayList<String> permission_list = new ArrayList<>();
    ArrayList<String> listPermissionsNeeded = new ArrayList<>();

    int req_code;

    public PermissionUtil(Context context) {
        this.context = context;
        this.current_activity = (Activity) context;

        permissionResultCallback= (PermissionResultCallback) context;
    }

    public PermissionUtil(Context context, PermissionResultCallback callback) {
        this.context = context;
        this.current_activity = (Activity) context;

        permissionResultCallback = callback;
    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param request_code
     */

    public void checkPermission(ArrayList<String> permissions, int request_code) {
        this.permission_list = permissions;
        this.req_code = request_code;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions(permissions, request_code)) {
                permissionResultCallback.PermissionGranted(request_code);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        }
        else {
            permissionResultCallback.PermissionGranted(request_code);

            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }
    }


    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */

    private  boolean checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {

        if(permissions.size() > 0) {
            listPermissionsNeeded = new ArrayList<>();

            for(int i=0; i<permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(current_activity,permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[0]), request_code);
                return false;
            }
        }

        return true;
    }

    /**
     *
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                Map<String, Integer> perms = new HashMap<>();

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                final ArrayList<String> pending_permissions = new ArrayList<>();

                for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                    if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(current_activity, listPermissionsNeeded.get(i)))
                            pending_permissions.add(listPermissionsNeeded.get(i));
                        else {
                            Log.i("Go to settings", "and enable permissions");
                            permissionResultCallback.NeverAskAgain(req_code);
                            Toast.makeText(current_activity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();

                            return;
                        }
                    }
                }

                if (pending_permissions.size() > 0) {
                    if (permission_list.size() == pending_permissions.size())
                        permissionResultCallback.PermissionDenied(req_code);
                    else
                        permissionResultCallback.PartialPermissionGranted(req_code, pending_permissions);
                } else {
                    Log.i("all", "permissions granted");
                    Log.i("proceed", "to next step");
                    permissionResultCallback.PermissionGranted(req_code);
                }
            }
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param okListener
     */
    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(current_activity)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public interface PermissionResultCallback {
        void PermissionGranted(int request_code);
        void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
        void PermissionDenied(int request_code);
        void NeverAskAgain(int request_code);
    }
}



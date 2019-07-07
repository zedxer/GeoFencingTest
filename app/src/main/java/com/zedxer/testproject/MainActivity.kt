package com.zedxer.testproject

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class MainActivity : AppCompatActivity() {
    companion object {
        var isPermissionGranted = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                isPermissionGranted = true;
                Handler().postDelayed({
                    val mainIntent = Intent(this@MainActivity, ActivityCreateNewLocation::class.java)
                    this@MainActivity.startActivity(mainIntent)
                    this@MainActivity.finish()

                    
                }, 1500)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                isPermissionGranted = false;
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .check()

    }
}

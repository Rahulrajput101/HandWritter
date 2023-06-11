package com.elkdocs.handwritter.presentation

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.ActivityMainBinding
import com.elkdocs.handwritter.util.Constant
import com.elkdocs.handwritter.util.Constant.APP_THEME_PREF
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding : ActivityMainBinding

    @Inject
    @Named("theme")
    lateinit var appThemePref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(appThemePref.getInt(APP_THEME_PREF, R.style.AppTheme))
         setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            // SDK version is 29 or higher, Scoped Storage is used instead of WRITE_EXTERNAL_STORAGE permission
            writePermissionGranted = true
            readPermissionGranted = true
        } else {
            // SDK version is lower than 29, request the READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions
            permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    handlePermissions(permissions)
                }

            updateOrRequestPermissions()

        }
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        writePermissionGranted = hasWritePermission
        if (!writePermissionGranted) {
            permissionLauncher.launch(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
            return
        }

        readPermissionGranted = hasReadPermission
        if (!readPermissionGranted) {
            permissionLauncher.launch(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    private fun handlePermissions(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filterValues { !it }
        if (deniedPermissions.isEmpty()) {
            // All permissions granted
            return
        }

        val deniedPermissionsList = ArrayList<String>()
        deniedPermissions.keys.forEach { permission ->
            if (shouldShowRequestPermissionRationale(permission)) {
                // User has denied the permission before, show rationale and try again
                deniedPermissionsList.add(permission)
            } else {
                // User has permanently denied the permission, show a message or take alternative action
                Toast.makeText(
                    this,
                    "Permission $permission has been permanently denied. Please grant the permission manually.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (deniedPermissionsList.isNotEmpty()) {
            // Request the denied permissions again with rationale
            permissionLauncher.launch(deniedPermissionsList.toTypedArray())
        }
    }

    fun openDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.openDrawer(GravityCompat.START)
    }


}
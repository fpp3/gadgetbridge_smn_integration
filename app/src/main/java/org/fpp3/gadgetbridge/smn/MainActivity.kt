package org.fpp3.gadgetbridge.smn

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var repository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = SettingsRepository(this)

        val locationModeGroup = findViewById<RadioGroup>(R.id.locationModeGroup)
        val currentLocationRadio = findViewById<RadioButton>(R.id.currentLocationRadio)
        val fixedLocationRadio = findViewById<RadioButton>(R.id.fixedLocationRadio)
        val latitudeInput = findViewById<EditText>(R.id.latitudeInput)
        val longitudeInput = findViewById<EditText>(R.id.longitudeInput)
        val updateRateInput = findViewById<EditText>(R.id.updateRateInput)
        val statusText = findViewById<TextView>(R.id.statusText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val updateNowButton = findViewById<Button>(R.id.updateNowButton)

        val settings = repository.load()
        when (settings.mode) {
            LocationMode.CURRENT -> currentLocationRadio.isChecked = true
            LocationMode.FIXED -> fixedLocationRadio.isChecked = true
        }
        latitudeInput.setText(settings.fixedLatitude.toString())
        longitudeInput.setText(settings.fixedLongitude.toString())
        updateRateInput.setText(settings.updateRateMinutes.toString())
        updateFixedLocationInputs(locationModeGroup, latitudeInput, longitudeInput)

        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions.values.any { it }
                if (!granted) {
                    statusText.text = "Status: location permission denied, fixed coordinates will be used"
                }
            }

        locationModeGroup.setOnCheckedChangeListener { _, _ ->
            updateFixedLocationInputs(locationModeGroup, latitudeInput, longitudeInput)
            if (isCurrentMode(locationModeGroup) && !hasLocationPermission()) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        saveButton.setOnClickListener {
            val parsedRate = updateRateInput.text.toString().toLongOrNull()?.coerceAtLeast(15L) ?: 30L
            val parsedLat = latitudeInput.text.toString().toDoubleOrNull() ?: settings.fixedLatitude
            val parsedLon = longitudeInput.text.toString().toDoubleOrNull() ?: settings.fixedLongitude
            val mode = if (isCurrentMode(locationModeGroup)) LocationMode.CURRENT else LocationMode.FIXED
            val newSettings = AppSettings(mode, parsedLat, parsedLon, parsedRate)
            repository.save(newSettings)
            WorkScheduler.schedulePeriodic(this, newSettings.updateRateMinutes)
            statusText.text = "Status: settings saved"
        }

        updateNowButton.setOnClickListener {
            if (isCurrentMode(locationModeGroup) && !hasLocationPermission()) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            WorkScheduler.enqueueImmediate(this)
            statusText.text = "Status: weather update requested"
        }

        WorkScheduler.schedulePeriodic(this, settings.updateRateMinutes)
    }

    private fun updateFixedLocationInputs(group: RadioGroup, latitudeInput: EditText, longitudeInput: EditText) {
        val fixedMode = !isCurrentMode(group)
        latitudeInput.isEnabled = fixedMode
        longitudeInput.isEnabled = fixedMode
    }

    private fun isCurrentMode(group: RadioGroup): Boolean {
        return group.checkedRadioButtonId == R.id.currentLocationRadio
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}

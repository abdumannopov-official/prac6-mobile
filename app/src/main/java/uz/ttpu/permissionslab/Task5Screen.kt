package uz.ttpu.permissionslab

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

enum class LocationPermissionState {
    PRECISE,
    APPROXIMATE,
    NONE
}

@Composable
fun Task5Screen() {
    val context = LocalContext.current

    var locationState by remember {
        mutableStateOf(checkLocationPermissions(context))
    }

    // Лаунчер для множественного запроса прав (RequestMultiplePermissions)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        locationState = evaluatePermissionMap(permissionsMap)
    }

    // Перепроверяем состояние каждый раз, когда экран возобновляет работу (ON_RESUME)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        locationState = checkLocationPermissions(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Task 5: One-Time Location",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Current State: ${locationState.name}",
            style = MaterialTheme.typography.headlineSmall,
            color = when (locationState) {
                LocationPermissionState.PRECISE -> MaterialTheme.colorScheme.primary
                LocationPermissionState.APPROXIMATE -> MaterialTheme.colorScheme.tertiary
                LocationPermissionState.NONE -> MaterialTheme.colorScheme.error
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }) {
            Text("Request Location Permissions")
        }
    }
}

private fun checkLocationPermissions(context: Context): LocationPermissionState {
    val fineGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return when {
        fineGranted -> LocationPermissionState.PRECISE
        coarseGranted -> LocationPermissionState.APPROXIMATE
        else -> LocationPermissionState.NONE
    }
}

private fun evaluatePermissionMap(result: Map<String, Boolean>): LocationPermissionState {
    val isFine = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
    val isCoarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

    return when {
        isFine -> LocationPermissionState.PRECISE
        isCoarse -> LocationPermissionState.APPROXIMATE
        else -> LocationPermissionState.NONE
    }
}
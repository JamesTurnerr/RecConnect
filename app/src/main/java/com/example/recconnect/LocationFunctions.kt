package com.example.recconnect

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
suspend fun getCurrentAddress(context: Context): String? {
    val client = LocationServices.getFusedLocationProviderClient(context)
    val location = client
        .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
        .await() ?: return null

    return coordsToAddress(context, location.latitude, location.longitude)
        ?: String.format(Locale.US, "%.5f, %.5f", location.latitude, location.longitude)
}

private suspend fun coordsToAddress(context: Context, lat: Double, lng: Double): String? {
    if (!Geocoder.isPresent()) return null
    val geocoder = Geocoder(context, Locale.getDefault())

    val address: Address? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) =
                    cont.resume(addresses.firstOrNull())
                override fun onError(errorMessage: String?) = cont.resume(null)
            })
        }
    } else {
        withContext(Dispatchers.IO) {
            runCatching { geocoder.getFromLocation(lat, lng, 1)?.firstOrNull() }.getOrNull()
        }
    }
    return address?.getAddressLine(0)
}
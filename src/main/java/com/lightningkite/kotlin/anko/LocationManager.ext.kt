//package com.lightningkite.kotlin.anko
//
//import android.location.Criteria
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import android.os.Bundle
//import android.os.Looper
//
///**
// * This function does assume we have permission already.
// */
//inline fun LocationManager.requestSingleUpdate(
//        criteria: Criteria = Criteria(),
//        crossinline onLocationHad: (Location) -> Unit
//) {
//    requestSingleUpdate(criteria, object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            onLocationHad(location)
//        }
//
//        override fun onProviderDisabled(p0: String?) {
//        }
//
//        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//        }
//
//        override fun onProviderEnabled(p0: String?) {
//        }
//    }, Looper.getMainLooper())
//}
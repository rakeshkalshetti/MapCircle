package com.radius

import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ui.IconGenerator

class Utils {
    companion object {

        private const val PATTERN_DASH_LENGTH_PX = 10
        private const val PATTERN_GAP_LENGTH_PX = 10
        private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
        private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
        val PATTERN_POLYGON_ALPHA = listOf(GAP, DASH)

        fun formatNumber(distance: Double): String? {
            var distance = distance
            var unit = " m"
            if (distance < 1) {
                distance *= 1000.0
                unit = " mm"
            } else if (distance > 1000) {
                distance /= 1000.0
                unit = " km"
            }
            return String.format("%d%s", distance.toInt(), unit)
        }

        fun updateTextAppearance(iconFactory: IconGenerator?, latLng: LatLng, currentDistance: Double ): MarkerOptions? {

            return MarkerOptions()
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        iconFactory!!.makeIcon(
                            formatNumber(
                                currentDistance
                            )
                        )
                    )
                )
                .position(
                    SphericalUtil.computeOffset(
                    latLng,
                    currentDistance / 1.2,
                    90.0
                ))
        }
    }
}
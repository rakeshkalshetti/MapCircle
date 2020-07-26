package com.radius

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ui.IconGenerator
import com.radius.Utils.Companion.PATTERN_POLYGON_ALPHA
import kotlin.math.ln

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerDragListener {

    private lateinit var mMap: GoogleMap

    private var currentLatLng: LatLng? = null

    private var innerCircle: Circle? = null
    private var outerCircle: Circle? = null

    private lateinit var dragDotMarker: Marker
    private lateinit var centerMarker: Marker

    private var distanceTextMarker: Marker? = null

    private var mPolyline: Polyline? = null

    private var distance = 0.0
    private var currentDistance = 0.0

    private var iconFactory: IconGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        iconFactory = IconGenerator(this)
        iconFactory!!.setColor(Color.TRANSPARENT)
        iconFactory!!.setBackground(null)
        iconFactory!!.setTextAppearance(R.style.DistanceTextStyle)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        currentLatLng = LatLng(18.615177, 73.765081)
        mMap.setOnMarkerDragListener(this)

        centerMarker = mMap.addMarker(
            MarkerOptions()
                .position(currentLatLng!!)
                .draggable(true)
        )

        val mIconGenerator = IconGenerator(this)
        mIconGenerator.setBackground(ColorDrawable(Color.TRANSPARENT))
        mIconGenerator.setContentView(
            LayoutInflater.from(this).inflate(R.layout.circle_marker_layout, null)
        )


        dragDotMarker = mMap.addMarker(
            MarkerOptions().position(
                SphericalUtil.computeOffset(
                    centerMarker.position,
                    110.0,
                    90.0
                )
            ).icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon())).draggable(true)
                .anchor(0.5f, 0.5f)
        )


        mPolyline = mMap.addPolyline(
            PolylineOptions().clickable(false)
                .pattern(PATTERN_POLYGON_ALPHA).geodesic(false).width(5.5f)
        )

        currentDistance = showDistance()

        val distanceCircleOptions = CircleOptions().radius(currentDistance)
            .center(centerMarker.position)
            .fillColor(ContextCompat.getColor(this, R.color.circleColor))
            .strokeColor(Color.BLACK).strokeWidth(5f)

        val innerCircleOptions = CircleOptions()
            .radius(currentDistance / 9)
            .fillColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            .center(centerMarker.position)
            .strokeColor(Color.WHITE)
            .strokeWidth(7f)

        outerCircle = mMap.addCircle(distanceCircleOptions)
        innerCircle = mMap.addCircle(innerCircleOptions)

        iconFactory = IconGenerator(this)

        iconFactory!!.setColor(Color.TRANSPARENT)
        iconFactory!!.setBackground(null)

        distanceTextMarker = mMap.addMarker(
            Utils.updateTextAppearance(
                iconFactory,
                centerMarker.position,
                currentDistance / 1.7
            )
        )
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                currentLatLng,
                getZoomLevel(110.0).toFloat()
            )
        )
        updatePolyline()
    }

    override fun onMarkerDragStart(marker: Marker?) {
        distance = showDistance()
    }

    override fun onMarkerDrag(marker: Marker) {

        currentDistance = showDistance()
        if (marker == centerMarker) {
            dragDotMarker.position = SphericalUtil.computeOffset(
                centerMarker.position,
                distance,
                90.toDouble()
            )

            innerCircle!!.center = centerMarker.position
            innerCircle!!.radius = currentDistance / 9

        } else {
            dragDotMarker.position = SphericalUtil.computeOffset(
                centerMarker.position,
                currentDistance,
                90.toDouble()
            )
        }

        distanceTextMarker!!.position = SphericalUtil.computeOffset(
            centerMarker.position,
            currentDistance / 1.7,
            90.toDouble()
        )
        outerCircle!!.center = centerMarker.position
        outerCircle!!.radius = currentDistance

        updatePolyline()
        distanceTextMarker!!.remove()

        distanceTextMarker = mMap.addMarker(
            Utils.updateTextAppearance(
                iconFactory,
                centerMarker.position,
                currentDistance / 1.7
            )
        )
    }

    override fun onMarkerDragEnd(marker: Marker?) {
        currentDistance = showDistance()

        dragDotMarker.position = SphericalUtil.computeOffset(
            centerMarker.position,
            currentDistance,
            90.0
        )

        innerCircle!!.center = centerMarker.position
        innerCircle!!.radius = currentDistance / 9

        distanceTextMarker!!.remove()
        distanceTextMarker = mMap.addMarker(
            Utils.updateTextAppearance(
                iconFactory,
                centerMarker.position,
                currentDistance / 1.7
            )
        )

        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                centerMarker.position,
                getZoomLevel(currentDistance).toFloat()
            )
        )
    }

    private fun getZoomLevel(distance: Double): Int {

        return (16 - ln((distance + distance / 2) / 500) / ln(2.0)).toInt()
    }

    private fun showDistance(): Double {
        val distance =
            SphericalUtil.computeDistanceBetween(centerMarker.position, dragDotMarker.position)
        if (distance < 100) {
            return 100.toDouble()
        } else if (distance > 25000) {
            return 25000.toDouble()
        }
        return distance
    }

    private fun updatePolyline() {
        mPolyline!!.points = listOf(centerMarker.position, dragDotMarker.position)
    }
}
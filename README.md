# Map Radius

## add draggable circle with distance calculate to Google Map


## Create inner circle

```
    val innerCircleOptions = CircleOptions()
                .radius(currentDistance / 9)
                .fillColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .center(centerMarker.position)
                .strokeColor(Color.WHITE)
                .strokeWidth(7f)

    innerCircle = mMap.addCircle(innerCircleOptions)
```

## Create outer circle with transparent color

 ```
    val distanceCircleOptions = CircleOptions().radius(currentDistance)
                .center(centerMarker.position)
                .fillColor(ContextCompat.getColor(this, R.color.circleColor))
                .strokeColor(Color.BLACK).strokeWidth(5f)
    
    outerCircle = mMap.addCircle(distanceCircleOptions) 
```

## add center draggable marker
 ```
    centerMarker = mMap.addMarker(
                MarkerOptions()
                    .position(currentLatLng!!)
                    .draggable(true)
            )
 ```

## add draggable dot marker right side

 ```
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

 ```


![screenshot](/screenshot/screen.gif?raw=true)
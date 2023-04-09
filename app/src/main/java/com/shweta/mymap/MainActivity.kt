package com.shweta.mymap

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.skyLayer
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.layers.properties.generated.SkyType
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener


class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        //mapView.getMapboxMap().loadStyleUri(Style.SATELLITE)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
        mapView!!.getMapboxMap().loadStyle(
            styleExtension = style(getString(R.string.mapbox_styleURL)){
                +terrain("TERRAIN_SOURCE").exaggeration(1.5)
                +skyLayer("sky"){
                    skyType(SkyType.ATMOSPHERE)
                    skyAtmosphereSun(listOf(-50.0,9.2))
                }
                +atmosphere {  }
                +projection(ProjectionName.GLOBE)
            }
        ){
            it.addSource(rasterDemSource("TERRAIN_SOURCE"){
                url("mapbox://mapbox.terrain-rgb")
                tileSize(512)
            })

            flyToCameraPosition()
            addCustomAnnotation()
        }


    }

    private fun flyToCameraPosition(){
        var cameraCenterCoordinates=com.mapbox.geojson.Point.fromLngLat(8.0061,46.5778)
        var cameraOptions=CameraOptions.Builder()
            .center(cameraCenterCoordinates)
            .bearing(130.0)
            .pitch(75.0)
            .zoom(13.0)
            .build()

        var animationOptions = MapAnimationOptions.Builder().duration(15000).build()
        mapView!!.getMapboxMap().flyTo(cameraOptions,animationOptions)
    }

    private fun addCustomAnnotation(){
        val annotationApi = mapView?.annotations
        val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView)
// Set options for the resulting symbol layer.
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            // Define a geographic coordinate.
            .withPoint(Point.fromLngLat(8.0061,46.5778))
            // Specify the bitmap you assigned to the point annotation
            // The bitmap will be added to map style automatically.
            .withIconImage(getDrawable(com.mapbox.maps.R.drawable.mapbox_logo_helmet)!!.toBitmap())
// Add the resulting pointAnnotation to the map.
        pointAnnotationManager?.create(pointAnnotationOptions)
    }
}
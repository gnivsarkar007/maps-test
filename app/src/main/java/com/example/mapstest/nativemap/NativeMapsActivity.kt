package com.example.mapstest.nativemap

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastJoinToString
import com.example.mapstest.R
import com.example.mapstest.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.DatasetFeature
import com.google.android.gms.maps.model.Feature
import com.google.android.gms.maps.model.FeatureClickEvent
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PlaceFeature
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import kotlin.random.Random


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, FeatureLayer.OnFeatureClickListener,
    FeatureLayer.StyleFactory, GoogleMap.InfoWindowAdapter {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    private val usaStates = listOf(
        "Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"
    )

    var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initializeWithNewPlacesApiEnabled(this, "AIzaSyCHcBKbK_Q88x8XjU_rqhCVMhB93KUmizk")

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        balloon = Balloon.Builder(this)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("Edit your profile here!")
            .setTextColorResource(android.R.color.white)
            .setTextSize(15f)
            .setIconDrawableResource(android.R.drawable.ic_menu_edit)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setCornerRadius(8f)
            .setBackgroundColorResource(android.R.color.holo_blue_light)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(this)
            .build()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private var areaLevel1Layer: FeatureLayer? = null
    lateinit var balloon: Balloon

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(
            "TAG",
            "isDataDrivenStylingAvailable: ${googleMap.mapCapabilities.isDataDrivenStylingAvailable}"
        )
        mMap = googleMap
        googleMap.setMaxZoomPreference(5f)
        googleMap.setMinZoomPreference(3.5f)

        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }
        googleMap.setOnMarkerClickListener {
            showToast(it.title.orEmpty())
            true
        }
        googleMap.setOnMapClickListener {




        }


        val australiaBounds = LatLngBounds(
            LatLng((-44.0), 113.0),  // SW bounds
            LatLng((-10.0), 154.0) // NE bounds
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(39.011902, -98.484246), 3.5f))
        // Get the ADMINISTRATIVE_AREA_LEVEL_1 feature layer.
//        val areaLevel1Layer = googleMap.getFeatureLayer(
//            FeatureLayerOptions.Builder()
//                .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
//                .build()
//        )
//        val countryLayer = googleMap.getFeatureLayer(
//            FeatureLayerOptions.Builder()
//                .featureType(FeatureType.COUNTRY)
//                .build()
//        )

        val dataSetLayer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                .datasetId("4b0460f7-f544-4afe-a52a-e998bd786ed7")
                .build()
        )

        dataSetLayer.apply {
            featureStyle = this@MapsActivity
            addOnFeatureClickListener(this@MapsActivity)
        }
//
//        areaLevel1Layer.apply {
//            featureStyle = this@MapsActivity
//            addOnFeatureClickListener(this@MapsActivity)
//        }
//        countryLayer.featureStyle = null
//            FeatureLayer.StyleFactory { //                if (feature is PlaceFeature) {
//            //                    val placeFeature: PlaceFeature = feature as PlaceFeature
//            //                    Log.d("TAG", "PLACE ID IS: ${placeFeature.placeId}")
//            ////            fetchDetails(placeFeature.placeId) {
//            //////                showToast(it.name.orEmpty())
//            ////            }
//            ////            // Return a hueColor in the range [-299,299]. If the value is
//            ////            // negative, add 300 to make the value positive.
//            ////            var hueColor: Int = placeFeature.placeId.hashCode() % 300
//            ////            if (hueColor < 0) {
//            ////                hueColor += 300
//            ////            }
//            //                    val fillColor = if (Random.nextInt() % 2 == 0) Color.BLUE else Color.RED
//            //
//            //                    return FeatureStyle.Builder()
//            //                        // Set the fill color for the state based on the hashed hue color.
//            //                        .fillColor(fillColor)
//            //                        .strokeColor(Color.BLACK)
//            //                        .strokeWidth(2F)
//            //                        .build()
//            //                }
//            null
//        }

        // Apply style factory function to ADMINISTRATIVE_AREA_LEVEL_1 layer.
//        styleAreaLevel1Layer()


    }

    fun fetchDetails(placeID: String, onPlaceFound: (place: Place) -> Unit) {
        // Define a Place ID.
        val placesClient = Places.createClient(this)

// Specify the fields to return.
        val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)

// Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.newInstance(placeID, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                Log.i("TAG", "Place found: ${place.latLng}")
                onPlaceFound(place)
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e("TAG", "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                }
            }
    }

    override fun onFeatureClick(featureClickEvent: FeatureClickEvent) {
        mMap.clear()
        if (featureClickEvent.features.isNotEmpty()) {
            if (featureClickEvent.features.any { it is PlaceFeature }) {
                val latlng = featureClickEvent.latLng
                fetchDetails((featureClickEvent.features[0] as PlaceFeature).placeId) {
                    showToast(it.name.orEmpty())
                }
            } else {
                Log.d(
                    "TAG",
                    "features ${featureClickEvent.features.fastJoinToString { "," }}"
                )
                val state =
                    (featureClickEvent.features[0] as DatasetFeature).datasetAttributes["NAME"].orEmpty()
                val lat: Double = (featureClickEvent.features[0] as DatasetFeature).datasetAttributes["COORDS/1"]?.toDouble() ?: 0.0
                val lng: Double = (featureClickEvent.features[0] as DatasetFeature).datasetAttributes["COORDS/0"]?.toDouble()?: 0.0

                showToast("$state ${Location.convert(lat, Location.FORMAT_DEGREES)} ${Location.convert(lng, Location.FORMAT_DEGREES)}")
                val latlng = LatLng(lat, lng)
                if (marker == null) {
                    marker = mMap.addMarker(
                        AdvancedMarkerOptions().position(latlng).title("Marker for $state").alpha(1f)
                            .iconView(
                                CustomMarkerView(this)
                            )
                    )
                } else {
                    marker?.remove()
                    marker = mMap.addMarker(
                        AdvancedMarkerOptions().position(latlng).title("Marker for $state").alpha(1f)
                            .iconView(
                                CustomMarkerView(this)
                            )
                    )
                }
//                val placesClient = Places.createClient(this@MapsActivity)
//                placesClient.
//                val geoCoder = Geocoder(this@MapsActivity)
//                val addresses = geoCoder.getFromLocation(it.latLng.latitude, it.latLng.longitude, 1) ?: emptyList()
//                if(addresses.isNotEmpty()) {
//
//                }

            }
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    val statesColorMap = mutableMapOf<String?, Int>()

    override fun getStyle(feature: Feature): FeatureStyle? {
        val fillColor =
            if (Random.nextInt() % 2 == 0) android.graphics.Color.BLUE else android.graphics.Color.RED
        Log.d("TAG", "getStyle was called")

        if (feature is PlaceFeature) {
            val placeFeature: PlaceFeature = feature as PlaceFeature
            Log.d("TAG", "PLACE ID IS: ${placeFeature.placeId}")
//            fetchDetails(placeFeature.placeId) {
////                showToast(it.name.orEmpty())
//            }
//            // Return a hueColor in the range [-299,299]. If the value is
//            // negative, add 300 to make the value positive.
//            var hueColor: Int = placeFeature.placeId.hashCode() % 300
//            if (hueColor < 0) {
//                hueColor += 300
//            }
            return null
        }

        if (feature is DatasetFeature) {
            Log.d("TAG", "datasetAttributes: ${feature.datasetAttributes}")
            val state = feature.datasetAttributes["NAME"].orEmpty()
            val color = statesColorMap[state] ?: fillColor.let { statesColorMap[state] = it; it }

//            val latlng = LatLng(lat, lng)
//            mMap.addMarker(
//                AdvancedMarkerOptions().position(latlng).title("Marker for $state").alpha(1f)
//                    .iconView(
//                        CustomMarkerView(this)
//                    )
//            )
//            if (marker == null) {
//                marker = mMap.addMarker(
//                    AdvancedMarkerOptions().position(latlng).title("Marker for $state").alpha(1f)
//                        .iconView(
//                            CustomMarkerView(this)
//                        )
//                )
//            } else {
//                marker?.remove()
//                marker = mMap.addMarker(
//                    AdvancedMarkerOptions().position(latlng).title("Marker for $state").alpha(1f)
//                        .iconView(
//                            CustomMarkerView(this)
//                        )
//                )
//            }
            return FeatureStyle.Builder()
                // Set the fill color for the state based on the hashed hue color.
                .fillColor(color)
                .strokeColor(android.graphics.Color.BLACK)
                .strokeWidth(2F)
                .build()
        }
        return null
    }

    override fun getInfoContents(p0: Marker): View? {
        TODO("Not yet implemented")
    }

    override fun getInfoWindow(p0: Marker): View? {
        TODO("Not yet implemented")
    }
}


@Composable
fun Int.toDp(): Dp {
    return LocalDensity.current.run { toDp() }
}

@Composable
fun Float.toDp(): Dp {
    return LocalDensity.current.run { toDp() }
}

@Composable
fun Dp.toPixel(): Float {
    return LocalDensity.current.run { toPx() }
}

@Composable
fun Dp.roundToPixel(): Int {
    return LocalDensity.current.run {
        roundToPx()
    }
}


@Composable
fun TipBallon(
    modifier: Modifier = Modifier,
    hint: FeatureTipUiModel,
    onCloseClicked: () -> Unit,
) {

    if (!hint.coordinates.isAttached) {
        return
    }

    val isTablet = false
    val totalWidth = LocalContext.current.resources.displayMetrics.widthPixels
    val tabletMenuWidth = 345.dp
    val tipHeight = 10.dp
    val rightMarginInPixel = 8.dp.toPixel()
    val distanceOfMenuFromLeft = rightMarginInPixel

    // margin between item to highlight and the tip
    val topMargin = 4.dp

    val calloutShape = CalloutBalloonShape(
        hint.tipDirection,
        hint.coordinates.positionInRoot().x + hint.coordinates.size.width / 2 - distanceOfMenuFromLeft, // coordinate should be relative to the view
        tipWidth = 23.dp.toPixel(),
        tipHeight = tipHeight.toPixel(),
    )

    // Default initial height to prevent too much jump
    val defaultBalloonHeight = 80.dp.roundToPixel()
    var balloonHeight by remember {
        mutableIntStateOf(defaultBalloonHeight)
    }

    val yOffset = if (hint.tipDirection == TipDirection.TOP) {
        (hint.coordinates.positionInRoot().y + hint.coordinates.size.height).toDp()
    } else {
        (hint.coordinates.positionInRoot().y + balloonHeight).toDp()
    } + topMargin

    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusable()
            .focusRequester(focusRequester),
    ) {
        TipOverlay(
            highlightRect = Rect(
                left = hint.coordinates.positionInRoot().x,
                top = hint.coordinates.positionInRoot().y,
                right = hint.coordinates.positionInRoot().x + hint.coordinates.size.width,
                bottom = hint.coordinates.positionInRoot().y + hint.coordinates.size.height,
            ),
            onOverlayTapped = onCloseClicked
        )

        val widthModifier = if (isTablet) {
            Modifier.width(tabletMenuWidth)
        } else {
            Modifier.fillMaxWidth()
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.TopEnd)
                .then(widthModifier)
                .offset(y = yOffset)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        balloonHeight = it.size.height
                    }
                    .clip(calloutShape)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = calloutShape,
                    )
                    .background(
                        Color.White,
                        calloutShape,
                    )
                    .then(
                        if (hint.tipDirection == TipDirection.TOP) {
                            Modifier.padding(top = tipHeight)
                        } else {
                            Modifier.padding(bottom = tipHeight)
                        }
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            // the purpose of this is to catch the tap event
                            // within boundaries of the menu
                        })
                    }
                    .focusGroup(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(
                            top = 5.dp,
                            start = 5.dp,
                            bottom = 5.dp,
                            end = 5.dp + 40.dp  // not to overlap the close icon
                        ),
                ) {
                    Text(
                        text = stringResource(hint.title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.background,
                    )
                    Text(
                        text = stringResource(id = hint.description),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                    onClick = { onCloseClicked() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        modifier = Modifier.size(18.dp),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )

                }
            }
        }
    }
}

class CalloutBalloonShape(
    private val calloutDirection: TipDirection,
    private val tipPointXCoordinate: Float,
    private val tipWidth: Float,
    private val tipHeight: Float,
    private val cornerSize: CornerSize = CornerSize(0.dp),
) : CornerBasedShape(
    topStart = cornerSize,
    topEnd = cornerSize,
    bottomEnd = cornerSize,
    bottomStart = cornerSize
) {

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): CornerBasedShape {
        return CalloutBalloonShape(
            calloutDirection,
            tipPointXCoordinate,
            tipWidth,
            tipHeight,
            cornerSize
        )
    }

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = when (calloutDirection) {
                TipDirection.TOP -> {
                    Path().apply {
                        moveTo(x = 0f, y = tipHeight)
                        lineTo(x = tipPointXCoordinate - tipWidth / 2, y = tipHeight)
                        lineTo(x = tipPointXCoordinate, y = 0f)
                        lineTo(x = tipPointXCoordinate + tipWidth / 2, y = tipHeight)
                        lineTo(x = size.width, y = tipHeight)
                        lineTo(x = size.width, y = size.height)
                        lineTo(x = 0f, y = size.height)
                        close()
                    }
                }

                TipDirection.DOWN -> {
                    Path().apply {
                        moveTo(x = 0f, y = 0f)
                        lineTo(x = size.width, y = 0f)
                        lineTo(x = size.width, y = size.height - tipHeight)
                        lineTo(
                            x = size.width - tipPointXCoordinate - tipWidth / 2,
                            y = size.height - tipHeight
                        )
                        lineTo(x = size.width - tipPointXCoordinate, y = size.height)
                        lineTo(
                            x = size.width - tipPointXCoordinate + tipWidth / 2,
                            y = size.height - tipHeight
                        )
                        lineTo(x = 0f, y = size.height - tipHeight)
                        close()
                    }
                }
            }
        )
    }
}

@Composable
fun TipOverlay(
    modifier: Modifier = Modifier,
    highlightRect: Rect,
    onOverlayTapped: () -> Unit,
) {
    val overlayColor = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0xA6000000)
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = modifier
                .fillMaxSize()
        ) {
            // This is a background overlay where the highlight zone is cut out
            clipRect(
                left = highlightRect.left,
                top = highlightRect.top,
                right = highlightRect.right,
                bottom = highlightRect.bottom,
                clipOp = ClipOp.Difference
            ) {
                drawRect(
                    color = overlayColor,
                    topLeft = Offset(x = 0f, y = 0f),
                    size = size
                )
            }
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(
                top = highlightRect.bottom
                    .toDp()
                    .coerceAtLeast(0.dp) + 10.dp
            )
            .clickable {
                onOverlayTapped()
            }
        )
    }
}

/**
 * Is the tip shown on top or bottom of the callout balloon
 */
enum class TipDirection {
    TOP,
    DOWN
}


data class FeatureTipUiModel(
    val key: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
    val coordinates: LayoutCoordinates,
    val tipDirection: TipDirection = TipDirection.TOP,
    val navigationDestinationId: Int? = null,
)

fun Activity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
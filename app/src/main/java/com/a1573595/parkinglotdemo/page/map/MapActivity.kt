package com.a1573595.parkinglotdemo.page.map

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.BaseColumns
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.a1573595.parkinglotdemo.BaseActivity
import com.a1573595.parkinglotdemo.R
import com.a1573595.parkinglotdemo.databinding.ActivityMapBinding
import com.a1573595.parkinglotdemo.databinding.DialogParkingLotBinding
import com.a1573595.parkinglotdemo.page.detail.DetailActivity
import com.a1573595.parkinglotdemo.tool.ParkingCluster
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.collections.MarkerManager
import java.lang.Exception
import java.util.*

class MapActivity : BaseActivity(), OnMapReadyCallback,
    OnCameraIdleListener, OnMarkerClickListener,
    OnClusterClickListener<ParkingCluster>,
    OnClusterItemClickListener<ParkingCluster> {
    companion object {
        private const val Max_Clustering_Room_Level = 17f

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MapActivity::class.java))
        }
    }

    private val viewModel: MapViewModel by viewModels()

    private lateinit var binding: ActivityMapBinding

    private lateinit var mMap: GoogleMap
    private lateinit var mClusterManager: ClusterManager<ParkingCluster>
    private lateinit var normalMarkerManager: MarkerManager.Collection

    private var zoomLevel = 0f

    private lateinit var geocoder: Geocoder
    private val searchHandler = Handler(Looper.getMainLooper())
    private var geocodeAddress: List<Address> = emptyList()

    private inner class ParkingRender :
        DefaultClusterRenderer<ParkingCluster>(applicationContext, mMap, mClusterManager) {
        override fun shouldRenderAsCluster(cluster: Cluster<ParkingCluster>): Boolean {
            return zoomLevel < Max_Clustering_Room_Level && super.shouldRenderAsCluster(
                cluster
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        subscriptViewModel()
    }

    private fun subscriptViewModel() {
        viewModel.dataSetEvent.observe(this, {
            it.peekContent().forEach {
                mClusterManager.addItem(
                    ParkingCluster(
                        LatLng(it.lat, it.lng), it.id,
                        it.name, it.area, it.totalcar,
                        it.totalmotor, it.totalbike, it.totalbus
                    )
                )
            }

            mClusterManager.cluster()
            mMap.uiSettings.setAllGesturesEnabled(true)
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.setAllGesturesEnabled(false)
        initClusterManager()
        initSearchView()

        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    25.0329694,
                    121.56541770000001
                ), 15f
            )
        )

        mMap.setOnCameraIdleListener(this)
        mMap.setOnMapLoadedCallback { viewModel.loadDataSet() }
    }

    override fun onCameraIdle() {
        zoomLevel = mMap.cameraPosition.zoom
        mClusterManager.onCameraIdle()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.position))

        return true
    }

    override fun onClusterClick(cluster: Cluster<ParkingCluster>): Boolean {
        val builder = LatLngBounds.builder()
        for (item in cluster.items) {
            builder.include(item.position)
        }

        try {
            val bounds = builder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun onClusterItemClick(item: ParkingCluster): Boolean {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(item.position))
        showDialog(item)
        return true
    }

    private fun initClusterManager() {
        mClusterManager = ClusterManager(this, mMap)
        val dm = resources.displayMetrics
        mClusterManager.setAlgorithm(
            NonHierarchicalViewBasedAlgorithm(
                dm.widthPixels,
                dm.heightPixels
            )
        )
        mClusterManager.renderer = ParkingRender()
        mClusterManager.setOnClusterClickListener(this)
        mClusterManager.setOnClusterItemClickListener(this)

        normalMarkerManager = mClusterManager.markerManager.newCollection()
        normalMarkerManager.setOnMarkerClickListener(this)
    }

    private fun initSearchView() {
        geocoder = Geocoder(this, Locale.getDefault())

        val from = arrayOf("address", "lat", "lng")
        val to = intArrayOf(android.R.id.text1)
        val simpleCursorAdapter = SimpleCursorAdapter(
            this, android.R.layout.simple_list_item_1,
            null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        binding.searchView.suggestionsAdapter = simpleCursorAdapter

        binding.searchView.setOnClickListener { binding.searchView.isIconified = false }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                simpleCursorAdapter.changeCursor(null)
                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({
                    try {
                        geocodeAddress =
                            geocoder.getFromLocationName(binding.searchView.query.toString(), 5)
                                ?: emptyList()
                        val c = MatrixCursor(
                            arrayOf(
                                BaseColumns._ID,
                                "address",
                                "lat",
                                "lng"
                            )
                        )
                        for (i in geocodeAddress.indices) {
                            c.addRow(
                                arrayOf(
                                    i,
                                    geocodeAddress[i].getAddressLine(0),
                                    geocodeAddress[i].latitude,
                                    geocodeAddress[i].longitude
                                )
                            )
                        }
                        simpleCursorAdapter.changeCursor(c)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 750)
                return false
            }
        })

        binding.searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = simpleCursorAdapter.getItem(position) as Cursor
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"))
                val lng = cursor.getDouble(cursor.getColumnIndexOrThrow("lng"))
                normalMarkerManager.clear()
                val marker = normalMarkerManager.addMarker(
                    MarkerOptions().position(LatLng(lat, lng)).title(address)
                )
                marker.showInfoWindow()
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
                return false
            }
        })

        binding.searchView.queryHint = getString(R.string.query_hint)
        binding.searchView.visibility = View.VISIBLE
    }

    private fun showDialog(cluster: ParkingCluster) {
        val binding = DialogParkingLotBinding.inflate(LayoutInflater.from(this)).apply {
            tvName.text = cluster.name
            tvAddress.text = cluster.area
            tvTotal.text = getString(
                R.string.transportation, cluster.totalBus, cluster.totalCar,
                cluster.totalMotor, cluster.totalBike
            )

            root.setOnClickListener {
                DetailActivity.startActivity(this@MapActivity, cluster.id)
            }
        }

        MaterialAlertDialogBuilder(this)
            .setView(binding.root)
            .show()
    }
}
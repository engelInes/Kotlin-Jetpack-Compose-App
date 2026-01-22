package com.inesengel.travelapp.UI.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.binding.adapters.AttractionAdapter
import com.inesengel.travelapp.UI.model.PageIndicatorState
import com.inesengel.travelapp.UI.viewmodel.DestinationTabViewModel
import com.inesengel.travelapp.databinding.AttractionDetailsFragmentBinding
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ARG_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.INVALID_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.SINGLE_PAGE_INDICATOR
import com.inesengel.travelapp.UI.view.utils.Constants.Orientation.HORIZONTAL_MARGIN
import com.inesengel.travelapp.UI.view.utils.Constants.Orientation.VERTICAL_MARGIN
import com.inesengel.travelapp.UI.view.utils.Constants.UIViews.GOOGLE_MAPS_TAG
import com.inesengel.travelapp.UI.viewmodel.DestinationDetailViewModel
import com.inesengel.travelapp.core.model.MapLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AttractionsFragment :
    Fragment(),
    OnMapReadyCallback {

    private inner class AttractionScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(
            recyclerView: RecyclerView,
            newState: Int
        ) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val centerView = snapHelper.findSnapView(layoutManager)

                centerView?.let {
                    val position = layoutManager.getPosition(it)
                    tabViewModel.onAttractionSwiped(position)
                }
            }
        }
    }

    private var _binding: AttractionDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private val destinationId: Int by lazy {
        arguments?.getInt(ARG_DESTINATION_ID) ?: INVALID_DESTINATION_ID
    }
    private val tabViewModel: DestinationTabViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private val parentViewModel: DestinationDetailViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private var googleMap: GoogleMap? = null
    private val snapHelper = PagerSnapHelper()
    private lateinit var adapter: AttractionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AttractionDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupRecyclerView()
        setupCollectors()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        parentViewModel.mapLocation.value?.let { location ->
            updateMapUI(
                map,
                location
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleMap = null
        _binding = null
    }

    private fun setupCollectors() {
        tabViewModel.attractions
            .flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            )
            .onEach { list ->
                adapter.submitList(list)
                setupPageIndicators(list.size)
            }
            .launchIn(lifecycleScope)

        tabViewModel.pageIndicatorState
            .flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            )
            .onEach { state ->
                renderPageIndicators(state)
            }
            .launchIn(lifecycleScope)

        parentViewModel.mapLocation
            .flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            )
            .onEach { location ->
                location?.let {
                    googleMap?.let { map ->
                        updateMapUI(map, it)
                    } ?: Log.d(
                        GOOGLE_MAPS_TAG,
                        "Location received but map is not ready yet"
                    )
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun renderPageIndicators(state: PageIndicatorState) {
        setupPageIndicators(state.count)
        updatePageIndicatorState(state.activeIndex)
    }

    private fun updateMapUI(
        map: GoogleMap,
        location: MapLocation
    ) {
        val latLng = LatLng(
            location.lat,
            location.lng
        )
        map.clear()
        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(location.title)
        )
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                12f
            )
        )
    }

    private fun setupRecyclerView() {
        adapter = AttractionAdapter { attraction ->
        }
        binding.attractionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            this.adapter = this@AttractionsFragment.adapter
            snapHelper.attachToRecyclerView(this)

            addOnScrollListener(AttractionScrollListener())
        }
    }

    private fun setupPageIndicators(count: Int) {
        binding.pageIndicatorContainer.removeAllViews()
        if (count <= SINGLE_PAGE_INDICATOR) return

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            HORIZONTAL_MARGIN,
            VERTICAL_MARGIN,
            HORIZONTAL_MARGIN,
            VERTICAL_MARGIN
        )

        repeat(count) {
            ImageView(requireContext()).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_dot
                    )
                )
                this.layoutParams = layoutParams
                binding.pageIndicatorContainer.addView(this)
            }
        }
    }

    private fun updatePageIndicatorState(position: Int) {
        val childCount = binding.pageIndicatorContainer.childCount

        for (i in 0 until childCount) {
            val imageView = binding.pageIndicatorContainer.getChildAt(i) as ImageView
            val isActive = i == position
            imageView.alpha = if (isActive) 1f else 0.4f
            imageView.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (isActive)
                        R.color.md_theme_primary
                    else
                        R.color.md_theme_onSurfaceVariant
                )
            )
        }
    }

    companion object {
        fun newInstance(destinationId: Int): AttractionsFragment {
            val fragment = AttractionsFragment()
            fragment.arguments = bundleOf(ARG_DESTINATION_ID to destinationId)
            return fragment
        }
    }
}
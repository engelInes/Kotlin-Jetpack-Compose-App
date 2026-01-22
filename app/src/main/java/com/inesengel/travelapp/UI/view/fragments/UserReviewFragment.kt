package com.inesengel.travelapp.UI.view.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.binding.adapters.ReviewAdapter
import com.inesengel.travelapp.UI.model.RatingRowState
import com.inesengel.travelapp.UI.model.ReviewStats
import com.inesengel.travelapp.UI.viewmodel.DestinationTabViewModel
import com.inesengel.travelapp.databinding.ReviewDetailsFragmentBinding
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ARG_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.INVALID_DESTINATION_ID
import com.inesengel.travelapp.databinding.ItemRatingProgressBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.getValue
import androidx.core.graphics.toColorInt

@AndroidEntryPoint
class UserReviewFragment : Fragment() {

    private var _binding: ReviewDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private val destinationId: Int by lazy {
        arguments?.getInt(ARG_DESTINATION_ID) ?: INVALID_DESTINATION_ID
    }
    private val tabViewModel: DestinationTabViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReviewDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCollectors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = ReviewAdapter()
        binding.apply {
            reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            reviewsRecyclerView.adapter = adapter
        }
    }

    private fun setupCollectors() {
        tabViewModel.reviews
            .flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            )
            .onEach { list ->
                adapter.submitList(list)
            }
            .launchIn(lifecycleScope)

        tabViewModel.reviewStats
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { stats -> bindStats(stats) }
            .launchIn(lifecycleScope)
    }

    private fun bindStats(stats: ReviewStats) {
        binding.apply {
            overallScoreTextView.text = stats.averageRating.toString()
            overallRatingBar.rating = stats.averageRating
            reviewCountTextView.text = getString(
                R.string.based_on_reviews,
                stats.totalReviews
            )

            val uiRows = listOf(
                rowExcellent,
                rowGood,
                rowAverage,
                rowBelowAverage,
                rowPoor
            )

            uiRows.zip(stats.ratingRows).forEach { (viewBinding, rowState) ->
                bindRow(
                    viewBinding,
                    rowState
                )
            }
        }
    }

    private fun bindRow(
        rowBinding: ItemRatingProgressBinding,
        state: RatingRowState
    ) {
        rowBinding.apply {
            labelTextView.text = state.label
            ratingProgressBar.max = state.maxProgress
            ratingProgressBar.progress = state.count
            ratingProgressBar.progressTintList =
                ColorStateList.valueOf(state.colorHex.toColorInt())
        }
    }

    companion object {
        fun newInstance(destinationId: Int): UserReviewFragment {
            val fragment = UserReviewFragment()
            fragment.arguments = bundleOf(ARG_DESTINATION_ID to destinationId)
            return fragment
        }
    }
}
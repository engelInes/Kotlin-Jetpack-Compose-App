package com.inesengel.travelapp.UI.binding.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.view.utils.Constants.UIViews.DESTINATION_DETAILS_ATTRACTION_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.UIViews.DESTINATION_DETAILS_REVIEW_INDEX
import com.inesengel.travelapp.UI.view.fragments.AttractionsFragment
import com.inesengel.travelapp.UI.view.fragments.UserReviewFragment

class DetailsViewPageAdapter(
    private val fragment: Fragment,
    val destinationId: Int
) : FragmentStateAdapter(
    fragment
) {
    private val fragmentFactoryMap: Map<Int, () -> Fragment> = mapOf(
        DESTINATION_DETAILS_ATTRACTION_INDEX to { AttractionsFragment.newInstance(destinationId) },
        DESTINATION_DETAILS_REVIEW_INDEX to { UserReviewFragment.newInstance(destinationId) }
    )

    override fun createFragment(position: Int): Fragment {
        val fragmentFactory = fragmentFactoryMap[position]

        requireNotNull(fragmentFactory) {
            fragment.requireContext().getString(
                R.string.error_message_no_fragment,
                position
            )
        }

        return fragmentFactory()
    }

    override fun getItemCount(): Int {
        return fragmentFactoryMap.size
    }
}

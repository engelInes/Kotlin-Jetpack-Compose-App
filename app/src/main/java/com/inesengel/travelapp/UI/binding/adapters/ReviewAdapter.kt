package com.inesengel.travelapp.UI.binding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inesengel.travelapp.databinding.ItemReviewBinding
import com.inesengel.travelapp.core.model.UserReview

class ReviewAdapter : ListAdapter<UserReview, ReviewAdapter.ReviewViewHolder>(
    ReviewDiffCallback()
) {
    inner class ReviewViewHolder(
        val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item: UserReview) {
            binding.apply {
                reviewerNameTextView.text = item.reviewerName
                reviewDetailsTextView.text = item.reviewText
                ratingBar.rating = item.rating.toFloat()
                ratingValueText.text = item.rating.toDouble().toString()
                dateTextView.text = item.date
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ReviewViewHolder,
        position: Int
    ) {
        val review = getItem(position)
        holder.bind(review)
    }
}

class ReviewDiffCallback : DiffUtil.ItemCallback<UserReview>() {
    override fun areItemsTheSame(
        oldItem: UserReview,
        newItem: UserReview
    ): Boolean {
        return oldItem.reviewerName == newItem.reviewerName
    }

    override fun areContentsTheSame(
        oldItem: UserReview,
        newItem: UserReview
    ): Boolean {
        return oldItem == newItem
    }
}

package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ViewholderRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter :
    ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        val binding = ViewholderRepresentativeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepresentativeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class RepresentativeViewHolder(val binding: ViewholderRepresentativeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Representative) {
        binding.apply {
            representative = item

            Glide.with(binding.root.context)
                .load(item.official.photoUrl ?: "")
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_connection_error)
                .into(binding.representativePhoto)

            showSocialLinks(channels = item.official.channels ?: emptyList())
            showWWWLinks(urls = item.official.urls ?: emptyList())

            executePendingBindings()
        }
    }

    //TODO: Add companion object to inflate ViewHolder (from) ?

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            enableLink(binding.facebookIcon, facebookUrl)
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            enableLink(binding.twitterIcon, twitterUrl)
        }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.wwwIcon, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
            .map { channel -> "https://www.facebook.com/${channel.id}" }
            .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
            .map { channel -> "https://www.twitter.com/${channel.id}" }
            .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}

class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        // Implement comparison logic based on unique attributes of Official and Office
        return oldItem.official.name == newItem.official.name && oldItem.office.name == newItem.office.name
    }

    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        // Compare the properties of the Representative objects
        return oldItem == newItem // This works if you want a deep comparison
    }
}

//TODO: Create RepresentativeListener
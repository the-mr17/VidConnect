package com.mr_17.vidconnect.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mr_17.vidconnect.databinding.ItemUsersRecyclerViewBinding
import com.mr_17.vidconnect.ui.home.models.User
import de.hdodenhof.circleimageview.CircleImageView

class UsersRecyclerViewAdapter(
    var list: List<User>,
    var context: Context,
    var listener: OnClickListener,
) :
    RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUsersRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
    false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.tvFullName.text = "${user.firstName} ${user.lastName}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        binding: ItemUsersRecyclerViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var civProfile: CircleImageView
        lateinit var tvFullName: TextView
        lateinit var ivCall: ImageView
        lateinit var ivVideoCall: ImageView

        init {
            binding.apply {
                this@ViewHolder.civProfile = civProfile
                this@ViewHolder.tvFullName = tvFullName
                this@ViewHolder.ivCall = ivCall
                this@ViewHolder.ivVideoCall = ivVideoCall

                ivCall.setOnClickListener {
                    listener.onCallButtonClick(binding.root, adapterPosition)
                }

                ivVideoCall.setOnClickListener {
                    listener.onVideoCallButtonClick(binding.root, adapterPosition)
                }
            }
        }
    }

    interface OnClickListener {
        fun onCallButtonClick(v: View?, position: Int)
        fun onVideoCallButtonClick(v: View?, position: Int)
    }
}
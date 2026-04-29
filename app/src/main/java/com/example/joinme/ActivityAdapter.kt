package com.example.joinme
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.joinme.data.entities.Activity

class ActivityAdapter(private val activityList: MutableList<Activity> = mutableListOf(),
                      private val isMyActivity : Boolean,
                      private val onParticipateClick: (Activity) -> Unit,
                      private val onDeleteClick: (Activity) -> Unit) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {


    class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.cardTitle)
        val date: TextView = view.findViewById(R.id.cardDate)
        val time: TextView = view.findViewById(R.id.cardTime)
        val participants: TextView = view.findViewById(R.id.cardParticipants)
        val location: TextView = view.findViewById(R.id.cardLocation)
        val participateBtn: Button = view.findViewById(R.id.participateBtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_card, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {

        val item = activityList[position]

        val currentParticipants = item.currentParticipants
        val maxParticipants = item.maxParticipants

        holder.title.text = item.title
        holder.date.text = item.date
        holder.time.text = item.time
        holder.participants.text = "$currentParticipants/$maxParticipants Συμμετοχές"
        holder.location.text = item.location

        if (isMyActivity) {
            holder.participateBtn.text = "Διαγραφή"
            holder.participateBtn.setBackgroundColor(android.graphics.Color.RED)
            holder.participateBtn.setOnClickListener { onDeleteClick(item) }
        } else {
            holder.participateBtn.text = "Συμμετοχή"
            holder.participateBtn.setBackgroundColor(android.graphics.Color.MAGENTA)
            holder.participateBtn.setOnClickListener { onParticipateClick(item) }


            // Αυτό τρέχει ΚΑΘΕ ΦΟΡΑ που εμφανίζεται μια κάρτα
            if (currentParticipants >= maxParticipants) {
                holder.participateBtn.isEnabled = false
                holder.participateBtn.setBackgroundColor(android.graphics.Color.LTGRAY)
                holder.participateBtn.text = "Πλήρες"
            } else {
                holder.participateBtn.isEnabled = true
                holder.participateBtn.setBackgroundColor(android.graphics.Color.MAGENTA)
                holder.participateBtn.text = "Συμμετοχή"
            }

            holder.participateBtn.setOnClickListener {
                if (currentParticipants < maxParticipants) {
                    notifyItemChanged(position)
                    onParticipateClick(item)

                    if (currentParticipants >= maxParticipants) {
                        holder.participateBtn.isEnabled = false
                        holder.participateBtn.setBackgroundColor(android.graphics.Color.LTGRAY)
                        holder.participateBtn.text = "Πλήρες"
                    }
                }
            }
        }

        holder.location.setOnClickListener {
            val mapUri = Uri.parse("geo:0,0?q=${item.location}")
            val intent = Intent(Intent.ACTION_VIEW, mapUri)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    fun updateData(newList: List<Activity>) {
        this.activityList.clear()
        this.activityList.addAll(newList)
        notifyDataSetChanged()
    }
}

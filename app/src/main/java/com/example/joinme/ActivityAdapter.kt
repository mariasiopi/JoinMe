package com.example.joinme
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(private val activityList: MutableList<ActivityModel>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

        class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.cardTitle)
            val date: TextView = view.findViewById(R.id.cardDate)
            val time: TextView = view.findViewById(R.id.cardTime)
            var participants: TextView = view.findViewById(R.id.cardParticipants)
            val location: TextView = view.findViewById(R.id.cardLocation)
            val participateBtn: Button = view.findViewById<Button>(R.id.participateBtn)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_activity_card, parent, false)
            return ActivityViewHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
            val item = activityList[position]
            holder.title.text = item.title
            holder.date.text = item.date
            holder.time.text = item.time
            holder.participants.text = "${item.participants} / ${item.maxParticipants} Συμμετοχές"
            holder.location.text = item.location

            // Αυτό τρέχει ΚΑΘΕ ΦΟΡΑ που εμφανίζεται μια κάρτα
            if (item.participants >= item.maxParticipants) {
                holder.participateBtn.isEnabled = false
                holder.participateBtn.setBackgroundColor(android.graphics.Color.LTGRAY)
                holder.participateBtn.text = "Πλήρες"
            } else {
                holder.participateBtn.isEnabled = true
                holder.participateBtn.setBackgroundColor(android.graphics.Color.BLUE) // Ή το χρώμα σου
                holder.participateBtn.text = "Συμμετοχή"
            }

            holder.participateBtn.setOnClickListener {
                if (item.participants < item.maxParticipants) {
                    item.participants += 1
                    notifyItemChanged(position)

                    if (item.participants >= item.maxParticipants) {
                        holder.participateBtn.isEnabled = false
                        holder.participateBtn.setBackgroundColor(android.graphics.Color.LTGRAY)
                        holder.participateBtn.text = "Πλήρες"
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
    }

package com.khl_app.ui.fragments

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.khl_app.R

class MatchAdapter(private val matches: List<String>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {
    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
        val currentScoreTextView: TextView = itemView.findViewById(R.id.currentScoreTextView)
        val forecastTextView: TextView = itemView.findViewById(R.id.forecastTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_main, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
//        holder.startTimeTextView.text = match.startTime
//        holder.currentScoreTextView.text = match.currentScore
//        holder.forecastTextView.text = match.forecast
    }

    override fun getItemCount() = matches.size
}
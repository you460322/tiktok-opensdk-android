package com.bytedance.sdk.demo.share.publish

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.R
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.model.ViewType

class ShareActivityAdapter(
    private val onSaveToggleStatus: (Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var models: List<DataModel> = listOf()

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val header: TextView
        val desc: TextView
        init {
            header = view.findViewById(R.id.header)
            desc = view.findViewById(R.id.desc)
        }
    }
    class ToggleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val subtitle: TextView
        val toggle: ToggleButton
        init {
            title = view.findViewById(R.id.title)
            subtitle = view.findViewById(R.id.subtitle)
            toggle = view.findViewById(R.id.toggle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ViewType.typeFrom(viewType)) {
            ViewType.HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
                HeaderViewHolder(view)
            }
            ViewType.TOGGLE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.toggle_item, parent, false)
                return ToggleViewHolder(view)
            }
            else -> throw Exception("Invalid View Type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val model = models[position]) {
            is HeaderModel -> {
                (holder as HeaderViewHolder).let {
                    it.header.text = model.title
                    model.desc?.let { desc ->
                        it.desc.text = desc
                        it.desc.visibility = View.VISIBLE
                    }
                }
            }
            is ToggleModel -> {
                (holder as ToggleViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.toggle.isChecked = model.isOn
                    it.toggle.setOnCheckedChangeListener { _, isOn ->
                        onSaveToggleStatus(isOn)
                    }
                }
            }
        }
    }

    fun updateModels(models: List<DataModel>) {
        this.models = models
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is ToggleViewHolder -> {
                holder.toggle.setOnCheckedChangeListener(null)
            }
            else -> Unit
        }
    }

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value
}

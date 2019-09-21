package com.example.rmpiv.echo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.rmpiv.echo.R
import com.example.rmpiv.echo.activities.MainActivity
import com.example.rmpiv.echo.fragments.AboutUsFragment
import com.example.rmpiv.echo.fragments.FavoriteFragment
import com.example.rmpiv.echo.fragments.MainScreenFragment
import com.example.rmpiv.echo.fragments.SettingFragment

/**
 * Created by rmpiv on 27-12-2017.
 */
class NavigationDrawerAdapter(var _contentList: ArrayList<String>, var _getImage: IntArray, var _contest: Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.navViewHolder>() {

    var contentList: ArrayList<String>? = null
    var getImage: IntArray? = null
    var mContext: Context? = null

    init {
        this.contentList = _contentList
        this.getImage = _getImage
        this.mContext = _contest
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): navViewHolder {
        var item = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_navigationdrawer, parent, false)
        val returnThis = navViewHolder(item)
        return returnThis
    }

    override fun onBindViewHolder(holder: navViewHolder?, position: Int) {
        holder?.text_GET?.setText(contentList?.get(position))
        holder?.icon_GET?.setBackgroundResource(getImage?.get(position) as Int)
        holder?.contentHolder?.setOnClickListener({
            if (position == 0) {
                val mainScreenFragment = MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, mainScreenFragment)
                        .commit()
            } else if (position == 1) {
                val favoriteFragment = FavoriteFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, favoriteFragment)
                        .commit()
            } else if (position == 2) {
                val settingFragment = SettingFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, settingFragment)
                        .commit()
            } else {
                val aboutUsFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, aboutUsFragment)
                        .commit()
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        })

    }

    override fun getItemCount(): Int {

        return _contentList.size
    }

    class navViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var icon_GET: ImageView? = null
        var text_GET: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {

            icon_GET = itemView?.findViewById(R.id.icon_navdrawer)
            text_GET = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}

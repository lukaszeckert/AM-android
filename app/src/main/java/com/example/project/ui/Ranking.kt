package com.example.project.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView


import com.example.project.State
import android.widget.TextView
import com.example.project.loadState
import kotlinx.android.synthetic.main.fragment_ranking.*


import java.lang.Exception
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.project.getRanking
import com.example.project.saveState
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import android.R
import android.graphics.Typeface
import android.widget.HeaderViewListAdapter


class Ranking : Fragment() {

    var state: State? = null
    var listView: ListView? = null
    var swiperefresh: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun updateRanking() {
        if (state == null)
            state = loadState(activity!!.applicationContext)
        if (state == null)
            return

        val ranking = getRanking()
        if (ranking != null) {
            state!!.ranking = ranking


            saveState(activity!!.applicationContext, state as State)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(com.example.project.R.layout.fragment_ranking, container, false)
        listView = view.findViewById<ListView>(com.example.project.R.id.listView)
        listView!!.adapter = RankingAdapter(activity!!.applicationContext, state!!.ranking, state!!.logedUser)


        val headerView = layoutInflater.inflate(com.example.project.R.layout.list_header, null)
        listView!!.addHeaderView(headerView)
        swiperefresh = view.findViewById<SwipeRefreshLayout>(com.example.project.R.id.swipe_to_refresh)
        swiperefresh!!.setOnRefreshListener{
            doAsync {

                Thread.sleep(2000)
                updateRanking()
                uiThread {

                    swiperefresh!!.isRefreshing = false
                    if (listView?.adapter != null)
                        ((listView?.adapter as HeaderViewListAdapter)
                            .wrappedAdapter as RankingAdapter).setData(state!!.ranking)
                }
            }

        }

        return view
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        state = loadState(activity!!.applicationContext)


    }

    override fun onDetach() {
        super.onDetach()

    }

}
class RankingAdapter(var context: Context, var items: ArrayList<State.UserScore>, val user: String): BaseAdapter(){
    val mInflater = LayoutInflater.from(context);
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view: View? = null
        if(p1 == null)
        {
            view = mInflater.inflate(com.example.project.R.layout.fragment_item, null)
            viewHolder = ViewHolder()
            viewHolder.userName = view.findViewById<TextView>(com.example.project.R.id.user_name)
            viewHolder.score = view.findViewById<TextView>(com.example.project.R.id.user_score)
            viewHolder.position = view.findViewById<TextView>(com.example.project.R.id.item_number)
            view.setTag(viewHolder)
        }else{
            view = p1
            viewHolder = p1.getTag() as ViewHolder
        }

        viewHolder.position?.setText((p0+1).toString())
        viewHolder.userName?.setText(items[p0].user)
        viewHolder.score?.setText(items[p0].score.toString())
        if(items[p0].user == user)
            viewHolder.userName?.setTypeface(null, Typeface.BOLD_ITALIC)
        else
            viewHolder.userName?.setTypeface(null, Typeface.NORMAL)
        if(view != null)
            return view
        throw Exception("Failed to create view")
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return items.count()
    }

    internal class ViewHolder {
        var position: TextView? = null
        var userName: TextView? = null
        var score: TextView? = null
    }
    fun setData(items: ArrayList<State.UserScore>){
        this.items = items
        this.notifyDataSetChanged()
    }
}

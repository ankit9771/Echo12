package com.example.rmpiv.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.rmpiv.echo.R
import com.example.rmpiv.echo.Songs
import com.example.rmpiv.echo.adapters.FavoriteAdapter
import com.example.rmpiv.echo.databases.EchoDatabase



/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {


    var getSongsList: ArrayList<Songs>?= null
    var noFavorites: TextView? = null
    var nowPlayingBottonBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent: EchoDatabase? = null
    var myActivity: Activity?=null
    var refreshList:ArrayList<Songs>? = null
    var getListfromDatabase:ArrayList<Songs>? =null


    object Statified{

        var mediaPlayer: MediaPlayer? = null

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
      activity.title = "Favorites"
       noFavorites = view?.findViewById(R.id.noFavorites)
       nowPlayingBottonBar = view.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        songTitle = view.findViewById(R.id.songTitleFavScreen)
        recyclerView = view.findViewById(R.id.favoriteRecycler)
   return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
         favoriteContent = EchoDatabase(myActivity as Context)
        getSongsList = favoriteContent?.queryDBList()

        if(getSongsList == null)
        {
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }else{
          var favoriteAdapter = FavoriteAdapter(getSongsList as ArrayList<Songs>,myActivity as Context)
          val mLayoutManager = LinearLayoutManager(activity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = favoriteAdapter
            recyclerView?.setHasFixedSize(true)
        }
        bottomBarSetup()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }



    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
        return arrayList
      }
      fun bottomBarSetup(){
          try {
              bottomBarClickHandler()
              songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
              SongPlayingFragment.Statified.mediaplayer?.setOnCompletionListener ({
                  songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                  SongPlayingFragment.Staticated.onSongComplete()
              })
              if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean)
              {
                  nowPlayingBottonBar?.visibility = View.VISIBLE
              }else{
                  nowPlayingBottonBar?.visibility = View.INVISIBLE
              }

          }catch (e:Exception)
          {
              e.printStackTrace()
          }
      }

     fun bottomBarClickHandler()   {
         nowPlayingBottonBar?.setOnClickListener({
             Statified.mediaPlayer = SongPlayingFragment.Statified.mediaplayer
             val songPlayingFragment = SongPlayingFragment()
             var args = Bundle()
             args.putString("songArtist",SongPlayingFragment.Statified.currentSongHelper?.songArtist)
             args.putString("path",SongPlayingFragment.Statified.currentSongHelper?.songPath)
             args.putString("songTitle",SongPlayingFragment.Statified.currentSongHelper?.songTitle)
             args.putInt("SongId",SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
             args.putInt("songPosition",SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
             args.putParcelableArrayList("songData",SongPlayingFragment.Statified.fetchSong)
             args.putString("FavBottomBar","success")
             songPlayingFragment.arguments = args
             fragmentManager.beginTransaction()
                     .replace(R.id.details_fragment,songPlayingFragment)
                     .addToBackStack("SongPlayingFragment")
                     .commit()
         })
         playPauseButton?.setOnClickListener({
             if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                 SongPlayingFragment.Statified.mediaplayer?.pause()
                 trackPosition = SongPlayingFragment.Statified.mediaplayer?.getCurrentPosition() as Int
                 playPauseButton?.setBackgroundResource(R.drawable.play_icon)
             }else{
                 SongPlayingFragment.Statified.mediaplayer?.seekTo(trackPosition)
                 SongPlayingFragment.Statified.mediaplayer?.start()
                 playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
             }
         })
     }

    fun display_favorites_by_searching(){
  if(favoriteContent?.checkSize() as Int>0){favoriteContent?.queryDBList()
  refreshList = ArrayList<Songs>()
      getListfromDatabase =favoriteContent?.queryDBList()
      var fetchListfromDevice = getSongsFromPhone()
      if(fetchListfromDevice != null){
          for(i in 0..fetchListfromDevice?.size - 1){
              for(j in 0..getListfromDatabase?.size as Int - 1){
                  if((getListfromDatabase?.get(j)?.songID)==(fetchListfromDevice?.get(i)?.songID)) {
                      refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                  }
              }
          }
      }else{

      }
  }
    }

    }// Required empty public constructor

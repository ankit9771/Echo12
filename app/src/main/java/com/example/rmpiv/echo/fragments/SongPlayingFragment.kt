package com.example.rmpiv.echo.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.rmpiv.echo.CurrentSongHelper
import com.example.rmpiv.echo.R
import com.example.rmpiv.echo.Songs
import com.example.rmpiv.echo.databases.EchoDatabase
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class SongPlayingFragment : Fragment() {
    object Statified {
        var myActivity: Activity? = null
        var mediaplayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null

        var currentPosition: Int = 0
        var fetchSong: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null

        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null

        var fab: ImageButton? = null
        var favoriteContent: EchoDatabase? = null
        var mSensorMangager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"


        var updateSongTime = object : Runnable {
            override fun run() {
                val getcurrent = mediaplayer?.getCurrentPosition()
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong()))))
                seekbar?.setProgress(getcurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }
        }
    }


    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var My_PREFS_LOOP = "Loop feature"

        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSong?.get(Statified.currentPosition)

                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.currentSongHelper?.songId = nextSong?.songID as Long


                    updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

                    Statified.mediaplayer?.reset()
                    try {
                        Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified.mediaplayer?.prepare()
                        Statified.mediaplayer?.start()
                        processInformation(Statified.mediaplayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }

            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
            } else {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))

            }
        }

        fun updateTextViews(songtitle: String, songArtist: String) {
            var songTitleUpdated = songtitle
            var songArtistUpdated = songArtist
            if (songtitle.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistUpdated = "unknown"
            }
            Statified.songTitleView?.setText(songTitleUpdated)
            Statified.songArtistView?.setText(songArtistUpdated)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            var finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Statified.seekbar?.max = finalTime
            Statified.startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    (TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))))
            Statified.endTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    (TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())-TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))))
            Statified.seekbar?.setProgress(startTime)
            Handler().postDelayed(Statified.updateSongTime, 1000)
        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                Statified.currentPosition = Statified.currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(Statified.fetchSong?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition
            }
            if (Statified.currentPosition == Statified.fetchSong?.size) {
                Statified.currentPosition = 0
            }
            var nextSong = Statified.fetchSong?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songID as Long


            updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaplayer?.prepare()
                Statified.mediaplayer?.start()
                processInformation(Statified.mediaplayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
            } else {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))
            }
        }
    }

    var mAccelaration: Float = 0f
    var mAccelarationCurrent: Float = 0f
    var mAccelarationLast: Float = 0f

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity.title = "Now Playing"
        Statified.seekbar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id.favoriteIcon)
        Statified.fab?.alpha = 0.8f
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    //To CONNECT FRAGMENT TO ACTIVITY
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensorMangager?.registerListener(Statified.mSensorListener,
                Statified.mSensorMangager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        Statified.audioVisualization?.onPause()

        Statified.mSensorMangager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Statified.audioVisualization?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorMangager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelaration = 0.0f
        mAccelarationCurrent = SensorManager.GRAVITY_EARTH
        mAccelarationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.favoriteContent = EchoDatabase(Statified.myActivity as Context)
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long? = 0
        try {
            path = arguments.getString("path")
            _songTitle = arguments.getString("songTitle")
            _songArtist = arguments.getString("songArtist")
            songId = arguments.getInt("SongId").toLong()
            Statified.currentPosition = arguments.getInt("songPosition")
            Statified.fetchSong = arguments.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments.get("FavBottomBar") as? String
        var fromMainBottomBar = arguments.get("MainBottomBar") as? String
        if (fromFavBottomBar != null) {
            Statified.mediaplayer = FavoriteFragment.Statified.mediaPlayer
        }
     else  if (fromMainBottomBar != null) {
        Statified.mediaplayer = MainScreenFragment.Statified.mediaPlayer
    }

        else
    {
        Statified.mediaplayer = MediaPlayer()
        Statified.mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(path))
            Statified.mediaplayer?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Statified.mediaplayer?.start()
    }
        Staticated.processInformation(Statified.mediaplayer as MediaPlayer)

        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.mediaplayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }

        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audioVisualization?.linkTo(visualizationHandler)


        var prefdForShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefdForShuffle?.getBoolean("feature", false)

        if (isShuffleAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefdForLoop = Statified.myActivity?.getSharedPreferences(Staticated.My_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefdForLoop?.getBoolean("feature", false)

        if (isLoopAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isLoop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            Statified.currentSongHelper?.isLoop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        }


        if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))

        } else {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))

        }
    }

    fun clickHandler() {

        Statified.fab?.setOnClickListener({
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))
                Statified.favoriteContent?.deleteFavourite(Statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
                Statified.favoriteContent?.storeAsFavorite(Statified.currentSongHelper?.songId?.toInt(),
                        Statified.currentSongHelper?.songArtist, Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath)
                Toast.makeText(Statified.myActivity, "Added to favorites", Toast.LENGTH_SHORT).show()

            }
        })

        Statified.shuffleImageButton?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.My_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.currentSongHelper?.isShuffle = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()

            } else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        Statified.nextImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        })
        Statified.previousImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        Statified.loopImageButton?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.My_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()

            }
        })

        Statified.playpauseImageButton?.setOnClickListener({
            if (Statified.mediaplayer?.isPlaying as Boolean) {
                Statified.mediaplayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                Statified.mediaplayer?.start()
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
        Statified.seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Statified.seekbar?.progress = Statified.seekbar?.progress as Int
                Statified.mediaplayer?.seekTo(Statified.seekbar?.progress as Int)
            }
        })

    }


    fun playPrevious() {
        Statified.currentPosition = Statified.currentPosition - 1
        if (Statified.currentPosition == -1) {
            Statified.currentPosition = 0
        }
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

        }
        Statified.currentSongHelper?.isLoop = false
        var nextSong = Statified.fetchSong?.get(Statified.currentPosition)
        Statified.currentSongHelper?.songTitle = nextSong?.songTitle
        Statified.currentSongHelper?.songPath = nextSong?.songData
        Statified.currentSongHelper?.currentPosition = Statified.currentPosition
        Statified.currentSongHelper?.songId = nextSong?.songID as Long

        Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)


        Statified.mediaplayer?.reset()
        try {
            Statified.mediaplayer?.setDataSource(activity, Uri.parse(Statified.currentSongHelper?.songPath))
            Statified.mediaplayer?.prepare()
            Statified.mediaplayer?.start()
            Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
        } else {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))

        }
    }

    fun bindShakeListener() {
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent) {

                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]
                mAccelarationLast = mAccelarationCurrent
                mAccelarationCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta = mAccelarationCurrent - mAccelarationLast
                mAccelaration = mAccelaration * 0.9f + delta

                if (mAccelaration > 12) {
                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }

        }
    }

}// Required empty public constructor

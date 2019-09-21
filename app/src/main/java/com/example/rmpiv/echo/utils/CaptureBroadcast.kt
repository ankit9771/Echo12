package com.example.rmpiv.echo.utils

import android.app.Service
import android.bluetooth.BluetoothClass
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import com.example.rmpiv.echo.R
import com.example.rmpiv.echo.activities.MainActivity
import com.example.rmpiv.echo.fragments.SongPlayingFragment

/**
 * Created by rmpiv on 08-01-2018.
 */
class CaptureBroadcast:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try {
                MainActivity.Statified.notificationManager?.cancel(1978)
            }catch (e: Exception){
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaplayer?.pause()
                    SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            }catch (e:Exception)
            {
                e.printStackTrace()
            }
        }else{
            val tm: TelephonyManager = p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    try {
                        MainActivity.Statified.notificationManager?.cancel(1978)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Statified.mediaplayer?.pause()
                            SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                }
                else ->{

                }
            }

        }
    }
}
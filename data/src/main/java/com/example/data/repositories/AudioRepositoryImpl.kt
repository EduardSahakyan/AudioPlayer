package com.example.data.repositories

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.data.R
import com.example.data.models.AudioModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.FileNotFoundException
import java.io.IOException


internal class AudioRepositoryImpl(private val context: Context): AudioRepository {

    override fun getAudioList(): Flow<AudioModel> {
        return flow {
            val proj = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
            )
            val audioCursor: Cursor? = context.contentResolver
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null)
            if (audioCursor != null) {
                if (audioCursor.moveToFirst()) {
                    do {
                        val audioTitle = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                        val audioDuration =
                            audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                        val audioData = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                        val audioAlbum = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                        val albumId: Long = audioCursor.getLong(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                        val sArtworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
                        val albumArtUri: Uri = ContentUris.withAppendedId(sArtworkUri, albumId)
                        var bitmap: Bitmap? = null
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                context.contentResolver, albumArtUri
                            )
                            bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
                        } catch (exception: FileNotFoundException) {
                            bitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.ic_mus
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        val audioModel = AudioModel(
                            bitmap!!,
                            audioCursor.getString(audioTitle),
                            audioCursor.getInt(audioDuration),
                            audioCursor.getString(audioData),
                            audioCursor.getInt(audioAlbum)
                        )
                        emit(audioModel)
                    } while (audioCursor.moveToNext())
                }
            }
        }
    }
}
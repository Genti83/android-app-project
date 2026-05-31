# MusicPlayer - Android App

A feature-rich music player for Android with a dark theme UI, audio visualizer, and full playback controls.

## Features

- 🎵 Play MP3 and other audio files from device storage
- 🎨 Album art display
- 📊 Real-time audio spectrum visualizer with colorful bars
- 🔄 Repeat modes (OFF / ONE / ALL)
- 📋 Song metadata display (Title, Artist, Album, Year, Genre)
- ⏩ Track navigation (Previous / Next)
- 🎚️ Seek bar with time display
- 📈 Technical info (Kbps, Type, Track count, Sample rate)

## Screenshots

The app features a dark-themed interface with:
- Album art at the top
- Scrolling filename (marquee)
- Song metadata section
- Technical info bar (Kbps, Type, Repeat, Track, kHz)
- Progress/seek bar
- Audio visualizer with spectrum bars
- Control buttons (MENU, PREV, PAUSE, NEXT, CLOSE)

## Requirements

- Android 7.0 (API 24) or higher
- Storage permission (for reading music files)
- Record Audio permission (for visualizer)

## Build

Open the project in Android Studio and build the APK:

```bash
./gradlew assembleDebug
```

The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/src/main/
├── java/com/genti/musicplayer/
│   ├── MusicPlayerActivity.java    - Main player activity
│   ├── AudioVisualizerView.java    - Custom spectrum visualizer view
│   └── SongInfo.java               - Song data model
├── res/
│   ├── layout/activity_music_player.xml  - Main UI layout
│   ├── values/colors.xml
│   ├── values/strings.xml
│   ├── values/themes.xml
│   └── drawable/ic_music_note.xml
└── AndroidManifest.xml
```

## Permissions

- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_AUDIO` - Access music files
- `RECORD_AUDIO` - Audio visualizer functionality
- `FOREGROUND_SERVICE` - Background playback

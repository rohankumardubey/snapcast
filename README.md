# Snapcast

![Snapcast](https://raw.githubusercontent.com/badaix/snapcast/master/doc/Snapcast_800.png)

**S**y**n**chronous **a**udio **p**layer

[![Build Status](
https://travis-ci.org/badaix/snapcast.svg?branch=master)](https://travis-ci.org/badaix/snapcast)
[![Github Releases](https://img.shields.io/github/release/badaix/snapcast.svg)](https://github.com/badaix/snapcast/releases)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.me/badaix)

Snapcast is a multiroom client-server audio player, where all clients are time synchronized with the server to play perfectly synced audio. It's not a standalone player, but an extension that turns your existing audio player into a Sonos-like multiroom solution.  
Audio is captured by the server and routed to the connected clients. Several players can feed audio to the server in parallel and clients can be grouped to play the same audio stream.  
One of the most generic ways to use Snapcast is in conjunction with the music player daemon ([MPD](http://www.musicpd.org/)) or [Mopidy](https://www.mopidy.com/).

![Overview](https://raw.githubusercontent.com/badaix/snapcast/master/doc/Overview.png)

## How does it work

The Snapserver reads PCM chunks from configurable stream sources:

- **Named pipe**, e.g. `/tmp/snapfifo`
- **ALSA** to capture line-in, microphone, alsa-loop (to capture audio from other players)
- **TCP**
- **stdout** of a process
- Many more

The chunks are encoded and tagged with the local time. Supported codecs are:

- **PCM** lossless uncompressed
- **FLAC** lossless compressed [default]
- **Vorbis** lossy compression
- **Opus** lossy low-latency compression

The encoded chunks are sent via a TCP connection to the Snapclients.
Each client does continuous time synchronization with the server, so that the client is always aware of the local server time.
Every received chunk is first decoded and added to the client's chunk-buffer. Knowing the server's time, the chunk is played out using a system dependend low level audio API (e.g. ALSA) at the appropriate time. Time deviations are corrected by playing faster/slower, which is done by removing/duplicating single samples (a sample at 48kHz has a duration of ~0.02ms).

Typically the deviation is below 0.2ms.

For more information on the binary protocol, please see the [documentation](doc/binary_protocol.md).

## Installation

You can either install Snapcast from a prebuilt package (recommended for new users), or build and install snapcast from source.

### Install Linux packages (recommended for beginners)

Snapcast packages are available for several Linux distributions:

- [Debian](doc/install.md#debian)
- [OpenWrt](doc/install.md#openwrt)
- [Alpine Linux](doc/install.md#alpine-linux)
- [Archlinux](doc/install.md#archlinux)
- [Void Linux](doc/install.md#void-linux)

### Installation from source

Please follow this [guide](doc/build.md) to build Snapcast for

- [Linux](doc/build.md#linux-native)
- [FreeBSD](doc/build.md#freebsd-native)
- [macOS](doc/build.md#macos-native)
- [Android](doc/build.md#android-cross-compile)
- [OpenWrt](doc/build.md#openwrtlede-cross-compile)
- [Buildroot](doc/build.md#buildroot-cross-compile)
- [Raspberry Pi](doc/build.md#raspberry-pi-cross-compile)
- [Windows](doc/build.md#windows-vcpkg)

## SnapOS

The bravest among you may be interested in [SnapOS](https://github.com/badaix/snapos), a small and fast-booting "just enough" OS to run Snapcast as an appliance.

There is a guide (with the necessary buildfiles) available to build SnapOS, which comes in two flavors:

- [Buildroot](https://github.com/badaix/snapos/blob/master/buildroot-external/README.md) based, or
- [OpenWrt](https://github.com/badaix/snapos/tree/master/openwrt) based.

Please note that there are no pre-built firmware packages available.

## Configuration

After installation, Snapserver and Snapclient are started with the command line arguments that are configured in `/etc/default/snapserver` and `/etc/default/snapclient`.
Allowed options are listed in the man pages (`man snapserver`, `man snapclient`) or by invoking the snapserver or snapclient with the `-h` option.

The server configuration is done in `/etc/snapserver.conf`. Different streams can by configured in the `[stream]` section with a list of `stream` options, e.g.:

    [stream]
    stream = pipe:///tmp/snapfifo?name=Radio&sampleformat=48000:16:2&codec=flac
    stream = file:///home/user/Musik/Some%20wave%20file.wav?name=File

The pipe stream (`stream = pipe`) will per default create the pipe. Sometimes your audio source might insist in creating the pipe itself. So the pipe creation mode can by changed to "not create, but only read mode", using the `mode` option set to `create` or `read`:

    stream = pipe:///tmp/snapfifo?name=Radio&mode=read

Available stream sources are:

- **pipe**: read audio from a named pipe
- **alsa**: read audio from an alsa device
- **librespot**: launches librespot and reads audio from stdout
- **airplay**: launches airplay and read audio from stdout
- **file**: read PCM audio from a file
- **tcp**: receives audio from a TCP socket, can act as client or server

Configuration options for the stream sources can be found in `/etc/snapserver.conf`.

## Test

You can test your installation by copying random data into the server's fifo file

    sudo cat /dev/urandom > /tmp/snapfifo

All connected clients should play random noise now. You might raise the client's volume with "alsamixer".
It's also possible to let the server play a WAV file. Simply configure a `file` stream in `/etc/snapserver.conf`, and restart the server:

    [stream]
    stream = file:///home/user/Musik/Some%20wave%20file.wav?name=test

When you are using a Raspberry Pi, you might have to change your audio output to the 3.5mm jack:

    #The last number is the audio output with 1 being the 3.5 jack, 2 being HDMI and 0 being auto.
    amixer cset numid=3 1

To setup WiFi on a Raspberry Pi, you can follow this [guide](https://www.raspberrypi.org/documentation/configuration/wireless/wireless-cli.md)

## Control

Snapcast can be controlled using a [JSON-RPC API](doc/json_rpc_api/v2_0_0.md) over plain TCP, HTTP, or Websockets:

- Set client's volume
- Mute clients
- Rename clients
- Assign a client to a stream
- Manage groups
- ...

The server is shipped with [Snapweb](https://github.com/badaix/snapweb), this WebApp can be reached under `http://<snapserver host>:1780`.

There is an Android client [snapdroid](https://github.com/badaix/snapdroid) available in [Releases](https://github.com/badaix/snapdroid/releases/latest) and on [Google Play](https://play.google.com/store/apps/details?id=de.badaix.snapcast)

![Snapcast for Android](https://raw.githubusercontent.com/badaix/snapcast/master/doc/snapcast_android_scaled.png)

### Contributions

There is also an unofficial WebApp from @atoomic [atoomic/snapcast-volume-ui](https://github.com/atoomic/snapcast-volume-ui).
This app lists all clients connected to a server and allows you to control individually the volume of each client.
Once installed, you can use any mobile device, laptop, desktop, or browser.

There is also an [unofficial FHEM module](https://forum.fhem.de/index.php/topic,62389.0.html) from @unimatrix27 which integrates a Snapcast controller into the [FHEM](https://fhem.de/fhem.html) home automation system.

There is a [snapcast component for Home Assistant](https://home-assistant.io/components/media_player.snapcast/) which integrates a Snapcast controller in to the [Home Assistant](https://home-assistant.io/) home automation system.

For a web interface in Python, see [snapcastr](https://github.com/xkonni/snapcastr), based on [python-snapcast](https://github.com/happyleavesaoc/python-snapcast). This interface controls client volume and assigns streams to groups.

Another web interface running on any device is [snapcast-websockets-ui](https://github.com/derglaus/snapcast-websockets-ui), running entirely in the browser, which needs [websockify](https://github.com/novnc/websockify). No configuration needed; features almost all functions; still needs some tuning for the optics.

A web interface called [HydraPlay](https://github.com/mariolukas/HydraPlay) integrates Snapcast and multiple Mopidy instances. It is JavaScript based and uses Angular 7. A Snapcast web socket proxy server is needed to connect Snapcast to HydraPlay over web sockets.

For Windows, there's [Snap.Net](https://github.com/stijnvdb88/snap.net), a control client and player. It runs in the tray and lets you adjust client volumes with just a few clicks. The player simplifies setting up snapclient to play your music through multiple Windows sound devices simultaneously: pc speakers, hdmi audio, any usb audio devices you may have, etc. Snap.Net also runs on Android, and has limited support for iOS.

## Setup of audio players/server

Snapcast can be used with a number of different audio players and servers, and so it can be integrated into your favorite audio-player solution and make it synced-multiroom capable.
The only requirement is that the player's audio can be redirected into the Snapserver's fifo `/tmp/snapfifo`. In the following configuration hints for [MPD](http://www.musicpd.org/) and [Mopidy](https://www.mopidy.com/) are given, which are base of other audio player solutions, like [Volumio](https://volumio.org/) or [RuneAudio](http://www.runeaudio.com/) (both MPD) or [Pi MusicBox](http://www.pimusicbox.com/) (Mopidy).

The goal is to build the following chain:

    audio player software -> snapfifo -> snapserver -> network -> snapclient -> alsa

This [guide](doc/player_setup.md) shows how to configure different players/audio sources to redirect their audio signal into the Snapserver's fifo:

- [MPD](doc/player_setup.md#mpd)
- [Mopidy](doc/player_setup.md#mopidy)
- [FFmpeg](doc/player_setup.md#ffmpeg)
- [mpv](doc/player_setup.md#mpv)
- [MPlayer](doc/player_setup.md#mplayer)
- [Alsa](doc/player_setup.md#alsa)
- [PulseAudio](doc/player_setup.md#pulseaudio)
- [AirPlay](doc/player_setup.md#airplay)
- [Spotify](doc/player_setup.md#spotify)
- [Process](doc/player_setup.md#process)
- [Line-in](doc/player_setup.md#line-in)
- [VLC](doc/player_setup.md#vlc)

## Roadmap

Unordered list of features that should make it into the v1.0

- [X] **Remote control** JSON-RPC API to change client latency, volume, zone,...
- [X] **Android client** JSON-RPC client and Snapclient
- [X] **Streams** Support multiple streams
- [X] **Debian packages** prebuild deb packages
- [X] **Endian** independent code
- [X] **OpenWrt** port Snapclient to OpenWrt
- [X] **Hi-Res audio** support (like 96kHz 24bit)
- [X] **Groups** support multiple Groups of clients ("Zones")
- [ ] **JSON-RPC** Possibility to add, remove, rename streams
- [ ] **Protocol specification** Snapcast binary streaming protocol, JSON-RPC protocol
- [ ] **Ports** Snapclient for Windows, ~~Mac OS X~~,...

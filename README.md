# Android Raw Audio to UDP

## Why

I'm creating a simple in-home voice assistant, an old Android phone of mine will serve as a microphone.

# How

The app listens to port ```50003``` by default if it receives the (ascii encoded) text "```AUDIO PLZ\n```" it'll start sending a raw PCM audio stream.

Audio settings:

- Sample Rate: 44100 Hz
- Channels: MONO (1)

UDP Packets from the android device are then sent back to the requester at port ```50000```.

# References

Dean Thomason: https://github.com/DeanThomson/android-udp-audio-chat

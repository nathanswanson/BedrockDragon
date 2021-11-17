# BedrockDragon

This is a passion project by me to create the most efficent decentralized server I can. 

Please note that my common code is still private as I finish up some touches. 

This is required to run and develop this project which means this code is unusable unless you have access to my private Space.

I will release the common code alpha soon.


## Framework

The server is structured around kotlin coroutine and sharable flows. A netty thread controls the IO of the server and passes packet information to the
coroutine network.


## Current Status

Currently I am devleoping this as my main project. The server as of Nov. 10 lets players joins and they can move around a super flat world and use chat. 

## Current Todo

- finish text packet
- finish abstract framework for reactive.
- make sure chats are on one coroutine.
- fix MOTD
- find bug where player gets stuck on connecting on server
- unsubscribe leaving players
- make sure all information is handled when player leaves.

- NBT and save files

## Contributing

Contributing is currently closed unless invited until a solid framework is finished.

### Guide

You must have:
- A copy of Windows 10 Minecraft
- Intelij IDEA. I'm sure another editor would work but this is built to support IDEA

There is two jar dependencies kotlin-math, and Common both which are found on our jetbrains space.

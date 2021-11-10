# BedrockDragon

This is a passion project by me to create the most efficent decentralized server I can. 

Please note that my common code is still private as I finish up some touches. 

This is required to run and develop this project which means this code is unusable unless you have access to my private Space.

I will release the common code alpha soon.


## Framework

The server is structured around kotlin coroutine and sharable flows. A netty thread controls the IO of the server and passes packet information to the
coroutine network.


## Current Status

Currently I am devleoping this as my main project. The server as of Nov. 10 lets players join however that is it. Once thye join they will see an empty world 
which they can use the chat with.

## Current Todo

~~- change player packet handler to branch off.~~
- finish text packet
- finish abstract framework for reactive.
~~- remove many packet creations and replace with streams.~~
- make sure chats are on one coroutine.
- fix MOTD
- find bug where player gets stuck on connecting on server
~~- fix string byte searlize in packet.kt~~
- unsubscribe leaving players
- make sure all information is handled when player leaves.

- then we can start on chunk loading

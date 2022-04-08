# BedrockDragon

This is a passion project by me to create the most efficient decentralized server I can. This project uses zero minecraft code and is built from scratch
.

## Framework

The server structures around kotlin coroutine and sharable flows. A netty thread controls the IO of the server and passes packet information to the
coroutine network.


## Current Status

Currently, I am developing this as my main project.

- Players can join
- World loads from save with some bugs
- Basic inventory system
- Custom block and item DSL
- Players can move around world
- Chat
- Chunk loading with some bugs
- Basic block interaction
## Current Todo
       
          
- Fix error where when two players load same chunk it creates a corrupt packet.
- Fix chunk loading bug where some sub chunks randomly appear as one block.
- Block breaking.


## Contributing

If you know an answer to a current git issue, please don't hesitate to respond. I welcome any feedback on my project good or bad.
### Guide

You must have:
- A copy of Windows 10 Minecraft
- IntelliJ IDEA. I'm sure another editor would work but this is built to support IDEA


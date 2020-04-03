# KamiAutoSkidder
An automatic KAMI/kamiblue skidder written in Kotlin using ASM.

## Images
![Program in action](https://cdn.discordapp.com/attachments/662312542822989827/695682782831050812/askid.png)
![An example skidded client](https://cdn.discordapp.com/attachments/662312542822989827/695683495821050006/2020-04-03_13.16.55.png)

## How to use
See the example folder for an example of how to use it

### Options
- input: name of KAMI/kamiblue jar
- output: name of the jar to output
- name: name of the skidded mod
- version: version of the skidded mod
- appid: discord rich presence app id (use "kami" for the name of the large image)
- chat-append: dictionary of chat appends to rename

### Replacing assets
To replace assets in the input jar, make a folder named "assets" in the same directory as the autoskidder jar, in this folder you can place images with the same name as ones in the kami jar and they will be replaced. See the example folder for examples
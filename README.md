# MML2ASM-Pokemon-Crystal-
Converts MML syntax to a syntax compatible with the pokecrystal disassembly

Currently a WIP, this tool takes input Music Markup Language (MML) syntax and outputs a .asm file, making scripting custom music for Pokemon Crystal much easier.

At the moment, the tool only converts note data, stereo panning, tempo, volume, and wave duty cycle changing for the pulse channels. The tool also ignores any comments indicated by a ";"

Useage: java -jar mml2asm.jar
(Then, place your MML file as a .txt in the same directory as the .jar and type it in the program when it prompts you to)

Future plans:
- Implement vibrato detection
- Optimize redundant octave commands
- Drag/drop file over the tool

---

Some useful supplemental tools I recommend:
http://forums.famitracker.com/viewtopic.php?t=610 (To write your music in FamiTracker and convert it to a readable MIDI, using Namco for the Wave Channel)
https://github.com/loveemu/petitemm (To convert the MIDI to MML syntax)
# MML2ASM-Pokemon-Crystal-
Java tool which takes input Music Markup Language (MML) syntax and outputs a .asm file, making scripting custom music for Pokemon Crystal much easier. (Currently a WIP)

At the moment, the tool only converts note data, stereo panning, tempo, volume, and wave duty cycle changing for the pulse channels. The tool also ignores any comments indicated by a ";".

Useage: java -jar mml2asm.jar <file>.txt
Place your .txt in the folder where you put the .jar. If you don't specify for a file, it'll add one itself. Also, it'll amend ".txt" to the file search argument if you didn't.

Future plans:
- Implement vibrato detection

---

Some useful supplemental tools I recommend:
http://forums.famitracker.com/viewtopic.php?t=610 (To write your music in FamiTracker and convert it to a readable MIDI, using Namco for the Wave Channel)
https://github.com/loveemu/petitemm (To convert the MIDI to MML syntax)

Special thanks:
pigdevil2010: Provided info about pokecrystal disassembly's commands and GB noise bank reference
sirocyl: Advice for tempo/rhythm algorithms
# MML2ASM-Pokemon-Crystal-
Java tool which takes input Music Markup Language (MML) syntax and outputs a .asm file, making scripting custom music for Pokemon Crystal much easier. (Currently a WIP)

At the moment, the tool only converts note data, stereo panning, tempo, volume, and wave duty cycle changing for the pulse channels. The tool also ignores any comments indicated by a ";".

Useage: java -jar mml2asm.jar <file>.txt
Place your .txt in the folder where you put the .jar. If you don't specify for a file, it'll add one itself. Also, it'll amend ".txt" to the file search argument if you didn't.

Future plans:
- Implement vibrato detection
- Implement proper label looping
- Implement proper note rhythms

---

Some useful supplemental resources I recommend:
- http://forums.famitracker.com/viewtopic.php?t=610 (To write your music in FamiTracker and convert it to a readable MIDI, using Namco for the Wave Channel)
- https://github.com/loveemu/petitemm (To convert the MIDI to MML syntax)
- http://shauninman.com/assets/downloads/ppmck_guide.html (For PPMCK MML syntax reference, which this conversion tool is based on)

Special thanks to pigdevil2010 for providing info about pokecrystal disassembly's commands and GB noise bank reference.
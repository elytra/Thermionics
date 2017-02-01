# Thermionics
A new generation of user-friendly energy, transport, and logic devices

# License: MIT
See LICENSE for details. Go ahead and add this mod to your modpack!

# Cloning and Compiling
For now, this mod integrates with TheOneProbe. You can settle this dependency by
dropping the plugin jar (as well as the compatlayer jar, if applicable) into the
"libs" folder in the root directory of the project. ForgeGradle will automatically
recognize and link to the plugin, and use it in test runs.


In the future, inspection will instead be made through ProbeDataProvider (such as
FruitPhone). This will change the dependency map. CommonCapabilities is also
expected to become a hard dependency to enable temperature interop.

# Code Style
* Tabs for indentation
* Spaces for cosmetic alignment
* If it doesn't line up right when you change your tab spacing, you did it wrong
* License information at the top of every java source file, above the package declaration.
* One commit only per pull request

# Making Friends with This Mod
Thermionics, in one form or another, has been around since 1.7.10, and gone through
a lot of changes along the way, but has always held to a few hard guarantees:

Thermionics embraces simple, open standards. This is doubly true for Capabilities.
If you're requesting items, grab an IItemHandler. If you're finding out how hot
something is, ask for an ITemperature. If you want to implement a wrench, use
ITweakable. If the most open standard is not supported, put in an Issue.

Coming from the same mindset, interoperability is incredibly important. Each system
has its own character, and historically, combining machines has resulted in some
fantastic rube goldberg automations and anachronistic frankensteins. All us mod
developers are pushing towards a future where steel towers full of arcane machinery
work tirelessly to deliver luxuries to your base, while darkening the sky with
smokestacks and nuclear fallout.

Interop comes with a price. Often carelessly-combined systems create a race to the bottom,
to find the simplest first-order optimization to get the most output from the
least engaging input. We feel that the potential benefits for finely-tuned modpacks
outweigh these risks.

# Implementation Quirks
Substances in this mod often have realistic properties we're not used to encountering
in mods. Properties like resistance and capacitance. You may find that a wire won't
accept 1FU of power, but *will* accept 50FU. This is resistance. You need to overcome
the hesitance of the wire to conduct. You may find that after pumping 50FU into that
wire, your machine four blocks away only receives 10FU. This is capacitance. The other
40FU are invested in the wire. The good news is, there's no signal reflection. You can
assume that everything is internally terminated.


Many blocks have a preferred direction. Not many blocks have internal configurations.
This is a design choice. Most of the time you'll need several blocks to accomplish a
task. You should be able to determine a machine's function from a screenshot or
blueprint of how the blocks are arranged. This helps encourage big towers full of
machinery instead of one box that does everything.


All the redstone cables and devices ignore phantom power. Phantom power *always* leads to
stranded charges and anisotropy. It's technically a bug that's only still here to preserve
badly-designed redstone machines.


# Balance Issues

Interop can be hard. Follow these simple rules, and you will experience maximum harmony:

* No ore tripling, quadrupling, quintupling, etc. Ever. - the absolute limit on ore multiplication
is 2.6 ingots per ore block, with typical gains being closer to 1.6
* A single block can never store more than 36 items, even when it's part of a multiblock
structure.
* When an item can be used as furnace fuel, it can often be burned to produce energy. It
should produce no more than 30FU per furnace fuel tick. It can produce this as slowly or
as quickly as you want.
* Wireless interactions are permitted, and encouraged because they are often good for servers.
* Time is not a balancing mechanic. Free energy that takes a long time to generate is still free
energy. (this is why there are no time constraints placed on energy generation)
* Material cost is not a balancing mechanic. An overpowered machine that requires 26 expensive
casing blocks is overpowered, and also not a real multiblock machine.


Vazkii has some really good writing on passive generation and why you want to avoid it
(http://vazkii.us/uncategorized/sins-of-a-solar-empire-or-the-passive-generation-conundrum/ )
and Extra Credits has a whole video on FOO ( https://www.youtube.com/watch?v=EitZRLt2G3w ).
This is really important, and I feel like we aren't getting it as modpack creators. Please think
about these things. They matter.

# Welcome to the home of trimixMix - android trimix mixing stick software
Most of continuous gas blenders use the same principle of operation. The air is mixed with the low pressure gas in a first mixing stage, analyzed for oxygen share, mixed with second gas and analyzed for oxygen again. First and second gas is pure oxygen and pure helium in any order, based on personal preference and desired mix. Desired oxygen and helium flow is adjusted using needle valves that are feed from gas regulators.

There is no need for expensive helium analyzer in this process; everything there is to know about end mix can be calculated from two readings of oxygen share.
Gas mix is than compressed using any diving compressor and filled into a scuba tank.
Based on which gas (oxygen or helium) is mixed with air in the first mixing stage and which gas is added in a second mixing stage, two flavors of procedure can be used:
• Oxygen first procedure
• Helium first procedure
The choose which procedure to use is made based on few criteria
• Avoiding oxygen rich readings (more than 40%) for safety reasons – oxygen first procedure produces higher readings on first analyzer. Mix richer than 40% must never be feed into compressor inlet.
• Avoiding oxygen low readings (less than 10%) for accuracy reasons – helium first procedure produces lower readings on the first analyzer
• Personal preference
The mix being filled can be calculated by the readings of the first and a second oxygen sensor. Commercial blending units use a microprocessor unit to calculate the mix in real time (plus they can provide some safety features like shutting off the oxygen supply in case of too rich oxygen concentration being feed into a compressor inlet). In fact the processor unit is the priciest part of commercial blender and that is why most home-build blending-units will use double nitrox analyzer instead.

Determining the reading of both oxygen analyzers that corresponds to desired mix is pretty easy math. In fact if “oxygen first” procedure is used the fist analyzer always reads the same value (that depends on MOD of the mix being filled), the reading of the second analyzer is the same as the oxygen share of the mix being filled.
The tricky part is setting both needle valves to set the desired oxygen readings for both analyzers. On oxygen first procedure opening oxygen valve will increase oxygen reading on both analyzers, opening helium valve will decrease reading on second analyzer and increase reading on the first. The situation is similar on helium first procedure.
Knowing that typical response time of oxygen sensor is 5 sec one can imagine that setting correct flows of oxygen and helium is long iterative process of adjusting small changes on both needle valves.

## How to use it?
The solutin consists of:
1. Adruino nano board with external AT1115 16 bit AD converter (see hw)
2. Android device
Adruino is connected to an android device via OTG USB cable

The project contains source code for both, arduion board and android device and an arduino setup schematic

## Questions?

Feel free to drop us an email or create issue right here on github.com

## Forks

If you have a useful fork that should be listed there please contact us

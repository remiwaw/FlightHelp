# FlightHelp

**A simple app using ryanair api**

Api link https://www.ryanair.com/api/booking/v4


DONE:
* Basic version: fetching albums, album info, artist info
* Uses:
    * MVI
    * RecyclerView
    * Retrofit
    * Coroutines

TODO:
* Better layouts / UI
* State is not yet saved on rotations
* UnitTests

Also i could find only flights with 1 connection a day
https://www.ryanair.com/api/booking/v4/en-gb/Availability?dateout=2020-12-30&roundtrip=false&origin=WRO&destination=DUB&adt=1&chd=0&inf=0&ToUs=AGREED 

So it wasnt really tested on some connection with more flights (it should be good though).

![](FlighHelper-min.gif)


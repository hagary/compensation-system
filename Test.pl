TA = ([(1,2),(1,3),(1,5),(2,3),(2,4),(3,1),(3,2),(4,4),(4,5)],[],[5,6]).
Group = ([(1,3),(1,5),(2,1),(2,2),(2,4),(4,2),(4,3),(4,5)],[],[3,5],35).
Rooms = [(1,1,[(1,1),(1,3),(2,3),(2,4),(3,4),(3,5),(4,2)],[],25,2),(2,1,[(1,1),(1,3),(2,3),(2,4),(3,4),(3,5),(4,2)],[],25,1),(3,3,[(1,2),(1,3),(1,4),(2,1),(2,2),(2,4),(4,2),(5,3)],[],20,2),(4,2,[(1,3),(1,5),(2,1),(2,4),(2,5),(3,2),(3,3),(4,2),(4,3),(4,4)],[],35,2)].
CompStart = (2,2).
Preference = ([(4,3)],[2,3,1])

compensate((TA,Group,2,CompStart,Preference,Rooms,[]),OUT).
RoomTuple = (CompRoomID, RoomLoc, RoomCapacity, RoomType),

IN = (TA, Group, CompStart, Preferences, Holidays,Pref),
TA = (TAOccup, TAComp, TAOff),
Group = (GroupOccup, GroupComp, GroupOff, GroupSize),
Rooms = [RoomsIDs, RoomsLocs, RoomsCaps, RoomsTypes, RoomsOccupList, RoomsCompList],

Rooms = [[1,2,3],[2,2,2],[30,40,30],[2,1,1],[[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[1,0,1,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]], []],
TA = ([],[],[]),
Group = ([],[],[], 40),
CompStart = (1,1),
Preferences = [(1,1)],
compensate((TA, Group, CompStart, Preferences, [], Rooms, 1, [2,1,3]), OUT).

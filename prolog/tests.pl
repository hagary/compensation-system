TA = (TAOccup, TAComp, TAOff),
TAOccup = [(1,3)], TAComp = [(1,1,4)], TAOff = [],

Group = (GroupOccup, GroupComp, GroupOff, GroupSize),
GroupOccup = [(2,1)], GroupComp = [(1,2,2)], GroupOff = [], GroupSize = 40,

CompStart = (1,1),

Rooms = [RoomsIDs, RoomsLocs, RoomsCaps, RoomsTypes, RoomsOccupLists, RoomsCompLists],
RoomsIDs = [1,2,3],
RoomsLocs = [2,2,2],
RoomsCaps = [30,40,30],
RoomsTypes = [1,1,3],
RoomsOccupLists = [0,512,0],
RoomsCompLists = [[0,256,0],[0,0,0],[10,0,0]],

Preferences = (PrefTimes, PrefRoomType, PrefRoomLocs),
PrefTimes = [(1,1)],
PrefRoomType = 1,
PrefRoomLocs = [2,1,3],

Holidays = [(2,1)],

IN = (TA, Group, CompStart, Holidays, Rooms, Preferences),
compensate(IN, OUT).

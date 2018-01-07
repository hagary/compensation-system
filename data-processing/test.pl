TA = (TAOccup, TAComp, TAOff),
TAOccup = [(1,2), (1,1), (1,1)],
TAOff = [2, 3, 4, 5, 6, 7],
Group = (GroupOccup, GroupComp, GroupOff, GroupSize),
GroupSize = 25,
GroupOccup = [(6,4), (2,1), (3,4), (3,2), (2,4), (6,2), (6,2), (6,3), (1,1), (2,2), (2,3), (1,4), (3,1), (1,3), (1,2)],
GroupComp = [],
CompStart = (1,1),
Rooms = [RoomsIDs, RoomsLocs, RoomsCaps, RoomsTypes, RoomsOccupLists, RoomsCompLists],
RoomsIDs = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39],
RoomsLocs = [2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2],
RoomsCaps = [30, 28, 30, 27, 255, 255, 102, 27, 27, 26, 28, 29, 29, 29, 29, 21, 29, 30, 34, 30, 32, 23, 29, 29, 30, 27, 29, 29, 30, 29, 29, 26, 25, 34, 104, 24, 99, 32, 26],
RoomsTypes = [1, 2, 2, 2, 3, 3, 3, 1, 2, 2, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 3, 2, 2, 2, 2, 2, 3, 2, 3, 2, 2],
RoomsOccupLists = [626262016, 553648192, 18087936, 67108864, 134221824, 134217728, 352321536, 138412032, 67108864, 262144, 358879232, 17039424, 805306368, 268439552, 268697600, 262144, 262144, 262208, 67371008, 536870912, 18874368, 262144, 341839872, 71303168, 67108864, 17039360, 2097152, 272633856, 5246976, 17829888, 6291456, 4456448, 4194304, 335544384, 19136576, 262144, 805306368, 268435456, 553648128],
RoomsCompLists = [],
Preferences = (PrefTimes, PrefRoomType, PrefRoomLocs),
PrefTimes = [(1,1)],
PrefRoomType = 1,
PrefRoomLocs = [2, 3, 1],
Holidays = [(3,3)],
IN = (TA, Group, CompStart, Holidays, Rooms, Preferences),
compensate(IN, OUT).
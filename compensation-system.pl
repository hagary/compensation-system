:- use_module(library(clpfd)).

compensate(IN, OUT):-
  %----------------------------VARIABLES------------------------------
  IN = (TA, Group, PrefRoomType, CompStart, Preferences, Rooms, Holidays),
  TA = (TAOccup, TAComp, TAOff),
  Group = (GroupOccup, GroupComp, GroupOff,GroupSize),
  CompStart = (CompStWeek, CompStDay),
  % e.g. Times = [(WD, Slot)]
  % Places (ordered) = [B->1, C->2, D->3]
  % RoomType = [lab->1, tut->2, lec->3]
  Compensation = (CompWeek, CompDay, CompSlot, CompRoom, RoomLoc),

  %----------------------------DOMAINS------------------------------
  CompWeek in 1..16,
  CompDay in 1..6,
  CompSlot in 1..5,
  member(RoomTuple, Rooms),
  RoomTuple = (CompRoom, RoomLoc,  RoomOccup, RoomCompDates, RoomCapacity, RoomType),

  %----------------------------CONSTRAINTS------------------------------
  valid_date(CompStWeek, CompStDay, CompWeek, CompDay),

  % Validate the type of the room.
  validate_room_type(RoomType,PrefRoomType),

  % Compensation Room should roughly fit the group size.
  RoomCapacity #>= GroupSize - 10,

  % Compensation time should not be during a normal scheduled session for both group & TA.
  not_member((CompDay, CompSlot), TAOccup),
  not_member((CompDay, CompSlot), GroupOccup),
  % Compensation should not be held in an occupied room.
  not_member((CompDay, CompSlot), RoomOccup),
  % Compensation time should not be during a compensation by the TA or for the group.
  not_member((CompDate, CompSlot), TAComp),
  not_member((CompDate, CompSlot), GroupComp),
  % Compensation should not be held in a room scheduled for another compensation.
  not_member((CompDate, CompSlot), RoomCompDates),
  % Compensation Date should not be on an official holiday.
  not_member(CompDate, Holidays),

  %----------------------------LABELING------------------------------
  cost(TAOff, GroupOff, (RoomCapacity,GroupSize), CompStart, Preferences, Compensation, Cost),
  OUT = ('Cost: ', Cost, 'Week: ', CompWeek, 'Day: ', CompDay, 'Slot: ', CompSlot, 'Room: ', CompRoom, 'Location: ', RoomLoc),
  labeling([min(Cost)], [Cost, CompWeek, CompDay, CompSlot, CompRoom, CompWeek]).

%------------------------------------------------------------------------------------------------------------------------
%------------------------------------------------------------------------------------------------------------------------

valid_date(StartWeek, StartDay, CompWeek, CompDay):-
  StartWeek #= CompWeek,
  CompDay #>= StartDay.
valid_date(StartWeek, _, CompWeek, _):-
  CompWeek #> StartWeek.

validate_room_type(1,1).
  validate_room_type(_,PrefRoomType):-
  PrefRoomType #\= 1.

% Calculate diff between two dates.
date_diff((W,D),(CompW,CompD),Diff):-
  Diff #= 6*(CompW-W)+(CompD-D).

not_member(_, []).
not_member((X,Y), L ):-
  L = [(_,Y1) | T],
  Y #\= Y1,
  not_member((X,Y) , T).
not_member((X,Y), L ):-
  L = [(X1,_) | T],
  X #\= X1,
  not_member((X,Y) , T).

% Calculate the cost of the preference day compared to the found day.
member_tuple(1, [(D1,S1)|_],(D2,S2)):-
  D1 #= D2,
  S1 #= S2.
member_tuple(X, [(_,_)|T],(D,S)):-
  X #> 1,
  X1 #= X - 1,
  member_tuple(X1,T,(D,S)).

%Calculate Cost of the Week Day according to the days off of TA and Group.
cost_of_TA(Arr,Element,C):-
  element(_,Arr,Element),
  C #= 10.
cost_of_TA(_,_,C):-
  C #= 0.

cost_of_group(Arr,Element,C):-
  element(_,Arr,Element),
  C #= 10.
cost_of_group(_,_,C):-
  C #= 0.

cost_of_time((Week, _), Times, (Week, CompD, CompSlot),C):-
  member_tuple(C,Times,(CompD, CompSlot)),
  write('prefered cost: ').
cost_of_time((StartW,StartD),Times,T,C):-
  write('difference cost: '),
  T = (CompW, CompD, _),
  length(Times,N),
  date_diff((StartW,StartD),(CompW,CompD),C1),
  C #= (C1+1) * (N+1).

cost_of_loc(PrefLoc,RoomLoc,C):-
  element(C,PrefLoc,RoomLoc).

% Calculate the cost of the room capacity.
cost_of_capacity(RoomCapacity,GroupSize,C):-
  RoomCapacity #< GroupSize,
  C1 #= GroupSize - RoomCapacity,
  C  #= C1*10.
cost_of_capacity(RoomCapacity,GroupSize,C):-
  C #= RoomCapacity - GroupSize.

% Calculate total cost of the output.
cost(TAOff, GroupOff, Room, CompStart, Preferences, Compensation, C):-
  Compensation = (CompWeek, CompDay, CompSlot, _ , RoomLoc),
  Preferences = (Times, Places),
  Room  = (RoomCapacity, GroupSize),
  cost_of_TA(TAOff,CompDay,C1),
  cost_of_group(GroupOff,CompDay,C2),
  cost_of_time(CompStart,Times,(CompWeek,CompDay,CompSlot),C3),
  cost_of_loc(Places,RoomLoc,C4),
  cost_of_capacity(RoomCapacity,GroupSize,C5),
  C #= C1 + C2 + C3 + C4 + C5.

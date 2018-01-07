:- use_module(library(clpfd)).
:- use_module(library(sgml_write)).

:- ['query.pl'].
%----------------------------NUMBERING CODE-------------------------------------
% Locations: [Bs->1, Cs->2, Ds->3]
% Room Types: [lab->1, tut->2, lec->3]

weeks_count(16).
days_count(6).
slots_count(5).

compensate(IN, OUT):-

  %----------------------------VARIABLES----------------------------------------

  IN = (TA, Group, CompStart, Holidays, Rooms, Preferences),
  TA = (TAOccup, TAComp, TAOff),
  Group = (GroupOccup, GroupComp, GroupOff, GroupSize),
  CompStart = (CompStWeek, CompStDay),
  Compensation = (CompWeek, CompDay, CompSlot),
  Rooms = [RoomsIDs, RoomsLocs, RoomsCaps, RoomsTypes, RoomsOccups,
  RoomsCompsEachWeek],
  Preferences = (PrefTimes, PrefRoomType, PrefRoomLocs),

  %----------------------------DOMAINS------------------------------------------

  weeks_count(WeeksCount),
  days_count(DaysCount),
  slots_count(SlotsCount),
  WeeklySlotsCount is DaysCount*SlotsCount,
  length(RoomsCompsEachWeek, WeeksCount),

  % maplist(binary_number(WeeklySlotsCount), RoomsOccupLists, RoomsOccups),

  CompWeek in 1..WeeksCount,
  CompDay in 1..DaysCount,
  CompSlot in 1..SlotsCount,
  element(RoomIdx, RoomsIDs, RoomID),
  element(RoomIdx, RoomsLocs, RoomLoc),
  element(RoomIdx, RoomsCaps, RoomCap),
  element(RoomIdx, RoomsTypes, RoomType),
  element(RoomIdx, RoomsOccups, RoomOccup),
  maplist(get_comp(RoomIdx), RoomsCompsEachWeek, RoomCompEachWeek),

  %----------------------------CONSTRAINTS--------------------------------------

  %----------------------------TIME CONSTRAINTS---------------------------------

  % Validate the date of the compensation according to the required start date.
  valid_date(CompStWeek, CompStDay, CompWeek, CompDay),

  % Compensation time should not be during a normal scheduled session for both group & TA.
  is_member_pair(TAOccup, (CompDay, CompSlot), IsTAOccup),
  is_member_pair(GroupOccup, (CompDay, CompSlot), IsGroupOccup),
  IsTAOccup #= 0,
  IsGroupOccup #= 0,

  % Compensation time should not be during a compensation by the TA or for the group.
  is_member_triple(TAComp, (CompWeek, CompDay, CompSlot), IsTAComp),
  is_member_triple(GroupComp, (CompWeek, CompDay, CompSlot), IsGroupComp),
  IsTAComp #= 0,
  IsGroupComp #= 0,

  % Compensation date should not be on an official holiday.
  is_member_pair(Holidays, (CompWeek, CompDay), IsHoliday),
  IsHoliday #= 0,

  %----------------------------ROOM CONSTRAINTS---------------------------------

  % Validate the type of the room (if lab) according to the selected room type.
  valid_room_type(RoomType, PrefRoomType),

  % Compensation Room should roughly fit the group size.
  RoomCap #>= GroupSize - 10,

  % Get comp time in terms of weekly slot number.
  time_to_pair(CompTime, (CompDay, CompSlot)),

  % Compensation should not be held in an occupied room.
  binary_number(WeeklySlotsCount, RoomOccupList, RoomOccup),
  element(CompTime, RoomOccupList, IsRoomOccup),
  IsRoomOccup #= 0,

  % Compensation should not be held in a room scheduled for another compensation.
  element(CompWeek, RoomCompEachWeek, RoomCompInWeek),
  binary_number(WeeklySlotsCount, RoomCompInWeekList, RoomCompInWeek),
  element(CompTime, RoomCompInWeekList, IsRoomComp),
  IsRoomComp #= 0,


  %----------------------------LABELING-----------------------------------------

  cost(TAOff, GroupOff, CompStart, PrefTimes, Compensation, RoomLoc,
  PrefRoomLocs, RoomCap, GroupSize, Cost),
  OUT = ('Cost: ', Cost, 'Week: ', CompWeek, 'Day: ', CompDay, 'Slot: ',
  CompSlot, 'RoomID: ', RoomID),
  labeling([min(Cost)], [CompWeek, CompDay, CompSlot, RoomIdx]),

  %----------------------------OUTPUT-------------------------------------------
  open('output.xml', write, Stream),
  write_result(Stream, Compensation, RoomID),
  close(Stream).
%----------------------------END OF MAIN PREDICATE------------------------------

%-------------------------------------------------------------------------------
                              % HELPER PREDICATES
%-------------------------------------------------------------------------------
write_result(Stream, (W,D,S), RoomID):-
  Week = element(week, [], [W]), Day = element(day, [], [D]),
  Slot = element(slot, [], [S]), Room = element(room, [], [RoomID]),
  xml_write(Stream, [element(compensation, [], [Week, Day, Slot, Room])], [header(false)]).
%--------------------------------COST PREDICATES--------------------------------

% Calculate total cost of the output.
cost(TAOff, GroupOff, CompStart, PrefTimes, CompensationTime, RoomLoc, PrefLocs,
 RoomCap, GroupSize, C):-
  cost_of_time(CompStart, PrefTimes, TAOff, GroupOff, CompensationTime, C1),
  cost_of_capacity(RoomCap, GroupSize, C2),
  cost_of_loc(PrefLocs, RoomLoc, C3),
  C #= C1 + C2 + C3.

% Calculate the cost of the chosen time.
cost_of_time((StartW, StartD), PrefTimes, TAOff, GroupOff, (CompW, CompD,
CompSlot), C):-
  member_pair_idx(I, PrefTimes, (CompD, CompSlot), _),
  date_diff((StartW, StartD),(CompW,CompD), Diff),
  is_member(GroupOff, CompD, BGroup),
  is_member(TAOff, CompD, BTA),
  CTA #= BTA*10,
  CGroup #= BGroup*5,
  days_count(DaysInWeek),
  ((((I #= 0) #\/ (Diff #>= DaysInWeek)) #==> (C #= Diff + CTA + CGroup + 10))  #/\
  (((I #> 0) #/\ (Diff #< DaysInWeek)) #==> (C #= I))).

% Calculate the cost of the chosen location.
cost_of_loc(PrefLocs, RoomLoc, C):-
  member_idx(I, PrefLocs, RoomLoc, _),
  C #= I*2.

% Calculate the cost of the capacity of the chosen room.
cost_of_capacity(RoomCapacity, GroupSize, C):-
  Diff #= RoomCapacity - GroupSize,
  Diff #< 0 #==> C  #= - Diff,
  Diff #>= 0 #==> C #= Diff + 5.

%------------------------------DATE & ROOM VALIDATION --------------------------

valid_date(StartWeek, StartDay, CompWeek, CompDay):-
  ((CompWeek #= StartWeek) #/\ (CompDay #>= StartDay)) #\/
   (CompWeek #> StartWeek).
% If it's a lab, then the selected room type should be a lab too.
valid_room_type(RoomType, PrefRoomType):-
  (PrefRoomType #= 1) #==> (RoomType #= 1).

%------------------------------MEMBER PREDICATES--------------------------------

is_member([], _, 0).
is_member([H|T], Elem, B):-
  (Elem #= H #<==> B1) #/\ (B #<==> (B2 #\/ B1)),
  is_member(T, Elem, B2).

is_member_pair([], _, 0).
is_member_pair([(N,M)|T], (X,Y), B):-
  ((X #= N #/\ Y #= M) #<==> B1) #/\ (B #<==> (B2 #\/ B1)),
  is_member_pair(T, (X,Y), B2).

is_member_triple([], _, 0).
is_member_triple([(N, M, K)|T], (X,Y,Z), B):-
  ((X #= N #/\ Y #= M #/\ Z #= K) #<==> B1) #/\ (B #<==> (B2 #\/ B1)),
  is_member_triple(T, (X,Y,Z), B2).

member_idx(0,[], _, 0).
member_idx(I, [H|T], X, B):-
  ((H #= X) #<==> B1) #/\ (B #<==> (B2 #\/ B1))
  #/\ (B1 #= 1 #==> I #= 1) #/\ (B1 #= 0 #==> I #= B2*(I1+1)),
  member_idx(I1, T, X, B2).

member_pair_idx(0,[], _, 0).
member_pair_idx(I, [(N,M)|T], (X,Y), B):-
  ((X #= N #/\ Y #= M) #<==> B1) #/\ (B #<==> (B2 #\/ B1))
  #/\ (B1 #= 1 #==> I #= 1) #/\ (B1 #= 0 #==> I #= B2*(I1+1)),
  member_pair_idx(I1, T, (X,Y), B2).

%------------------------------DATES HANDLING-----------------------------------

% Calculate the days count between two dates (W,D,S).
date_diff((W,D),(CompW,CompD),Diff):-
  days_count(DC),
  Diff #= DC*(CompW - W) + (CompD - D).

% Convert a weekly slot to a (Day, Slot) pair (both ways).
time_to_pair(0, (0,0)):-!.
time_to_pair(T, (D, S)):-
  days_count(DC), slots_count(SC),
   D in 1..DC, S in 1..SC,
  (D-1)*SC + S #= T.

% Convert a semester slot to a (Week, Day, Slot) triple (both ways).
date_to_triple(0, (0,0,0)):-!.
date_to_triple(T, (W, D, S)):-
  weeks_count(WC) , days_count(DC), slots_count(SC),
   W in 1..WC, D in 1..DC, S in 1..SC,
  (W-1)*DC*SC + (D-1)*SC + S #= T.

%------------------------------BINARY CONVERSION--------------------------------

binary_number(BitCount, Bits, N) :-
    binary_number_min(BitCount, Bits, 0, N, N).

binary_number_min(0, [], N, N, _M):-!.
binary_number_min(BitCount, L, N0, N, M) :-
    L = [Bit|Bits], length(L, BitCount), BC1 #= BitCount - 1,
    Bit in 0..1,
    N1 #= N0*2 + Bit,
    M #>= N1,
    binary_number_min(BC1, Bits, N1, N, M).

%-------------------------------------------------------------------------------
get_comp(RoomIdx, WeekList, RoomCompInWeek):-
  element(RoomIdx, WeekList, RoomCompInWeek).
%-------------------------------------------------------------------------------

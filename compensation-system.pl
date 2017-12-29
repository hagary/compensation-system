:- use_module(library(clpfd)).

compensate(IN, OUT):-
  %----------------------------VARIABLES------------------------------
  IN = (TA, Group, CompStart, PrefTimes, Holidays, Rooms, PrefRoomType, PrefRoomLocs),
  TA = (TAOccup, TAComp, TAOff),
  Group = (GroupOccup, GroupComp, GroupOff, GroupSize),
  CompStart = (CompStWeek, CompStDay),
  % PrefPlaces (ordered) = [B->1, C->2, D->3]
  % RoomType = [lab->1, tut->2, lec->3]
  % Compensation = (CompWeek, CompDay, CompSlot, CompRoomID, RoomLoc),
  Compensation = (CompWeek, CompDay, CompSlot),

  %----------------------------DOMAINS------------------------------
  %TODO adjust back week range.
  CompWeek in 1..3,
  CompDay in 1..6,
  CompSlot in 1..5,

  % Domain of the room ID and location.
  member(RoomTuple, Rooms),
  RoomTuple = (CompRoomID, RoomLoc, RoomCapacity, RoomType, RoomOccup, RoomCompDates),

  %----------------------------CONSTRAINTS------------------------------------

  %----------------------------TIME CONSTRAINTS-------------------------------

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
  % Compensation Date should not be on an official holiday.
  is_member_pair(Holidays, (CompWeek, CompDay), IsHoliday),
  IsHoliday #= 0,

  %----------------------------ROOM CONSTRAINTS-------------------------------

  % Validate the type of the room (if lab) according to the selected room type.
  validate_room_type(RoomType, PrefRoomType),

  % Compensation Room should roughly fit the group size.
  RoomCapacity #>= GroupSize - 10,
  % % Compensation should not be held in an occupied room.
  is_member_pair(RoomOccup, (CompDay, CompSlot), IsRoomOccup),
  IsRoomOccup #= 0,
  % Compensation should not be held in a room scheduled for another compensation.
  is_member_triple(RoomCompDates, (CompWeek, CompDay,  CompSlot), IsRoomComp),
  IsRoomComp #= 0,


  %----------------------------LABELING------------------------------
  cost(TAOff, GroupOff, CompStart, PrefTimes, Compensation, RoomLoc, PrefRoomLocs, RoomCapacity, GroupSize, Cost),
  OUT = ('Cost: ', Cost, 'Week: ', CompWeek, 'Day: ', CompDay, 'Slot: ', CompSlot, 'RoomID: ', CompRoomID),
  labeling([min(Cost)], [CompWeek, CompDay, CompSlot]).

%------------------------------------------------------------------------------------------------------------------------
                                            % HELPER PREDICATES
%------------------------------------------------------------------------------------------------------------------------

valid_date(StartWeek, StartDay, CompWeek, CompDay):-
  ((CompWeek #= StartWeek) #/\ (CompDay #>= StartDay)) #\/
   (CompWeek #> StartWeek).

%------------------------------------------------------------------------------------------------------------------------
% Calculate total cost of the output.
cost(TAOff, GroupOff, CompStart, PrefTimes, Compensation, RoomLoc, PrefLocs, RoomCapacity, GroupSize, C):-
  cost_of_time(CompStart, PrefTimes, TAOff, GroupOff, Compensation, C1),
  cost_of_loc(PrefLocs, RoomLoc, C2),
  % cost_of_capacity(RoomCapacity, GroupSize, C5),
  C #= C1 + C2.
cost_of_time((StartW, StartD), PrefTimes, TAOff, GroupOff, (CompW, CompD, CompSlot), C):-
  member_pair_idx(I, PrefTimes, (CompD, CompSlot), _),
  date_diff((StartW, StartD),(CompW,CompD), Diff),
  is_member(GroupOff, CompD, BGroup),
  is_member(TAOff, CompD, BTA),
  CTA #= BTA*10,
  CGroup #= BGroup*5,
  ((((I #= 0) #\/ (Diff #>= 6)) #==> (C #= Diff + CTA + CGroup + 10))  #/\ (((I #> 0) #/\ (Diff #< 6)) #==> (C #= I))).

% If it's a lab, then the selected room type should be a lab too.
validate_room_type(RoomType, PrefRoomType):-
  (PrefRoomType #= 1) #==> (RoomType #= 1).
%------------------------------------------------------------------------------------------------------------------------

% Calculate diff between two dates.
date_diff((W,D),(CompW,CompD),Diff):-
  Diff #= 6*(CompW-W)+(CompD-D).
%------------------------------------------------------------------------------------------------------------------------

not_member(_,[]).
not_member(X, [H|T]):-
  X #\= H,
  not_member(X, T).

not_member_pair(_, []).
not_member_pair((X,Y), L ):-
  L = [(X1,_) | T],
  X #\= X1,
  not_member_pair((X,Y) , T).
not_member_pair((X,Y), L ):-
  L = [(X1,Y1) | T],
  X #= X1,
  Y #\= Y1,
  not_member_pair((X,Y) , T).

not_member_triple(_,[]).
not_member_triple((X,Y,Z), L ):-
  L = [(X1,_,_) | T],
  X #\= X1,
  not_member_triple((X,Y,Z) , T).
not_member_triple((X,Y,Z), L ):-
  L = [(X1,Y1,_) | T],
  X #= X1,
  Y #\= Y1,
  not_member_triple((X,Y,Z) , T).
not_member_triple((X,Y,Z), L ):-
  L = [(X1,Y1,Z1) | T],
  X #= X1,
  Y #= Y1,
  Z #\= Z1,
  not_member_triple((X,Y,Z) , T).
%------------------------------------------------------------------------------------------------------------------------
member_pair(0, L, V):-
  is_member_pair(L, V, 0).
member_pair(1, [(D1,S1)|_], (D,S)):-
  D1 #= D,
  S1 #= S.
member_pair(X, [(_,_)|T], (D,S)):-
  X #> 1,
  X1 #= X - 1,
  member_pair(X1, T, (D,S)).


room_member(1, [H|_], Room):-
  Room = (CompRoomID, CompRoomLoc, CompRoomCapacity, CompRoomType, CompRoomOcuup),
  H = (RoomID, RoomLoc, RoomCapacity, RoomType),
  RoomID #= CompRoomID, RoomLoc #= CompRoomLoc, RoomCapacity #= CompRoomCapacity, CompRoomType #= RoomType.

room_member(X, [_|T], Room):-
  X #> 1,
  X1 #= X - 1,
  room_member(X1, T, Room).

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

member_pair_idx(0,[], _, 0).
member_pair_idx(I, [(N,M)|T], (X,Y), B):-
  ((X #= N #/\ Y #= M) #<==> B1) #/\ (B #<==> (B2 #\/ B1))
  #/\ (B1 #= 1 #==> I #= 1) #/\ (B1 #= 0 #==> I #= B2*(I1+1)),
  member_pair_idx(I1, T, (X,Y), B2).

member_idx(0,[], _, 0).
member_idx(I, [H|T], X, B):-
  ((H #= X) #<==> B1) #/\ (B #<==> (B2 #\/ B1))
  #/\ (B1 #= 1 #==> I #= 1) #/\ (B1 #= 0 #==> I #= B2*(I1+1)),
  member_idx(I1, T, X, B2).

% B = 1 if elem is in Arr, B = 0 otherwise.
% is_member(Arr, Elem, B):-
%   reifiable_element(Idx, Arr, Elem),
%   (Idx #> 0) #<==> B.

% reifiable_element(Idx, Arr, Elem):-
%   element(Idx, Arr, Elem).
% reifiable_element(0, Arr, Elem):-
%   not_member(Elem, Arr).


%------------------------------------------------------------------------------------------------------------------------
%
% cost_of_dayOff_TA(DaysOff, CompDay, C):-
%   is_member(DaysOff, CompDay, B),
%   C #= B*10.
% cost_of_dayOff_Group(DaysOff, CompDay, C):-
%   is_member(DaysOff, CompDay, B),
%   C #= B*5.


cost_of_loc(PrefLocs, RoomLoc, C):-
  member_idx(I, PrefLocs, RoomLoc, _),
  C #= I * 5.

% Calculate the cost of the room capacity.
cost_of_capacity(RoomCapacity, GroupSize, C):-
  Diff #= RoomCapacity - GroupSize,
  Diff #< 0 #==> C  #= -Diff*10,
  Diff #>=0 #==> C #= Diff.

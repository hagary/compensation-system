:- use_module(library(clpfd)).
% Locations: [Bs->1, Cs->2, Ds->3]
% Room Type: [lab->1, tut->2, lec->3]

compensate(IN, OUT):-
  %----------------------------VARIABLES------------------------------
  IN = (TA, Group, CompStart, PrefTimes, Holidays, Rooms, PrefRoomType, PrefRoomLocs),
  TA = (TAOccup, TAComp, TAOff),
  Group = (GroupOccup, GroupComp, GroupOff, GroupSize),
  CompStart = (CompStWeek, CompStDay),
  Compensation = (CompWeek, CompDay, CompSlot),
  Rooms = [RoomsIDs, RoomsLocs, RoomsCaps, RoomsTypes, RoomsOccupLists, RoomsCompList],
  %TODO adjust slots count to 30
  SlotsCount = 15,
  maplist(binary_number(SlotsCount), RoomsOccupLists, RoomsOccups),
  %----------------------------DOMAINS------------------------------

  %TODO adjust back week and day domains.
  CompWeek in 1..3,
  CompDay in 1..6,
  CompSlot in 1..3,
  element(RoomIdx, RoomsIDs, RoomID),
  element(RoomIdx, RoomsLocs, RoomLoc),
  element(RoomIdx, RoomsCaps, RoomCap),
  element(RoomIdx, RoomsTypes, RoomType),
  element(RoomIdx, RoomsOccups, RoomOccup),

  binary_number(SlotsCount, RoomOccupList, RoomOccup),

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
  RoomCap #>= GroupSize - 10,
  % Compensation should not be held in an occupied room.
  % is_member_pair(RoomOccup, (CompDay, CompSlot), IsRoomOccup),
  % IsRoomOccup #= 0,
  time_to_pair(CompTime, (CompDay, CompSlot)), write(CompTime),
  element(CompTime, RoomOccupList, IsRoomOccup),
  IsRoomOccup #= 0,
  % % Compensation should not be held in a room scheduled for another compensation.
  % is_member_triple(RoomComp, (CompWeek, CompDay,  CompSlot), IsRoomComp),
  % IsRoomComp #= 0,


  %----------------------------LABELING------------------------------
  cost(TAOff, GroupOff, CompStart, PrefTimes, Compensation, RoomLoc, PrefRoomLocs, RoomCap, GroupSize, Cost),
  OUT = ('Cost: ', Cost, 'Week: ', CompWeek, 'Day: ', CompDay, 'Slot: ', CompSlot, 'RoomID: ', RoomID),
  labeling([min(Cost)], [CompWeek, CompDay, CompSlot, RoomIdx]).

%------------------------------------------------------------------------------------------------------------------------
                                            % HELPER PREDICATES
%------------------------------------------------------------------------------------------------------------------------


%------------------------------------------------------------------------------------------------------------------------
% Calculate total cost of the output.
cost(TAOff, GroupOff, CompStart, PrefTimes, CompensationTime, RoomLoc, PrefLocs, RoomCap, GroupSize, C):-
  cost_of_time(CompStart, PrefTimes, TAOff, GroupOff, CompensationTime, C1),
  cost_of_capacity(RoomCap, GroupSize, C2),
  cost_of_loc(PrefLocs, RoomLoc, C3),
  C #= C1 + C2 + C3.

cost_of_time((StartW, StartD), PrefTimes, TAOff, GroupOff, (CompW, CompD, CompSlot), C):-
  member_pair_idx(I, PrefTimes, (CompD, CompSlot), _),
  date_diff((StartW, StartD),(CompW,CompD), Diff),
  is_member(GroupOff, CompD, BGroup),
  is_member(TAOff, CompD, BTA),
  CTA #= BTA*10,
  CGroup #= BGroup*5,
  ((((I #= 0) #\/ (Diff #>= 6)) #==> (C #= Diff + CTA + CGroup + 10))  #/\ (((I #> 0) #/\ (Diff #< 6)) #==> (C #= I))).

cost_of_loc(PrefLocs, RoomLoc, C):-
  member_idx(I, PrefLocs, RoomLoc, _),
  C #= I*5.

cost_of_capacity(RoomCapacity, GroupSize, C):-
  Diff #= RoomCapacity - GroupSize,
  Diff #< 0 #==> C  #= - Diff,
  Diff #>= 0 #==> C #= Diff + 5.
%------------------------------------------------------------------------------------------------------------------------

valid_date(StartWeek, StartDay, CompWeek, CompDay):-
  ((CompWeek #= StartWeek) #/\ (CompDay #>= StartDay)) #\/
   (CompWeek #> StartWeek).
% If it's a lab, then the selected room type should be a lab too.
validate_room_type(RoomType, PrefRoomType):-
  (PrefRoomType #= 1) #==> (RoomType #= 1).

% Calculate diff between two dates.
date_diff((W,D),(CompW,CompD),Diff):-
  Diff #= 6*(CompW-W)+(CompD-D).
%------------------------------------------------------------------------------------------------------------------------

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

times_to_pairs(Times, Pairs):-
  maplist(time_to_pair, Times, Pairs).
time_to_pair(0, (0,0)):-!.
time_to_pair(T, (D, S)):-
   D in 1..6, S in 1..5,
  (D-1)*5 + S #= T.

dates_to_triples(Dates, Pairs):-
  maplist(date_to_triple, Dates, Pairs).
date_to_triple(0, (0,0,0)):-!.
date_to_triple(T, (W, D, S)):-
  %TODO fix week range
   W in 1..3, D in 1..6, S in 1..5,
  (W-1)*5*6 + (D-1)*5 + S #= T.
%--------------------------------------------------------------------------
dom_integers(D, Is) :- phrase(dom_integers_(D), Is).

dom_integers_(I)      --> { integer(I) }, [I].
dom_integers_(L..U)   --> { numlist(L, U, Is) }, Is.
dom_integers_(D1\/D2) --> dom_integers_(D1), dom_integers_(D2).

set_domain(Bases, Var) :-
        make_domain(Bases, Term),
        Var in Term.
make_domain([], 0).
make_domain([H], H):-!.
make_domain([H | T], '\\/'(H, TDomain)) :-
      make_domain(T, TDomain).

binary_number(BitCount, Bits, N) :-
    binary_number_min(BitCount, Bits, 0, N, N).

binary_number_min(0, [], N, N, _M):-!.
binary_number_min(BitCount, L, N0, N, M) :-
    L = [Bit|Bits], length(L, BitCount), BC1 #= BitCount - 1,
    Bit in 0..1,
    N1 #= N0*2 + Bit,
    M #>= N1,
    binary_number_min(BC1, Bits, N1, N, M).

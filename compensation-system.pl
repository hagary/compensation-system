:- use_module(library(clpfd)).
compensate(IN, OUT):-
  %----------------------------VARIABLES------------------------------
  IN = (TA, Group, Rooms, Holidays, CompStart, Preferences),
  TA = (TAOccup, TAComp, TAOff),
  Group = (GroupOccup, GroupComp, GroupOff),
  CompStart = (CompStWeek, CompStDay),
  Preferences = (Times, Places),
  % e.g. Times = [(WD, Slot)], Places (ordered) = [B->1, C->2, D->3]

  OUT = (CompWeek, CompDay, CompSlot, CompRoom, RoomLoc),

  %----------------------------DOMAINS------------------------------
  CompWeek in 1..16,
  CompSlot in 1..5,
  CompDay in 1..6,
  element(_, Rooms, RoomTuple),
  RoomTuple = (CompRoom, RoomLoc,  RoomOccup, RoomCompDates),

  %----------------------------CONSTRAINTS------------------------------
  valid_date(CompStWeek, CompStDay, CompWeek, CompDay),

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
  cost(TAOff, GroupOff, Preferences, OUT, C),
  labeling([min(C)], [CompWeek, CompDay, CompSlot, CompRoom]).

% TODO cost(TAOff, GroupOff, Preferences, OUT, C).

valid_date(W, StartDay, W, CompDay):-
  CompDay #>= StartDay.
valid_date(StartWeek, StartDay, CompWeek, CompDay):-
  CompWeek #> StartWeek.


not_member(_, []).
not_member(V, L):-
  L = [H | T],
  V #\= H,
  not_member(V, T).

goog.provide('puerto_rico.game.rules');


puerto_rico.game.rules.execute_settler = (function puerto_rico$game$rules$execute_settler(game_state,player_id,plantation_choice){

var player_idx = cljs.core.first(cljs.core.first(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38781_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(cljs.core.second(p1__38781_SHARP_)),player_id);
}),cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_state)))));
var available_plantations = cljs.core.keys(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38782_SHARP_){
return (cljs.core.val(p1__38782_SHARP_) > (0));
}),new cljs.core.Keyword(null,"plantation-supply","plantation-supply",-1569976839).cljs$core$IFn$_invoke$arity$1(game_state)));
if(cljs.core.truth_((function (){var and__5023__auto__ = plantation_choice;
if(cljs.core.truth_(and__5023__auto__)){
var and__5023__auto____$1 = cljs.core.contains_QMARK_(cljs.core.set(available_plantations),plantation_choice);
if(and__5023__auto____$1){
var and__5023__auto____$2 = player_idx;
if(cljs.core.truth_(and__5023__auto____$2)){
return (((player_idx >= (0))) && ((player_idx < cljs.core.count(new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_state)))));
} else {
return and__5023__auto____$2;
}
} else {
return and__5023__auto____$1;
}
} else {
return and__5023__auto__;
}
})())){
return cljs.core.update_in.cljs$core$IFn$_invoke$arity$3(cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(game_state,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx,new cljs.core.Keyword(null,"plantations","plantations",1490777018)], null),cljs.core.conj,plantation_choice),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"plantation-supply","plantation-supply",-1569976839),plantation_choice], null),cljs.core.dec);
} else {
return game_state;
}
});
puerto_rico.game.rules.execute_mayor = (function puerto_rico$game$rules$execute_mayor(game_state){

var total_colonists = new cljs.core.Keyword(null,"colonist-supply","colonist-supply",666169845).cljs$core$IFn$_invoke$arity$1(game_state);
var num_players = cljs.core.count(new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_state));
var colonists_per_player = (function (){var x__5113__auto__ = (1);
var y__5114__auto__ = cljs.core.quot(total_colonists,num_players);
return ((x__5113__auto__ < y__5114__auto__) ? x__5113__auto__ : y__5114__auto__);
})();
var remaining_colonists = (total_colonists - (colonists_per_player * num_players));
return cljs.core.update.cljs$core$IFn$_invoke$arity$4(cljs.core.update.cljs$core$IFn$_invoke$arity$3(game_state,new cljs.core.Keyword(null,"players","players",-1361554569),(function (players){
return cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (p1__38783_SHARP_){
return cljs.core.update.cljs$core$IFn$_invoke$arity$3(p1__38783_SHARP_,new cljs.core.Keyword(null,"colonists","colonists",-1960965550),(function (current){
return cljs.core.vec(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(current,cljs.core.repeat.cljs$core$IFn$_invoke$arity$2(colonists_per_player,new cljs.core.Keyword(null,"colonist","colonist",2134042039))));
}));
}),players);
})),new cljs.core.Keyword(null,"colonist-supply","colonist-supply",666169845),cljs.core._,(colonists_per_player * num_players));
});
puerto_rico.game.rules.can_build_building_QMARK_ = (function puerto_rico$game$rules$can_build_building_QMARK_(player,building_key,building_info){

return (((new cljs.core.Keyword(null,"money","money",250333921).cljs$core$IFn$_invoke$arity$1(player) >= new cljs.core.Keyword(null,"cost","cost",-1094861735).cljs$core$IFn$_invoke$arity$1(building_info))) && ((((!(cljs.core.contains_QMARK_(cljs.core.set(new cljs.core.Keyword(null,"buildings","buildings",-308691065).cljs$core$IFn$_invoke$arity$1(player)),building_key)))) && ((cljs.core.count(new cljs.core.Keyword(null,"buildings","buildings",-308691065).cljs$core$IFn$_invoke$arity$1(player)) < (12))))));
});
puerto_rico.game.rules.execute_builder = (function puerto_rico$game$rules$execute_builder(game_state,player_id,building_choice){

var player_idx = cljs.core.first(cljs.core.first(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38784_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(cljs.core.second(p1__38784_SHARP_)),player_id);
}),cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_state)))));
var player = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(game_state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx], null));
var building_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(puerto_rico.game.state.buildings,building_choice);
if(cljs.core.truth_((function (){var and__5023__auto__ = building_choice;
if(cljs.core.truth_(and__5023__auto__)){
var and__5023__auto____$1 = building_info;
if(cljs.core.truth_(and__5023__auto____$1)){
return puerto_rico.game.rules.can_build_building_QMARK_(player,building_choice,building_info);
} else {
return and__5023__auto____$1;
}
} else {
return and__5023__auto__;
}
})())){
return cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(game_state,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx,new cljs.core.Keyword(null,"buildings","buildings",-308691065)], null),cljs.core.conj,building_choice),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx,new cljs.core.Keyword(null,"money","money",250333921)], null),cljs.core._,new cljs.core.Keyword(null,"cost","cost",-1094861735).cljs$core$IFn$_invoke$arity$1(building_info));
} else {
return game_state;
}
});
puerto_rico.game.rules.execute_craftsman = (function puerto_rico$game$rules$execute_craftsman(game_state){

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Craftsman role executed (simplified version)"], 0));

return game_state;
});
puerto_rico.game.rules.can_trade_good_QMARK_ = (function puerto_rico$game$rules$can_trade_good_QMARK_(game_state,player,good){

return (((cljs.core.get_in.cljs$core$IFn$_invoke$arity$3(player,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"goods","goods",702040840),good], null),(0)) > (0))) && ((((!(cljs.core.contains_QMARK_(cljs.core.set(cljs.core.map.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"good","good",511701169),new cljs.core.Keyword(null,"trading-house","trading-house",1920769251).cljs$core$IFn$_invoke$arity$1(game_state))),good)))) && ((cljs.core.count(new cljs.core.Keyword(null,"trading-house","trading-house",1920769251).cljs$core$IFn$_invoke$arity$1(game_state)) < (4))))));
});
puerto_rico.game.rules.execute_trader = (function puerto_rico$game$rules$execute_trader(game_state,player_id,good_choice){

var player_idx = cljs.core.first(cljs.core.first(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38785_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(cljs.core.second(p1__38785_SHARP_)),player_id);
}),cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_state)))));
var player = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(game_state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx], null));
var trade_value = (function (){var G__38786 = good_choice;
var G__38786__$1 = (((G__38786 instanceof cljs.core.Keyword))?G__38786.fqn:null);
switch (G__38786__$1) {
case "corn":
return (0);

break;
case "indigo":
return (1);

break;
case "sugar":
return (2);

break;
case "tobacco":
return (3);

break;
case "coffee":
return (4);

break;
default:
return (0);

}
})();
if(cljs.core.truth_((function (){var and__5023__auto__ = good_choice;
if(cljs.core.truth_(and__5023__auto__)){
return puerto_rico.game.rules.can_trade_good_QMARK_(game_state,player,good_choice);
} else {
return and__5023__auto__;
}
})())){
return cljs.core.update.cljs$core$IFn$_invoke$arity$4(cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(cljs.core.update_in.cljs$core$IFn$_invoke$arity$3(game_state,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx,new cljs.core.Keyword(null,"goods","goods",702040840),good_choice], null),cljs.core.dec),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx,new cljs.core.Keyword(null,"money","money",250333921)], null),cljs.core._PLUS_,trade_value),new cljs.core.Keyword(null,"trading-house","trading-house",1920769251),cljs.core.conj,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"good","good",511701169),good_choice,new cljs.core.Keyword(null,"player-id","player-id",1003896428),player_id], null));
} else {
return game_state;
}
});
puerto_rico.game.rules.find_ship_for_good = (function puerto_rico$game$rules$find_ship_for_good(ships,good,amount){

return cljs.core.first(cljs.core.sort_by.cljs$core$IFn$_invoke$arity$2((function (p__38787){
var vec__38788 = p__38787;
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38788,(0),null);
var ship = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38788,(1),null);
return new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship);
}),cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p__38791){
var vec__38792 = p__38791;
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38792,(0),null);
var ship = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38792,(1),null);
return ((new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship) - new cljs.core.Keyword(null,"amount","amount",364489504).cljs$core$IFn$_invoke$arity$1(ship)) >= amount);
}),cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p__38795){
var vec__38796 = p__38795;
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38796,(0),null);
var ship = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38796,(1),null);
return (((new cljs.core.Keyword(null,"good","good",511701169).cljs$core$IFn$_invoke$arity$1(ship) == null)) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"good","good",511701169).cljs$core$IFn$_invoke$arity$1(ship),good)));
}),cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,ships)))));
});
puerto_rico.game.rules.execute_captain = (function puerto_rico$game$rules$execute_captain(game_state,player_id,good_choice){

var player_idx = cljs.core.first(cljs.core.first(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38799_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(cljs.core.second(p1__38799_SHARP_)),player_id);
}),cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_state)))));
var player = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(game_state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx], null));
var amount_to_ship = cljs.core.get_in.cljs$core$IFn$_invoke$arity$3(player,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"goods","goods",702040840),good_choice], null),(0));
var ship_choice = puerto_rico.game.rules.find_ship_for_good(new cljs.core.Keyword(null,"ships","ships",-875113158).cljs$core$IFn$_invoke$arity$1(game_state),good_choice,amount_to_ship);
if(cljs.core.truth_((function (){var and__5023__auto__ = good_choice;
if(cljs.core.truth_(and__5023__auto__)){
var and__5023__auto____$1 = (amount_to_ship > (0));
if(and__5023__auto____$1){
return ship_choice;
} else {
return and__5023__auto____$1;
}
} else {
return and__5023__auto__;
}
})())){
var vec__38800 = ship_choice;
var ship_idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38800,(0),null);
var ship = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38800,(1),null);
var actual_amount = (function (){var x__5113__auto__ = amount_to_ship;
var y__5114__auto__ = (new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship) - new cljs.core.Keyword(null,"amount","amount",364489504).cljs$core$IFn$_invoke$arity$1(ship));
return ((x__5113__auto__ < y__5114__auto__) ? x__5113__auto__ : y__5114__auto__);
})();
return cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(cljs.core.assoc_in(cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(game_state,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx,new cljs.core.Keyword(null,"goods","goods",702040840),good_choice], null),cljs.core._,actual_amount),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),player_idx,new cljs.core.Keyword(null,"victory-points","victory-points",-2106714317)], null),cljs.core._PLUS_,actual_amount),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ships","ships",-875113158),ship_idx,new cljs.core.Keyword(null,"good","good",511701169)], null),good_choice),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ships","ships",-875113158),ship_idx,new cljs.core.Keyword(null,"amount","amount",364489504)], null),cljs.core._PLUS_,actual_amount);
} else {
return game_state;
}
});
puerto_rico.game.rules.select_role = (function puerto_rico$game$rules$select_role(game_state,player_id,role){

if(cljs.core.contains_QMARK_(new cljs.core.Keyword(null,"available-roles","available-roles",-1628893019).cljs$core$IFn$_invoke$arity$1(game_state),role)){
var new_state = cljs.core.update.cljs$core$IFn$_invoke$arity$4(cljs.core.update.cljs$core$IFn$_invoke$arity$4(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(game_state,new cljs.core.Keyword(null,"selected-role","selected-role",8663708),role),new cljs.core.Keyword(null,"role-player-idx","role-player-idx",1417379264),cljs.core.first(cljs.core.first(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38803_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(cljs.core.second(p1__38803_SHARP_)),player_id);
}),cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_state)))))),new cljs.core.Keyword(null,"phase","phase",575722892),new cljs.core.Keyword(null,"role-execution","role-execution",-1842623549)),new cljs.core.Keyword(null,"available-roles","available-roles",-1628893019),cljs.core.disj,role),new cljs.core.Keyword(null,"used-roles","used-roles",1915499651),cljs.core.conj,role);
var G__38807 = role;
var G__38807__$1 = (((G__38807 instanceof cljs.core.Keyword))?G__38807.fqn:null);
switch (G__38807__$1) {
case "mayor":
case "craftsman":
case "prospector":
var G__38808 = (function (){var G__38809 = (puerto_rico.game.rules.execute_role.cljs$core$IFn$_invoke$arity$3 ? puerto_rico.game.rules.execute_role.cljs$core$IFn$_invoke$arity$3(new_state,role,player_id) : puerto_rico.game.rules.execute_role.call(null,new_state,role,player_id));
return (puerto_rico.game.rules.end_role_execution.cljs$core$IFn$_invoke$arity$1 ? puerto_rico.game.rules.end_role_execution.cljs$core$IFn$_invoke$arity$1(G__38809) : puerto_rico.game.rules.end_role_execution.call(null,G__38809));
})();
return (puerto_rico.game.rules.end_round.cljs$core$IFn$_invoke$arity$1 ? puerto_rico.game.rules.end_round.cljs$core$IFn$_invoke$arity$1(G__38808) : puerto_rico.game.rules.end_round.call(null,G__38808));

break;
default:
var current_player = puerto_rico.game.state.current_player(new_state);
var has_valid_actions = (function (){var G__38810 = role;
var G__38810__$1 = (((G__38810 instanceof cljs.core.Keyword))?G__38810.fqn:null);
switch (G__38810__$1) {
case "settler":
return cljs.core.seq(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38804_SHARP_){
return (cljs.core.val(p1__38804_SHARP_) > (0));
}),new cljs.core.Keyword(null,"plantation-supply","plantation-supply",-1569976839).cljs$core$IFn$_invoke$arity$1(new_state)));

break;
case "builder":
return cljs.core.seq(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p__38811){
var vec__38812 = p__38811;
var building = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38812,(0),null);
var info = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__38812,(1),null);
return puerto_rico.game.rules.can_build_building_QMARK_(current_player,building,info);
}),puerto_rico.game.state.buildings));

break;
case "trader":
return cljs.core.seq(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38805_SHARP_){
return puerto_rico.game.rules.can_trade_good_QMARK_(new_state,current_player,p1__38805_SHARP_);
}),puerto_rico.game.state.goods));

break;
case "captain":
return cljs.core.seq(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__38806_SHARP_){
return (cljs.core.get_in.cljs$core$IFn$_invoke$arity$3(current_player,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"goods","goods",702040840),p1__38806_SHARP_], null),(0)) > (0));
}),puerto_rico.game.state.goods));

break;
default:
return true;

}
})();
if(cljs.core.truth_(has_valid_actions)){
return new_state;
} else {
var G__38815 = (function (){var G__38816 = (puerto_rico.game.rules.execute_role.cljs$core$IFn$_invoke$arity$3 ? puerto_rico.game.rules.execute_role.cljs$core$IFn$_invoke$arity$3(new_state,role,player_id) : puerto_rico.game.rules.execute_role.call(null,new_state,role,player_id));
return (puerto_rico.game.rules.end_role_execution.cljs$core$IFn$_invoke$arity$1 ? puerto_rico.game.rules.end_role_execution.cljs$core$IFn$_invoke$arity$1(G__38816) : puerto_rico.game.rules.end_role_execution.call(null,G__38816));
})();
return (puerto_rico.game.rules.end_round.cljs$core$IFn$_invoke$arity$1 ? puerto_rico.game.rules.end_round.cljs$core$IFn$_invoke$arity$1(G__38815) : puerto_rico.game.rules.end_round.call(null,G__38815));
}

}
} else {
return game_state;
}
});
puerto_rico.game.rules.execute_role = (function puerto_rico$game$rules$execute_role(var_args){
var args__5755__auto__ = [];
var len__5749__auto___38842 = arguments.length;
var i__5750__auto___38843 = (0);
while(true){
if((i__5750__auto___38843 < len__5749__auto___38842)){
args__5755__auto__.push((arguments[i__5750__auto___38843]));

var G__38844 = (i__5750__auto___38843 + (1));
i__5750__auto___38843 = G__38844;
continue;
} else {
}
break;
}

var argseq__5756__auto__ = ((((3) < args__5755__auto__.length))?(new cljs.core.IndexedSeq(args__5755__auto__.slice((3)),(0),null)):null);
return puerto_rico.game.rules.execute_role.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__5756__auto__);
});

(puerto_rico.game.rules.execute_role.cljs$core$IFn$_invoke$arity$variadic = (function (game_state,role,player_id,args){

var G__38821 = role;
var G__38821__$1 = (((G__38821 instanceof cljs.core.Keyword))?G__38821.fqn:null);
switch (G__38821__$1) {
case "settler":
return puerto_rico.game.rules.execute_settler(game_state,player_id,cljs.core.first(args));

break;
case "mayor":
return puerto_rico.game.rules.execute_mayor(game_state);

break;
case "builder":
return puerto_rico.game.rules.execute_builder(game_state,player_id,cljs.core.first(args));

break;
case "craftsman":
return puerto_rico.game.rules.execute_craftsman(game_state);

break;
case "trader":
return puerto_rico.game.rules.execute_trader(game_state,player_id,cljs.core.first(args));

break;
case "captain":
return puerto_rico.game.rules.execute_captain(game_state,player_id,cljs.core.first(args));

break;
case "prospector":
return cljs.core.update_in.cljs$core$IFn$_invoke$arity$3(game_state,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"players","players",-1361554569),new cljs.core.Keyword(null,"role-player-idx","role-player-idx",1417379264).cljs$core$IFn$_invoke$arity$1(game_state),new cljs.core.Keyword(null,"money","money",250333921)], null),cljs.core.inc);

break;
default:
return game_state;

}
}));

(puerto_rico.game.rules.execute_role.cljs$lang$maxFixedArity = (3));

/** @this {Function} */
(puerto_rico.game.rules.execute_role.cljs$lang$applyTo = (function (seq38817){
var G__38818 = cljs.core.first(seq38817);
var seq38817__$1 = cljs.core.next(seq38817);
var G__38819 = cljs.core.first(seq38817__$1);
var seq38817__$2 = cljs.core.next(seq38817__$1);
var G__38820 = cljs.core.first(seq38817__$2);
var seq38817__$3 = cljs.core.next(seq38817__$2);
var self__5734__auto__ = this;
return self__5734__auto__.cljs$core$IFn$_invoke$arity$variadic(G__38818,G__38819,G__38820,seq38817__$3);
}));

puerto_rico.game.rules.end_role_execution = (function puerto_rico$game$rules$end_role_execution(game_state){

return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(game_state,new cljs.core.Keyword(null,"selected-role","selected-role",8663708),null),new cljs.core.Keyword(null,"role-player-idx","role-player-idx",1417379264),null),new cljs.core.Keyword(null,"phase","phase",575722892),new cljs.core.Keyword(null,"role-selection","role-selection",991461347));
});
puerto_rico.game.rules.end_round = (function puerto_rico$game$rules$end_round(game_state){

var all_roles_used_QMARK_ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.count(new cljs.core.Keyword(null,"used-roles","used-roles",1915499651).cljs$core$IFn$_invoke$arity$1(game_state)),cljs.core.count(puerto_rico.game.state.roles));
if(all_roles_used_QMARK_){
return cljs.core.update.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.update.cljs$core$IFn$_invoke$arity$3(game_state,new cljs.core.Keyword(null,"round","round",2009433328),cljs.core.inc),new cljs.core.Keyword(null,"available-roles","available-roles",-1628893019),cljs.core.set(puerto_rico.game.state.roles)),new cljs.core.Keyword(null,"used-roles","used-roles",1915499651),cljs.core.PersistentHashSet.EMPTY),new cljs.core.Keyword(null,"current-player-idx","current-player-idx",-1334769522),(0)),new cljs.core.Keyword(null,"phase","phase",575722892),new cljs.core.Keyword(null,"role-selection","role-selection",991461347)),new cljs.core.Keyword(null,"trading-house","trading-house",1920769251),cljs.core.PersistentVector.EMPTY),new cljs.core.Keyword(null,"ships","ships",-875113158),(function (ships){
return cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (ship){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"amount","amount",364489504).cljs$core$IFn$_invoke$arity$1(ship),new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship))){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(ship,new cljs.core.Keyword(null,"good","good",511701169),null,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"amount","amount",364489504),(0)], 0));
} else {
return ship;
}
}),ships);
}));
} else {
return puerto_rico.game.state.advance_to_next_player(game_state);
}
});
puerto_rico.game.rules.valid_move_QMARK_ = (function puerto_rico$game$rules$valid_move_QMARK_(game_state,player_id,move){

var G__38822 = new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(move);
var G__38822__$1 = (((G__38822 instanceof cljs.core.Keyword))?G__38822.fqn:null);
switch (G__38822__$1) {
case "select-role":
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"phase","phase",575722892).cljs$core$IFn$_invoke$arity$1(game_state),new cljs.core.Keyword(null,"role-selection","role-selection",991461347))) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(puerto_rico.game.state.current_player(game_state)),player_id)) && (cljs.core.contains_QMARK_(new cljs.core.Keyword(null,"available-roles","available-roles",-1628893019).cljs$core$IFn$_invoke$arity$1(game_state),new cljs.core.Keyword(null,"role","role",-736691072).cljs$core$IFn$_invoke$arity$1(move))))));

break;
case "role-action":
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"phase","phase",575722892).cljs$core$IFn$_invoke$arity$1(game_state),new cljs.core.Keyword(null,"role-execution","role-execution",-1842623549))) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"selected-role","selected-role",8663708).cljs$core$IFn$_invoke$arity$1(game_state),new cljs.core.Keyword(null,"role","role",-736691072).cljs$core$IFn$_invoke$arity$1(move))));

break;
default:
return false;

}
});
puerto_rico.game.rules.apply_move = (function puerto_rico$game$rules$apply_move(game_state,move){

var G__38823 = new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(move);
var G__38823__$1 = (((G__38823 instanceof cljs.core.Keyword))?G__38823.fqn:null);
switch (G__38823__$1) {
case "select-role":
return puerto_rico.game.rules.select_role(game_state,new cljs.core.Keyword(null,"player-id","player-id",1003896428).cljs$core$IFn$_invoke$arity$1(move),new cljs.core.Keyword(null,"role","role",-736691072).cljs$core$IFn$_invoke$arity$1(move));

break;
case "role-action":
return puerto_rico.game.rules.end_round(puerto_rico.game.rules.end_role_execution(puerto_rico.game.rules.execute_role.cljs$core$IFn$_invoke$arity$variadic(game_state,new cljs.core.Keyword(null,"role","role",-736691072).cljs$core$IFn$_invoke$arity$1(move),new cljs.core.Keyword(null,"player-id","player-id",1003896428).cljs$core$IFn$_invoke$arity$1(move),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"args","args",1315556576).cljs$core$IFn$_invoke$arity$1(move)], 0))));

break;
default:
return game_state;

}
});

//# sourceMappingURL=puerto_rico.game.rules.js.map

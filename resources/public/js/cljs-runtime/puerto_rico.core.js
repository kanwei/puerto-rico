goog.provide('puerto_rico.core');
if((typeof puerto_rico !== 'undefined') && (typeof puerto_rico.core !== 'undefined') && (typeof puerto_rico.core.game_state !== 'undefined')){
} else {
puerto_rico.core.game_state = reagent.core.atom.cljs$core$IFn$_invoke$arity$1(new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"game-state","game-state",935682735),null,new cljs.core.Keyword(null,"game-id","game-id",385578016),null,new cljs.core.Keyword(null,"loading","loading",-737050189),false,new cljs.core.Keyword(null,"error","error",-978969032),null], null));
}
puerto_rico.core.create_new_game = (function puerto_rico$core$create_new_game(){
var players = new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.game.state.new_player((1),"Alice (Human)"),puerto_rico.game.state.new_player((2),"Bob (AI)"),puerto_rico.game.state.new_player((3),"Carol (AI)")], null);
return puerto_rico.game.state.new_game_state(players);
});
puerto_rico.core.current_player = (function puerto_rico$core$current_player(game_data){
return puerto_rico.game.state.current_player(game_data);
});
puerto_rico.core.handle_role_selection = (function puerto_rico$core$handle_role_selection(role){
var current_game = new cljs.core.Keyword(null,"game-state","game-state",935682735).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(puerto_rico.core.game_state));
var current_player_data = puerto_rico.core.current_player(current_game);
var player_id = new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(current_player_data);
if(cljs.core.truth_(current_game)){
var new_game_state = puerto_rico.game.rules.select_role(current_game,player_id,role);
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(puerto_rico.core.game_state,cljs.core.assoc,new cljs.core.Keyword(null,"game-state","game-state",935682735),new_game_state);

return console.log("Role selected:",role,"New game state:",new_game_state);
} else {
return null;
}
});
puerto_rico.core.role_card = (function puerto_rico$core$role_card(role,available_QMARK_,on_select){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.role-card","div.role-card",1191678600),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"class","class",-2030961996),(cljs.core.truth_(available_QMARK_)?null:"disabled"),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(cljs.core.truth_(available_QMARK_)?(function (){
return (on_select.cljs$core$IFn$_invoke$arity$1 ? on_select.cljs$core$IFn$_invoke$arity$1(role) : on_select.call(null,role));
}):null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),cljs.core.name(role)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),(function (){var G__40179 = role;
var G__40179__$1 = (((G__40179 instanceof cljs.core.Keyword))?G__40179.fqn:null);
switch (G__40179__$1) {
case "settler":
return "Take a plantation";

break;
case "mayor":
return "Get colonists";

break;
case "builder":
return "Build buildings";

break;
case "craftsman":
return "Produce goods";

break;
case "trader":
return "Sell to trading house";

break;
case "captain":
return "Ship goods for VP";

break;
case "prospector":
return "Get money";

break;
default:
return "Choose this role";

}
})()], null)], null);
});
puerto_rico.core.player_board = (function puerto_rico$core$player_board(player,current_QMARK_){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.player-board","div.player-board",230119522),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),(cljs.core.truth_(current_QMARK_)?"current-player":null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(player)),(cljs.core.truth_(current_QMARK_)?" \u2B50":null)].join('')], null),new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.player-stats","div.player-stats",-370156407),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83D\uDCB0 Money: $",new cljs.core.Keyword(null,"money","money",250333921).cljs$core$IFn$_invoke$arity$1(player)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83C\uDFC6 Victory Points: ",new cljs.core.Keyword(null,"victory-points","victory-points",-2106714317).cljs$core$IFn$_invoke$arity$1(player)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.goods","div.goods",-2123892496),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"\uD83D\uDCE6 Goods:"], null),((cljs.core.seq(new cljs.core.Keyword(null,"goods","goods",702040840).cljs$core$IFn$_invoke$arity$1(player)))?(function (){var iter__5503__auto__ = (function puerto_rico$core$player_board_$_iter__40180(s__40181){
return (new cljs.core.LazySeq(null,(function (){
var s__40181__$1 = s__40181;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40181__$1);
if(temp__5825__auto__){
var s__40181__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40181__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40181__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40183 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40182 = (0);
while(true){
if((i__40182 < size__5502__auto__)){
var vec__40184 = cljs.core._nth(c__5501__auto__,i__40182);
var good = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40184,(0),null);
var amount = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40184,(1),null);
cljs.core.chunk_append(b__40183,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good","span.good",1042856380),[cljs.core.name(good),": ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(amount)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)));

var G__40257 = (i__40182 + (1));
i__40182 = G__40257;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40183),puerto_rico$core$player_board_$_iter__40180(cljs.core.chunk_rest(s__40181__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40183),null);
}
} else {
var vec__40187 = cljs.core.first(s__40181__$2);
var good = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40187,(0),null);
var amount = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40187,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good","span.good",1042856380),[cljs.core.name(good),": ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(amount)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)),puerto_rico$core$player_board_$_iter__40180(cljs.core.rest(s__40181__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"goods","goods",702040840).cljs$core$IFn$_invoke$arity$1(player));
})():new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.empty","span.empty",157805619),"None"], null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.buildings","div.buildings",-859161456),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"\uD83C\uDFE2 Buildings:"], null),((cljs.core.seq(new cljs.core.Keyword(null,"buildings","buildings",-308691065).cljs$core$IFn$_invoke$arity$1(player)))?(function (){var iter__5503__auto__ = (function puerto_rico$core$player_board_$_iter__40190(s__40191){
return (new cljs.core.LazySeq(null,(function (){
var s__40191__$1 = s__40191;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40191__$1);
if(temp__5825__auto__){
var s__40191__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40191__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40191__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40193 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40192 = (0);
while(true){
if((i__40192 < size__5502__auto__)){
var building = cljs.core._nth(c__5501__auto__,i__40192);
cljs.core.chunk_append(b__40193,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building","span.building",1472209381),[cljs.core.name(building)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),building], null)));

var G__40258 = (i__40192 + (1));
i__40192 = G__40258;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40193),puerto_rico$core$player_board_$_iter__40190(cljs.core.chunk_rest(s__40191__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40193),null);
}
} else {
var building = cljs.core.first(s__40191__$2);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building","span.building",1472209381),[cljs.core.name(building)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),building], null)),puerto_rico$core$player_board_$_iter__40190(cljs.core.rest(s__40191__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"buildings","buildings",-308691065).cljs$core$IFn$_invoke$arity$1(player));
})():new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.empty","span.empty",157805619),"None"], null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.plantations","div.plantations",1884257203),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"\uD83C\uDF31 Plantations:"], null),((cljs.core.seq(new cljs.core.Keyword(null,"plantations","plantations",1490777018).cljs$core$IFn$_invoke$arity$1(player)))?(function (){var iter__5503__auto__ = (function puerto_rico$core$player_board_$_iter__40194(s__40195){
return (new cljs.core.LazySeq(null,(function (){
var s__40195__$1 = s__40195;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40195__$1);
if(temp__5825__auto__){
var s__40195__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40195__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40195__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40197 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40196 = (0);
while(true){
if((i__40196 < size__5502__auto__)){
var plantation = cljs.core._nth(c__5501__auto__,i__40196);
cljs.core.chunk_append(b__40197,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.plantation","span.plantation",553547986),[cljs.core.name(plantation)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),plantation], null)));

var G__40259 = (i__40196 + (1));
i__40196 = G__40259;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40197),puerto_rico$core$player_board_$_iter__40194(cljs.core.chunk_rest(s__40195__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40197),null);
}
} else {
var plantation = cljs.core.first(s__40195__$2);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.plantation","span.plantation",553547986),[cljs.core.name(plantation)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),plantation], null)),puerto_rico$core$player_board_$_iter__40194(cljs.core.rest(s__40195__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"plantations","plantations",1490777018).cljs$core$IFn$_invoke$arity$1(player));
})():new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.empty","span.empty",157805619),"None"], null))], null)], null)], null);
});
puerto_rico.core.common_area = (function puerto_rico$core$common_area(game_data){
return new cljs.core.PersistentVector(null, 9, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.common-area","div.common-area",1892298722),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h2","h2",-372662728),"\uD83C\uDFE2 Common Area"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.supply-section","div.supply-section",1191876844),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"\uD83C\uDFC6 Victory Points Supply"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"Remaining: ",new cljs.core.Keyword(null,"victory-point-supply","victory-point-supply",-1793899896).cljs$core$IFn$_invoke$arity$1(game_data)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.supply-section","div.supply-section",1191876844),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"\uD83D\uDC65 Colonist Supply"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"Remaining: ",new cljs.core.Keyword(null,"colonist-supply","colonist-supply",666169845).cljs$core$IFn$_invoke$arity$1(game_data)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.supply-section","div.supply-section",1191876844),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"\uD83C\uDF31 Plantation Tiles"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.plantation-tiles","div.plantation-tiles",-2107384310),(function (){var iter__5503__auto__ = (function puerto_rico$core$common_area_$_iter__40198(s__40199){
return (new cljs.core.LazySeq(null,(function (){
var s__40199__$1 = s__40199;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40199__$1);
if(temp__5825__auto__){
var s__40199__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40199__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40199__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40201 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40200 = (0);
while(true){
if((i__40200 < size__5502__auto__)){
var vec__40202 = cljs.core._nth(c__5501__auto__,i__40200);
var tile_type = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40202,(0),null);
var count = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40202,(1),null);
cljs.core.chunk_append(b__40201,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.tile-count","div.tile-count",-536505598),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.tile-name","span.tile-name",-188657658),cljs.core.name(tile_type)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.tile-amount","span.tile-amount",-1818412155),": ",count], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),tile_type], null)));

var G__40260 = (i__40200 + (1));
i__40200 = G__40260;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40201),puerto_rico$core$common_area_$_iter__40198(cljs.core.chunk_rest(s__40199__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40201),null);
}
} else {
var vec__40205 = cljs.core.first(s__40199__$2);
var tile_type = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40205,(0),null);
var count = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40205,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.tile-count","div.tile-count",-536505598),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.tile-name","span.tile-name",-188657658),cljs.core.name(tile_type)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.tile-amount","span.tile-amount",-1818412155),": ",count], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),tile_type], null)),puerto_rico$core$common_area_$_iter__40198(cljs.core.rest(s__40199__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"plantation-supply","plantation-supply",-1569976839).cljs$core$IFn$_invoke$arity$1(game_data));
})()], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.supply-section","div.supply-section",1191876844),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"\uD83D\uDCE6 Goods Supply"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.goods-supply","div.goods-supply",-1941185744),(function (){var iter__5503__auto__ = (function puerto_rico$core$common_area_$_iter__40208(s__40209){
return (new cljs.core.LazySeq(null,(function (){
var s__40209__$1 = s__40209;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40209__$1);
if(temp__5825__auto__){
var s__40209__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40209__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40209__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40211 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40210 = (0);
while(true){
if((i__40210 < size__5502__auto__)){
var vec__40212 = cljs.core._nth(c__5501__auto__,i__40210);
var good = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40212,(0),null);
var count = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40212,(1),null);
cljs.core.chunk_append(b__40211,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.good-count","div.good-count",1119939393),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good-name","span.good-name",-708895267),cljs.core.name(good)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good-amount","span.good-amount",-723870465),": ",count], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)));

var G__40261 = (i__40210 + (1));
i__40210 = G__40261;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40211),puerto_rico$core$common_area_$_iter__40208(cljs.core.chunk_rest(s__40209__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40211),null);
}
} else {
var vec__40215 = cljs.core.first(s__40209__$2);
var good = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40215,(0),null);
var count = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40215,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.good-count","div.good-count",1119939393),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good-name","span.good-name",-708895267),cljs.core.name(good)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good-amount","span.good-amount",-723870465),": ",count], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)),puerto_rico$core$common_area_$_iter__40208(cljs.core.rest(s__40209__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"goods-supply","goods-supply",1480279979).cljs$core$IFn$_invoke$arity$1(game_data));
})()], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.supply-section","div.supply-section",1191876844),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"\uD83C\uDFD7\uFE0F Building Supply"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.building-supply","div.building-supply",-1395847675),(function (){var iter__5503__auto__ = (function puerto_rico$core$common_area_$_iter__40218(s__40219){
return (new cljs.core.LazySeq(null,(function (){
var s__40219__$1 = s__40219;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40219__$1);
if(temp__5825__auto__){
var s__40219__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40219__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40219__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40221 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40220 = (0);
while(true){
if((i__40220 < size__5502__auto__)){
var vec__40222 = cljs.core._nth(c__5501__auto__,i__40220);
var building = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40222,(0),null);
var count = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40222,(1),null);
cljs.core.chunk_append(b__40221,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.building-count","div.building-count",1950256063),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building-name","span.building-name",-1442642621),cljs.core.name(building)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building-amount","span.building-amount",-1225899117),": ",count], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),building], null)));

var G__40262 = (i__40220 + (1));
i__40220 = G__40262;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40221),puerto_rico$core$common_area_$_iter__40218(cljs.core.chunk_rest(s__40219__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40221),null);
}
} else {
var vec__40225 = cljs.core.first(s__40219__$2);
var building = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40225,(0),null);
var count = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40225,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.building-count","div.building-count",1950256063),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building-name","span.building-name",-1442642621),cljs.core.name(building)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building-amount","span.building-amount",-1225899117),": ",count], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),building], null)),puerto_rico$core$common_area_$_iter__40218(cljs.core.rest(s__40219__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(cljs.core.sort_by.cljs$core$IFn$_invoke$arity$2(cljs.core.first,new cljs.core.Keyword(null,"building-supply","building-supply",1258258350).cljs$core$IFn$_invoke$arity$1(game_data)));
})()], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.supply-section","div.supply-section",1191876844),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"\uD83C\uDFEA Trading House"], null),((cljs.core.seq(new cljs.core.Keyword(null,"trading-house","trading-house",1920769251).cljs$core$IFn$_invoke$arity$1(game_data)))?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.trading-house","div.trading-house",1219074577),(function (){var iter__5503__auto__ = (function puerto_rico$core$common_area_$_iter__40228(s__40229){
return (new cljs.core.LazySeq(null,(function (){
var s__40229__$1 = s__40229;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40229__$1);
if(temp__5825__auto__){
var s__40229__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40229__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40229__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40231 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40230 = (0);
while(true){
if((i__40230 < size__5502__auto__)){
var good = cljs.core._nth(c__5501__auto__,i__40230);
cljs.core.chunk_append(b__40231,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.traded-good","span.traded-good",483794539),cljs.core.name(good)," "], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)));

var G__40263 = (i__40230 + (1));
i__40230 = G__40263;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40231),puerto_rico$core$common_area_$_iter__40228(cljs.core.chunk_rest(s__40229__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40231),null);
}
} else {
var good = cljs.core.first(s__40229__$2);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.traded-good","span.traded-good",483794539),cljs.core.name(good)," "], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)),puerto_rico$core$common_area_$_iter__40228(cljs.core.rest(s__40229__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"trading-house","trading-house",1920769251).cljs$core$IFn$_invoke$arity$1(game_data));
})()], null):new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p.empty","p.empty",-231343869),"Empty"], null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.supply-section","div.supply-section",1191876844),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"\uD83D\uDEA2 Ships"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.ships","div.ships",599278968),(function (){var iter__5503__auto__ = (function puerto_rico$core$common_area_$_iter__40232(s__40233){
return (new cljs.core.LazySeq(null,(function (){
var s__40233__$1 = s__40233;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40233__$1);
if(temp__5825__auto__){
var s__40233__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40233__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40233__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40235 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40234 = (0);
while(true){
if((i__40234 < size__5502__auto__)){
var vec__40236 = cljs.core._nth(c__5501__auto__,i__40234);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40236,(0),null);
var ship = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40236,(1),null);
cljs.core.chunk_append(b__40235,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.ship","div.ship",1803811433),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.ship-info","span.ship-info",-1974926751),"Ship ",(idx + (1)),": ",(cljs.core.truth_(new cljs.core.Keyword(null,"good","good",511701169).cljs$core$IFn$_invoke$arity$1(ship))?[cljs.core.name(new cljs.core.Keyword(null,"good","good",511701169).cljs$core$IFn$_invoke$arity$1(ship))," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"amount","amount",364489504).cljs$core$IFn$_invoke$arity$1(ship)),"/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship))].join(''):["Empty (Capacity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship)),")"].join(''))], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),idx], null)));

var G__40264 = (i__40234 + (1));
i__40234 = G__40264;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40235),puerto_rico$core$common_area_$_iter__40232(cljs.core.chunk_rest(s__40233__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40235),null);
}
} else {
var vec__40239 = cljs.core.first(s__40233__$2);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40239,(0),null);
var ship = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40239,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.ship","div.ship",1803811433),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.ship-info","span.ship-info",-1974926751),"Ship ",(idx + (1)),": ",(cljs.core.truth_(new cljs.core.Keyword(null,"good","good",511701169).cljs$core$IFn$_invoke$arity$1(ship))?[cljs.core.name(new cljs.core.Keyword(null,"good","good",511701169).cljs$core$IFn$_invoke$arity$1(ship))," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"amount","amount",364489504).cljs$core$IFn$_invoke$arity$1(ship)),"/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship))].join(''):["Empty (Capacity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"capacity","capacity",72689734).cljs$core$IFn$_invoke$arity$1(ship)),")"].join(''))], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),idx], null)),puerto_rico$core$common_area_$_iter__40232(cljs.core.rest(s__40233__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"ships","ships",-875113158).cljs$core$IFn$_invoke$arity$1(game_data)));
})()], null)], null)], null);
});
puerto_rico.core.game_board = (function puerto_rico$core$game_board(){
var game_data = new cljs.core.Keyword(null,"game-state","game-state",935682735).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(puerto_rico.core.game_state));
if(cljs.core.truth_(game_data)){
var current_player_data = puerto_rico.core.current_player(game_data);
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.game-board","div.game-board",1833032215),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h1","h1",-1896887462),"\uD83C\uDFDD\uFE0F Puerto Rico"], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.game-info","div.game-info",-605893819),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83D\uDCC5 Round: ",new cljs.core.Keyword(null,"round","round",2009433328).cljs$core$IFn$_invoke$arity$1(game_data)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\u26A1 Phase: ",cljs.core.name(new cljs.core.Keyword(null,"phase","phase",575722892).cljs$core$IFn$_invoke$arity$1(game_data))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83D\uDC64 Current Player: ",new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(current_player_data)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.main-content","div.main-content",-2054693948),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.left-column","div.left-column",-193214983),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.roles-section","div.roles-section",-1109666721),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h2","h2",-372662728),"\uD83C\uDFAD Available Roles"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.roles-grid","div.roles-grid",1438990017),(function (){var iter__5503__auto__ = (function puerto_rico$core$game_board_$_iter__40242(s__40243){
return (new cljs.core.LazySeq(null,(function (){
var s__40243__$1 = s__40243;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40243__$1);
if(temp__5825__auto__){
var s__40243__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40243__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40243__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40245 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40244 = (0);
while(true){
if((i__40244 < size__5502__auto__)){
var role = cljs.core._nth(c__5501__auto__,i__40244);
cljs.core.chunk_append(b__40245,cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.role_card,role,true,puerto_rico.core.handle_role_selection], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),role], null)));

var G__40265 = (i__40244 + (1));
i__40244 = G__40265;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40245),puerto_rico$core$game_board_$_iter__40242(cljs.core.chunk_rest(s__40243__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40245),null);
}
} else {
var role = cljs.core.first(s__40243__$2);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.role_card,role,true,puerto_rico.core.handle_role_selection], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),role], null)),puerto_rico$core$game_board_$_iter__40242(cljs.core.rest(s__40243__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"available-roles","available-roles",-1628893019).cljs$core$IFn$_invoke$arity$1(game_data));
})()], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.common_area,game_data], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.right-column","div.right-column",1851404035),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.players-section","div.players-section",2084695001),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h2","h2",-372662728),"\uD83D\uDC65 Players"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.players-grid","div.players-grid",121475030),(function (){var iter__5503__auto__ = (function puerto_rico$core$game_board_$_iter__40246(s__40247){
return (new cljs.core.LazySeq(null,(function (){
var s__40247__$1 = s__40247;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__40247__$1);
if(temp__5825__auto__){
var s__40247__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__40247__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__40247__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__40249 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__40248 = (0);
while(true){
if((i__40248 < size__5502__auto__)){
var vec__40250 = cljs.core._nth(c__5501__auto__,i__40248);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40250,(0),null);
var player = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40250,(1),null);
cljs.core.chunk_append(b__40249,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.player_board,player,cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(idx,new cljs.core.Keyword(null,"current-player-idx","current-player-idx",-1334769522).cljs$core$IFn$_invoke$arity$1(game_data))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(player)], null)));

var G__40266 = (i__40248 + (1));
i__40248 = G__40266;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__40249),puerto_rico$core$game_board_$_iter__40246(cljs.core.chunk_rest(s__40247__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__40249),null);
}
} else {
var vec__40253 = cljs.core.first(s__40247__$2);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40253,(0),null);
var player = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__40253,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.player_board,player,cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(idx,new cljs.core.Keyword(null,"current-player-idx","current-player-idx",-1334769522).cljs$core$IFn$_invoke$arity$1(game_data))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(player)], null)),puerto_rico$core$game_board_$_iter__40246(cljs.core.rest(s__40247__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_data)));
})()], null)], null)], null)], null)], null);
} else {
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.no-game","div.no-game",869928775),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h1","h1",-1896887462),"\uD83C\uDFDD\uFE0F Puerto Rico"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"Welcome to the Puerto Rico board game!"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"button","button",1456579943),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(puerto_rico.core.game_state,cljs.core.assoc,new cljs.core.Keyword(null,"game-state","game-state",935682735),puerto_rico.core.create_new_game());
})], null),"\uD83C\uDFAE Start New Game"], null)], null);
}
});
puerto_rico.core.main_panel = (function puerto_rico$core$main_panel(){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.game_board], null);
});
/**
 * Initialize the application
 */
puerto_rico.core.init = (function puerto_rico$core$init(){
console.log("Initializing Puerto Rico application...");

return reagent.dom.render.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.main_panel], null),document.getElementById("app"));
});
/**
 * Test function to verify Shadow-CLJS nREPL is working
 */
puerto_rico.core.test_nrepl_connection = (function puerto_rico$core$test_nrepl_connection(){
alert("\uD83C\uDF89 Shadow-CLJS nREPL Connection Test Successful! \uD83C\uDF89");

console.log("Shadow-CLJS nREPL is connected and working properly");

console.log("Current timestamp:",(new Date()));

return new cljs.core.Keyword(null,"success","success",1890645906);
});
puerto_rico.core.init();

//# sourceMappingURL=puerto_rico.core.js.map

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
}):null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),cljs.core.name(role)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),(function (){var G__39261 = role;
var G__39261__$1 = (((G__39261 instanceof cljs.core.Keyword))?G__39261.fqn:null);
switch (G__39261__$1) {
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
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.player-board","div.player-board",230119522),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),(cljs.core.truth_(current_QMARK_)?"current-player":null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(player)),(cljs.core.truth_(current_QMARK_)?" \u2B50":null)].join('')], null),new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.player-stats","div.player-stats",-370156407),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83D\uDCB0 Money: $",new cljs.core.Keyword(null,"money","money",250333921).cljs$core$IFn$_invoke$arity$1(player)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83C\uDFC6 Victory Points: ",new cljs.core.Keyword(null,"victory-points","victory-points",-2106714317).cljs$core$IFn$_invoke$arity$1(player)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.goods","div.goods",-2123892496),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"\uD83D\uDCE6 Goods:"], null),((cljs.core.seq(new cljs.core.Keyword(null,"goods","goods",702040840).cljs$core$IFn$_invoke$arity$1(player)))?(function (){var iter__5503__auto__ = (function puerto_rico$core$player_board_$_iter__39262(s__39263){
return (new cljs.core.LazySeq(null,(function (){
var s__39263__$1 = s__39263;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__39263__$1);
if(temp__5825__auto__){
var s__39263__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__39263__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__39263__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__39265 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__39264 = (0);
while(true){
if((i__39264 < size__5502__auto__)){
var vec__39266 = cljs.core._nth(c__5501__auto__,i__39264);
var good = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39266,(0),null);
var amount = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39266,(1),null);
cljs.core.chunk_append(b__39265,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good","span.good",1042856380),[cljs.core.name(good),": ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(amount)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)));

var G__39295 = (i__39264 + (1));
i__39264 = G__39295;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__39265),puerto_rico$core$player_board_$_iter__39262(cljs.core.chunk_rest(s__39263__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__39265),null);
}
} else {
var vec__39269 = cljs.core.first(s__39263__$2);
var good = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39269,(0),null);
var amount = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39269,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.good","span.good",1042856380),[cljs.core.name(good),": ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(amount)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),good], null)),puerto_rico$core$player_board_$_iter__39262(cljs.core.rest(s__39263__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"goods","goods",702040840).cljs$core$IFn$_invoke$arity$1(player));
})():new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.empty","span.empty",157805619),"None"], null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.buildings","div.buildings",-859161456),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"\uD83C\uDFE2 Buildings:"], null),((cljs.core.seq(new cljs.core.Keyword(null,"buildings","buildings",-308691065).cljs$core$IFn$_invoke$arity$1(player)))?(function (){var iter__5503__auto__ = (function puerto_rico$core$player_board_$_iter__39272(s__39273){
return (new cljs.core.LazySeq(null,(function (){
var s__39273__$1 = s__39273;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__39273__$1);
if(temp__5825__auto__){
var s__39273__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__39273__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__39273__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__39275 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__39274 = (0);
while(true){
if((i__39274 < size__5502__auto__)){
var building = cljs.core._nth(c__5501__auto__,i__39274);
cljs.core.chunk_append(b__39275,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building","span.building",1472209381),[cljs.core.name(building)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),building], null)));

var G__39296 = (i__39274 + (1));
i__39274 = G__39296;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__39275),puerto_rico$core$player_board_$_iter__39272(cljs.core.chunk_rest(s__39273__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__39275),null);
}
} else {
var building = cljs.core.first(s__39273__$2);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.building","span.building",1472209381),[cljs.core.name(building)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),building], null)),puerto_rico$core$player_board_$_iter__39272(cljs.core.rest(s__39273__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"buildings","buildings",-308691065).cljs$core$IFn$_invoke$arity$1(player));
})():new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.empty","span.empty",157805619),"None"], null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.plantations","div.plantations",1884257203),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"\uD83C\uDF31 Plantations:"], null),((cljs.core.seq(new cljs.core.Keyword(null,"plantations","plantations",1490777018).cljs$core$IFn$_invoke$arity$1(player)))?(function (){var iter__5503__auto__ = (function puerto_rico$core$player_board_$_iter__39276(s__39277){
return (new cljs.core.LazySeq(null,(function (){
var s__39277__$1 = s__39277;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__39277__$1);
if(temp__5825__auto__){
var s__39277__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__39277__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__39277__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__39279 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__39278 = (0);
while(true){
if((i__39278 < size__5502__auto__)){
var plantation = cljs.core._nth(c__5501__auto__,i__39278);
cljs.core.chunk_append(b__39279,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.plantation","span.plantation",553547986),[cljs.core.name(plantation)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),plantation], null)));

var G__39297 = (i__39278 + (1));
i__39278 = G__39297;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__39279),puerto_rico$core$player_board_$_iter__39276(cljs.core.chunk_rest(s__39277__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__39279),null);
}
} else {
var plantation = cljs.core.first(s__39277__$2);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.plantation","span.plantation",553547986),[cljs.core.name(plantation)," "].join('')], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),plantation], null)),puerto_rico$core$player_board_$_iter__39276(cljs.core.rest(s__39277__$2)));
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
puerto_rico.core.game_board = (function puerto_rico$core$game_board(){
var game_data = new cljs.core.Keyword(null,"game-state","game-state",935682735).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(puerto_rico.core.game_state));
if(cljs.core.truth_(game_data)){
var current_player_data = puerto_rico.core.current_player(game_data);
return new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.game-board","div.game-board",1833032215),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h1","h1",-1896887462),"\uD83C\uDFDD\uFE0F Puerto Rico"], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.game-info","div.game-info",-605893819),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83D\uDCC5 Round: ",new cljs.core.Keyword(null,"round","round",2009433328).cljs$core$IFn$_invoke$arity$1(game_data)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\u26A1 Phase: ",cljs.core.name(new cljs.core.Keyword(null,"phase","phase",575722892).cljs$core$IFn$_invoke$arity$1(game_data))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"\uD83D\uDC64 Current Player: ",new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(current_player_data)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.roles-section","div.roles-section",-1109666721),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h2","h2",-372662728),"\uD83C\uDFAD Available Roles"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.roles-grid","div.roles-grid",1438990017),(function (){var iter__5503__auto__ = (function puerto_rico$core$game_board_$_iter__39280(s__39281){
return (new cljs.core.LazySeq(null,(function (){
var s__39281__$1 = s__39281;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__39281__$1);
if(temp__5825__auto__){
var s__39281__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__39281__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__39281__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__39283 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__39282 = (0);
while(true){
if((i__39282 < size__5502__auto__)){
var role = cljs.core._nth(c__5501__auto__,i__39282);
cljs.core.chunk_append(b__39283,cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.role_card,role,true,puerto_rico.core.handle_role_selection], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),role], null)));

var G__39298 = (i__39282 + (1));
i__39282 = G__39298;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__39283),puerto_rico$core$game_board_$_iter__39280(cljs.core.chunk_rest(s__39281__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__39283),null);
}
} else {
var role = cljs.core.first(s__39281__$2);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.role_card,role,true,puerto_rico.core.handle_role_selection], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),role], null)),puerto_rico$core$game_board_$_iter__39280(cljs.core.rest(s__39281__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"available-roles","available-roles",-1628893019).cljs$core$IFn$_invoke$arity$1(game_data));
})()], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.players-section","div.players-section",2084695001),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h2","h2",-372662728),"\uD83D\uDC65 Players"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.players-grid","div.players-grid",121475030),(function (){var iter__5503__auto__ = (function puerto_rico$core$game_board_$_iter__39284(s__39285){
return (new cljs.core.LazySeq(null,(function (){
var s__39285__$1 = s__39285;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__39285__$1);
if(temp__5825__auto__){
var s__39285__$2 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(s__39285__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__39285__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__39287 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__39286 = (0);
while(true){
if((i__39286 < size__5502__auto__)){
var vec__39288 = cljs.core._nth(c__5501__auto__,i__39286);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39288,(0),null);
var player = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39288,(1),null);
cljs.core.chunk_append(b__39287,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.player_board,player,cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(idx,new cljs.core.Keyword(null,"current-player-idx","current-player-idx",-1334769522).cljs$core$IFn$_invoke$arity$1(game_data))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(player)], null)));

var G__39299 = (i__39286 + (1));
i__39286 = G__39299;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__39287),puerto_rico$core$game_board_$_iter__39284(cljs.core.chunk_rest(s__39285__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__39287),null);
}
} else {
var vec__39291 = cljs.core.first(s__39285__$2);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39291,(0),null);
var player = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__39291,(1),null);
return cljs.core.cons(cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [puerto_rico.core.player_board,player,cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(idx,new cljs.core.Keyword(null,"current-player-idx","current-player-idx",-1334769522).cljs$core$IFn$_invoke$arity$1(game_data))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(player)], null)),puerto_rico$core$game_board_$_iter__39284(cljs.core.rest(s__39285__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,new cljs.core.Keyword(null,"players","players",-1361554569).cljs$core$IFn$_invoke$arity$1(game_data)));
})()], null)], null)], null);
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

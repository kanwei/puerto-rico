goog.provide('shadow.dom');
shadow.dom.transition_supported_QMARK_ = true;

/**
 * @interface
 */
shadow.dom.IElement = function(){};

var shadow$dom$IElement$_to_dom$dyn_31341 = (function (this$){
var x__5373__auto__ = (((this$ == null))?null:this$);
var m__5374__auto__ = (shadow.dom._to_dom[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5374__auto__.call(null,this$));
} else {
var m__5372__auto__ = (shadow.dom._to_dom["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5372__auto__.call(null,this$));
} else {
throw cljs.core.missing_protocol("IElement.-to-dom",this$);
}
}
});
shadow.dom._to_dom = (function shadow$dom$_to_dom(this$){
if((((!((this$ == null)))) && ((!((this$.shadow$dom$IElement$_to_dom$arity$1 == null)))))){
return this$.shadow$dom$IElement$_to_dom$arity$1(this$);
} else {
return shadow$dom$IElement$_to_dom$dyn_31341(this$);
}
});


/**
 * @interface
 */
shadow.dom.SVGElement = function(){};

var shadow$dom$SVGElement$_to_svg$dyn_31346 = (function (this$){
var x__5373__auto__ = (((this$ == null))?null:this$);
var m__5374__auto__ = (shadow.dom._to_svg[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5374__auto__.call(null,this$));
} else {
var m__5372__auto__ = (shadow.dom._to_svg["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5372__auto__.call(null,this$));
} else {
throw cljs.core.missing_protocol("SVGElement.-to-svg",this$);
}
}
});
shadow.dom._to_svg = (function shadow$dom$_to_svg(this$){
if((((!((this$ == null)))) && ((!((this$.shadow$dom$SVGElement$_to_svg$arity$1 == null)))))){
return this$.shadow$dom$SVGElement$_to_svg$arity$1(this$);
} else {
return shadow$dom$SVGElement$_to_svg$dyn_31346(this$);
}
});

shadow.dom.lazy_native_coll_seq = (function shadow$dom$lazy_native_coll_seq(coll,idx){
if((idx < coll.length)){
return (new cljs.core.LazySeq(null,(function (){
return cljs.core.cons((coll[idx]),(function (){var G__30066 = coll;
var G__30067 = (idx + (1));
return (shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2 ? shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2(G__30066,G__30067) : shadow.dom.lazy_native_coll_seq.call(null,G__30066,G__30067));
})());
}),null,null));
} else {
return null;
}
});

/**
* @constructor
 * @implements {cljs.core.IIndexed}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IDeref}
 * @implements {shadow.dom.IElement}
*/
shadow.dom.NativeColl = (function (coll){
this.coll = coll;
this.cljs$lang$protocol_mask$partition0$ = 8421394;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(shadow.dom.NativeColl.prototype.cljs$core$IDeref$_deref$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return self__.coll;
}));

(shadow.dom.NativeColl.prototype.cljs$core$IIndexed$_nth$arity$2 = (function (this$,n){
var self__ = this;
var this$__$1 = this;
return (self__.coll[n]);
}));

(shadow.dom.NativeColl.prototype.cljs$core$IIndexed$_nth$arity$3 = (function (this$,n,not_found){
var self__ = this;
var this$__$1 = this;
var or__5025__auto__ = (self__.coll[n]);
if(cljs.core.truth_(or__5025__auto__)){
return or__5025__auto__;
} else {
return not_found;
}
}));

(shadow.dom.NativeColl.prototype.cljs$core$ICounted$_count$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return self__.coll.length;
}));

(shadow.dom.NativeColl.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return shadow.dom.lazy_native_coll_seq(self__.coll,(0));
}));

(shadow.dom.NativeColl.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.dom.NativeColl.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return self__.coll;
}));

(shadow.dom.NativeColl.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"coll","coll",-1006698606,null)], null);
}));

(shadow.dom.NativeColl.cljs$lang$type = true);

(shadow.dom.NativeColl.cljs$lang$ctorStr = "shadow.dom/NativeColl");

(shadow.dom.NativeColl.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"shadow.dom/NativeColl");
}));

/**
 * Positional factory function for shadow.dom/NativeColl.
 */
shadow.dom.__GT_NativeColl = (function shadow$dom$__GT_NativeColl(coll){
return (new shadow.dom.NativeColl(coll));
});

shadow.dom.native_coll = (function shadow$dom$native_coll(coll){
return (new shadow.dom.NativeColl(coll));
});
shadow.dom.dom_node = (function shadow$dom$dom_node(el){
if((el == null)){
return null;
} else {
if((((!((el == null))))?((((false) || ((cljs.core.PROTOCOL_SENTINEL === el.shadow$dom$IElement$))))?true:false):false)){
return el.shadow$dom$IElement$_to_dom$arity$1(null);
} else {
if(typeof el === 'string'){
return document.createTextNode(el);
} else {
if(typeof el === 'number'){
return document.createTextNode(cljs.core.str.cljs$core$IFn$_invoke$arity$1(el));
} else {
return el;

}
}
}
}
});
shadow.dom.query_one = (function shadow$dom$query_one(var_args){
var G__30085 = arguments.length;
switch (G__30085) {
case 1:
return shadow.dom.query_one.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.query_one.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.query_one.cljs$core$IFn$_invoke$arity$1 = (function (sel){
return document.querySelector(sel);
}));

(shadow.dom.query_one.cljs$core$IFn$_invoke$arity$2 = (function (sel,root){
return shadow.dom.dom_node(root).querySelector(sel);
}));

(shadow.dom.query_one.cljs$lang$maxFixedArity = 2);

shadow.dom.query = (function shadow$dom$query(var_args){
var G__30089 = arguments.length;
switch (G__30089) {
case 1:
return shadow.dom.query.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.query.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.query.cljs$core$IFn$_invoke$arity$1 = (function (sel){
return (new shadow.dom.NativeColl(document.querySelectorAll(sel)));
}));

(shadow.dom.query.cljs$core$IFn$_invoke$arity$2 = (function (sel,root){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(root).querySelectorAll(sel)));
}));

(shadow.dom.query.cljs$lang$maxFixedArity = 2);

shadow.dom.by_id = (function shadow$dom$by_id(var_args){
var G__30099 = arguments.length;
switch (G__30099) {
case 2:
return shadow.dom.by_id.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 1:
return shadow.dom.by_id.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.by_id.cljs$core$IFn$_invoke$arity$2 = (function (id,el){
return shadow.dom.dom_node(el).getElementById(id);
}));

(shadow.dom.by_id.cljs$core$IFn$_invoke$arity$1 = (function (id){
return document.getElementById(id);
}));

(shadow.dom.by_id.cljs$lang$maxFixedArity = 2);

shadow.dom.build = shadow.dom.dom_node;
shadow.dom.ev_stop = (function shadow$dom$ev_stop(var_args){
var G__30108 = arguments.length;
switch (G__30108) {
case 1:
return shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1 = (function (e){
if(cljs.core.truth_(e.stopPropagation)){
e.stopPropagation();

e.preventDefault();
} else {
(e.cancelBubble = true);

(e.returnValue = false);
}

return e;
}));

(shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$2 = (function (e,el){
shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1(e);

return el;
}));

(shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$4 = (function (e,el,scope,owner){
shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1(e);

return el;
}));

(shadow.dom.ev_stop.cljs$lang$maxFixedArity = 4);

/**
 * check wether a parent node (or the document) contains the child
 */
shadow.dom.contains_QMARK_ = (function shadow$dom$contains_QMARK_(var_args){
var G__30115 = arguments.length;
switch (G__30115) {
case 1:
return shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$1 = (function (el){
return goog.dom.contains(document,shadow.dom.dom_node(el));
}));

(shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$2 = (function (parent,el){
return goog.dom.contains(shadow.dom.dom_node(parent),shadow.dom.dom_node(el));
}));

(shadow.dom.contains_QMARK_.cljs$lang$maxFixedArity = 2);

shadow.dom.add_class = (function shadow$dom$add_class(el,cls){
return goog.dom.classlist.add(shadow.dom.dom_node(el),cls);
});
shadow.dom.remove_class = (function shadow$dom$remove_class(el,cls){
return goog.dom.classlist.remove(shadow.dom.dom_node(el),cls);
});
shadow.dom.toggle_class = (function shadow$dom$toggle_class(var_args){
var G__30121 = arguments.length;
switch (G__30121) {
case 2:
return shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$2 = (function (el,cls){
return goog.dom.classlist.toggle(shadow.dom.dom_node(el),cls);
}));

(shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$3 = (function (el,cls,v){
if(cljs.core.truth_(v)){
return shadow.dom.add_class(el,cls);
} else {
return shadow.dom.remove_class(el,cls);
}
}));

(shadow.dom.toggle_class.cljs$lang$maxFixedArity = 3);

shadow.dom.dom_listen = (cljs.core.truth_((function (){var or__5025__auto__ = (!((typeof document !== 'undefined')));
if(or__5025__auto__){
return or__5025__auto__;
} else {
return document.addEventListener;
}
})())?(function shadow$dom$dom_listen_good(el,ev,handler){
return el.addEventListener(ev,handler,false);
}):(function shadow$dom$dom_listen_ie(el,ev,handler){
try{return el.attachEvent(["on",cljs.core.str.cljs$core$IFn$_invoke$arity$1(ev)].join(''),(function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
}));
}catch (e30132){if((e30132 instanceof Object)){
var e = e30132;
return console.log("didnt support attachEvent",el,e);
} else {
throw e30132;

}
}}));
shadow.dom.dom_listen_remove = (cljs.core.truth_((function (){var or__5025__auto__ = (!((typeof document !== 'undefined')));
if(or__5025__auto__){
return or__5025__auto__;
} else {
return document.removeEventListener;
}
})())?(function shadow$dom$dom_listen_remove_good(el,ev,handler){
return el.removeEventListener(ev,handler,false);
}):(function shadow$dom$dom_listen_remove_ie(el,ev,handler){
return el.detachEvent(["on",cljs.core.str.cljs$core$IFn$_invoke$arity$1(ev)].join(''),handler);
}));
shadow.dom.on_query = (function shadow$dom$on_query(root_el,ev,selector,handler){
var seq__30151 = cljs.core.seq(shadow.dom.query.cljs$core$IFn$_invoke$arity$2(selector,root_el));
var chunk__30153 = null;
var count__30154 = (0);
var i__30155 = (0);
while(true){
if((i__30155 < count__30154)){
var el = chunk__30153.cljs$core$IIndexed$_nth$arity$2(null,i__30155);
var handler_31408__$1 = ((function (seq__30151,chunk__30153,count__30154,i__30155,el){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__30151,chunk__30153,count__30154,i__30155,el))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_31408__$1);


var G__31411 = seq__30151;
var G__31412 = chunk__30153;
var G__31413 = count__30154;
var G__31414 = (i__30155 + (1));
seq__30151 = G__31411;
chunk__30153 = G__31412;
count__30154 = G__31413;
i__30155 = G__31414;
continue;
} else {
var temp__5825__auto__ = cljs.core.seq(seq__30151);
if(temp__5825__auto__){
var seq__30151__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__30151__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__30151__$1);
var G__31417 = cljs.core.chunk_rest(seq__30151__$1);
var G__31418 = c__5548__auto__;
var G__31419 = cljs.core.count(c__5548__auto__);
var G__31420 = (0);
seq__30151 = G__31417;
chunk__30153 = G__31418;
count__30154 = G__31419;
i__30155 = G__31420;
continue;
} else {
var el = cljs.core.first(seq__30151__$1);
var handler_31423__$1 = ((function (seq__30151,chunk__30153,count__30154,i__30155,el,seq__30151__$1,temp__5825__auto__){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__30151,chunk__30153,count__30154,i__30155,el,seq__30151__$1,temp__5825__auto__))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_31423__$1);


var G__31426 = cljs.core.next(seq__30151__$1);
var G__31427 = null;
var G__31428 = (0);
var G__31429 = (0);
seq__30151 = G__31426;
chunk__30153 = G__31427;
count__30154 = G__31428;
i__30155 = G__31429;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.dom.on = (function shadow$dom$on(var_args){
var G__30173 = arguments.length;
switch (G__30173) {
case 3:
return shadow.dom.on.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return shadow.dom.on.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.on.cljs$core$IFn$_invoke$arity$3 = (function (el,ev,handler){
return shadow.dom.on.cljs$core$IFn$_invoke$arity$4(el,ev,handler,false);
}));

(shadow.dom.on.cljs$core$IFn$_invoke$arity$4 = (function (el,ev,handler,capture){
if(cljs.core.vector_QMARK_(ev)){
return shadow.dom.on_query(el,cljs.core.first(ev),cljs.core.second(ev),handler);
} else {
var handler__$1 = (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});
return shadow.dom.dom_listen(shadow.dom.dom_node(el),cljs.core.name(ev),handler__$1);
}
}));

(shadow.dom.on.cljs$lang$maxFixedArity = 4);

shadow.dom.remove_event_handler = (function shadow$dom$remove_event_handler(el,ev,handler){
return shadow.dom.dom_listen_remove(shadow.dom.dom_node(el),cljs.core.name(ev),handler);
});
shadow.dom.add_event_listeners = (function shadow$dom$add_event_listeners(el,events){
var seq__30187 = cljs.core.seq(events);
var chunk__30188 = null;
var count__30189 = (0);
var i__30190 = (0);
while(true){
if((i__30190 < count__30189)){
var vec__30206 = chunk__30188.cljs$core$IIndexed$_nth$arity$2(null,i__30190);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30206,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30206,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__31459 = seq__30187;
var G__31460 = chunk__30188;
var G__31461 = count__30189;
var G__31462 = (i__30190 + (1));
seq__30187 = G__31459;
chunk__30188 = G__31460;
count__30189 = G__31461;
i__30190 = G__31462;
continue;
} else {
var temp__5825__auto__ = cljs.core.seq(seq__30187);
if(temp__5825__auto__){
var seq__30187__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__30187__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__30187__$1);
var G__31467 = cljs.core.chunk_rest(seq__30187__$1);
var G__31468 = c__5548__auto__;
var G__31469 = cljs.core.count(c__5548__auto__);
var G__31470 = (0);
seq__30187 = G__31467;
chunk__30188 = G__31468;
count__30189 = G__31469;
i__30190 = G__31470;
continue;
} else {
var vec__30214 = cljs.core.first(seq__30187__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30214,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30214,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__31473 = cljs.core.next(seq__30187__$1);
var G__31474 = null;
var G__31475 = (0);
var G__31476 = (0);
seq__30187 = G__31473;
chunk__30188 = G__31474;
count__30189 = G__31475;
i__30190 = G__31476;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.dom.set_style = (function shadow$dom$set_style(el,styles){
var dom = shadow.dom.dom_node(el);
var seq__30220 = cljs.core.seq(styles);
var chunk__30221 = null;
var count__30222 = (0);
var i__30223 = (0);
while(true){
if((i__30223 < count__30222)){
var vec__30246 = chunk__30221.cljs$core$IIndexed$_nth$arity$2(null,i__30223);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30246,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30246,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__31480 = seq__30220;
var G__31481 = chunk__30221;
var G__31482 = count__30222;
var G__31483 = (i__30223 + (1));
seq__30220 = G__31480;
chunk__30221 = G__31481;
count__30222 = G__31482;
i__30223 = G__31483;
continue;
} else {
var temp__5825__auto__ = cljs.core.seq(seq__30220);
if(temp__5825__auto__){
var seq__30220__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__30220__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__30220__$1);
var G__31485 = cljs.core.chunk_rest(seq__30220__$1);
var G__31486 = c__5548__auto__;
var G__31487 = cljs.core.count(c__5548__auto__);
var G__31488 = (0);
seq__30220 = G__31485;
chunk__30221 = G__31486;
count__30222 = G__31487;
i__30223 = G__31488;
continue;
} else {
var vec__30258 = cljs.core.first(seq__30220__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30258,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30258,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__31492 = cljs.core.next(seq__30220__$1);
var G__31493 = null;
var G__31494 = (0);
var G__31495 = (0);
seq__30220 = G__31492;
chunk__30221 = G__31493;
count__30222 = G__31494;
i__30223 = G__31495;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.dom.set_attr_STAR_ = (function shadow$dom$set_attr_STAR_(el,key,value){
var G__30270_31496 = key;
var G__30270_31497__$1 = (((G__30270_31496 instanceof cljs.core.Keyword))?G__30270_31496.fqn:null);
switch (G__30270_31497__$1) {
case "id":
(el.id = cljs.core.str.cljs$core$IFn$_invoke$arity$1(value));

break;
case "class":
(el.className = cljs.core.str.cljs$core$IFn$_invoke$arity$1(value));

break;
case "for":
(el.htmlFor = value);

break;
case "cellpadding":
el.setAttribute("cellPadding",value);

break;
case "cellspacing":
el.setAttribute("cellSpacing",value);

break;
case "colspan":
el.setAttribute("colSpan",value);

break;
case "frameborder":
el.setAttribute("frameBorder",value);

break;
case "height":
el.setAttribute("height",value);

break;
case "maxlength":
el.setAttribute("maxLength",value);

break;
case "role":
el.setAttribute("role",value);

break;
case "rowspan":
el.setAttribute("rowSpan",value);

break;
case "type":
el.setAttribute("type",value);

break;
case "usemap":
el.setAttribute("useMap",value);

break;
case "valign":
el.setAttribute("vAlign",value);

break;
case "width":
el.setAttribute("width",value);

break;
case "on":
shadow.dom.add_event_listeners(el,value);

break;
case "style":
if((value == null)){
} else {
if(typeof value === 'string'){
el.setAttribute("style",value);
} else {
if(cljs.core.map_QMARK_(value)){
shadow.dom.set_style(el,value);
} else {
goog.style.setStyle(el,value);

}
}
}

break;
default:
var ks_31508 = cljs.core.name(key);
if(cljs.core.truth_((function (){var or__5025__auto__ = goog.string.startsWith(ks_31508,"data-");
if(cljs.core.truth_(or__5025__auto__)){
return or__5025__auto__;
} else {
return goog.string.startsWith(ks_31508,"aria-");
}
})())){
el.setAttribute(ks_31508,value);
} else {
(el[ks_31508] = value);
}

}

return el;
});
shadow.dom.set_attrs = (function shadow$dom$set_attrs(el,attrs){
return cljs.core.reduce_kv((function (el__$1,key,value){
shadow.dom.set_attr_STAR_(el__$1,key,value);

return el__$1;
}),shadow.dom.dom_node(el),attrs);
});
shadow.dom.set_attr = (function shadow$dom$set_attr(el,key,value){
return shadow.dom.set_attr_STAR_(shadow.dom.dom_node(el),key,value);
});
shadow.dom.has_class_QMARK_ = (function shadow$dom$has_class_QMARK_(el,cls){
return goog.dom.classlist.contains(shadow.dom.dom_node(el),cls);
});
shadow.dom.merge_class_string = (function shadow$dom$merge_class_string(current,extra_class){
if(cljs.core.seq(current)){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(current)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(extra_class)].join('');
} else {
return extra_class;
}
});
shadow.dom.parse_tag = (function shadow$dom$parse_tag(spec){
var spec__$1 = cljs.core.name(spec);
var fdot = spec__$1.indexOf(".");
var fhash = spec__$1.indexOf("#");
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fdot)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fhash)))){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1,null,null], null);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fhash)){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1.substring((0),fdot),null,clojure.string.replace(spec__$1.substring((fdot + (1))),/\./," ")], null);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fdot)){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1.substring((0),fhash),spec__$1.substring((fhash + (1))),null], null);
} else {
if((fhash > fdot)){
throw ["cant have id after class?",spec__$1].join('');
} else {
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1.substring((0),fhash),spec__$1.substring((fhash + (1)),fdot),clojure.string.replace(spec__$1.substring((fdot + (1))),/\./," ")], null);

}
}
}
}
});
shadow.dom.create_dom_node = (function shadow$dom$create_dom_node(tag_def,p__30322){
var map__30325 = p__30322;
var map__30325__$1 = cljs.core.__destructure_map(map__30325);
var props = map__30325__$1;
var class$ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__30325__$1,new cljs.core.Keyword(null,"class","class",-2030961996));
var tag_props = ({});
var vec__30327 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30327,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30327,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30327,(2),null);
if(cljs.core.truth_(tag_id)){
(tag_props["id"] = tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
(tag_props["class"] = shadow.dom.merge_class_string(class$,tag_classes));
} else {
}

var G__30335 = goog.dom.createDom(tag_name,tag_props);
shadow.dom.set_attrs(G__30335,cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(props,new cljs.core.Keyword(null,"class","class",-2030961996)));

return G__30335;
});
shadow.dom.append = (function shadow$dom$append(var_args){
var G__30350 = arguments.length;
switch (G__30350) {
case 1:
return shadow.dom.append.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.append.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.append.cljs$core$IFn$_invoke$arity$1 = (function (node){
if(cljs.core.truth_(node)){
var temp__5825__auto__ = shadow.dom.dom_node(node);
if(cljs.core.truth_(temp__5825__auto__)){
var n = temp__5825__auto__;
document.body.appendChild(n);

return n;
} else {
return null;
}
} else {
return null;
}
}));

(shadow.dom.append.cljs$core$IFn$_invoke$arity$2 = (function (el,node){
if(cljs.core.truth_(node)){
var temp__5825__auto__ = shadow.dom.dom_node(node);
if(cljs.core.truth_(temp__5825__auto__)){
var n = temp__5825__auto__;
shadow.dom.dom_node(el).appendChild(n);

return n;
} else {
return null;
}
} else {
return null;
}
}));

(shadow.dom.append.cljs$lang$maxFixedArity = 2);

shadow.dom.destructure_node = (function shadow$dom$destructure_node(create_fn,p__30397){
var vec__30399 = p__30397;
var seq__30400 = cljs.core.seq(vec__30399);
var first__30401 = cljs.core.first(seq__30400);
var seq__30400__$1 = cljs.core.next(seq__30400);
var nn = first__30401;
var first__30401__$1 = cljs.core.first(seq__30400__$1);
var seq__30400__$2 = cljs.core.next(seq__30400__$1);
var np = first__30401__$1;
var nc = seq__30400__$2;
var node = vec__30399;
if((nn instanceof cljs.core.Keyword)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("invalid dom node",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"node","node",581201198),node], null));
}

if((((np == null)) && ((nc == null)))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__30403 = nn;
var G__30404 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__30403,G__30404) : create_fn.call(null,G__30403,G__30404));
})(),cljs.core.List.EMPTY], null);
} else {
if(cljs.core.map_QMARK_(np)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(nn,np) : create_fn.call(null,nn,np)),nc], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__30405 = nn;
var G__30406 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__30405,G__30406) : create_fn.call(null,G__30405,G__30406));
})(),cljs.core.conj.cljs$core$IFn$_invoke$arity$2(nc,np)], null);

}
}
});
shadow.dom.make_dom_node = (function shadow$dom$make_dom_node(structure){
var vec__30407 = shadow.dom.destructure_node(shadow.dom.create_dom_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30407,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30407,(1),null);
var seq__30410_31566 = cljs.core.seq(node_children);
var chunk__30411_31567 = null;
var count__30412_31568 = (0);
var i__30413_31569 = (0);
while(true){
if((i__30413_31569 < count__30412_31568)){
var child_struct_31572 = chunk__30411_31567.cljs$core$IIndexed$_nth$arity$2(null,i__30413_31569);
var children_31575 = shadow.dom.dom_node(child_struct_31572);
if(cljs.core.seq_QMARK_(children_31575)){
var seq__30459_31577 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_31575));
var chunk__30461_31578 = null;
var count__30462_31579 = (0);
var i__30463_31580 = (0);
while(true){
if((i__30463_31580 < count__30462_31579)){
var child_31582 = chunk__30461_31578.cljs$core$IIndexed$_nth$arity$2(null,i__30463_31580);
if(cljs.core.truth_(child_31582)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_31582);


var G__31585 = seq__30459_31577;
var G__31586 = chunk__30461_31578;
var G__31587 = count__30462_31579;
var G__31588 = (i__30463_31580 + (1));
seq__30459_31577 = G__31585;
chunk__30461_31578 = G__31586;
count__30462_31579 = G__31587;
i__30463_31580 = G__31588;
continue;
} else {
var G__31590 = seq__30459_31577;
var G__31591 = chunk__30461_31578;
var G__31592 = count__30462_31579;
var G__31593 = (i__30463_31580 + (1));
seq__30459_31577 = G__31590;
chunk__30461_31578 = G__31591;
count__30462_31579 = G__31592;
i__30463_31580 = G__31593;
continue;
}
} else {
var temp__5825__auto___31595 = cljs.core.seq(seq__30459_31577);
if(temp__5825__auto___31595){
var seq__30459_31597__$1 = temp__5825__auto___31595;
if(cljs.core.chunked_seq_QMARK_(seq__30459_31597__$1)){
var c__5548__auto___31599 = cljs.core.chunk_first(seq__30459_31597__$1);
var G__31600 = cljs.core.chunk_rest(seq__30459_31597__$1);
var G__31601 = c__5548__auto___31599;
var G__31602 = cljs.core.count(c__5548__auto___31599);
var G__31603 = (0);
seq__30459_31577 = G__31600;
chunk__30461_31578 = G__31601;
count__30462_31579 = G__31602;
i__30463_31580 = G__31603;
continue;
} else {
var child_31608 = cljs.core.first(seq__30459_31597__$1);
if(cljs.core.truth_(child_31608)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_31608);


var G__31614 = cljs.core.next(seq__30459_31597__$1);
var G__31615 = null;
var G__31616 = (0);
var G__31617 = (0);
seq__30459_31577 = G__31614;
chunk__30461_31578 = G__31615;
count__30462_31579 = G__31616;
i__30463_31580 = G__31617;
continue;
} else {
var G__31618 = cljs.core.next(seq__30459_31597__$1);
var G__31619 = null;
var G__31620 = (0);
var G__31621 = (0);
seq__30459_31577 = G__31618;
chunk__30461_31578 = G__31619;
count__30462_31579 = G__31620;
i__30463_31580 = G__31621;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_31575);
}


var G__31622 = seq__30410_31566;
var G__31623 = chunk__30411_31567;
var G__31624 = count__30412_31568;
var G__31625 = (i__30413_31569 + (1));
seq__30410_31566 = G__31622;
chunk__30411_31567 = G__31623;
count__30412_31568 = G__31624;
i__30413_31569 = G__31625;
continue;
} else {
var temp__5825__auto___31627 = cljs.core.seq(seq__30410_31566);
if(temp__5825__auto___31627){
var seq__30410_31630__$1 = temp__5825__auto___31627;
if(cljs.core.chunked_seq_QMARK_(seq__30410_31630__$1)){
var c__5548__auto___31631 = cljs.core.chunk_first(seq__30410_31630__$1);
var G__31633 = cljs.core.chunk_rest(seq__30410_31630__$1);
var G__31634 = c__5548__auto___31631;
var G__31635 = cljs.core.count(c__5548__auto___31631);
var G__31636 = (0);
seq__30410_31566 = G__31633;
chunk__30411_31567 = G__31634;
count__30412_31568 = G__31635;
i__30413_31569 = G__31636;
continue;
} else {
var child_struct_31638 = cljs.core.first(seq__30410_31630__$1);
var children_31639 = shadow.dom.dom_node(child_struct_31638);
if(cljs.core.seq_QMARK_(children_31639)){
var seq__30485_31640 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_31639));
var chunk__30487_31641 = null;
var count__30488_31642 = (0);
var i__30489_31643 = (0);
while(true){
if((i__30489_31643 < count__30488_31642)){
var child_31644 = chunk__30487_31641.cljs$core$IIndexed$_nth$arity$2(null,i__30489_31643);
if(cljs.core.truth_(child_31644)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_31644);


var G__31646 = seq__30485_31640;
var G__31647 = chunk__30487_31641;
var G__31648 = count__30488_31642;
var G__31649 = (i__30489_31643 + (1));
seq__30485_31640 = G__31646;
chunk__30487_31641 = G__31647;
count__30488_31642 = G__31648;
i__30489_31643 = G__31649;
continue;
} else {
var G__31650 = seq__30485_31640;
var G__31651 = chunk__30487_31641;
var G__31652 = count__30488_31642;
var G__31653 = (i__30489_31643 + (1));
seq__30485_31640 = G__31650;
chunk__30487_31641 = G__31651;
count__30488_31642 = G__31652;
i__30489_31643 = G__31653;
continue;
}
} else {
var temp__5825__auto___31654__$1 = cljs.core.seq(seq__30485_31640);
if(temp__5825__auto___31654__$1){
var seq__30485_31655__$1 = temp__5825__auto___31654__$1;
if(cljs.core.chunked_seq_QMARK_(seq__30485_31655__$1)){
var c__5548__auto___31656 = cljs.core.chunk_first(seq__30485_31655__$1);
var G__31657 = cljs.core.chunk_rest(seq__30485_31655__$1);
var G__31658 = c__5548__auto___31656;
var G__31659 = cljs.core.count(c__5548__auto___31656);
var G__31660 = (0);
seq__30485_31640 = G__31657;
chunk__30487_31641 = G__31658;
count__30488_31642 = G__31659;
i__30489_31643 = G__31660;
continue;
} else {
var child_31662 = cljs.core.first(seq__30485_31655__$1);
if(cljs.core.truth_(child_31662)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_31662);


var G__31665 = cljs.core.next(seq__30485_31655__$1);
var G__31666 = null;
var G__31667 = (0);
var G__31668 = (0);
seq__30485_31640 = G__31665;
chunk__30487_31641 = G__31666;
count__30488_31642 = G__31667;
i__30489_31643 = G__31668;
continue;
} else {
var G__31669 = cljs.core.next(seq__30485_31655__$1);
var G__31670 = null;
var G__31671 = (0);
var G__31672 = (0);
seq__30485_31640 = G__31669;
chunk__30487_31641 = G__31670;
count__30488_31642 = G__31671;
i__30489_31643 = G__31672;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_31639);
}


var G__31674 = cljs.core.next(seq__30410_31630__$1);
var G__31675 = null;
var G__31676 = (0);
var G__31677 = (0);
seq__30410_31566 = G__31674;
chunk__30411_31567 = G__31675;
count__30412_31568 = G__31676;
i__30413_31569 = G__31677;
continue;
}
} else {
}
}
break;
}

return node;
});
(cljs.core.Keyword.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.Keyword.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return shadow.dom.make_dom_node(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [this$__$1], null));
}));

(cljs.core.PersistentVector.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.PersistentVector.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return shadow.dom.make_dom_node(this$__$1);
}));

(cljs.core.LazySeq.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.LazySeq.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom._to_dom,this$__$1);
}));
if(cljs.core.truth_(((typeof HTMLElement) != 'undefined'))){
(HTMLElement.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(HTMLElement.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1;
}));
} else {
}
if(cljs.core.truth_(((typeof DocumentFragment) != 'undefined'))){
(DocumentFragment.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(DocumentFragment.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1;
}));
} else {
}
/**
 * clear node children
 */
shadow.dom.reset = (function shadow$dom$reset(node){
return goog.dom.removeChildren(shadow.dom.dom_node(node));
});
shadow.dom.remove = (function shadow$dom$remove(node){
if((((!((node == null))))?(((((node.cljs$lang$protocol_mask$partition0$ & (8388608))) || ((cljs.core.PROTOCOL_SENTINEL === node.cljs$core$ISeqable$))))?true:false):false)){
var seq__30553 = cljs.core.seq(node);
var chunk__30554 = null;
var count__30555 = (0);
var i__30556 = (0);
while(true){
if((i__30556 < count__30555)){
var n = chunk__30554.cljs$core$IIndexed$_nth$arity$2(null,i__30556);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__31697 = seq__30553;
var G__31698 = chunk__30554;
var G__31699 = count__30555;
var G__31700 = (i__30556 + (1));
seq__30553 = G__31697;
chunk__30554 = G__31698;
count__30555 = G__31699;
i__30556 = G__31700;
continue;
} else {
var temp__5825__auto__ = cljs.core.seq(seq__30553);
if(temp__5825__auto__){
var seq__30553__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__30553__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__30553__$1);
var G__31703 = cljs.core.chunk_rest(seq__30553__$1);
var G__31704 = c__5548__auto__;
var G__31705 = cljs.core.count(c__5548__auto__);
var G__31706 = (0);
seq__30553 = G__31703;
chunk__30554 = G__31704;
count__30555 = G__31705;
i__30556 = G__31706;
continue;
} else {
var n = cljs.core.first(seq__30553__$1);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__31708 = cljs.core.next(seq__30553__$1);
var G__31709 = null;
var G__31710 = (0);
var G__31711 = (0);
seq__30553 = G__31708;
chunk__30554 = G__31709;
count__30555 = G__31710;
i__30556 = G__31711;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return goog.dom.removeNode(node);
}
});
shadow.dom.replace_node = (function shadow$dom$replace_node(old,new$){
return goog.dom.replaceNode(shadow.dom.dom_node(new$),shadow.dom.dom_node(old));
});
shadow.dom.text = (function shadow$dom$text(var_args){
var G__30592 = arguments.length;
switch (G__30592) {
case 2:
return shadow.dom.text.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 1:
return shadow.dom.text.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.text.cljs$core$IFn$_invoke$arity$2 = (function (el,new_text){
return (shadow.dom.dom_node(el).innerText = new_text);
}));

(shadow.dom.text.cljs$core$IFn$_invoke$arity$1 = (function (el){
return shadow.dom.dom_node(el).innerText;
}));

(shadow.dom.text.cljs$lang$maxFixedArity = 2);

shadow.dom.check = (function shadow$dom$check(var_args){
var G__30600 = arguments.length;
switch (G__30600) {
case 1:
return shadow.dom.check.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.check.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.check.cljs$core$IFn$_invoke$arity$1 = (function (el){
return shadow.dom.check.cljs$core$IFn$_invoke$arity$2(el,true);
}));

(shadow.dom.check.cljs$core$IFn$_invoke$arity$2 = (function (el,checked){
return (shadow.dom.dom_node(el).checked = checked);
}));

(shadow.dom.check.cljs$lang$maxFixedArity = 2);

shadow.dom.checked_QMARK_ = (function shadow$dom$checked_QMARK_(el){
return shadow.dom.dom_node(el).checked;
});
shadow.dom.form_elements = (function shadow$dom$form_elements(el){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(el).elements));
});
shadow.dom.children = (function shadow$dom$children(el){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(el).children));
});
shadow.dom.child_nodes = (function shadow$dom$child_nodes(el){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(el).childNodes));
});
shadow.dom.attr = (function shadow$dom$attr(var_args){
var G__30607 = arguments.length;
switch (G__30607) {
case 2:
return shadow.dom.attr.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return shadow.dom.attr.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.attr.cljs$core$IFn$_invoke$arity$2 = (function (el,key){
return shadow.dom.dom_node(el).getAttribute(cljs.core.name(key));
}));

(shadow.dom.attr.cljs$core$IFn$_invoke$arity$3 = (function (el,key,default$){
var or__5025__auto__ = shadow.dom.dom_node(el).getAttribute(cljs.core.name(key));
if(cljs.core.truth_(or__5025__auto__)){
return or__5025__auto__;
} else {
return default$;
}
}));

(shadow.dom.attr.cljs$lang$maxFixedArity = 3);

shadow.dom.del_attr = (function shadow$dom$del_attr(el,key){
return shadow.dom.dom_node(el).removeAttribute(cljs.core.name(key));
});
shadow.dom.data = (function shadow$dom$data(el,key){
return shadow.dom.dom_node(el).getAttribute(["data-",cljs.core.name(key)].join(''));
});
shadow.dom.set_data = (function shadow$dom$set_data(el,key,value){
return shadow.dom.dom_node(el).setAttribute(["data-",cljs.core.name(key)].join(''),cljs.core.str.cljs$core$IFn$_invoke$arity$1(value));
});
shadow.dom.set_html = (function shadow$dom$set_html(node,text){
return (shadow.dom.dom_node(node).innerHTML = text);
});
shadow.dom.get_html = (function shadow$dom$get_html(node){
return shadow.dom.dom_node(node).innerHTML;
});
shadow.dom.fragment = (function shadow$dom$fragment(var_args){
var args__5755__auto__ = [];
var len__5749__auto___31739 = arguments.length;
var i__5750__auto___31741 = (0);
while(true){
if((i__5750__auto___31741 < len__5749__auto___31739)){
args__5755__auto__.push((arguments[i__5750__auto___31741]));

var G__31745 = (i__5750__auto___31741 + (1));
i__5750__auto___31741 = G__31745;
continue;
} else {
}
break;
}

var argseq__5756__auto__ = ((((0) < args__5755__auto__.length))?(new cljs.core.IndexedSeq(args__5755__auto__.slice((0)),(0),null)):null);
return shadow.dom.fragment.cljs$core$IFn$_invoke$arity$variadic(argseq__5756__auto__);
});

(shadow.dom.fragment.cljs$core$IFn$_invoke$arity$variadic = (function (nodes){
var fragment = document.createDocumentFragment();
var seq__30640_31756 = cljs.core.seq(nodes);
var chunk__30641_31757 = null;
var count__30642_31758 = (0);
var i__30643_31759 = (0);
while(true){
if((i__30643_31759 < count__30642_31758)){
var node_31760 = chunk__30641_31757.cljs$core$IIndexed$_nth$arity$2(null,i__30643_31759);
fragment.appendChild(shadow.dom._to_dom(node_31760));


var G__31763 = seq__30640_31756;
var G__31764 = chunk__30641_31757;
var G__31765 = count__30642_31758;
var G__31766 = (i__30643_31759 + (1));
seq__30640_31756 = G__31763;
chunk__30641_31757 = G__31764;
count__30642_31758 = G__31765;
i__30643_31759 = G__31766;
continue;
} else {
var temp__5825__auto___31768 = cljs.core.seq(seq__30640_31756);
if(temp__5825__auto___31768){
var seq__30640_31769__$1 = temp__5825__auto___31768;
if(cljs.core.chunked_seq_QMARK_(seq__30640_31769__$1)){
var c__5548__auto___31771 = cljs.core.chunk_first(seq__30640_31769__$1);
var G__31772 = cljs.core.chunk_rest(seq__30640_31769__$1);
var G__31773 = c__5548__auto___31771;
var G__31774 = cljs.core.count(c__5548__auto___31771);
var G__31775 = (0);
seq__30640_31756 = G__31772;
chunk__30641_31757 = G__31773;
count__30642_31758 = G__31774;
i__30643_31759 = G__31775;
continue;
} else {
var node_31776 = cljs.core.first(seq__30640_31769__$1);
fragment.appendChild(shadow.dom._to_dom(node_31776));


var G__31778 = cljs.core.next(seq__30640_31769__$1);
var G__31779 = null;
var G__31780 = (0);
var G__31781 = (0);
seq__30640_31756 = G__31778;
chunk__30641_31757 = G__31779;
count__30642_31758 = G__31780;
i__30643_31759 = G__31781;
continue;
}
} else {
}
}
break;
}

return (new shadow.dom.NativeColl(fragment));
}));

(shadow.dom.fragment.cljs$lang$maxFixedArity = (0));

/** @this {Function} */
(shadow.dom.fragment.cljs$lang$applyTo = (function (seq30626){
var self__5735__auto__ = this;
return self__5735__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq(seq30626));
}));

/**
 * given a html string, eval all <script> tags and return the html without the scripts
 * don't do this for everything, only content you trust.
 */
shadow.dom.eval_scripts = (function shadow$dom$eval_scripts(s){
var scripts = cljs.core.re_seq(/<script[^>]*?>(.+?)<\/script>/,s);
var seq__30658_31784 = cljs.core.seq(scripts);
var chunk__30659_31785 = null;
var count__30660_31787 = (0);
var i__30661_31788 = (0);
while(true){
if((i__30661_31788 < count__30660_31787)){
var vec__30683_31794 = chunk__30659_31785.cljs$core$IIndexed$_nth$arity$2(null,i__30661_31788);
var script_tag_31796 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30683_31794,(0),null);
var script_body_31797 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30683_31794,(1),null);
eval(script_body_31797);


var G__31799 = seq__30658_31784;
var G__31800 = chunk__30659_31785;
var G__31801 = count__30660_31787;
var G__31802 = (i__30661_31788 + (1));
seq__30658_31784 = G__31799;
chunk__30659_31785 = G__31800;
count__30660_31787 = G__31801;
i__30661_31788 = G__31802;
continue;
} else {
var temp__5825__auto___31803 = cljs.core.seq(seq__30658_31784);
if(temp__5825__auto___31803){
var seq__30658_31805__$1 = temp__5825__auto___31803;
if(cljs.core.chunked_seq_QMARK_(seq__30658_31805__$1)){
var c__5548__auto___31807 = cljs.core.chunk_first(seq__30658_31805__$1);
var G__31808 = cljs.core.chunk_rest(seq__30658_31805__$1);
var G__31809 = c__5548__auto___31807;
var G__31810 = cljs.core.count(c__5548__auto___31807);
var G__31811 = (0);
seq__30658_31784 = G__31808;
chunk__30659_31785 = G__31809;
count__30660_31787 = G__31810;
i__30661_31788 = G__31811;
continue;
} else {
var vec__30694_31814 = cljs.core.first(seq__30658_31805__$1);
var script_tag_31815 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30694_31814,(0),null);
var script_body_31816 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30694_31814,(1),null);
eval(script_body_31816);


var G__31818 = cljs.core.next(seq__30658_31805__$1);
var G__31819 = null;
var G__31820 = (0);
var G__31821 = (0);
seq__30658_31784 = G__31818;
chunk__30659_31785 = G__31819;
count__30660_31787 = G__31820;
i__30661_31788 = G__31821;
continue;
}
} else {
}
}
break;
}

return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (s__$1,p__30702){
var vec__30707 = p__30702;
var script_tag = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30707,(0),null);
var script_body = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30707,(1),null);
return clojure.string.replace(s__$1,script_tag,"");
}),s,scripts);
});
shadow.dom.str__GT_fragment = (function shadow$dom$str__GT_fragment(s){
var el = document.createElement("div");
(el.innerHTML = s);

return (new shadow.dom.NativeColl(goog.dom.childrenToNode_(document,el)));
});
shadow.dom.node_name = (function shadow$dom$node_name(el){
return shadow.dom.dom_node(el).nodeName;
});
shadow.dom.ancestor_by_class = (function shadow$dom$ancestor_by_class(el,cls){
return goog.dom.getAncestorByClass(shadow.dom.dom_node(el),cls);
});
shadow.dom.ancestor_by_tag = (function shadow$dom$ancestor_by_tag(var_args){
var G__30713 = arguments.length;
switch (G__30713) {
case 2:
return shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$2 = (function (el,tag){
return goog.dom.getAncestorByTagNameAndClass(shadow.dom.dom_node(el),cljs.core.name(tag));
}));

(shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$3 = (function (el,tag,cls){
return goog.dom.getAncestorByTagNameAndClass(shadow.dom.dom_node(el),cljs.core.name(tag),cljs.core.name(cls));
}));

(shadow.dom.ancestor_by_tag.cljs$lang$maxFixedArity = 3);

shadow.dom.get_value = (function shadow$dom$get_value(dom){
return goog.dom.forms.getValue(shadow.dom.dom_node(dom));
});
shadow.dom.set_value = (function shadow$dom$set_value(dom,value){
return goog.dom.forms.setValue(shadow.dom.dom_node(dom),value);
});
shadow.dom.px = (function shadow$dom$px(value){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1((value | (0))),"px"].join('');
});
shadow.dom.pct = (function shadow$dom$pct(value){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(value),"%"].join('');
});
shadow.dom.remove_style_STAR_ = (function shadow$dom$remove_style_STAR_(el,style){
return el.style.removeProperty(cljs.core.name(style));
});
shadow.dom.remove_style = (function shadow$dom$remove_style(el,style){
var el__$1 = shadow.dom.dom_node(el);
return shadow.dom.remove_style_STAR_(el__$1,style);
});
shadow.dom.remove_styles = (function shadow$dom$remove_styles(el,style_keys){
var el__$1 = shadow.dom.dom_node(el);
var seq__30769 = cljs.core.seq(style_keys);
var chunk__30770 = null;
var count__30771 = (0);
var i__30772 = (0);
while(true){
if((i__30772 < count__30771)){
var it = chunk__30770.cljs$core$IIndexed$_nth$arity$2(null,i__30772);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__31856 = seq__30769;
var G__31857 = chunk__30770;
var G__31858 = count__30771;
var G__31859 = (i__30772 + (1));
seq__30769 = G__31856;
chunk__30770 = G__31857;
count__30771 = G__31858;
i__30772 = G__31859;
continue;
} else {
var temp__5825__auto__ = cljs.core.seq(seq__30769);
if(temp__5825__auto__){
var seq__30769__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__30769__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__30769__$1);
var G__31862 = cljs.core.chunk_rest(seq__30769__$1);
var G__31863 = c__5548__auto__;
var G__31864 = cljs.core.count(c__5548__auto__);
var G__31865 = (0);
seq__30769 = G__31862;
chunk__30770 = G__31863;
count__30771 = G__31864;
i__30772 = G__31865;
continue;
} else {
var it = cljs.core.first(seq__30769__$1);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__31866 = cljs.core.next(seq__30769__$1);
var G__31867 = null;
var G__31868 = (0);
var G__31869 = (0);
seq__30769 = G__31866;
chunk__30770 = G__31867;
count__30771 = G__31868;
i__30772 = G__31869;
continue;
}
} else {
return null;
}
}
break;
}
});

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IKVReduce}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
shadow.dom.Coordinate = (function (x,y,__meta,__extmap,__hash){
this.x = x;
this.y = y;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
(shadow.dom.Coordinate.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__5323__auto__,k__5324__auto__){
var self__ = this;
var this__5323__auto____$1 = this;
return this__5323__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__5324__auto__,null);
}));

(shadow.dom.Coordinate.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5325__auto__,k30808,else__5326__auto__){
var self__ = this;
var this__5325__auto____$1 = this;
var G__30894 = k30808;
var G__30894__$1 = (((G__30894 instanceof cljs.core.Keyword))?G__30894.fqn:null);
switch (G__30894__$1) {
case "x":
return self__.x;

break;
case "y":
return self__.y;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k30808,else__5326__auto__);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5343__auto__,f__5344__auto__,init__5345__auto__){
var self__ = this;
var this__5343__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5346__auto__,p__30908){
var vec__30909 = p__30908;
var k__5347__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30909,(0),null);
var v__5348__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__30909,(1),null);
return (f__5344__auto__.cljs$core$IFn$_invoke$arity$3 ? f__5344__auto__.cljs$core$IFn$_invoke$arity$3(ret__5346__auto__,k__5347__auto__,v__5348__auto__) : f__5344__auto__.call(null,ret__5346__auto__,k__5347__auto__,v__5348__auto__));
}),init__5345__auto__,this__5343__auto____$1);
}));

(shadow.dom.Coordinate.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__5338__auto__,writer__5339__auto__,opts__5340__auto__){
var self__ = this;
var this__5338__auto____$1 = this;
var pr_pair__5341__auto__ = (function (keyval__5342__auto__){
return cljs.core.pr_sequential_writer(writer__5339__auto__,cljs.core.pr_writer,""," ","",opts__5340__auto__,keyval__5342__auto__);
});
return cljs.core.pr_sequential_writer(writer__5339__auto__,pr_pair__5341__auto__,"#shadow.dom.Coordinate{",", ","}",opts__5340__auto__,cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"x","x",2099068185),self__.x],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"y","y",-1757859776),self__.y],null))], null),self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__30807){
var self__ = this;
var G__30807__$1 = this;
return (new cljs.core.RecordIter((0),G__30807__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.Keyword(null,"y","y",-1757859776)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__5321__auto__){
var self__ = this;
var this__5321__auto____$1 = this;
return self__.__meta;
}));

(shadow.dom.Coordinate.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__5318__auto__){
var self__ = this;
var this__5318__auto____$1 = this;
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,self__.__extmap,self__.__hash));
}));

(shadow.dom.Coordinate.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__5327__auto__){
var self__ = this;
var this__5327__auto____$1 = this;
return (2 + cljs.core.count(self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__5319__auto__){
var self__ = this;
var this__5319__auto____$1 = this;
var h__5134__auto__ = self__.__hash;
if((!((h__5134__auto__ == null)))){
return h__5134__auto__;
} else {
var h__5134__auto____$1 = (function (coll__5320__auto__){
return (145542109 ^ cljs.core.hash_unordered_coll(coll__5320__auto__));
})(this__5319__auto____$1);
(self__.__hash = h__5134__auto____$1);

return h__5134__auto____$1;
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this30809,other30810){
var self__ = this;
var this30809__$1 = this;
return (((!((other30810 == null)))) && ((((this30809__$1.constructor === other30810.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this30809__$1.x,other30810.x)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this30809__$1.y,other30810.y)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this30809__$1.__extmap,other30810.__extmap)))))))));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__5333__auto__,k__5334__auto__){
var self__ = this;
var this__5333__auto____$1 = this;
if(cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"y","y",-1757859776),null,new cljs.core.Keyword(null,"x","x",2099068185),null], null), null),k__5334__auto__)){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(cljs.core._with_meta(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,this__5333__auto____$1),self__.__meta),k__5334__auto__);
} else {
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,cljs.core.not_empty(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(self__.__extmap,k__5334__auto__)),null));
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5330__auto__,k30808){
var self__ = this;
var this__5330__auto____$1 = this;
var G__30948 = k30808;
var G__30948__$1 = (((G__30948 instanceof cljs.core.Keyword))?G__30948.fqn:null);
switch (G__30948__$1) {
case "x":
case "y":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k30808);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5331__auto__,k__5332__auto__,G__30807){
var self__ = this;
var this__5331__auto____$1 = this;
var pred__30950 = cljs.core.keyword_identical_QMARK_;
var expr__30951 = k__5332__auto__;
if(cljs.core.truth_((pred__30950.cljs$core$IFn$_invoke$arity$2 ? pred__30950.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"x","x",2099068185),expr__30951) : pred__30950.call(null,new cljs.core.Keyword(null,"x","x",2099068185),expr__30951)))){
return (new shadow.dom.Coordinate(G__30807,self__.y,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__30950.cljs$core$IFn$_invoke$arity$2 ? pred__30950.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"y","y",-1757859776),expr__30951) : pred__30950.call(null,new cljs.core.Keyword(null,"y","y",-1757859776),expr__30951)))){
return (new shadow.dom.Coordinate(self__.x,G__30807,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5332__auto__,G__30807),null));
}
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5336__auto__){
var self__ = this;
var this__5336__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"x","x",2099068185),self__.x,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"y","y",-1757859776),self__.y,null))], null),self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5322__auto__,G__30807){
var self__ = this;
var this__5322__auto____$1 = this;
return (new shadow.dom.Coordinate(self__.x,self__.y,G__30807,self__.__extmap,self__.__hash));
}));

(shadow.dom.Coordinate.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__5328__auto__,entry__5329__auto__){
var self__ = this;
var this__5328__auto____$1 = this;
if(cljs.core.vector_QMARK_(entry__5329__auto__)){
return this__5328__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth(entry__5329__auto__,(0)),cljs.core._nth(entry__5329__auto__,(1)));
} else {
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(cljs.core._conj,this__5328__auto____$1,entry__5329__auto__);
}
}));

(shadow.dom.Coordinate.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"x","x",-555367584,null),new cljs.core.Symbol(null,"y","y",-117328249,null)], null);
}));

(shadow.dom.Coordinate.cljs$lang$type = true);

(shadow.dom.Coordinate.cljs$lang$ctorPrSeq = (function (this__5369__auto__){
return (new cljs.core.List(null,"shadow.dom/Coordinate",null,(1),null));
}));

(shadow.dom.Coordinate.cljs$lang$ctorPrWriter = (function (this__5369__auto__,writer__5370__auto__){
return cljs.core._write(writer__5370__auto__,"shadow.dom/Coordinate");
}));

/**
 * Positional factory function for shadow.dom/Coordinate.
 */
shadow.dom.__GT_Coordinate = (function shadow$dom$__GT_Coordinate(x,y){
return (new shadow.dom.Coordinate(x,y,null,null,null));
});

/**
 * Factory function for shadow.dom/Coordinate, taking a map of keywords to field values.
 */
shadow.dom.map__GT_Coordinate = (function shadow$dom$map__GT_Coordinate(G__30870){
var extmap__5365__auto__ = (function (){var G__30968 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__30870,new cljs.core.Keyword(null,"x","x",2099068185),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"y","y",-1757859776)], 0));
if(cljs.core.record_QMARK_(G__30870)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__30968);
} else {
return G__30968;
}
})();
return (new shadow.dom.Coordinate(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(G__30870),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(G__30870),null,cljs.core.not_empty(extmap__5365__auto__),null));
});

shadow.dom.get_position = (function shadow$dom$get_position(el){
var pos = goog.style.getPosition(shadow.dom.dom_node(el));
return shadow.dom.__GT_Coordinate(pos.x,pos.y);
});
shadow.dom.get_client_position = (function shadow$dom$get_client_position(el){
var pos = goog.style.getClientPosition(shadow.dom.dom_node(el));
return shadow.dom.__GT_Coordinate(pos.x,pos.y);
});
shadow.dom.get_page_offset = (function shadow$dom$get_page_offset(el){
var pos = goog.style.getPageOffset(shadow.dom.dom_node(el));
return shadow.dom.__GT_Coordinate(pos.x,pos.y);
});

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IKVReduce}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
shadow.dom.Size = (function (w,h,__meta,__extmap,__hash){
this.w = w;
this.h = h;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
(shadow.dom.Size.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__5323__auto__,k__5324__auto__){
var self__ = this;
var this__5323__auto____$1 = this;
return this__5323__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__5324__auto__,null);
}));

(shadow.dom.Size.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5325__auto__,k31003,else__5326__auto__){
var self__ = this;
var this__5325__auto____$1 = this;
var G__31018 = k31003;
var G__31018__$1 = (((G__31018 instanceof cljs.core.Keyword))?G__31018.fqn:null);
switch (G__31018__$1) {
case "w":
return self__.w;

break;
case "h":
return self__.h;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k31003,else__5326__auto__);

}
}));

(shadow.dom.Size.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5343__auto__,f__5344__auto__,init__5345__auto__){
var self__ = this;
var this__5343__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5346__auto__,p__31033){
var vec__31035 = p__31033;
var k__5347__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31035,(0),null);
var v__5348__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31035,(1),null);
return (f__5344__auto__.cljs$core$IFn$_invoke$arity$3 ? f__5344__auto__.cljs$core$IFn$_invoke$arity$3(ret__5346__auto__,k__5347__auto__,v__5348__auto__) : f__5344__auto__.call(null,ret__5346__auto__,k__5347__auto__,v__5348__auto__));
}),init__5345__auto__,this__5343__auto____$1);
}));

(shadow.dom.Size.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__5338__auto__,writer__5339__auto__,opts__5340__auto__){
var self__ = this;
var this__5338__auto____$1 = this;
var pr_pair__5341__auto__ = (function (keyval__5342__auto__){
return cljs.core.pr_sequential_writer(writer__5339__auto__,cljs.core.pr_writer,""," ","",opts__5340__auto__,keyval__5342__auto__);
});
return cljs.core.pr_sequential_writer(writer__5339__auto__,pr_pair__5341__auto__,"#shadow.dom.Size{",", ","}",opts__5340__auto__,cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"w","w",354169001),self__.w],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"h","h",1109658740),self__.h],null))], null),self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__31002){
var self__ = this;
var G__31002__$1 = this;
return (new cljs.core.RecordIter((0),G__31002__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"w","w",354169001),new cljs.core.Keyword(null,"h","h",1109658740)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
}));

(shadow.dom.Size.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__5321__auto__){
var self__ = this;
var this__5321__auto____$1 = this;
return self__.__meta;
}));

(shadow.dom.Size.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__5318__auto__){
var self__ = this;
var this__5318__auto____$1 = this;
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,self__.__extmap,self__.__hash));
}));

(shadow.dom.Size.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__5327__auto__){
var self__ = this;
var this__5327__auto____$1 = this;
return (2 + cljs.core.count(self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__5319__auto__){
var self__ = this;
var this__5319__auto____$1 = this;
var h__5134__auto__ = self__.__hash;
if((!((h__5134__auto__ == null)))){
return h__5134__auto__;
} else {
var h__5134__auto____$1 = (function (coll__5320__auto__){
return (-1228019642 ^ cljs.core.hash_unordered_coll(coll__5320__auto__));
})(this__5319__auto____$1);
(self__.__hash = h__5134__auto____$1);

return h__5134__auto____$1;
}
}));

(shadow.dom.Size.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this31004,other31005){
var self__ = this;
var this31004__$1 = this;
return (((!((other31005 == null)))) && ((((this31004__$1.constructor === other31005.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this31004__$1.w,other31005.w)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this31004__$1.h,other31005.h)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this31004__$1.__extmap,other31005.__extmap)))))))));
}));

(shadow.dom.Size.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__5333__auto__,k__5334__auto__){
var self__ = this;
var this__5333__auto____$1 = this;
if(cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"w","w",354169001),null,new cljs.core.Keyword(null,"h","h",1109658740),null], null), null),k__5334__auto__)){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(cljs.core._with_meta(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,this__5333__auto____$1),self__.__meta),k__5334__auto__);
} else {
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,cljs.core.not_empty(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(self__.__extmap,k__5334__auto__)),null));
}
}));

(shadow.dom.Size.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5330__auto__,k31003){
var self__ = this;
var this__5330__auto____$1 = this;
var G__31076 = k31003;
var G__31076__$1 = (((G__31076 instanceof cljs.core.Keyword))?G__31076.fqn:null);
switch (G__31076__$1) {
case "w":
case "h":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k31003);

}
}));

(shadow.dom.Size.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5331__auto__,k__5332__auto__,G__31002){
var self__ = this;
var this__5331__auto____$1 = this;
var pred__31080 = cljs.core.keyword_identical_QMARK_;
var expr__31081 = k__5332__auto__;
if(cljs.core.truth_((pred__31080.cljs$core$IFn$_invoke$arity$2 ? pred__31080.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"w","w",354169001),expr__31081) : pred__31080.call(null,new cljs.core.Keyword(null,"w","w",354169001),expr__31081)))){
return (new shadow.dom.Size(G__31002,self__.h,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__31080.cljs$core$IFn$_invoke$arity$2 ? pred__31080.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"h","h",1109658740),expr__31081) : pred__31080.call(null,new cljs.core.Keyword(null,"h","h",1109658740),expr__31081)))){
return (new shadow.dom.Size(self__.w,G__31002,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5332__auto__,G__31002),null));
}
}
}));

(shadow.dom.Size.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5336__auto__){
var self__ = this;
var this__5336__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"w","w",354169001),self__.w,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"h","h",1109658740),self__.h,null))], null),self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5322__auto__,G__31002){
var self__ = this;
var this__5322__auto____$1 = this;
return (new shadow.dom.Size(self__.w,self__.h,G__31002,self__.__extmap,self__.__hash));
}));

(shadow.dom.Size.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__5328__auto__,entry__5329__auto__){
var self__ = this;
var this__5328__auto____$1 = this;
if(cljs.core.vector_QMARK_(entry__5329__auto__)){
return this__5328__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth(entry__5329__auto__,(0)),cljs.core._nth(entry__5329__auto__,(1)));
} else {
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(cljs.core._conj,this__5328__auto____$1,entry__5329__auto__);
}
}));

(shadow.dom.Size.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"w","w",1994700528,null),new cljs.core.Symbol(null,"h","h",-1544777029,null)], null);
}));

(shadow.dom.Size.cljs$lang$type = true);

(shadow.dom.Size.cljs$lang$ctorPrSeq = (function (this__5369__auto__){
return (new cljs.core.List(null,"shadow.dom/Size",null,(1),null));
}));

(shadow.dom.Size.cljs$lang$ctorPrWriter = (function (this__5369__auto__,writer__5370__auto__){
return cljs.core._write(writer__5370__auto__,"shadow.dom/Size");
}));

/**
 * Positional factory function for shadow.dom/Size.
 */
shadow.dom.__GT_Size = (function shadow$dom$__GT_Size(w,h){
return (new shadow.dom.Size(w,h,null,null,null));
});

/**
 * Factory function for shadow.dom/Size, taking a map of keywords to field values.
 */
shadow.dom.map__GT_Size = (function shadow$dom$map__GT_Size(G__31007){
var extmap__5365__auto__ = (function (){var G__31114 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__31007,new cljs.core.Keyword(null,"w","w",354169001),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"h","h",1109658740)], 0));
if(cljs.core.record_QMARK_(G__31007)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__31114);
} else {
return G__31114;
}
})();
return (new shadow.dom.Size(new cljs.core.Keyword(null,"w","w",354169001).cljs$core$IFn$_invoke$arity$1(G__31007),new cljs.core.Keyword(null,"h","h",1109658740).cljs$core$IFn$_invoke$arity$1(G__31007),null,cljs.core.not_empty(extmap__5365__auto__),null));
});

shadow.dom.size__GT_clj = (function shadow$dom$size__GT_clj(size){
return (new shadow.dom.Size(size.width,size.height,null,null,null));
});
shadow.dom.get_size = (function shadow$dom$get_size(el){
return shadow.dom.size__GT_clj(goog.style.getSize(shadow.dom.dom_node(el)));
});
shadow.dom.get_height = (function shadow$dom$get_height(el){
return shadow.dom.get_size(el).h;
});
shadow.dom.get_viewport_size = (function shadow$dom$get_viewport_size(){
return shadow.dom.size__GT_clj(goog.dom.getViewportSize());
});
shadow.dom.first_child = (function shadow$dom$first_child(el){
return (shadow.dom.dom_node(el).children[(0)]);
});
shadow.dom.select_option_values = (function shadow$dom$select_option_values(el){
var native$ = shadow.dom.dom_node(el);
var opts = (native$["options"]);
var a__5613__auto__ = opts;
var l__5614__auto__ = a__5613__auto__.length;
var i = (0);
var ret = cljs.core.PersistentVector.EMPTY;
while(true){
if((i < l__5614__auto__)){
var G__32054 = (i + (1));
var G__32055 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,(opts[i]["value"]));
i = G__32054;
ret = G__32055;
continue;
} else {
return ret;
}
break;
}
});
shadow.dom.build_url = (function shadow$dom$build_url(path,query_params){
if(cljs.core.empty_QMARK_(query_params)){
return path;
} else {
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(path),"?",clojure.string.join.cljs$core$IFn$_invoke$arity$2("&",cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p__31184){
var vec__31185 = p__31184;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31185,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31185,(1),null);
return [cljs.core.name(k),"=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(encodeURIComponent(cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)))].join('');
}),query_params))].join('');
}
});
shadow.dom.redirect = (function shadow$dom$redirect(var_args){
var G__31194 = arguments.length;
switch (G__31194) {
case 1:
return shadow.dom.redirect.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.redirect.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(shadow.dom.redirect.cljs$core$IFn$_invoke$arity$1 = (function (path){
return shadow.dom.redirect.cljs$core$IFn$_invoke$arity$2(path,cljs.core.PersistentArrayMap.EMPTY);
}));

(shadow.dom.redirect.cljs$core$IFn$_invoke$arity$2 = (function (path,query_params){
return (document["location"]["href"] = shadow.dom.build_url(path,query_params));
}));

(shadow.dom.redirect.cljs$lang$maxFixedArity = 2);

shadow.dom.reload_BANG_ = (function shadow$dom$reload_BANG_(){
return (document.location.href = document.location.href);
});
shadow.dom.tag_name = (function shadow$dom$tag_name(el){
var dom = shadow.dom.dom_node(el);
return dom.tagName;
});
shadow.dom.insert_after = (function shadow$dom$insert_after(ref,new$){
var new_node = shadow.dom.dom_node(new$);
goog.dom.insertSiblingAfter(new_node,shadow.dom.dom_node(ref));

return new_node;
});
shadow.dom.insert_before = (function shadow$dom$insert_before(ref,new$){
var new_node = shadow.dom.dom_node(new$);
goog.dom.insertSiblingBefore(new_node,shadow.dom.dom_node(ref));

return new_node;
});
shadow.dom.insert_first = (function shadow$dom$insert_first(ref,new$){
var temp__5823__auto__ = shadow.dom.dom_node(ref).firstChild;
if(cljs.core.truth_(temp__5823__auto__)){
var child = temp__5823__auto__;
return shadow.dom.insert_before(child,new$);
} else {
return shadow.dom.append.cljs$core$IFn$_invoke$arity$2(ref,new$);
}
});
shadow.dom.index_of = (function shadow$dom$index_of(el){
var el__$1 = shadow.dom.dom_node(el);
var i = (0);
while(true){
var ps = el__$1.previousSibling;
if((ps == null)){
return i;
} else {
var G__32080 = ps;
var G__32081 = (i + (1));
el__$1 = G__32080;
i = G__32081;
continue;
}
break;
}
});
shadow.dom.get_parent = (function shadow$dom$get_parent(el){
return goog.dom.getParentElement(shadow.dom.dom_node(el));
});
shadow.dom.parents = (function shadow$dom$parents(el){
var parent = shadow.dom.get_parent(el);
if(cljs.core.truth_(parent)){
return cljs.core.cons(parent,(new cljs.core.LazySeq(null,(function (){
return (shadow.dom.parents.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.parents.cljs$core$IFn$_invoke$arity$1(parent) : shadow.dom.parents.call(null,parent));
}),null,null)));
} else {
return null;
}
});
shadow.dom.matches = (function shadow$dom$matches(el,sel){
return shadow.dom.dom_node(el).matches(sel);
});
shadow.dom.get_next_sibling = (function shadow$dom$get_next_sibling(el){
return goog.dom.getNextElementSibling(shadow.dom.dom_node(el));
});
shadow.dom.get_previous_sibling = (function shadow$dom$get_previous_sibling(el){
return goog.dom.getPreviousElementSibling(shadow.dom.dom_node(el));
});
shadow.dom.xmlns = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(new cljs.core.PersistentArrayMap(null, 2, ["svg","http://www.w3.org/2000/svg","xlink","http://www.w3.org/1999/xlink"], null));
shadow.dom.create_svg_node = (function shadow$dom$create_svg_node(tag_def,props){
var vec__31218 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31218,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31218,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31218,(2),null);
var el = document.createElementNS("http://www.w3.org/2000/svg",tag_name);
if(cljs.core.truth_(tag_id)){
el.setAttribute("id",tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
el.setAttribute("class",shadow.dom.merge_class_string(new cljs.core.Keyword(null,"class","class",-2030961996).cljs$core$IFn$_invoke$arity$1(props),tag_classes));
} else {
}

var seq__31223_32098 = cljs.core.seq(props);
var chunk__31224_32099 = null;
var count__31225_32100 = (0);
var i__31226_32101 = (0);
while(true){
if((i__31226_32101 < count__31225_32100)){
var vec__31236_32103 = chunk__31224_32099.cljs$core$IIndexed$_nth$arity$2(null,i__31226_32101);
var k_32104 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31236_32103,(0),null);
var v_32105 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31236_32103,(1),null);
el.setAttributeNS((function (){var temp__5825__auto__ = cljs.core.namespace(k_32104);
if(cljs.core.truth_(temp__5825__auto__)){
var ns = temp__5825__auto__;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_32104),v_32105);


var G__32106 = seq__31223_32098;
var G__32107 = chunk__31224_32099;
var G__32108 = count__31225_32100;
var G__32109 = (i__31226_32101 + (1));
seq__31223_32098 = G__32106;
chunk__31224_32099 = G__32107;
count__31225_32100 = G__32108;
i__31226_32101 = G__32109;
continue;
} else {
var temp__5825__auto___32110 = cljs.core.seq(seq__31223_32098);
if(temp__5825__auto___32110){
var seq__31223_32111__$1 = temp__5825__auto___32110;
if(cljs.core.chunked_seq_QMARK_(seq__31223_32111__$1)){
var c__5548__auto___32113 = cljs.core.chunk_first(seq__31223_32111__$1);
var G__32114 = cljs.core.chunk_rest(seq__31223_32111__$1);
var G__32115 = c__5548__auto___32113;
var G__32116 = cljs.core.count(c__5548__auto___32113);
var G__32117 = (0);
seq__31223_32098 = G__32114;
chunk__31224_32099 = G__32115;
count__31225_32100 = G__32116;
i__31226_32101 = G__32117;
continue;
} else {
var vec__31244_32118 = cljs.core.first(seq__31223_32111__$1);
var k_32119 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31244_32118,(0),null);
var v_32120 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31244_32118,(1),null);
el.setAttributeNS((function (){var temp__5825__auto____$1 = cljs.core.namespace(k_32119);
if(cljs.core.truth_(temp__5825__auto____$1)){
var ns = temp__5825__auto____$1;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_32119),v_32120);


var G__32124 = cljs.core.next(seq__31223_32111__$1);
var G__32125 = null;
var G__32126 = (0);
var G__32127 = (0);
seq__31223_32098 = G__32124;
chunk__31224_32099 = G__32125;
count__31225_32100 = G__32126;
i__31226_32101 = G__32127;
continue;
}
} else {
}
}
break;
}

return el;
});
shadow.dom.svg_node = (function shadow$dom$svg_node(el){
if((el == null)){
return null;
} else {
if((((!((el == null))))?((((false) || ((cljs.core.PROTOCOL_SENTINEL === el.shadow$dom$SVGElement$))))?true:false):false)){
return el.shadow$dom$SVGElement$_to_svg$arity$1(null);
} else {
return el;

}
}
});
shadow.dom.make_svg_node = (function shadow$dom$make_svg_node(structure){
var vec__31252 = shadow.dom.destructure_node(shadow.dom.create_svg_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31252,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31252,(1),null);
var seq__31256_32129 = cljs.core.seq(node_children);
var chunk__31258_32130 = null;
var count__31259_32131 = (0);
var i__31260_32132 = (0);
while(true){
if((i__31260_32132 < count__31259_32131)){
var child_struct_32133 = chunk__31258_32130.cljs$core$IIndexed$_nth$arity$2(null,i__31260_32132);
if((!((child_struct_32133 == null)))){
if(typeof child_struct_32133 === 'string'){
var text_32134 = (node["textContent"]);
(node["textContent"] = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_32134),child_struct_32133].join(''));
} else {
var children_32135 = shadow.dom.svg_node(child_struct_32133);
if(cljs.core.seq_QMARK_(children_32135)){
var seq__31288_32136 = cljs.core.seq(children_32135);
var chunk__31290_32137 = null;
var count__31291_32138 = (0);
var i__31292_32139 = (0);
while(true){
if((i__31292_32139 < count__31291_32138)){
var child_32140 = chunk__31290_32137.cljs$core$IIndexed$_nth$arity$2(null,i__31292_32139);
if(cljs.core.truth_(child_32140)){
node.appendChild(child_32140);


var G__32143 = seq__31288_32136;
var G__32144 = chunk__31290_32137;
var G__32145 = count__31291_32138;
var G__32146 = (i__31292_32139 + (1));
seq__31288_32136 = G__32143;
chunk__31290_32137 = G__32144;
count__31291_32138 = G__32145;
i__31292_32139 = G__32146;
continue;
} else {
var G__32148 = seq__31288_32136;
var G__32149 = chunk__31290_32137;
var G__32150 = count__31291_32138;
var G__32151 = (i__31292_32139 + (1));
seq__31288_32136 = G__32148;
chunk__31290_32137 = G__32149;
count__31291_32138 = G__32150;
i__31292_32139 = G__32151;
continue;
}
} else {
var temp__5825__auto___32153 = cljs.core.seq(seq__31288_32136);
if(temp__5825__auto___32153){
var seq__31288_32154__$1 = temp__5825__auto___32153;
if(cljs.core.chunked_seq_QMARK_(seq__31288_32154__$1)){
var c__5548__auto___32155 = cljs.core.chunk_first(seq__31288_32154__$1);
var G__32156 = cljs.core.chunk_rest(seq__31288_32154__$1);
var G__32157 = c__5548__auto___32155;
var G__32158 = cljs.core.count(c__5548__auto___32155);
var G__32159 = (0);
seq__31288_32136 = G__32156;
chunk__31290_32137 = G__32157;
count__31291_32138 = G__32158;
i__31292_32139 = G__32159;
continue;
} else {
var child_32160 = cljs.core.first(seq__31288_32154__$1);
if(cljs.core.truth_(child_32160)){
node.appendChild(child_32160);


var G__32161 = cljs.core.next(seq__31288_32154__$1);
var G__32162 = null;
var G__32163 = (0);
var G__32164 = (0);
seq__31288_32136 = G__32161;
chunk__31290_32137 = G__32162;
count__31291_32138 = G__32163;
i__31292_32139 = G__32164;
continue;
} else {
var G__32165 = cljs.core.next(seq__31288_32154__$1);
var G__32166 = null;
var G__32167 = (0);
var G__32168 = (0);
seq__31288_32136 = G__32165;
chunk__31290_32137 = G__32166;
count__31291_32138 = G__32167;
i__31292_32139 = G__32168;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_32135);
}
}


var G__32169 = seq__31256_32129;
var G__32170 = chunk__31258_32130;
var G__32171 = count__31259_32131;
var G__32172 = (i__31260_32132 + (1));
seq__31256_32129 = G__32169;
chunk__31258_32130 = G__32170;
count__31259_32131 = G__32171;
i__31260_32132 = G__32172;
continue;
} else {
var G__32174 = seq__31256_32129;
var G__32175 = chunk__31258_32130;
var G__32176 = count__31259_32131;
var G__32177 = (i__31260_32132 + (1));
seq__31256_32129 = G__32174;
chunk__31258_32130 = G__32175;
count__31259_32131 = G__32176;
i__31260_32132 = G__32177;
continue;
}
} else {
var temp__5825__auto___32178 = cljs.core.seq(seq__31256_32129);
if(temp__5825__auto___32178){
var seq__31256_32179__$1 = temp__5825__auto___32178;
if(cljs.core.chunked_seq_QMARK_(seq__31256_32179__$1)){
var c__5548__auto___32180 = cljs.core.chunk_first(seq__31256_32179__$1);
var G__32185 = cljs.core.chunk_rest(seq__31256_32179__$1);
var G__32186 = c__5548__auto___32180;
var G__32187 = cljs.core.count(c__5548__auto___32180);
var G__32188 = (0);
seq__31256_32129 = G__32185;
chunk__31258_32130 = G__32186;
count__31259_32131 = G__32187;
i__31260_32132 = G__32188;
continue;
} else {
var child_struct_32189 = cljs.core.first(seq__31256_32179__$1);
if((!((child_struct_32189 == null)))){
if(typeof child_struct_32189 === 'string'){
var text_32190 = (node["textContent"]);
(node["textContent"] = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_32190),child_struct_32189].join(''));
} else {
var children_32194 = shadow.dom.svg_node(child_struct_32189);
if(cljs.core.seq_QMARK_(children_32194)){
var seq__31303_32195 = cljs.core.seq(children_32194);
var chunk__31305_32196 = null;
var count__31306_32197 = (0);
var i__31307_32198 = (0);
while(true){
if((i__31307_32198 < count__31306_32197)){
var child_32203 = chunk__31305_32196.cljs$core$IIndexed$_nth$arity$2(null,i__31307_32198);
if(cljs.core.truth_(child_32203)){
node.appendChild(child_32203);


var G__32204 = seq__31303_32195;
var G__32205 = chunk__31305_32196;
var G__32206 = count__31306_32197;
var G__32207 = (i__31307_32198 + (1));
seq__31303_32195 = G__32204;
chunk__31305_32196 = G__32205;
count__31306_32197 = G__32206;
i__31307_32198 = G__32207;
continue;
} else {
var G__32208 = seq__31303_32195;
var G__32209 = chunk__31305_32196;
var G__32210 = count__31306_32197;
var G__32211 = (i__31307_32198 + (1));
seq__31303_32195 = G__32208;
chunk__31305_32196 = G__32209;
count__31306_32197 = G__32210;
i__31307_32198 = G__32211;
continue;
}
} else {
var temp__5825__auto___32214__$1 = cljs.core.seq(seq__31303_32195);
if(temp__5825__auto___32214__$1){
var seq__31303_32215__$1 = temp__5825__auto___32214__$1;
if(cljs.core.chunked_seq_QMARK_(seq__31303_32215__$1)){
var c__5548__auto___32217 = cljs.core.chunk_first(seq__31303_32215__$1);
var G__32218 = cljs.core.chunk_rest(seq__31303_32215__$1);
var G__32219 = c__5548__auto___32217;
var G__32220 = cljs.core.count(c__5548__auto___32217);
var G__32221 = (0);
seq__31303_32195 = G__32218;
chunk__31305_32196 = G__32219;
count__31306_32197 = G__32220;
i__31307_32198 = G__32221;
continue;
} else {
var child_32222 = cljs.core.first(seq__31303_32215__$1);
if(cljs.core.truth_(child_32222)){
node.appendChild(child_32222);


var G__32223 = cljs.core.next(seq__31303_32215__$1);
var G__32224 = null;
var G__32225 = (0);
var G__32226 = (0);
seq__31303_32195 = G__32223;
chunk__31305_32196 = G__32224;
count__31306_32197 = G__32225;
i__31307_32198 = G__32226;
continue;
} else {
var G__32227 = cljs.core.next(seq__31303_32215__$1);
var G__32228 = null;
var G__32229 = (0);
var G__32230 = (0);
seq__31303_32195 = G__32227;
chunk__31305_32196 = G__32228;
count__31306_32197 = G__32229;
i__31307_32198 = G__32230;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_32194);
}
}


var G__32231 = cljs.core.next(seq__31256_32179__$1);
var G__32232 = null;
var G__32233 = (0);
var G__32234 = (0);
seq__31256_32129 = G__32231;
chunk__31258_32130 = G__32232;
count__31259_32131 = G__32233;
i__31260_32132 = G__32234;
continue;
} else {
var G__32235 = cljs.core.next(seq__31256_32179__$1);
var G__32236 = null;
var G__32237 = (0);
var G__32238 = (0);
seq__31256_32129 = G__32235;
chunk__31258_32130 = G__32236;
count__31259_32131 = G__32237;
i__31260_32132 = G__32238;
continue;
}
}
} else {
}
}
break;
}

return node;
});
(shadow.dom.SVGElement["string"] = true);

(shadow.dom._to_svg["string"] = (function (this$){
if((this$ instanceof cljs.core.Keyword)){
return shadow.dom.make_svg_node(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [this$], null));
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("strings cannot be in svgs",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"this","this",-611633625),this$], null));
}
}));

(cljs.core.PersistentVector.prototype.shadow$dom$SVGElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.PersistentVector.prototype.shadow$dom$SVGElement$_to_svg$arity$1 = (function (this$){
var this$__$1 = this;
return shadow.dom.make_svg_node(this$__$1);
}));

(cljs.core.LazySeq.prototype.shadow$dom$SVGElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.LazySeq.prototype.shadow$dom$SVGElement$_to_svg$arity$1 = (function (this$){
var this$__$1 = this;
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom._to_svg,this$__$1);
}));

(shadow.dom.SVGElement["null"] = true);

(shadow.dom._to_svg["null"] = (function (_){
return null;
}));
shadow.dom.svg = (function shadow$dom$svg(var_args){
var args__5755__auto__ = [];
var len__5749__auto___32248 = arguments.length;
var i__5750__auto___32250 = (0);
while(true){
if((i__5750__auto___32250 < len__5749__auto___32248)){
args__5755__auto__.push((arguments[i__5750__auto___32250]));

var G__32251 = (i__5750__auto___32250 + (1));
i__5750__auto___32250 = G__32251;
continue;
} else {
}
break;
}

var argseq__5756__auto__ = ((((1) < args__5755__auto__.length))?(new cljs.core.IndexedSeq(args__5755__auto__.slice((1)),(0),null)):null);
return shadow.dom.svg.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5756__auto__);
});

(shadow.dom.svg.cljs$core$IFn$_invoke$arity$variadic = (function (attrs,children){
return shadow.dom._to_svg(cljs.core.vec(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"svg","svg",856789142),attrs], null),children)));
}));

(shadow.dom.svg.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(shadow.dom.svg.cljs$lang$applyTo = (function (seq31327){
var G__31328 = cljs.core.first(seq31327);
var seq31327__$1 = cljs.core.next(seq31327);
var self__5734__auto__ = this;
return self__5734__auto__.cljs$core$IFn$_invoke$arity$variadic(G__31328,seq31327__$1);
}));


//# sourceMappingURL=shadow.dom.js.map

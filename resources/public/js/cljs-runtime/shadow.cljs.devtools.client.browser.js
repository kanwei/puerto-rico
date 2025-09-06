goog.provide('shadow.cljs.devtools.client.browser');
shadow.cljs.devtools.client.browser.devtools_msg = (function shadow$cljs$devtools$client$browser$devtools_msg(var_args){
var args__5755__auto__ = [];
var len__5749__auto___38250 = arguments.length;
var i__5750__auto___38251 = (0);
while(true){
if((i__5750__auto___38251 < len__5749__auto___38250)){
args__5755__auto__.push((arguments[i__5750__auto___38251]));

var G__38252 = (i__5750__auto___38251 + (1));
i__5750__auto___38251 = G__38252;
continue;
} else {
}
break;
}

var argseq__5756__auto__ = ((((1) < args__5755__auto__.length))?(new cljs.core.IndexedSeq(args__5755__auto__.slice((1)),(0),null)):null);
return shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5756__auto__);
});

(shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic = (function (msg,args){
if(shadow.cljs.devtools.client.env.log){
if(cljs.core.seq(shadow.cljs.devtools.client.env.log_style)){
return console.log.apply(console,cljs.core.into_array.cljs$core$IFn$_invoke$arity$1(cljs.core.into.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [["%cshadow-cljs: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg)].join(''),shadow.cljs.devtools.client.env.log_style], null),args)));
} else {
return console.log.apply(console,cljs.core.into_array.cljs$core$IFn$_invoke$arity$1(cljs.core.into.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [["shadow-cljs: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg)].join('')], null),args)));
}
} else {
return null;
}
}));

(shadow.cljs.devtools.client.browser.devtools_msg.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(shadow.cljs.devtools.client.browser.devtools_msg.cljs$lang$applyTo = (function (seq37821){
var G__37822 = cljs.core.first(seq37821);
var seq37821__$1 = cljs.core.next(seq37821);
var self__5734__auto__ = this;
return self__5734__auto__.cljs$core$IFn$_invoke$arity$variadic(G__37822,seq37821__$1);
}));

shadow.cljs.devtools.client.browser.script_eval = (function shadow$cljs$devtools$client$browser$script_eval(code){
return goog.globalEval(code);
});
shadow.cljs.devtools.client.browser.do_js_load = (function shadow$cljs$devtools$client$browser$do_js_load(sources){
var seq__37836 = cljs.core.seq(sources);
var chunk__37837 = null;
var count__37838 = (0);
var i__37839 = (0);
while(true){
if((i__37839 < count__37838)){
var map__37844 = chunk__37837.cljs$core$IIndexed$_nth$arity$2(null,i__37839);
var map__37844__$1 = cljs.core.__destructure_map(map__37844);
var src = map__37844__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37844__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37844__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37844__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37844__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval([cljs.core.str.cljs$core$IFn$_invoke$arity$1(js),"\n//# sourceURL=",cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase),cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)].join(''));
}catch (e37845){var e_38254 = e37845;
if(shadow.cljs.devtools.client.env.log){
console.error(["Failed to load ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)].join(''),e_38254);
} else {
}

throw (new Error(["Failed to load ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name),": ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_38254.message)].join('')));
}

var G__38255 = seq__37836;
var G__38256 = chunk__37837;
var G__38257 = count__37838;
var G__38258 = (i__37839 + (1));
seq__37836 = G__38255;
chunk__37837 = G__38256;
count__37838 = G__38257;
i__37839 = G__38258;
continue;
} else {
var temp__5825__auto__ = cljs.core.seq(seq__37836);
if(temp__5825__auto__){
var seq__37836__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__37836__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__37836__$1);
var G__38259 = cljs.core.chunk_rest(seq__37836__$1);
var G__38260 = c__5548__auto__;
var G__38261 = cljs.core.count(c__5548__auto__);
var G__38262 = (0);
seq__37836 = G__38259;
chunk__37837 = G__38260;
count__37838 = G__38261;
i__37839 = G__38262;
continue;
} else {
var map__37846 = cljs.core.first(seq__37836__$1);
var map__37846__$1 = cljs.core.__destructure_map(map__37846);
var src = map__37846__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37846__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37846__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37846__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37846__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval([cljs.core.str.cljs$core$IFn$_invoke$arity$1(js),"\n//# sourceURL=",cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase),cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)].join(''));
}catch (e37849){var e_38263 = e37849;
if(shadow.cljs.devtools.client.env.log){
console.error(["Failed to load ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)].join(''),e_38263);
} else {
}

throw (new Error(["Failed to load ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name),": ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_38263.message)].join('')));
}

var G__38264 = cljs.core.next(seq__37836__$1);
var G__38265 = null;
var G__38266 = (0);
var G__38267 = (0);
seq__37836 = G__38264;
chunk__37837 = G__38265;
count__37838 = G__38266;
i__37839 = G__38267;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.cljs.devtools.client.browser.do_js_reload = (function shadow$cljs$devtools$client$browser$do_js_reload(msg,sources,complete_fn,failure_fn){
return shadow.cljs.devtools.client.env.do_js_reload.cljs$core$IFn$_invoke$arity$4(cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(msg,new cljs.core.Keyword(null,"log-missing-fn","log-missing-fn",732676765),(function (fn_sym){
return null;
}),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"log-call-async","log-call-async",183826192),(function (fn_sym){
return shadow.cljs.devtools.client.browser.devtools_msg(["call async ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym)].join(''));
}),new cljs.core.Keyword(null,"log-call","log-call",412404391),(function (fn_sym){
return shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym)].join(''));
})], 0)),(function (next){
shadow.cljs.devtools.client.browser.do_js_load(sources);

return (next.cljs$core$IFn$_invoke$arity$0 ? next.cljs$core$IFn$_invoke$arity$0() : next.call(null));
}),complete_fn,failure_fn);
});
/**
 * when (require '["some-str" :as x]) is done at the REPL we need to manually call the shadow.js.require for it
 * since the file only adds the shadow$provide. only need to do this for shadow-js.
 */
shadow.cljs.devtools.client.browser.do_js_requires = (function shadow$cljs$devtools$client$browser$do_js_requires(js_requires){
var seq__37852 = cljs.core.seq(js_requires);
var chunk__37853 = null;
var count__37854 = (0);
var i__37855 = (0);
while(true){
if((i__37855 < count__37854)){
var js_ns = chunk__37853.cljs$core$IIndexed$_nth$arity$2(null,i__37855);
var require_str_38268 = ["var ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)," = shadow.js.require(\"",cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns),"\");"].join('');
shadow.cljs.devtools.client.browser.script_eval(require_str_38268);


var G__38270 = seq__37852;
var G__38271 = chunk__37853;
var G__38272 = count__37854;
var G__38273 = (i__37855 + (1));
seq__37852 = G__38270;
chunk__37853 = G__38271;
count__37854 = G__38272;
i__37855 = G__38273;
continue;
} else {
var temp__5825__auto__ = cljs.core.seq(seq__37852);
if(temp__5825__auto__){
var seq__37852__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__37852__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__37852__$1);
var G__38274 = cljs.core.chunk_rest(seq__37852__$1);
var G__38275 = c__5548__auto__;
var G__38276 = cljs.core.count(c__5548__auto__);
var G__38277 = (0);
seq__37852 = G__38274;
chunk__37853 = G__38275;
count__37854 = G__38276;
i__37855 = G__38277;
continue;
} else {
var js_ns = cljs.core.first(seq__37852__$1);
var require_str_38278 = ["var ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)," = shadow.js.require(\"",cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns),"\");"].join('');
shadow.cljs.devtools.client.browser.script_eval(require_str_38278);


var G__38279 = cljs.core.next(seq__37852__$1);
var G__38280 = null;
var G__38281 = (0);
var G__38282 = (0);
seq__37852 = G__38279;
chunk__37853 = G__38280;
count__37854 = G__38281;
i__37855 = G__38282;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.cljs.devtools.client.browser.handle_build_complete = (function shadow$cljs$devtools$client$browser$handle_build_complete(runtime,p__37859){
var map__37860 = p__37859;
var map__37860__$1 = cljs.core.__destructure_map(map__37860);
var msg = map__37860__$1;
var info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37860__$1,new cljs.core.Keyword(null,"info","info",-317069002));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37860__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var warnings = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.distinct.cljs$core$IFn$_invoke$arity$1((function (){var iter__5503__auto__ = (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__37861(s__37862){
return (new cljs.core.LazySeq(null,(function (){
var s__37862__$1 = s__37862;
while(true){
var temp__5825__auto__ = cljs.core.seq(s__37862__$1);
if(temp__5825__auto__){
var xs__6385__auto__ = temp__5825__auto__;
var map__37867 = cljs.core.first(xs__6385__auto__);
var map__37867__$1 = cljs.core.__destructure_map(map__37867);
var src = map__37867__$1;
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37867__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var warnings = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37867__$1,new cljs.core.Keyword(null,"warnings","warnings",-735437651));
if(cljs.core.not(new cljs.core.Keyword(null,"from-jar","from-jar",1050932827).cljs$core$IFn$_invoke$arity$1(src))){
var iterys__5499__auto__ = ((function (s__37862__$1,map__37867,map__37867__$1,src,resource_name,warnings,xs__6385__auto__,temp__5825__auto__,map__37860,map__37860__$1,msg,info,reload_info){
return (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__37861_$_iter__37863(s__37864){
return (new cljs.core.LazySeq(null,((function (s__37862__$1,map__37867,map__37867__$1,src,resource_name,warnings,xs__6385__auto__,temp__5825__auto__,map__37860,map__37860__$1,msg,info,reload_info){
return (function (){
var s__37864__$1 = s__37864;
while(true){
var temp__5825__auto____$1 = cljs.core.seq(s__37864__$1);
if(temp__5825__auto____$1){
var s__37864__$2 = temp__5825__auto____$1;
if(cljs.core.chunked_seq_QMARK_(s__37864__$2)){
var c__5501__auto__ = cljs.core.chunk_first(s__37864__$2);
var size__5502__auto__ = cljs.core.count(c__5501__auto__);
var b__37866 = cljs.core.chunk_buffer(size__5502__auto__);
if((function (){var i__37865 = (0);
while(true){
if((i__37865 < size__5502__auto__)){
var warning = cljs.core._nth(c__5501__auto__,i__37865);
cljs.core.chunk_append(b__37866,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name));

var G__38284 = (i__37865 + (1));
i__37865 = G__38284;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__37866),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__37861_$_iter__37863(cljs.core.chunk_rest(s__37864__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__37866),null);
}
} else {
var warning = cljs.core.first(s__37864__$2);
return cljs.core.cons(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__37861_$_iter__37863(cljs.core.rest(s__37864__$2)));
}
} else {
return null;
}
break;
}
});})(s__37862__$1,map__37867,map__37867__$1,src,resource_name,warnings,xs__6385__auto__,temp__5825__auto__,map__37860,map__37860__$1,msg,info,reload_info))
,null,null));
});})(s__37862__$1,map__37867,map__37867__$1,src,resource_name,warnings,xs__6385__auto__,temp__5825__auto__,map__37860,map__37860__$1,msg,info,reload_info))
;
var fs__5500__auto__ = cljs.core.seq(iterys__5499__auto__(warnings));
if(fs__5500__auto__){
return cljs.core.concat.cljs$core$IFn$_invoke$arity$2(fs__5500__auto__,shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__37861(cljs.core.rest(s__37862__$1)));
} else {
var G__38285 = cljs.core.rest(s__37862__$1);
s__37862__$1 = G__38285;
continue;
}
} else {
var G__38286 = cljs.core.rest(s__37862__$1);
s__37862__$1 = G__38286;
continue;
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5503__auto__(new cljs.core.Keyword(null,"sources","sources",-321166424).cljs$core$IFn$_invoke$arity$1(info));
})()));
if(shadow.cljs.devtools.client.env.log){
var seq__37872_38287 = cljs.core.seq(warnings);
var chunk__37873_38288 = null;
var count__37874_38289 = (0);
var i__37875_38290 = (0);
while(true){
if((i__37875_38290 < count__37874_38289)){
var map__37884_38291 = chunk__37873_38288.cljs$core$IIndexed$_nth$arity$2(null,i__37875_38290);
var map__37884_38292__$1 = cljs.core.__destructure_map(map__37884_38291);
var w_38293 = map__37884_38292__$1;
var msg_38294__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37884_38292__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_38295 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37884_38292__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_38296 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37884_38292__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_38297 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37884_38292__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn(["BUILD-WARNING in ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_38297)," at [",cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_38295),":",cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_38296),"]\n\t",cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_38294__$1)].join(''));


var G__38298 = seq__37872_38287;
var G__38299 = chunk__37873_38288;
var G__38300 = count__37874_38289;
var G__38301 = (i__37875_38290 + (1));
seq__37872_38287 = G__38298;
chunk__37873_38288 = G__38299;
count__37874_38289 = G__38300;
i__37875_38290 = G__38301;
continue;
} else {
var temp__5825__auto___38303 = cljs.core.seq(seq__37872_38287);
if(temp__5825__auto___38303){
var seq__37872_38304__$1 = temp__5825__auto___38303;
if(cljs.core.chunked_seq_QMARK_(seq__37872_38304__$1)){
var c__5548__auto___38305 = cljs.core.chunk_first(seq__37872_38304__$1);
var G__38306 = cljs.core.chunk_rest(seq__37872_38304__$1);
var G__38307 = c__5548__auto___38305;
var G__38308 = cljs.core.count(c__5548__auto___38305);
var G__38309 = (0);
seq__37872_38287 = G__38306;
chunk__37873_38288 = G__38307;
count__37874_38289 = G__38308;
i__37875_38290 = G__38309;
continue;
} else {
var map__37885_38310 = cljs.core.first(seq__37872_38304__$1);
var map__37885_38311__$1 = cljs.core.__destructure_map(map__37885_38310);
var w_38312 = map__37885_38311__$1;
var msg_38313__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37885_38311__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_38314 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37885_38311__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_38315 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37885_38311__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_38316 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37885_38311__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn(["BUILD-WARNING in ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_38316)," at [",cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_38314),":",cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_38315),"]\n\t",cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_38313__$1)].join(''));


var G__38317 = cljs.core.next(seq__37872_38304__$1);
var G__38318 = null;
var G__38319 = (0);
var G__38320 = (0);
seq__37872_38287 = G__38317;
chunk__37873_38288 = G__38318;
count__37874_38289 = G__38319;
i__37875_38290 = G__38320;
continue;
}
} else {
}
}
break;
}
} else {
}

if((!(shadow.cljs.devtools.client.env.autoload))){
return shadow.cljs.devtools.client.hud.load_end_success();
} else {
if(((cljs.core.empty_QMARK_(warnings)) || (shadow.cljs.devtools.client.env.ignore_warnings))){
var sources_to_get = shadow.cljs.devtools.client.env.filter_reload_sources(info,reload_info);
if(cljs.core.not(cljs.core.seq(sources_to_get))){
return shadow.cljs.devtools.client.hud.load_end_success();
} else {
if(cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"after-load","after-load",-1278503285)], null)))){
} else {
shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("reloading code but no :after-load hooks are configured!",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["https://shadow-cljs.github.io/docs/UsersGuide.html#_lifecycle_hooks"], 0));
}

return shadow.cljs.devtools.client.shared.load_sources(runtime,sources_to_get,(function (p1__37858_SHARP_){
return shadow.cljs.devtools.client.browser.do_js_reload(msg,p1__37858_SHARP_,shadow.cljs.devtools.client.hud.load_end_success,shadow.cljs.devtools.client.hud.load_failure);
}));
}
} else {
return null;
}
}
});
shadow.cljs.devtools.client.browser.page_load_uri = (cljs.core.truth_(goog.global.document)?goog.Uri.parse(document.location.href):null);
shadow.cljs.devtools.client.browser.match_paths = (function shadow$cljs$devtools$client$browser$match_paths(old,new$){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("file",shadow.cljs.devtools.client.browser.page_load_uri.getScheme())){
var rel_new = cljs.core.subs.cljs$core$IFn$_invoke$arity$2(new$,(1));
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(old,rel_new)) || (clojure.string.starts_with_QMARK_(old,[rel_new,"?"].join(''))))){
return rel_new;
} else {
return null;
}
} else {
var node_uri = goog.Uri.parse(old);
var node_uri_resolved = shadow.cljs.devtools.client.browser.page_load_uri.resolve(node_uri);
var node_abs = node_uri_resolved.getPath();
var and__5023__auto__ = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$1(shadow.cljs.devtools.client.browser.page_load_uri.hasSameDomainAs(node_uri))) || (cljs.core.not(node_uri.hasDomain())));
if(and__5023__auto__){
var and__5023__auto____$1 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(node_abs,new$);
if(and__5023__auto____$1){
return cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var G__37893 = node_uri;
G__37893.setQuery(null);

G__37893.setPath(new$);

return G__37893;
})());
} else {
return and__5023__auto____$1;
}
} else {
return and__5023__auto__;
}
}
});
shadow.cljs.devtools.client.browser.handle_asset_update = (function shadow$cljs$devtools$client$browser$handle_asset_update(p__37894){
var map__37895 = p__37894;
var map__37895__$1 = cljs.core.__destructure_map(map__37895);
var msg = map__37895__$1;
var updates = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37895__$1,new cljs.core.Keyword(null,"updates","updates",2013983452));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__37895__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var seq__37896 = cljs.core.seq(updates);
var chunk__37898 = null;
var count__37899 = (0);
var i__37900 = (0);
while(true){
if((i__37900 < count__37899)){
var path = chunk__37898.cljs$core$IIndexed$_nth$arity$2(null,i__37900);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__38091_38322 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__38095_38323 = null;
var count__38096_38324 = (0);
var i__38097_38325 = (0);
while(true){
if((i__38097_38325 < count__38096_38324)){
var node_38326 = chunk__38095_38323.cljs$core$IIndexed$_nth$arity$2(null,i__38097_38325);
if(cljs.core.not(node_38326.shadow$old)){
var path_match_38327 = shadow.cljs.devtools.client.browser.match_paths(node_38326.getAttribute("href"),path);
if(cljs.core.truth_(path_match_38327)){
var new_link_38328 = (function (){var G__38130 = node_38326.cloneNode(true);
G__38130.setAttribute("href",[cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_38327),"?r=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())].join(''));

return G__38130;
})();
(node_38326.shadow$old = true);

(new_link_38328.onload = ((function (seq__38091_38322,chunk__38095_38323,count__38096_38324,i__38097_38325,seq__37896,chunk__37898,count__37899,i__37900,new_link_38328,path_match_38327,node_38326,path,map__37895,map__37895__$1,msg,updates,reload_info){
return (function (e){
var seq__38131_38329 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__38133_38330 = null;
var count__38134_38331 = (0);
var i__38135_38332 = (0);
while(true){
if((i__38135_38332 < count__38134_38331)){
var map__38139_38333 = chunk__38133_38330.cljs$core$IIndexed$_nth$arity$2(null,i__38135_38332);
var map__38139_38334__$1 = cljs.core.__destructure_map(map__38139_38333);
var task_38335 = map__38139_38334__$1;
var fn_str_38336 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38139_38334__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38337 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38139_38334__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38338 = goog.getObjectByName(fn_str_38336,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38337)].join(''));

(fn_obj_38338.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38338.cljs$core$IFn$_invoke$arity$2(path,new_link_38328) : fn_obj_38338.call(null,path,new_link_38328));


var G__38339 = seq__38131_38329;
var G__38340 = chunk__38133_38330;
var G__38341 = count__38134_38331;
var G__38342 = (i__38135_38332 + (1));
seq__38131_38329 = G__38339;
chunk__38133_38330 = G__38340;
count__38134_38331 = G__38341;
i__38135_38332 = G__38342;
continue;
} else {
var temp__5825__auto___38343 = cljs.core.seq(seq__38131_38329);
if(temp__5825__auto___38343){
var seq__38131_38344__$1 = temp__5825__auto___38343;
if(cljs.core.chunked_seq_QMARK_(seq__38131_38344__$1)){
var c__5548__auto___38345 = cljs.core.chunk_first(seq__38131_38344__$1);
var G__38346 = cljs.core.chunk_rest(seq__38131_38344__$1);
var G__38347 = c__5548__auto___38345;
var G__38348 = cljs.core.count(c__5548__auto___38345);
var G__38349 = (0);
seq__38131_38329 = G__38346;
chunk__38133_38330 = G__38347;
count__38134_38331 = G__38348;
i__38135_38332 = G__38349;
continue;
} else {
var map__38140_38350 = cljs.core.first(seq__38131_38344__$1);
var map__38140_38351__$1 = cljs.core.__destructure_map(map__38140_38350);
var task_38352 = map__38140_38351__$1;
var fn_str_38353 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38140_38351__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38354 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38140_38351__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38355 = goog.getObjectByName(fn_str_38353,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38354)].join(''));

(fn_obj_38355.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38355.cljs$core$IFn$_invoke$arity$2(path,new_link_38328) : fn_obj_38355.call(null,path,new_link_38328));


var G__38359 = cljs.core.next(seq__38131_38344__$1);
var G__38360 = null;
var G__38361 = (0);
var G__38362 = (0);
seq__38131_38329 = G__38359;
chunk__38133_38330 = G__38360;
count__38134_38331 = G__38361;
i__38135_38332 = G__38362;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_38326);
});})(seq__38091_38322,chunk__38095_38323,count__38096_38324,i__38097_38325,seq__37896,chunk__37898,count__37899,i__37900,new_link_38328,path_match_38327,node_38326,path,map__37895,map__37895__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_38327], 0));

goog.dom.insertSiblingAfter(new_link_38328,node_38326);


var G__38363 = seq__38091_38322;
var G__38364 = chunk__38095_38323;
var G__38365 = count__38096_38324;
var G__38366 = (i__38097_38325 + (1));
seq__38091_38322 = G__38363;
chunk__38095_38323 = G__38364;
count__38096_38324 = G__38365;
i__38097_38325 = G__38366;
continue;
} else {
var G__38367 = seq__38091_38322;
var G__38368 = chunk__38095_38323;
var G__38369 = count__38096_38324;
var G__38370 = (i__38097_38325 + (1));
seq__38091_38322 = G__38367;
chunk__38095_38323 = G__38368;
count__38096_38324 = G__38369;
i__38097_38325 = G__38370;
continue;
}
} else {
var G__38371 = seq__38091_38322;
var G__38372 = chunk__38095_38323;
var G__38373 = count__38096_38324;
var G__38374 = (i__38097_38325 + (1));
seq__38091_38322 = G__38371;
chunk__38095_38323 = G__38372;
count__38096_38324 = G__38373;
i__38097_38325 = G__38374;
continue;
}
} else {
var temp__5825__auto___38375 = cljs.core.seq(seq__38091_38322);
if(temp__5825__auto___38375){
var seq__38091_38376__$1 = temp__5825__auto___38375;
if(cljs.core.chunked_seq_QMARK_(seq__38091_38376__$1)){
var c__5548__auto___38377 = cljs.core.chunk_first(seq__38091_38376__$1);
var G__38378 = cljs.core.chunk_rest(seq__38091_38376__$1);
var G__38379 = c__5548__auto___38377;
var G__38380 = cljs.core.count(c__5548__auto___38377);
var G__38381 = (0);
seq__38091_38322 = G__38378;
chunk__38095_38323 = G__38379;
count__38096_38324 = G__38380;
i__38097_38325 = G__38381;
continue;
} else {
var node_38382 = cljs.core.first(seq__38091_38376__$1);
if(cljs.core.not(node_38382.shadow$old)){
var path_match_38383 = shadow.cljs.devtools.client.browser.match_paths(node_38382.getAttribute("href"),path);
if(cljs.core.truth_(path_match_38383)){
var new_link_38384 = (function (){var G__38141 = node_38382.cloneNode(true);
G__38141.setAttribute("href",[cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_38383),"?r=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())].join(''));

return G__38141;
})();
(node_38382.shadow$old = true);

(new_link_38384.onload = ((function (seq__38091_38322,chunk__38095_38323,count__38096_38324,i__38097_38325,seq__37896,chunk__37898,count__37899,i__37900,new_link_38384,path_match_38383,node_38382,seq__38091_38376__$1,temp__5825__auto___38375,path,map__37895,map__37895__$1,msg,updates,reload_info){
return (function (e){
var seq__38142_38385 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__38144_38386 = null;
var count__38145_38387 = (0);
var i__38146_38388 = (0);
while(true){
if((i__38146_38388 < count__38145_38387)){
var map__38156_38389 = chunk__38144_38386.cljs$core$IIndexed$_nth$arity$2(null,i__38146_38388);
var map__38156_38390__$1 = cljs.core.__destructure_map(map__38156_38389);
var task_38391 = map__38156_38390__$1;
var fn_str_38392 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38156_38390__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38393 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38156_38390__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38394 = goog.getObjectByName(fn_str_38392,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38393)].join(''));

(fn_obj_38394.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38394.cljs$core$IFn$_invoke$arity$2(path,new_link_38384) : fn_obj_38394.call(null,path,new_link_38384));


var G__38395 = seq__38142_38385;
var G__38396 = chunk__38144_38386;
var G__38397 = count__38145_38387;
var G__38398 = (i__38146_38388 + (1));
seq__38142_38385 = G__38395;
chunk__38144_38386 = G__38396;
count__38145_38387 = G__38397;
i__38146_38388 = G__38398;
continue;
} else {
var temp__5825__auto___38399__$1 = cljs.core.seq(seq__38142_38385);
if(temp__5825__auto___38399__$1){
var seq__38142_38400__$1 = temp__5825__auto___38399__$1;
if(cljs.core.chunked_seq_QMARK_(seq__38142_38400__$1)){
var c__5548__auto___38401 = cljs.core.chunk_first(seq__38142_38400__$1);
var G__38402 = cljs.core.chunk_rest(seq__38142_38400__$1);
var G__38403 = c__5548__auto___38401;
var G__38404 = cljs.core.count(c__5548__auto___38401);
var G__38405 = (0);
seq__38142_38385 = G__38402;
chunk__38144_38386 = G__38403;
count__38145_38387 = G__38404;
i__38146_38388 = G__38405;
continue;
} else {
var map__38157_38406 = cljs.core.first(seq__38142_38400__$1);
var map__38157_38407__$1 = cljs.core.__destructure_map(map__38157_38406);
var task_38408 = map__38157_38407__$1;
var fn_str_38409 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38157_38407__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38410 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38157_38407__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38411 = goog.getObjectByName(fn_str_38409,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38410)].join(''));

(fn_obj_38411.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38411.cljs$core$IFn$_invoke$arity$2(path,new_link_38384) : fn_obj_38411.call(null,path,new_link_38384));


var G__38412 = cljs.core.next(seq__38142_38400__$1);
var G__38413 = null;
var G__38414 = (0);
var G__38415 = (0);
seq__38142_38385 = G__38412;
chunk__38144_38386 = G__38413;
count__38145_38387 = G__38414;
i__38146_38388 = G__38415;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_38382);
});})(seq__38091_38322,chunk__38095_38323,count__38096_38324,i__38097_38325,seq__37896,chunk__37898,count__37899,i__37900,new_link_38384,path_match_38383,node_38382,seq__38091_38376__$1,temp__5825__auto___38375,path,map__37895,map__37895__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_38383], 0));

goog.dom.insertSiblingAfter(new_link_38384,node_38382);


var G__38416 = cljs.core.next(seq__38091_38376__$1);
var G__38417 = null;
var G__38418 = (0);
var G__38419 = (0);
seq__38091_38322 = G__38416;
chunk__38095_38323 = G__38417;
count__38096_38324 = G__38418;
i__38097_38325 = G__38419;
continue;
} else {
var G__38420 = cljs.core.next(seq__38091_38376__$1);
var G__38421 = null;
var G__38422 = (0);
var G__38423 = (0);
seq__38091_38322 = G__38420;
chunk__38095_38323 = G__38421;
count__38096_38324 = G__38422;
i__38097_38325 = G__38423;
continue;
}
} else {
var G__38424 = cljs.core.next(seq__38091_38376__$1);
var G__38425 = null;
var G__38426 = (0);
var G__38427 = (0);
seq__38091_38322 = G__38424;
chunk__38095_38323 = G__38425;
count__38096_38324 = G__38426;
i__38097_38325 = G__38427;
continue;
}
}
} else {
}
}
break;
}


var G__38428 = seq__37896;
var G__38429 = chunk__37898;
var G__38430 = count__37899;
var G__38431 = (i__37900 + (1));
seq__37896 = G__38428;
chunk__37898 = G__38429;
count__37899 = G__38430;
i__37900 = G__38431;
continue;
} else {
var G__38432 = seq__37896;
var G__38433 = chunk__37898;
var G__38434 = count__37899;
var G__38435 = (i__37900 + (1));
seq__37896 = G__38432;
chunk__37898 = G__38433;
count__37899 = G__38434;
i__37900 = G__38435;
continue;
}
} else {
var temp__5825__auto__ = cljs.core.seq(seq__37896);
if(temp__5825__auto__){
var seq__37896__$1 = temp__5825__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__37896__$1)){
var c__5548__auto__ = cljs.core.chunk_first(seq__37896__$1);
var G__38436 = cljs.core.chunk_rest(seq__37896__$1);
var G__38437 = c__5548__auto__;
var G__38438 = cljs.core.count(c__5548__auto__);
var G__38439 = (0);
seq__37896 = G__38436;
chunk__37898 = G__38437;
count__37899 = G__38438;
i__37900 = G__38439;
continue;
} else {
var path = cljs.core.first(seq__37896__$1);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__38159_38440 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__38163_38441 = null;
var count__38164_38442 = (0);
var i__38165_38443 = (0);
while(true){
if((i__38165_38443 < count__38164_38442)){
var node_38444 = chunk__38163_38441.cljs$core$IIndexed$_nth$arity$2(null,i__38165_38443);
if(cljs.core.not(node_38444.shadow$old)){
var path_match_38445 = shadow.cljs.devtools.client.browser.match_paths(node_38444.getAttribute("href"),path);
if(cljs.core.truth_(path_match_38445)){
var new_link_38446 = (function (){var G__38199 = node_38444.cloneNode(true);
G__38199.setAttribute("href",[cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_38445),"?r=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())].join(''));

return G__38199;
})();
(node_38444.shadow$old = true);

(new_link_38446.onload = ((function (seq__38159_38440,chunk__38163_38441,count__38164_38442,i__38165_38443,seq__37896,chunk__37898,count__37899,i__37900,new_link_38446,path_match_38445,node_38444,path,seq__37896__$1,temp__5825__auto__,map__37895,map__37895__$1,msg,updates,reload_info){
return (function (e){
var seq__38202_38447 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__38204_38448 = null;
var count__38205_38449 = (0);
var i__38206_38450 = (0);
while(true){
if((i__38206_38450 < count__38205_38449)){
var map__38212_38451 = chunk__38204_38448.cljs$core$IIndexed$_nth$arity$2(null,i__38206_38450);
var map__38212_38452__$1 = cljs.core.__destructure_map(map__38212_38451);
var task_38453 = map__38212_38452__$1;
var fn_str_38454 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38212_38452__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38455 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38212_38452__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38456 = goog.getObjectByName(fn_str_38454,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38455)].join(''));

(fn_obj_38456.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38456.cljs$core$IFn$_invoke$arity$2(path,new_link_38446) : fn_obj_38456.call(null,path,new_link_38446));


var G__38457 = seq__38202_38447;
var G__38458 = chunk__38204_38448;
var G__38459 = count__38205_38449;
var G__38460 = (i__38206_38450 + (1));
seq__38202_38447 = G__38457;
chunk__38204_38448 = G__38458;
count__38205_38449 = G__38459;
i__38206_38450 = G__38460;
continue;
} else {
var temp__5825__auto___38461__$1 = cljs.core.seq(seq__38202_38447);
if(temp__5825__auto___38461__$1){
var seq__38202_38462__$1 = temp__5825__auto___38461__$1;
if(cljs.core.chunked_seq_QMARK_(seq__38202_38462__$1)){
var c__5548__auto___38463 = cljs.core.chunk_first(seq__38202_38462__$1);
var G__38464 = cljs.core.chunk_rest(seq__38202_38462__$1);
var G__38465 = c__5548__auto___38463;
var G__38466 = cljs.core.count(c__5548__auto___38463);
var G__38467 = (0);
seq__38202_38447 = G__38464;
chunk__38204_38448 = G__38465;
count__38205_38449 = G__38466;
i__38206_38450 = G__38467;
continue;
} else {
var map__38213_38468 = cljs.core.first(seq__38202_38462__$1);
var map__38213_38469__$1 = cljs.core.__destructure_map(map__38213_38468);
var task_38470 = map__38213_38469__$1;
var fn_str_38471 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38213_38469__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38472 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38213_38469__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38473 = goog.getObjectByName(fn_str_38471,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38472)].join(''));

(fn_obj_38473.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38473.cljs$core$IFn$_invoke$arity$2(path,new_link_38446) : fn_obj_38473.call(null,path,new_link_38446));


var G__38474 = cljs.core.next(seq__38202_38462__$1);
var G__38475 = null;
var G__38476 = (0);
var G__38477 = (0);
seq__38202_38447 = G__38474;
chunk__38204_38448 = G__38475;
count__38205_38449 = G__38476;
i__38206_38450 = G__38477;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_38444);
});})(seq__38159_38440,chunk__38163_38441,count__38164_38442,i__38165_38443,seq__37896,chunk__37898,count__37899,i__37900,new_link_38446,path_match_38445,node_38444,path,seq__37896__$1,temp__5825__auto__,map__37895,map__37895__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_38445], 0));

goog.dom.insertSiblingAfter(new_link_38446,node_38444);


var G__38478 = seq__38159_38440;
var G__38479 = chunk__38163_38441;
var G__38480 = count__38164_38442;
var G__38481 = (i__38165_38443 + (1));
seq__38159_38440 = G__38478;
chunk__38163_38441 = G__38479;
count__38164_38442 = G__38480;
i__38165_38443 = G__38481;
continue;
} else {
var G__38482 = seq__38159_38440;
var G__38483 = chunk__38163_38441;
var G__38484 = count__38164_38442;
var G__38485 = (i__38165_38443 + (1));
seq__38159_38440 = G__38482;
chunk__38163_38441 = G__38483;
count__38164_38442 = G__38484;
i__38165_38443 = G__38485;
continue;
}
} else {
var G__38486 = seq__38159_38440;
var G__38487 = chunk__38163_38441;
var G__38488 = count__38164_38442;
var G__38489 = (i__38165_38443 + (1));
seq__38159_38440 = G__38486;
chunk__38163_38441 = G__38487;
count__38164_38442 = G__38488;
i__38165_38443 = G__38489;
continue;
}
} else {
var temp__5825__auto___38490__$1 = cljs.core.seq(seq__38159_38440);
if(temp__5825__auto___38490__$1){
var seq__38159_38491__$1 = temp__5825__auto___38490__$1;
if(cljs.core.chunked_seq_QMARK_(seq__38159_38491__$1)){
var c__5548__auto___38492 = cljs.core.chunk_first(seq__38159_38491__$1);
var G__38493 = cljs.core.chunk_rest(seq__38159_38491__$1);
var G__38494 = c__5548__auto___38492;
var G__38495 = cljs.core.count(c__5548__auto___38492);
var G__38496 = (0);
seq__38159_38440 = G__38493;
chunk__38163_38441 = G__38494;
count__38164_38442 = G__38495;
i__38165_38443 = G__38496;
continue;
} else {
var node_38497 = cljs.core.first(seq__38159_38491__$1);
if(cljs.core.not(node_38497.shadow$old)){
var path_match_38498 = shadow.cljs.devtools.client.browser.match_paths(node_38497.getAttribute("href"),path);
if(cljs.core.truth_(path_match_38498)){
var new_link_38499 = (function (){var G__38214 = node_38497.cloneNode(true);
G__38214.setAttribute("href",[cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_38498),"?r=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())].join(''));

return G__38214;
})();
(node_38497.shadow$old = true);

(new_link_38499.onload = ((function (seq__38159_38440,chunk__38163_38441,count__38164_38442,i__38165_38443,seq__37896,chunk__37898,count__37899,i__37900,new_link_38499,path_match_38498,node_38497,seq__38159_38491__$1,temp__5825__auto___38490__$1,path,seq__37896__$1,temp__5825__auto__,map__37895,map__37895__$1,msg,updates,reload_info){
return (function (e){
var seq__38215_38500 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__38217_38501 = null;
var count__38218_38502 = (0);
var i__38219_38503 = (0);
while(true){
if((i__38219_38503 < count__38218_38502)){
var map__38224_38504 = chunk__38217_38501.cljs$core$IIndexed$_nth$arity$2(null,i__38219_38503);
var map__38224_38505__$1 = cljs.core.__destructure_map(map__38224_38504);
var task_38506 = map__38224_38505__$1;
var fn_str_38507 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38224_38505__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38508 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38224_38505__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38509 = goog.getObjectByName(fn_str_38507,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38508)].join(''));

(fn_obj_38509.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38509.cljs$core$IFn$_invoke$arity$2(path,new_link_38499) : fn_obj_38509.call(null,path,new_link_38499));


var G__38510 = seq__38215_38500;
var G__38511 = chunk__38217_38501;
var G__38512 = count__38218_38502;
var G__38513 = (i__38219_38503 + (1));
seq__38215_38500 = G__38510;
chunk__38217_38501 = G__38511;
count__38218_38502 = G__38512;
i__38219_38503 = G__38513;
continue;
} else {
var temp__5825__auto___38514__$2 = cljs.core.seq(seq__38215_38500);
if(temp__5825__auto___38514__$2){
var seq__38215_38515__$1 = temp__5825__auto___38514__$2;
if(cljs.core.chunked_seq_QMARK_(seq__38215_38515__$1)){
var c__5548__auto___38516 = cljs.core.chunk_first(seq__38215_38515__$1);
var G__38517 = cljs.core.chunk_rest(seq__38215_38515__$1);
var G__38518 = c__5548__auto___38516;
var G__38519 = cljs.core.count(c__5548__auto___38516);
var G__38520 = (0);
seq__38215_38500 = G__38517;
chunk__38217_38501 = G__38518;
count__38218_38502 = G__38519;
i__38219_38503 = G__38520;
continue;
} else {
var map__38225_38521 = cljs.core.first(seq__38215_38515__$1);
var map__38225_38522__$1 = cljs.core.__destructure_map(map__38225_38521);
var task_38523 = map__38225_38522__$1;
var fn_str_38524 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38225_38522__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_38525 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38225_38522__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_38526 = goog.getObjectByName(fn_str_38524,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg(["call ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_38525)].join(''));

(fn_obj_38526.cljs$core$IFn$_invoke$arity$2 ? fn_obj_38526.cljs$core$IFn$_invoke$arity$2(path,new_link_38499) : fn_obj_38526.call(null,path,new_link_38499));


var G__38527 = cljs.core.next(seq__38215_38515__$1);
var G__38528 = null;
var G__38529 = (0);
var G__38530 = (0);
seq__38215_38500 = G__38527;
chunk__38217_38501 = G__38528;
count__38218_38502 = G__38529;
i__38219_38503 = G__38530;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_38497);
});})(seq__38159_38440,chunk__38163_38441,count__38164_38442,i__38165_38443,seq__37896,chunk__37898,count__37899,i__37900,new_link_38499,path_match_38498,node_38497,seq__38159_38491__$1,temp__5825__auto___38490__$1,path,seq__37896__$1,temp__5825__auto__,map__37895,map__37895__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_38498], 0));

goog.dom.insertSiblingAfter(new_link_38499,node_38497);


var G__38531 = cljs.core.next(seq__38159_38491__$1);
var G__38532 = null;
var G__38533 = (0);
var G__38534 = (0);
seq__38159_38440 = G__38531;
chunk__38163_38441 = G__38532;
count__38164_38442 = G__38533;
i__38165_38443 = G__38534;
continue;
} else {
var G__38535 = cljs.core.next(seq__38159_38491__$1);
var G__38536 = null;
var G__38537 = (0);
var G__38538 = (0);
seq__38159_38440 = G__38535;
chunk__38163_38441 = G__38536;
count__38164_38442 = G__38537;
i__38165_38443 = G__38538;
continue;
}
} else {
var G__38539 = cljs.core.next(seq__38159_38491__$1);
var G__38540 = null;
var G__38541 = (0);
var G__38542 = (0);
seq__38159_38440 = G__38539;
chunk__38163_38441 = G__38540;
count__38164_38442 = G__38541;
i__38165_38443 = G__38542;
continue;
}
}
} else {
}
}
break;
}


var G__38543 = cljs.core.next(seq__37896__$1);
var G__38544 = null;
var G__38545 = (0);
var G__38546 = (0);
seq__37896 = G__38543;
chunk__37898 = G__38544;
count__37899 = G__38545;
i__37900 = G__38546;
continue;
} else {
var G__38547 = cljs.core.next(seq__37896__$1);
var G__38548 = null;
var G__38549 = (0);
var G__38550 = (0);
seq__37896 = G__38547;
chunk__37898 = G__38548;
count__37899 = G__38549;
i__37900 = G__38550;
continue;
}
}
} else {
return null;
}
}
break;
}
});
shadow.cljs.devtools.client.browser.global_eval = (function shadow$cljs$devtools$client$browser$global_eval(js){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("undefined",typeof(module))){
return eval(js);
} else {
return (0,eval)(js);;
}
});
shadow.cljs.devtools.client.browser.runtime_info = (((typeof SHADOW_CONFIG !== 'undefined'))?shadow.json.to_clj.cljs$core$IFn$_invoke$arity$1(SHADOW_CONFIG):null);
shadow.cljs.devtools.client.browser.client_info = cljs.core.merge.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([shadow.cljs.devtools.client.browser.runtime_info,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"host","host",-1558485167),(cljs.core.truth_(goog.global.document)?new cljs.core.Keyword(null,"browser","browser",828191719):new cljs.core.Keyword(null,"browser-worker","browser-worker",1638998282)),new cljs.core.Keyword(null,"user-agent","user-agent",1220426212),[(cljs.core.truth_(goog.userAgent.OPERA)?"Opera":(cljs.core.truth_(goog.userAgent.product.CHROME)?"Chrome":(cljs.core.truth_(goog.userAgent.IE)?"MSIE":(cljs.core.truth_(goog.userAgent.EDGE)?"Edge":(cljs.core.truth_(goog.userAgent.GECKO)?"Firefox":(cljs.core.truth_(goog.userAgent.SAFARI)?"Safari":(cljs.core.truth_(goog.userAgent.WEBKIT)?"Webkit":null)))))))," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.userAgent.VERSION)," [",cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.userAgent.PLATFORM),"]"].join(''),new cljs.core.Keyword(null,"dom","dom",-1236537922),(!((goog.global.document == null)))], null)], 0));
if((typeof shadow !== 'undefined') && (typeof shadow.cljs !== 'undefined') && (typeof shadow.cljs.devtools !== 'undefined') && (typeof shadow.cljs.devtools.client !== 'undefined') && (typeof shadow.cljs.devtools.client.browser !== 'undefined') && (typeof shadow.cljs.devtools.client.browser.ws_was_welcome_ref !== 'undefined')){
} else {
shadow.cljs.devtools.client.browser.ws_was_welcome_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(false);
}
if(((shadow.cljs.devtools.client.env.enabled) && ((shadow.cljs.devtools.client.env.worker_client_id > (0))))){
(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IEvalJS$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IEvalJS$_js_eval$arity$4 = (function (this$,code,success,fail){
var this$__$1 = this;
try{var G__38228 = shadow.cljs.devtools.client.browser.global_eval(code);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__38228) : success.call(null,G__38228));
}catch (e38227){var e = e38227;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$arity$5 = (function (this$,ns,p__38230,success,fail){
var map__38231 = p__38230;
var map__38231__$1 = cljs.core.__destructure_map(map__38231);
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38231__$1,new cljs.core.Keyword(null,"js","js",1768080579));
var this$__$1 = this;
try{var G__38233 = shadow.cljs.devtools.client.browser.global_eval(js);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__38233) : success.call(null,G__38233));
}catch (e38232){var e = e38232;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$arity$4 = (function (runtime,p__38234,done,error){
var map__38235 = p__38234;
var map__38235__$1 = cljs.core.__destructure_map(map__38235);
var repl_sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38235__$1,new cljs.core.Keyword(null,"repl-sources","repl-sources",723867535));
var runtime__$1 = this;
return shadow.cljs.devtools.client.shared.load_sources(runtime__$1,cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2(shadow.cljs.devtools.client.env.src_is_loaded_QMARK_,repl_sources)),(function (sources){
shadow.cljs.devtools.client.browser.do_js_load(sources);

return (done.cljs$core$IFn$_invoke$arity$0 ? done.cljs$core$IFn$_invoke$arity$0() : done.call(null));
}));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$arity$4 = (function (runtime,p__38236,done,error){
var map__38237 = p__38236;
var map__38237__$1 = cljs.core.__destructure_map(map__38237);
var msg = map__38237__$1;
var sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38237__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
var reload_namespaces = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38237__$1,new cljs.core.Keyword(null,"reload-namespaces","reload-namespaces",250210134));
var js_requires = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38237__$1,new cljs.core.Keyword(null,"js-requires","js-requires",-1311472051));
var runtime__$1 = this;
var sources_to_load = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p__38238){
var map__38239 = p__38238;
var map__38239__$1 = cljs.core.__destructure_map(map__38239);
var src = map__38239__$1;
var provides = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38239__$1,new cljs.core.Keyword(null,"provides","provides",-1634397992));
var and__5023__auto__ = shadow.cljs.devtools.client.env.src_is_loaded_QMARK_(src);
if(cljs.core.truth_(and__5023__auto__)){
return cljs.core.not(cljs.core.some(reload_namespaces,provides));
} else {
return and__5023__auto__;
}
}),sources));
if(cljs.core.not(cljs.core.seq(sources_to_load))){
var G__38240 = cljs.core.PersistentVector.EMPTY;
return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(G__38240) : done.call(null,G__38240));
} else {
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3(runtime__$1,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"cljs-load-sources","cljs-load-sources",-1458295962),new cljs.core.Keyword(null,"to","to",192099007),shadow.cljs.devtools.client.env.worker_client_id,new cljs.core.Keyword(null,"sources","sources",-321166424),cljs.core.into.cljs$core$IFn$_invoke$arity$3(cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582)),sources_to_load)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"cljs-sources","cljs-sources",31121610),(function (p__38241){
var map__38242 = p__38241;
var map__38242__$1 = cljs.core.__destructure_map(map__38242);
var msg__$1 = map__38242__$1;
var sources__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38242__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
try{shadow.cljs.devtools.client.browser.do_js_load(sources__$1);

if(cljs.core.seq(js_requires)){
shadow.cljs.devtools.client.browser.do_js_requires(js_requires);
} else {
}

return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(sources_to_load) : done.call(null,sources_to_load));
}catch (e38243){var ex = e38243;
return (error.cljs$core$IFn$_invoke$arity$1 ? error.cljs$core$IFn$_invoke$arity$1(ex) : error.call(null,ex));
}})], null));
}
}));

shadow.cljs.devtools.client.shared.add_plugin_BANG_(new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282),cljs.core.PersistentHashSet.EMPTY,(function (p__38244){
var map__38245 = p__38244;
var map__38245__$1 = cljs.core.__destructure_map(map__38245);
var env = map__38245__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38245__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
var svc = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"runtime","runtime",-1331573996),runtime], null);
shadow.remote.runtime.api.add_extension(runtime,new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"on-welcome","on-welcome",1895317125),(function (){
cljs.core.reset_BANG_(shadow.cljs.devtools.client.browser.ws_was_welcome_ref,true);

shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

shadow.cljs.devtools.client.env.patch_goog_BANG_();

return shadow.cljs.devtools.client.browser.devtools_msg(["#",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"client-id","client-id",-464622140).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(new cljs.core.Keyword(null,"state-ref","state-ref",2127874952).cljs$core$IFn$_invoke$arity$1(runtime))))," ready!"].join(''));
}),new cljs.core.Keyword(null,"on-disconnect","on-disconnect",-809021814),(function (e){
if(cljs.core.truth_(cljs.core.deref(shadow.cljs.devtools.client.browser.ws_was_welcome_ref))){
shadow.cljs.devtools.client.hud.connection_error("The Websocket connection was closed!");

return cljs.core.reset_BANG_(shadow.cljs.devtools.client.browser.ws_was_welcome_ref,false);
} else {
return null;
}
}),new cljs.core.Keyword(null,"on-reconnect","on-reconnect",1239988702),(function (e){
return shadow.cljs.devtools.client.hud.connection_error("Reconnecting ...");
}),new cljs.core.Keyword(null,"ops","ops",1237330063),new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"access-denied","access-denied",959449406),(function (msg){
cljs.core.reset_BANG_(shadow.cljs.devtools.client.browser.ws_was_welcome_ref,false);

return shadow.cljs.devtools.client.hud.connection_error(["Stale Output! Your loaded JS was not produced by the running shadow-cljs instance."," Is the watch for this build running?"].join(''));
}),new cljs.core.Keyword(null,"cljs-asset-update","cljs-asset-update",1224093028),(function (msg){
return shadow.cljs.devtools.client.browser.handle_asset_update(msg);
}),new cljs.core.Keyword(null,"cljs-build-configure","cljs-build-configure",-2089891268),(function (msg){
return null;
}),new cljs.core.Keyword(null,"cljs-build-start","cljs-build-start",-725781241),(function (msg){
shadow.cljs.devtools.client.hud.hud_hide();

shadow.cljs.devtools.client.hud.load_start();

return shadow.cljs.devtools.client.env.run_custom_notify_BANG_(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"build-start","build-start",-959649480)));
}),new cljs.core.Keyword(null,"cljs-build-complete","cljs-build-complete",273626153),(function (msg){
var msg__$1 = shadow.cljs.devtools.client.env.add_warnings_to_info(msg);
shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

shadow.cljs.devtools.client.hud.hud_warnings(msg__$1);

shadow.cljs.devtools.client.browser.handle_build_complete(runtime,msg__$1);

return shadow.cljs.devtools.client.env.run_custom_notify_BANG_(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg__$1,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"build-complete","build-complete",-501868472)));
}),new cljs.core.Keyword(null,"cljs-build-failure","cljs-build-failure",1718154990),(function (msg){
shadow.cljs.devtools.client.hud.load_end();

shadow.cljs.devtools.client.hud.hud_error(msg);

return shadow.cljs.devtools.client.env.run_custom_notify_BANG_(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"build-failure","build-failure",-2107487466)));
}),new cljs.core.Keyword("shadow.cljs.devtools.client.env","worker-notify","shadow.cljs.devtools.client.env/worker-notify",-1456820670),(function (p__38246){
var map__38247 = p__38246;
var map__38247__$1 = cljs.core.__destructure_map(map__38247);
var event_op = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38247__$1,new cljs.core.Keyword(null,"event-op","event-op",200358057));
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38247__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"client-disconnect","client-disconnect",640227957),event_op)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(client_id,shadow.cljs.devtools.client.env.worker_client_id)))){
shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

return shadow.cljs.devtools.client.hud.connection_error("The watch for this build was stopped!");
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"client-connect","client-connect",-1113973888),event_op)){
shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

return shadow.cljs.devtools.client.hud.connection_error("The watch for this build was restarted. Reload required!");
} else {
return null;
}
}
})], null)], null));

return svc;
}),(function (p__38248){
var map__38249 = p__38248;
var map__38249__$1 = cljs.core.__destructure_map(map__38249);
var svc = map__38249__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__38249__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
return shadow.remote.runtime.api.del_extension(runtime,new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282));
}));

shadow.cljs.devtools.client.shared.init_runtime_BANG_(shadow.cljs.devtools.client.browser.client_info,shadow.cljs.devtools.client.websocket.start,shadow.cljs.devtools.client.websocket.send,shadow.cljs.devtools.client.websocket.stop);
} else {
}

//# sourceMappingURL=shadow.cljs.devtools.client.browser.js.map

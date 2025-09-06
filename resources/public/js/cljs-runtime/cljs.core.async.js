goog.provide('cljs.core.async');
goog.scope(function(){
  cljs.core.async.goog$module$goog$array = goog.module.get('goog.array');
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async31204 = (function (f,blockable,meta31205){
this.f = f;
this.blockable = blockable;
this.meta31205 = meta31205;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async31204.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_31206,meta31205__$1){
var self__ = this;
var _31206__$1 = this;
return (new cljs.core.async.t_cljs$core$async31204(self__.f,self__.blockable,meta31205__$1));
}));

(cljs.core.async.t_cljs$core$async31204.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_31206){
var self__ = this;
var _31206__$1 = this;
return self__.meta31205;
}));

(cljs.core.async.t_cljs$core$async31204.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async31204.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async31204.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.blockable;
}));

(cljs.core.async.t_cljs$core$async31204.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.f;
}));

(cljs.core.async.t_cljs$core$async31204.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"blockable","blockable",-28395259,null),new cljs.core.Symbol(null,"meta31205","meta31205",33773525,null)], null);
}));

(cljs.core.async.t_cljs$core$async31204.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async31204.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async31204");

(cljs.core.async.t_cljs$core$async31204.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async31204");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async31204.
 */
cljs.core.async.__GT_t_cljs$core$async31204 = (function cljs$core$async$__GT_t_cljs$core$async31204(f,blockable,meta31205){
return (new cljs.core.async.t_cljs$core$async31204(f,blockable,meta31205));
});


cljs.core.async.fn_handler = (function cljs$core$async$fn_handler(var_args){
var G__31203 = arguments.length;
switch (G__31203) {
case 1:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1 = (function (f){
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2(f,true);
}));

(cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2 = (function (f,blockable){
return (new cljs.core.async.t_cljs$core$async31204(f,blockable,cljs.core.PersistentArrayMap.EMPTY));
}));

(cljs.core.async.fn_handler.cljs$lang$maxFixedArity = 2);

/**
 * Returns a fixed buffer of size n. When full, puts will block/park.
 */
cljs.core.async.buffer = (function cljs$core$async$buffer(n){
return cljs.core.async.impl.buffers.fixed_buffer(n);
});
/**
 * Returns a buffer of size n. When full, puts will complete but
 *   val will be dropped (no transfer).
 */
cljs.core.async.dropping_buffer = (function cljs$core$async$dropping_buffer(n){
return cljs.core.async.impl.buffers.dropping_buffer(n);
});
/**
 * Returns a buffer of size n. When full, puts will complete, and be
 *   buffered, but oldest elements in buffer will be dropped (not
 *   transferred).
 */
cljs.core.async.sliding_buffer = (function cljs$core$async$sliding_buffer(n){
return cljs.core.async.impl.buffers.sliding_buffer(n);
});
/**
 * Returns true if a channel created with buff will never block. That is to say,
 * puts into this buffer will never cause the buffer to be full. 
 */
cljs.core.async.unblocking_buffer_QMARK_ = (function cljs$core$async$unblocking_buffer_QMARK_(buff){
if((!((buff == null)))){
if(((false) || ((cljs.core.PROTOCOL_SENTINEL === buff.cljs$core$async$impl$protocols$UnblockingBuffer$)))){
return true;
} else {
if((!buff.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_(cljs.core.async.impl.protocols.UnblockingBuffer,buff);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_(cljs.core.async.impl.protocols.UnblockingBuffer,buff);
}
});
/**
 * Creates a channel with an optional buffer, an optional transducer (like (map f),
 *   (filter p) etc or a composition thereof), and an optional exception handler.
 *   If buf-or-n is a number, will create and use a fixed buffer of that size. If a
 *   transducer is supplied a buffer must be specified. ex-handler must be a
 *   fn of one argument - if an exception occurs during transformation it will be called
 *   with the thrown value as an argument, and any non-nil return value will be placed
 *   in the channel.
 */
cljs.core.async.chan = (function cljs$core$async$chan(var_args){
var G__31215 = arguments.length;
switch (G__31215) {
case 0:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(null);
}));

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1 = (function (buf_or_n){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3(buf_or_n,null,null);
}));

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2 = (function (buf_or_n,xform){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3(buf_or_n,xform,null);
}));

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3 = (function (buf_or_n,xform,ex_handler){
var buf_or_n__$1 = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(buf_or_n,(0)))?null:buf_or_n);
if(cljs.core.truth_(xform)){
if(cljs.core.truth_(buf_or_n__$1)){
} else {
throw (new Error(["Assert failed: ","buffer must be supplied when transducer is","\n","buf-or-n"].join('')));
}
} else {
}

return cljs.core.async.impl.channels.chan.cljs$core$IFn$_invoke$arity$3(((typeof buf_or_n__$1 === 'number')?cljs.core.async.buffer(buf_or_n__$1):buf_or_n__$1),xform,ex_handler);
}));

(cljs.core.async.chan.cljs$lang$maxFixedArity = 3);

/**
 * Creates a promise channel with an optional transducer, and an optional
 *   exception-handler. A promise channel can take exactly one value that consumers
 *   will receive. Once full, puts complete but val is dropped (no transfer).
 *   Consumers will block until either a value is placed in the channel or the
 *   channel is closed, then return the value (or nil) forever. See chan for the
 *   semantics of xform and ex-handler.
 */
cljs.core.async.promise_chan = (function cljs$core$async$promise_chan(var_args){
var G__31222 = arguments.length;
switch (G__31222) {
case 0:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1(null);
}));

(cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1 = (function (xform){
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2(xform,null);
}));

(cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2 = (function (xform,ex_handler){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3(cljs.core.async.impl.buffers.promise_buffer(),xform,ex_handler);
}));

(cljs.core.async.promise_chan.cljs$lang$maxFixedArity = 2);

/**
 * Returns a channel that will close after msecs
 */
cljs.core.async.timeout = (function cljs$core$async$timeout(msecs){
return cljs.core.async.impl.timers.timeout(msecs);
});
/**
 * takes a val from port. Must be called inside a (go ...) block. Will
 *   return nil if closed. Will park if nothing is available.
 *   Returns true unless port is already closed
 */
cljs.core.async._LT__BANG_ = (function cljs$core$async$_LT__BANG_(port){
throw (new Error("<! used not in (go ...) block"));
});
/**
 * Asynchronously takes a val from port, passing to fn1. Will pass nil
 * if closed. If on-caller? (default true) is true, and value is
 * immediately available, will call fn1 on calling thread.
 * Returns nil.
 */
cljs.core.async.take_BANG_ = (function cljs$core$async$take_BANG_(var_args){
var G__31240 = arguments.length;
switch (G__31240) {
case 2:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,fn1){
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3(port,fn1,true);
}));

(cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,fn1,on_caller_QMARK_){
var ret = cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1(fn1));
if(cljs.core.truth_(ret)){
var val_34731 = cljs.core.deref(ret);
if(cljs.core.truth_(on_caller_QMARK_)){
(fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_34731) : fn1.call(null,val_34731));
} else {
cljs.core.async.impl.dispatch.run((function (){
return (fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_34731) : fn1.call(null,val_34731));
}));
}
} else {
}

return null;
}));

(cljs.core.async.take_BANG_.cljs$lang$maxFixedArity = 3);

cljs.core.async.nop = (function cljs$core$async$nop(_){
return null;
});
cljs.core.async.fhnop = cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1(cljs.core.async.nop);
/**
 * puts a val into port. nil values are not allowed. Must be called
 *   inside a (go ...) block. Will park if no buffer space is available.
 *   Returns true unless port is already closed.
 */
cljs.core.async._GT__BANG_ = (function cljs$core$async$_GT__BANG_(port,val){
throw (new Error(">! used not in (go ...) block"));
});
/**
 * Asynchronously puts a val into port, calling fn1 (if supplied) when
 * complete. nil values are not allowed. Will throw if closed. If
 * on-caller? (default true) is true, and the put is immediately
 * accepted, will call fn1 on calling thread.  Returns nil.
 */
cljs.core.async.put_BANG_ = (function cljs$core$async$put_BANG_(var_args){
var G__31251 = arguments.length;
switch (G__31251) {
case 2:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,val){
var temp__5823__auto__ = cljs.core.async.impl.protocols.put_BANG_(port,val,cljs.core.async.fhnop);
if(cljs.core.truth_(temp__5823__auto__)){
var ret = temp__5823__auto__;
return cljs.core.deref(ret);
} else {
return true;
}
}));

(cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,val,fn1){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4(port,val,fn1,true);
}));

(cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4 = (function (port,val,fn1,on_caller_QMARK_){
var temp__5823__auto__ = cljs.core.async.impl.protocols.put_BANG_(port,val,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1(fn1));
if(cljs.core.truth_(temp__5823__auto__)){
var retb = temp__5823__auto__;
var ret = cljs.core.deref(retb);
if(cljs.core.truth_(on_caller_QMARK_)){
(fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(ret) : fn1.call(null,ret));
} else {
cljs.core.async.impl.dispatch.run((function (){
return (fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(ret) : fn1.call(null,ret));
}));
}

return ret;
} else {
return true;
}
}));

(cljs.core.async.put_BANG_.cljs$lang$maxFixedArity = 4);

cljs.core.async.close_BANG_ = (function cljs$core$async$close_BANG_(port){
return cljs.core.async.impl.protocols.close_BANG_(port);
});
cljs.core.async.random_array = (function cljs$core$async$random_array(n){
var a = (new Array(n));
var n__5616__auto___34737 = n;
var x_34738 = (0);
while(true){
if((x_34738 < n__5616__auto___34737)){
(a[x_34738] = x_34738);

var G__34739 = (x_34738 + (1));
x_34738 = G__34739;
continue;
} else {
}
break;
}

cljs.core.async.goog$module$goog$array.shuffle(a);

return a;
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async31273 = (function (flag,meta31274){
this.flag = flag;
this.meta31274 = meta31274;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async31273.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_31275,meta31274__$1){
var self__ = this;
var _31275__$1 = this;
return (new cljs.core.async.t_cljs$core$async31273(self__.flag,meta31274__$1));
}));

(cljs.core.async.t_cljs$core$async31273.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_31275){
var self__ = this;
var _31275__$1 = this;
return self__.meta31274;
}));

(cljs.core.async.t_cljs$core$async31273.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async31273.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.deref(self__.flag);
}));

(cljs.core.async.t_cljs$core$async31273.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async31273.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.flag,null);

return true;
}));

(cljs.core.async.t_cljs$core$async31273.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"meta31274","meta31274",-1491179612,null)], null);
}));

(cljs.core.async.t_cljs$core$async31273.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async31273.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async31273");

(cljs.core.async.t_cljs$core$async31273.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async31273");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async31273.
 */
cljs.core.async.__GT_t_cljs$core$async31273 = (function cljs$core$async$__GT_t_cljs$core$async31273(flag,meta31274){
return (new cljs.core.async.t_cljs$core$async31273(flag,meta31274));
});


cljs.core.async.alt_flag = (function cljs$core$async$alt_flag(){
var flag = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(true);
return (new cljs.core.async.t_cljs$core$async31273(flag,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async31284 = (function (flag,cb,meta31285){
this.flag = flag;
this.cb = cb;
this.meta31285 = meta31285;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async31284.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_31286,meta31285__$1){
var self__ = this;
var _31286__$1 = this;
return (new cljs.core.async.t_cljs$core$async31284(self__.flag,self__.cb,meta31285__$1));
}));

(cljs.core.async.t_cljs$core$async31284.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_31286){
var self__ = this;
var _31286__$1 = this;
return self__.meta31285;
}));

(cljs.core.async.t_cljs$core$async31284.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async31284.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.flag);
}));

(cljs.core.async.t_cljs$core$async31284.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async31284.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.async.impl.protocols.commit(self__.flag);

return self__.cb;
}));

(cljs.core.async.t_cljs$core$async31284.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"cb","cb",-2064487928,null),new cljs.core.Symbol(null,"meta31285","meta31285",-1963833796,null)], null);
}));

(cljs.core.async.t_cljs$core$async31284.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async31284.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async31284");

(cljs.core.async.t_cljs$core$async31284.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async31284");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async31284.
 */
cljs.core.async.__GT_t_cljs$core$async31284 = (function cljs$core$async$__GT_t_cljs$core$async31284(flag,cb,meta31285){
return (new cljs.core.async.t_cljs$core$async31284(flag,cb,meta31285));
});


cljs.core.async.alt_handler = (function cljs$core$async$alt_handler(flag,cb){
return (new cljs.core.async.t_cljs$core$async31284(flag,cb,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * returns derefable [val port] if immediate, nil if enqueued
 */
cljs.core.async.do_alts = (function cljs$core$async$do_alts(fret,ports,opts){
if((cljs.core.count(ports) > (0))){
} else {
throw (new Error(["Assert failed: ","alts must have at least one channel operation","\n","(pos? (count ports))"].join('')));
}

var flag = cljs.core.async.alt_flag();
var ports__$1 = cljs.core.vec(ports);
var n = cljs.core.count(ports__$1);
var _ = (function (){var i = (0);
while(true){
if((i < n)){
var port_34743 = cljs.core.nth.cljs$core$IFn$_invoke$arity$2(ports__$1,i);
if(cljs.core.vector_QMARK_(port_34743)){
if((!(((port_34743.cljs$core$IFn$_invoke$arity$1 ? port_34743.cljs$core$IFn$_invoke$arity$1((1)) : port_34743.call(null,(1))) == null)))){
} else {
throw (new Error(["Assert failed: ","can't put nil on channel","\n","(some? (port 1))"].join('')));
}
} else {
}

var G__34744 = (i + (1));
i = G__34744;
continue;
} else {
return null;
}
break;
}
})();
var idxs = cljs.core.async.random_array(n);
var priority = new cljs.core.Keyword(null,"priority","priority",1431093715).cljs$core$IFn$_invoke$arity$1(opts);
var ret = (function (){var i = (0);
while(true){
if((i < n)){
var idx = (cljs.core.truth_(priority)?i:(idxs[i]));
var port = cljs.core.nth.cljs$core$IFn$_invoke$arity$2(ports__$1,idx);
var wport = ((cljs.core.vector_QMARK_(port))?(port.cljs$core$IFn$_invoke$arity$1 ? port.cljs$core$IFn$_invoke$arity$1((0)) : port.call(null,(0))):null);
var vbox = (cljs.core.truth_(wport)?(function (){var val = (port.cljs$core$IFn$_invoke$arity$1 ? port.cljs$core$IFn$_invoke$arity$1((1)) : port.call(null,(1)));
return cljs.core.async.impl.protocols.put_BANG_(wport,val,cljs.core.async.alt_handler(flag,((function (i,val,idx,port,wport,flag,ports__$1,n,_,idxs,priority){
return (function (p1__31301_SHARP_){
var G__31315 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__31301_SHARP_,wport], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__31315) : fret.call(null,G__31315));
});})(i,val,idx,port,wport,flag,ports__$1,n,_,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.alt_handler(flag,((function (i,idx,port,wport,flag,ports__$1,n,_,idxs,priority){
return (function (p1__31302_SHARP_){
var G__31317 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__31302_SHARP_,port], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__31317) : fret.call(null,G__31317));
});})(i,idx,port,wport,flag,ports__$1,n,_,idxs,priority))
)));
if(cljs.core.truth_(vbox)){
return cljs.core.async.impl.channels.box(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.deref(vbox),(function (){var or__5025__auto__ = wport;
if(cljs.core.truth_(or__5025__auto__)){
return or__5025__auto__;
} else {
return port;
}
})()], null));
} else {
var G__34746 = (i + (1));
i = G__34746;
continue;
}
} else {
return null;
}
break;
}
})();
var or__5025__auto__ = ret;
if(cljs.core.truth_(or__5025__auto__)){
return or__5025__auto__;
} else {
if(cljs.core.contains_QMARK_(opts,new cljs.core.Keyword(null,"default","default",-1987822328))){
var temp__5825__auto__ = (function (){var and__5023__auto__ = flag.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1(null);
if(cljs.core.truth_(and__5023__auto__)){
return flag.cljs$core$async$impl$protocols$Handler$commit$arity$1(null);
} else {
return and__5023__auto__;
}
})();
if(cljs.core.truth_(temp__5825__auto__)){
var got = temp__5825__auto__;
return cljs.core.async.impl.channels.box(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"default","default",-1987822328).cljs$core$IFn$_invoke$arity$1(opts),new cljs.core.Keyword(null,"default","default",-1987822328)], null));
} else {
return null;
}
} else {
return null;
}
}
});
/**
 * Completes at most one of several channel operations. Must be called
 * inside a (go ...) block. ports is a vector of channel endpoints,
 * which can be either a channel to take from or a vector of
 *   [channel-to-put-to val-to-put], in any combination. Takes will be
 *   made as if by <!, and puts will be made as if by >!. Unless
 *   the :priority option is true, if more than one port operation is
 *   ready a non-deterministic choice will be made. If no operation is
 *   ready and a :default value is supplied, [default-val :default] will
 *   be returned, otherwise alts! will park until the first operation to
 *   become ready completes. Returns [val port] of the completed
 *   operation, where val is the value taken for takes, and a
 *   boolean (true unless already closed, as per put!) for puts.
 * 
 *   opts are passed as :key val ... Supported options:
 * 
 *   :default val - the value to use if none of the operations are immediately ready
 *   :priority true - (default nil) when true, the operations will be tried in order.
 * 
 *   Note: there is no guarantee that the port exps or val exprs will be
 *   used, nor in what order should they be, so they should not be
 *   depended upon for side effects.
 */
cljs.core.async.alts_BANG_ = (function cljs$core$async$alts_BANG_(var_args){
var args__5755__auto__ = [];
var len__5749__auto___34749 = arguments.length;
var i__5750__auto___34750 = (0);
while(true){
if((i__5750__auto___34750 < len__5749__auto___34749)){
args__5755__auto__.push((arguments[i__5750__auto___34750]));

var G__34751 = (i__5750__auto___34750 + (1));
i__5750__auto___34750 = G__34751;
continue;
} else {
}
break;
}

var argseq__5756__auto__ = ((((1) < args__5755__auto__.length))?(new cljs.core.IndexedSeq(args__5755__auto__.slice((1)),(0),null)):null);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5756__auto__);
});

(cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ports,p__31325){
var map__31326 = p__31325;
var map__31326__$1 = cljs.core.__destructure_map(map__31326);
var opts = map__31326__$1;
throw (new Error("alts! used not in (go ...) block"));
}));

(cljs.core.async.alts_BANG_.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(cljs.core.async.alts_BANG_.cljs$lang$applyTo = (function (seq31319){
var G__31320 = cljs.core.first(seq31319);
var seq31319__$1 = cljs.core.next(seq31319);
var self__5734__auto__ = this;
return self__5734__auto__.cljs$core$IFn$_invoke$arity$variadic(G__31320,seq31319__$1);
}));

/**
 * Puts a val into port if it's possible to do so immediately.
 *   nil values are not allowed. Never blocks. Returns true if offer succeeds.
 */
cljs.core.async.offer_BANG_ = (function cljs$core$async$offer_BANG_(port,val){
var ret = cljs.core.async.impl.protocols.put_BANG_(port,val,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2(cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref(ret);
} else {
return null;
}
});
/**
 * Takes a val from port if it's possible to do so immediately.
 *   Never blocks. Returns value if successful, nil otherwise.
 */
cljs.core.async.poll_BANG_ = (function cljs$core$async$poll_BANG_(port){
var ret = cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2(cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref(ret);
} else {
return null;
}
});
/**
 * Takes elements from the from channel and supplies them to the to
 * channel. By default, the to channel will be closed when the from
 * channel closes, but can be determined by the close?  parameter. Will
 * stop consuming the from channel if the to channel closes
 */
cljs.core.async.pipe = (function cljs$core$async$pipe(var_args){
var G__31335 = arguments.length;
switch (G__31335) {
case 2:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2 = (function (from,to){
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3(from,to,true);
}));

(cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3 = (function (from,to,close_QMARK_){
var c__31012__auto___34756 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_31478){
var state_val_31484 = (state_31478[(1)]);
if((state_val_31484 === (7))){
var inst_31447 = (state_31478[(2)]);
var state_31478__$1 = state_31478;
var statearr_31512_34760 = state_31478__$1;
(statearr_31512_34760[(2)] = inst_31447);

(statearr_31512_34760[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (1))){
var state_31478__$1 = state_31478;
var statearr_31516_34761 = state_31478__$1;
(statearr_31516_34761[(2)] = null);

(statearr_31516_34761[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (4))){
var inst_31397 = (state_31478[(7)]);
var inst_31397__$1 = (state_31478[(2)]);
var inst_31415 = (inst_31397__$1 == null);
var state_31478__$1 = (function (){var statearr_31523 = state_31478;
(statearr_31523[(7)] = inst_31397__$1);

return statearr_31523;
})();
if(cljs.core.truth_(inst_31415)){
var statearr_31529_34769 = state_31478__$1;
(statearr_31529_34769[(1)] = (5));

} else {
var statearr_31531_34770 = state_31478__$1;
(statearr_31531_34770[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (13))){
var state_31478__$1 = state_31478;
var statearr_31533_34772 = state_31478__$1;
(statearr_31533_34772[(2)] = null);

(statearr_31533_34772[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (6))){
var inst_31397 = (state_31478[(7)]);
var state_31478__$1 = state_31478;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_31478__$1,(11),to,inst_31397);
} else {
if((state_val_31484 === (3))){
var inst_31457 = (state_31478[(2)]);
var state_31478__$1 = state_31478;
return cljs.core.async.impl.ioc_helpers.return_chan(state_31478__$1,inst_31457);
} else {
if((state_val_31484 === (12))){
var state_31478__$1 = state_31478;
var statearr_31543_34775 = state_31478__$1;
(statearr_31543_34775[(2)] = null);

(statearr_31543_34775[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (2))){
var state_31478__$1 = state_31478;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_31478__$1,(4),from);
} else {
if((state_val_31484 === (11))){
var inst_31439 = (state_31478[(2)]);
var state_31478__$1 = state_31478;
if(cljs.core.truth_(inst_31439)){
var statearr_31551_34776 = state_31478__$1;
(statearr_31551_34776[(1)] = (12));

} else {
var statearr_31552_34777 = state_31478__$1;
(statearr_31552_34777[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (9))){
var state_31478__$1 = state_31478;
var statearr_31553_34779 = state_31478__$1;
(statearr_31553_34779[(2)] = null);

(statearr_31553_34779[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (5))){
var state_31478__$1 = state_31478;
if(cljs.core.truth_(close_QMARK_)){
var statearr_31555_34780 = state_31478__$1;
(statearr_31555_34780[(1)] = (8));

} else {
var statearr_31559_34782 = state_31478__$1;
(statearr_31559_34782[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (14))){
var inst_31445 = (state_31478[(2)]);
var state_31478__$1 = state_31478;
var statearr_31561_34783 = state_31478__$1;
(statearr_31561_34783[(2)] = inst_31445);

(statearr_31561_34783[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (10))){
var inst_31435 = (state_31478[(2)]);
var state_31478__$1 = state_31478;
var statearr_31565_34784 = state_31478__$1;
(statearr_31565_34784[(2)] = inst_31435);

(statearr_31565_34784[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31484 === (8))){
var inst_31424 = cljs.core.async.close_BANG_(to);
var state_31478__$1 = state_31478;
var statearr_31571_34786 = state_31478__$1;
(statearr_31571_34786[(2)] = inst_31424);

(statearr_31571_34786[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_31583 = [null,null,null,null,null,null,null,null];
(statearr_31583[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_31583[(1)] = (1));

return statearr_31583;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_31478){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_31478);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e31596){var ex__29954__auto__ = e31596;
var statearr_31598_34787 = state_31478;
(statearr_31598_34787[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_31478[(4)]))){
var statearr_31607_34788 = state_31478;
(statearr_31607_34788[(1)] = cljs.core.first((state_31478[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34789 = state_31478;
state_31478 = G__34789;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_31478){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_31478);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_31626 = f__31016__auto__();
(statearr_31626[(6)] = c__31012__auto___34756);

return statearr_31626;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return to;
}));

(cljs.core.async.pipe.cljs$lang$maxFixedArity = 3);

cljs.core.async.pipeline_STAR_ = (function cljs$core$async$pipeline_STAR_(n,to,xf,from,close_QMARK_,ex_handler,type){
if((n > (0))){
} else {
throw (new Error("Assert failed: (pos? n)"));
}

var jobs = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(n);
var results = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(n);
var process__$1 = (function (p__31678){
var vec__31679 = p__31678;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31679,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31679,(1),null);
var job = vec__31679;
if((job == null)){
cljs.core.async.close_BANG_(results);

return null;
} else {
var res = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((1),xf,ex_handler);
var c__31012__auto___34790 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_31694){
var state_val_31695 = (state_31694[(1)]);
if((state_val_31695 === (1))){
var state_31694__$1 = state_31694;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_31694__$1,(2),res,v);
} else {
if((state_val_31695 === (2))){
var inst_31690 = (state_31694[(2)]);
var inst_31691 = cljs.core.async.close_BANG_(res);
var state_31694__$1 = (function (){var statearr_31712 = state_31694;
(statearr_31712[(7)] = inst_31690);

return statearr_31712;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_31694__$1,inst_31691);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0 = (function (){
var statearr_31714 = [null,null,null,null,null,null,null,null];
(statearr_31714[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__);

(statearr_31714[(1)] = (1));

return statearr_31714;
});
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1 = (function (state_31694){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_31694);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e31716){var ex__29954__auto__ = e31716;
var statearr_31717_34795 = state_31694;
(statearr_31717_34795[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_31694[(4)]))){
var statearr_31720_34796 = state_31694;
(statearr_31720_34796[(1)] = cljs.core.first((state_31694[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34797 = state_31694;
state_31694 = G__34797;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = function(state_31694){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1.call(this,state_31694);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_31724 = f__31016__auto__();
(statearr_31724[(6)] = c__31012__auto___34790);

return statearr_31724;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(p,res);

return true;
}
});
var async = (function (p__31729){
var vec__31730 = p__31729;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31730,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__31730,(1),null);
var job = vec__31730;
if((job == null)){
cljs.core.async.close_BANG_(results);

return null;
} else {
var res = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
(xf.cljs$core$IFn$_invoke$arity$2 ? xf.cljs$core$IFn$_invoke$arity$2(v,res) : xf.call(null,v,res));

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(p,res);

return true;
}
});
var n__5616__auto___34804 = n;
var __34805 = (0);
while(true){
if((__34805 < n__5616__auto___34804)){
var G__31734_34806 = type;
var G__31734_34807__$1 = (((G__31734_34806 instanceof cljs.core.Keyword))?G__31734_34806.fqn:null);
switch (G__31734_34807__$1) {
case "compute":
var c__31012__auto___34810 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__34805,c__31012__auto___34810,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async){
return (function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = ((function (__34805,c__31012__auto___34810,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async){
return (function (state_31754){
var state_val_31755 = (state_31754[(1)]);
if((state_val_31755 === (1))){
var state_31754__$1 = state_31754;
var statearr_31777_34812 = state_31754__$1;
(statearr_31777_34812[(2)] = null);

(statearr_31777_34812[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31755 === (2))){
var state_31754__$1 = state_31754;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_31754__$1,(4),jobs);
} else {
if((state_val_31755 === (3))){
var inst_31751 = (state_31754[(2)]);
var state_31754__$1 = state_31754;
return cljs.core.async.impl.ioc_helpers.return_chan(state_31754__$1,inst_31751);
} else {
if((state_val_31755 === (4))){
var inst_31742 = (state_31754[(2)]);
var inst_31743 = process__$1(inst_31742);
var state_31754__$1 = state_31754;
if(cljs.core.truth_(inst_31743)){
var statearr_31790_34816 = state_31754__$1;
(statearr_31790_34816[(1)] = (5));

} else {
var statearr_31798_34817 = state_31754__$1;
(statearr_31798_34817[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31755 === (5))){
var state_31754__$1 = state_31754;
var statearr_31804_34823 = state_31754__$1;
(statearr_31804_34823[(2)] = null);

(statearr_31804_34823[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31755 === (6))){
var state_31754__$1 = state_31754;
var statearr_31813_34824 = state_31754__$1;
(statearr_31813_34824[(2)] = null);

(statearr_31813_34824[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31755 === (7))){
var inst_31749 = (state_31754[(2)]);
var state_31754__$1 = state_31754;
var statearr_31817_34825 = state_31754__$1;
(statearr_31817_34825[(2)] = inst_31749);

(statearr_31817_34825[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__34805,c__31012__auto___34810,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async))
;
return ((function (__34805,switch__29950__auto__,c__31012__auto___34810,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0 = (function (){
var statearr_31825 = [null,null,null,null,null,null,null];
(statearr_31825[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__);

(statearr_31825[(1)] = (1));

return statearr_31825;
});
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1 = (function (state_31754){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_31754);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e31826){var ex__29954__auto__ = e31826;
var statearr_31827_34841 = state_31754;
(statearr_31827_34841[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_31754[(4)]))){
var statearr_31829_34842 = state_31754;
(statearr_31829_34842[(1)] = cljs.core.first((state_31754[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34848 = state_31754;
state_31754 = G__34848;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = function(state_31754){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1.call(this,state_31754);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__;
})()
;})(__34805,switch__29950__auto__,c__31012__auto___34810,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async))
})();
var state__31017__auto__ = (function (){var statearr_31835 = f__31016__auto__();
(statearr_31835[(6)] = c__31012__auto___34810);

return statearr_31835;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
});})(__34805,c__31012__auto___34810,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async))
);


break;
case "async":
var c__31012__auto___34856 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__34805,c__31012__auto___34856,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async){
return (function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = ((function (__34805,c__31012__auto___34856,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async){
return (function (state_31849){
var state_val_31850 = (state_31849[(1)]);
if((state_val_31850 === (1))){
var state_31849__$1 = state_31849;
var statearr_31860_34864 = state_31849__$1;
(statearr_31860_34864[(2)] = null);

(statearr_31860_34864[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31850 === (2))){
var state_31849__$1 = state_31849;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_31849__$1,(4),jobs);
} else {
if((state_val_31850 === (3))){
var inst_31847 = (state_31849[(2)]);
var state_31849__$1 = state_31849;
return cljs.core.async.impl.ioc_helpers.return_chan(state_31849__$1,inst_31847);
} else {
if((state_val_31850 === (4))){
var inst_31839 = (state_31849[(2)]);
var inst_31840 = async(inst_31839);
var state_31849__$1 = state_31849;
if(cljs.core.truth_(inst_31840)){
var statearr_31872_34873 = state_31849__$1;
(statearr_31872_34873[(1)] = (5));

} else {
var statearr_31873_34874 = state_31849__$1;
(statearr_31873_34874[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31850 === (5))){
var state_31849__$1 = state_31849;
var statearr_31875_34875 = state_31849__$1;
(statearr_31875_34875[(2)] = null);

(statearr_31875_34875[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31850 === (6))){
var state_31849__$1 = state_31849;
var statearr_31876_34877 = state_31849__$1;
(statearr_31876_34877[(2)] = null);

(statearr_31876_34877[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31850 === (7))){
var inst_31845 = (state_31849[(2)]);
var state_31849__$1 = state_31849;
var statearr_31883_34884 = state_31849__$1;
(statearr_31883_34884[(2)] = inst_31845);

(statearr_31883_34884[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__34805,c__31012__auto___34856,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async))
;
return ((function (__34805,switch__29950__auto__,c__31012__auto___34856,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0 = (function (){
var statearr_31887 = [null,null,null,null,null,null,null];
(statearr_31887[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__);

(statearr_31887[(1)] = (1));

return statearr_31887;
});
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1 = (function (state_31849){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_31849);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e31889){var ex__29954__auto__ = e31889;
var statearr_31890_34891 = state_31849;
(statearr_31890_34891[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_31849[(4)]))){
var statearr_31892_34892 = state_31849;
(statearr_31892_34892[(1)] = cljs.core.first((state_31849[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34901 = state_31849;
state_31849 = G__34901;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = function(state_31849){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1.call(this,state_31849);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__;
})()
;})(__34805,switch__29950__auto__,c__31012__auto___34856,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async))
})();
var state__31017__auto__ = (function (){var statearr_31895 = f__31016__auto__();
(statearr_31895[(6)] = c__31012__auto___34856);

return statearr_31895;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
});})(__34805,c__31012__auto___34856,G__31734_34806,G__31734_34807__$1,n__5616__auto___34804,jobs,results,process__$1,async))
);


break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__31734_34807__$1)].join('')));

}

var G__34915 = (__34805 + (1));
__34805 = G__34915;
continue;
} else {
}
break;
}

var c__31012__auto___34917 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_31935){
var state_val_31936 = (state_31935[(1)]);
if((state_val_31936 === (7))){
var inst_31929 = (state_31935[(2)]);
var state_31935__$1 = state_31935;
var statearr_31943_34921 = state_31935__$1;
(statearr_31943_34921[(2)] = inst_31929);

(statearr_31943_34921[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31936 === (1))){
var state_31935__$1 = state_31935;
var statearr_31945_34922 = state_31935__$1;
(statearr_31945_34922[(2)] = null);

(statearr_31945_34922[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31936 === (4))){
var inst_31901 = (state_31935[(7)]);
var inst_31901__$1 = (state_31935[(2)]);
var inst_31903 = (inst_31901__$1 == null);
var state_31935__$1 = (function (){var statearr_31949 = state_31935;
(statearr_31949[(7)] = inst_31901__$1);

return statearr_31949;
})();
if(cljs.core.truth_(inst_31903)){
var statearr_31950_34930 = state_31935__$1;
(statearr_31950_34930[(1)] = (5));

} else {
var statearr_31952_34931 = state_31935__$1;
(statearr_31952_34931[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31936 === (6))){
var inst_31901 = (state_31935[(7)]);
var inst_31909 = (state_31935[(8)]);
var inst_31909__$1 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var inst_31916 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_31921 = [inst_31901,inst_31909__$1];
var inst_31922 = (new cljs.core.PersistentVector(null,2,(5),inst_31916,inst_31921,null));
var state_31935__$1 = (function (){var statearr_31958 = state_31935;
(statearr_31958[(8)] = inst_31909__$1);

return statearr_31958;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_31935__$1,(8),jobs,inst_31922);
} else {
if((state_val_31936 === (3))){
var inst_31931 = (state_31935[(2)]);
var state_31935__$1 = state_31935;
return cljs.core.async.impl.ioc_helpers.return_chan(state_31935__$1,inst_31931);
} else {
if((state_val_31936 === (2))){
var state_31935__$1 = state_31935;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_31935__$1,(4),from);
} else {
if((state_val_31936 === (9))){
var inst_31926 = (state_31935[(2)]);
var state_31935__$1 = (function (){var statearr_31962 = state_31935;
(statearr_31962[(9)] = inst_31926);

return statearr_31962;
})();
var statearr_31965_34937 = state_31935__$1;
(statearr_31965_34937[(2)] = null);

(statearr_31965_34937[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31936 === (5))){
var inst_31905 = cljs.core.async.close_BANG_(jobs);
var state_31935__$1 = state_31935;
var statearr_31967_34950 = state_31935__$1;
(statearr_31967_34950[(2)] = inst_31905);

(statearr_31967_34950[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_31936 === (8))){
var inst_31909 = (state_31935[(8)]);
var inst_31924 = (state_31935[(2)]);
var state_31935__$1 = (function (){var statearr_31968 = state_31935;
(statearr_31968[(10)] = inst_31924);

return statearr_31968;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_31935__$1,(9),results,inst_31909);
} else {
return null;
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0 = (function (){
var statearr_31970 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_31970[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__);

(statearr_31970[(1)] = (1));

return statearr_31970;
});
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1 = (function (state_31935){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_31935);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e31971){var ex__29954__auto__ = e31971;
var statearr_31973_34977 = state_31935;
(statearr_31973_34977[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_31935[(4)]))){
var statearr_31975_34983 = state_31935;
(statearr_31975_34983[(1)] = cljs.core.first((state_31935[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34992 = state_31935;
state_31935 = G__34992;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = function(state_31935){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1.call(this,state_31935);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_31979 = f__31016__auto__();
(statearr_31979[(6)] = c__31012__auto___34917);

return statearr_31979;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


var c__31012__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_32025){
var state_val_32026 = (state_32025[(1)]);
if((state_val_32026 === (7))){
var inst_32021 = (state_32025[(2)]);
var state_32025__$1 = state_32025;
var statearr_32031_34994 = state_32025__$1;
(statearr_32031_34994[(2)] = inst_32021);

(statearr_32031_34994[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (20))){
var state_32025__$1 = state_32025;
var statearr_32033_35000 = state_32025__$1;
(statearr_32033_35000[(2)] = null);

(statearr_32033_35000[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (1))){
var state_32025__$1 = state_32025;
var statearr_32034_35010 = state_32025__$1;
(statearr_32034_35010[(2)] = null);

(statearr_32034_35010[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (4))){
var inst_31987 = (state_32025[(7)]);
var inst_31987__$1 = (state_32025[(2)]);
var inst_31989 = (inst_31987__$1 == null);
var state_32025__$1 = (function (){var statearr_32041 = state_32025;
(statearr_32041[(7)] = inst_31987__$1);

return statearr_32041;
})();
if(cljs.core.truth_(inst_31989)){
var statearr_32044_35018 = state_32025__$1;
(statearr_32044_35018[(1)] = (5));

} else {
var statearr_32045_35019 = state_32025__$1;
(statearr_32045_35019[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (15))){
var inst_32001 = (state_32025[(8)]);
var state_32025__$1 = state_32025;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_32025__$1,(18),to,inst_32001);
} else {
if((state_val_32026 === (21))){
var inst_32016 = (state_32025[(2)]);
var state_32025__$1 = state_32025;
var statearr_32048_35032 = state_32025__$1;
(statearr_32048_35032[(2)] = inst_32016);

(statearr_32048_35032[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (13))){
var inst_32018 = (state_32025[(2)]);
var state_32025__$1 = (function (){var statearr_32049 = state_32025;
(statearr_32049[(9)] = inst_32018);

return statearr_32049;
})();
var statearr_32052_35041 = state_32025__$1;
(statearr_32052_35041[(2)] = null);

(statearr_32052_35041[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (6))){
var inst_31987 = (state_32025[(7)]);
var state_32025__$1 = state_32025;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32025__$1,(11),inst_31987);
} else {
if((state_val_32026 === (17))){
var inst_32010 = (state_32025[(2)]);
var state_32025__$1 = state_32025;
if(cljs.core.truth_(inst_32010)){
var statearr_32056_35052 = state_32025__$1;
(statearr_32056_35052[(1)] = (19));

} else {
var statearr_32058_35057 = state_32025__$1;
(statearr_32058_35057[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (3))){
var inst_32023 = (state_32025[(2)]);
var state_32025__$1 = state_32025;
return cljs.core.async.impl.ioc_helpers.return_chan(state_32025__$1,inst_32023);
} else {
if((state_val_32026 === (12))){
var inst_31998 = (state_32025[(10)]);
var state_32025__$1 = state_32025;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32025__$1,(14),inst_31998);
} else {
if((state_val_32026 === (2))){
var state_32025__$1 = state_32025;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32025__$1,(4),results);
} else {
if((state_val_32026 === (19))){
var state_32025__$1 = state_32025;
var statearr_32068_35074 = state_32025__$1;
(statearr_32068_35074[(2)] = null);

(statearr_32068_35074[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (11))){
var inst_31998 = (state_32025[(2)]);
var state_32025__$1 = (function (){var statearr_32069 = state_32025;
(statearr_32069[(10)] = inst_31998);

return statearr_32069;
})();
var statearr_32070_35080 = state_32025__$1;
(statearr_32070_35080[(2)] = null);

(statearr_32070_35080[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (9))){
var state_32025__$1 = state_32025;
var statearr_32072_35085 = state_32025__$1;
(statearr_32072_35085[(2)] = null);

(statearr_32072_35085[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (5))){
var state_32025__$1 = state_32025;
if(cljs.core.truth_(close_QMARK_)){
var statearr_32074_35087 = state_32025__$1;
(statearr_32074_35087[(1)] = (8));

} else {
var statearr_32076_35089 = state_32025__$1;
(statearr_32076_35089[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (14))){
var inst_32001 = (state_32025[(8)]);
var inst_32004 = (state_32025[(11)]);
var inst_32001__$1 = (state_32025[(2)]);
var inst_32003 = (inst_32001__$1 == null);
var inst_32004__$1 = cljs.core.not(inst_32003);
var state_32025__$1 = (function (){var statearr_32088 = state_32025;
(statearr_32088[(8)] = inst_32001__$1);

(statearr_32088[(11)] = inst_32004__$1);

return statearr_32088;
})();
if(inst_32004__$1){
var statearr_32093_35093 = state_32025__$1;
(statearr_32093_35093[(1)] = (15));

} else {
var statearr_32094_35094 = state_32025__$1;
(statearr_32094_35094[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (16))){
var inst_32004 = (state_32025[(11)]);
var state_32025__$1 = state_32025;
var statearr_32095_35097 = state_32025__$1;
(statearr_32095_35097[(2)] = inst_32004);

(statearr_32095_35097[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (10))){
var inst_31995 = (state_32025[(2)]);
var state_32025__$1 = state_32025;
var statearr_32096_35112 = state_32025__$1;
(statearr_32096_35112[(2)] = inst_31995);

(statearr_32096_35112[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (18))){
var inst_32007 = (state_32025[(2)]);
var state_32025__$1 = state_32025;
var statearr_32097_35113 = state_32025__$1;
(statearr_32097_35113[(2)] = inst_32007);

(statearr_32097_35113[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32026 === (8))){
var inst_31992 = cljs.core.async.close_BANG_(to);
var state_32025__$1 = state_32025;
var statearr_32102_35115 = state_32025__$1;
(statearr_32102_35115[(2)] = inst_31992);

(statearr_32102_35115[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0 = (function (){
var statearr_32112 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_32112[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__);

(statearr_32112[(1)] = (1));

return statearr_32112;
});
var cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1 = (function (state_32025){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_32025);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e32121){var ex__29954__auto__ = e32121;
var statearr_32122_35120 = state_32025;
(statearr_32122_35120[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_32025[(4)]))){
var statearr_32123_35123 = state_32025;
(statearr_32123_35123[(1)] = cljs.core.first((state_32025[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35125 = state_32025;
state_32025 = G__35125;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__ = function(state_32025){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1.call(this,state_32025);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__29951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_32128 = f__31016__auto__();
(statearr_32128[(6)] = c__31012__auto__);

return statearr_32128;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));

return c__31012__auto__;
});
/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the async function af, with parallelism n. af
 *   must be a function of two arguments, the first an input value and
 *   the second a channel on which to place the result(s). The
 *   presumption is that af will return immediately, having launched some
 *   asynchronous operation whose completion/callback will put results on
 *   the channel, then close! it. Outputs will be returned in order
 *   relative to the inputs. By default, the to channel will be closed
 *   when the from channel closes, but can be determined by the close?
 *   parameter. Will stop consuming the from channel if the to channel
 *   closes. See also pipeline, pipeline-blocking.
 */
cljs.core.async.pipeline_async = (function cljs$core$async$pipeline_async(var_args){
var G__32152 = arguments.length;
switch (G__32152) {
case 4:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4 = (function (n,to,af,from){
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5(n,to,af,from,true);
}));

(cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5 = (function (n,to,af,from,close_QMARK_){
return cljs.core.async.pipeline_STAR_(n,to,af,from,close_QMARK_,null,new cljs.core.Keyword(null,"async","async",1050769601));
}));

(cljs.core.async.pipeline_async.cljs$lang$maxFixedArity = 5);

/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the transducer xf, with parallelism n. Because
 *   it is parallel, the transducer will be applied independently to each
 *   element, not across elements, and may produce zero or more outputs
 *   per input.  Outputs will be returned in order relative to the
 *   inputs. By default, the to channel will be closed when the from
 *   channel closes, but can be determined by the close?  parameter. Will
 *   stop consuming the from channel if the to channel closes.
 * 
 *   Note this is supplied for API compatibility with the Clojure version.
 *   Values of N > 1 will not result in actual concurrency in a
 *   single-threaded runtime.
 */
cljs.core.async.pipeline = (function cljs$core$async$pipeline(var_args){
var G__32216 = arguments.length;
switch (G__32216) {
case 4:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
case 6:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4 = (function (n,to,xf,from){
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5(n,to,xf,from,true);
}));

(cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5 = (function (n,to,xf,from,close_QMARK_){
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6(n,to,xf,from,close_QMARK_,null);
}));

(cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6 = (function (n,to,xf,from,close_QMARK_,ex_handler){
return cljs.core.async.pipeline_STAR_(n,to,xf,from,close_QMARK_,ex_handler,new cljs.core.Keyword(null,"compute","compute",1555393130));
}));

(cljs.core.async.pipeline.cljs$lang$maxFixedArity = 6);

/**
 * Takes a predicate and a source channel and returns a vector of two
 *   channels, the first of which will contain the values for which the
 *   predicate returned true, the second those for which it returned
 *   false.
 * 
 *   The out channels will be unbuffered by default, or two buf-or-ns can
 *   be supplied. The channels will close after the source channel has
 *   closed.
 */
cljs.core.async.split = (function cljs$core$async$split(var_args){
var G__32252 = arguments.length;
switch (G__32252) {
case 2:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.split.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$4(p,ch,null,null);
}));

(cljs.core.async.split.cljs$core$IFn$_invoke$arity$4 = (function (p,ch,t_buf_or_n,f_buf_or_n){
var tc = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(t_buf_or_n);
var fc = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(f_buf_or_n);
var c__31012__auto___35160 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_32290){
var state_val_32291 = (state_32290[(1)]);
if((state_val_32291 === (7))){
var inst_32286 = (state_32290[(2)]);
var state_32290__$1 = state_32290;
var statearr_32300_35167 = state_32290__$1;
(statearr_32300_35167[(2)] = inst_32286);

(statearr_32300_35167[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (1))){
var state_32290__$1 = state_32290;
var statearr_32302_35174 = state_32290__$1;
(statearr_32302_35174[(2)] = null);

(statearr_32302_35174[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (4))){
var inst_32262 = (state_32290[(7)]);
var inst_32262__$1 = (state_32290[(2)]);
var inst_32263 = (inst_32262__$1 == null);
var state_32290__$1 = (function (){var statearr_32304 = state_32290;
(statearr_32304[(7)] = inst_32262__$1);

return statearr_32304;
})();
if(cljs.core.truth_(inst_32263)){
var statearr_32305_35184 = state_32290__$1;
(statearr_32305_35184[(1)] = (5));

} else {
var statearr_32307_35185 = state_32290__$1;
(statearr_32307_35185[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (13))){
var state_32290__$1 = state_32290;
var statearr_32309_35190 = state_32290__$1;
(statearr_32309_35190[(2)] = null);

(statearr_32309_35190[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (6))){
var inst_32262 = (state_32290[(7)]);
var inst_32269 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_32262) : p.call(null,inst_32262));
var state_32290__$1 = state_32290;
if(cljs.core.truth_(inst_32269)){
var statearr_32310_35191 = state_32290__$1;
(statearr_32310_35191[(1)] = (9));

} else {
var statearr_32311_35196 = state_32290__$1;
(statearr_32311_35196[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (3))){
var inst_32288 = (state_32290[(2)]);
var state_32290__$1 = state_32290;
return cljs.core.async.impl.ioc_helpers.return_chan(state_32290__$1,inst_32288);
} else {
if((state_val_32291 === (12))){
var state_32290__$1 = state_32290;
var statearr_32316_35200 = state_32290__$1;
(statearr_32316_35200[(2)] = null);

(statearr_32316_35200[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (2))){
var state_32290__$1 = state_32290;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32290__$1,(4),ch);
} else {
if((state_val_32291 === (11))){
var inst_32262 = (state_32290[(7)]);
var inst_32277 = (state_32290[(2)]);
var state_32290__$1 = state_32290;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_32290__$1,(8),inst_32277,inst_32262);
} else {
if((state_val_32291 === (9))){
var state_32290__$1 = state_32290;
var statearr_32325_35211 = state_32290__$1;
(statearr_32325_35211[(2)] = tc);

(statearr_32325_35211[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (5))){
var inst_32266 = cljs.core.async.close_BANG_(tc);
var inst_32267 = cljs.core.async.close_BANG_(fc);
var state_32290__$1 = (function (){var statearr_32326 = state_32290;
(statearr_32326[(8)] = inst_32266);

return statearr_32326;
})();
var statearr_32328_35218 = state_32290__$1;
(statearr_32328_35218[(2)] = inst_32267);

(statearr_32328_35218[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (14))){
var inst_32284 = (state_32290[(2)]);
var state_32290__$1 = state_32290;
var statearr_32333_35219 = state_32290__$1;
(statearr_32333_35219[(2)] = inst_32284);

(statearr_32333_35219[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (10))){
var state_32290__$1 = state_32290;
var statearr_32334_35220 = state_32290__$1;
(statearr_32334_35220[(2)] = fc);

(statearr_32334_35220[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32291 === (8))){
var inst_32279 = (state_32290[(2)]);
var state_32290__$1 = state_32290;
if(cljs.core.truth_(inst_32279)){
var statearr_32338_35221 = state_32290__$1;
(statearr_32338_35221[(1)] = (12));

} else {
var statearr_32340_35225 = state_32290__$1;
(statearr_32340_35225[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_32345 = [null,null,null,null,null,null,null,null,null];
(statearr_32345[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_32345[(1)] = (1));

return statearr_32345;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_32290){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_32290);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e32346){var ex__29954__auto__ = e32346;
var statearr_32350_35226 = state_32290;
(statearr_32350_35226[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_32290[(4)]))){
var statearr_32351_35227 = state_32290;
(statearr_32351_35227[(1)] = cljs.core.first((state_32290[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35229 = state_32290;
state_32290 = G__35229;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_32290){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_32290);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_32355 = f__31016__auto__();
(statearr_32355[(6)] = c__31012__auto___35160);

return statearr_32355;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [tc,fc], null);
}));

(cljs.core.async.split.cljs$lang$maxFixedArity = 4);

/**
 * f should be a function of 2 arguments. Returns a channel containing
 *   the single result of applying f to init and the first item from the
 *   channel, then applying f to that result and the 2nd item, etc. If
 *   the channel closes without yielding items, returns init and f is not
 *   called. ch must close before reduce produces a result.
 */
cljs.core.async.reduce = (function cljs$core$async$reduce(f,init,ch){
var c__31012__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_32395){
var state_val_32396 = (state_32395[(1)]);
if((state_val_32396 === (7))){
var inst_32391 = (state_32395[(2)]);
var state_32395__$1 = state_32395;
var statearr_32407_35245 = state_32395__$1;
(statearr_32407_35245[(2)] = inst_32391);

(statearr_32407_35245[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32396 === (1))){
var inst_32360 = init;
var inst_32366 = inst_32360;
var state_32395__$1 = (function (){var statearr_32412 = state_32395;
(statearr_32412[(7)] = inst_32366);

return statearr_32412;
})();
var statearr_32414_35257 = state_32395__$1;
(statearr_32414_35257[(2)] = null);

(statearr_32414_35257[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32396 === (4))){
var inst_32374 = (state_32395[(8)]);
var inst_32374__$1 = (state_32395[(2)]);
var inst_32376 = (inst_32374__$1 == null);
var state_32395__$1 = (function (){var statearr_32417 = state_32395;
(statearr_32417[(8)] = inst_32374__$1);

return statearr_32417;
})();
if(cljs.core.truth_(inst_32376)){
var statearr_32419_35265 = state_32395__$1;
(statearr_32419_35265[(1)] = (5));

} else {
var statearr_32423_35269 = state_32395__$1;
(statearr_32423_35269[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32396 === (6))){
var inst_32366 = (state_32395[(7)]);
var inst_32374 = (state_32395[(8)]);
var inst_32379 = (state_32395[(9)]);
var inst_32379__$1 = (f.cljs$core$IFn$_invoke$arity$2 ? f.cljs$core$IFn$_invoke$arity$2(inst_32366,inst_32374) : f.call(null,inst_32366,inst_32374));
var inst_32380 = cljs.core.reduced_QMARK_(inst_32379__$1);
var state_32395__$1 = (function (){var statearr_32426 = state_32395;
(statearr_32426[(9)] = inst_32379__$1);

return statearr_32426;
})();
if(inst_32380){
var statearr_32427_35277 = state_32395__$1;
(statearr_32427_35277[(1)] = (8));

} else {
var statearr_32428_35278 = state_32395__$1;
(statearr_32428_35278[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32396 === (3))){
var inst_32393 = (state_32395[(2)]);
var state_32395__$1 = state_32395;
return cljs.core.async.impl.ioc_helpers.return_chan(state_32395__$1,inst_32393);
} else {
if((state_val_32396 === (2))){
var state_32395__$1 = state_32395;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32395__$1,(4),ch);
} else {
if((state_val_32396 === (9))){
var inst_32379 = (state_32395[(9)]);
var inst_32366 = inst_32379;
var state_32395__$1 = (function (){var statearr_32435 = state_32395;
(statearr_32435[(7)] = inst_32366);

return statearr_32435;
})();
var statearr_32437_35283 = state_32395__$1;
(statearr_32437_35283[(2)] = null);

(statearr_32437_35283[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32396 === (5))){
var inst_32366 = (state_32395[(7)]);
var state_32395__$1 = state_32395;
var statearr_32438_35290 = state_32395__$1;
(statearr_32438_35290[(2)] = inst_32366);

(statearr_32438_35290[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32396 === (10))){
var inst_32388 = (state_32395[(2)]);
var state_32395__$1 = state_32395;
var statearr_32440_35291 = state_32395__$1;
(statearr_32440_35291[(2)] = inst_32388);

(statearr_32440_35291[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32396 === (8))){
var inst_32379 = (state_32395[(9)]);
var inst_32384 = cljs.core.deref(inst_32379);
var state_32395__$1 = state_32395;
var statearr_32443_35301 = state_32395__$1;
(statearr_32443_35301[(2)] = inst_32384);

(statearr_32443_35301[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$reduce_$_state_machine__29951__auto__ = null;
var cljs$core$async$reduce_$_state_machine__29951__auto____0 = (function (){
var statearr_32455 = [null,null,null,null,null,null,null,null,null,null];
(statearr_32455[(0)] = cljs$core$async$reduce_$_state_machine__29951__auto__);

(statearr_32455[(1)] = (1));

return statearr_32455;
});
var cljs$core$async$reduce_$_state_machine__29951__auto____1 = (function (state_32395){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_32395);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e32457){var ex__29954__auto__ = e32457;
var statearr_32458_35317 = state_32395;
(statearr_32458_35317[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_32395[(4)]))){
var statearr_32461_35323 = state_32395;
(statearr_32461_35323[(1)] = cljs.core.first((state_32395[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35325 = state_32395;
state_32395 = G__35325;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$reduce_$_state_machine__29951__auto__ = function(state_32395){
switch(arguments.length){
case 0:
return cljs$core$async$reduce_$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$reduce_$_state_machine__29951__auto____1.call(this,state_32395);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$reduce_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$reduce_$_state_machine__29951__auto____0;
cljs$core$async$reduce_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$reduce_$_state_machine__29951__auto____1;
return cljs$core$async$reduce_$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_32468 = f__31016__auto__();
(statearr_32468[(6)] = c__31012__auto__);

return statearr_32468;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));

return c__31012__auto__;
});
/**
 * async/reduces a channel with a transformation (xform f).
 *   Returns a channel containing the result.  ch must close before
 *   transduce produces a result.
 */
cljs.core.async.transduce = (function cljs$core$async$transduce(xform,f,init,ch){
var f__$1 = (xform.cljs$core$IFn$_invoke$arity$1 ? xform.cljs$core$IFn$_invoke$arity$1(f) : xform.call(null,f));
var c__31012__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_32486){
var state_val_32487 = (state_32486[(1)]);
if((state_val_32487 === (1))){
var inst_32481 = cljs.core.async.reduce(f__$1,init,ch);
var state_32486__$1 = state_32486;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32486__$1,(2),inst_32481);
} else {
if((state_val_32487 === (2))){
var inst_32483 = (state_32486[(2)]);
var inst_32484 = (f__$1.cljs$core$IFn$_invoke$arity$1 ? f__$1.cljs$core$IFn$_invoke$arity$1(inst_32483) : f__$1.call(null,inst_32483));
var state_32486__$1 = state_32486;
return cljs.core.async.impl.ioc_helpers.return_chan(state_32486__$1,inst_32484);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$transduce_$_state_machine__29951__auto__ = null;
var cljs$core$async$transduce_$_state_machine__29951__auto____0 = (function (){
var statearr_32499 = [null,null,null,null,null,null,null];
(statearr_32499[(0)] = cljs$core$async$transduce_$_state_machine__29951__auto__);

(statearr_32499[(1)] = (1));

return statearr_32499;
});
var cljs$core$async$transduce_$_state_machine__29951__auto____1 = (function (state_32486){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_32486);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e32502){var ex__29954__auto__ = e32502;
var statearr_32505_35347 = state_32486;
(statearr_32505_35347[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_32486[(4)]))){
var statearr_32507_35348 = state_32486;
(statearr_32507_35348[(1)] = cljs.core.first((state_32486[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35349 = state_32486;
state_32486 = G__35349;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$transduce_$_state_machine__29951__auto__ = function(state_32486){
switch(arguments.length){
case 0:
return cljs$core$async$transduce_$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$transduce_$_state_machine__29951__auto____1.call(this,state_32486);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$transduce_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$transduce_$_state_machine__29951__auto____0;
cljs$core$async$transduce_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$transduce_$_state_machine__29951__auto____1;
return cljs$core$async$transduce_$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_32509 = f__31016__auto__();
(statearr_32509[(6)] = c__31012__auto__);

return statearr_32509;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));

return c__31012__auto__;
});
/**
 * Puts the contents of coll into the supplied channel.
 * 
 *   By default the channel will be closed after the items are copied,
 *   but can be determined by the close? parameter.
 * 
 *   Returns a channel which will close after the items are copied.
 */
cljs.core.async.onto_chan_BANG_ = (function cljs$core$async$onto_chan_BANG_(var_args){
var G__32524 = arguments.length;
switch (G__32524) {
case 2:
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (ch,coll){
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3(ch,coll,true);
}));

(cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (ch,coll,close_QMARK_){
var c__31012__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_32554){
var state_val_32555 = (state_32554[(1)]);
if((state_val_32555 === (7))){
var inst_32535 = (state_32554[(2)]);
var state_32554__$1 = state_32554;
var statearr_32565_35360 = state_32554__$1;
(statearr_32565_35360[(2)] = inst_32535);

(statearr_32565_35360[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (1))){
var inst_32528 = cljs.core.seq(coll);
var inst_32529 = inst_32528;
var state_32554__$1 = (function (){var statearr_32566 = state_32554;
(statearr_32566[(7)] = inst_32529);

return statearr_32566;
})();
var statearr_32569_35361 = state_32554__$1;
(statearr_32569_35361[(2)] = null);

(statearr_32569_35361[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (4))){
var inst_32529 = (state_32554[(7)]);
var inst_32533 = cljs.core.first(inst_32529);
var state_32554__$1 = state_32554;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_32554__$1,(7),ch,inst_32533);
} else {
if((state_val_32555 === (13))){
var inst_32547 = (state_32554[(2)]);
var state_32554__$1 = state_32554;
var statearr_32575_35364 = state_32554__$1;
(statearr_32575_35364[(2)] = inst_32547);

(statearr_32575_35364[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (6))){
var inst_32538 = (state_32554[(2)]);
var state_32554__$1 = state_32554;
if(cljs.core.truth_(inst_32538)){
var statearr_32577_35365 = state_32554__$1;
(statearr_32577_35365[(1)] = (8));

} else {
var statearr_32578_35366 = state_32554__$1;
(statearr_32578_35366[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (3))){
var inst_32551 = (state_32554[(2)]);
var state_32554__$1 = state_32554;
return cljs.core.async.impl.ioc_helpers.return_chan(state_32554__$1,inst_32551);
} else {
if((state_val_32555 === (12))){
var state_32554__$1 = state_32554;
var statearr_32582_35367 = state_32554__$1;
(statearr_32582_35367[(2)] = null);

(statearr_32582_35367[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (2))){
var inst_32529 = (state_32554[(7)]);
var state_32554__$1 = state_32554;
if(cljs.core.truth_(inst_32529)){
var statearr_32585_35372 = state_32554__$1;
(statearr_32585_35372[(1)] = (4));

} else {
var statearr_32586_35373 = state_32554__$1;
(statearr_32586_35373[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (11))){
var inst_32544 = cljs.core.async.close_BANG_(ch);
var state_32554__$1 = state_32554;
var statearr_32588_35378 = state_32554__$1;
(statearr_32588_35378[(2)] = inst_32544);

(statearr_32588_35378[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (9))){
var state_32554__$1 = state_32554;
if(cljs.core.truth_(close_QMARK_)){
var statearr_32590_35380 = state_32554__$1;
(statearr_32590_35380[(1)] = (11));

} else {
var statearr_32591_35381 = state_32554__$1;
(statearr_32591_35381[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (5))){
var inst_32529 = (state_32554[(7)]);
var state_32554__$1 = state_32554;
var statearr_32592_35382 = state_32554__$1;
(statearr_32592_35382[(2)] = inst_32529);

(statearr_32592_35382[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (10))){
var inst_32549 = (state_32554[(2)]);
var state_32554__$1 = state_32554;
var statearr_32593_35383 = state_32554__$1;
(statearr_32593_35383[(2)] = inst_32549);

(statearr_32593_35383[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32555 === (8))){
var inst_32529 = (state_32554[(7)]);
var inst_32540 = cljs.core.next(inst_32529);
var inst_32529__$1 = inst_32540;
var state_32554__$1 = (function (){var statearr_32597 = state_32554;
(statearr_32597[(7)] = inst_32529__$1);

return statearr_32597;
})();
var statearr_32598_35389 = state_32554__$1;
(statearr_32598_35389[(2)] = null);

(statearr_32598_35389[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_32600 = [null,null,null,null,null,null,null,null];
(statearr_32600[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_32600[(1)] = (1));

return statearr_32600;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_32554){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_32554);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e32602){var ex__29954__auto__ = e32602;
var statearr_32603_35394 = state_32554;
(statearr_32603_35394[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_32554[(4)]))){
var statearr_32604_35395 = state_32554;
(statearr_32604_35395[(1)] = cljs.core.first((state_32554[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35396 = state_32554;
state_32554 = G__35396;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_32554){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_32554);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_32608 = f__31016__auto__();
(statearr_32608[(6)] = c__31012__auto__);

return statearr_32608;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));

return c__31012__auto__;
}));

(cljs.core.async.onto_chan_BANG_.cljs$lang$maxFixedArity = 3);

/**
 * Creates and returns a channel which contains the contents of coll,
 *   closing when exhausted.
 */
cljs.core.async.to_chan_BANG_ = (function cljs$core$async$to_chan_BANG_(coll){
var ch = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(cljs.core.bounded_count((100),coll));
cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$2(ch,coll);

return ch;
});
/**
 * Deprecated - use onto-chan!
 */
cljs.core.async.onto_chan = (function cljs$core$async$onto_chan(var_args){
var G__32619 = arguments.length;
switch (G__32619) {
case 2:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2 = (function (ch,coll){
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3(ch,coll,true);
}));

(cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3 = (function (ch,coll,close_QMARK_){
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3(ch,coll,close_QMARK_);
}));

(cljs.core.async.onto_chan.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - use to-chan!
 */
cljs.core.async.to_chan = (function cljs$core$async$to_chan(coll){
return cljs.core.async.to_chan_BANG_(coll);
});

/**
 * @interface
 */
cljs.core.async.Mux = function(){};

var cljs$core$async$Mux$muxch_STAR_$dyn_35399 = (function (_){
var x__5373__auto__ = (((_ == null))?null:_);
var m__5374__auto__ = (cljs.core.async.muxch_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$1(_) : m__5374__auto__.call(null,_));
} else {
var m__5372__auto__ = (cljs.core.async.muxch_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$1(_) : m__5372__auto__.call(null,_));
} else {
throw cljs.core.missing_protocol("Mux.muxch*",_);
}
}
});
cljs.core.async.muxch_STAR_ = (function cljs$core$async$muxch_STAR_(_){
if((((!((_ == null)))) && ((!((_.cljs$core$async$Mux$muxch_STAR_$arity$1 == null)))))){
return _.cljs$core$async$Mux$muxch_STAR_$arity$1(_);
} else {
return cljs$core$async$Mux$muxch_STAR_$dyn_35399(_);
}
});


/**
 * @interface
 */
cljs.core.async.Mult = function(){};

var cljs$core$async$Mult$tap_STAR_$dyn_35400 = (function (m,ch,close_QMARK_){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.tap_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$3(m,ch,close_QMARK_) : m__5374__auto__.call(null,m,ch,close_QMARK_));
} else {
var m__5372__auto__ = (cljs.core.async.tap_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$3(m,ch,close_QMARK_) : m__5372__auto__.call(null,m,ch,close_QMARK_));
} else {
throw cljs.core.missing_protocol("Mult.tap*",m);
}
}
});
cljs.core.async.tap_STAR_ = (function cljs$core$async$tap_STAR_(m,ch,close_QMARK_){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$tap_STAR_$arity$3 == null)))))){
return m.cljs$core$async$Mult$tap_STAR_$arity$3(m,ch,close_QMARK_);
} else {
return cljs$core$async$Mult$tap_STAR_$dyn_35400(m,ch,close_QMARK_);
}
});

var cljs$core$async$Mult$untap_STAR_$dyn_35405 = (function (m,ch){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.untap_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5374__auto__.call(null,m,ch));
} else {
var m__5372__auto__ = (cljs.core.async.untap_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5372__auto__.call(null,m,ch));
} else {
throw cljs.core.missing_protocol("Mult.untap*",m);
}
}
});
cljs.core.async.untap_STAR_ = (function cljs$core$async$untap_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mult$untap_STAR_$arity$2(m,ch);
} else {
return cljs$core$async$Mult$untap_STAR_$dyn_35405(m,ch);
}
});

var cljs$core$async$Mult$untap_all_STAR_$dyn_35410 = (function (m){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.untap_all_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5374__auto__.call(null,m));
} else {
var m__5372__auto__ = (cljs.core.async.untap_all_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5372__auto__.call(null,m));
} else {
throw cljs.core.missing_protocol("Mult.untap-all*",m);
}
}
});
cljs.core.async.untap_all_STAR_ = (function cljs$core$async$untap_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mult$untap_all_STAR_$arity$1(m);
} else {
return cljs$core$async$Mult$untap_all_STAR_$dyn_35410(m);
}
});


/**
* @constructor
 * @implements {cljs.core.async.Mult}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async32645 = (function (ch,cs,meta32646){
this.ch = ch;
this.cs = cs;
this.meta32646 = meta32646;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_32647,meta32646__$1){
var self__ = this;
var _32647__$1 = this;
return (new cljs.core.async.t_cljs$core$async32645(self__.ch,self__.cs,meta32646__$1));
}));

(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_32647){
var self__ = this;
var _32647__$1 = this;
return self__.meta32646;
}));

(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$async$Mult$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = (function (_,ch__$1,close_QMARK_){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch__$1,close_QMARK_);

return null;
}));

(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = (function (_,ch__$1){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch__$1);

return null;
}));

(cljs.core.async.t_cljs$core$async32645.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return null;
}));

(cljs.core.async.t_cljs$core$async32645.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"meta32646","meta32646",-491350052,null)], null);
}));

(cljs.core.async.t_cljs$core$async32645.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async32645.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async32645");

(cljs.core.async.t_cljs$core$async32645.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async32645");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async32645.
 */
cljs.core.async.__GT_t_cljs$core$async32645 = (function cljs$core$async$__GT_t_cljs$core$async32645(ch,cs,meta32646){
return (new cljs.core.async.t_cljs$core$async32645(ch,cs,meta32646));
});


/**
 * Creates and returns a mult(iple) of the supplied channel. Channels
 *   containing copies of the channel can be created with 'tap', and
 *   detached with 'untap'.
 * 
 *   Each item is distributed to all taps in parallel and synchronously,
 *   i.e. each tap must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow taps from holding up the mult.
 * 
 *   Items received when there are no taps get dropped.
 * 
 *   If a tap puts to a closed channel, it will be removed from the mult.
 */
cljs.core.async.mult = (function cljs$core$async$mult(ch){
var cs = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var m = (new cljs.core.async.t_cljs$core$async32645(ch,cs,cljs.core.PersistentArrayMap.EMPTY));
var dchan = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var dctr = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var done = (function (_){
if((cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(dchan,true);
} else {
return null;
}
});
var c__31012__auto___35429 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_32822){
var state_val_32823 = (state_32822[(1)]);
if((state_val_32823 === (7))){
var inst_32817 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_32841_35432 = state_32822__$1;
(statearr_32841_35432[(2)] = inst_32817);

(statearr_32841_35432[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (20))){
var inst_32711 = (state_32822[(7)]);
var inst_32724 = cljs.core.first(inst_32711);
var inst_32725 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_32724,(0),null);
var inst_32726 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_32724,(1),null);
var state_32822__$1 = (function (){var statearr_32848 = state_32822;
(statearr_32848[(8)] = inst_32725);

return statearr_32848;
})();
if(cljs.core.truth_(inst_32726)){
var statearr_32850_35442 = state_32822__$1;
(statearr_32850_35442[(1)] = (22));

} else {
var statearr_32851_35443 = state_32822__$1;
(statearr_32851_35443[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (27))){
var inst_32757 = (state_32822[(9)]);
var inst_32759 = (state_32822[(10)]);
var inst_32766 = (state_32822[(11)]);
var inst_32673 = (state_32822[(12)]);
var inst_32766__$1 = cljs.core._nth(inst_32757,inst_32759);
var inst_32767 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_32766__$1,inst_32673,done);
var state_32822__$1 = (function (){var statearr_32860 = state_32822;
(statearr_32860[(11)] = inst_32766__$1);

return statearr_32860;
})();
if(cljs.core.truth_(inst_32767)){
var statearr_32861_35444 = state_32822__$1;
(statearr_32861_35444[(1)] = (30));

} else {
var statearr_32862_35445 = state_32822__$1;
(statearr_32862_35445[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (1))){
var state_32822__$1 = state_32822;
var statearr_32864_35446 = state_32822__$1;
(statearr_32864_35446[(2)] = null);

(statearr_32864_35446[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (24))){
var inst_32711 = (state_32822[(7)]);
var inst_32733 = (state_32822[(2)]);
var inst_32734 = cljs.core.next(inst_32711);
var inst_32683 = inst_32734;
var inst_32684 = null;
var inst_32685 = (0);
var inst_32686 = (0);
var state_32822__$1 = (function (){var statearr_32875 = state_32822;
(statearr_32875[(13)] = inst_32733);

(statearr_32875[(14)] = inst_32683);

(statearr_32875[(15)] = inst_32684);

(statearr_32875[(16)] = inst_32685);

(statearr_32875[(17)] = inst_32686);

return statearr_32875;
})();
var statearr_32876_35449 = state_32822__$1;
(statearr_32876_35449[(2)] = null);

(statearr_32876_35449[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (39))){
var state_32822__$1 = state_32822;
var statearr_32889_35451 = state_32822__$1;
(statearr_32889_35451[(2)] = null);

(statearr_32889_35451[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (4))){
var inst_32673 = (state_32822[(12)]);
var inst_32673__$1 = (state_32822[(2)]);
var inst_32674 = (inst_32673__$1 == null);
var state_32822__$1 = (function (){var statearr_32892 = state_32822;
(statearr_32892[(12)] = inst_32673__$1);

return statearr_32892;
})();
if(cljs.core.truth_(inst_32674)){
var statearr_32893_35453 = state_32822__$1;
(statearr_32893_35453[(1)] = (5));

} else {
var statearr_32894_35454 = state_32822__$1;
(statearr_32894_35454[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (15))){
var inst_32686 = (state_32822[(17)]);
var inst_32683 = (state_32822[(14)]);
var inst_32684 = (state_32822[(15)]);
var inst_32685 = (state_32822[(16)]);
var inst_32705 = (state_32822[(2)]);
var inst_32708 = (inst_32686 + (1));
var tmp32880 = inst_32683;
var tmp32881 = inst_32684;
var tmp32882 = inst_32685;
var inst_32683__$1 = tmp32880;
var inst_32684__$1 = tmp32881;
var inst_32685__$1 = tmp32882;
var inst_32686__$1 = inst_32708;
var state_32822__$1 = (function (){var statearr_32897 = state_32822;
(statearr_32897[(18)] = inst_32705);

(statearr_32897[(14)] = inst_32683__$1);

(statearr_32897[(15)] = inst_32684__$1);

(statearr_32897[(16)] = inst_32685__$1);

(statearr_32897[(17)] = inst_32686__$1);

return statearr_32897;
})();
var statearr_32899_35463 = state_32822__$1;
(statearr_32899_35463[(2)] = null);

(statearr_32899_35463[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (21))){
var inst_32737 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_32909_35465 = state_32822__$1;
(statearr_32909_35465[(2)] = inst_32737);

(statearr_32909_35465[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (31))){
var inst_32766 = (state_32822[(11)]);
var inst_32772 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_32766);
var state_32822__$1 = state_32822;
var statearr_32910_35466 = state_32822__$1;
(statearr_32910_35466[(2)] = inst_32772);

(statearr_32910_35466[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (32))){
var inst_32759 = (state_32822[(10)]);
var inst_32756 = (state_32822[(19)]);
var inst_32757 = (state_32822[(9)]);
var inst_32758 = (state_32822[(20)]);
var inst_32774 = (state_32822[(2)]);
var inst_32775 = (inst_32759 + (1));
var tmp32902 = inst_32756;
var tmp32903 = inst_32757;
var tmp32904 = inst_32758;
var inst_32756__$1 = tmp32902;
var inst_32757__$1 = tmp32903;
var inst_32758__$1 = tmp32904;
var inst_32759__$1 = inst_32775;
var state_32822__$1 = (function (){var statearr_32911 = state_32822;
(statearr_32911[(21)] = inst_32774);

(statearr_32911[(19)] = inst_32756__$1);

(statearr_32911[(9)] = inst_32757__$1);

(statearr_32911[(20)] = inst_32758__$1);

(statearr_32911[(10)] = inst_32759__$1);

return statearr_32911;
})();
var statearr_32912_35471 = state_32822__$1;
(statearr_32912_35471[(2)] = null);

(statearr_32912_35471[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (40))){
var inst_32790 = (state_32822[(22)]);
var inst_32794 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_32790);
var state_32822__$1 = state_32822;
var statearr_32914_35474 = state_32822__$1;
(statearr_32914_35474[(2)] = inst_32794);

(statearr_32914_35474[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (33))){
var inst_32778 = (state_32822[(23)]);
var inst_32783 = cljs.core.chunked_seq_QMARK_(inst_32778);
var state_32822__$1 = state_32822;
if(inst_32783){
var statearr_32917_35475 = state_32822__$1;
(statearr_32917_35475[(1)] = (36));

} else {
var statearr_32918_35476 = state_32822__$1;
(statearr_32918_35476[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (13))){
var inst_32697 = (state_32822[(24)]);
var inst_32702 = cljs.core.async.close_BANG_(inst_32697);
var state_32822__$1 = state_32822;
var statearr_32921_35483 = state_32822__$1;
(statearr_32921_35483[(2)] = inst_32702);

(statearr_32921_35483[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (22))){
var inst_32725 = (state_32822[(8)]);
var inst_32730 = cljs.core.async.close_BANG_(inst_32725);
var state_32822__$1 = state_32822;
var statearr_32925_35488 = state_32822__$1;
(statearr_32925_35488[(2)] = inst_32730);

(statearr_32925_35488[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (36))){
var inst_32778 = (state_32822[(23)]);
var inst_32785 = cljs.core.chunk_first(inst_32778);
var inst_32786 = cljs.core.chunk_rest(inst_32778);
var inst_32787 = cljs.core.count(inst_32785);
var inst_32756 = inst_32786;
var inst_32757 = inst_32785;
var inst_32758 = inst_32787;
var inst_32759 = (0);
var state_32822__$1 = (function (){var statearr_32931 = state_32822;
(statearr_32931[(19)] = inst_32756);

(statearr_32931[(9)] = inst_32757);

(statearr_32931[(20)] = inst_32758);

(statearr_32931[(10)] = inst_32759);

return statearr_32931;
})();
var statearr_32934_35493 = state_32822__$1;
(statearr_32934_35493[(2)] = null);

(statearr_32934_35493[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (41))){
var inst_32778 = (state_32822[(23)]);
var inst_32796 = (state_32822[(2)]);
var inst_32797 = cljs.core.next(inst_32778);
var inst_32756 = inst_32797;
var inst_32757 = null;
var inst_32758 = (0);
var inst_32759 = (0);
var state_32822__$1 = (function (){var statearr_32937 = state_32822;
(statearr_32937[(25)] = inst_32796);

(statearr_32937[(19)] = inst_32756);

(statearr_32937[(9)] = inst_32757);

(statearr_32937[(20)] = inst_32758);

(statearr_32937[(10)] = inst_32759);

return statearr_32937;
})();
var statearr_32940_35494 = state_32822__$1;
(statearr_32940_35494[(2)] = null);

(statearr_32940_35494[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (43))){
var state_32822__$1 = state_32822;
var statearr_32945_35495 = state_32822__$1;
(statearr_32945_35495[(2)] = null);

(statearr_32945_35495[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (29))){
var inst_32805 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_32950_35496 = state_32822__$1;
(statearr_32950_35496[(2)] = inst_32805);

(statearr_32950_35496[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (44))){
var inst_32814 = (state_32822[(2)]);
var state_32822__$1 = (function (){var statearr_32953 = state_32822;
(statearr_32953[(26)] = inst_32814);

return statearr_32953;
})();
var statearr_32954_35497 = state_32822__$1;
(statearr_32954_35497[(2)] = null);

(statearr_32954_35497[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (6))){
var inst_32748 = (state_32822[(27)]);
var inst_32746 = cljs.core.deref(cs);
var inst_32748__$1 = cljs.core.keys(inst_32746);
var inst_32749 = cljs.core.count(inst_32748__$1);
var inst_32750 = cljs.core.reset_BANG_(dctr,inst_32749);
var inst_32755 = cljs.core.seq(inst_32748__$1);
var inst_32756 = inst_32755;
var inst_32757 = null;
var inst_32758 = (0);
var inst_32759 = (0);
var state_32822__$1 = (function (){var statearr_32960 = state_32822;
(statearr_32960[(27)] = inst_32748__$1);

(statearr_32960[(28)] = inst_32750);

(statearr_32960[(19)] = inst_32756);

(statearr_32960[(9)] = inst_32757);

(statearr_32960[(20)] = inst_32758);

(statearr_32960[(10)] = inst_32759);

return statearr_32960;
})();
var statearr_32961_35498 = state_32822__$1;
(statearr_32961_35498[(2)] = null);

(statearr_32961_35498[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (28))){
var inst_32756 = (state_32822[(19)]);
var inst_32778 = (state_32822[(23)]);
var inst_32778__$1 = cljs.core.seq(inst_32756);
var state_32822__$1 = (function (){var statearr_32964 = state_32822;
(statearr_32964[(23)] = inst_32778__$1);

return statearr_32964;
})();
if(inst_32778__$1){
var statearr_32966_35500 = state_32822__$1;
(statearr_32966_35500[(1)] = (33));

} else {
var statearr_32967_35501 = state_32822__$1;
(statearr_32967_35501[(1)] = (34));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (25))){
var inst_32759 = (state_32822[(10)]);
var inst_32758 = (state_32822[(20)]);
var inst_32762 = (inst_32759 < inst_32758);
var inst_32763 = inst_32762;
var state_32822__$1 = state_32822;
if(cljs.core.truth_(inst_32763)){
var statearr_32968_35506 = state_32822__$1;
(statearr_32968_35506[(1)] = (27));

} else {
var statearr_32969_35507 = state_32822__$1;
(statearr_32969_35507[(1)] = (28));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (34))){
var state_32822__$1 = state_32822;
var statearr_32970_35509 = state_32822__$1;
(statearr_32970_35509[(2)] = null);

(statearr_32970_35509[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (17))){
var state_32822__$1 = state_32822;
var statearr_32971_35510 = state_32822__$1;
(statearr_32971_35510[(2)] = null);

(statearr_32971_35510[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (3))){
var inst_32819 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
return cljs.core.async.impl.ioc_helpers.return_chan(state_32822__$1,inst_32819);
} else {
if((state_val_32823 === (12))){
var inst_32742 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_32973_35513 = state_32822__$1;
(statearr_32973_35513[(2)] = inst_32742);

(statearr_32973_35513[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (2))){
var state_32822__$1 = state_32822;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32822__$1,(4),ch);
} else {
if((state_val_32823 === (23))){
var state_32822__$1 = state_32822;
var statearr_32976_35517 = state_32822__$1;
(statearr_32976_35517[(2)] = null);

(statearr_32976_35517[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (35))){
var inst_32803 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_32977_35518 = state_32822__$1;
(statearr_32977_35518[(2)] = inst_32803);

(statearr_32977_35518[(1)] = (29));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (19))){
var inst_32711 = (state_32822[(7)]);
var inst_32716 = cljs.core.chunk_first(inst_32711);
var inst_32717 = cljs.core.chunk_rest(inst_32711);
var inst_32718 = cljs.core.count(inst_32716);
var inst_32683 = inst_32717;
var inst_32684 = inst_32716;
var inst_32685 = inst_32718;
var inst_32686 = (0);
var state_32822__$1 = (function (){var statearr_32979 = state_32822;
(statearr_32979[(14)] = inst_32683);

(statearr_32979[(15)] = inst_32684);

(statearr_32979[(16)] = inst_32685);

(statearr_32979[(17)] = inst_32686);

return statearr_32979;
})();
var statearr_32981_35519 = state_32822__$1;
(statearr_32981_35519[(2)] = null);

(statearr_32981_35519[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (11))){
var inst_32683 = (state_32822[(14)]);
var inst_32711 = (state_32822[(7)]);
var inst_32711__$1 = cljs.core.seq(inst_32683);
var state_32822__$1 = (function (){var statearr_32983 = state_32822;
(statearr_32983[(7)] = inst_32711__$1);

return statearr_32983;
})();
if(inst_32711__$1){
var statearr_32987_35520 = state_32822__$1;
(statearr_32987_35520[(1)] = (16));

} else {
var statearr_32988_35521 = state_32822__$1;
(statearr_32988_35521[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (9))){
var inst_32744 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_32990_35522 = state_32822__$1;
(statearr_32990_35522[(2)] = inst_32744);

(statearr_32990_35522[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (5))){
var inst_32681 = cljs.core.deref(cs);
var inst_32682 = cljs.core.seq(inst_32681);
var inst_32683 = inst_32682;
var inst_32684 = null;
var inst_32685 = (0);
var inst_32686 = (0);
var state_32822__$1 = (function (){var statearr_32992 = state_32822;
(statearr_32992[(14)] = inst_32683);

(statearr_32992[(15)] = inst_32684);

(statearr_32992[(16)] = inst_32685);

(statearr_32992[(17)] = inst_32686);

return statearr_32992;
})();
var statearr_32993_35524 = state_32822__$1;
(statearr_32993_35524[(2)] = null);

(statearr_32993_35524[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (14))){
var state_32822__$1 = state_32822;
var statearr_32994_35525 = state_32822__$1;
(statearr_32994_35525[(2)] = null);

(statearr_32994_35525[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (45))){
var inst_32811 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_32997_35528 = state_32822__$1;
(statearr_32997_35528[(2)] = inst_32811);

(statearr_32997_35528[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (26))){
var inst_32748 = (state_32822[(27)]);
var inst_32807 = (state_32822[(2)]);
var inst_32808 = cljs.core.seq(inst_32748);
var state_32822__$1 = (function (){var statearr_32999 = state_32822;
(statearr_32999[(29)] = inst_32807);

return statearr_32999;
})();
if(inst_32808){
var statearr_33000_35530 = state_32822__$1;
(statearr_33000_35530[(1)] = (42));

} else {
var statearr_33001_35531 = state_32822__$1;
(statearr_33001_35531[(1)] = (43));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (16))){
var inst_32711 = (state_32822[(7)]);
var inst_32713 = cljs.core.chunked_seq_QMARK_(inst_32711);
var state_32822__$1 = state_32822;
if(inst_32713){
var statearr_33004_35532 = state_32822__$1;
(statearr_33004_35532[(1)] = (19));

} else {
var statearr_33006_35533 = state_32822__$1;
(statearr_33006_35533[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (38))){
var inst_32800 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_33008_35536 = state_32822__$1;
(statearr_33008_35536[(2)] = inst_32800);

(statearr_33008_35536[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (30))){
var state_32822__$1 = state_32822;
var statearr_33009_35539 = state_32822__$1;
(statearr_33009_35539[(2)] = null);

(statearr_33009_35539[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (10))){
var inst_32684 = (state_32822[(15)]);
var inst_32686 = (state_32822[(17)]);
var inst_32695 = cljs.core._nth(inst_32684,inst_32686);
var inst_32697 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_32695,(0),null);
var inst_32699 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_32695,(1),null);
var state_32822__$1 = (function (){var statearr_33010 = state_32822;
(statearr_33010[(24)] = inst_32697);

return statearr_33010;
})();
if(cljs.core.truth_(inst_32699)){
var statearr_33013_35544 = state_32822__$1;
(statearr_33013_35544[(1)] = (13));

} else {
var statearr_33014_35545 = state_32822__$1;
(statearr_33014_35545[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (18))){
var inst_32740 = (state_32822[(2)]);
var state_32822__$1 = state_32822;
var statearr_33016_35547 = state_32822__$1;
(statearr_33016_35547[(2)] = inst_32740);

(statearr_33016_35547[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (42))){
var state_32822__$1 = state_32822;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_32822__$1,(45),dchan);
} else {
if((state_val_32823 === (37))){
var inst_32778 = (state_32822[(23)]);
var inst_32790 = (state_32822[(22)]);
var inst_32673 = (state_32822[(12)]);
var inst_32790__$1 = cljs.core.first(inst_32778);
var inst_32791 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_32790__$1,inst_32673,done);
var state_32822__$1 = (function (){var statearr_33024 = state_32822;
(statearr_33024[(22)] = inst_32790__$1);

return statearr_33024;
})();
if(cljs.core.truth_(inst_32791)){
var statearr_33026_35552 = state_32822__$1;
(statearr_33026_35552[(1)] = (39));

} else {
var statearr_33027_35553 = state_32822__$1;
(statearr_33027_35553[(1)] = (40));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_32823 === (8))){
var inst_32686 = (state_32822[(17)]);
var inst_32685 = (state_32822[(16)]);
var inst_32689 = (inst_32686 < inst_32685);
var inst_32690 = inst_32689;
var state_32822__$1 = state_32822;
if(cljs.core.truth_(inst_32690)){
var statearr_33029_35554 = state_32822__$1;
(statearr_33029_35554[(1)] = (10));

} else {
var statearr_33030_35555 = state_32822__$1;
(statearr_33030_35555[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$mult_$_state_machine__29951__auto__ = null;
var cljs$core$async$mult_$_state_machine__29951__auto____0 = (function (){
var statearr_33035 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_33035[(0)] = cljs$core$async$mult_$_state_machine__29951__auto__);

(statearr_33035[(1)] = (1));

return statearr_33035;
});
var cljs$core$async$mult_$_state_machine__29951__auto____1 = (function (state_32822){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_32822);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e33037){var ex__29954__auto__ = e33037;
var statearr_33038_35562 = state_32822;
(statearr_33038_35562[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_32822[(4)]))){
var statearr_33040_35563 = state_32822;
(statearr_33040_35563[(1)] = cljs.core.first((state_32822[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35568 = state_32822;
state_32822 = G__35568;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$mult_$_state_machine__29951__auto__ = function(state_32822){
switch(arguments.length){
case 0:
return cljs$core$async$mult_$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$mult_$_state_machine__29951__auto____1.call(this,state_32822);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mult_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mult_$_state_machine__29951__auto____0;
cljs$core$async$mult_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mult_$_state_machine__29951__auto____1;
return cljs$core$async$mult_$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_33041 = f__31016__auto__();
(statearr_33041[(6)] = c__31012__auto___35429);

return statearr_33041;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return m;
});
/**
 * Copies the mult source onto the supplied channel.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.tap = (function cljs$core$async$tap(var_args){
var G__33046 = arguments.length;
switch (G__33046) {
case 2:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2 = (function (mult,ch){
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3(mult,ch,true);
}));

(cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3 = (function (mult,ch,close_QMARK_){
cljs.core.async.tap_STAR_(mult,ch,close_QMARK_);

return ch;
}));

(cljs.core.async.tap.cljs$lang$maxFixedArity = 3);

/**
 * Disconnects a target channel from a mult
 */
cljs.core.async.untap = (function cljs$core$async$untap(mult,ch){
return cljs.core.async.untap_STAR_(mult,ch);
});
/**
 * Disconnects all target channels from a mult
 */
cljs.core.async.untap_all = (function cljs$core$async$untap_all(mult){
return cljs.core.async.untap_all_STAR_(mult);
});

/**
 * @interface
 */
cljs.core.async.Mix = function(){};

var cljs$core$async$Mix$admix_STAR_$dyn_35580 = (function (m,ch){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.admix_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5374__auto__.call(null,m,ch));
} else {
var m__5372__auto__ = (cljs.core.async.admix_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5372__auto__.call(null,m,ch));
} else {
throw cljs.core.missing_protocol("Mix.admix*",m);
}
}
});
cljs.core.async.admix_STAR_ = (function cljs$core$async$admix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$admix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$admix_STAR_$arity$2(m,ch);
} else {
return cljs$core$async$Mix$admix_STAR_$dyn_35580(m,ch);
}
});

var cljs$core$async$Mix$unmix_STAR_$dyn_35583 = (function (m,ch){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.unmix_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5374__auto__.call(null,m,ch));
} else {
var m__5372__auto__ = (cljs.core.async.unmix_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5372__auto__.call(null,m,ch));
} else {
throw cljs.core.missing_protocol("Mix.unmix*",m);
}
}
});
cljs.core.async.unmix_STAR_ = (function cljs$core$async$unmix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$unmix_STAR_$arity$2(m,ch);
} else {
return cljs$core$async$Mix$unmix_STAR_$dyn_35583(m,ch);
}
});

var cljs$core$async$Mix$unmix_all_STAR_$dyn_35588 = (function (m){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.unmix_all_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5374__auto__.call(null,m));
} else {
var m__5372__auto__ = (cljs.core.async.unmix_all_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5372__auto__.call(null,m));
} else {
throw cljs.core.missing_protocol("Mix.unmix-all*",m);
}
}
});
cljs.core.async.unmix_all_STAR_ = (function cljs$core$async$unmix_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mix$unmix_all_STAR_$arity$1(m);
} else {
return cljs$core$async$Mix$unmix_all_STAR_$dyn_35588(m);
}
});

var cljs$core$async$Mix$toggle_STAR_$dyn_35595 = (function (m,state_map){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.toggle_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$2(m,state_map) : m__5374__auto__.call(null,m,state_map));
} else {
var m__5372__auto__ = (cljs.core.async.toggle_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$2(m,state_map) : m__5372__auto__.call(null,m,state_map));
} else {
throw cljs.core.missing_protocol("Mix.toggle*",m);
}
}
});
cljs.core.async.toggle_STAR_ = (function cljs$core$async$toggle_STAR_(m,state_map){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$toggle_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$toggle_STAR_$arity$2(m,state_map);
} else {
return cljs$core$async$Mix$toggle_STAR_$dyn_35595(m,state_map);
}
});

var cljs$core$async$Mix$solo_mode_STAR_$dyn_35603 = (function (m,mode){
var x__5373__auto__ = (((m == null))?null:m);
var m__5374__auto__ = (cljs.core.async.solo_mode_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$2(m,mode) : m__5374__auto__.call(null,m,mode));
} else {
var m__5372__auto__ = (cljs.core.async.solo_mode_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$2(m,mode) : m__5372__auto__.call(null,m,mode));
} else {
throw cljs.core.missing_protocol("Mix.solo-mode*",m);
}
}
});
cljs.core.async.solo_mode_STAR_ = (function cljs$core$async$solo_mode_STAR_(m,mode){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$solo_mode_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$solo_mode_STAR_$arity$2(m,mode);
} else {
return cljs$core$async$Mix$solo_mode_STAR_$dyn_35603(m,mode);
}
});

cljs.core.async.ioc_alts_BANG_ = (function cljs$core$async$ioc_alts_BANG_(var_args){
var args__5755__auto__ = [];
var len__5749__auto___35607 = arguments.length;
var i__5750__auto___35608 = (0);
while(true){
if((i__5750__auto___35608 < len__5749__auto___35607)){
args__5755__auto__.push((arguments[i__5750__auto___35608]));

var G__35609 = (i__5750__auto___35608 + (1));
i__5750__auto___35608 = G__35609;
continue;
} else {
}
break;
}

var argseq__5756__auto__ = ((((3) < args__5755__auto__.length))?(new cljs.core.IndexedSeq(args__5755__auto__.slice((3)),(0),null)):null);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__5756__auto__);
});

(cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (state,cont_block,ports,p__33103){
var map__33104 = p__33103;
var map__33104__$1 = cljs.core.__destructure_map(map__33104);
var opts = map__33104__$1;
var statearr_33105_35613 = state;
(statearr_33105_35613[(1)] = cont_block);


var temp__5825__auto__ = cljs.core.async.do_alts((function (val){
var statearr_33107_35615 = state;
(statearr_33107_35615[(2)] = val);


return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state);
}),ports,opts);
if(cljs.core.truth_(temp__5825__auto__)){
var cb = temp__5825__auto__;
var statearr_33111_35616 = state;
(statearr_33111_35616[(2)] = cljs.core.deref(cb));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}));

(cljs.core.async.ioc_alts_BANG_.cljs$lang$maxFixedArity = (3));

/** @this {Function} */
(cljs.core.async.ioc_alts_BANG_.cljs$lang$applyTo = (function (seq33094){
var G__33095 = cljs.core.first(seq33094);
var seq33094__$1 = cljs.core.next(seq33094);
var G__33096 = cljs.core.first(seq33094__$1);
var seq33094__$2 = cljs.core.next(seq33094__$1);
var G__33097 = cljs.core.first(seq33094__$2);
var seq33094__$3 = cljs.core.next(seq33094__$2);
var self__5734__auto__ = this;
return self__5734__auto__.cljs$core$IFn$_invoke$arity$variadic(G__33095,G__33096,G__33097,seq33094__$3);
}));


/**
* @constructor
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mix}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33130 = (function (change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta33131){
this.change = change;
this.solo_mode = solo_mode;
this.pick = pick;
this.cs = cs;
this.calc_state = calc_state;
this.out = out;
this.changed = changed;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.meta33131 = meta33131;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_33132,meta33131__$1){
var self__ = this;
var _33132__$1 = this;
return (new cljs.core.async.t_cljs$core$async33130(self__.change,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta33131__$1));
}));

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_33132){
var self__ = this;
var _33132__$1 = this;
return self__.meta33131;
}));

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.out;
}));

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mix$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = (function (_,state_map){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core.merge_with,cljs.core.merge),state_map);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async33130.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = (function (_,mode){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.solo_modes.cljs$core$IFn$_invoke$arity$1 ? self__.solo_modes.cljs$core$IFn$_invoke$arity$1(mode) : self__.solo_modes.call(null,mode)))){
} else {
throw (new Error(["Assert failed: ",["mode must be one of: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)].join(''),"\n","(solo-modes mode)"].join('')));
}

cljs.core.reset_BANG_(self__.solo_mode,mode);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async33130.getBasis = (function (){
return new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"change","change",477485025,null),new cljs.core.Symbol(null,"solo-mode","solo-mode",2031788074,null),new cljs.core.Symbol(null,"pick","pick",1300068175,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"calc-state","calc-state",-349968968,null),new cljs.core.Symbol(null,"out","out",729986010,null),new cljs.core.Symbol(null,"changed","changed",-2083710852,null),new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"attrs","attrs",-450137186,null),new cljs.core.Symbol(null,"meta33131","meta33131",1719080776,null)], null);
}));

(cljs.core.async.t_cljs$core$async33130.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async33130.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33130");

(cljs.core.async.t_cljs$core$async33130.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async33130");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33130.
 */
cljs.core.async.__GT_t_cljs$core$async33130 = (function cljs$core$async$__GT_t_cljs$core$async33130(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta33131){
return (new cljs.core.async.t_cljs$core$async33130(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta33131));
});


/**
 * Creates and returns a mix of one or more input channels which will
 *   be put on the supplied out channel. Input sources can be added to
 *   the mix with 'admix', and removed with 'unmix'. A mix supports
 *   soloing, muting and pausing multiple inputs atomically using
 *   'toggle', and can solo using either muting or pausing as determined
 *   by 'solo-mode'.
 * 
 *   Each channel can have zero or more boolean modes set via 'toggle':
 * 
 *   :solo - when true, only this (ond other soloed) channel(s) will appear
 *        in the mix output channel. :mute and :pause states of soloed
 *        channels are ignored. If solo-mode is :mute, non-soloed
 *        channels are muted, if :pause, non-soloed channels are
 *        paused.
 * 
 *   :mute - muted channels will have their contents consumed but not included in the mix
 *   :pause - paused channels will not have their contents consumed (and thus also not included in the mix)
 */
cljs.core.async.mix = (function cljs$core$async$mix(out){
var cs = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var solo_modes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pause","pause",-2095325672),null,new cljs.core.Keyword(null,"mute","mute",1151223646),null], null), null);
var attrs = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(solo_modes,new cljs.core.Keyword(null,"solo","solo",-316350075));
var solo_mode = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"mute","mute",1151223646));
var change = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(cljs.core.async.sliding_buffer((1)));
var changed = (function (){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(change,true);
});
var pick = (function (attr,chs){
return cljs.core.reduce_kv((function (ret,c,v){
if(cljs.core.truth_((attr.cljs$core$IFn$_invoke$arity$1 ? attr.cljs$core$IFn$_invoke$arity$1(v) : attr.call(null,v)))){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,c);
} else {
return ret;
}
}),cljs.core.PersistentHashSet.EMPTY,chs);
});
var calc_state = (function (){
var chs = cljs.core.deref(cs);
var mode = cljs.core.deref(solo_mode);
var solos = pick(new cljs.core.Keyword(null,"solo","solo",-316350075),chs);
var pauses = pick(new cljs.core.Keyword(null,"pause","pause",-2095325672),chs);
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"solos","solos",1441458643),solos,new cljs.core.Keyword(null,"mutes","mutes",1068806309),pick(new cljs.core.Keyword(null,"mute","mute",1151223646),chs),new cljs.core.Keyword(null,"reads","reads",-1215067361),cljs.core.conj.cljs$core$IFn$_invoke$arity$2(((((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mode,new cljs.core.Keyword(null,"pause","pause",-2095325672))) && (cljs.core.seq(solos))))?cljs.core.vec(solos):cljs.core.vec(cljs.core.remove.cljs$core$IFn$_invoke$arity$2(pauses,cljs.core.keys(chs)))),change)], null);
});
var m = (new cljs.core.async.t_cljs$core$async33130(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,cljs.core.PersistentArrayMap.EMPTY));
var c__31012__auto___35647 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_33220){
var state_val_33221 = (state_33220[(1)]);
if((state_val_33221 === (7))){
var inst_33178 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
if(cljs.core.truth_(inst_33178)){
var statearr_33229_35649 = state_33220__$1;
(statearr_33229_35649[(1)] = (8));

} else {
var statearr_33230_35650 = state_33220__$1;
(statearr_33230_35650[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (20))){
var inst_33168 = (state_33220[(7)]);
var state_33220__$1 = state_33220;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_33220__$1,(23),out,inst_33168);
} else {
if((state_val_33221 === (1))){
var inst_33150 = calc_state();
var inst_33151 = cljs.core.__destructure_map(inst_33150);
var inst_33152 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_33151,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_33153 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_33151,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_33154 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_33151,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var inst_33155 = inst_33150;
var state_33220__$1 = (function (){var statearr_33231 = state_33220;
(statearr_33231[(8)] = inst_33152);

(statearr_33231[(9)] = inst_33153);

(statearr_33231[(10)] = inst_33154);

(statearr_33231[(11)] = inst_33155);

return statearr_33231;
})();
var statearr_33233_35651 = state_33220__$1;
(statearr_33233_35651[(2)] = null);

(statearr_33233_35651[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (24))){
var inst_33159 = (state_33220[(12)]);
var inst_33155 = inst_33159;
var state_33220__$1 = (function (){var statearr_33235 = state_33220;
(statearr_33235[(11)] = inst_33155);

return statearr_33235;
})();
var statearr_33238_35652 = state_33220__$1;
(statearr_33238_35652[(2)] = null);

(statearr_33238_35652[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (4))){
var inst_33168 = (state_33220[(7)]);
var inst_33172 = (state_33220[(13)]);
var inst_33167 = (state_33220[(2)]);
var inst_33168__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_33167,(0),null);
var inst_33169 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_33167,(1),null);
var inst_33172__$1 = (inst_33168__$1 == null);
var state_33220__$1 = (function (){var statearr_33245 = state_33220;
(statearr_33245[(7)] = inst_33168__$1);

(statearr_33245[(14)] = inst_33169);

(statearr_33245[(13)] = inst_33172__$1);

return statearr_33245;
})();
if(cljs.core.truth_(inst_33172__$1)){
var statearr_33250_35657 = state_33220__$1;
(statearr_33250_35657[(1)] = (5));

} else {
var statearr_33251_35658 = state_33220__$1;
(statearr_33251_35658[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (15))){
var inst_33160 = (state_33220[(15)]);
var inst_33194 = (state_33220[(16)]);
var inst_33194__$1 = cljs.core.empty_QMARK_(inst_33160);
var state_33220__$1 = (function (){var statearr_33253 = state_33220;
(statearr_33253[(16)] = inst_33194__$1);

return statearr_33253;
})();
if(inst_33194__$1){
var statearr_33254_35660 = state_33220__$1;
(statearr_33254_35660[(1)] = (17));

} else {
var statearr_33255_35661 = state_33220__$1;
(statearr_33255_35661[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (21))){
var inst_33159 = (state_33220[(12)]);
var inst_33155 = inst_33159;
var state_33220__$1 = (function (){var statearr_33259 = state_33220;
(statearr_33259[(11)] = inst_33155);

return statearr_33259;
})();
var statearr_33260_35662 = state_33220__$1;
(statearr_33260_35662[(2)] = null);

(statearr_33260_35662[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (13))){
var inst_33187 = (state_33220[(2)]);
var inst_33188 = calc_state();
var inst_33155 = inst_33188;
var state_33220__$1 = (function (){var statearr_33261 = state_33220;
(statearr_33261[(17)] = inst_33187);

(statearr_33261[(11)] = inst_33155);

return statearr_33261;
})();
var statearr_33266_35665 = state_33220__$1;
(statearr_33266_35665[(2)] = null);

(statearr_33266_35665[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (22))){
var inst_33214 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
var statearr_33267_35666 = state_33220__$1;
(statearr_33267_35666[(2)] = inst_33214);

(statearr_33267_35666[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (6))){
var inst_33169 = (state_33220[(14)]);
var inst_33176 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_33169,change);
var state_33220__$1 = state_33220;
var statearr_33268_35667 = state_33220__$1;
(statearr_33268_35667[(2)] = inst_33176);

(statearr_33268_35667[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (25))){
var state_33220__$1 = state_33220;
var statearr_33269_35668 = state_33220__$1;
(statearr_33269_35668[(2)] = null);

(statearr_33269_35668[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (17))){
var inst_33161 = (state_33220[(18)]);
var inst_33169 = (state_33220[(14)]);
var inst_33196 = (inst_33161.cljs$core$IFn$_invoke$arity$1 ? inst_33161.cljs$core$IFn$_invoke$arity$1(inst_33169) : inst_33161.call(null,inst_33169));
var inst_33197 = cljs.core.not(inst_33196);
var state_33220__$1 = state_33220;
var statearr_33270_35673 = state_33220__$1;
(statearr_33270_35673[(2)] = inst_33197);

(statearr_33270_35673[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (3))){
var inst_33218 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
return cljs.core.async.impl.ioc_helpers.return_chan(state_33220__$1,inst_33218);
} else {
if((state_val_33221 === (12))){
var state_33220__$1 = state_33220;
var statearr_33271_35674 = state_33220__$1;
(statearr_33271_35674[(2)] = null);

(statearr_33271_35674[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (2))){
var inst_33155 = (state_33220[(11)]);
var inst_33159 = (state_33220[(12)]);
var inst_33159__$1 = cljs.core.__destructure_map(inst_33155);
var inst_33160 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_33159__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_33161 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_33159__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_33162 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_33159__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var state_33220__$1 = (function (){var statearr_33272 = state_33220;
(statearr_33272[(12)] = inst_33159__$1);

(statearr_33272[(15)] = inst_33160);

(statearr_33272[(18)] = inst_33161);

return statearr_33272;
})();
return cljs.core.async.ioc_alts_BANG_(state_33220__$1,(4),inst_33162);
} else {
if((state_val_33221 === (23))){
var inst_33205 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
if(cljs.core.truth_(inst_33205)){
var statearr_33273_35677 = state_33220__$1;
(statearr_33273_35677[(1)] = (24));

} else {
var statearr_33274_35678 = state_33220__$1;
(statearr_33274_35678[(1)] = (25));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (19))){
var inst_33200 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
var statearr_33275_35680 = state_33220__$1;
(statearr_33275_35680[(2)] = inst_33200);

(statearr_33275_35680[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (11))){
var inst_33169 = (state_33220[(14)]);
var inst_33184 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(cs,cljs.core.dissoc,inst_33169);
var state_33220__$1 = state_33220;
var statearr_33277_35681 = state_33220__$1;
(statearr_33277_35681[(2)] = inst_33184);

(statearr_33277_35681[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (9))){
var inst_33160 = (state_33220[(15)]);
var inst_33169 = (state_33220[(14)]);
var inst_33191 = (state_33220[(19)]);
var inst_33191__$1 = (inst_33160.cljs$core$IFn$_invoke$arity$1 ? inst_33160.cljs$core$IFn$_invoke$arity$1(inst_33169) : inst_33160.call(null,inst_33169));
var state_33220__$1 = (function (){var statearr_33282 = state_33220;
(statearr_33282[(19)] = inst_33191__$1);

return statearr_33282;
})();
if(cljs.core.truth_(inst_33191__$1)){
var statearr_33283_35683 = state_33220__$1;
(statearr_33283_35683[(1)] = (14));

} else {
var statearr_33284_35684 = state_33220__$1;
(statearr_33284_35684[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (5))){
var inst_33172 = (state_33220[(13)]);
var state_33220__$1 = state_33220;
var statearr_33288_35685 = state_33220__$1;
(statearr_33288_35685[(2)] = inst_33172);

(statearr_33288_35685[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (14))){
var inst_33191 = (state_33220[(19)]);
var state_33220__$1 = state_33220;
var statearr_33289_35686 = state_33220__$1;
(statearr_33289_35686[(2)] = inst_33191);

(statearr_33289_35686[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (26))){
var inst_33210 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
var statearr_33290_35688 = state_33220__$1;
(statearr_33290_35688[(2)] = inst_33210);

(statearr_33290_35688[(1)] = (22));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (16))){
var inst_33202 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
if(cljs.core.truth_(inst_33202)){
var statearr_33297_35699 = state_33220__$1;
(statearr_33297_35699[(1)] = (20));

} else {
var statearr_33303_35700 = state_33220__$1;
(statearr_33303_35700[(1)] = (21));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (10))){
var inst_33216 = (state_33220[(2)]);
var state_33220__$1 = state_33220;
var statearr_33307_35701 = state_33220__$1;
(statearr_33307_35701[(2)] = inst_33216);

(statearr_33307_35701[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (18))){
var inst_33194 = (state_33220[(16)]);
var state_33220__$1 = state_33220;
var statearr_33308_35703 = state_33220__$1;
(statearr_33308_35703[(2)] = inst_33194);

(statearr_33308_35703[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33221 === (8))){
var inst_33168 = (state_33220[(7)]);
var inst_33182 = (inst_33168 == null);
var state_33220__$1 = state_33220;
if(cljs.core.truth_(inst_33182)){
var statearr_33315_35704 = state_33220__$1;
(statearr_33315_35704[(1)] = (11));

} else {
var statearr_33316_35705 = state_33220__$1;
(statearr_33316_35705[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$mix_$_state_machine__29951__auto__ = null;
var cljs$core$async$mix_$_state_machine__29951__auto____0 = (function (){
var statearr_33318 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_33318[(0)] = cljs$core$async$mix_$_state_machine__29951__auto__);

(statearr_33318[(1)] = (1));

return statearr_33318;
});
var cljs$core$async$mix_$_state_machine__29951__auto____1 = (function (state_33220){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_33220);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e33319){var ex__29954__auto__ = e33319;
var statearr_33320_35706 = state_33220;
(statearr_33320_35706[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_33220[(4)]))){
var statearr_33329_35707 = state_33220;
(statearr_33329_35707[(1)] = cljs.core.first((state_33220[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35708 = state_33220;
state_33220 = G__35708;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$mix_$_state_machine__29951__auto__ = function(state_33220){
switch(arguments.length){
case 0:
return cljs$core$async$mix_$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$mix_$_state_machine__29951__auto____1.call(this,state_33220);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mix_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mix_$_state_machine__29951__auto____0;
cljs$core$async$mix_$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mix_$_state_machine__29951__auto____1;
return cljs$core$async$mix_$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_33336 = f__31016__auto__();
(statearr_33336[(6)] = c__31012__auto___35647);

return statearr_33336;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return m;
});
/**
 * Adds ch as an input to the mix
 */
cljs.core.async.admix = (function cljs$core$async$admix(mix,ch){
return cljs.core.async.admix_STAR_(mix,ch);
});
/**
 * Removes ch as an input to the mix
 */
cljs.core.async.unmix = (function cljs$core$async$unmix(mix,ch){
return cljs.core.async.unmix_STAR_(mix,ch);
});
/**
 * removes all inputs from the mix
 */
cljs.core.async.unmix_all = (function cljs$core$async$unmix_all(mix){
return cljs.core.async.unmix_all_STAR_(mix);
});
/**
 * Atomically sets the state(s) of one or more channels in a mix. The
 *   state map is a map of channels -> channel-state-map. A
 *   channel-state-map is a map of attrs -> boolean, where attr is one or
 *   more of :mute, :pause or :solo. Any states supplied are merged with
 *   the current state.
 * 
 *   Note that channels can be added to a mix via toggle, which can be
 *   used to add channels in a particular (e.g. paused) state.
 */
cljs.core.async.toggle = (function cljs$core$async$toggle(mix,state_map){
return cljs.core.async.toggle_STAR_(mix,state_map);
});
/**
 * Sets the solo mode of the mix. mode must be one of :mute or :pause
 */
cljs.core.async.solo_mode = (function cljs$core$async$solo_mode(mix,mode){
return cljs.core.async.solo_mode_STAR_(mix,mode);
});

/**
 * @interface
 */
cljs.core.async.Pub = function(){};

var cljs$core$async$Pub$sub_STAR_$dyn_35716 = (function (p,v,ch,close_QMARK_){
var x__5373__auto__ = (((p == null))?null:p);
var m__5374__auto__ = (cljs.core.async.sub_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$4(p,v,ch,close_QMARK_) : m__5374__auto__.call(null,p,v,ch,close_QMARK_));
} else {
var m__5372__auto__ = (cljs.core.async.sub_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$4(p,v,ch,close_QMARK_) : m__5372__auto__.call(null,p,v,ch,close_QMARK_));
} else {
throw cljs.core.missing_protocol("Pub.sub*",p);
}
}
});
cljs.core.async.sub_STAR_ = (function cljs$core$async$sub_STAR_(p,v,ch,close_QMARK_){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$sub_STAR_$arity$4 == null)))))){
return p.cljs$core$async$Pub$sub_STAR_$arity$4(p,v,ch,close_QMARK_);
} else {
return cljs$core$async$Pub$sub_STAR_$dyn_35716(p,v,ch,close_QMARK_);
}
});

var cljs$core$async$Pub$unsub_STAR_$dyn_35717 = (function (p,v,ch){
var x__5373__auto__ = (((p == null))?null:p);
var m__5374__auto__ = (cljs.core.async.unsub_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$3(p,v,ch) : m__5374__auto__.call(null,p,v,ch));
} else {
var m__5372__auto__ = (cljs.core.async.unsub_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$3(p,v,ch) : m__5372__auto__.call(null,p,v,ch));
} else {
throw cljs.core.missing_protocol("Pub.unsub*",p);
}
}
});
cljs.core.async.unsub_STAR_ = (function cljs$core$async$unsub_STAR_(p,v,ch){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_STAR_$arity$3 == null)))))){
return p.cljs$core$async$Pub$unsub_STAR_$arity$3(p,v,ch);
} else {
return cljs$core$async$Pub$unsub_STAR_$dyn_35717(p,v,ch);
}
});

var cljs$core$async$Pub$unsub_all_STAR_$dyn_35720 = (function() {
var G__35721 = null;
var G__35721__1 = (function (p){
var x__5373__auto__ = (((p == null))?null:p);
var m__5374__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$1(p) : m__5374__auto__.call(null,p));
} else {
var m__5372__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$1(p) : m__5372__auto__.call(null,p));
} else {
throw cljs.core.missing_protocol("Pub.unsub-all*",p);
}
}
});
var G__35721__2 = (function (p,v){
var x__5373__auto__ = (((p == null))?null:p);
var m__5374__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__5373__auto__)]);
if((!((m__5374__auto__ == null)))){
return (m__5374__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5374__auto__.cljs$core$IFn$_invoke$arity$2(p,v) : m__5374__auto__.call(null,p,v));
} else {
var m__5372__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__5372__auto__ == null)))){
return (m__5372__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5372__auto__.cljs$core$IFn$_invoke$arity$2(p,v) : m__5372__auto__.call(null,p,v));
} else {
throw cljs.core.missing_protocol("Pub.unsub-all*",p);
}
}
});
G__35721 = function(p,v){
switch(arguments.length){
case 1:
return G__35721__1.call(this,p);
case 2:
return G__35721__2.call(this,p,v);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
G__35721.cljs$core$IFn$_invoke$arity$1 = G__35721__1;
G__35721.cljs$core$IFn$_invoke$arity$2 = G__35721__2;
return G__35721;
})()
;
cljs.core.async.unsub_all_STAR_ = (function cljs$core$async$unsub_all_STAR_(var_args){
var G__33375 = arguments.length;
switch (G__33375) {
case 1:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1 = (function (p){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$1 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$1(p);
} else {
return cljs$core$async$Pub$unsub_all_STAR_$dyn_35720(p);
}
}));

(cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (p,v){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$2 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else {
return cljs$core$async$Pub$unsub_all_STAR_$dyn_35720(p,v);
}
}));

(cljs.core.async.unsub_all_STAR_.cljs$lang$maxFixedArity = 2);



/**
* @constructor
 * @implements {cljs.core.async.Pub}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33385 = (function (ch,topic_fn,buf_fn,mults,ensure_mult,meta33386){
this.ch = ch;
this.topic_fn = topic_fn;
this.buf_fn = buf_fn;
this.mults = mults;
this.ensure_mult = ensure_mult;
this.meta33386 = meta33386;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_33387,meta33386__$1){
var self__ = this;
var _33387__$1 = this;
return (new cljs.core.async.t_cljs$core$async33385(self__.ch,self__.topic_fn,self__.buf_fn,self__.mults,self__.ensure_mult,meta33386__$1));
}));

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_33387){
var self__ = this;
var _33387__$1 = this;
return self__.meta33386;
}));

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$async$Pub$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = (function (p,topic,ch__$1,close_QMARK_){
var self__ = this;
var p__$1 = this;
var m = (self__.ensure_mult.cljs$core$IFn$_invoke$arity$1 ? self__.ensure_mult.cljs$core$IFn$_invoke$arity$1(topic) : self__.ensure_mult.call(null,topic));
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3(m,ch__$1,close_QMARK_);
}));

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = (function (p,topic,ch__$1){
var self__ = this;
var p__$1 = this;
var temp__5825__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(self__.mults),topic);
if(cljs.core.truth_(temp__5825__auto__)){
var m = temp__5825__auto__;
return cljs.core.async.untap(m,ch__$1);
} else {
return null;
}
}));

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.reset_BANG_(self__.mults,cljs.core.PersistentArrayMap.EMPTY);
}));

(cljs.core.async.t_cljs$core$async33385.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = (function (_,topic){
var self__ = this;
var ___$1 = this;
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.mults,cljs.core.dissoc,topic);
}));

(cljs.core.async.t_cljs$core$async33385.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"topic-fn","topic-fn",-862449736,null),new cljs.core.Symbol(null,"buf-fn","buf-fn",-1200281591,null),new cljs.core.Symbol(null,"mults","mults",-461114485,null),new cljs.core.Symbol(null,"ensure-mult","ensure-mult",1796584816,null),new cljs.core.Symbol(null,"meta33386","meta33386",1987997787,null)], null);
}));

(cljs.core.async.t_cljs$core$async33385.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async33385.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33385");

(cljs.core.async.t_cljs$core$async33385.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async33385");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33385.
 */
cljs.core.async.__GT_t_cljs$core$async33385 = (function cljs$core$async$__GT_t_cljs$core$async33385(ch,topic_fn,buf_fn,mults,ensure_mult,meta33386){
return (new cljs.core.async.t_cljs$core$async33385(ch,topic_fn,buf_fn,mults,ensure_mult,meta33386));
});


/**
 * Creates and returns a pub(lication) of the supplied channel,
 *   partitioned into topics by the topic-fn. topic-fn will be applied to
 *   each value on the channel and the result will determine the 'topic'
 *   on which that value will be put. Channels can be subscribed to
 *   receive copies of topics using 'sub', and unsubscribed using
 *   'unsub'. Each topic will be handled by an internal mult on a
 *   dedicated channel. By default these internal channels are
 *   unbuffered, but a buf-fn can be supplied which, given a topic,
 *   creates a buffer with desired properties.
 * 
 *   Each item is distributed to all subs in parallel and synchronously,
 *   i.e. each sub must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow subs from holding up the pub.
 * 
 *   Items received when there are no matching subs get dropped.
 * 
 *   Note that if buf-fns are used then each topic is handled
 *   asynchronously, i.e. if a channel is subscribed to more than one
 *   topic it should not expect them to be interleaved identically with
 *   the source.
 */
cljs.core.async.pub = (function cljs$core$async$pub(var_args){
var G__33384 = arguments.length;
switch (G__33384) {
case 2:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2 = (function (ch,topic_fn){
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3(ch,topic_fn,cljs.core.constantly(null));
}));

(cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3 = (function (ch,topic_fn,buf_fn){
var mults = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var ensure_mult = (function (topic){
var or__5025__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(mults),topic);
if(cljs.core.truth_(or__5025__auto__)){
return or__5025__auto__;
} else {
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(mults,(function (p1__33378_SHARP_){
if(cljs.core.truth_((p1__33378_SHARP_.cljs$core$IFn$_invoke$arity$1 ? p1__33378_SHARP_.cljs$core$IFn$_invoke$arity$1(topic) : p1__33378_SHARP_.call(null,topic)))){
return p1__33378_SHARP_;
} else {
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(p1__33378_SHARP_,topic,cljs.core.async.mult(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((buf_fn.cljs$core$IFn$_invoke$arity$1 ? buf_fn.cljs$core$IFn$_invoke$arity$1(topic) : buf_fn.call(null,topic)))));
}
})),topic);
}
});
var p = (new cljs.core.async.t_cljs$core$async33385(ch,topic_fn,buf_fn,mults,ensure_mult,cljs.core.PersistentArrayMap.EMPTY));
var c__31012__auto___35749 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_33493){
var state_val_33494 = (state_33493[(1)]);
if((state_val_33494 === (7))){
var inst_33489 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
var statearr_33497_35750 = state_33493__$1;
(statearr_33497_35750[(2)] = inst_33489);

(statearr_33497_35750[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (20))){
var state_33493__$1 = state_33493;
var statearr_33503_35752 = state_33493__$1;
(statearr_33503_35752[(2)] = null);

(statearr_33503_35752[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (1))){
var state_33493__$1 = state_33493;
var statearr_33505_35753 = state_33493__$1;
(statearr_33505_35753[(2)] = null);

(statearr_33505_35753[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (24))){
var inst_33469 = (state_33493[(7)]);
var inst_33481 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(mults,cljs.core.dissoc,inst_33469);
var state_33493__$1 = state_33493;
var statearr_33506_35759 = state_33493__$1;
(statearr_33506_35759[(2)] = inst_33481);

(statearr_33506_35759[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (4))){
var inst_33412 = (state_33493[(8)]);
var inst_33412__$1 = (state_33493[(2)]);
var inst_33413 = (inst_33412__$1 == null);
var state_33493__$1 = (function (){var statearr_33507 = state_33493;
(statearr_33507[(8)] = inst_33412__$1);

return statearr_33507;
})();
if(cljs.core.truth_(inst_33413)){
var statearr_33508_35762 = state_33493__$1;
(statearr_33508_35762[(1)] = (5));

} else {
var statearr_33511_35763 = state_33493__$1;
(statearr_33511_35763[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (15))){
var inst_33457 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
var statearr_33512_35768 = state_33493__$1;
(statearr_33512_35768[(2)] = inst_33457);

(statearr_33512_35768[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (21))){
var inst_33486 = (state_33493[(2)]);
var state_33493__$1 = (function (){var statearr_33515 = state_33493;
(statearr_33515[(9)] = inst_33486);

return statearr_33515;
})();
var statearr_33516_35771 = state_33493__$1;
(statearr_33516_35771[(2)] = null);

(statearr_33516_35771[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (13))){
var inst_33438 = (state_33493[(10)]);
var inst_33440 = cljs.core.chunked_seq_QMARK_(inst_33438);
var state_33493__$1 = state_33493;
if(inst_33440){
var statearr_33517_35776 = state_33493__$1;
(statearr_33517_35776[(1)] = (16));

} else {
var statearr_33518_35777 = state_33493__$1;
(statearr_33518_35777[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (22))){
var inst_33475 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
if(cljs.core.truth_(inst_33475)){
var statearr_33519_35778 = state_33493__$1;
(statearr_33519_35778[(1)] = (23));

} else {
var statearr_33520_35779 = state_33493__$1;
(statearr_33520_35779[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (6))){
var inst_33412 = (state_33493[(8)]);
var inst_33469 = (state_33493[(7)]);
var inst_33471 = (state_33493[(11)]);
var inst_33469__$1 = (topic_fn.cljs$core$IFn$_invoke$arity$1 ? topic_fn.cljs$core$IFn$_invoke$arity$1(inst_33412) : topic_fn.call(null,inst_33412));
var inst_33470 = cljs.core.deref(mults);
var inst_33471__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_33470,inst_33469__$1);
var state_33493__$1 = (function (){var statearr_33523 = state_33493;
(statearr_33523[(7)] = inst_33469__$1);

(statearr_33523[(11)] = inst_33471__$1);

return statearr_33523;
})();
if(cljs.core.truth_(inst_33471__$1)){
var statearr_33524_35789 = state_33493__$1;
(statearr_33524_35789[(1)] = (19));

} else {
var statearr_33525_35790 = state_33493__$1;
(statearr_33525_35790[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (25))){
var inst_33483 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
var statearr_33529_35791 = state_33493__$1;
(statearr_33529_35791[(2)] = inst_33483);

(statearr_33529_35791[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (17))){
var inst_33438 = (state_33493[(10)]);
var inst_33447 = cljs.core.first(inst_33438);
var inst_33448 = cljs.core.async.muxch_STAR_(inst_33447);
var inst_33449 = cljs.core.async.close_BANG_(inst_33448);
var inst_33451 = cljs.core.next(inst_33438);
var inst_33423 = inst_33451;
var inst_33424 = null;
var inst_33425 = (0);
var inst_33426 = (0);
var state_33493__$1 = (function (){var statearr_33532 = state_33493;
(statearr_33532[(12)] = inst_33449);

(statearr_33532[(13)] = inst_33423);

(statearr_33532[(14)] = inst_33424);

(statearr_33532[(15)] = inst_33425);

(statearr_33532[(16)] = inst_33426);

return statearr_33532;
})();
var statearr_33533_35798 = state_33493__$1;
(statearr_33533_35798[(2)] = null);

(statearr_33533_35798[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (3))){
var inst_33491 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
return cljs.core.async.impl.ioc_helpers.return_chan(state_33493__$1,inst_33491);
} else {
if((state_val_33494 === (12))){
var inst_33459 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
var statearr_33534_35808 = state_33493__$1;
(statearr_33534_35808[(2)] = inst_33459);

(statearr_33534_35808[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (2))){
var state_33493__$1 = state_33493;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_33493__$1,(4),ch);
} else {
if((state_val_33494 === (23))){
var state_33493__$1 = state_33493;
var statearr_33537_35809 = state_33493__$1;
(statearr_33537_35809[(2)] = null);

(statearr_33537_35809[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (19))){
var inst_33471 = (state_33493[(11)]);
var inst_33412 = (state_33493[(8)]);
var inst_33473 = cljs.core.async.muxch_STAR_(inst_33471);
var state_33493__$1 = state_33493;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_33493__$1,(22),inst_33473,inst_33412);
} else {
if((state_val_33494 === (11))){
var inst_33423 = (state_33493[(13)]);
var inst_33438 = (state_33493[(10)]);
var inst_33438__$1 = cljs.core.seq(inst_33423);
var state_33493__$1 = (function (){var statearr_33539 = state_33493;
(statearr_33539[(10)] = inst_33438__$1);

return statearr_33539;
})();
if(inst_33438__$1){
var statearr_33540_35819 = state_33493__$1;
(statearr_33540_35819[(1)] = (13));

} else {
var statearr_33541_35822 = state_33493__$1;
(statearr_33541_35822[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (9))){
var inst_33461 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
var statearr_33542_35825 = state_33493__$1;
(statearr_33542_35825[(2)] = inst_33461);

(statearr_33542_35825[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (5))){
var inst_33420 = cljs.core.deref(mults);
var inst_33421 = cljs.core.vals(inst_33420);
var inst_33422 = cljs.core.seq(inst_33421);
var inst_33423 = inst_33422;
var inst_33424 = null;
var inst_33425 = (0);
var inst_33426 = (0);
var state_33493__$1 = (function (){var statearr_33545 = state_33493;
(statearr_33545[(13)] = inst_33423);

(statearr_33545[(14)] = inst_33424);

(statearr_33545[(15)] = inst_33425);

(statearr_33545[(16)] = inst_33426);

return statearr_33545;
})();
var statearr_33546_35827 = state_33493__$1;
(statearr_33546_35827[(2)] = null);

(statearr_33546_35827[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (14))){
var state_33493__$1 = state_33493;
var statearr_33550_35829 = state_33493__$1;
(statearr_33550_35829[(2)] = null);

(statearr_33550_35829[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (16))){
var inst_33438 = (state_33493[(10)]);
var inst_33442 = cljs.core.chunk_first(inst_33438);
var inst_33443 = cljs.core.chunk_rest(inst_33438);
var inst_33444 = cljs.core.count(inst_33442);
var inst_33423 = inst_33443;
var inst_33424 = inst_33442;
var inst_33425 = inst_33444;
var inst_33426 = (0);
var state_33493__$1 = (function (){var statearr_33551 = state_33493;
(statearr_33551[(13)] = inst_33423);

(statearr_33551[(14)] = inst_33424);

(statearr_33551[(15)] = inst_33425);

(statearr_33551[(16)] = inst_33426);

return statearr_33551;
})();
var statearr_33552_35835 = state_33493__$1;
(statearr_33552_35835[(2)] = null);

(statearr_33552_35835[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (10))){
var inst_33424 = (state_33493[(14)]);
var inst_33426 = (state_33493[(16)]);
var inst_33423 = (state_33493[(13)]);
var inst_33425 = (state_33493[(15)]);
var inst_33432 = cljs.core._nth(inst_33424,inst_33426);
var inst_33433 = cljs.core.async.muxch_STAR_(inst_33432);
var inst_33434 = cljs.core.async.close_BANG_(inst_33433);
var inst_33435 = (inst_33426 + (1));
var tmp33547 = inst_33425;
var tmp33548 = inst_33424;
var tmp33549 = inst_33423;
var inst_33423__$1 = tmp33549;
var inst_33424__$1 = tmp33548;
var inst_33425__$1 = tmp33547;
var inst_33426__$1 = inst_33435;
var state_33493__$1 = (function (){var statearr_33555 = state_33493;
(statearr_33555[(17)] = inst_33434);

(statearr_33555[(13)] = inst_33423__$1);

(statearr_33555[(14)] = inst_33424__$1);

(statearr_33555[(15)] = inst_33425__$1);

(statearr_33555[(16)] = inst_33426__$1);

return statearr_33555;
})();
var statearr_33556_35842 = state_33493__$1;
(statearr_33556_35842[(2)] = null);

(statearr_33556_35842[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (18))){
var inst_33454 = (state_33493[(2)]);
var state_33493__$1 = state_33493;
var statearr_33557_35849 = state_33493__$1;
(statearr_33557_35849[(2)] = inst_33454);

(statearr_33557_35849[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33494 === (8))){
var inst_33426 = (state_33493[(16)]);
var inst_33425 = (state_33493[(15)]);
var inst_33429 = (inst_33426 < inst_33425);
var inst_33430 = inst_33429;
var state_33493__$1 = state_33493;
if(cljs.core.truth_(inst_33430)){
var statearr_33558_35851 = state_33493__$1;
(statearr_33558_35851[(1)] = (10));

} else {
var statearr_33559_35853 = state_33493__$1;
(statearr_33559_35853[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_33567 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_33567[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_33567[(1)] = (1));

return statearr_33567;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_33493){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_33493);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e33568){var ex__29954__auto__ = e33568;
var statearr_33569_35862 = state_33493;
(statearr_33569_35862[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_33493[(4)]))){
var statearr_33570_35863 = state_33493;
(statearr_33570_35863[(1)] = cljs.core.first((state_33493[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35866 = state_33493;
state_33493 = G__35866;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_33493){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_33493);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_33574 = f__31016__auto__();
(statearr_33574[(6)] = c__31012__auto___35749);

return statearr_33574;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return p;
}));

(cljs.core.async.pub.cljs$lang$maxFixedArity = 3);

/**
 * Subscribes a channel to a topic of a pub.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.sub = (function cljs$core$async$sub(var_args){
var G__33576 = arguments.length;
switch (G__33576) {
case 3:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3 = (function (p,topic,ch){
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4(p,topic,ch,true);
}));

(cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4 = (function (p,topic,ch,close_QMARK_){
return cljs.core.async.sub_STAR_(p,topic,ch,close_QMARK_);
}));

(cljs.core.async.sub.cljs$lang$maxFixedArity = 4);

/**
 * Unsubscribes a channel from a topic of a pub
 */
cljs.core.async.unsub = (function cljs$core$async$unsub(p,topic,ch){
return cljs.core.async.unsub_STAR_(p,topic,ch);
});
/**
 * Unsubscribes all channels from a pub, or a topic of a pub
 */
cljs.core.async.unsub_all = (function cljs$core$async$unsub_all(var_args){
var G__33591 = arguments.length;
switch (G__33591) {
case 1:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1 = (function (p){
return cljs.core.async.unsub_all_STAR_(p);
}));

(cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2 = (function (p,topic){
return cljs.core.async.unsub_all_STAR_(p,topic);
}));

(cljs.core.async.unsub_all.cljs$lang$maxFixedArity = 2);

/**
 * Takes a function and a collection of source channels, and returns a
 *   channel which contains the values produced by applying f to the set
 *   of first items taken from each source channel, followed by applying
 *   f to the set of second items from each channel, until any one of the
 *   channels is closed, at which point the output channel will be
 *   closed. The returned channel will be unbuffered by default, or a
 *   buf-or-n can be supplied
 */
cljs.core.async.map = (function cljs$core$async$map(var_args){
var G__33604 = arguments.length;
switch (G__33604) {
case 2:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.map.cljs$core$IFn$_invoke$arity$2 = (function (f,chs){
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$3(f,chs,null);
}));

(cljs.core.async.map.cljs$core$IFn$_invoke$arity$3 = (function (f,chs,buf_or_n){
var chs__$1 = cljs.core.vec(chs);
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var cnt = cljs.core.count(chs__$1);
var rets = cljs.core.object_array.cljs$core$IFn$_invoke$arity$1(cnt);
var dchan = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var dctr = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var done = cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (i){
return (function (ret){
(rets[i] = ret);

if((cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(dchan,rets.slice((0)));
} else {
return null;
}
});
}),cljs.core.range.cljs$core$IFn$_invoke$arity$1(cnt));
if((cnt === (0))){
cljs.core.async.close_BANG_(out);
} else {
var c__31012__auto___35883 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_33674){
var state_val_33675 = (state_33674[(1)]);
if((state_val_33675 === (7))){
var state_33674__$1 = state_33674;
var statearr_33679_35884 = state_33674__$1;
(statearr_33679_35884[(2)] = null);

(statearr_33679_35884[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (1))){
var state_33674__$1 = state_33674;
var statearr_33680_35887 = state_33674__$1;
(statearr_33680_35887[(2)] = null);

(statearr_33680_35887[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (4))){
var inst_33633 = (state_33674[(7)]);
var inst_33632 = (state_33674[(8)]);
var inst_33635 = (inst_33633 < inst_33632);
var state_33674__$1 = state_33674;
if(cljs.core.truth_(inst_33635)){
var statearr_33681_35890 = state_33674__$1;
(statearr_33681_35890[(1)] = (6));

} else {
var statearr_33682_35891 = state_33674__$1;
(statearr_33682_35891[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (15))){
var inst_33660 = (state_33674[(9)]);
var inst_33665 = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(f,inst_33660);
var state_33674__$1 = state_33674;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_33674__$1,(17),out,inst_33665);
} else {
if((state_val_33675 === (13))){
var inst_33660 = (state_33674[(9)]);
var inst_33660__$1 = (state_33674[(2)]);
var inst_33661 = cljs.core.some(cljs.core.nil_QMARK_,inst_33660__$1);
var state_33674__$1 = (function (){var statearr_33683 = state_33674;
(statearr_33683[(9)] = inst_33660__$1);

return statearr_33683;
})();
if(cljs.core.truth_(inst_33661)){
var statearr_33684_35894 = state_33674__$1;
(statearr_33684_35894[(1)] = (14));

} else {
var statearr_33685_35895 = state_33674__$1;
(statearr_33685_35895[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (6))){
var state_33674__$1 = state_33674;
var statearr_33686_35901 = state_33674__$1;
(statearr_33686_35901[(2)] = null);

(statearr_33686_35901[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (17))){
var inst_33667 = (state_33674[(2)]);
var state_33674__$1 = (function (){var statearr_33691 = state_33674;
(statearr_33691[(10)] = inst_33667);

return statearr_33691;
})();
var statearr_33692_35903 = state_33674__$1;
(statearr_33692_35903[(2)] = null);

(statearr_33692_35903[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (3))){
var inst_33672 = (state_33674[(2)]);
var state_33674__$1 = state_33674;
return cljs.core.async.impl.ioc_helpers.return_chan(state_33674__$1,inst_33672);
} else {
if((state_val_33675 === (12))){
var _ = (function (){var statearr_33693 = state_33674;
(statearr_33693[(4)] = cljs.core.rest((state_33674[(4)])));

return statearr_33693;
})();
var state_33674__$1 = state_33674;
var ex33690 = (state_33674__$1[(2)]);
var statearr_33699_35906 = state_33674__$1;
(statearr_33699_35906[(5)] = ex33690);


if((ex33690 instanceof Object)){
var statearr_33706_35907 = state_33674__$1;
(statearr_33706_35907[(1)] = (11));

(statearr_33706_35907[(5)] = null);

} else {
throw ex33690;

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (2))){
var inst_33631 = cljs.core.reset_BANG_(dctr,cnt);
var inst_33632 = cnt;
var inst_33633 = (0);
var state_33674__$1 = (function (){var statearr_33709 = state_33674;
(statearr_33709[(11)] = inst_33631);

(statearr_33709[(8)] = inst_33632);

(statearr_33709[(7)] = inst_33633);

return statearr_33709;
})();
var statearr_33711_35908 = state_33674__$1;
(statearr_33711_35908[(2)] = null);

(statearr_33711_35908[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (11))){
var inst_33639 = (state_33674[(2)]);
var inst_33640 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec);
var state_33674__$1 = (function (){var statearr_33712 = state_33674;
(statearr_33712[(12)] = inst_33639);

return statearr_33712;
})();
var statearr_33717_35912 = state_33674__$1;
(statearr_33717_35912[(2)] = inst_33640);

(statearr_33717_35912[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (9))){
var inst_33633 = (state_33674[(7)]);
var _ = (function (){var statearr_33718 = state_33674;
(statearr_33718[(4)] = cljs.core.cons((12),(state_33674[(4)])));

return statearr_33718;
})();
var inst_33646 = (chs__$1.cljs$core$IFn$_invoke$arity$1 ? chs__$1.cljs$core$IFn$_invoke$arity$1(inst_33633) : chs__$1.call(null,inst_33633));
var inst_33647 = (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(inst_33633) : done.call(null,inst_33633));
var inst_33648 = cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2(inst_33646,inst_33647);
var ___$1 = (function (){var statearr_33719 = state_33674;
(statearr_33719[(4)] = cljs.core.rest((state_33674[(4)])));

return statearr_33719;
})();
var state_33674__$1 = state_33674;
var statearr_33720_35921 = state_33674__$1;
(statearr_33720_35921[(2)] = inst_33648);

(statearr_33720_35921[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (5))){
var inst_33658 = (state_33674[(2)]);
var state_33674__$1 = (function (){var statearr_33721 = state_33674;
(statearr_33721[(13)] = inst_33658);

return statearr_33721;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_33674__$1,(13),dchan);
} else {
if((state_val_33675 === (14))){
var inst_33663 = cljs.core.async.close_BANG_(out);
var state_33674__$1 = state_33674;
var statearr_33722_35924 = state_33674__$1;
(statearr_33722_35924[(2)] = inst_33663);

(statearr_33722_35924[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (16))){
var inst_33670 = (state_33674[(2)]);
var state_33674__$1 = state_33674;
var statearr_33723_35925 = state_33674__$1;
(statearr_33723_35925[(2)] = inst_33670);

(statearr_33723_35925[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (10))){
var inst_33633 = (state_33674[(7)]);
var inst_33651 = (state_33674[(2)]);
var inst_33652 = (inst_33633 + (1));
var inst_33633__$1 = inst_33652;
var state_33674__$1 = (function (){var statearr_33724 = state_33674;
(statearr_33724[(14)] = inst_33651);

(statearr_33724[(7)] = inst_33633__$1);

return statearr_33724;
})();
var statearr_33725_35929 = state_33674__$1;
(statearr_33725_35929[(2)] = null);

(statearr_33725_35929[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33675 === (8))){
var inst_33656 = (state_33674[(2)]);
var state_33674__$1 = state_33674;
var statearr_33727_35932 = state_33674__$1;
(statearr_33727_35932[(2)] = inst_33656);

(statearr_33727_35932[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_33729 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_33729[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_33729[(1)] = (1));

return statearr_33729;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_33674){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_33674);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e33730){var ex__29954__auto__ = e33730;
var statearr_33731_35939 = state_33674;
(statearr_33731_35939[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_33674[(4)]))){
var statearr_33732_35941 = state_33674;
(statearr_33732_35941[(1)] = cljs.core.first((state_33674[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35947 = state_33674;
state_33674 = G__35947;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_33674){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_33674);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_33733 = f__31016__auto__();
(statearr_33733[(6)] = c__31012__auto___35883);

return statearr_33733;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));

}

return out;
}));

(cljs.core.async.map.cljs$lang$maxFixedArity = 3);

/**
 * Takes a collection of source channels and returns a channel which
 *   contains all values taken from them. The returned channel will be
 *   unbuffered by default, or a buf-or-n can be supplied. The channel
 *   will close after all the source channels have closed.
 */
cljs.core.async.merge = (function cljs$core$async$merge(var_args){
var G__33739 = arguments.length;
switch (G__33739) {
case 1:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1 = (function (chs){
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2(chs,null);
}));

(cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2 = (function (chs,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__31012__auto___35957 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_33775){
var state_val_33776 = (state_33775[(1)]);
if((state_val_33776 === (7))){
var inst_33753 = (state_33775[(7)]);
var inst_33754 = (state_33775[(8)]);
var inst_33753__$1 = (state_33775[(2)]);
var inst_33754__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_33753__$1,(0),null);
var inst_33755 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_33753__$1,(1),null);
var inst_33756 = (inst_33754__$1 == null);
var state_33775__$1 = (function (){var statearr_33794 = state_33775;
(statearr_33794[(7)] = inst_33753__$1);

(statearr_33794[(8)] = inst_33754__$1);

(statearr_33794[(9)] = inst_33755);

return statearr_33794;
})();
if(cljs.core.truth_(inst_33756)){
var statearr_33795_35963 = state_33775__$1;
(statearr_33795_35963[(1)] = (8));

} else {
var statearr_33797_35964 = state_33775__$1;
(statearr_33797_35964[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33776 === (1))){
var inst_33743 = cljs.core.vec(chs);
var inst_33744 = inst_33743;
var state_33775__$1 = (function (){var statearr_33798 = state_33775;
(statearr_33798[(10)] = inst_33744);

return statearr_33798;
})();
var statearr_33799_35966 = state_33775__$1;
(statearr_33799_35966[(2)] = null);

(statearr_33799_35966[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33776 === (4))){
var inst_33744 = (state_33775[(10)]);
var state_33775__$1 = state_33775;
return cljs.core.async.ioc_alts_BANG_(state_33775__$1,(7),inst_33744);
} else {
if((state_val_33776 === (6))){
var inst_33771 = (state_33775[(2)]);
var state_33775__$1 = state_33775;
var statearr_33808_35969 = state_33775__$1;
(statearr_33808_35969[(2)] = inst_33771);

(statearr_33808_35969[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33776 === (3))){
var inst_33773 = (state_33775[(2)]);
var state_33775__$1 = state_33775;
return cljs.core.async.impl.ioc_helpers.return_chan(state_33775__$1,inst_33773);
} else {
if((state_val_33776 === (2))){
var inst_33744 = (state_33775[(10)]);
var inst_33746 = cljs.core.count(inst_33744);
var inst_33747 = (inst_33746 > (0));
var state_33775__$1 = state_33775;
if(cljs.core.truth_(inst_33747)){
var statearr_33814_35973 = state_33775__$1;
(statearr_33814_35973[(1)] = (4));

} else {
var statearr_33815_35974 = state_33775__$1;
(statearr_33815_35974[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33776 === (11))){
var inst_33744 = (state_33775[(10)]);
var inst_33763 = (state_33775[(2)]);
var tmp33811 = inst_33744;
var inst_33744__$1 = tmp33811;
var state_33775__$1 = (function (){var statearr_33816 = state_33775;
(statearr_33816[(11)] = inst_33763);

(statearr_33816[(10)] = inst_33744__$1);

return statearr_33816;
})();
var statearr_33817_35976 = state_33775__$1;
(statearr_33817_35976[(2)] = null);

(statearr_33817_35976[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33776 === (9))){
var inst_33754 = (state_33775[(8)]);
var state_33775__$1 = state_33775;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_33775__$1,(11),out,inst_33754);
} else {
if((state_val_33776 === (5))){
var inst_33769 = cljs.core.async.close_BANG_(out);
var state_33775__$1 = state_33775;
var statearr_33821_35977 = state_33775__$1;
(statearr_33821_35977[(2)] = inst_33769);

(statearr_33821_35977[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33776 === (10))){
var inst_33766 = (state_33775[(2)]);
var state_33775__$1 = state_33775;
var statearr_33822_35978 = state_33775__$1;
(statearr_33822_35978[(2)] = inst_33766);

(statearr_33822_35978[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33776 === (8))){
var inst_33744 = (state_33775[(10)]);
var inst_33753 = (state_33775[(7)]);
var inst_33754 = (state_33775[(8)]);
var inst_33755 = (state_33775[(9)]);
var inst_33758 = (function (){var cs = inst_33744;
var vec__33749 = inst_33753;
var v = inst_33754;
var c = inst_33755;
return (function (p1__33737_SHARP_){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(c,p1__33737_SHARP_);
});
})();
var inst_33759 = cljs.core.filterv(inst_33758,inst_33744);
var inst_33744__$1 = inst_33759;
var state_33775__$1 = (function (){var statearr_33832 = state_33775;
(statearr_33832[(10)] = inst_33744__$1);

return statearr_33832;
})();
var statearr_33833_35986 = state_33775__$1;
(statearr_33833_35986[(2)] = null);

(statearr_33833_35986[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_33834 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_33834[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_33834[(1)] = (1));

return statearr_33834;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_33775){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_33775);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e33835){var ex__29954__auto__ = e33835;
var statearr_33836_35996 = state_33775;
(statearr_33836_35996[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_33775[(4)]))){
var statearr_33837_35998 = state_33775;
(statearr_33837_35998[(1)] = cljs.core.first((state_33775[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35999 = state_33775;
state_33775 = G__35999;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_33775){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_33775);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_33839 = f__31016__auto__();
(statearr_33839[(6)] = c__31012__auto___35957);

return statearr_33839;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return out;
}));

(cljs.core.async.merge.cljs$lang$maxFixedArity = 2);

/**
 * Returns a channel containing the single (collection) result of the
 *   items taken from the channel conjoined to the supplied
 *   collection. ch must close before into produces a result.
 */
cljs.core.async.into = (function cljs$core$async$into(coll,ch){
return cljs.core.async.reduce(cljs.core.conj,coll,ch);
});
/**
 * Returns a channel that will return, at most, n items from ch. After n items
 * have been returned, or ch has been closed, the return chanel will close.
 * 
 *   The output channel is unbuffered by default, unless buf-or-n is given.
 */
cljs.core.async.take = (function cljs$core$async$take(var_args){
var G__33846 = arguments.length;
switch (G__33846) {
case 2:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.take.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$3(n,ch,null);
}));

(cljs.core.async.take.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__31012__auto___36003 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_33883){
var state_val_33884 = (state_33883[(1)]);
if((state_val_33884 === (7))){
var inst_33858 = (state_33883[(7)]);
var inst_33858__$1 = (state_33883[(2)]);
var inst_33866 = (inst_33858__$1 == null);
var inst_33867 = cljs.core.not(inst_33866);
var state_33883__$1 = (function (){var statearr_33889 = state_33883;
(statearr_33889[(7)] = inst_33858__$1);

return statearr_33889;
})();
if(inst_33867){
var statearr_33899_36005 = state_33883__$1;
(statearr_33899_36005[(1)] = (8));

} else {
var statearr_33900_36006 = state_33883__$1;
(statearr_33900_36006[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (1))){
var inst_33851 = (0);
var state_33883__$1 = (function (){var statearr_33904 = state_33883;
(statearr_33904[(8)] = inst_33851);

return statearr_33904;
})();
var statearr_33905_36007 = state_33883__$1;
(statearr_33905_36007[(2)] = null);

(statearr_33905_36007[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (4))){
var state_33883__$1 = state_33883;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_33883__$1,(7),ch);
} else {
if((state_val_33884 === (6))){
var inst_33878 = (state_33883[(2)]);
var state_33883__$1 = state_33883;
var statearr_33906_36012 = state_33883__$1;
(statearr_33906_36012[(2)] = inst_33878);

(statearr_33906_36012[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (3))){
var inst_33880 = (state_33883[(2)]);
var inst_33881 = cljs.core.async.close_BANG_(out);
var state_33883__$1 = (function (){var statearr_33911 = state_33883;
(statearr_33911[(9)] = inst_33880);

return statearr_33911;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_33883__$1,inst_33881);
} else {
if((state_val_33884 === (2))){
var inst_33851 = (state_33883[(8)]);
var inst_33853 = (inst_33851 < n);
var state_33883__$1 = state_33883;
if(cljs.core.truth_(inst_33853)){
var statearr_33912_36016 = state_33883__$1;
(statearr_33912_36016[(1)] = (4));

} else {
var statearr_33913_36017 = state_33883__$1;
(statearr_33913_36017[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (11))){
var inst_33851 = (state_33883[(8)]);
var inst_33870 = (state_33883[(2)]);
var inst_33871 = (inst_33851 + (1));
var inst_33851__$1 = inst_33871;
var state_33883__$1 = (function (){var statearr_33921 = state_33883;
(statearr_33921[(10)] = inst_33870);

(statearr_33921[(8)] = inst_33851__$1);

return statearr_33921;
})();
var statearr_33922_36018 = state_33883__$1;
(statearr_33922_36018[(2)] = null);

(statearr_33922_36018[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (9))){
var state_33883__$1 = state_33883;
var statearr_33923_36019 = state_33883__$1;
(statearr_33923_36019[(2)] = null);

(statearr_33923_36019[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (5))){
var state_33883__$1 = state_33883;
var statearr_33924_36020 = state_33883__$1;
(statearr_33924_36020[(2)] = null);

(statearr_33924_36020[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (10))){
var inst_33875 = (state_33883[(2)]);
var state_33883__$1 = state_33883;
var statearr_33926_36022 = state_33883__$1;
(statearr_33926_36022[(2)] = inst_33875);

(statearr_33926_36022[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33884 === (8))){
var inst_33858 = (state_33883[(7)]);
var state_33883__$1 = state_33883;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_33883__$1,(11),out,inst_33858);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_33931 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_33931[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_33931[(1)] = (1));

return statearr_33931;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_33883){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_33883);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e33932){var ex__29954__auto__ = e33932;
var statearr_33933_36031 = state_33883;
(statearr_33933_36031[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_33883[(4)]))){
var statearr_33934_36033 = state_33883;
(statearr_33934_36033[(1)] = cljs.core.first((state_33883[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__36034 = state_33883;
state_33883 = G__36034;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_33883){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_33883);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_33940 = f__31016__auto__();
(statearr_33940[(6)] = c__31012__auto___36003);

return statearr_33940;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return out;
}));

(cljs.core.async.take.cljs$lang$maxFixedArity = 3);


/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33954 = (function (f,ch,meta33947,_,fn1,meta33955){
this.f = f;
this.ch = ch;
this.meta33947 = meta33947;
this._ = _;
this.fn1 = fn1;
this.meta33955 = meta33955;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async33954.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_33956,meta33955__$1){
var self__ = this;
var _33956__$1 = this;
return (new cljs.core.async.t_cljs$core$async33954(self__.f,self__.ch,self__.meta33947,self__._,self__.fn1,meta33955__$1));
}));

(cljs.core.async.t_cljs$core$async33954.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_33956){
var self__ = this;
var _33956__$1 = this;
return self__.meta33955;
}));

(cljs.core.async.t_cljs$core$async33954.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33954.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.fn1);
}));

(cljs.core.async.t_cljs$core$async33954.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async33954.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
var f1 = cljs.core.async.impl.protocols.commit(self__.fn1);
return (function (p1__33944_SHARP_){
var G__33974 = (((p1__33944_SHARP_ == null))?null:(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(p1__33944_SHARP_) : self__.f.call(null,p1__33944_SHARP_)));
return (f1.cljs$core$IFn$_invoke$arity$1 ? f1.cljs$core$IFn$_invoke$arity$1(G__33974) : f1.call(null,G__33974));
});
}));

(cljs.core.async.t_cljs$core$async33954.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta33947","meta33947",1420497978,null),cljs.core.with_meta(new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core.async","t_cljs$core$async33946","cljs.core.async/t_cljs$core$async33946",-446316778,null)], null)),new cljs.core.Symbol(null,"fn1","fn1",895834444,null),new cljs.core.Symbol(null,"meta33955","meta33955",-1394151381,null)], null);
}));

(cljs.core.async.t_cljs$core$async33954.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async33954.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33954");

(cljs.core.async.t_cljs$core$async33954.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async33954");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33954.
 */
cljs.core.async.__GT_t_cljs$core$async33954 = (function cljs$core$async$__GT_t_cljs$core$async33954(f,ch,meta33947,_,fn1,meta33955){
return (new cljs.core.async.t_cljs$core$async33954(f,ch,meta33947,_,fn1,meta33955));
});



/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33946 = (function (f,ch,meta33947){
this.f = f;
this.ch = ch;
this.meta33947 = meta33947;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_33948,meta33947__$1){
var self__ = this;
var _33948__$1 = this;
return (new cljs.core.async.t_cljs$core$async33946(self__.f,self__.ch,meta33947__$1));
}));

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_33948){
var self__ = this;
var _33948__$1 = this;
return self__.meta33947;
}));

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
var ret = cljs.core.async.impl.protocols.take_BANG_(self__.ch,(new cljs.core.async.t_cljs$core$async33954(self__.f,self__.ch,self__.meta33947,___$1,fn1,cljs.core.PersistentArrayMap.EMPTY)));
if(cljs.core.truth_((function (){var and__5023__auto__ = ret;
if(cljs.core.truth_(and__5023__auto__)){
return (!((cljs.core.deref(ret) == null)));
} else {
return and__5023__auto__;
}
})())){
return cljs.core.async.impl.channels.box((function (){var G__33987 = cljs.core.deref(ret);
return (self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(G__33987) : self__.f.call(null,G__33987));
})());
} else {
return ret;
}
}));

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33946.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
}));

(cljs.core.async.t_cljs$core$async33946.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta33947","meta33947",1420497978,null)], null);
}));

(cljs.core.async.t_cljs$core$async33946.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async33946.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33946");

(cljs.core.async.t_cljs$core$async33946.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async33946");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33946.
 */
cljs.core.async.__GT_t_cljs$core$async33946 = (function cljs$core$async$__GT_t_cljs$core$async33946(f,ch,meta33947){
return (new cljs.core.async.t_cljs$core$async33946(f,ch,meta33947));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_LT_ = (function cljs$core$async$map_LT_(f,ch){
return (new cljs.core.async.t_cljs$core$async33946(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33998 = (function (f,ch,meta33999){
this.f = f;
this.ch = ch;
this.meta33999 = meta33999;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_34000,meta33999__$1){
var self__ = this;
var _34000__$1 = this;
return (new cljs.core.async.t_cljs$core$async33998(self__.f,self__.ch,meta33999__$1));
}));

(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_34000){
var self__ = this;
var _34000__$1 = this;
return self__.meta33999;
}));

(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async33998.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(val) : self__.f.call(null,val)),fn1);
}));

(cljs.core.async.t_cljs$core$async33998.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta33999","meta33999",-246167549,null)], null);
}));

(cljs.core.async.t_cljs$core$async33998.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async33998.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33998");

(cljs.core.async.t_cljs$core$async33998.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async33998");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33998.
 */
cljs.core.async.__GT_t_cljs$core$async33998 = (function cljs$core$async$__GT_t_cljs$core$async33998(f,ch,meta33999){
return (new cljs.core.async.t_cljs$core$async33998(f,ch,meta33999));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_GT_ = (function cljs$core$async$map_GT_(f,ch){
return (new cljs.core.async.t_cljs$core$async33998(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34024 = (function (p,ch,meta34025){
this.p = p;
this.ch = ch;
this.meta34025 = meta34025;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_34026,meta34025__$1){
var self__ = this;
var _34026__$1 = this;
return (new cljs.core.async.t_cljs$core$async34024(self__.p,self__.ch,meta34025__$1));
}));

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_34026){
var self__ = this;
var _34026__$1 = this;
return self__.meta34025;
}));

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async34024.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.p.cljs$core$IFn$_invoke$arity$1 ? self__.p.cljs$core$IFn$_invoke$arity$1(val) : self__.p.call(null,val)))){
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
} else {
return cljs.core.async.impl.channels.box(cljs.core.not(cljs.core.async.impl.protocols.closed_QMARK_(self__.ch)));
}
}));

(cljs.core.async.t_cljs$core$async34024.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"p","p",1791580836,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta34025","meta34025",-734719813,null)], null);
}));

(cljs.core.async.t_cljs$core$async34024.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async34024.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34024");

(cljs.core.async.t_cljs$core$async34024.cljs$lang$ctorPrWriter = (function (this__5310__auto__,writer__5311__auto__,opt__5312__auto__){
return cljs.core._write(writer__5311__auto__,"cljs.core.async/t_cljs$core$async34024");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34024.
 */
cljs.core.async.__GT_t_cljs$core$async34024 = (function cljs$core$async$__GT_t_cljs$core$async34024(p,ch,meta34025){
return (new cljs.core.async.t_cljs$core$async34024(p,ch,meta34025));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_GT_ = (function cljs$core$async$filter_GT_(p,ch){
return (new cljs.core.async.t_cljs$core$async34024(p,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_GT_ = (function cljs$core$async$remove_GT_(p,ch){
return cljs.core.async.filter_GT_(cljs.core.complement(p),ch);
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_LT_ = (function cljs$core$async$filter_LT_(var_args){
var G__34055 = arguments.length;
switch (G__34055) {
case 2:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3(p,ch,null);
}));

(cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__31012__auto___36120 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_34099){
var state_val_34100 = (state_34099[(1)]);
if((state_val_34100 === (7))){
var inst_34093 = (state_34099[(2)]);
var state_34099__$1 = state_34099;
var statearr_34112_36121 = state_34099__$1;
(statearr_34112_36121[(2)] = inst_34093);

(statearr_34112_36121[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (1))){
var state_34099__$1 = state_34099;
var statearr_34113_36122 = state_34099__$1;
(statearr_34113_36122[(2)] = null);

(statearr_34113_36122[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (4))){
var inst_34070 = (state_34099[(7)]);
var inst_34070__$1 = (state_34099[(2)]);
var inst_34071 = (inst_34070__$1 == null);
var state_34099__$1 = (function (){var statearr_34114 = state_34099;
(statearr_34114[(7)] = inst_34070__$1);

return statearr_34114;
})();
if(cljs.core.truth_(inst_34071)){
var statearr_34121_36123 = state_34099__$1;
(statearr_34121_36123[(1)] = (5));

} else {
var statearr_34122_36124 = state_34099__$1;
(statearr_34122_36124[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (6))){
var inst_34070 = (state_34099[(7)]);
var inst_34084 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_34070) : p.call(null,inst_34070));
var state_34099__$1 = state_34099;
if(cljs.core.truth_(inst_34084)){
var statearr_34125_36125 = state_34099__$1;
(statearr_34125_36125[(1)] = (8));

} else {
var statearr_34126_36126 = state_34099__$1;
(statearr_34126_36126[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (3))){
var inst_34095 = (state_34099[(2)]);
var state_34099__$1 = state_34099;
return cljs.core.async.impl.ioc_helpers.return_chan(state_34099__$1,inst_34095);
} else {
if((state_val_34100 === (2))){
var state_34099__$1 = state_34099;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_34099__$1,(4),ch);
} else {
if((state_val_34100 === (11))){
var inst_34087 = (state_34099[(2)]);
var state_34099__$1 = state_34099;
var statearr_34136_36127 = state_34099__$1;
(statearr_34136_36127[(2)] = inst_34087);

(statearr_34136_36127[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (9))){
var state_34099__$1 = state_34099;
var statearr_34138_36128 = state_34099__$1;
(statearr_34138_36128[(2)] = null);

(statearr_34138_36128[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (5))){
var inst_34075 = cljs.core.async.close_BANG_(out);
var state_34099__$1 = state_34099;
var statearr_34139_36129 = state_34099__$1;
(statearr_34139_36129[(2)] = inst_34075);

(statearr_34139_36129[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (10))){
var inst_34090 = (state_34099[(2)]);
var state_34099__$1 = (function (){var statearr_34142 = state_34099;
(statearr_34142[(8)] = inst_34090);

return statearr_34142;
})();
var statearr_34143_36130 = state_34099__$1;
(statearr_34143_36130[(2)] = null);

(statearr_34143_36130[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34100 === (8))){
var inst_34070 = (state_34099[(7)]);
var state_34099__$1 = state_34099;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34099__$1,(11),out,inst_34070);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_34145 = [null,null,null,null,null,null,null,null,null];
(statearr_34145[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_34145[(1)] = (1));

return statearr_34145;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_34099){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_34099);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e34146){var ex__29954__auto__ = e34146;
var statearr_34147_36131 = state_34099;
(statearr_34147_36131[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_34099[(4)]))){
var statearr_34148_36132 = state_34099;
(statearr_34148_36132[(1)] = cljs.core.first((state_34099[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__36136 = state_34099;
state_34099 = G__36136;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_34099){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_34099);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_34156 = f__31016__auto__();
(statearr_34156[(6)] = c__31012__auto___36120);

return statearr_34156;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return out;
}));

(cljs.core.async.filter_LT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_LT_ = (function cljs$core$async$remove_LT_(var_args){
var G__34167 = arguments.length;
switch (G__34167) {
case 2:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3(p,ch,null);
}));

(cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3(cljs.core.complement(p),ch,buf_or_n);
}));

(cljs.core.async.remove_LT_.cljs$lang$maxFixedArity = 3);

cljs.core.async.mapcat_STAR_ = (function cljs$core$async$mapcat_STAR_(f,in$,out){
var c__31012__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_34251){
var state_val_34252 = (state_34251[(1)]);
if((state_val_34252 === (7))){
var inst_34247 = (state_34251[(2)]);
var state_34251__$1 = state_34251;
var statearr_34254_36149 = state_34251__$1;
(statearr_34254_36149[(2)] = inst_34247);

(statearr_34254_36149[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (20))){
var inst_34211 = (state_34251[(7)]);
var inst_34228 = (state_34251[(2)]);
var inst_34229 = cljs.core.next(inst_34211);
var inst_34190 = inst_34229;
var inst_34191 = null;
var inst_34192 = (0);
var inst_34193 = (0);
var state_34251__$1 = (function (){var statearr_34258 = state_34251;
(statearr_34258[(8)] = inst_34228);

(statearr_34258[(9)] = inst_34190);

(statearr_34258[(10)] = inst_34191);

(statearr_34258[(11)] = inst_34192);

(statearr_34258[(12)] = inst_34193);

return statearr_34258;
})();
var statearr_34259_36155 = state_34251__$1;
(statearr_34259_36155[(2)] = null);

(statearr_34259_36155[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (1))){
var state_34251__$1 = state_34251;
var statearr_34260_36160 = state_34251__$1;
(statearr_34260_36160[(2)] = null);

(statearr_34260_36160[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (4))){
var inst_34175 = (state_34251[(13)]);
var inst_34175__$1 = (state_34251[(2)]);
var inst_34176 = (inst_34175__$1 == null);
var state_34251__$1 = (function (){var statearr_34261 = state_34251;
(statearr_34261[(13)] = inst_34175__$1);

return statearr_34261;
})();
if(cljs.core.truth_(inst_34176)){
var statearr_34262_36166 = state_34251__$1;
(statearr_34262_36166[(1)] = (5));

} else {
var statearr_34263_36167 = state_34251__$1;
(statearr_34263_36167[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (15))){
var state_34251__$1 = state_34251;
var statearr_34267_36168 = state_34251__$1;
(statearr_34267_36168[(2)] = null);

(statearr_34267_36168[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (21))){
var state_34251__$1 = state_34251;
var statearr_34268_36170 = state_34251__$1;
(statearr_34268_36170[(2)] = null);

(statearr_34268_36170[(1)] = (23));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (13))){
var inst_34193 = (state_34251[(12)]);
var inst_34190 = (state_34251[(9)]);
var inst_34191 = (state_34251[(10)]);
var inst_34192 = (state_34251[(11)]);
var inst_34207 = (state_34251[(2)]);
var inst_34208 = (inst_34193 + (1));
var tmp34264 = inst_34190;
var tmp34265 = inst_34192;
var tmp34266 = inst_34191;
var inst_34190__$1 = tmp34264;
var inst_34191__$1 = tmp34266;
var inst_34192__$1 = tmp34265;
var inst_34193__$1 = inst_34208;
var state_34251__$1 = (function (){var statearr_34276 = state_34251;
(statearr_34276[(14)] = inst_34207);

(statearr_34276[(9)] = inst_34190__$1);

(statearr_34276[(10)] = inst_34191__$1);

(statearr_34276[(11)] = inst_34192__$1);

(statearr_34276[(12)] = inst_34193__$1);

return statearr_34276;
})();
var statearr_34286_36172 = state_34251__$1;
(statearr_34286_36172[(2)] = null);

(statearr_34286_36172[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (22))){
var state_34251__$1 = state_34251;
var statearr_34287_36174 = state_34251__$1;
(statearr_34287_36174[(2)] = null);

(statearr_34287_36174[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (6))){
var inst_34175 = (state_34251[(13)]);
var inst_34188 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_34175) : f.call(null,inst_34175));
var inst_34189 = cljs.core.seq(inst_34188);
var inst_34190 = inst_34189;
var inst_34191 = null;
var inst_34192 = (0);
var inst_34193 = (0);
var state_34251__$1 = (function (){var statearr_34290 = state_34251;
(statearr_34290[(9)] = inst_34190);

(statearr_34290[(10)] = inst_34191);

(statearr_34290[(11)] = inst_34192);

(statearr_34290[(12)] = inst_34193);

return statearr_34290;
})();
var statearr_34293_36182 = state_34251__$1;
(statearr_34293_36182[(2)] = null);

(statearr_34293_36182[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (17))){
var inst_34211 = (state_34251[(7)]);
var inst_34215 = cljs.core.chunk_first(inst_34211);
var inst_34216 = cljs.core.chunk_rest(inst_34211);
var inst_34217 = cljs.core.count(inst_34215);
var inst_34190 = inst_34216;
var inst_34191 = inst_34215;
var inst_34192 = inst_34217;
var inst_34193 = (0);
var state_34251__$1 = (function (){var statearr_34296 = state_34251;
(statearr_34296[(9)] = inst_34190);

(statearr_34296[(10)] = inst_34191);

(statearr_34296[(11)] = inst_34192);

(statearr_34296[(12)] = inst_34193);

return statearr_34296;
})();
var statearr_34297_36186 = state_34251__$1;
(statearr_34297_36186[(2)] = null);

(statearr_34297_36186[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (3))){
var inst_34249 = (state_34251[(2)]);
var state_34251__$1 = state_34251;
return cljs.core.async.impl.ioc_helpers.return_chan(state_34251__$1,inst_34249);
} else {
if((state_val_34252 === (12))){
var inst_34237 = (state_34251[(2)]);
var state_34251__$1 = state_34251;
var statearr_34298_36187 = state_34251__$1;
(statearr_34298_36187[(2)] = inst_34237);

(statearr_34298_36187[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (2))){
var state_34251__$1 = state_34251;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_34251__$1,(4),in$);
} else {
if((state_val_34252 === (23))){
var inst_34245 = (state_34251[(2)]);
var state_34251__$1 = state_34251;
var statearr_34299_36188 = state_34251__$1;
(statearr_34299_36188[(2)] = inst_34245);

(statearr_34299_36188[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (19))){
var inst_34232 = (state_34251[(2)]);
var state_34251__$1 = state_34251;
var statearr_34300_36189 = state_34251__$1;
(statearr_34300_36189[(2)] = inst_34232);

(statearr_34300_36189[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (11))){
var inst_34190 = (state_34251[(9)]);
var inst_34211 = (state_34251[(7)]);
var inst_34211__$1 = cljs.core.seq(inst_34190);
var state_34251__$1 = (function (){var statearr_34304 = state_34251;
(statearr_34304[(7)] = inst_34211__$1);

return statearr_34304;
})();
if(inst_34211__$1){
var statearr_34305_36191 = state_34251__$1;
(statearr_34305_36191[(1)] = (14));

} else {
var statearr_34309_36192 = state_34251__$1;
(statearr_34309_36192[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (9))){
var inst_34239 = (state_34251[(2)]);
var inst_34240 = cljs.core.async.impl.protocols.closed_QMARK_(out);
var state_34251__$1 = (function (){var statearr_34310 = state_34251;
(statearr_34310[(15)] = inst_34239);

return statearr_34310;
})();
if(cljs.core.truth_(inst_34240)){
var statearr_34313_36193 = state_34251__$1;
(statearr_34313_36193[(1)] = (21));

} else {
var statearr_34316_36194 = state_34251__$1;
(statearr_34316_36194[(1)] = (22));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (5))){
var inst_34179 = cljs.core.async.close_BANG_(out);
var state_34251__$1 = state_34251;
var statearr_34319_36195 = state_34251__$1;
(statearr_34319_36195[(2)] = inst_34179);

(statearr_34319_36195[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (14))){
var inst_34211 = (state_34251[(7)]);
var inst_34213 = cljs.core.chunked_seq_QMARK_(inst_34211);
var state_34251__$1 = state_34251;
if(inst_34213){
var statearr_34320_36196 = state_34251__$1;
(statearr_34320_36196[(1)] = (17));

} else {
var statearr_34321_36197 = state_34251__$1;
(statearr_34321_36197[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (16))){
var inst_34235 = (state_34251[(2)]);
var state_34251__$1 = state_34251;
var statearr_34322_36198 = state_34251__$1;
(statearr_34322_36198[(2)] = inst_34235);

(statearr_34322_36198[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34252 === (10))){
var inst_34191 = (state_34251[(10)]);
var inst_34193 = (state_34251[(12)]);
var inst_34203 = cljs.core._nth(inst_34191,inst_34193);
var state_34251__$1 = state_34251;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34251__$1,(13),out,inst_34203);
} else {
if((state_val_34252 === (18))){
var inst_34211 = (state_34251[(7)]);
var inst_34226 = cljs.core.first(inst_34211);
var state_34251__$1 = state_34251;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34251__$1,(20),out,inst_34226);
} else {
if((state_val_34252 === (8))){
var inst_34193 = (state_34251[(12)]);
var inst_34192 = (state_34251[(11)]);
var inst_34195 = (inst_34193 < inst_34192);
var inst_34196 = inst_34195;
var state_34251__$1 = state_34251;
if(cljs.core.truth_(inst_34196)){
var statearr_34323_36200 = state_34251__$1;
(statearr_34323_36200[(1)] = (10));

} else {
var statearr_34324_36201 = state_34251__$1;
(statearr_34324_36201[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$mapcat_STAR__$_state_machine__29951__auto__ = null;
var cljs$core$async$mapcat_STAR__$_state_machine__29951__auto____0 = (function (){
var statearr_34325 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34325[(0)] = cljs$core$async$mapcat_STAR__$_state_machine__29951__auto__);

(statearr_34325[(1)] = (1));

return statearr_34325;
});
var cljs$core$async$mapcat_STAR__$_state_machine__29951__auto____1 = (function (state_34251){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_34251);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e34326){var ex__29954__auto__ = e34326;
var statearr_34327_36203 = state_34251;
(statearr_34327_36203[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_34251[(4)]))){
var statearr_34328_36204 = state_34251;
(statearr_34328_36204[(1)] = cljs.core.first((state_34251[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__36209 = state_34251;
state_34251 = G__36209;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$mapcat_STAR__$_state_machine__29951__auto__ = function(state_34251){
switch(arguments.length){
case 0:
return cljs$core$async$mapcat_STAR__$_state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$mapcat_STAR__$_state_machine__29951__auto____1.call(this,state_34251);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mapcat_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mapcat_STAR__$_state_machine__29951__auto____0;
cljs$core$async$mapcat_STAR__$_state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mapcat_STAR__$_state_machine__29951__auto____1;
return cljs$core$async$mapcat_STAR__$_state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_34332 = f__31016__auto__();
(statearr_34332[(6)] = c__31012__auto__);

return statearr_34332;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));

return c__31012__auto__;
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_LT_ = (function cljs$core$async$mapcat_LT_(var_args){
var G__34337 = arguments.length;
switch (G__34337) {
case 2:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2 = (function (f,in$){
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3(f,in$,null);
}));

(cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3 = (function (f,in$,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
cljs.core.async.mapcat_STAR_(f,in$,out);

return out;
}));

(cljs.core.async.mapcat_LT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_GT_ = (function cljs$core$async$mapcat_GT_(var_args){
var G__34348 = arguments.length;
switch (G__34348) {
case 2:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2 = (function (f,out){
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3(f,out,null);
}));

(cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3 = (function (f,out,buf_or_n){
var in$ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
cljs.core.async.mapcat_STAR_(f,in$,out);

return in$;
}));

(cljs.core.async.mapcat_GT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.unique = (function cljs$core$async$unique(var_args){
var G__34353 = arguments.length;
switch (G__34353) {
case 1:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1 = (function (ch){
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2(ch,null);
}));

(cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2 = (function (ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__31012__auto___36235 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_34392){
var state_val_34393 = (state_34392[(1)]);
if((state_val_34393 === (7))){
var inst_34387 = (state_34392[(2)]);
var state_34392__$1 = state_34392;
var statearr_34410_36237 = state_34392__$1;
(statearr_34410_36237[(2)] = inst_34387);

(statearr_34410_36237[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34393 === (1))){
var inst_34366 = null;
var state_34392__$1 = (function (){var statearr_34411 = state_34392;
(statearr_34411[(7)] = inst_34366);

return statearr_34411;
})();
var statearr_34412_36243 = state_34392__$1;
(statearr_34412_36243[(2)] = null);

(statearr_34412_36243[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34393 === (4))){
var inst_34372 = (state_34392[(8)]);
var inst_34372__$1 = (state_34392[(2)]);
var inst_34373 = (inst_34372__$1 == null);
var inst_34374 = cljs.core.not(inst_34373);
var state_34392__$1 = (function (){var statearr_34413 = state_34392;
(statearr_34413[(8)] = inst_34372__$1);

return statearr_34413;
})();
if(inst_34374){
var statearr_34414_36249 = state_34392__$1;
(statearr_34414_36249[(1)] = (5));

} else {
var statearr_34415_36252 = state_34392__$1;
(statearr_34415_36252[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34393 === (6))){
var state_34392__$1 = state_34392;
var statearr_34416_36258 = state_34392__$1;
(statearr_34416_36258[(2)] = null);

(statearr_34416_36258[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34393 === (3))){
var inst_34389 = (state_34392[(2)]);
var inst_34390 = cljs.core.async.close_BANG_(out);
var state_34392__$1 = (function (){var statearr_34417 = state_34392;
(statearr_34417[(9)] = inst_34389);

return statearr_34417;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_34392__$1,inst_34390);
} else {
if((state_val_34393 === (2))){
var state_34392__$1 = state_34392;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_34392__$1,(4),ch);
} else {
if((state_val_34393 === (11))){
var inst_34372 = (state_34392[(8)]);
var inst_34381 = (state_34392[(2)]);
var inst_34366 = inst_34372;
var state_34392__$1 = (function (){var statearr_34418 = state_34392;
(statearr_34418[(10)] = inst_34381);

(statearr_34418[(7)] = inst_34366);

return statearr_34418;
})();
var statearr_34419_36262 = state_34392__$1;
(statearr_34419_36262[(2)] = null);

(statearr_34419_36262[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34393 === (9))){
var inst_34372 = (state_34392[(8)]);
var state_34392__$1 = state_34392;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34392__$1,(11),out,inst_34372);
} else {
if((state_val_34393 === (5))){
var inst_34372 = (state_34392[(8)]);
var inst_34366 = (state_34392[(7)]);
var inst_34376 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_34372,inst_34366);
var state_34392__$1 = state_34392;
if(inst_34376){
var statearr_34421_36263 = state_34392__$1;
(statearr_34421_36263[(1)] = (8));

} else {
var statearr_34422_36268 = state_34392__$1;
(statearr_34422_36268[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34393 === (10))){
var inst_34384 = (state_34392[(2)]);
var state_34392__$1 = state_34392;
var statearr_34423_36274 = state_34392__$1;
(statearr_34423_36274[(2)] = inst_34384);

(statearr_34423_36274[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34393 === (8))){
var inst_34366 = (state_34392[(7)]);
var tmp34420 = inst_34366;
var inst_34366__$1 = tmp34420;
var state_34392__$1 = (function (){var statearr_34424 = state_34392;
(statearr_34424[(7)] = inst_34366__$1);

return statearr_34424;
})();
var statearr_34425_36286 = state_34392__$1;
(statearr_34425_36286[(2)] = null);

(statearr_34425_36286[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_34426 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_34426[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_34426[(1)] = (1));

return statearr_34426;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_34392){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_34392);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e34428){var ex__29954__auto__ = e34428;
var statearr_34429_36304 = state_34392;
(statearr_34429_36304[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_34392[(4)]))){
var statearr_34430_36309 = state_34392;
(statearr_34430_36309[(1)] = cljs.core.first((state_34392[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__36312 = state_34392;
state_34392 = G__36312;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_34392){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_34392);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_34431 = f__31016__auto__();
(statearr_34431[(6)] = c__31012__auto___36235);

return statearr_34431;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return out;
}));

(cljs.core.async.unique.cljs$lang$maxFixedArity = 2);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition = (function cljs$core$async$partition(var_args){
var G__34434 = arguments.length;
switch (G__34434) {
case 2:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3(n,ch,null);
}));

(cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__31012__auto___36319 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_34481){
var state_val_34482 = (state_34481[(1)]);
if((state_val_34482 === (7))){
var inst_34477 = (state_34481[(2)]);
var state_34481__$1 = state_34481;
var statearr_34490_36325 = state_34481__$1;
(statearr_34490_36325[(2)] = inst_34477);

(statearr_34490_36325[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (1))){
var inst_34438 = (new Array(n));
var inst_34439 = inst_34438;
var inst_34440 = (0);
var state_34481__$1 = (function (){var statearr_34491 = state_34481;
(statearr_34491[(7)] = inst_34439);

(statearr_34491[(8)] = inst_34440);

return statearr_34491;
})();
var statearr_34492_36331 = state_34481__$1;
(statearr_34492_36331[(2)] = null);

(statearr_34492_36331[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (4))){
var inst_34443 = (state_34481[(9)]);
var inst_34443__$1 = (state_34481[(2)]);
var inst_34444 = (inst_34443__$1 == null);
var inst_34445 = cljs.core.not(inst_34444);
var state_34481__$1 = (function (){var statearr_34493 = state_34481;
(statearr_34493[(9)] = inst_34443__$1);

return statearr_34493;
})();
if(inst_34445){
var statearr_34494_36333 = state_34481__$1;
(statearr_34494_36333[(1)] = (5));

} else {
var statearr_34495_36334 = state_34481__$1;
(statearr_34495_36334[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (15))){
var inst_34467 = (state_34481[(2)]);
var state_34481__$1 = state_34481;
var statearr_34496_36338 = state_34481__$1;
(statearr_34496_36338[(2)] = inst_34467);

(statearr_34496_36338[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (13))){
var state_34481__$1 = state_34481;
var statearr_34501_36343 = state_34481__$1;
(statearr_34501_36343[(2)] = null);

(statearr_34501_36343[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (6))){
var inst_34440 = (state_34481[(8)]);
var inst_34463 = (inst_34440 > (0));
var state_34481__$1 = state_34481;
if(cljs.core.truth_(inst_34463)){
var statearr_34502_36351 = state_34481__$1;
(statearr_34502_36351[(1)] = (12));

} else {
var statearr_34503_36352 = state_34481__$1;
(statearr_34503_36352[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (3))){
var inst_34479 = (state_34481[(2)]);
var state_34481__$1 = state_34481;
return cljs.core.async.impl.ioc_helpers.return_chan(state_34481__$1,inst_34479);
} else {
if((state_val_34482 === (12))){
var inst_34439 = (state_34481[(7)]);
var inst_34465 = cljs.core.vec(inst_34439);
var state_34481__$1 = state_34481;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34481__$1,(15),out,inst_34465);
} else {
if((state_val_34482 === (2))){
var state_34481__$1 = state_34481;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_34481__$1,(4),ch);
} else {
if((state_val_34482 === (11))){
var inst_34456 = (state_34481[(2)]);
var inst_34457 = (new Array(n));
var inst_34439 = inst_34457;
var inst_34440 = (0);
var state_34481__$1 = (function (){var statearr_34513 = state_34481;
(statearr_34513[(10)] = inst_34456);

(statearr_34513[(7)] = inst_34439);

(statearr_34513[(8)] = inst_34440);

return statearr_34513;
})();
var statearr_34514_36364 = state_34481__$1;
(statearr_34514_36364[(2)] = null);

(statearr_34514_36364[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (9))){
var inst_34439 = (state_34481[(7)]);
var inst_34454 = cljs.core.vec(inst_34439);
var state_34481__$1 = state_34481;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34481__$1,(11),out,inst_34454);
} else {
if((state_val_34482 === (5))){
var inst_34439 = (state_34481[(7)]);
var inst_34440 = (state_34481[(8)]);
var inst_34443 = (state_34481[(9)]);
var inst_34449 = (state_34481[(11)]);
var inst_34448 = (inst_34439[inst_34440] = inst_34443);
var inst_34449__$1 = (inst_34440 + (1));
var inst_34450 = (inst_34449__$1 < n);
var state_34481__$1 = (function (){var statearr_34515 = state_34481;
(statearr_34515[(12)] = inst_34448);

(statearr_34515[(11)] = inst_34449__$1);

return statearr_34515;
})();
if(cljs.core.truth_(inst_34450)){
var statearr_34516_36369 = state_34481__$1;
(statearr_34516_36369[(1)] = (8));

} else {
var statearr_34517_36370 = state_34481__$1;
(statearr_34517_36370[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (14))){
var inst_34470 = (state_34481[(2)]);
var inst_34472 = cljs.core.async.close_BANG_(out);
var state_34481__$1 = (function (){var statearr_34519 = state_34481;
(statearr_34519[(13)] = inst_34470);

return statearr_34519;
})();
var statearr_34520_36373 = state_34481__$1;
(statearr_34520_36373[(2)] = inst_34472);

(statearr_34520_36373[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (10))){
var inst_34460 = (state_34481[(2)]);
var state_34481__$1 = state_34481;
var statearr_34521_36374 = state_34481__$1;
(statearr_34521_36374[(2)] = inst_34460);

(statearr_34521_36374[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34482 === (8))){
var inst_34439 = (state_34481[(7)]);
var inst_34449 = (state_34481[(11)]);
var tmp34518 = inst_34439;
var inst_34439__$1 = tmp34518;
var inst_34440 = inst_34449;
var state_34481__$1 = (function (){var statearr_34522 = state_34481;
(statearr_34522[(7)] = inst_34439__$1);

(statearr_34522[(8)] = inst_34440);

return statearr_34522;
})();
var statearr_34523_36375 = state_34481__$1;
(statearr_34523_36375[(2)] = null);

(statearr_34523_36375[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_34524 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34524[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_34524[(1)] = (1));

return statearr_34524;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_34481){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_34481);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e34526){var ex__29954__auto__ = e34526;
var statearr_34527_36376 = state_34481;
(statearr_34527_36376[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_34481[(4)]))){
var statearr_34528_36377 = state_34481;
(statearr_34528_36377[(1)] = cljs.core.first((state_34481[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__36378 = state_34481;
state_34481 = G__36378;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_34481){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_34481);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_34531 = f__31016__auto__();
(statearr_34531[(6)] = c__31012__auto___36319);

return statearr_34531;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return out;
}));

(cljs.core.async.partition.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition_by = (function cljs$core$async$partition_by(var_args){
var G__34536 = arguments.length;
switch (G__34536) {
case 2:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2 = (function (f,ch){
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3(f,ch,null);
}));

(cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3 = (function (f,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__31012__auto___36388 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__31016__auto__ = (function (){var switch__29950__auto__ = (function (state_34599){
var state_val_34600 = (state_34599[(1)]);
if((state_val_34600 === (7))){
var inst_34592 = (state_34599[(2)]);
var state_34599__$1 = state_34599;
var statearr_34601_36390 = state_34599__$1;
(statearr_34601_36390[(2)] = inst_34592);

(statearr_34601_36390[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (1))){
var inst_34552 = [];
var inst_34553 = inst_34552;
var inst_34554 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);
var state_34599__$1 = (function (){var statearr_34605 = state_34599;
(statearr_34605[(7)] = inst_34553);

(statearr_34605[(8)] = inst_34554);

return statearr_34605;
})();
var statearr_34606_36399 = state_34599__$1;
(statearr_34606_36399[(2)] = null);

(statearr_34606_36399[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (4))){
var inst_34557 = (state_34599[(9)]);
var inst_34557__$1 = (state_34599[(2)]);
var inst_34558 = (inst_34557__$1 == null);
var inst_34559 = cljs.core.not(inst_34558);
var state_34599__$1 = (function (){var statearr_34608 = state_34599;
(statearr_34608[(9)] = inst_34557__$1);

return statearr_34608;
})();
if(inst_34559){
var statearr_34612_36405 = state_34599__$1;
(statearr_34612_36405[(1)] = (5));

} else {
var statearr_34613_36410 = state_34599__$1;
(statearr_34613_36410[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (15))){
var inst_34553 = (state_34599[(7)]);
var inst_34584 = cljs.core.vec(inst_34553);
var state_34599__$1 = state_34599;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34599__$1,(18),out,inst_34584);
} else {
if((state_val_34600 === (13))){
var inst_34579 = (state_34599[(2)]);
var state_34599__$1 = state_34599;
var statearr_34616_36412 = state_34599__$1;
(statearr_34616_36412[(2)] = inst_34579);

(statearr_34616_36412[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (6))){
var inst_34553 = (state_34599[(7)]);
var inst_34581 = inst_34553.length;
var inst_34582 = (inst_34581 > (0));
var state_34599__$1 = state_34599;
if(cljs.core.truth_(inst_34582)){
var statearr_34618_36414 = state_34599__$1;
(statearr_34618_36414[(1)] = (15));

} else {
var statearr_34622_36415 = state_34599__$1;
(statearr_34622_36415[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (17))){
var inst_34589 = (state_34599[(2)]);
var inst_34590 = cljs.core.async.close_BANG_(out);
var state_34599__$1 = (function (){var statearr_34623 = state_34599;
(statearr_34623[(10)] = inst_34589);

return statearr_34623;
})();
var statearr_34624_36416 = state_34599__$1;
(statearr_34624_36416[(2)] = inst_34590);

(statearr_34624_36416[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (3))){
var inst_34594 = (state_34599[(2)]);
var state_34599__$1 = state_34599;
return cljs.core.async.impl.ioc_helpers.return_chan(state_34599__$1,inst_34594);
} else {
if((state_val_34600 === (12))){
var inst_34553 = (state_34599[(7)]);
var inst_34572 = cljs.core.vec(inst_34553);
var state_34599__$1 = state_34599;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_34599__$1,(14),out,inst_34572);
} else {
if((state_val_34600 === (2))){
var state_34599__$1 = state_34599;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_34599__$1,(4),ch);
} else {
if((state_val_34600 === (11))){
var inst_34553 = (state_34599[(7)]);
var inst_34557 = (state_34599[(9)]);
var inst_34561 = (state_34599[(11)]);
var inst_34569 = inst_34553.push(inst_34557);
var tmp34625 = inst_34553;
var inst_34553__$1 = tmp34625;
var inst_34554 = inst_34561;
var state_34599__$1 = (function (){var statearr_34628 = state_34599;
(statearr_34628[(12)] = inst_34569);

(statearr_34628[(7)] = inst_34553__$1);

(statearr_34628[(8)] = inst_34554);

return statearr_34628;
})();
var statearr_34629_36417 = state_34599__$1;
(statearr_34629_36417[(2)] = null);

(statearr_34629_36417[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (9))){
var inst_34554 = (state_34599[(8)]);
var inst_34565 = cljs.core.keyword_identical_QMARK_(inst_34554,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));
var state_34599__$1 = state_34599;
var statearr_34634_36422 = state_34599__$1;
(statearr_34634_36422[(2)] = inst_34565);

(statearr_34634_36422[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (5))){
var inst_34557 = (state_34599[(9)]);
var inst_34561 = (state_34599[(11)]);
var inst_34554 = (state_34599[(8)]);
var inst_34562 = (state_34599[(13)]);
var inst_34561__$1 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_34557) : f.call(null,inst_34557));
var inst_34562__$1 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_34561__$1,inst_34554);
var state_34599__$1 = (function (){var statearr_34637 = state_34599;
(statearr_34637[(11)] = inst_34561__$1);

(statearr_34637[(13)] = inst_34562__$1);

return statearr_34637;
})();
if(inst_34562__$1){
var statearr_34638_36423 = state_34599__$1;
(statearr_34638_36423[(1)] = (8));

} else {
var statearr_34639_36424 = state_34599__$1;
(statearr_34639_36424[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (14))){
var inst_34557 = (state_34599[(9)]);
var inst_34561 = (state_34599[(11)]);
var inst_34574 = (state_34599[(2)]);
var inst_34575 = [];
var inst_34576 = inst_34575.push(inst_34557);
var inst_34553 = inst_34575;
var inst_34554 = inst_34561;
var state_34599__$1 = (function (){var statearr_34642 = state_34599;
(statearr_34642[(14)] = inst_34574);

(statearr_34642[(15)] = inst_34576);

(statearr_34642[(7)] = inst_34553);

(statearr_34642[(8)] = inst_34554);

return statearr_34642;
})();
var statearr_34645_36428 = state_34599__$1;
(statearr_34645_36428[(2)] = null);

(statearr_34645_36428[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (16))){
var state_34599__$1 = state_34599;
var statearr_34646_36430 = state_34599__$1;
(statearr_34646_36430[(2)] = null);

(statearr_34646_36430[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (10))){
var inst_34567 = (state_34599[(2)]);
var state_34599__$1 = state_34599;
if(cljs.core.truth_(inst_34567)){
var statearr_34647_36431 = state_34599__$1;
(statearr_34647_36431[(1)] = (11));

} else {
var statearr_34648_36432 = state_34599__$1;
(statearr_34648_36432[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (18))){
var inst_34586 = (state_34599[(2)]);
var state_34599__$1 = state_34599;
var statearr_34649_36433 = state_34599__$1;
(statearr_34649_36433[(2)] = inst_34586);

(statearr_34649_36433[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34600 === (8))){
var inst_34562 = (state_34599[(13)]);
var state_34599__$1 = state_34599;
var statearr_34650_36435 = state_34599__$1;
(statearr_34650_36435[(2)] = inst_34562);

(statearr_34650_36435[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__29951__auto__ = null;
var cljs$core$async$state_machine__29951__auto____0 = (function (){
var statearr_34651 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34651[(0)] = cljs$core$async$state_machine__29951__auto__);

(statearr_34651[(1)] = (1));

return statearr_34651;
});
var cljs$core$async$state_machine__29951__auto____1 = (function (state_34599){
while(true){
var ret_value__29952__auto__ = (function (){try{while(true){
var result__29953__auto__ = switch__29950__auto__(state_34599);
if(cljs.core.keyword_identical_QMARK_(result__29953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__29953__auto__;
}
break;
}
}catch (e34652){var ex__29954__auto__ = e34652;
var statearr_34653_36442 = state_34599;
(statearr_34653_36442[(2)] = ex__29954__auto__);


if(cljs.core.seq((state_34599[(4)]))){
var statearr_34654_36443 = state_34599;
(statearr_34654_36443[(1)] = cljs.core.first((state_34599[(4)])));

} else {
throw ex__29954__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__29952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__36444 = state_34599;
state_34599 = G__36444;
continue;
} else {
return ret_value__29952__auto__;
}
break;
}
});
cljs$core$async$state_machine__29951__auto__ = function(state_34599){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__29951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__29951__auto____1.call(this,state_34599);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__29951__auto____0;
cljs$core$async$state_machine__29951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__29951__auto____1;
return cljs$core$async$state_machine__29951__auto__;
})()
})();
var state__31017__auto__ = (function (){var statearr_34658 = f__31016__auto__();
(statearr_34658[(6)] = c__31012__auto___36388);

return statearr_34658;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__31017__auto__);
}));


return out;
}));

(cljs.core.async.partition_by.cljs$lang$maxFixedArity = 3);


//# sourceMappingURL=cljs.core.async.js.map

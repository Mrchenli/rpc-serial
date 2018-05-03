package io.mrchenli.netty.reactor.eventdrivendesign;

/**
 * 1.Usually more efficient than alternatives
 *      fewer resources
 *          don't usually need a thread per client
 *      less overhead
 *          less context switching,often less locking
 *      but dispatching can be slower
 *          must manually bind actions to events
 *  2.Usually harder to program
 *      must break up into simple no-blocking actions
 *      cannot eliminate all blocking : gc page fault,etc.
 *    Must keep track of logical state of service
 *
 *   click -->awt event queue(event...) -->button -->AWT thread(ActionListener:doSomething)
 */
public class PackageInfo {
}

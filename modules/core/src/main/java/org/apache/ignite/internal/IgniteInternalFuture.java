/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteDescribe;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteInClosure;
import org.jetbrains.annotations.Async;

/**
 * Extension for standard {@link Future} interface. It adds simplified exception handling,
 * functional programming support and ability to listen for future completion via functional
 * callback.
 * @param <R> Type of the result for the future.
 */
public interface IgniteInternalFuture<R> extends IgniteDescribe {
    /**
     * Synchronously waits for completion of the computation and
     * returns computation result.
     *
     * @return Computation result.
     * @throws IgniteInterruptedCheckedException Subclass of {@link IgniteCheckedException} thrown if the wait was interrupted.
     * @throws IgniteFutureCancelledCheckedException Subclass of {@link IgniteCheckedException} throws if computation was cancelled.
     * @throws IgniteCheckedException If computation failed.
     */
    public R get() throws IgniteCheckedException;

    /**
     * Synchronously waits for completion of the computation for
     * up to the timeout specified and returns computation result.
     * This method is equivalent to calling {@link #get(long, TimeUnit) get(long, TimeUnit.MILLISECONDS)}.
     *
     * @param timeout The maximum time to wait in milliseconds.
     * @return Computation result.
     * @throws IgniteInterruptedCheckedException Subclass of {@link IgniteCheckedException} thrown if the wait was interrupted.
     * @throws IgniteFutureTimeoutCheckedException Subclass of {@link IgniteCheckedException} thrown if the wait was timed out.
     * @throws IgniteFutureCancelledCheckedException Subclass of {@link IgniteCheckedException} throws if computation was cancelled.
     * @throws IgniteCheckedException If computation failed.
     */
    public R get(long timeout) throws IgniteCheckedException;

    /**
     * Synchronously waits for completion of the computation for
     * up to the timeout specified and returns computation result.
     *
     * @param timeout The maximum time to wait.
     * @param unit The time unit of the {@code timeout} argument.
     * @return Computation result.
     * @throws IgniteInterruptedCheckedException Subclass of {@link IgniteCheckedException} thrown if the wait was interrupted.
     * @throws IgniteFutureTimeoutCheckedException Subclass of {@link IgniteCheckedException} thrown if the wait was timed out.
     * @throws IgniteFutureCancelledCheckedException Subclass of {@link IgniteCheckedException} throws if computation was cancelled.
     * @throws IgniteCheckedException If computation failed.
     */
    public R get(long timeout, TimeUnit unit) throws IgniteCheckedException;

    /**
     * Synchronously waits for completion of the computation and returns computation result ignoring interrupts.
     *
     * @return Computation result.
     * @throws IgniteFutureCancelledCheckedException Subclass of {@link IgniteCheckedException} throws if computation
     *     was cancelled.
     * @throws IgniteCheckedException If computation failed.
     */
    public R getUninterruptibly() throws IgniteCheckedException;

    /**
     * Cancels this future.
     *
     * @return {@code True} if future was canceled (i.e. was not finished prior to this call).
     * @throws IgniteCheckedException If cancellation failed.
     */
    public boolean cancel() throws IgniteCheckedException;

    /**
     * Checks if computation is done.
     *
     * @return {@code True} if computation is done, {@code false} otherwise.
     */
    public boolean isDone();

    /**
     * Returns {@code true} if this computation was cancelled before it completed normally.
     *
     * @return {@code True} if this computation was cancelled before it completed normally.
     */
    public boolean isCancelled();

    /**
     * Registers listener closure to be asynchronously notified whenever future completes.
     *
     * @param lsnr Listener closure to register. If not provided - this method is no-op.
     */
    @Async.Schedule
    public void listen(IgniteInClosure<? super IgniteInternalFuture<R>> lsnr);

    /**
     * Make a chained future to convert result of this future (when complete) into a new format.
     * It is guaranteed that done callback will be called only ONCE.
     *
     * @param doneCb Done callback that is applied to this future when it finishes to produce chained future result.
     * @return Chained future that finishes after this future completes and done callback is called.
     */
    @Async.Schedule
    public <T> IgniteInternalFuture<T> chain(IgniteClosure<? super IgniteInternalFuture<R>, T> doneCb);

    /**
     * Make a chained future to convert result of this future (when complete) into a new format.
     * It is guaranteed that done callback will be called only ONCE.
     *
     * @param doneCb Done callback that is applied to this future when it finishes to produce chained future result.
     * @param exec Executor to run callback.
     * @return Chained future that finishes after this future completes and done callback is called.
     */
    @Async.Schedule
    public <T> IgniteInternalFuture<T> chain(IgniteClosure<? super IgniteInternalFuture<R>, T> doneCb, Executor exec);

    /**
     * @return Error value if future has already been completed with error.
     */
    public Throwable error();

    /**
     * @return Result value if future has already been completed normally.
     */
    public R result();
}

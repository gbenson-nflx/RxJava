package rx.lang.scala.concurrency

import scala.concurrent.duration.Duration
import rx.lang.scala.Scheduler
//import org.scalatest.junit.JUnitSuite

/**
 * Scheduler with artificial time, useful for testing.
 * 
 * For example, you could test the `Observable.interval` operation using a `TestScheduler` as follows:
 * 
 * {{{
 * @Test def testInterval() {
 *   import org.mockito.Matchers._
 *   import org.mockito.Mockito._
 *   
 *   val scheduler = TestScheduler()
 *   val observer = mock(classOf[rx.Observer[Long]])
 *
 *   val o = Observable.interval(1 second, scheduler)
 *   val sub = o.subscribe(observer)
 *
 *   verify(observer, never).onNext(0L)
 *   verify(observer, never).onCompleted()
 *   verify(observer, never).onError(any(classOf[Throwable]))
 *
 *   scheduler.advanceTimeTo(2 seconds)
 *
 *   val inOrdr = inOrder(observer);
 *   inOrdr.verify(observer, times(1)).onNext(0L)
 *   inOrdr.verify(observer, times(1)).onNext(1L)
 *   inOrdr.verify(observer, never).onNext(2L)
 *   verify(observer, never).onCompleted()
 *   verify(observer, never).onError(any(classOf[Throwable]))
 *
 *   sub.unsubscribe();
 *   scheduler.advanceTimeTo(4 seconds)
 *   verify(observer, never).onNext(2L)
 *   verify(observer, times(1)).onCompleted()
 *   verify(observer, never).onError(any(classOf[Throwable]))
 * }
 * }}}
 */
class TestScheduler extends Scheduler {
  val asJavaScheduler = new rx.concurrency.TestScheduler

  def advanceTimeBy(time: Duration) {
    asJavaScheduler.advanceTimeBy(time.length, time.unit)
  }

  def advanceTimeTo(time: Duration) {
    asJavaScheduler.advanceTimeTo(time.length, time.unit)
  }

  def triggerActions() {
    asJavaScheduler.triggerActions()
  }
}

/**
 * Provides constructors for `TestScheduler`.
 */
object TestScheduler {
  def apply(): TestScheduler = {
    new TestScheduler
  }
}

//private class UnitTest extends JUnitSuite {
//  import org.junit.Test
//  import scala.concurrent.duration._
//  import scala.language.postfixOps
//  import rx.lang.scala.{Observable, Observer}
//
//  @Test def testInterval() {
//    import org.mockito.Matchers._
//    import org.mockito.Mockito._
//
//    val scheduler = TestScheduler()
//    val observer = mock(classOf[rx.Observer[Long]])
//
//    val o = Observable.interval(1 second, scheduler)
//    val sub = o.subscribe(observer)
//
//    verify(observer, never).onNext(0L)
//    verify(observer, never).onCompleted()
//    verify(observer, never).onError(any(classOf[Throwable]))
//
//    scheduler.advanceTimeTo(2 seconds)
//
//    val inOrdr = inOrder(observer);
//    inOrdr.verify(observer, times(1)).onNext(0L)
//    inOrdr.verify(observer, times(1)).onNext(1L)
//    inOrdr.verify(observer, never).onNext(2L)
//    verify(observer, never).onCompleted()
//    verify(observer, never).onError(any(classOf[Throwable]))
//
//    sub.unsubscribe();
//    scheduler.advanceTimeTo(4 seconds)
//    verify(observer, never).onNext(2L)
//    verify(observer, times(1)).onCompleted()
//    verify(observer, never).onError(any(classOf[Throwable]))
//  }
//}


package ruk.tlb

import org.scalatest.Reporter
import org.scalatest.events.{SuiteCompleted, TestFailed, SuiteAborted, Event}
import tlb.factory.TlbFactory
import tlb.utils.SystemEnvironment
import collection.mutable

class ScalaTestTLBReporter extends Reporter {
  val server = TlbFactory.getTalkToService(new SystemEnvironment())
  val failedSuites:mutable.ListBuffer[String] = mutable.ListBuffer()

  def apply(event: Event) {
    event match {
      case e: SuiteAborted => record(e.suiteClassName, e.duration)(suiteAborted)
      case e: TestFailed => record(e.suiteClassName, e.duration)(testFailed)
      case e: SuiteCompleted => record(e.suiteClassName, e.duration)(suiteCompleted)
      case _ =>
    }
  }

  private def record(suiteClassName:Option[String], duration:Option[Long])(func: (String, Long)=> Unit) {
    (suiteClassName, duration) match {
      case (Some(s), Some(d)) if s != "org.scalatest.ScalaTestTLBSuite" => func(s, d)
      case _ =>
    }
  }

  private def suiteAborted(suiteClassName:String, duration:Long) {
    reportToServer(suiteClassName, duration, failed = true)
  }

  private def suiteCompleted(suiteClassName:String, duration:Long) {
    if (failedSuites.contains(suiteClassName)){
      reportToServer(suiteClassName, duration, failed = true)
    } else {
      reportToServer(suiteClassName, duration, failed = false)
    }
  }

  private def reportToServer(suiteClassName:String, duration:Long, failed :Boolean) {
    server.testClassFailure(suiteClassName, failed)
    server.testClassTime(suiteClassName, duration)
  }

  private def testFailed(suiteClassName:String, duration:Long) {
    failedSuites.append(suiteClassName)
  }
}

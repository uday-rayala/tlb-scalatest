package ruk.tlb

import org.scalatest.Reporter
import org.scalatest.events.{SuiteCompleted, TestFailed, SuiteAborted, Event}
import tlb.factory.TlbFactory
import tlb.utils.SystemEnvironment
import collection.mutable

class ScalaTestTLBReporter extends Reporter {
  val server = TlbFactory.getTalkToService(new SystemEnvironment())
  val failedSuites:mutable.ListBuffer[String] = mutable.ListBuffer()

  private def suiteAborted(suiteClassName:String, duration:Long) {
    reportToServer(suiteClassName, duration, true)
  }

  private def suiteCompleted(suiteClassName:String, duration:Long) {
    if (failedSuites.contains(suiteClassName)){
      reportToServer(suiteClassName, duration, true)
    } else {
      reportToServer(suiteClassName, duration, false)
    }
  }

  private def testFailed(suiteClassName:String, duration:Long) {
    failedSuites.append(suiteClassName)
  }

  private def record(suiteClassName:Option[String], duration:Option[Long])(func: (String, Long)=> Unit) {
    (suiteClassName, duration) match {
      case (Some(s), Some(d)) if s != "org.scalatest.ScalaTestTLBSuite" => func(s, d)
      case _ =>
    }
  }

  def apply(event: Event) {
    try {
      event match {
        case e: SuiteAborted => record(e.suiteClassName, e.duration)(suiteAborted)
        case e: TestFailed => record(e.suiteClassName, e.duration)(testFailed)
        case e: SuiteCompleted => record(e.suiteClassName, e.duration)(suiteCompleted)
        case _ =>
      }
    }
    catch {
      case e: Throwable => reportInFile(e.toString + "\n" + e.getStackTraceString)
    }
  }
}

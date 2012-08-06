package ruk.tlb

import org.scalatest.Reporter
import org.scalatest.events.{SuiteCompleted, TestFailed, SuiteAborted, Event}
import tlb.factory.TlbFactory
import tlb.utils.SystemEnvironment
import collection.mutable
import java.io.{FileOutputStream, File}

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

  private def reportToServer(suiteClassName:String, duration:Long, failed :Boolean) {
    reportInFile("Suite class name: %s, duration: %s, failed: %s".format(suiteClassName, duration, failed))
    server.testClassFailure(suiteClassName, failed)
    server.testClassTime(suiteClassName, duration)
  }

  private def blah(suiteClassName:Option[String], duration:Option[Long])(func: (String, Long)=> Unit) {
    (suiteClassName, duration) match {
      case (Some(s), Some(d)) if s != "org.scalatest.ScalaTestTLBSuite" => func(s, d)
      case _ =>
    }
  }

  private def reportInFile(str:String) {
    val file = new File("/tmp/scalatest-reporter.log")
    val fileOutputStream: FileOutputStream = new FileOutputStream(file, true)
    fileOutputStream.write(str.getBytes)
    fileOutputStream.write("\n".getBytes)
    fileOutputStream.close()
  }

  def apply(event: Event) {
    try {
      event match {
        case e: SuiteAborted => blah(e.suiteClassName, e.duration)(suiteAborted)
        case e: TestFailed => blah(e.suiteClassName, e.duration)(testFailed)
        case e: SuiteCompleted => blah(e.suiteClassName, e.duration)(suiteCompleted)
        case _ =>
      }
    }
    catch {
      case e: Throwable => reportInFile(e.toString + "\n" + e.getStackTraceString)
    }
  }
}

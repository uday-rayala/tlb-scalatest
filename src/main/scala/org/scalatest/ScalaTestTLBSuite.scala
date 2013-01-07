package org.scalatest

import tools.{DiscoverySuite, SuiteDiscoveryHelper}
import java.net.URLClassLoader
import java.io.File
import tlb.factory.TlbBalancerFactory
import tlb.splitter.TestSplitter
import tlb.utils.SystemEnvironment
import tlb.{TlbSuiteFile, TlbSuiteFileImpl}
import scala.collection.JavaConversions._
import tlb.orderer.TestOrderer
import java.util.Comparator

class ScalaTestTLBSuite extends Suite {
  val environment = new SystemEnvironment()
  val jarFile = System.getProperty("tlb.jar.file")

  val splitter = TlbBalancerFactory.getCriteria(environment.`val`(TestSplitter.TLB_SPLITTER), environment)
  val orderer:Comparator[TlbSuiteFile] = TlbBalancerFactory.getOrderer(environment.`val`(TestOrderer.TLB_ORDERER), environment)

  if (jarFile == null)
    fail("Please specify the tlb jar file")

  override def nestedSuites = {
    val loader = new URLClassLoader(Array(new File(jarFile).toURI.toURL), classOf[Suite].getClassLoader)
    val accessibleSuites: Set[String] = SuiteDiscoveryHelper.discoverSuiteNames(List(jarFile), loader, None)

    val files = splitter.filterSuites(accessibleSuites.map(x => new TlbSuiteFileImpl(x)).toSeq)
    val orderedFiles = files.sorted(Ordering.comparatorToOrdering(orderer))
    toSuites(orderedFiles.map(_.getName).toSeq, loader).toList
  }

  def toSuites(suiteNames:Seq[String], runpathClassLoader:ClassLoader) = {
    for (suiteClassName <- suiteNames)
    yield {
      try {
        val clazz = runpathClassLoader.loadClass(suiteClassName)
        clazz.newInstance.asInstanceOf[Suite]
      }
      catch {
        case e: Exception => {
          val msg = Resources("cannotLoadDiscoveredSuite", suiteClassName)
          throw new RuntimeException(msg, e)
        }
      }
    }
  }
}

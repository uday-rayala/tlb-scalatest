**Test load balancer support for Scalatest framework**

This repository contains two main classes:

1. org.scalatest.ScalaTestTLBSuite.scala: This is the Scalatest suite which will talk to the tlb server and decides which tests to run.

2. ruk.tlb.ScalaTestTLBReporter.scala: This is the reporter class which will report to the tlb server whether tests passed/failed and the time taken to run them.

I have tested it working with only the scalatest runner. Following is the command which will run all the tests in the functional-tests.jar package.

```java $TESTS_JVM_OPS -Dtlb.jar.file=./lib/functional-tests.jar -cp "./lib/*" org.scalatest.tools.Runner -u reports 
-r ruk.tlb.ScalaTestTLBReporter -oW -s org.scalatest.ScalaTestTLBSuite```

Few things to note in the above command are:

1. tlb.jar.file: This is a java system property which specifies the jar file which has all the tests to run.
2. -r ruk.tlb.ScalaTestTLBReporter: This specifies the reporter class.
3. -s org.scalatest.ScalaTestTLBSuite: This specifies the suite class which returns the tests to run after talking to the tlb server.

Although I used it to run using the scalatest runner class, it should be easy to support running by SBT or any other build framework with some coding effort.
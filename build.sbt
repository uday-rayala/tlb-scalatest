name := "tlb-scalatest"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" withSources()

parallelExecution in Test := false

retrieveManaged := true

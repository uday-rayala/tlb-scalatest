name := "tlb-scalatest"

version:= "1.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" withSources()

parallelExecution in Test := false

retrieveManaged := true

crossPaths := false

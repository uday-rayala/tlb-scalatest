name := "tlb-scalatest"

version:= "1.1"

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "1.8" withSources(),
    "log4j" % "log4j" % "1.2.14"
)

parallelExecution in Test := false

retrieveManaged := true

crossPaths := false

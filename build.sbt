name := "tlb-scalatest"

version:= "1.0"

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "1.6.1" withSources(),
    "log4j" % "log4j" % "1.2.14"
)

parallelExecution in Test := false

retrieveManaged := true

crossPaths := false

name := "dojoScala"
version := "0.1"
scalaVersion := "2.12.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
libraryDependencies ++= {
  val akkaV       = "2.4.3"
  val scalaTestV  = "2.2.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.16",
    "com.typesafe.akka" %% "akka-testkit" % "2.5.16",
    "com.typesafe.akka" %% "akka-stream" % "2.5.16",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.16",
    "com.typesafe.akka" %% "akka-http" % "10.1.4",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.1.4" % Test,
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5"
  )
}
import scala.io.Source

scalaVersion := "2.13.3"
organization := "com.akoot.plugins"
version := "1.22.0"
name := "UltraVanilla"
resolvers ++= List(
  "jitpack.io" at "https://jitpack.io",
  "papermc" at "https://papermc.io/repo/repository/maven-public/",
  ("vault-repo" at "http://nexus.hc.to/content/repositories/pub_releases").withAllowInsecureProtocol(true),
  "dmulloy2-repo" at "https://repo.dmulloy2.net/nexus/repository/public/"
)
libraryDependencies ++= List(
  "com.destroystokyo.paper" % "paper-api" % "1.16.1-R0.1-SNAPSHOT" % Provided,
  "com.github.MilkBowl" % "VaultAPI" % "1.7" % Provided,
  "net.luckperms" % "api" % "5.2" % Provided
)

scalacOptions += "-target:11"
javacOptions ++= Seq("-source", "11", "-target", "11")

Compile / resourceGenerators += Def.task {
  val file = (Compile / resourceManaged).value / "plugin.yml"
  val pluginConfig = Source.fromFile("plugin.yml").getLines.mkString("\n")
  val contents = pluginConfig.replace("@VERSION@", version.value)
  IO.write(file, contents)
  Seq(file)
}.taskValue

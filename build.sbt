scalaVersion := "2.13.3"
organization := "world.ultravanilla"
version := "1.27.0"
name := "UltraVanilla"
resolvers ++= List(
  "jitpack.io" at "https://jitpack.io",
  "papermc" at "https://papermc.io/repo/repository/maven-public/",
  ("vault-repo" at "http://nexus.hc.to/content/repositories/pub_releases").withAllowInsecureProtocol(true),
  "dmulloy2-repo" at "https://repo.dmulloy2.net/nexus/repository/public/",
  "jcenter" at "https://jcenter.bintray.com"
)
libraryDependencies ++= List(
  "com.destroystokyo.paper" % "paper-api" % "1.16.1-R0.1-SNAPSHOT" % Provided,
  "com.github.MilkBowl" % "VaultAPI" % "1.7" % Provided,
  "net.luckperms" % "api" % "5.2" % Provided,
  "net.dv8tion" % "JDA" % "4.2.0_228" exclude("club.minnced", "opus-java")
)

scalacOptions += "-target:11"
javacOptions ++= Seq("-source", "11", "-target", "11")

Compile / resourceGenerators += Def.task {
  val file = (Compile / resourceManaged).value / "plugin.yml"
  val pluginConfig = scala.io.Source.fromFile("plugin.yml").getLines.mkString("\n")
  val contents = pluginConfig.replace("@VERSION@", version.value)
  IO.write(file, contents)
  Seq(file)
}.taskValue

assemblyMergeStrategy in assembly := {
  case "module-info.class" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

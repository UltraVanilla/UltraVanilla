scalaVersion := "2.13.5"
organization := "world.ultravanilla"
version := "2.0.0-SNAPSHOT"
name := "UltraVanilla"
resolvers ++= List(
    "jitpack.io" at "https://jitpack.io",
    "papermc" at "https://papermc.io/repo/repository/maven-public/",
    "dmulloy2-repo" at "https://repo.dmulloy2.net/nexus/repository/public/",
    "jcenter" at "https://jcenter.bintray.com"
)
libraryDependencies ++= List(
    "com.destroystokyo.paper" % "paper-api" % "1.16.5-R0.1-SNAPSHOT" % Provided,
    "net.luckperms" % "api" % "5.2" % Provided,
    "net.dv8tion" % "JDA" % "4.2.0_228" exclude("club.minnced", "opus-java")
)

scalacOptions += "-target:16"
javacOptions ++= Seq("-source", "16", "-target", "16")

Compile / resourceGenerators += Def.task {
    val file = (Compile / resourceManaged).value / "plugin.yml"
    val pluginConfig = scala.io.Source.fromFile("plugin.yml").getLines.mkString("\n")
    val contents = pluginConfig.replace("@VERSION@", version.value)
    IO.write(file, contents)
    Seq(file)
}.taskValue

assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.first
    case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
}

val sbtDefaultTarget = System.getProperty("sbtDefaultTarget", "false")
val sbtOutputDirectory = System.getProperty("sbtOutputDirectory", "testserver/plugins")

assembly / assemblyOutputPath := {
    val default = (assembly / assemblyOutputPath).value
    if (sbtDefaultTarget == "true") {
        default
    } else {
        file(sbtOutputDirectory + "/" + name.value + "-" + version.value + ".jar")
    }
}

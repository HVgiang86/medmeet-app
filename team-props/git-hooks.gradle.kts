fun isLinuxMacOsOrWindows(): Boolean {
    return isMac() || isLinux() || isWindows()
}

fun isLinux(): Boolean {
    return System.getProperty("os.name").lowercase().contains("linux")
}

fun isWindows(): Boolean {
    return System.getProperty("os.name").lowercase().contains("window")
}

fun isMac(): Boolean {
    val osName = System.getProperty("os.name").lowercase()
    return osName.contains("mac os")
            || osName.contains("macos")
}

tasks.register("copyGitHooks", Copy::class) {
    description = "Copies the git hooks from team-props/git-hooks to the .git folder."
    val path = if (isLinux()) "$rootDir/team-props/git-hooks/linux"
    else if (isMac()) "$rootDir/team-props/git-hooks/mac"
    else "$rootDir/team-props/git-hooks/windows"
    from(path) {
        include("**/*.sh")
        rename("(.*).sh", "$1")
    }
    into("$rootDir/.git/hooks")
    onlyIf { isLinuxMacOsOrWindows() }
}

tasks.register("installGitHooks", Exec::class) {
    description = "Installs the pre-commit git hooks from team-props/git-hooks."
    group = "git hooks"
    workingDir = rootDir
    setCommandLine("chmod", "-R", "+x", ".git/hooks/")
    dependsOn("copyGitHooks")
    onlyIf { isLinuxMacOsOrWindows() }
    doLast {
        logger.info("Git hook installed successfully!")
    }
}

afterEvaluate {
    tasks["clean"].dependsOn("installGitHooks")
}

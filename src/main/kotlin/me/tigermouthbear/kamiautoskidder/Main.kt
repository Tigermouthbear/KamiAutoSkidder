package me.tigermouthbear.kamiautoskidder

import java.io.File
import kotlin.system.exitProcess

/**
 * @author Tigermouthbear
 * Created on April 3, 2020
 */

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Parameters: <config file>")
        exitProcess(-1)
    }

    println("\n" +
            "   _____          __          ___________   .__    .___\n" +
            "  /  _  \\  __ ___/  |_  ____ /   _____/  | _|__| __| _/\n" +
            " /  /_\\  \\|  |  \\   __\\/  _ \\\\_____  \\|  |/ /  |/ __ | \n" +
            "/    |    \\  |  /|  | (  <_> )        \\    <|  / /_/ | \n" +
            "\\____|__  /____/ |__|  \\____/_______  /__|_ \\__\\____ | \n" +
            "        \\/                          \\/     \\/       \\/ \n")
    println("Created by Tigermouthbear\n\n")

    KamiAutoSkidder.run(File(args[0]))

    println("\nFinished skidding")
}
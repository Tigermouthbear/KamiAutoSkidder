package me.tigermouthbear.kamiautoskidder

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.HashMap
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @author Tigermouthbear
 * Created on April 3, 2020
 */

object AsmHelper {
    private val files: MutableMap<String, ByteArray> = HashMap()
    private val classNodes: MutableMap<String, ClassNode> = HashMap()


    fun setJar(jar: JarFile) {
        val entries = jar.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            try {
                jar.getInputStream(entry).use { `in` ->
                    val bytes: ByteArray
                    val baos = ByteArrayOutputStream()
                    val buf = ByteArray(256)
                    var n: Int
                    while (`in`.read(buf).also { n = it } != -1) {
                        baos.write(buf, 0, n)
                    }
                    bytes = baos.toByteArray()

                    if (!entry.name.endsWith(".class")) {
                        files[entry.name] = bytes
                    } else {
                        val c = ClassNode()
                        ClassReader(bytes).accept(c, ClassReader.EXPAND_FRAMES)
                        classNodes.put(c.name, c)
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveJar(output: String) {
        var loc = output
        if (!loc.endsWith(".jar")) loc += ".jar"
        val jarPath = Paths.get(loc)
        Files.deleteIfExists(jarPath)
        val outJar = JarOutputStream(Files.newOutputStream(jarPath, *arrayOf(StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)))
        //Write classes into obf jar
        for (node in getClassNodes().values) {
            val entry = JarEntry(node.name + ".class")
            outJar.putNextEntry(entry)
            val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
            node.accept(writer)
            outJar.write(writer.toByteArray())
            outJar.closeEntry()
        }
        //Copy files from previous jar into obf jar
        for ((key, value) in getFiles()) {
            outJar.putNextEntry(JarEntry(key))
            outJar.write(value)
            outJar.closeEntry()
        }
        outJar.close()
    }

    fun getFiles(): MutableMap<String, ByteArray> {
        return files
    }

    fun getClassNodes(): MutableMap<String, ClassNode> {
        return classNodes
    }

    fun setStringVal(field: FieldNode, name: String): String {
        if(field.value is String) {
            field.value = name;
        }

        return name
    }
}
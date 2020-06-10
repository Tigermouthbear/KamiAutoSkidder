package dev.tigr.kamiautoskidder

import dev.tigr.kamiautoskidder.config.ConfigReader
import dev.tigr.kamiautoskidder.config.DictionaryConfig
import dev.tigr.kamiautoskidder.config.StringConfig
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import java.io.File
import java.util.jar.JarFile

/**
 * @author Tigermouthbear
 * Created on April 3, 2020
 */

object KamiAutoSkidder {
    private val restrictedClasses = arrayOf("me/zeroeightsix/kami/module/modules/capes/Capes\$1")

    private val input = StringConfig("input")
    private val output = StringConfig("output")

    private val name = StringConfig("name")
    private val version = StringConfig("version")
    private val appid = StringConfig("appid")
    private val chatDict = DictionaryConfig("chat-append")

    fun run(file: File) {
        ConfigReader.read(file)

        val blue = file.name.contains("blue")

        AsmHelper.setJar(JarFile(input.value))

        val mainClass: ClassNode = AsmHelper.getClassNodes().get("me/zeroeightsix/kami/KamiMod")!!

        val MODNAME = name.value
        val MODID = name.value.replace(" ", "").toLowerCase();
        val MODVER = version.value
        val MODCONFIG = MODNAME.replace(" ", "") + "Config.json"
        val MODLASTCONFIG = MODNAME.replace(" ", "") + "LastConfig.txt"
        val APPID = appid.value


        var num = 0
        val annList: MutableList<Any> = ArrayList()
        for(ann: Any in mainClass.visibleAnnotations.get(0).values) {
            var newAnn: Any = ann
            if(ann is String) {
                when (num) {
                    1 -> newAnn = MODID
                    3 -> newAnn = MODNAME
                    5 -> newAnn = MODVER
                    7 -> newAnn = ""
                }
            }
            annList.add(newAnn)
            num += 1
        }
        mainClass.visibleAnnotations.get(0).values = annList


        // store static vars for distribution to ldcs
        val valueMap: MutableMap<String, String> = HashMap()

        // redefine static vars
        mainClass.fields.forEach { field: FieldNode ->
            when (field.name) {
                "MODNAME" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(field, MODNAME)
                "MODID" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(field, MODID)
                "MODVER" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(field, MODVER)
                "MODVERSMALL" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(field, MODVER)
                "KAMI_CONFIG_NAME_DEFAULT" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(field, MODCONFIG)
                "APPID" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(field, APPID)
                "KAMI_KANJI" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(
                        field,
                        chatDict.value.getString("kanji")
                    )
                "KAMI_BLUE" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(
                        field,
                        chatDict.value.getString("blue")
                    )
                "KAMI_JAPANESE_ONTOP" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(
                        field,
                        chatDict.value.getString("japanontop")
                    )
                "KAMI_ONTOP" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(
                        field,
                        chatDict.value.getString("kamiontop")
                    )
                "KAMI_WEBSITE" -> valueMap[field.value as String] =
                    AsmHelper.setStringVal(
                        field,
                        chatDict.value.getString("website")
                    )
            }
        }

        // redefine ldcs
        for(cn in AsmHelper.getClassNodes().values.stream().filter{ cn -> !restrictedClasses.contains(cn.name)}) {
            for (mn in cn.methods) {
                for (ain in mn.instructions.toArray()) {
                    if (ain.opcode == Opcodes.LDC) {
                        val ldc = ain as LdcInsnNode
                        if(ldc.cst is String)
                        {
                            for(value: String in valueMap.keys) {
                                if((ldc.cst as String).contains(value)) {
                                    val initVal = ldc.cst as String
                                    ldc.cst = (ldc.cst as String).replace(value, valueMap[value]!!, true)
                                    println(("Edited " + cn.name + " to change " + initVal + " to " + ldc.cst as String).replace("\n", ""))
                                }
                            }

                            when (ldc.cst) {
                                "KAMI" -> ldc.cst = MODNAME
                                "KAMI Blue" -> ldc.cst = MODNAME
                                "KAMIBlueLastConfig.txt" -> ldc.cst = MODLASTCONFIG
                                "KAMILastConfig.txt" -> ldc.cst = MODLASTCONFIG
                            }
                        }
                    }
                }
            }
        }

        // fix assets
        val remove: ArrayList<String> = ArrayList()
        val tempFileMap: MutableMap<String, ByteArray> = HashMap()
        for(fileName: String in AsmHelper.getFiles().keys) {
            var newFileName: String = ""
            when {
                fileName.startsWith("assets/kamiblue") -> newFileName = "assets/" + MODID + fileName.replace("assets/kamiblue", "")
                fileName.startsWith("assets/kami") -> newFileName = "assets/" + MODID + fileName.replace("assets/kami", "")
                fileName.startsWith("assets/minecraft/kamiblue") -> newFileName = "assets/minecraft/" + MODID + fileName.replace("assets/minecraft/kamiblue", "")
                fileName.startsWith("assets/minecraft/kami") -> newFileName = "assets/minecraft/" + MODID + fileName.replace("assets/minecraft/kami", "")
            }

            if(newFileName != "" && AsmHelper.getFiles()[fileName] != null) {
                var byteArray: ByteArray = AsmHelper.getFiles()[fileName]!!

                val dir = File("assets")
                if(dir.exists()) {
                    for(file: File in dir.listFiles()) {
                        if(newFileName.contains(file.name)) {
                            byteArray = file.readBytes()
                            println("Replaced asset $fileName with $file")
                            break
                        }
                    }
                }

                tempFileMap[newFileName] = byteArray
                println("Fixed $fileName")
                remove.add(fileName)
            }
        }

        //save changes
        AsmHelper.getFiles().putAll(tempFileMap)
        remove.forEach{fileName -> AsmHelper.getFiles().remove(fileName)}

        // save output
        AsmHelper.saveJar(output.value)
    }
}

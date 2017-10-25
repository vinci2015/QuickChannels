package com.github.vinci.quickChannels;

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskState


public class QuickChannels implements Plugin<Project>,TaskExecutionListener {
    def Project rootProject
    def destinationPath
    def apkPath
    def String nameFormat
    def channelFile
    @Override
    void apply(Project project) {
        rootProject = project
        rootProject.extensions.create('channelExt',ChannelExtension)
        project.gradle.addListener(this)
    }

    @Override
    void beforeExecute(Task task) {
        if(task.path == ':app:assembleRelease'){
            println "${task.path} start to execute"
        }
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        if(task.path == ':app:assembleRelease'){
            println "${task.name} end"
            destinationPath = rootProject.channelExt.desPath
            apkPath = rootProject.channelExt.apkPath
            nameFormat = rootProject.channelExt.nameFormat
            channelFile = rootProject.channelExt.channelFile
            println "destination path : $destinationPath"
            println "apk path : $apkPath"
            task.project.tasks.create('genChannelPackage'){
                println '----------------------------generate channel packages-------------------------'
                def channels = getChannels()
                channels.each { channel ->
                    createNewPackage(channel)
                }
                File file = new File("$destinationPath\\new")
                assert file.exists()
                file.deleteDir()
                println '----------------------------generate end-------------------------------'
            }
        }
    }
    def createNewPackage(name) {
        println "channel name : $name"
        def modefiedName = formatName(name)
        println modefiedName
        File mfile = new File("$destinationPath\\$modefiedName")
        if(mfile.exists()){
            println "exist"
            println mfile.delete()
        }
        try {
            println 'zip start'
            FileTree tree = rootProject.fileTree("$destinationPath\\new")
            if (!tree.dir.exists()) {
                ZipFile zipFile = new ZipFile(apkPath)
                println destinationPath
                zipFile.extractAll("$destinationPath\\new")
            }
            File parent = tree.dir.listFiles(new FileFilter() {
                @Override
                boolean accept(File pathname) {
                    return pathname.name == "META-INF"
                }
            }).first()
            def parentPath = parent.getAbsolutePath()
            File file = new File("$parentPath\\pwchannel-$name")
            file.setText(file.name)
            ZipFile zipFile1 = new ZipFile("$destinationPath\\$modefiedName")
            ZipParameters zipParameters = new ZipParameters()
            zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE)
            zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL)
            tree.dir.listFiles().each { ele ->
                if (ele.isDirectory()) {
                    zipFile1.addFolder(ele, zipParameters)
                } else {
                    zipFile1.addFile(ele, zipParameters)
                }
            }
            file.delete()
            println 'zip end '
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def getChannels() {
        def pro
        if (channelFile != ''){
            pro = new File(channelFile)
        }else {
            pro = new File('channels.properties')
        }
        def properties = new Properties()
        pro.withInputStream { stream ->
            properties.load(stream)
        }
        return properties.values()
    }
    def read(String name){
        def result = System.console().readLine("\n $name already exist ,press yes to cover it or input no to cancle")
        return result
    }
    def formatName(String channel){
        return nameFormat.replace("{channel}",channel)
    }
}
public class ChannelExtension{
    def desPath = 'E:\\outputapks'
    def apkPath = ''
    def String nameFormat = ''
    def channelFile = ""
}

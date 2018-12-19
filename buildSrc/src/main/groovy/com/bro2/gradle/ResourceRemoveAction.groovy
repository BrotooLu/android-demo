package com.bro2.gradle

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.PackageApplication
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ResourceRemoveAction implements Action<DefaultTask> {

    @Input
    Project project

    @Input
    BaseVariant variant

    // aar中的assets目录
    @Input
    String aarAssetsFolderPath

    // jar包
    @Input
    Collection<File> jarResFiles

    String[] removeFiles = []
    String[] containStrings = []
    String[] removeFolders = []
    @Override
    void execute(DefaultTask defaultTask) {
        println('Remove assets files:')

        // aar中的assets
//        String filesFolder = "${project.buildDir}/${AndroidProject.FD_INTERMEDIATES}/assets/${variant.dirName}"
        if (aarAssetsFolderPath == null || aarAssetsFolderPath.length() == 0) {
            println("task: " + defaultTask.name)
            if (defaultTask instanceof PackageApplication) {
                def assets = ((PackageApplication) defaultTask).getAssets()
                if (assets.hasProperty('path')) {
                    aarAssetsFolderPath = assets.path
                } else if (assets.hasProperty('asPath')) {
                    aarAssetsFolderPath = assets.asPath
                }
                println("packageApplication aarAssetsFolderPath: $aarAssetsFolderPath")
            }
        }
        if (aarAssetsFolderPath != null && aarAssetsFolderPath.length() != 0) {
            def fileDir = new File(aarAssetsFolderPath)
            FileTree tree = project.fileTree(dir: fileDir)

            tree.filter { File file ->
                return file.exists() && forceRemove(file.path, variant.dirName)
            }.each { File file ->
                println("Remove $file.path")
                file.delete()
            }
        }

        // jar中的assets
        if (jarResFiles == null || jarResFiles.size() == 0) {
            if (defaultTask instanceof PackageApplication) {
                jarResFiles = ((PackageApplication) defaultTask).getJavaResourceFiles().asCollection()
                println("packageApplication jarResFiles: $jarResFiles")
            }
        }

        if (jarResFiles != null) {
            for (File jarFile : jarResFiles) {
                println("jarResFiles jar: $jarFile")
                if (jarFile.exists() && jarFile.name.endsWith('.jar')) {
                    println("Search in $jarFile.path")
                    def tempJarFile = new File("jar.temp", jarFile.parentFile)
                    if (tempJarFile.exists()) {
                        tempJarFile.delete()
                    }

                    process(jarFile, tempJarFile)

                    jarFile.delete()
                    tempJarFile.renameTo(jarFile)
                }
            }
        }
    }

    private void process(File zipFile, File tempZipFile) {
        byte[] buf = new byte[1024 * 8]

        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile))
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tempZipFile))

        ZipEntry entry = zin.getNextEntry()
        while (entry != null) {
            String name = entry.getName()
            if (name.contains("assets/")) {
                // 只检查assets目录
                if (forceRemove(name, null)) {
                    entry = zin.getNextEntry()
                    println("Remove $name in $zipFile.name")
                    continue
                }
            }

            // 参照minify中的做法
            // Add ZIP entry to output stream.
            ZipEntry zipEntry = new ZipEntry(name)

            if (ZipEntry.STORED == entry.getMethod()) {
                zipEntry.setMethod(entry.getMethod())
                zipEntry.setSize(entry.getSize())
                zipEntry.setCompressedSize(entry.getCompressedSize())
                zipEntry.setCrc(entry.getCrc())
            }

            out.putNextEntry(zipEntry)

            // Transfer bytes from the ZIP file to the output file
            int len
            while ((len = zin.read(buf)) > 0) {
                out.write(buf, 0, len)
            }
            out.closeEntry()
            entry = zin.getNextEntry()
        }
        // Close the streams
        zin.close()
        out.close()
    }

    private boolean forceRemove(String pathName, String variantDirName) {
        println("removing: $pathName at $variantDirName")

        if (true) {
            return false
        }

        if (pathName == null || pathName.length() == 0) {
            return false
        }
        String[] splits = pathName.split('/')
        if (splits == null || splits.length == 0) {
            return false
        }

        boolean remove = false

        String fileName = splits[splits.length - 1]
        if (fileName != null && fileName.length() != 0) {
            if (removeFiles != null) {
                for (String removeFileName : removeFiles) {
                    if (removeFileName != null && removeFileName.length() > 0 && fileName.startsWith(removeFileName)) {
                        remove = true
                        println("$pathName startsWith $removeFileName")
                        break
                    }
                }
            }
            if (remove) {
                return remove
            }
            if (containStrings != null) {
                for (String str : containStrings) {
                    if (str != null && str.length() > 0 && fileName.contains(str)) {
                        remove = true
                        println("$pathName contains $str")
                        break
                    }
                }
            }
            if (remove) {
                return remove
            }
            if (removeFolders != null) {
                String dirPath = ''
                if (variantDirName != null && variantDirName.length() > 0) {
                    dirPath = variantDirName + '/'
                }
                for (String str : removeFolders) {
                    if (str != null && str.length() > 0) {
                        String folder = str.endsWith('/') ? str.substring(0, str.length() - 1) : str
                        if (pathName ==~ /(.*\/)*assets\/$dirPath$folder(\/.+)+/) {
                            remove = true
                            println("$pathName is in $folder ")
                            break
                        }
                    }
                }
            }
        }
        return remove
    }
}
package com.bro2.gradle.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class DemoPlugin extends Transform implements Plugin<Project> {
    private static final String TAG = "demo-transform"

    private static final String SUFFIX_CLASS = ".class"
    private static final int SUFFIX_CLASS_LEN = SUFFIX_CLASS.length()

    private List<String> exceptClassList = ["com.bro2.sp.B2SPConverter"]
    private List<String> exceptJarList = ["com.android.support:support-annotations:27.1.1"]
    private Project project
    private Logger logger

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        transform(transformInvocation.context, transformInvocation.inputs, transformInvocation.referencedInputs, transformInvocation.outputProvider, transformInvocation.incremental)
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        logger.error(TAG + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<cips-transform start")
        ClassPool classPool = new ClassPool()
        project.android.bootClasspath.each {
            classPool.appendClassPath((String) it.absolutePath)
        }

        inputs.eachWithIndex { it, index ->
            it.directoryInputs.each { dir ->
                classPool.appendClassPath(dir.file.absolutePath)
            }

            it.jarInputs.each { jar ->
                classPool.appendClassPath(jar.file.absolutePath)
            }
        }

        inputs.eachWithIndex { it, index ->
            it.directoryInputs.each { dir ->
                dir.changedFiles.each {
                    logger.error(TAG + " file key: ${it.key} file: ${it.value.name()}")
                }
                File dest = outputProvider.getContentLocation(dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY)
                logger.error(TAG + " transform dir: " + dir.file.absolutePath + " destDir: " + dest.absolutePath)
                transformDir(classPool, dir.file, dest)
            }

            it.jarInputs.each { jar ->
                File dest = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
                if (exceptJarList.contains(jar.name)) {
                    logger.error(TAG + " copy jar: ${jar.name} from: ${jar.file.absolutePath} to: ${dest.absolutePath}")
                    FileUtils.copyFile(jar.file, dest)
                } else {
                    logger.error(TAG + " transform jar: ${jar.name} from: ${jar.file.absolutePath} to: ${dest.absolutePath}")
                    transformJar(classPool, jar.file, dest)
                }
            }
        }
        logger.error(TAG + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>cips-transform end")
    }

    private CtClass transformClassIfNeed(ClassPool classPool, String fileName) {
        int classNameEndIndex = fileName.indexOf(SUFFIX_CLASS)
        if (classNameEndIndex != -1 && classNameEndIndex + SUFFIX_CLASS_LEN == fileName.length()) {
            String className = fileName.substring(0, classNameEndIndex).replaceAll("/", ".")
            if (!exceptClassList.contains(className)) {
                CtClass clazz = classPool.getCtClass(className)
                if (clazz.frozen) {
                    clazz.defrost()
                }

                clazz.instrument(new ExprEditor() {
                    @Override
                    void edit(MethodCall m) throws CannotCompileException {
                        CtClass calledClass = classPool.getCtClass(m.className)
                        if (calledClass.subclassOf(classPool.getCtClass("android.content.Context"))
                                && m.methodName == "getSharedPreferences"
                                && m.signature == "(Ljava/lang/String;I)Landroid/content/SharedPreferences;") {
                            logger.error(TAG + " ******************start replace: file: " + m.fileName + " line: " + m.lineNumber + " signature: " + m.signature)
                            m.replace('''{$_ = com.bro2.sp.B2SPConverter.getSharedPreference($0, $1, $2);}''')
                        }

                    }
                })
                return clazz
            }
        }

        return null
    }

    private void transformDir(ClassPool classPool, File srcDir, File destDir) {
        if (!destDir.isDirectory() || !destDir.exists()) {
            destDir.mkdirs()
        }

        int childFileStartIndex = srcDir.absolutePath.length()
        if (!srcDir.absolutePath.endsWith("/")) {
            childFileStartIndex = childFileStartIndex + 1
        }

        String destDirPath = destDir.absolutePath
        if (!destDirPath.endsWith("/")) {
            destDirPath += "/"
        }

        srcDir.eachFileRecurse { File file ->
            String childFileName = file.absolutePath.substring(childFileStartIndex)
            CtClass clazz = transformClassIfNeed(classPool, childFileName)
            if (clazz != null) {
                clazz.writeFile(destDirPath)
                clazz.detach()
            } else if (file.isFile()) {
                FileUtils.copyFile(file, new File(destDirPath + childFileName))
            } else {
                File dir = new File(destDirPath + childFileName)
                if (!dir.isDirectory() || !dir.exists()) {
                    dir.mkdirs()
                }
            }
        }

    }

    private void transformJar(ClassPool classPool, File srcJar, File destJar) {
        File destParent = destJar.getParentFile()
        if (!destParent.isDirectory() || !destParent.exists()) {
            destParent.mkdirs()
        }

        JarOutputStream destZOS = new JarOutputStream(new FileOutputStream(destJar))
        JarFile srcJarFile = new JarFile(srcJar)
        Enumeration<JarEntry> srcEntries = srcJarFile.entries()
        while (srcEntries.hasMoreElements()) {
            JarEntry jarEntry = srcEntries.nextElement()
            JarEntry destEntry = new JarEntry(jarEntry.name)
            InputStream srcEntryStream = srcJarFile.getInputStream(jarEntry)
            destZOS.putNextEntry(destEntry)
            CtClass clazz = transformClassIfNeed(classPool, jarEntry.name)
            if (clazz != null) {
                byte[] code = clazz.toBytecode()
                destZOS.write(code, 0, code.length)
                clazz.detach()
            } else {
                IOUtils.copy(srcEntryStream, destZOS)
            }
            srcEntryStream.close()
            destZOS.closeEntry()
        }
        destZOS.close()
    }

    @Override
    String getName() {
        return "demo-transform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void apply(Project project) {
        this.project = project
        logger = project.logger
        project.android.registerTransform(this)
    }

}
package com.bro2.gradle.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

public class DemoPlugin extends Transform implements Plugin<Project> {
    private Logger logger

    @Override
    void apply(Project project) {
        project.android.registerTransform(this)
        logger = project.logger
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        logger.error("transform one: ${inputs.size()}")
        inputs.eachWithIndex { it, index ->
            logger.error("index ${index} dir size: ${it.directoryInputs.size()} jarsize: ${it.jarInputs.size()}")
            it.directoryInputs.each { dir ->
                dir.changedFiles.each {
                    logger.error("file key: ${it.key} file: ${it.value.name()}")
                }
            }
            it.jarInputs.each {
                logger.error("jar name: ${it.name} status: ${it.status}")
            }
        }
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        logger.error('transform two')
        super.transform(transformInvocation)
    }

    @Override
    String getName() {
        return 'demo-transform'
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
}
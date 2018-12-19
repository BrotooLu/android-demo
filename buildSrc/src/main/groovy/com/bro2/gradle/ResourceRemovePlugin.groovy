package com.bro2.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

/**
 * Created by Brotoo on 2018/12/19
 */
class ResourceRemovePlugin implements Plugin<Project> {

    static DomainObjectCollection<BaseVariant> getAndroidVariants(Project project) {
        if (project.getPlugins().hasPlugin(AppPlugin)) {
            return (DomainObjectCollection<BaseVariant>) ((AppExtension) ((AppPlugin) project.getPlugins().getPlugin(AppPlugin)).extension).applicationVariants;
        } else if (project.getPlugins().hasPlugin(LibraryPlugin)) {
            return (DomainObjectCollection<BaseVariant>) ((LibraryExtension) ((LibraryPlugin) project.getPlugins().getPlugin(LibraryPlugin)).extension).libraryVariants;
        } else {
            throw new ProjectConfigurationException("Plugin requires the 'android' or 'android-library' plugin to be configured.", null)
        }
    }

    @Override
    void apply(Project project) {
        def isAppPlugin = project.plugins.withType(AppPlugin)
        if (!isAppPlugin) {
            return
        }

        project.afterEvaluate {
            getAndroidVariants(project).all { variant ->
                def variantName = variant.name.capitalize()
                println("variantName: $variantName")
                def pkgTask = project.tasks.findByName("package${variantName}")
                if (pkgTask == null) {
                    return
                }

                ResourceRemoveAction action = new ResourceRemoveAction()
                action.project = project
                action.variant = variant
                pkgTask.doFirst(action)
            }
        }
    }
}

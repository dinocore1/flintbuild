package com.devsmart.flintbuild;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FlintPlugin implements Plugin<Project> {


    @Override
    public void apply(Project project) {
        FlintExtention ext = project.getExtensions().create("flint", FlintExtention.class, project);


    }
}

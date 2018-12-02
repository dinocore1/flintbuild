package com.devsmart.flintbuild;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FlintPlugin implements Plugin<Project> {

    private FlintExtention mExt;

    @Override
    public void apply(Project project) {
        FlintExtention mExt = project.getExtensions().create("flint", FlintExtention.class, project);
        project.afterEvaluate(mAfterEval);

    }

    private final Action<Project> mAfterEval = new Action<Project>() {

        @Override
        public void execute(Project project) {

        }
    };
}

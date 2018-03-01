/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.spine.tools.gradle.compiler;

import io.spine.tools.compiler.type.ProtoToJavaTypeMapper;
import io.spine.tools.gradle.GradleTask;
import io.spine.tools.gradle.SpinePlugin;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;

import static io.spine.tools.gradle.TaskName.GENERATE_PROTO;
import static io.spine.tools.gradle.TaskName.GENERATE_TEST_PROTO;
import static io.spine.tools.gradle.TaskName.MAP_PROTO_TO_JAVA;
import static io.spine.tools.gradle.TaskName.MAP_TEST_PROTO_TO_JAVA;
import static io.spine.tools.gradle.TaskName.PROCESS_RESOURCES;
import static io.spine.tools.gradle.TaskName.PROCESS_TEST_RESOURCES;
import static io.spine.tools.gradle.compiler.Extension.getMainDescriptorSetPath;
import static io.spine.tools.gradle.compiler.Extension.getMainTargetGenResourcesDir;
import static io.spine.tools.gradle.compiler.Extension.getTestDescriptorSetPath;
import static io.spine.tools.gradle.compiler.Extension.getTestTargetGenResourcesDir;

/**
 * Plugin which maps all Protobuf types to the corresponding Java classes.
 *
 * <p>Generates a {@code .properties} file, which contains entries like:
 *
 * <p>{@code PROTO_TYPE_URL=JAVA_FULL_CLASS_NAME}
 *
 * @author Mikhail Mikhaylov
 * @author Alexander Yevsyukov
 * @author Alexander Litus
 * @author Alex Tymchenko
 */
public class ProtoToJavaMapperPlugin extends SpinePlugin {

    /**
     * Adds tasks to map Protobuf types to Java classes in the project.
     */
    @Override
    public void apply(final Project project) {
        final Action<Task> mainScopeAction = mainScopeActionFor(project);

        logDependingTask(MAP_PROTO_TO_JAVA, PROCESS_RESOURCES, GENERATE_PROTO);

        final GradleTask mainScopeTask =
                newTask(MAP_PROTO_TO_JAVA, mainScopeAction)
                        .insertAfterTask(GENERATE_PROTO)
                        .insertBeforeTask(PROCESS_RESOURCES)
                        .applyNowTo(project);

        final Action<Task> testScopeAction = testScopeActionFor(project);

        logDependingTask(MAP_TEST_PROTO_TO_JAVA, PROCESS_TEST_RESOURCES, GENERATE_TEST_PROTO);

        final GradleTask testScopeTask =
                newTask(MAP_TEST_PROTO_TO_JAVA, testScopeAction)
                        .insertAfterTask(GENERATE_TEST_PROTO)
                        .insertBeforeTask(PROCESS_TEST_RESOURCES)
                        .applyNowTo(project);

        log().debug("Proto-to-Java mapping phase initialized with tasks: {}, {}",
                    mainScopeTask, testScopeTask);
    }

    private Action<Task> testScopeActionFor(final Project project) {
        log().debug("Initializing the proto to java mapping for the \"test\" source code");
        return new Action<Task>() {
            @Override
            public void execute(Task task) {
                processDescriptorSet(getTestDescriptorSetPath(project),
                                     getTestTargetGenResourcesDir(project)
                );
            }
        };
    }

    private Action<Task> mainScopeActionFor(final Project project) {
        log().debug("Initializing the proto to java mapping for the \"main\" source code.");
        return new Action<Task>() {
            @Override
            public void execute(Task task) {
                processDescriptorSet(getMainDescriptorSetPath(project),
                                     getMainTargetGenResourcesDir(project)
                );
            }
        };
    }

    private void processDescriptorSet(String descriptorSetFile, String targetDir) {
        final File setFile = new File(descriptorSetFile);
        if (!setFile.exists()) {
            logMissingDescriptorSetFile(setFile);
            return;
        }

        ProtoToJavaTypeMapper.processDescriptorSet(setFile, targetDir);
    }
}

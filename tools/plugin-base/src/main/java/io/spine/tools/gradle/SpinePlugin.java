/*
 * Copyright 2019, TeamDev. All rights reserved.
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
package io.spine.tools.gradle;

import io.spine.logging.Logging;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Abstract base for Spine plugins.
 *
 * <p>Brings helper functionality to operate the Gradle build lifecycle.
 */
public abstract class SpinePlugin implements Plugin<Project>, Logging {

    /**
     * Create a new instance of {@link GradleTask.Builder}.
     *
     * <p>NOTE: the Gradle build steps are NOT modified until
     * {@link GradleTask.Builder#applyNowTo(Project)} is invoked.
     *
     * @param name   the name for the new task
     * @param action the action to invoke during the new task processing
     * @return the instance of {@code Builder}
     * @see GradleTask.Builder#applyNowTo(Project)
     */
    protected GradleTask.Builder newTask(TaskName name, Action<Task> action) {
        GradleTask.Builder result = new GradleTask.Builder(name, action);
        return result;
    }

    /**
     * Resolves an absolute file name obtained as a string from the passed supplier.
     */
    public static File resolve(Supplier<String> path) {
        String pathname = path.get();
        Path normalized = new File(pathname).toPath()
                                            .normalize();
        File result = normalized.toAbsolutePath()
                                .toFile();
        return result;
    }

    @SuppressWarnings("HardcodedLineSeparator") // handled by Slf4J
    protected void logMissingDescriptorSetFile(File setFile) {
        _debug("Missing descriptor set file {}.\n" +
                       "Please enable descriptor set generation. See: " +
                       "https://github.com/google/protobuf-gradle-plugin/blob/master/README.md" +
                       "#generate-descriptor-set-files",
               setFile.getPath());
    }
}

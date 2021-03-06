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

package io.spine.tools.gradle.compiler;

import com.google.common.collect.ImmutableList;
import io.spine.io.Files2;
import io.spine.tools.gradle.compiler.protoc.GeneratedInterfaces;
import io.spine.tools.gradle.compiler.protoc.GeneratedMethods;
import io.spine.tools.protoc.AddMethods;
import io.spine.tools.protoc.Classpath;
import io.spine.tools.protoc.SpineProtocConfig;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import static io.spine.tools.gradle.compiler.Extension.getInterfaces;
import static io.spine.tools.gradle.compiler.Extension.getMethods;
import static io.spine.util.Exceptions.newIllegalStateException;

/**
 * A {@link SpineProtocConfig} holder.
 */
final class ProtocPluginConfiguration {

    private final SpineProtocConfig config;

    private ProtocPluginConfiguration(SpineProtocConfig config) {
        this.config = config;
    }

    /**
     * Creates a configuration holder for a supplied {@code project}.
     */
    static ProtocPluginConfiguration forProject(Project project) {
        SpineProtocConfig config = assembleSpineProtocConfig(project);
        return new ProtocPluginConfiguration(config);
    }

    /**
     * Writes the configuration to the file denoted by the supplied {@code configPath}.
     */
    void writeTo(Path configPath) {
        Files2.ensureFile(configPath);
        try (FileOutputStream fos = new FileOutputStream(configPath.toFile())) {
            config.writeTo(fos);
        } catch (FileNotFoundException e) {
            throw newIllegalStateException(
                    e,
                    "Unable to create Spine Protoc Plugin configuration file at: `%s`.",
                    configPath);
        } catch (IOException e) {
            throw newIllegalStateException(
                    e,
                    "Unable store Spine Protoc Plugin configuration file at: `%s`.",
                    configPath);
        }
    }

    private static SpineProtocConfig assembleSpineProtocConfig(Project project) {
        GeneratedInterfaces interfaces = getInterfaces(project);
        GeneratedMethods methods = getMethods(project);
        AddMethods methodsGeneration = methods
                .asProtocConfig()
                .toBuilder()
                .setFactoryClasspath(projectClasspath(project))
                .build();
        SpineProtocConfig result = SpineProtocConfig
                .newBuilder()
                .setAddInterfaces(interfaces.asProtocConfig())
                .setAddMethods(methodsGeneration)
                .build();
        return result;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") // Classpath.Builder usage in `forEach`
    private static Classpath projectClasspath(Project project) {
        Classpath.Builder classpath = Classpath.newBuilder();
        Collection<JavaCompile> javaCompileViews = project.getTasks()
                                                          .withType(JavaCompile.class);
        ImmutableList.copyOf(javaCompileViews)
                     .stream()
                     .map(JavaCompile::getClasspath)
                     .map(FileCollection::getFiles)
                     .flatMap(Set::stream)
                     .map(File::getAbsolutePath)
                     .forEach(classpath::addJar);
        return classpath.build();
    }
}

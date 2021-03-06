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

package io.spine.js.gradle;

import io.spine.code.fs.js.DefaultJsProject;
import io.spine.code.fs.js.Directory;
import io.spine.js.generate.resolve.ExternalModule;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TempDirectory.class)
@DisplayName("Extension should")
class ExtensionTest {

    private static final String PLUGIN_ID = "io.spine.tools.proto-js-plugin";

    private static final String GROUP_ID = "my.company";
    private static final String VERSION = "42";

    private Project project;
    private DefaultJsProject defaultProject;

    @BeforeEach
    void setUp(@TempDir Path tempDirPath) {
        project = ProjectBuilder.builder()
                                .withProjectDir(tempDirPath.toFile())
                                .build();
        PluginManager pluginManager = project.getPluginManager();
        pluginManager.apply("java");
        pluginManager.apply(PLUGIN_ID);
        defaultProject = DefaultJsProject.at(project.getProjectDir());

        project.setGroup(GROUP_ID);
        project.setVersion(VERSION);
    }

    @Test
    @DisplayName("return the default directory with main generated Protobufs")
    void defaultMainGenProto() {
        Directory directory = Extension.getMainGenProto(project);
        Directory expected = defaultProject.proto()
                                           .mainJs();
        assertEquals(expected, directory);
    }

    @Test
    @DisplayName("return the set directory with main generated Protobufs")
    void customMainGenProto() {
        String customPath = "proto/main";
        pluginExtension().mainGenProtoDir = customPath;
        Directory directory = Extension.getMainGenProto(project);
        Directory expected = Directory.at(Paths.get(customPath));
        assertEquals(expected, directory);
    }

    @Test
    @DisplayName("return the default directory with test generated Protobufs")
    void defaultTestGenProto() {
        Directory directory = Extension.getTestGenProtoDir(project);
        Directory expected = defaultProject.proto()
                                           .testJs();
        assertEquals(expected, directory);
    }

    @Test
    @DisplayName("return the set directory with test generated Protobufs")
    void customTestGenProto() {
        String customPath = "proto/test";
        pluginExtension().testGenProtoDir = customPath;
        Directory directory = Extension.getTestGenProtoDir(project);
        Directory expected = Directory.at(Paths.get(customPath));
        assertEquals(expected, directory);
    }

    @Test
    @DisplayName("return the main descriptor set at the default path")
    void defaultMainDescriptorSet() {
        File file = Extension.getMainDescriptorSet(project);
        Path mainDescriptors = defaultProject.buildRoot()
                                             .descriptors()
                                             .mainDescriptors();
        File expected = mainDescriptors
                .resolve(GROUP_ID + '_' + project.getName() + '_' + VERSION + ".desc")
                .toFile();
        assertEquals(expected, file);
    }

    @Test
    @DisplayName("return the main descriptor set at the custom path")
    void customMainDescriptorSet() {
        String customPath = "main/types.desc";
        pluginExtension().mainDescriptorSetPath = customPath;
        File file = Extension.getMainDescriptorSet(project);
        File expected = new File(customPath);
        assertEquals(expected, file);
    }

    @Test
    @DisplayName("return the test descriptor set at the default path")
    void defaultTestDescriptorSet() {
        File file = Extension.getTestDescriptorSet(project);
        Path testDescriptors = defaultProject.buildRoot()
                                  .descriptors()
                                  .testDescriptors();
        File expected = testDescriptors
                .resolve(GROUP_ID + '_' + project.getName() + '_' + VERSION + "_test.desc")
                .toFile();
        assertEquals(expected, file);
    }

    @Test
    @DisplayName("return the test descriptor set at the custom path")
    void customTestDescriptorSet() {
        String customPath = "test/types.desc";
        pluginExtension().testDescriptorSetPath = customPath;
        File file = Extension.getTestDescriptorSet(project);
        File expected = new File(customPath);
        assertEquals(expected, file);
    }

    @Test
    @DisplayName("include predefined Spine modules")
    void includePredefinedModules() {
        List<ExternalModule> modules = Extension.modules(project);
        assertThat(modules).containsAtLeastElementsIn(Extension.predefinedModules());
    }

    @Test
    @DisplayName("add custom modules to resolve")
    void setModulesToResolve() {
        String moduleName = "foo-bar";
        Map<String, List<String>> modulesExt = pluginExtension().modules;
        modulesExt.put(moduleName, emptyList());
        List<ExternalModule> modules = Extension.modules(project);
        assertThat(modules).containsAtLeastElementsIn(Extension.predefinedModules());
        assertThat(modules).contains(new ExternalModule(moduleName, emptyList()));
    }

    private Extension pluginExtension() {
        return Extension.extension(project);
    }
}

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

package io.spine.code.java;

import io.spine.code.DefaultProject;
import io.spine.code.SourceCodeDirectory;

import java.io.File;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A default directory structure for a Spine-based Java project.
 *
 * <p>The project structure is based on the standard Maven/Gradle project conventions, with the
 * following directories under the project root:
 *
 * <ul>
 * <li>{@code src/main} — manually written code production code, with {@code java} and
 * {@code proto} sub-directories.
 *
 * <li>{@code src/test} — the code of tests.
 *
 * <li>{@code generated} — computer-generated code, with sub-directories:
 * <ul>
 *     <li>{@code java} — the code generated by the Protobuf Compiler.
 *     <li>{@code gRPC} — the code generated by the gPRC Protobuf Compiler Plug-in.
 *     <li>{@code spine} — the code generated by the Spine Model Compiler.
 * </ul>
 * </li>
 *
 * <li>{@code .spine} — temporary build artifacts directory used by the Spine Model Compiler.
 * </ul>
 */
public final class DefaultJavaProject extends DefaultProject {

    private DefaultJavaProject(Path path) {
        super(path);
    }

    public static DefaultJavaProject at(Path root) {
        checkNotNull(root);
        DefaultJavaProject result = new DefaultJavaProject(root);
        return result;
    }

    public static DefaultJavaProject at(File projectDir) {
        checkNotNull(projectDir);
        return at(projectDir.toPath());
    }

    public HandmadeCodeRoot src() {
        return new HandmadeCodeRoot(this, "src");
    }

    public GeneratedRoot generated() {
        return new GeneratedRoot(this);
    }

    private static class JavaCodeRoot extends SourceRoot {

        private JavaCodeRoot(DefaultProject parent, String name) {
            super(parent, name);
        }

        /**
         * A root directory for main Java code.
         */
        public Directory mainJava() {
            return Directory.rootIn(getMain());
        }

        /**
         * A root directory for test Java code.
         */
        public Directory testJava() {
            return Directory.rootIn(getTest());
        }
    }

    /**
     * A root source code directory for manually written code.
     *
     * <p>Adds a root directory for the proto code in addition to those exposed
     * by {@link io.spine.code.DefaultProject.SourceRoot SourceRoot}.
     */
    public static class HandmadeCodeRoot extends JavaCodeRoot {

        private HandmadeCodeRoot(DefaultProject parent, String name) {
            super(parent, name);
        }

        /**
         * A root for the main proto code.
         */
        public io.spine.code.proto.Directory mainProto() {
            return io.spine.code.proto.Directory.rootIn(getMain());
        }

        /**
         * A root for the test proto code.
         */
        public io.spine.code.proto.Directory testProto() {
            return io.spine.code.proto.Directory.rootIn(getTest());
        }
    }

    /**
     * A root directory for the generated code.
     */
    @SuppressWarnings("DuplicateStringLiteralInspection") // Random duplication.
    public static final class GeneratedRoot extends JavaCodeRoot {

        private static final String SPINE_DIR = "spine";
        private static final String GRPC_DIR = "grpc";
        private static final String RESOURCES_DIR = "resources";

        private GeneratedRoot(DefaultProject parent) {
            super(parent, "generated");
        }

        /**
         * Spine-generated source code directory.
         */
        public SourceCodeDirectory mainSpine() {
            return new SourceDir(getMain(), SPINE_DIR);
        }

        /**
         * Spine-generated source code directory for tests.
         */
        public SourceCodeDirectory testSpine() {
            return new SourceDir(getTest(), SPINE_DIR);
        }

        /**
         * The directory for the source code generated by gRPC.
         */
        public SourceCodeDirectory mainGrpc() {
            return new SourceDir(getMain(), GRPC_DIR);
        }

        /**
         * The directory for the test source code generated by gRPC.
         */
        public SourceCodeDirectory testGrpc() {
            return new SourceDir(getTest(), GRPC_DIR);
        }

        /**
         * The directory for generated resources.
         */
        public SourceCodeDirectory mainResources() {
            return new SourceDir(getMain(), RESOURCES_DIR);
        }

        /**
         * The directory for generated test resources.
         */
        public SourceCodeDirectory testResources() {
            return new SourceDir(getTest(), RESOURCES_DIR);
        }
    }
}

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

package io.spine.tools.protoc.given;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.compiler.PluginProtos;
import io.spine.option.OptionsProto;
import io.spine.tools.gradle.compiler.protoc.GeneratedInterfaces;
import io.spine.tools.gradle.compiler.protoc.GeneratedMethods;
import io.spine.tools.protoc.SpineProtocConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;

import static com.google.common.base.Charsets.UTF_8;

/**
 * A helper class for {@link com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
 * CodeGeneratorRequest}s creation.
 */
public final class CodeGeneratorRequestGiven {

    /** Prevents instantiation of this utility class. */
    private CodeGeneratorRequestGiven() {
    }

    /**
     * Creates a {@link com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest.Builder
     * CodeGeneratorRequest.Builder} instance with the default Protobuf descriptors and
     * Spine options set.
     */
    public static PluginProtos.CodeGeneratorRequest.Builder requestBuilder() {
        return PluginProtos.CodeGeneratorRequest
                .newBuilder()
                .addProtoFile(descriptorProto())
                .addProtoFile(spineOptionsProto())
                .setCompilerVersion(compilerVersion());
    }

    /**
     * Creates an instance of the latest supported Protobuf version.
     */
    public static PluginProtos.Version compilerVersion() {
        return PluginProtos.Version.newBuilder()
                                   .setMajor(3)
                                   .setMajor(6)
                                   .setPatch(1)
                                   .setSuffix("")
                                   .build();
    }

    /**
     * Creates a {@link SpineProtocConfig} out of the supplied {@code GeneratedMethods} and
     * a default instance of {@code GeneratedInterfaces} and stores it in the supplied {@code path}.
     *
     * @return base64 encoded path to the plugin configuration
     * @see #protocConfig(GeneratedInterfaces, GeneratedMethods, File)
     */
    public static String protocConfig(GeneratedMethods methods, Path configPath) {
        return protocConfig(new GeneratedInterfaces(), methods, configPath);
    }

    /**
     * Creates a {@link SpineProtocConfig} out of the supplied {@code GeneratedInterfaces} and
     * a default instance of {@code GeneratedMethods} and stores it in the supplied {@code path}.
     *
     * @return base64 encoded path to the plugin configuration
     * @see #protocConfig(GeneratedInterfaces, GeneratedMethods, File)
     */
    public static String protocConfig(GeneratedInterfaces interfaces, Path configPath) {
        return protocConfig(interfaces, new GeneratedMethods(), configPath);
    }

    /**
     * Creates a {@link SpineProtocConfig} out of the supplied {@code GeneratedInterfaces} and
     * {@code GeneratedMethods} and stores it in the supplied {@code path}.
     *
     * @return base64 encoded path to the plugin configuration
     */
    public static String
    protocConfig(GeneratedInterfaces interfaces, GeneratedMethods methods, Path configPath) {
        SpineProtocConfig config = SpineProtocConfig
                .newBuilder()
                .setAddInterfaces(interfaces.asProtocConfig())
                .setAddMethods(methods.asProtocConfig())
                .build();
        try (FileOutputStream fos = new FileOutputStream(configPath.toFile())) {
            config.writeTo(fos);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return base64Encoded(configPath.toAbsolutePath()
                                       .toString());
    }

    private static String base64Encoded(String value) {
        byte[] valueBytes = value.getBytes(UTF_8);
        String result = Base64.getEncoder()
                              .encodeToString(valueBytes);
        return result;
    }

    private static DescriptorProtos.FileDescriptorProto spineOptionsProto() {
        return OptionsProto.getDescriptor()
                           .toProto();
    }

    private static DescriptorProtos.FileDescriptorProto descriptorProto() {
        return DescriptorProtos.getDescriptor()
                               .toProto();
    }
}

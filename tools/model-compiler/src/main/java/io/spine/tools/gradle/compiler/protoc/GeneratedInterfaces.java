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

package io.spine.tools.gradle.compiler.protoc;

import io.spine.annotation.Internal;
import io.spine.base.EventMessage;
import io.spine.code.java.ClassName;
import io.spine.tools.protoc.AddInterfaces;
import io.spine.tools.protoc.UuidConfig;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.tools.protoc.ProtocTaskConfigs.uuidConfig;

/**
 * A configuration of interfaces to be generated for Java message classes.
 */
public final class GeneratedInterfaces extends GeneratedConfigurations<AddInterfaces> {

    private UuidConfig uuidInterface = UuidConfig.getDefaultInstance();

    public GeneratedInterfaces() {
        super();
    }

    /**
     * Configures an interface generation for messages declared in files matching a given pattern.
     *
     * <p>Sample usage is:
     * <pre>
     *     {@code
     *     mark messages().inFiles(suffix: "events.proto"), asType("my.custom.EventMessage")
     *     }
     * </pre>
     *
     * <p>The statement in the example above configures all message types declared in a file which
     * name ends with {@code events.proto} to implement the {@code my.custom.EventMessage}. It is
     * expected that {@code my.custom.EventMessage} is an interface defined by the user.
     *
     * <p>Note that only the top-level messages declarations are affected by this configuration.
     * Nested messages defined in the same file do not implement the interface.
     *
     * <p>Caution. In order for the framework components to function properly, one should not
     * entirely override the {@code default} message interfaces. Instead, provide a custom
     * interface which {@code extends} the standard one.
     *
     * Example of a safe way to override standard interfaces:
     * <pre>
     *     In my/custom/EventMessage.java:
     *     {@code
     *     package my.custom;
     *
     *     public interface EventMessage extends io.spine.base.EventMessage {
     *         // ...
     *     }
     *     }
     *
     *     In build.gradle:
     *     {@code
     *     // ...
     *
     *     modelCompiler {
     *         interfaces {
     *             mark messages().inFiles(suffix: "events.proto"), asType("my.custom.EventMessage")
     *         }
     *     }
     *     }
     * </pre>
     *
     * <p>In the example above, {@code my.custom.EventMessage} extends
     * {@link EventMessage io.spine.base.EventMessage} and thus it is safe to mark all events with
     * this interface instead of the default one.
     */
    public final void mark(PatternSelector patternSelector, @FullyQualifiedName ClassName interfaceName) {
        checkNotNull(patternSelector);
        checkNotNull(interfaceName);
        addPattern(patternSelector, interfaceName);
    }

    /**
     * Configures an interface generation for messages with a single {@code string} field called
     * {@code uuid}.
     *
     * <p>This method functions similarly to the {@link #mark(PatternSelector, ClassName)} except for
     * several differences:
     * <ul>
     *     <li>the file in which the message type is defined does not matter;
     *     <li>nested definitions are affected as well as top-level ones.
     * </ul>
     *
     * <p>Sample usage is:
     * <pre>
     *      {@code
     *      mark messages().uuid(), asType("my.custom.Identifier")
     *      }
     * </pre>
     */
    public final void mark(UuidMessage uuidMessage, ClassName interfaceName) {
        checkNotNull(uuidMessage);
        uuidInterface = uuidConfig(interfaceName);
    }

    /**
     * A syntax sugar method used for a more natural Gradle DSL.
     */
    @SuppressWarnings({"MethodMayBeStatic", "unused"}) // Gradle DSL
    public final ClassName asType(String interfaceName){
        return ClassName.of(interfaceName);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") // `Builder` API is used in `forEach` lambda.
    @Override
    @Internal
    public AddInterfaces asProtocConfig() {
        AddInterfaces.Builder result = AddInterfaces
                .newBuilder()
                .setUuidInterface(uuidInterface);
        patternConfigurations()
                .stream()
                .map(GeneratedConfigurations::toPatternConfig)
                .forEach(result::addInterfaceByPattern);
        return result.build();
    }
}

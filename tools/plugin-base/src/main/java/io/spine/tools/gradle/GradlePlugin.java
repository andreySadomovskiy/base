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

import com.google.common.base.Objects;
import io.spine.code.java.ClassName;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * An identifier of a Gradle plugin.
 *
 * <p>A plugin is represented by the Java class which implements it. The class must implement
 * the {@link Plugin org.gradle.api.Plugin} interface.
 */
public final class GradlePlugin {

    private final Class<? extends Plugin<? extends Project>> implementationClass;

    private GradlePlugin(Class<? extends Plugin<? extends Project>> pluginClass) {
        this.implementationClass = checkNotNull(pluginClass);
    }

    /**
     * Creates a new instance with the given implementation class.
     *
     * @param pluginClass
     *         the plugin implementation class
     * @return new instance
     */
    public static GradlePlugin
    implementedIn(Class<? extends Plugin<? extends Project>> pluginClass) {
        return new GradlePlugin(pluginClass);
    }

    /**
     * Obtains the implementation class of this plugin.
     */
    public Class<? extends Plugin<? extends Project>> implementationClass() {
        return implementationClass;
    }

    /**
     * Obtains the fully qualified name of the implementation class.
     */
    public ClassName className() {
        return ClassName.of(implementationClass().getCanonicalName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GradlePlugin)) {
            return false;
        }
        GradlePlugin plugin = (GradlePlugin) o;
        return Objects.equal(implementationClass, plugin.implementationClass);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(implementationClass);
    }

    @Override
    public String toString() {
        return format("Plugin %s", className());
    }
}

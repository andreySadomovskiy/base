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

package io.spine.js.generate.resolve;

import io.spine.code.proto.PackageName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PackagePattern should")
class PackagePatternTest {

    @Test
    @DisplayName("obtain package name for the non-nested format")
    void packageNameForNonNested() {
        String packageName = "original";
        PackagePattern pattern = PackagePattern.of(packageName);
        assertEquals(packageName, pattern.packageName()
                                         .value());
    }

    @Test
    @DisplayName("obtain package name for the nested format")
    void packageNameForNested() {
        PackagePattern pattern = PackagePattern.of("work.*");
        assertEquals("work", pattern.packageName()
                                    .value());
    }

    @Test
    @DisplayName("not match nested packages by default")
    void notMatchNestedPackages() {
        PackagePattern pattern = PackagePattern.of("first");
        PackageName packageName = PackageName.of("first.second");
        boolean matches = pattern.matches(packageName);
        assertFalse(matches);
    }

    @Test
    @DisplayName("match same packages")
    void matchSamePackages() {
        String name = "protos";
        PackagePattern pattern = PackagePattern.of(name);
        PackageName packageName = PackageName.of(name);
        boolean matches = pattern.matches(packageName);
        assertTrue(matches);
    }

    @Test
    @DisplayName("match nested packages if specified")
    void matchNestedPackages() {
        PackagePattern pattern = PackagePattern.of("foo.*");
        PackageName packageName = PackageName.of("foo.bar");
        boolean matches = pattern.matches(packageName);
        assertTrue(matches);
    }
}

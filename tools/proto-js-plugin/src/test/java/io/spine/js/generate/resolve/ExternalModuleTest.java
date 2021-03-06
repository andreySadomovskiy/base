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

import com.google.common.testing.EqualsTester;
import io.spine.code.fs.js.FileReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.js.generate.resolve.given.Given.newModule;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ExternalModule should")
class ExternalModuleTest {

    private static final String moduleName = "module";

    @Test
    @DisplayName("not have an empty name")
    void notHaveEmptyModuleName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> newModule("", "some/directory")
        );
    }

    @Test
    @DisplayName("be equal if module name and patterns are same")
    void equals() {
        new EqualsTester()
                .addEqualityGroup(ExternalModule.spineWeb(), ExternalModule.spineWeb())
                .addEqualityGroup(new ExternalModule("a-module", emptySet()))
                .addEqualityGroup(new ExternalModule("b-module", emptySet()))
                .testEquals();
    }

    @Test
    @DisplayName("resolve an import if the directory is same as in the pattern")
    void resolveIfDirectorySame() {
        ExternalModule module = newModule(moduleName, "d");
        FileReference origin = FileReference.of("./../../d/f.js");
        FileReference result = module.fileInModule(origin);
        FileReference expected = FileReference.of(moduleName + "/d/f.js");
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("resolve an import for a subdirectory of a pattern")
    void resolveForSubdirectoryPattern() {
        ExternalModule module = newModule(moduleName, "d/*");
        FileReference origin = FileReference.of("./../../d/d2/f.js");
        FileReference result = module.fileInModule(origin);
        FileReference expected = FileReference.of(moduleName + "/d/d2/f.js");
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("resolve an import if subdirectories match but root is absent")
    void resolveImportMatchingSubdirectories() {
        ExternalModule module = newModule(moduleName, "d/d2/d3");
        FileReference origin = FileReference.of("./../../d2/d3/f.js");
        FileReference result = module.fileInModule(origin);
        FileReference expected = FileReference.of(moduleName + "/d/d2/d3/f.js");
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("resolve an import if subdirectories match by pattern but root is absent")
    void resolveImportMatchingAnySubdirectories() {
        ExternalModule module = newModule(moduleName, "d/d2/d3/*");
        FileReference origin = FileReference.of("./../../d2/d3/d4/f.js");
        FileReference result = module.fileInModule(origin);
        FileReference expected = FileReference.of(moduleName + "/d/d2/d3/d4/f.js");
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("compose an import path only if package is provided by the module")
    void acceptOnlyProvidedProto() {
        ExternalModule module = newModule(moduleName, "should_not_match_it");
        FileReference reference = FileReference.of("d/index_pb.js");
        assertThrows(
                IllegalStateException.class,
                () -> module.fileInModule(reference)
        );
    }
}

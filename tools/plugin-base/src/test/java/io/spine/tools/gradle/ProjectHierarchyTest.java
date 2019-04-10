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

import com.google.common.testing.NullPointerTester;
import io.spine.testing.UtilityClassTest;
import io.spine.tools.gradle.testing.NoOp;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ProjectHierarchy should")
class ProjectHierarchyTest extends UtilityClassTest<ProjectHierarchy> {

    ProjectHierarchyTest() {
        super(ProjectHierarchy.class);
    }

    @Override
    protected void configure(NullPointerTester tester) {
        super.configure(tester);
        tester.setDefault(Project.class, mock(Project.class))
              .setDefault(Action.class, mock(Action.class));
    }

    @Test
    @DisplayName("traverse hierarchy in bf ordering")
    void traverseHierarchyInBfOrdering() {
        Project root = mock(Project.class);
        Project sub1 = mock(Project.class);
        Project sub2 = mock(Project.class);
        Project subsub1 = mock(Project.class);
        Project subsub2 = mock(Project.class);

        when(root.getSubprojects()).thenReturn(newHashSet(sub1, sub2));
        when(sub1.getSubprojects()).thenReturn(newHashSet(subsub1, subsub2));
        when(sub2.getSubprojects()).thenReturn(emptySet());
        when(subsub1.getSubprojects()).thenReturn(emptySet());
        when(subsub2.getSubprojects()).thenReturn(emptySet());

        when(root.getRootProject()).thenReturn(root);

        Set<Project> visited = newHashSet();
        ProjectHierarchy.applyToAll(root, project -> {
            assertFalse(visited.contains(project));
            for (Project child : project.getSubprojects()) {
                assertFalse(visited.contains(child));
            }
            visited.add(project);
        });

        assertTrue(visited.containsAll(asList(root, sub1, sub2, subsub1, subsub2)));
        assertEquals(5, visited.size());
    }

    @Test
    @DisplayName("not accept non root projects")
    void notAcceptNonRootProjects() {
        Project project = mock(Project.class);
        when(project.getRootProject()).thenReturn(mock(Project.class)); // other instance
        assertThrows(IllegalArgumentException.class,
                     () -> ProjectHierarchy.applyToAll(project, NoOp.action()));
    }

}

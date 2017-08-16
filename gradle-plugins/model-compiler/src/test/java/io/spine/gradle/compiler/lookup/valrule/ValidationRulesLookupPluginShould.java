/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
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

package io.spine.gradle.compiler.lookup.valrule;

import io.spine.gradle.compiler.GradleProject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import static io.spine.gradle.TaskName.FIND_VALIDATION_RULES;
import static io.spine.gradle.compiler.Extension.getDefaultMainGenResDir;
import static io.spine.util.Exceptions.illegalStateWithCauseOf;
import static io.spine.validate.rules.ValidationRules.getValRulesPropsFileName;
import static org.junit.Assert.assertEquals;

/**
 * @author Dmytro Grankin
 */
public class ValidationRulesLookupPluginShould {

    private static final char DOT = '.';
    private static final String PROJECT_NAME = "validation-rules-lookup-plugin-test";
    private static final String PROTO_FILE_PACKAGE = "test.valrule";
    private static final String OUTER_MESSAGE_TYPE = "Outer";
    private static final String VALIDATION_RULE_TYPE = "ValidationRule";
    private static final String VALIDATION_TARGET = PROTO_FILE_PACKAGE + DOT +
                                                    OUTER_MESSAGE_TYPE + DOT +
                                                    "field_name";
    private static final List<String> NESTED_VALIDATION_RULE_PROTO =
            Arrays.asList("syntax = \"proto3\";",
                          "package " + PROTO_FILE_PACKAGE + ';',
                          "import \"spine/options.proto\";",

                          "message " + OUTER_MESSAGE_TYPE + " {",

                                "message " + VALIDATION_RULE_TYPE + " {",
                                    "option (validation_of) = \"" + VALIDATION_TARGET + "\";",
                                "}",
                          "}"
            );

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    @Test
    public void findNestedValidationRules() throws Exception {
        final String file = "nested_validation_rule.proto";
        final GradleProject project = newProjectWithFile(file, NESTED_VALIDATION_RULE_PROTO);
        project.executeTask(FIND_VALIDATION_RULES);

        final String expectedKey = PROTO_FILE_PACKAGE + DOT +
                                   OUTER_MESSAGE_TYPE + DOT +
                                   VALIDATION_RULE_TYPE;
        final String value = (String) getProperties().get(expectedKey);
        assertEquals(VALIDATION_TARGET, value);
    }

    private Dictionary getProperties() {
        final String projectPath = testProjectDir.getRoot()
                                                 .getAbsolutePath();
        final Path path = Paths.get(projectPath, getDefaultMainGenResDir(),
                                    getValRulesPropsFileName());
        try {
            final InputStream inputStream = new FileInputStream(path.toFile());
            final Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw illegalStateWithCauseOf(e);
        }
    }

    private GradleProject newProjectWithFile(String protoFileName, List<String> protoFileLines) {
        return GradleProject.newBuilder()
                            .setProjectName(PROJECT_NAME)
                            .setProjectFolder(testProjectDir)
                            .createProto(protoFileName, protoFileLines)
                            .build();
    }
}
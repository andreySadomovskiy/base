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

import com.google.common.truth.DefaultSubject;
import com.google.common.truth.Subject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("FilePattern should")
final class FilePatternTest {

    @DisplayName("ensure that implementations differ")
    @Test
    void implementationsDiffer() {
        String pattern = "testPattern";

        Subject<DefaultSubject, Object> prefix = assertThat(new PrefixSelector(pattern));
        prefix.isNotEqualTo(new SuffixSelector(pattern));
        prefix.isNotEqualTo(new RegexSelector(pattern));

        Subject<DefaultSubject, Object> suffix = assertThat(new SuffixSelector(pattern));
        suffix.isNotEqualTo(new PrefixSelector(pattern));
        suffix.isNotEqualTo(new RegexSelector(pattern));

        Subject<DefaultSubject, Object> regex = assertThat(new RegexSelector(pattern));
        regex.isNotEqualTo(new SuffixSelector(pattern));
        regex.isNotEqualTo(new PrefixSelector(pattern));
    }
}

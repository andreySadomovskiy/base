/*
 * Copyright 2018, TeamDev. All rights reserved.
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

package io.spine.tools.codestyle.javadoc;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Provides regular expressions for finding patterns in Javadoc.
 *
 * @author Alexander Aleksandrov
 */
enum JavadocPattern {

    /**
     * This regexp matches every link or linkplain in Javadoc that is not in the format of
     * {@link <FQN> <text>} or {@linkplain <FQN> <text>}.
     *
     * <p>Wrong links:
     * <pre>
     *     {@link java.util.regex.Pattern} or {@linkplain java.util.regex.Pattern}
     * </pre>
     *
     * <p>Correct links:
     * <pre>
     *     {@link JavadocPattern(Pattern)}
     *     {@link io.spine.base.Identifier Identifier}
     *     {@linkplain io.spine.base.Identifier an identifier}
     * </pre>
     */
    LINK(compile(

        /* 1st Capturing Group "(\{@link|\{@linkplain)"
         *
         * 1st Alternative "\{@link"
         * "\{" matches the character "{" literally (case sensitive)
         * "@link" matches the characters "@link" literally (case sensitive)
         * 2nd Alternative "\{@linkplain"
         * "\{" matches the character "{" literally (case sensitive)
         * "@linkplain" matches the characters "@linkplain" literally (case sensitive)
         * " *" matches the character " " literally (case sensitive)
         * "*" Quantifier — Matches between zero and unlimited times, as many times as possible,
         * giving back as needed (greedy).
         */
        "(\\{@link|\\{@linkplain) *" +

        /*
         * 2nd Capturing Group "((?!-)[a-z0-9-]{1,63}\.)"
         *
         * Negative Lookahead "(?!-)"
         * Assert that the Regex below does not match
         * "-"matches the character "-" literally (case sensitive)
         * Match a single character present in the list below "[a-z0-9-]{1,63}"
         * "{1,63}" Quantifier — Matches between 1 and 63 times, as many times as possible,
         * giving back as needed (greedy)
         * "a-z" a single character in the range between "a" (ASCII 97) and "z" (ASCII 122)
         * (case sensitive)
         * "0-9" a single character in the range between "0" (ASCII 48) and "9" (ASCII 57)
         * (case sensitive)
         * "-" matches the character "-" literally (case sensitive)
         * "\." matches the character "." literally (case sensitive).
         */
        "((?!-)[a-z0-9-]{1,63}\\.)" +

        /*
         * 3rd Capturing Group "((?!-)[a-zA-Z0-9-]{1,63}[a-zA-Z0-9-]\.)+"
         *
         * "+" Quantifier — Matches between one and unlimited times, as many times as possible,
         * giving back as needed (greedy)
         * A repeated capturing group will only capture the last iteration.
         * Put a capturing group around the repeated group to capture all iterations or use a
         * non-capturing group instead if you're not interested in the data.
         */
        "((?!-)[a-zA-Z0-9-]{1,63}[a-zA-Z0-9-]\\.)" +

        /*
         * 4th Capturing Group "(\}|\ *\})"
         * 1st Alternative "\}"
         * "}" matches the character "}" literally (case sensitive)
         * 2nd Alternative "\ *\}"
         * " *" matches the character " " literally (case sensitive)
         *  "*" Quantifier — Matches between zero and unlimited times, as many times as possible,
         *  giving back as needed (greedy)
         * "}" matches the character "}" literally (case sensitive).
         */
        "+[a-zA-Z]{2,63}(}| *})")
    );

    private final Pattern pattern;

    JavadocPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    Pattern getPattern() {
        return pattern;
    }
}
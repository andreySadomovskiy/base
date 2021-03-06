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

package io.spine.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for checking preconditions.
 */
public final class Preconditions2 {

    /** Prevents instantiation of this utility class. */
    private Preconditions2() {
    }

    /**
     * Ensures that the passed string is not {@code null}, empty or blank string.
     *
     * @param stringToCheck the string to check
     * @return the passed string
     * @throws NullPointerException if the passed string is {@code null}
     * @throws IllegalArgumentException if the string is empty or blank
     */
    @CanIgnoreReturnValue
    public static String checkNotEmptyOrBlank(String stringToCheck) {
        return checkNotEmptyOrBlank(stringToCheck, "");
    }

    /**
     * Ensures that the passed string is not {@code null}, empty or blank string.
     *
     * @param stringToCheck the string to check
     * @return the passed string
     * @throws NullPointerException if the passed string is {@code null}
     * @throws IllegalArgumentException if the string is empty or blank
     */
    @CanIgnoreReturnValue
    public static String checkNotEmptyOrBlank(String stringToCheck, String message) {
        checkNotNull(stringToCheck);
        checkNotNull(message);
        checkArgument(!stringToCheck.isEmpty(), message);
        String trimmed = stringToCheck.trim();
        checkArgument(trimmed.length() > 0, message);
        return stringToCheck;
    }
}

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

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Preconditions2.checkNotEmptyOrBlank;

/**
 * A pattern to match a Protobuf package.
 */
public final class PackagePattern {

    private static final String INCLUDE_NESTED_PATTERN_ENDING = ".*";

    private final PackageName packageName;
    private final boolean includeNested;

    private PackagePattern(PackageName packageName, boolean includeNested) {
        checkNotNull(packageName);
        this.packageName = packageName;
        this.includeNested = includeNested;
    }

    /**
     * Creates a new instance.
     *
     * <p>The following formats are supported:
     * <ul>
     *     <li>The exact package — {@code foo.bar}.
     *     <li>The exact package and nested packages — {@code foo.bar.*}.
     * </ul>
     *
     * @param value
     *         the value of the pattern
     * @return a new instance
     */
    @SuppressWarnings("ResultOfMethodCallIgnored" /* The result can be ignored. */)
    public static PackagePattern of(String value) {
        checkNotEmptyOrBlank(value);
        boolean includeNested = value.endsWith(INCLUDE_NESTED_PATTERN_ENDING);
        PackageName packageName;
        if (includeNested) {
            int packageNameEnd = value.length() - INCLUDE_NESTED_PATTERN_ENDING.length();
            packageName = PackageName.of(value.substring(0, packageNameEnd));
        } else {
            packageName = PackageName.of(value);
        }
        return new PackagePattern(packageName, includeNested);
    }

    /**
     * Checks if the pattern matches the specified package name.
     */
    boolean matches(PackageName targetPackage) {
        boolean result = includeNested
                         ? targetPackage.isNestedIn(packageName)
                         : packageName.equals(targetPackage);
        return result;
    }

    /**
     * Obtains the package name used in the pattern.
     */
    PackageName packageName() {
        return packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PackagePattern)) {
            return false;
        }
        PackagePattern pattern = (PackagePattern) o;
        return includeNested == pattern.includeNested &&
                packageName.equals(pattern.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, includeNested);
    }
}

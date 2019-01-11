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

package io.spine.code.java;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import io.spine.value.StringTypeValue;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * A name of a potentially nested class with outer class names separated with dots.
 *
 * <p>A top level class name would have equal to {@link SimpleClassName}.
 */
public final class NestedClassName extends StringTypeValue {

    private static final long serialVersionUID = 0L;

    private static final Splitter nameSplitter = Splitter.on('.')
                                                         .omitEmptyStrings();

    private NestedClassName(String value) {
        super(value);
    }

    /**
     * Creates a new instance by fully-qualified name.
     */
    static NestedClassName create(ClassName className) {
        String nameWithOuter = ClassName.afterDot(className.value());
        String dotted = ClassName.toDotted(nameWithOuter);
        return new NestedClassName(dotted);
    }

    public ImmutableList<SimpleClassName> pathToNested() {
        String fullName = value();
        ImmutableList<SimpleClassName> result =
                nameSplitter.splitToList(fullName)
                            .stream()
                            .map(SimpleClassName::create)
                            .collect(toImmutableList());
        return result;
    }
}

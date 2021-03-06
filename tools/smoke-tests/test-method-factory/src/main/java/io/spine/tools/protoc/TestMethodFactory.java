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

package io.spine.tools.protoc;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.MethodSpec;
import io.spine.tools.protoc.method.GeneratedMethod;
import io.spine.tools.protoc.method.MethodFactory;
import io.spine.type.MessageType;
import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.lang.model.element.Modifier;
import java.util.List;

@Immutable
public final class TestMethodFactory implements MethodFactory {

    @Override
    public List<GeneratedMethod> createFor(MessageType messageType) {
        MethodSpec spec = MethodSpec
                .methodBuilder("ownType")
                .returns(MessageType.class)
                .addStatement("return new $T(getDescriptor())", MessageType.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addJavadoc("Returns {@link $T MessageType} of the current message.\n",
                            MessageType.class)
                .build();
        return ImmutableList.of(new GeneratedMethod(spec.toString()));
    }
}

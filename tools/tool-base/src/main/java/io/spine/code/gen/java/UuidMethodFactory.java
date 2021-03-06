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

package io.spine.code.gen.java;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import io.spine.code.java.PackageName;
import io.spine.code.java.SimpleClassName;
import io.spine.tools.protoc.method.GeneratedMethod;
import io.spine.tools.protoc.method.MethodFactory;
import io.spine.type.MessageType;
import io.spine.util.Exceptions;
import io.spine.util.Preconditions2;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link MethodFactory} used by default to create new static helper methods for
 * {@link io.spine.base.UuidValue UuidValue} messages.
 *
 * <p>Creates following methods:
 * <ul>
 *     <li>{@code public static final T generate()} — generates a new instance of the
 *     {@code UuidValue} message with a random {@code uuid} value.
 *     <li>{@code public static final T of(String uuid} — create a new instance of the
 *     {@code UuidValue} message with a supplied {@code uuid} value.
 * </ul>
 */
@Immutable
public final class UuidMethodFactory implements MethodFactory {

    private static final String INVALID_STRING_MESSAGE = "Invalid UUID string: %s";

    @Override
    public List<GeneratedMethod> createFor(MessageType messageType) {
        checkNotNull(messageType);
        if (!messageType.isUuidValue()) {
            return ImmutableList.of();
        }
        PackageName packageName = messageType.javaPackage();
        SimpleClassName simpleClassName = messageType.simpleJavaClassName();
        ClassName self = ClassName.get(packageName.value(), simpleClassName.value());
        return ImmutableList.of(newGenerateMethodSpec(self), newOfMethodSpec(self));
    }

    /**
     * Creates a new {@code public static final T of(String uuid)} method:
     * <pre>
     *     {@code
     *     public static final T of(String uuid){
     *         Preconditions2.checkNotEmptyOrBlank(uuid);
     *         try {
     *             UUID.fromString(uuid);
     *         }
     *         catch(NumberFormatException e) {
     *             throw Exceptions.newIllegalArgumentException(e, INVALID_STRING_MESSAGE, uuid);
     *         }
     *         return newBuilder().setUuid(uuid).build();
     *     }
     *     }
     * </pre>
     */
    private static GeneratedMethod newOfMethodSpec(ClassName self) {
        ParameterSpec uuidParameter = ParameterSpec
                .builder(String.class, "uuid")
                .build();
        MethodSpec spec = MethodSpec
                .methodBuilder("of")
                .returns(self)
                .addParameter(uuidParameter)
                .addStatement("$T.checkNotEmptyOrBlank($N)", Preconditions2.class, uuidParameter)
                .beginControlFlow("try")
                .addStatement("$T.fromString($N)", UUID.class, uuidParameter)
                .nextControlFlow("catch($T e)", NumberFormatException.class)
                .addStatement("throw $T.newIllegalArgumentException(e, $S, $N)",
                              Exceptions.class, INVALID_STRING_MESSAGE, uuidParameter)
                .endControlFlow()
                .addStatement("return newBuilder().setUuid($N).build()", uuidParameter)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addJavadoc("Creates a new instance from the passed value.\n")
                .addJavadoc("@throws $T if the passed value is not a valid UUID string\n",
                            IllegalArgumentException.class)
                .build();
        return new GeneratedMethod(spec.toString());
    }

    /**
     * Creates new {@code public static final @NonNull T generate()} method:
     * <pre>
     *      {@code
     *      public static final T generate(){
     *          return newBuilder().setUuid(UUID.randomUUID().toString()).build();
     *      }
     *      }
     * </pre>
     */
    private static GeneratedMethod newGenerateMethodSpec(ClassName self) {
        MethodSpec spec = MethodSpec
                .methodBuilder("generate")
                .returns(self)
                .addStatement("return newBuilder().setUuid($T.randomUUID().toString()).build()",
                              UUID.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addJavadoc("Creates a new instance with a random UUID value.\n")
                .addJavadoc("@see $T#randomUUID\n", UUID.class)
                .build();
        return new GeneratedMethod(spec.toString());
    }
}

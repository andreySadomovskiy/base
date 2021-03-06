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

package io.spine.tools.compiler.field.type;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import io.spine.code.java.PrimitiveType;
import io.spine.code.proto.FieldDeclaration;
import io.spine.tools.compiler.field.AccessorTemplate;

import java.util.List;
import java.util.Optional;

import static io.spine.tools.compiler.field.AccessorTemplates.adder;
import static io.spine.tools.compiler.field.AccessorTemplates.allAdder;
import static io.spine.tools.compiler.field.AccessorTemplates.clearer;
import static io.spine.tools.compiler.field.AccessorTemplates.countGetter;
import static io.spine.tools.compiler.field.AccessorTemplates.getter;
import static io.spine.tools.compiler.field.AccessorTemplates.listGetter;
import static io.spine.tools.compiler.field.AccessorTemplates.setter;

/**
 * Represents repeated {@linkplain FieldType field type}.
 */
public final class RepeatedFieldType implements FieldType {

    private static final ImmutableSet<AccessorTemplate> GENERATED_ACCESSORS =
            ImmutableSet.of(
                    getter(),
                    listGetter(),
                    countGetter(),
                    setter(),
                    adder(),
                    allAdder(),
                    clearer()
            );

    private final TypeName typeName;

    /**
     * Constructs a new instance based on component type.
     *
     * @param declaration
     *         the declaration of the field
     */
    RepeatedFieldType(FieldDeclaration declaration) {
        this.typeName = constructTypeNameFor(declaration.javaTypeName());
    }

    @Override
    public TypeName getTypeName() {
        return typeName;
    }

    @Override
    public ImmutableSet<AccessorTemplate> generatedAccessorTemplates() {
        return GENERATED_ACCESSORS;
    }

    /**
     * Returns "addAll" setter prefix, used to initialize a repeated field using with a call to
     * Protobuf message builder.
     */
    @Override
    public AccessorTemplate primarySetterTemplate() {
        return allAdder();
    }

    private static TypeName constructTypeNameFor(String componentTypeName) {
        Optional<? extends Class<?>> wrapperClass =
                PrimitiveType.getWrapperClass(componentTypeName);

        TypeName componentType = wrapperClass.isPresent()
                                 ? TypeName.get(wrapperClass.get())
                                 : ClassName.bestGuess(componentTypeName);
        ParameterizedTypeName result =
                ParameterizedTypeName.get(ClassName.get(List.class), componentType);
        return result;
    }

    @Override
    public String toString() {
        return typeName.toString();
    }
}

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

package io.spine.tools.compiler.validation;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import io.spine.base.ConversionException;
import io.spine.code.proto.FieldDeclaration;
import io.spine.code.proto.FieldName;
import io.spine.logging.Logging;
import io.spine.tools.compiler.field.type.FieldType;
import io.spine.validate.ValidationException;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.tools.compiler.validation.Methods.clearPrefix;
import static io.spine.tools.compiler.validation.Methods.clearProperty;
import static io.spine.tools.compiler.validation.Methods.getMessageBuilder;
import static io.spine.tools.compiler.validation.Methods.rawSuffix;
import static io.spine.tools.compiler.validation.Methods.removePrefix;
import static io.spine.tools.compiler.validation.Methods.returnThis;
import static java.lang.String.format;

/**
 * A method constructor of the {@code MethodSpec} objects based on the Protobuf message declaration.
 *
 * <p>Constructs the {@code MethodSpec} objects for the repeated fields.
 */
final class RepeatedFieldMethods extends AbstractMethodGroup implements Logging {

    @SuppressWarnings("DuplicateStringLiteralInspection")
    // It cannot be used as the constant across the project.
    // Although it has the equivalent literal they have the different meaning.
    private static final String VALUE = "value";
    @SuppressWarnings("DuplicateStringLiteralInspection")
    // It cannot be used as the constant across the project.
    // Although it has the equivalent literal they have the different meaning.
    private static final String INDEX = "index";

    private static final String ADD_PREFIX = "add";
    private static final String SET_PREFIX = "set";
    private static final String ADD_RAW_PREFIX = "addRaw";
    private static final String SET_RAW_PREFIX = "setRaw";
    private static final String CONVERTED_VALUE = "convertedValue";

    private static final String ADD_ALL_METHOD = ".addAll%s(%s)";

    private final FieldType fieldType;
    private final String javaFieldName;
    private final String methodNamePart;
    private final ClassName listElementClassName;
    private final FieldDescriptor field;
    private final boolean isScalarOrEnum;

    /**
     * Creates a new builder for the {@code RepeatedFieldMethodConstructor} class.
     *
     * @return created builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Constructs the {@code RepeatedFieldMethodConstructor}.
     *
     * @param builder the {@code RepeatedFieldMethodConstructorBuilder} instance
     */
    private RepeatedFieldMethods(Builder builder) {
        super(builder);
        this.fieldType = checkNotNull(builder.getFieldType());
        this.field = checkNotNull(builder.getField());
        FieldDescriptorProto fdescr = field.toProto();
        FieldName fieldName = FieldName.of(fdescr);
        this.javaFieldName = fieldName.javaCase();
        this.methodNamePart = fieldName.toCamelCase();
        FieldDeclaration fieldDecl = new FieldDeclaration(field);
        String fieldJavaClass = fieldDecl.javaTypeName();
        this.listElementClassName = ClassName.bestGuess(fieldJavaClass);
        this.isScalarOrEnum = fieldDecl.isScalar() || fieldDecl.isEnum();
    }

    @Override
    public Collection<MethodSpec> generate() {
        _debug("The methods construction for the {} repeated field is started.", javaFieldName);
        ImmutableList.Builder<MethodSpec> methods = methods()
                .add(getter())
                .addAll(repeatedMethods())
                .addAll(repeatedRawMethods());
        _debug("The methods construction for the {} repeated field is finished.", javaFieldName);
        return methods.build();
    }

    private MethodSpec getter() {
        _debug("The getter construction for the repeated field is started.");

        String methodName = "get" + methodNamePart;
        ClassName rawType = ClassName.get(List.class);
        ParameterizedTypeName returnType = ParameterizedTypeName.get(rawType, listElementClassName);
        String returnStatement = format("return %s.get%sList()",
                                        getMessageBuilder(), methodNamePart);
        MethodSpec methodSpec = MethodSpec
                .methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addStatement(returnStatement)
                .build();

        _debug("The getter construction for the repeated field is finished.");
        return methodSpec;
    }

    private Collection<MethodSpec> repeatedRawMethods() {
        _debug("The raw methods construction for the repeated field is is started.");
        ImmutableList.Builder<MethodSpec> methods = methods()
                .add(rawAddObjectMethod())
                .add(rawSetObjectByIndexMethod())
                .add(rawAddAllMethod());
        // Some methods are not available in Protobuf Message.Builder for scalar types.
        if (!isScalarOrEnum) {
            methods.add(createRawAddObjectByIndexMethod());
        }

        _debug("The raw methods construction for the repeated field is is finished.");
        return methods.build();
    }

    private Collection<MethodSpec> repeatedMethods() {
        ImmutableList.Builder<MethodSpec> methods = methods()
                .add(clearMethod())
                .add(addObjectMethod())
                .add(setObjectByIndexMethod())
                .add(addAllMethod());

        // Some methods are not available in Protobuf Message.Builder for scalar types and enums.
        if (!isScalarOrEnum) {
            methods.add(addObjectByIndexMethod())
                   .add(removeObjectByIndexMethod());
        }
        return methods.build();
    }

    private MethodSpec rawAddObjectMethod() {
        String methodName = ADD_RAW_PREFIX + methodNamePart;
        String addValueStatement = getMessageBuilder() + '.'
                + ADD_PREFIX + methodNamePart + "(convertedValue)";
        MethodSpec result = newBuilderSetter(methodName)
                .addParameter(String.class, VALUE)
                .addException(ValidationException.class)
                .addException(ConversionException.class)
                .addStatement(ConvertStatement.of(VALUE, listElementClassName)
                                              .value())
                .addStatement(descriptorDeclaration())
                .addStatement(validateStatement(CONVERTED_VALUE, field.getName()))
                .addStatement(addValueStatement)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private MethodSpec createRawAddObjectByIndexMethod() {
        MethodSpec result = modifyCollectionByIndexWithRaw(ADD_RAW_PREFIX, ADD_PREFIX);
        return result;
    }

    private MethodSpec rawSetObjectByIndexMethod() {
        return modifyCollectionByIndexWithRaw(SET_RAW_PREFIX, SET_PREFIX);
    }

    private MethodSpec modifyCollectionByIndexWithRaw(String methodNamePrefix,
                                                      String realBuilderCallPrefix) {
        String methodName = methodNamePrefix + methodNamePart;
        String modificationStatement =
                format("%s.%s%s(%s, convertedValue)",
                       getMessageBuilder(), realBuilderCallPrefix, methodNamePart, INDEX);
        MethodSpec result = newBuilderSetter(methodName)
                .addParameter(TypeName.INT, INDEX)
                .addParameter(String.class, VALUE)
                .addException(ValidationException.class)
                .addException(ConversionException.class)
                .addStatement(ConvertStatement.of(VALUE, listElementClassName)
                                              .value())
                .addStatement(descriptorDeclaration())
                .addStatement(validateStatement(CONVERTED_VALUE, field.getName()))
                .addStatement(modificationStatement)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private MethodSpec rawAddAllMethod() {
        String methodName = fieldType.getSetterPrefix() + rawSuffix() + methodNamePart;
        String addAllValues = getMessageBuilder()
                + format(ADD_ALL_METHOD, methodNamePart, CONVERTED_VALUE);
        MethodSpec result = newBuilderSetter(methodName)
                .addParameter(String.class, VALUE)
                .addException(ValidationException.class)
                .addException(ConversionException.class)
                .addStatement(createGetConvertedCollectionValue(),
                              List.class,
                              listElementClassName,
                              listElementClassName)
                .addStatement(descriptorDeclaration())
                .addStatement(validateStatement(CONVERTED_VALUE, field.getName()))
                .addStatement(addAllValues)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private MethodSpec addAllMethod() {
        String methodName = fieldType.getSetterPrefix() + methodNamePart;
        ClassName rawType = ClassName.get(List.class);
        ParameterizedTypeName parameter = ParameterizedTypeName.get(rawType, listElementClassName);
        String fieldName = field.getName();
        String addAllValues = getMessageBuilder()
                + format(ADD_ALL_METHOD, methodNamePart, VALUE);
        MethodSpec result = newBuilderSetter(methodName)
                .addParameter(parameter, VALUE)
                .addException(ValidationException.class)
                .addStatement(descriptorDeclaration())
                .addStatement(validateStatement(VALUE, fieldName))
                .addStatement(addAllValues)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private MethodSpec addObjectMethod() {
        String methodName = ADD_PREFIX + methodNamePart;
        String addValue = format("%s.%s%s(%s)",
                                 getMessageBuilder(), ADD_PREFIX, methodNamePart, VALUE);
        String descriptorDeclaration = descriptorDeclaration();
        MethodSpec result = newBuilderSetter(methodName)
                .addParameter(listElementClassName, VALUE)
                .addException(ValidationException.class)
                .addStatement(descriptorDeclaration)
                .addStatement(validateStatement(VALUE, javaFieldName))
                .addStatement(addValue)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private MethodSpec addObjectByIndexMethod() {
        return modifyCollectionByIndex(ADD_PREFIX);
    }

    private MethodSpec setObjectByIndexMethod() {
        return modifyCollectionByIndex(SET_PREFIX);
    }

    private MethodSpec removeObjectByIndexMethod() {
        String methodName = removePrefix() + methodNamePart;
        String addValue = format("%s.%s%s(%s)", getMessageBuilder(),
                                 removePrefix(), methodNamePart, INDEX);
        MethodSpec result = newBuilderSetter(methodName)
                .addParameter(TypeName.INT, INDEX)
                .addStatement(addValue)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private MethodSpec modifyCollectionByIndex(String methodPrefix) {
        String methodName = methodPrefix + methodNamePart;
        String modificationStatement = format("%s.%s%s(%s, %s)", getMessageBuilder(),
                                              methodPrefix, methodNamePart, INDEX, VALUE);
        MethodSpec result = newBuilderSetter(methodName)
                .addParameter(TypeName.INT, INDEX)
                .addParameter(listElementClassName, VALUE)
                .addException(ValidationException.class)
                .addStatement(descriptorDeclaration())
                .addStatement(validateStatement(VALUE, javaFieldName))
                .addStatement(modificationStatement)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private MethodSpec clearMethod() {
        String clearField = getMessageBuilder() + clearProperty(methodNamePart);
        String methodName = clearPrefix() + methodNamePart;
        MethodSpec result = newBuilderSetter(methodName)
                .addStatement(clearField)
                .addStatement(returnThis())
                .build();
        return result;
    }

    private static String createGetConvertedCollectionValue() {
        String result = "final $T<$T> convertedValue = convertToList(value, $T.class)";
        return result;
    }

    /**
     * A builder for the {@code RepeatedFieldMethodConstructor} class.
     */
    static class Builder extends AbstractMethodGroupBuilder<RepeatedFieldMethods> {

        @Override
        RepeatedFieldMethods build() {
            checkFields();
            return new RepeatedFieldMethods(this);
        }
    }
}
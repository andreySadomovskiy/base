/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
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

package io.spine.type;

import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import io.spine.annotation.Internal;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Exceptions.newIllegalStateException;

/**
 * A map which contains all Protobuf types known to the application.
 *
 * @author Mikhail Mikhaylov
 * @author Alexander Yevsyukov
 * @author Alexander Litus
 */
@Internal
public class KnownTypes {

    /**
     * A map from Protobuf type URL to Java class name.
     *
     * <p>For example, for a key {@code type.spine.io/spine.base.EventId},
     * there will be the value {@code EventId}.
     */
    private static final BiMap<TypeUrl, ClassName> knownTypes = Loader.load();

    /**
     * A map from Protobuf type name to type URL.
     *
     * <p>For example, for a key {@code spine.base.EventId},
     * there will be the value {@code type.spine.io/spine.base.EventId}.
     *
     * @see TypeUrl
     */
    private static final ImmutableMap<String, TypeUrl> typeUrls = buildTypeToUrlMap(knownTypes);

    /** Prevents instantiation of this utility class. */
    private KnownTypes() {
    }

    /**
     * Retrieves Protobuf type URLs known to the application.
     */
    public static Set<TypeUrl> getAllUrls() {
        final Set<TypeUrl> result = knownTypes.keySet();
        return ImmutableSet.copyOf(result);
    }

    /**
     * Retrieves a Java class name generated for the Protobuf type by its type url
     * to be used to parse {@link com.google.protobuf.Message Message} from {@link Any}.
     *
     * @param typeUrl {@link Any} type url
     * @return Java class name
     * @throws UnknownTypeException if there is no such type known to the application
     */
    public static ClassName getClassName(TypeUrl typeUrl) throws UnknownTypeException {
        if (!knownTypes.containsKey(typeUrl)) {
            throw new UnknownTypeException(typeUrl.getTypeName());
        }
        final ClassName result = knownTypes.get(typeUrl);
        return result;
    }

    /**
     * Returns the Protobuf name for the class with the given name.
     *
     * @param className the name of the Java class for which to get Protobuf type
     * @return a Protobuf type name
     * @throws IllegalStateException if there is no Protobuf type for the specified class
     */
    public static TypeUrl getTypeUrl(ClassName className) {
        final TypeUrl result = knownTypes.inverse()
                                         .get(className);
        if (result == null) {
            throw newIllegalStateException("No Protobuf type URL found for the Java class %s",
                                            className);
        }
        return result;
    }

    /** Returns a Protobuf type URL by Protobuf type name. */
    @Nullable
    static TypeUrl getTypeUrl(String typeName) {
        final TypeUrl typeUrl = typeUrls.get(typeName);
        return typeUrl;
    }

    /**
     * Retrieves all the types that belong to the given package or its subpackages.
     *
     * @param packageName proto package name
     * @return set of {@link TypeUrl TypeUrl}s of types that belong to the given package
     */
    public static Set<TypeUrl> getAllFromPackage(final String packageName) {
        final Collection<TypeUrl> knownTypeUrls = knownTypes.keySet();
        final Collection<TypeUrl> resultCollection = Collections2.filter(
                knownTypeUrls, new Predicate<TypeUrl>() {
            @Override
            public boolean apply(@Nullable TypeUrl input) {
                if (input == null) {
                    return false;
                }

                final String typeName = input.getTypeName();
                final boolean inPackage = typeName.startsWith(packageName)
                        && typeName.charAt(packageName.length()) == TypeName.PACKAGE_SEPARATOR;
                return inPackage;
            }
        });

        final Set<TypeUrl> resultSet = ImmutableSet.copyOf(resultCollection);
        return resultSet;
    }

    private static ImmutableMap<String, TypeUrl> buildTypeToUrlMap(BiMap<TypeUrl,
                                                                   ClassName> knownTypes) {
        final ImmutableMap.Builder<String, TypeUrl> builder = ImmutableMap.builder();
        for (TypeUrl typeUrl : knownTypes.keySet()) {
            builder.put(typeUrl.getTypeName(), typeUrl);
        }
        return builder.build();
    }

    /**
     * Obtains a Java class for the passed type URL.
     *
     * @throws UnknownTypeException if there is no Java class for the passed type URL
     */
    static <T extends Message> Class<T> getJavaClass(TypeUrl typeUrl) throws UnknownTypeException {
        checkNotNull(typeUrl);
        final ClassName className = getClassName(typeUrl);
        try {
            @SuppressWarnings("unchecked") // the client considers this message is of this class
            final Class<T> result = (Class<T>) Class.forName(className.value());
            return result;
        } catch (ClassNotFoundException e) {
            throw new UnknownTypeException(typeUrl.getTypeName(), e);
        }
    }
}

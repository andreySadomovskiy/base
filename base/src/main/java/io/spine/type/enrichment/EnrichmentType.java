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

package io.spine.type.enrichment;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import io.spine.type.KnownTypes;
import io.spine.type.MessageType;
import io.spine.type.ref.TypeRef;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * An enrichment type is a message which is added to a context of another message
 * to extends its information for the benefit of message handlers.
 *
 * <p>An enrichment message declaration has:
 * <ol>
 *   <li>The {@link io.spine.option.OptionsProto#enrichmentFor (enrichment_for)} option with a
 *       {@linkplain TypeRef reference} to one or more message types this message enriches.
 *   <li>One or more fields with the {@link io.spine.option.OptionsProto#by (by)} option with
 *       the value {@linkplain FieldRef referencing} one ore more fields of source message types.
 * </ol>
 *
 * <p>If one of the above is missing, the enrichment declaration is not valid.
 */
public final class EnrichmentType extends MessageType {

    private final ImmutableList<TypeRef> sourceTypeRefs;
    private final ImmutableSet<MessageType> sourceTypes;
    private final ImmutableList<FieldDef> fields;
    private final ImmutableMap<MessageType, FieldMatch> fieldMatches;

    /**
     * Verifies if the passed message type has the {@code (enrichment_for)} option, and as such
     * is a candidate for being a valid enrichment type.
     *
     * @see EnrichmentType#test(MessageType)
     */
    public static boolean test(Descriptor type) {
        checkNotNull(type);
        List<String> sourceRefs = EnrichmentForOption.parse(type.toProto());
        boolean result = !sourceRefs.isEmpty();
        return result;
    }

    /**
     * Verifies if the passed message type has the {@code (enrichment_for)} option, and as such
     * is a candidate for being a valid enrichment type.
     *
     * @see EnrichmentType#test(com.google.protobuf.Descriptors.Descriptor)
     */
    public static boolean test(MessageType type) {
        checkNotNull(type);
        boolean result = test(type.descriptor());
        return result;
    }

    /**
     * Creates new instance by the passed enrichment type descriptor descriptor.
     */
    public EnrichmentType(Descriptor type) {
        super(type);
        /*
          This constructor implementation relies on the order of field initialization.
          This approach is selected to minimize the number of parameters passed between the methods
          that are used only in construction. Reordering may break the construction, please note
          if you plan to refactor or extend the construction of this class.
        */
        this.sourceTypeRefs = parseSourceRefs();
        this.fields = parseFieldDefs();
        this.sourceTypes = collectSources();
        this.fieldMatches = collectFieldMatches();
    }

    /**
     * Obtains type references to the enrichable messages.
     */
    private ImmutableList<TypeRef> parseSourceRefs() {
        Descriptor type = descriptor();
        Collection<TypeRef> parsedReferences = EnrichmentForOption.typeRefsFrom(type);
        return ImmutableList.copyOf(parsedReferences);
    }

    /**
     * Obtains all known source types for defined type references.
     */
    private ImmutableSet<MessageType> collectSources() {
        return KnownTypes.instance()
                         .asTypeSet()
                         .messageTypes()
                         .stream()
                         .filter(m -> isSource(m.descriptor()))
                         .collect(toImmutableSet());
    }

    /**
     * Verifies if the passed type is the source for this enrichment type.
     */
    private boolean isSource(Descriptor message) {
        if (!matchesSourceRef(message)) {
            return false;
        }
        boolean result = matchesFieldRef(message);
        return result;
    }

    private boolean matchesSourceRef(Descriptor message) {
        boolean result = sourceTypeRefs.stream()
                                       .anyMatch(r -> r.test(message));
        return result;
    }

    private boolean matchesFieldRef(Descriptor message) {
        boolean result = fields.stream()
                               .anyMatch(f -> f.matchesType(message));
        return result;
    }

    /**
     * Obtains sources for creating fields.
     */
    private ImmutableList<FieldDef> parseFieldDefs() {
        Descriptor type = descriptor();
        ImmutableList<FieldDef> result =
                type.getFields()
                    .stream()
                    .map(FieldDef::new)
                    .collect(toImmutableList());
        return result;
    }

    /**
     * For each source message type obtain a field which serves as an input for creating this
     * enrichment type.
     */
    private ImmutableMap<MessageType, FieldMatch> collectFieldMatches() {
        ImmutableMap<MessageType, FieldMatch> result =
                Maps.toMap(sourceTypes, t -> new FieldMatch(checkNotNull(t), this, fields));
        return result;
    }

    /**
     * Obtains all known message types for which this type can serve as an enrichment.
     *
     * @see #sourceClasses()
     */
    public ImmutableSet<MessageType> sourceTypes() {
        return sourceTypes;
    }

    @VisibleForTesting
    FieldMatch sourceFieldsOf(MessageType source) {
        FieldMatch result = fieldMatches.get(source);
        checkNotNull(result, "Unable to find field match for the source type `%s`", source);
        return result;
    }

    /**
     * Obtains all classes of messages for which this type can serve as an enrichment.
     *
     * @see #sourceTypes()
     */
    public ImmutableList<? extends Class<? extends Message>> sourceClasses() {
        ImmutableList<? extends Class<? extends Message>> result =
                sourceTypes().stream()
                             .map(MessageType::javaClass)
                             .collect(toImmutableList());
        return result;
    }

    public void validate() {
        Inspector inspector = new Inspector(this);
        inspector.check();
    }

    /**
     * Validates an enrichment type providing diagnostics on errors in declarations if
     * the type is invalid.
     */
    private static final class Inspector {

        private final EnrichmentType type;

        private Inspector(EnrichmentType type) {
            this.type = type;
        }

        /**
         * Verifies the declaration of the enrichment type.
         *
         * @throws NoMatchingTypeException
         *          if there are no types matching the type reference found in
         *          the {@code (enrichment_for)} of the enrichment type declaration
         */
        void check() {
            if (!type.sourceTypes().isEmpty()) {
                // There is at least one type which matches the referenced type.
                //TODO:2019-02-26:alexander.yevsyukov: There may be other errors like double field
                // reference for a single type.
                return;
            }

            Set<MessageType> resolvedTypes = checkSourceTypeRef();
            checkFieldReferences(resolvedTypes);
        }

        /**
         * None of the types matches either the type reference <em>or</em> the field references,
         * as checked by {@link #isSource(Descriptor)}, which filters the types by the combination.
         *
         * <p>In order to report correct diagnostics this method verifies matching types. If there
         * is at least one matching type, it is returned as the result of this method.
         * Otherwise, {@link IllegalStateException} is thrown with corresponding error message.
         */
        private Set<MessageType> checkSourceTypeRef() {
            Set<MessageType> matchingTypes =
                    KnownTypes.instance()
                              .asTypeSet()
                              .messageTypes()
                              .stream()
                              .filter(m -> type.matchesSourceRef(m.descriptor()))
                              .collect(toImmutableSet());

            if (matchingTypes.isEmpty()) {
                throw new NoMatchingTypeException(typeRef());
            }
            return matchingTypes;
        }

        private void checkFieldReferences(Set<MessageType> types) {
            for (FieldDef field : type.fields) {
                ImmutableList<FieldRef> unresolved = field.findUnresolved(types);
                if (!unresolved.isEmpty()) {
                    throw new UnsatisfiedFieldReferenceException(type, unresolved, typeRef(), types);
                }
            }
        }

        private String typeRef() {
            return EnrichmentForOption.value(type.descriptor());
        }
    }
}

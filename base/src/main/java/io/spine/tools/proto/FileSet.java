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

package io.spine.tools.proto;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.FileDescriptor;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static io.spine.tools.proto.FileDescriptors.extractFiles;
import static io.spine.tools.proto.Linker.link;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * A set of proto files represented by their {@linkplain FileDescriptor descriptors}.
 *
 * @author Alexander Yevsyukov
 */
public final class FileSet {

    private static final FileDescriptor[] EMPTY = {};

    private final Set<FileDescriptor> files;

    private FileSet(Iterable<FileDescriptor> files) {
        this.files = Sets.newHashSet(files);
    }

    private FileSet() {
        this.files = Sets.newHashSet();
    }

    /**
     * Creates an empty set.
     */
    static FileSet newInstance() {
        return new FileSet();
    }

    /**
     * Creates a new file set by parsing the passed descriptor set file.
     */
    public static FileSet parse(File descriptorSet) {
        return parse(descriptorSet.getAbsolutePath());
    }

    /**
     * Creates a new file set by parsing the passed descriptor set file.
     */
    private static FileSet parse(String descriptorSetFile) {
        final List<FileDescriptorProto> files = FileDescriptors.parse(descriptorSetFile);
        final FileSet result = link(files);
        return result;
    }

    /**
     * Loads main file set from resources.
     */
    public static FileSet loadMain() {
        final Iterator<FileDescriptorSet> fileSets = FileDescriptors.loadMain();
        return linkSets(fileSets);
    }

    /**
     * Loads test file set from resources.
     */
    @VisibleForTesting
    public static FileSet loadTest() {
        final Iterator<FileDescriptorSet> fileSets = FileDescriptors.loadTest();
        return linkSets(fileSets);
    }

    private static FileSet linkSets(Iterator<FileDescriptorSet> descriptorSets) {
        final Collection<FileDescriptorProto> files = extractFiles(descriptorSets);
        final FileSet result = link(newArrayList(files));
        return result;
    }

    /**
     * Creates a new set which is a union of this and the passed one.
     */
    public FileSet union(FileSet another) {
        if (another.isEmpty()) {
            return this;
        }

        if (this.isEmpty()) {
            return another;
        }

        Set<FileDescriptor> files = Sets.union(this.files, another.files);
        FileSet result = new FileSet(files);
        return result;
    }

    /**
     * Obtains immutable view of the files in this set.
     */
    Iterable<FileDescriptor> files() {
        return ImmutableSet.copyOf(files);
    }

    /**
     * Obtains array with the files of this set.
     */
    FileDescriptor[] toArray() {
        return files.toArray(EMPTY);
    }

    /**
     * Returns {@code true} if the set contains a file with the passed name,
     * {@code false} otherwise.
     */
    public boolean contains(String fileName) {
        final Optional<FileDescriptor> found = tryFind(fileName);
        return found.isPresent();
    }

    /**
     * Returns {@code true} if the set contains all the files with the passed names,
     * {@code false} otherwise.
     */
    public boolean containsAll(Collection<String> fileNames) {
        final FileSet found = find(fileNames);
        final boolean result = found.size() == fileNames.size();
        return result;
    }

    /**
     * Obtains the set of the files that match passed names.
     */
    public FileSet find(Collection<String> fileNames) {
        final Iterable<FileDescriptor> filtered =
                files.stream()
                     .filter(file -> fileNames.contains(file.getName()))
                     .collect(toSet());
        return new FileSet(filtered);
    }

    /**
     * Returns an Optional containing the first file that matches the name, if such an file exists.
     */
    public Optional<FileDescriptor> tryFind(String fileName) {
        final Optional<FileDescriptor> found = files.stream()
                                                    .filter(file -> fileName.equals(file.getName()))
                                                    .findAny();
        return found;
    }

    /**
     * Adds file to the set.
     */
    @CanIgnoreReturnValue
    public boolean add(FileDescriptor file) {
        final boolean isNew = files.add(file);
        return isNew;
    }

    /**
     * Obtains the size of the set.
     */
    public int size() {
        final int result = files.size();
        return result;
    }

    /**
     * Verifies if the set is empty.
     */
    public boolean isEmpty() {
        final boolean result = files.isEmpty();
        return result;
    }

    /**
     * Obtains alphabetically sorted list of names of files of this set.
     */
    public List<FileName> getFileNames() {
        final Iterable<FileName> fileNames =
                files.stream()
                     .map(FileDescriptor::toProto)
                     .map(FileName::from)
                     .sorted()
                     .collect(toList());
        final List<FileName> result = copyOf(fileNames);
        return result;
    }

    /**
     * Returns a string with alphabetically sorted list of files of this set.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("files", getFileNames())
                          .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(files);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final FileSet other = (FileSet) obj;
        return Objects.equals(this.files, other.files);
    }
}

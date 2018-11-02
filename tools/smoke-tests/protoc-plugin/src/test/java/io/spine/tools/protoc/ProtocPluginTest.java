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

package io.spine.tools.protoc;

import com.google.protobuf.Message;
import io.spine.base.CommandMessage;
import io.spine.base.EventMessage;
import io.spine.base.RejectionMessage;
import io.spine.tools.protoc.test.PIUserEvent;
import io.spine.tools.protoc.test.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProtocPlugin should")
class ProtocPluginTest {

    private static final String EVENT_INTERFACE_FQN =
            "io.spine.tools.protoc.PICustomerEvent";
    private static final String COMMAND_INTERFACE_FQN =
            "io.spine.tools.protoc.PICustomerCommand";
    private static final String USER_COMMAND_FQN =
            "io.spine.tools.protoc.PIUserCommand";

    @Test
    @DisplayName("generate marker interfaces")
    void generate_marker_interfaces() throws ClassNotFoundException {
        checkMarkerInterface(EVENT_INTERFACE_FQN);
    }

    @Test
    @DisplayName("implement marker interface in the generated messages")
    void implement_marker_interfaces_in_generated_messages() {
        assertThat(PICustomerNotified.getDefaultInstance(), instanceOf(PICustomerEvent.class));
        assertThat(PICustomerEmailRecieved.getDefaultInstance(), instanceOf(PICustomerEvent.class));
    }

    @Test
    @DisplayName("generate marker interfaces for the separate messages")
    void generate_marker_interfaces_for_separate_messages() throws ClassNotFoundException {
        checkMarkerInterface(COMMAND_INTERFACE_FQN);
    }

    @Test
    @DisplayName("implement interface in the generated messages with `IS` option")
    void implement_interface_in_generated_messages_with_IS_option() {
        assertThat(PICustomerCreated.getDefaultInstance(), instanceOf(PICustomerEvent.class));
        assertThat(PICreateCustomer.getDefaultInstance(), instanceOf(PICustomerCommand.class));
    }

    @Test
    @DisplayName("use `IS` in priority to `EVERY IS`")
    void use_IS_in_priority_to_EVERY_IS() {
        assertThat(PIUserCreated.getDefaultInstance(), instanceOf(PIUserEvent.class));
        assertThat(PIUserNameUpdated.getDefaultInstance(), instanceOf(PIUserEvent.class));

        assertThat(UserName.getDefaultInstance(), not(instanceOf(PIUserEvent.class)));
        assertThat(UserName.getDefaultInstance(), instanceOf(UserInfo.class));
    }

    @Test
    @DisplayName("resolve packages from src proto if the packages are not specified")
    void resolve_packages_from_src_proto_if_not_specified() throws ClassNotFoundException {
        Class<?> cls = checkMarkerInterface(USER_COMMAND_FQN);
        assertTrue(cls.isAssignableFrom(PICreateUser.class));
    }

    @Test
    @DisplayName("skip non specified message types")
    void skip_non_specified_message_types() {
        Class<?> cls = CustomerName.class;
        Class[] interfaces = cls.getInterfaces();
        assertEquals(1, interfaces.length);
        assertSame(CustomerNameOrBuilder.class, interfaces[0]);
    }

    @Test
    @DisplayName("mark as event messages")
    void mark_event_messages() {
        assertThat(UserCreated.getDefaultInstance(), instanceOf(EventMessage.class));
        assertThat(UserCreated.getDefaultInstance(), instanceOf(FirstEvent.class));
        assertThat(UserNotfied.getDefaultInstance(), instanceOf(EventMessage.class));
    }

    @Test
    @DisplayName("mark as command messages")
    void mark_command_messages() {
        assertThat(CreateUser.getDefaultInstance(), instanceOf(CommandMessage.class));
        assertThat(NotifyUser.getDefaultInstance(), instanceOf(CommandMessage.class));
    }

    @Test
    @DisplayName("mark as rejection messages")
    void mark_rejection_messages() {
        assertThat(Rejections.UserAlreadyExists.getDefaultInstance(),
                   instanceOf(RejectionMessage.class));
        assertThat(Rejections.UserAlreadyExists.getDefaultInstance(),
                   instanceOf(UserRejection.class));
    }

    private static Class<?> checkMarkerInterface(String fqn) throws ClassNotFoundException {
        Class<?> cls = Class.forName(fqn);
        assertTrue(cls.isInterface());
        assertTrue(Message.class.isAssignableFrom(cls));

        Method[] declaredMethods = cls.getDeclaredMethods();
        assertEquals(0, declaredMethods.length);
        return cls;
    }
}
package com.chubock.userservice.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class LocalUserTest {

    @Test
    public void shouldThrowIllegalStateExceptionWhenEmailIsNull() {
        assertThatIllegalStateException().isThrownBy(() -> LocalUser.builder().build());
    }

}

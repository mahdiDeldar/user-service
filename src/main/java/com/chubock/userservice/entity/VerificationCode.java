package com.chubock.userservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @NotEmpty
    private String phone;

    @NotEmpty
    private String code;

    @Builder.Default
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdDate = LocalDateTime.now();

    private int attempt;

    private boolean expired;

    public void increaseAttempt() {
        attempt++;
    }

}

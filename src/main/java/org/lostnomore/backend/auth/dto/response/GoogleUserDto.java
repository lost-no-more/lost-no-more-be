package org.lostnomore.backend.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GoogleUserDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;
}

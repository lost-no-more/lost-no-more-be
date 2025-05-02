package org.lostnomore.backend.auth.oauth.dto;

public record UserTokenResponse(String accessToken, String refreshToken) {

}
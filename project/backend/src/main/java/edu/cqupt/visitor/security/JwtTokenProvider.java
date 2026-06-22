package edu.cqupt.visitor.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expirationSeconds;

    public JwtTokenProvider(ObjectMapper objectMapper,
                            @Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expirationSeconds = expirationMinutes * 60;
    }

    public String generateToken(CurrentUser user) {
        Instant now = Instant.now();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getId());
        payload.put("username", user.getUsername());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusSeconds(expirationSeconds).getEpochSecond());

        String headerPart = base64Url(toJsonBytes(header));
        String payloadPart = base64Url(toJsonBytes(payload));
        String signingInput = headerPart + "." + payloadPart;
        return signingInput + "." + sign(signingInput);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            String signingInput = parts[0] + "." + parts[1];
            String expected = sign(signingInput);
            if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                return false;
            }
            return getExpiration(token).isAfter(Instant.now());
        } catch (Exception ex) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Object value = parsePayload(token).get("sub");
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    public Instant getExpiration(String token) {
        Object value = parsePayload(token).get("exp");
        long epochSeconds = value instanceof Number number ? number.longValue() : Long.parseLong(String.valueOf(value));
        return Instant.ofEpochSecond(epochSeconds);
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private Map<String, Object> parsePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            byte[] json = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Token 解析失败", ex);
        }
    }

    private byte[] toJsonBytes(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Token 序列化失败", ex);
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return base64Url(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Token 签名失败", ex);
        }
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
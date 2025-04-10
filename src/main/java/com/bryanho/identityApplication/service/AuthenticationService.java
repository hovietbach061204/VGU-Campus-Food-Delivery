package com.bryanho.identityApplication.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bryanho.identityApplication.dto.RefreshRequest;
import com.bryanho.identityApplication.dto.request.AuthenticationRequest;
import com.bryanho.identityApplication.dto.request.IntrospectRequest;
import com.bryanho.identityApplication.dto.request.LogoutRequest;
import com.bryanho.identityApplication.dto.response.AuthenticationResponse;
import com.bryanho.identityApplication.dto.response.IntrospectResponse;
import com.bryanho.identityApplication.entity.InvalidatedToken;
import com.bryanho.identityApplication.entity.User;
import com.bryanho.identityApplication.exception.AppException;
import com.bryanho.identityApplication.exception.ErrorCode;
import com.bryanho.identityApplication.repository.InvalidatedTokenRepository;
import com.bryanho.identityApplication.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor // Autowired Bean
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal // this annotation for announcing that not injecting it to constructor
    @Value("${jwt.signerKey}") // this annotation of spring framework is to read a variable in yml file
    protected String SIGNER_KEY; // key for encoding

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false; // if the token is in the invalidated_token table, when we use the introspect api for this
            // token, returns false
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(
                request.getPassword(), user.getPassword()); // this variable authenticated will determine the user
        // login successfully or not

        if (!authenticated) // if user login is incorrect -> throw exception
        throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    // Method for logout
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime =
                (isRefresh) // if isRefresh is true -> the token is used for refreshing, else, it means that it is used
                        // to for token introspecting or authentication
                        ? new Date(
                                signedJWT // if isRefresh is true -> the time is calculated by: the time token is issued
                                        // + token refreshable time
                                        .getJWTClaimsSet()
                                        .getIssueTime()
                                        .toInstant()
                                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                                        .toEpochMilli())
                        : signedJWT
                                .getJWTClaimsSet()
                                .getExpirationTime(); // this is when isRefresh is false, the time would be equal to the
        // expiryTime of the token

        //        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) // if the token is incorrect or it reaches he expiry time
        throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(
                signedJWT.getJWTClaimsSet().getJWTID())) // if this token exists in the table invalidated_token
        throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    // spotless:off
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject(); // get username from the token
        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    // spotless: on

    // method create token
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // header

        // data in body of payload called claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("bryanho.com") // identify this token created from who
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli() // expired after 1 hour
                        ))
                .jwtID(UUID.randomUUID()
                        .toString()) // tokenId. UUID is the sequence of 32 ký tự randomly generated in even global unit
                .claim("scope", buildScope(user)) // a custom claim
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // payload -> json object from claims above
        JWSObject jwsObject = new JWSObject(header, payload); // requires header and payload as params

        try {
            jwsObject.sign(new MACSigner(
                    SIGNER_KEY.getBytes())); // sign with symmetric keys (key for encoding and decoding are the same)
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot generate token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}

package com.identityApplication.IdentityApplication.service;

import com.identityApplication.IdentityApplication.dto.request.AuthenticationRequest;
import com.identityApplication.IdentityApplication.dto.request.IntrospectRequest;
import com.identityApplication.IdentityApplication.dto.request.LogoutRequest;
import com.identityApplication.IdentityApplication.dto.response.AuthenticationResponse;
import com.identityApplication.IdentityApplication.dto.response.IntrospectResponse;
import com.identityApplication.IdentityApplication.entity.InvalidatedToken;
import com.identityApplication.IdentityApplication.entity.User;
import com.identityApplication.IdentityApplication.exception.AppException;
import com.identityApplication.IdentityApplication.exception.ErrorCode;
import com.identityApplication.IdentityApplication.repository.InvalidatedTokenRepository;
import com.identityApplication.IdentityApplication.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;


import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.logging.Logger;

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


    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword()); // this variable authenticated will determine the user
                                    // login successfully or not

        if(!authenticated) // if user login is incorrect -> throw exception
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


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

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                //.plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    // method create token
    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // header

        // data in body of payload called claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("bryanho.com") // identify this token created from who
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli() //expired after 1 hour
                ))
                .jwtID(UUID.randomUUID().toString()) // tokenId. UUID is the sequence of 32 ký tự randomly generated in even global unit
                .claim("scope", buildScope(user)) // a custom claim
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // payload -> json object from claims above
        JWSObject jwsObject = new JWSObject(header, payload); // requires header and payload as params

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes())); // sign with symmetric keys (key for encoding and decoding are the same)
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot generate token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().
                            forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}

package com.developersboard.web.controller.user;

import com.developersboard.annotation.Loggable;
import com.developersboard.backend.service.mail.EmailService;
import com.developersboard.backend.service.security.EncryptionService;
import com.developersboard.backend.service.security.JwtService;
import com.developersboard.backend.service.user.UserService;
import com.developersboard.constant.ErrorConstants;
import com.developersboard.constant.user.ProfileConstants;
import com.developersboard.constant.user.SignUpConstants;
import com.developersboard.constant.user.UserConstants;
import com.developersboard.enums.UserHistoryType;
import com.developersboard.shared.dto.UserDto;
import com.developersboard.shared.util.UserUtils;
import com.developersboard.web.payload.request.SignUpRequest;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles all request related to user's sign-up journey.
 *
 *  
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(SignUpConstants.SIGN_UP_MAPPING)
public class SignUpController {

  private final JwtService jwtService;
  private final UserService userService;
  private final EmailService emailService;
  private final EncryptionService encryptionService;

  /**
   * Returns user to the sign-up form to start registration.
   *
   * @param model the model to transfer objects to view
   * @return the view of the user registration page
   */
  @GetMapping
  public String signUp(final Model model) {
    model.addAttribute(UserConstants.USER_MODEL_KEY, new SignUpRequest());

    return SignUpConstants.SIGN_UP_VIEW_NAME;
  }

  /**
   * Creates a new user and send a verification email to the user.
   *
   * @param model model to transport objects to view
   * @param userDto the userDto
   * @return the view name for confirmation.
   */
  @Loggable
  @PostMapping
  public String signUp(@Valid @ModelAttribute final UserDto userDto, final Model model) {

    if (userService.existsByUsernameOrEmailAndEnabled(userDto.getUsername(), userDto.getEmail())) {
      LOG.warn(UserConstants.USERNAME_OR_EMAIL_EXISTS);
      model.addAttribute(ErrorConstants.ERROR, UserConstants.USERNAME_OR_EMAIL_EXISTS);

      return SignUpConstants.SIGN_UP_VIEW_NAME;
    }

    var verificationToken = jwtService.generateJwtToken(userDto.getUsername());
    userDto.setVerificationToken(verificationToken);

    var savedUserDto = userService.createUser(userDto);
    var encryptedToken = encryptionService.encrypt(verificationToken);
    var encodedToken = encryptionService.encode(encryptedToken);

    emailService.sendAccountVerificationEmail(savedUserDto, encodedToken);
    model.addAttribute(SignUpConstants.SIGN_UP_PENDING_KEY, true);

    model.addAttribute(UserConstants.USER_MODEL_KEY, new SignUpRequest());
    return SignUpConstants.SIGN_UP_VIEW_NAME;
  }

  /**
   * This mapping handles the continuation of sign up.
   *
   * @param token the token.
   * @param redirectAttributes the redirectAttributes
   * @return the view mapping for login.
   */
  @Loggable
  @GetMapping(SignUpConstants.SIGN_UP_VERIFY_MAPPING)
  public String completeSignUp(@RequestParam String token, RedirectAttributes redirectAttributes) {
    var decodedToken = encryptionService.decode(token);
    var verificationToken = encryptionService.decrypt(decodedToken);

    var userDto = validateTokenAndUpdateUser(verificationToken, redirectAttributes);
    if (Objects.nonNull(userDto) && !redirectAttributes.containsAttribute(ErrorConstants.ERROR)) {

      // send an account confirmation to the user.
      emailService.sendAccountConfirmationEmail(userDto);
      redirectAttributes.addFlashAttribute(SignUpConstants.SIGN_UP_SUCCESS_KEY, true);
      redirectAttributes.addFlashAttribute(ProfileConstants.NEW_PROFILE, true);

      return ProfileConstants.REDIRECT_TO_PROFILE;
    }

    return SignUpConstants.SIGN_UP_VIEW_NAME;
  }

  /**
   * Update the user at this point then send an email after an update if the token is valid.
   *
   * @param token the token
   * @return the user dto
   */
  private UserDto validateTokenAndUpdateUser(final String token, final Model model) {
    if (!jwtService.isValidJwtToken(token)) {
      LOG.debug(ErrorConstants.INVALID_TOKEN);
      model.addAttribute(ErrorConstants.ERROR, ErrorConstants.INVALID_TOKEN);
      return null;
    }

    var username = jwtService.getUsernameFromToken(token);
    var userDto = userService.findByUsername(username);

    if (Objects.isNull(userDto) || !token.equals(userDto.getVerificationToken())) {
      LOG.debug(ErrorConstants.INVALID_TOKEN);
      model.addAttribute(ErrorConstants.ERROR, ErrorConstants.INVALID_TOKEN);
      return null;
    }

    if (userDto.getUsername().equals(username) && userDto.isEnabled()) {
      LOG.debug(SignUpConstants.ACCOUNT_EXISTS);
      model.addAttribute(ErrorConstants.ERROR, SignUpConstants.ACCOUNT_EXISTS);
      return null;
    }

    if (userDto.getUsername().equals(username)) {
      UserUtils.enableUser(userDto);
      return userService.updateUser(userDto, UserHistoryType.VERIFIED);
    }

    return null;
  }
}

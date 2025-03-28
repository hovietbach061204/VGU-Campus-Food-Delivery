package com.developersboard.web.payload.response;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;


@Data
public class UserResponse implements Serializable {
  @Serial private static final long serialVersionUID = -8632756128923682589L;

  private String publicId;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private boolean enabled;
}

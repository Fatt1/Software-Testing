package com.flogin.dto.UserDtos;

/**
 * @see com.flogin.entity.User
 * @see LoginResponse
 */
public class UserDto {
  private String userName;
  private String email;

  public UserDto() {
  }
  public UserDto(String userName, String email) {
    this.userName = userName;
    this.email = email;
  }
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  
    
}

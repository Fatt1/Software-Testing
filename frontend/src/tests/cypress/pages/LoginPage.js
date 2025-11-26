/**
 * LoginPage - Page Object Model for Login Page
 * Features:
 * - Username and password input management
 * - Login button interactions
 * - Remember me checkbox
 * - Forgot password link
 * - Sign up link
 * - Password visibility toggle
 * - Error and success message verification
 * - Loading state verification
 * - Form validation support
 * @class LoginPage
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 */
class LoginPage {
  // All element getters return Cypress chainable elements

  /**
   * Get username input field
   * @returns {Cypress.Chainable} Username input element
   */
  get usernameInput() {
    return cy.get('input[type="text"]').first();
  }

  get passwordInput() {
    return cy.get('input[type="password"]');
  }

  get loginButton() {
    return cy.get("button").contains(/login|đăng nhập/i);
  }

  get rememberMeCheckbox() {
    return cy.get('input[type="checkbox"]');
  }

  get forgotPasswordLink() {
    return cy.get("a").contains(/quên|forgot/i);
  }

  get emailInput() {
    return cy.get('input[type="text"]').first();
  }

  get signupLink() {
    return cy.get("a").contains(/đăng ký|sign up|register/i);
  }

  get errorMessage() {
    return cy.get('[style*="color: rgb(220, 38, 38)"]').first();
  }

  get successMessage() {
    return cy.get('[style*="background: rgb(34, 197, 94)"]').first();
  }

  get loadingSpinner() {
    return cy.get('[class*="spinner"], [class*="loading"]');
  }

  get pageTitle() {
    return cy.contains("h1", /login|đăng nhập/i);
  }

  get passwordVisibilityToggle() {
    return cy.get('button[type="button"]').contains("👁️");
  }

  // Methods
  /**
   * Navigate to login page
   */
  navigateToLoginPage() {
    cy.visit("/");
    this.pageTitle.should("be.visible");
    return this;
  }

  /**
   * Enter username
   */
  enterUsername(username) {
    this.usernameInput.clear().type(username, { delay: 100 });
    return this;
  }

  /**
   * Enter password
   */
  enterPassword(password) {
    this.passwordInput.clear().type(password, { delay: 100 });
    return this;
  }

  /**
   * Click login button
   */
  clickLogin() {
    this.loginButton.click();
    return this;
  }

  /**
   * Complete login flow
   */
  login(username, password) {
    this.enterUsername(username);
    this.enterPassword(password);
    this.clickLogin();
    return this;
  }

  /**
   * Check remember me checkbox
   */
  checkRememberMe() {
    this.rememberMeCheckbox.check();
    return this;
  }

  /**
   * Uncheck remember me checkbox
   */
  uncheckRememberMe() {
    this.rememberMeCheckbox.uncheck();
    return this;
  }

  /**
   * Click forgot password link
   */
  clickForgotPassword() {
    this.forgotPasswordLink.click();
    return this;
  }

  /**
   * Click signup link
   */
  clickSignup() {
    this.signupLink.click();
    return this;
  }

  /**
   * Toggle password visibility
   */
  togglePasswordVisibility() {
    this.passwordVisibilityToggle.click();
    return this;
  }

  /**
   * Verify error message exists
   */
  verifyErrorMessage() {
    this.errorMessage.should("be.visible");
    return this;
  }

  /**
   * Verify error message with specific text
   */
  verifyErrorMessageText(text) {
    this.errorMessage.should("contain", text);
    return this;
  }

  /**
   * Verify success message exists
   */
  verifySuccessMessage() {
    this.successMessage.should("be.visible");
    return this;
  }

  /**
   * Verify login button is enabled
   */
  verifyLoginButtonEnabled() {
    this.loginButton.should("not.be.disabled");
    return this;
  }

  /**
   * Verify login button is disabled
   */
  verifyLoginButtonDisabled() {
    this.loginButton.should("be.disabled");
    return this;
  }

  /**
   * Verify loading spinner is visible
   */
  verifyLoadingSpinner() {
    this.loadingSpinner.should("be.visible");
    return this;
  }

  /**
   * Verify username input has value
   */
  verifyUsernameValue(value) {
    this.usernameInput.should("have.value", value);
    return this;
  }

  /**
   * Verify password input is empty
   */
  verifyPasswordEmpty() {
    this.passwordInput.should("have.value", "");
    return this;
  }

  /**
   * Verify page title is visible
   */
  verifyPageTitle() {
    this.pageTitle.should("be.visible");
    return this;
  }

  /**
   * Clear username input
   */
  clearUsername() {
    this.usernameInput.clear();
    return this;
  }

  /**
   * Clear password input
   */
  clearPassword() {
    this.passwordInput.clear();
    return this;
  }

  /**
   * Clear all inputs
   */
  clearAllInputs() {
    this.clearUsername();
    this.clearPassword();
    return this;
  }

  /**
   * Get username input value
   */
  getUsernameValue() {
    return this.usernameInput.invoke("val");
  }

  /**
   * Get password input type
   */
  getPasswordInputType() {
    return this.passwordInput.invoke("attr", "type");
  }

  /**
   * Verify username input is visible
   */
  verifyUsernameInputVisible() {
    this.usernameInput.should("be.visible");
    return this;
  }

  /**
   * Verify password input is visible
   */
  verifyPasswordInputVisible() {
    this.passwordInput.should("be.visible");
    return this;
  }

  /**
   * Verify all form elements are visible
   */
  verifyFormElementsVisible() {
    this.verifyPageTitle();
    this.usernameInput.should("be.visible");
    this.passwordInput.should("be.visible");
    this.loginButton.should("be.visible");
    return this;
  }

  /**
   * Verify username input is empty
   */
  verifyUsernameEmpty() {
    this.usernameInput.should("have.value", "");
    return this;
  }

  /**
   * Verify password input is empty
   */
  verifyPasswordEmpty() {
    this.passwordInput.should("have.value", "");
    return this;
  }

  /**
   * Wait for page to load
   */
  waitForPageLoad() {
    cy.wait(1000);
    this.pageTitle.should("be.visible");
    return this;
  }

  /**
   * Wait for loading to complete
   */
  waitForLoadingComplete() {
    this.loadingSpinner.should("not.exist");
    return this;
  }
}

// Export page object
export default new LoginPage();

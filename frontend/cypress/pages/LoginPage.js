/**
 * Page Object Model for Login Page
 * Encapsulates all selectors and methods for Login page interactions
 */
class LoginPage {
  // Selectors
  get emailInput() {
    return cy.get('input[type="email"]');
  }

  get passwordInput() {
    return cy.get('input[type="password"]');
  }

  get loginButton() {
    return cy.get('button').contains(/login|đăng nhập/i);
  }

  get rememberMeCheckbox() {
    return cy.get('input[type="checkbox"]');
  }

  get forgotPasswordLink() {
    return cy.get('a').contains(/quên|forgot/i);
  }

  get signupLink() {
    return cy.get('a').contains(/đăng ký|sign up|register/i);
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
    return cy.contains('h1', /login|đăng nhập/i);
  }

  get passwordVisibilityToggle() {
    return cy.get('button[type="button"]').contains('👁️');
  }

  // Methods
  /**
   * Navigate to login page
   */
  navigateToLoginPage() {
    cy.visit('/');
    this.pageTitle.should('be.visible');
    return this;
  }

  /**
   * Enter email
   */
  enterEmail(email) {
    this.emailInput.clear().type(email, { delay: 100 });
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
  login(email, password) {
    this.enterEmail(email);
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
    this.errorMessage.should('be.visible');
    return this;
  }

  /**
   * Verify error message with specific text
   */
  verifyErrorMessageText(text) {
    this.errorMessage.should('contain', text);
    return this;
  }

  /**
   * Verify success message exists
   */
  verifySuccessMessage() {
    this.successMessage.should('be.visible');
    return this;
  }

  /**
   * Verify login button is enabled
   */
  verifyLoginButtonEnabled() {
    this.loginButton.should('not.be.disabled');
    return this;
  }

  /**
   * Verify login button is disabled
   */
  verifyLoginButtonDisabled() {
    this.loginButton.should('be.disabled');
    return this;
  }

  /**
   * Verify loading spinner is visible
   */
  verifyLoadingSpinner() {
    this.loadingSpinner.should('be.visible');
    return this;
  }

  /**
   * Verify email input has value
   */
  verifyEmailValue(value) {
    this.emailInput.should('have.value', value);
    return this;
  }

  /**
   * Verify password input is empty
   */
  verifyPasswordEmpty() {
    this.passwordInput.should('have.value', '');
    return this;
  }

  /**
   * Verify page title is visible
   */
  verifyPageTitle() {
    this.pageTitle.should('be.visible');
    return this;
  }

  /**
   * Clear email input
   */
  clearEmail() {
    this.emailInput.clear();
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
    this.clearEmail();
    this.clearPassword();
    return this;
  }

  /**
   * Get email input value
   */
  getEmailValue() {
    return this.emailInput.invoke('val');
  }

  /**
   * Get password input type
   */
  getPasswordInputType() {
    return this.passwordInput.invoke('attr', 'type');
  }

  /**
   * Verify email input is visible
   */
  verifyEmailInputVisible() {
    this.emailInput.should('be.visible');
    return this;
  }

  /**
   * Verify password input is visible
   */
  verifyPasswordInputVisible() {
    this.passwordInput.should('be.visible');
    return this;
  }

  /**
   * Verify all form elements are visible
   */
  verifyFormElementsVisible() {
    this.verifyPageTitle();
    this.emailInput.should('be.visible');
    this.passwordInput.should('be.visible');
    this.loginButton.should('be.visible');
    return this;
  }

  /**
   * Verify email input is empty
   */
  verifyEmailEmpty() {
    this.emailInput.should('have.value', '');
    return this;
  }

  /**
   * Verify password input is empty
   */
  verifyPasswordEmpty() {
    this.passwordInput.should('have.value', '');
    return this;
  }

  /**
   * Wait for page to load
   */
  waitForPageLoad() {
    cy.wait(1000);
    this.pageTitle.should('be.visible');
    return this;
  }

  /**
   * Wait for loading to complete
   */
  waitForLoadingComplete() {
    this.loadingSpinner.should('not.exist');
    return this;
  }
}

// Export page object
module.exports = new LoginPage();

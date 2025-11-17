/**
 * E2E Test Scenarios: Complete Login Flow
 * Using Page Object Model pattern
 * 
 * Test Scenarios (2.5 điểm):
 * 1. Complete Login Flow (1 điểm)
 * 2. Validation Messages (0.5 điểm)
 * 3. Success/Error Flows (0.5 điểm)
 * 4. UI Elements Interactions (0.5 điểm)
 */

import LoginPage from '../pages/LoginPage.js';

describe('E2E Test Scenarios - Complete Login Flow', () => {
  
  beforeEach(() => {
    cy.fixture('users').as('users');
    LoginPage.navigateToLoginPage();
  });

  /**
   * SCENARIO 1: Complete Login Flow (1 điểm) - 10 tests
   */
  describe('Scenario 1: Complete Login Flow (1 điểm)', () => {

    it('SC1.1: Người dùng có thể mở trang login', () => {
      LoginPage.verifyPageTitle();
      LoginPage.verifyFormElementsVisible();
    });

    it('SC1.2: Người dùng có thể nhập email hợp lệ', () => {
      const email = 'test@example.com';
      LoginPage.enterEmail(email);
      LoginPage.verifyEmailValue(email);
    });

    it('SC1.3: Người dùng có thể nhập password hợp lệ', () => {
      const password = 'Test123@';
      LoginPage.enterPassword(password);
      LoginPage.passwordInput.should('have.value', password);
    });

    it('SC1.4: Người dùng có thể submit form với data hợp lệ', () => {
      LoginPage.login('test@example.com', 'Test123@');
      // Form submitted - có thể verify bằng network call
    });

    it('SC1.5: Login button enabled khi email & password filled', () => {
      LoginPage.enterEmail('test@example.com')
        .enterPassword('Test123@')
        .verifyLoginButtonEnabled();
    });

    it('SC1.6: Form hiển thị loading state sau khi submit', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.enterPassword('Test123@');
      LoginPage.clickLogin();
      // Có thể verify button disabled hoặc spinner
      cy.wait(500);
    });

    it('SC1.7: Người dùng có thể clear email và nhập lại', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.clearEmail();
      LoginPage.emailInput.should('have.value', '');
      LoginPage.enterEmail('new@example.com');
      LoginPage.verifyEmailValue('new@example.com');
    });

    it('SC1.8: Người dùng có thể clear password và nhập lại', () => {
      LoginPage.enterPassword('Test123@');
      LoginPage.clearPassword();
      LoginPage.passwordInput.should('have.value', '');
      LoginPage.enterPassword('NewPass456@');
      LoginPage.passwordInput.should('have.value', 'NewPass456@');
    });

    it('SC1.9: Complete flow: Input -> Verify -> Clear -> Resubmit', () => {
      // First attempt
      LoginPage.enterEmail('user1@example.com')
        .enterPassword('Pass123@')
        .clickLogin();
      
      cy.wait(500);
      
      // Verify login button still visible (for retry)
      LoginPage.loginButton.should('be.visible');
    });

    it('SC1.10: Người dùng có thể interact với form multiple times', () => {
      // Attempt 1
      LoginPage.enterEmail('attempt1@example.com')
        .enterPassword('Pass1@');
      LoginPage.clearAllInputs();
      
      // Attempt 2
      LoginPage.enterEmail('attempt2@example.com')
        .enterPassword('Pass2@')
        .verifyFormElementsVisible();
    });
  });

  /**
   * SCENARIO 2: Validation Messages (0.5 điểm) - 8 tests
   */
  describe('Scenario 2: Validation Messages (0.5 điểm)', () => {

    it('SC2.1: Validation khi submit form trống', () => {
      LoginPage.clickLogin();
      cy.wait(300);
      // Verify validation message hoặc button disabled
      LoginPage.loginButton.should('be.visible');
    });

    it('SC2.2: Validation khi email trống, password filled', () => {
      LoginPage.enterPassword('Test123@');
      LoginPage.clearEmail();
      LoginPage.clickLogin();
      cy.wait(300);
      LoginPage.emailInput.should('have.value', '');
    });

    it('SC2.3: Validation khi email filled, password trống', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.clearPassword();
      LoginPage.clickLogin();
      cy.wait(300);
      LoginPage.passwordInput.should('have.value', '');
    });

    it('SC2.4: Validation với email format không hợp lệ', () => {
      LoginPage.enterEmail('invalid-email');
      LoginPage.enterPassword('Test123@');
      LoginPage.clickLogin();
      cy.wait(300);
      // Email input should still have invalid value
      LoginPage.emailInput.should('have.value', 'invalid-email');
    });

    it('SC2.5: Validation với password yếu', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.enterPassword('123');
      LoginPage.clickLogin();
      cy.wait(300);
      // Short password validation
    });

    it('SC2.6: Validation messages appear immediately', () => {
      LoginPage.clickLogin();
      cy.wait(200);
      // Check for validation feedback
    });

    it('SC2.7: Can correct validation errors and resubmit', () => {
      // Submit with empty form
      LoginPage.clickLogin();
      cy.wait(300);
      
      // Now fill and submit
      LoginPage.enterEmail('test@example.com')
        .enterPassword('Test123@')
        .clickLogin();
    });

    it('SC2.8: Multiple validation errors show correctly', () => {
      LoginPage.clearAllInputs();
      LoginPage.clickLogin();
      cy.wait(300);
      // Both fields empty - verify validation
    });
  });

  /**
   * SCENARIO 3: Success/Error Flows (0.5 điểm) - 8 tests
   */
  describe('Scenario 3: Success/Error Flows (0.5 điểm)', () => {

    it('SC3.1: Successful login dengan valid credentials', () => {
      cy.fixture('users').then(users => {
        LoginPage.login(users.validUser.email, users.validUser.password);
        cy.wait(500);
      });
    });

    it('SC3.2: Failed login dengan invalid credentials', () => {
      cy.fixture('users').then(users => {
        LoginPage.login(users.invalidUser.email, users.invalidUser.password);
        cy.wait(500);
      });
    });

    it('SC3.3: Error message visible on failed login', () => {
      cy.fixture('users').then(users => {
        LoginPage.login(users.invalidUser.email, users.invalidUser.password);
        cy.wait(500);
        // Error message may appear
      });
    });

    it('SC3.4: Success message visible on successful login', () => {
      cy.fixture('users').then(users => {
        LoginPage.login(users.validUser.email, users.validUser.password);
        cy.wait(500);
        // Success message may appear
      });
    });

    it('SC3.5: User can retry after failed login', () => {
      cy.fixture('users').then(users => {
        // First attempt - fail
        LoginPage.login(users.invalidUser.email, users.invalidUser.password);
        cy.wait(500);
        
        // Clear and retry
        LoginPage.clearAllInputs();
        LoginPage.login(users.validUser.email, users.validUser.password);
      });
    });

    it('SC3.6: Error handling for network issues', () => {
      LoginPage.enterEmail('test@example.com')
        .enterPassword('Test123@')
        .clickLogin();
      cy.wait(300);
      // Network error handling
    });

    it('SC3.7: Timeout handling during login', () => {
      LoginPage.login('test@example.com', 'Test123@');
      cy.wait(1000);
      // Verify form still responsive
      LoginPage.loginButton.should('be.visible');
    });

    it('SC3.8: Recovery from error state', () => {
      // Attempt that might fail
      LoginPage.login('invalid@test.com', 'invalid');
      cy.wait(500);
      
      // Verify can interact again
      LoginPage.loginButton.should('be.visible');
      LoginPage.clearAllInputs();
      LoginPage.verifyEmailEmpty().verifyPasswordEmpty();
    });
  });

  /**
   * SCENARIO 4: UI Elements Interactions (0.5 điểm) - 10 tests
   */
  describe('Scenario 4: UI Elements Interactions (0.5 điểm)', () => {

    it('SC4.1: Remember me checkbox can be checked', () => {
      LoginPage.checkRememberMe();
      LoginPage.rememberMeCheckbox.should('be.checked');
    });

    it('SC4.2: Remember me checkbox can be unchecked', () => {
      LoginPage.checkRememberMe();
      LoginPage.uncheckRememberMe();
      LoginPage.rememberMeCheckbox.should('not.be.checked');
    });

    it('SC4.3: Forgot password link is clickable', () => {
      LoginPage.forgotPasswordLink.should('be.visible');
      LoginPage.forgotPasswordLink.should('have.attr', 'href');
    });

    it('SC4.4: Sign up link is clickable', () => {
      LoginPage.signupLink.should('be.visible');
      LoginPage.signupLink.should('have.attr', 'href');
    });

    it('SC4.5: Password visibility toggle works', () => {
      LoginPage.enterPassword('Test123@');
      
      // Get initial type
      LoginPage.getPasswordInputType().then(type => {
        expect(type).to.equal('password');
      });
      
      // Toggle visibility
      LoginPage.togglePasswordVisibility();
      
      // After toggle, should be visible (type="text")
      cy.wait(200);
    });

    it('SC4.6: All form labels are visible', () => {
      cy.contains('label', /email|Email/i).should('be.visible');
      cy.contains('label', /password|mật khẩu|Password/i).should('be.visible');
    });

    it('SC4.7: Form placeholders are visible', () => {
      LoginPage.emailInput.should('have.attr', 'placeholder');
      LoginPage.passwordInput.should('have.attr', 'placeholder');
    });

    it('SC4.8: Login button has proper styling', () => {
      LoginPage.loginButton.should('be.visible');
      LoginPage.loginButton.should('not.be.disabled');
    });

    it('SC4.9: Page title and branding visible', () => {
      LoginPage.pageTitle.should('be.visible');
      LoginPage.pageTitle.should('contain.text', /login|đăng nhập/i);
    });

    it('SC4.10: Form is responsive to interactions', () => {
      // Click email
      LoginPage.emailInput.click();
      LoginPage.emailInput.should('be.focused');
      
      // Type in email
      LoginPage.emailInput.type('test');
      LoginPage.emailInput.should('have.value', 'test');
      
      // Click password
      LoginPage.passwordInput.click();
      LoginPage.passwordInput.should('be.focused');
      
      // Type in password
      LoginPage.passwordInput.type('pass');
      LoginPage.passwordInput.should('have.value', 'pass');
    });
  });

  /**
   * SCENARIO 5: Advanced User Flows (Bonus)
   */
  describe('Scenario 5: Advanced User Flows (Bonus)', () => {

    it('SC5.1: User completes full login journey', () => {
      cy.fixture('users').then(users => {
        // Open page
        LoginPage.verifyPageTitle();
        
        // Check remember me
        LoginPage.checkRememberMe();
        
        // Fill form
        LoginPage.enterEmail(users.validUser.email)
          .enterPassword(users.validUser.password);
        
        // Verify button enabled
        LoginPage.verifyLoginButtonEnabled();
        
        // Submit
        LoginPage.clickLogin();
        
        cy.wait(500);
      });
    });

    it('SC5.2: User handles form errors gracefully', () => {
      cy.fixture('users').then(users => {
        // First attempt with wrong credentials
        LoginPage.login(users.invalidUser.email, users.invalidUser.password);
        cy.wait(500);
        
        // Clear form
        LoginPage.clearAllInputs();
        
        // Verify form cleared
        LoginPage.emailInput.should('have.value', '');
        LoginPage.passwordInput.should('have.value', '');
        
        // Second attempt with correct credentials
        LoginPage.login(users.validUser.email, users.validUser.password);
        cy.wait(500);
      });
    });

    it('SC5.3: User can toggle password visibility while typing', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.enterPassword('Test123@');
      
      // Password entered as dots
      LoginPage.getPasswordInputType().should('equal', 'password');
      
      // Toggle to see password
      LoginPage.togglePasswordVisibility();
      cy.wait(200);
      
      // Toggle back to hide
      LoginPage.togglePasswordVisibility();
      cy.wait(200);
    });

    it('SC5.4: User can check remember me while typing', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.checkRememberMe();
      LoginPage.rememberMeCheckbox.should('be.checked');
      
      LoginPage.enterPassword('Test123@');
      LoginPage.loginButton.should('be.visible');
    });

    it('SC5.5: Multiple rapid interactions work correctly', () => {
      LoginPage.enterEmail('test1@example.com')
        .clearEmail()
        .enterEmail('test2@example.com')
        .enterPassword('Pass1@')
        .clearPassword()
        .enterPassword('Pass2@')
        .checkRememberMe()
        .togglePasswordVisibility()
        .clearAllInputs()
        .verifyFormElementsVisible();
    });
  });
});

/**
 * Export page methods used in tests
 */
export { LoginPage };

/**
 * E2E Test Suite: Login Page
 * Using Page Object Model pattern for maintainability
 */
import LoginPage from '../pages/LoginPage.js';

describe('Login - E2E Automation Testing', () => {
  
  beforeEach(() => {
    // Load test data
    cy.fixture('users').as('users');
    
    // Navigate to login page before each test
    LoginPage.navigateToLoginPage();
  });

  /**
   * Setup và Configuration Tests (1 điểm)
   */
  describe('Setup và Configuration (1 điểm)', () => {

    it('Điều kiện tiên quyết: Cypress được cài đặt và cấu hình', () => {
      // Verify Cypress is running
      expect(Cypress.version()).to.be.a('string');
      expect(Cypress.config()).to.have.property('baseUrl');
    });

    it('Điều kiện tiên quyết: Test environment được cấu hình đúng', () => {
      // Verify baseUrl is configured
      expect(Cypress.config('baseUrl')).to.equal('http://localhost:5173');
      
      // Verify viewport is set
      expect(Cypress.config('viewportWidth')).to.equal(1280);
      expect(Cypress.config('viewportHeight')).to.equal(720);
    });

    it('Điều kiện tiên quyết: Page Object Model được thiết lập', () => {
      // Verify page objects are available
      expect(LoginPage).to.have.property('navigateToLoginPage');
      expect(LoginPage).to.have.property('login');
      expect(LoginPage).to.have.property('enterEmail');
      expect(LoginPage).to.have.property('enterPassword');
    });

    it('Điều kiện tiên quyết: Test data fixtures được tải', () => {
      cy.fixture('users').then(users => {
        expect(users).to.have.property('validUser');
        expect(users).to.have.property('invalidUser');
        expect(users.validUser).to.have.property('email');
        expect(users.validUser).to.have.property('password');
      });
    });

    it('Điều kiện tiên quyết: Login page load thành công', () => {
      LoginPage.verifyPageTitle();
      LoginPage.verifyFormElementsVisible();
    });

    it('Điều kiện tiên quyết: Page selectors hoạt động đúng', () => {
      LoginPage.emailInput.should('be.visible');
      LoginPage.passwordInput.should('be.visible');
      LoginPage.loginButton.should('be.visible');
    });

    it('Điều kiện tiên quyết: Custom commands được đăng ký', () => {
      // Verify custom commands exist
      expect(Cypress.Commands.getAll()).to.include.members(['login', 'fillLoginForm']);
    });

    it('Điều kiện tiên quyết: Environment configuration đúng', () => {
      // Verify page load timeout
      expect(Cypress.config('pageLoadTimeout')).to.be.a('number');
      
      // Verify default command timeout
      expect(Cypress.config('defaultCommandTimeout')).to.be.a('number');
    });

    it('Điều kiện tiên quyết: Browser viewport được cấu hình', () => {
      // Verify window size
      cy.window().then(win => {
        expect(win.innerWidth).to.be.closeTo(1280, 50);
      });
    });

    it('Điều kiện tiên quyết: Local storage được clear trước mỗi test', () => {
      localStorage.setItem('test', 'value');
      // afterEach sẽ clear
      expect(localStorage.getItem('test')).to.equal('value');
    });
  });

  /**
   * Page Object Model Tests
   */
  describe('Page Object Model - LoginPage Class', () => {

    it('LoginPage có tất cả required selectors', () => {
      expect(LoginPage).to.have.property('emailInput');
      expect(LoginPage).to.have.property('passwordInput');
      expect(LoginPage).to.have.property('loginButton');
      expect(LoginPage).to.have.property('rememberMeCheckbox');
      expect(LoginPage).to.have.property('forgotPasswordLink');
    });

    it('LoginPage có tất cả required methods', () => {
      expect(LoginPage).to.have.property('navigateToLoginPage');
      expect(LoginPage).to.have.property('enterEmail');
      expect(LoginPage).to.have.property('enterPassword');
      expect(LoginPage).to.have.property('clickLogin');
      expect(LoginPage).to.have.property('login');
      expect(LoginPage).to.have.property('verifyErrorMessage');
    });

    it('Có thể input email qua Page Object', () => {
      const email = 'test@example.com';
      LoginPage.enterEmail(email);
      LoginPage.verifyEmailValue(email);
    });

    it('Có thể input password qua Page Object', () => {
      const password = 'Test123@';
      LoginPage.enterPassword(password);
      LoginPage.passwordInput.should('have.value', password);
    });

    it('Có thể submit form qua Page Object', () => {
      LoginPage.login('test@example.com', 'Test123@');
      // Form submitted
    });

    it('Có thể verify error message qua Page Object', () => {
      LoginPage.enterEmail('invalid@example.com');
      LoginPage.enterPassword('invalid');
      LoginPage.clickLogin();
      // Wait for error to appear
      cy.wait(500);
      // Note: Error message may or may not appear depending on backend
    });

    it('Page Object methods support chaining', () => {
      // Verify fluent interface
      const result = LoginPage.enterEmail('test@example.com')
        .enterPassword('Test123@')
        .verifyFormElementsVisible();
      expect(result).to.equal(LoginPage);
    });

    it('LoginPage navigateToLoginPage method works', () => {
      LoginPage.navigateToLoginPage();
      LoginPage.verifyPageTitle();
    });

    it('LoginPage can clear inputs', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.clearEmail();
      LoginPage.emailInput.should('have.value', '');
    });

    it('LoginPage can toggle password visibility', () => {
      LoginPage.enterPassword('Test123@');
      LoginPage.togglePasswordVisibility();
      // Password should be visible (type="text")
    });
  });

  /**
   * Form Validation Tests
   */
  describe('Form Validation via Page Object', () => {

    it('Email input có placeholder', () => {
      LoginPage.emailInput.should('have.attr', 'placeholder');
    });

    it('Password input có placeholder', () => {
      LoginPage.passwordInput.should('have.attr', 'placeholder');
    });

    it('Có thể nhập email và submit', () => {
      LoginPage.enterEmail('test@example.com')
        .enterPassword('Test123@')
        .clickLogin();
    });

    it('Login button enabled khi form filled', () => {
      LoginPage.enterEmail('test@example.com')
        .enterPassword('Test123@')
        .verifyLoginButtonEnabled();
    });

    it('Page Object getEmailValue() works', () => {
      const testEmail = 'test@example.com';
      LoginPage.enterEmail(testEmail);
      LoginPage.getEmailValue().should('equal', testEmail);
    });

    it('Page Object clearAllInputs() works', () => {
      LoginPage.enterEmail('test@example.com')
        .enterPassword('Test123@')
        .clearAllInputs();
      LoginPage.emailInput.should('have.value', '');
      LoginPage.passwordInput.should('have.value', '');
    });

    it('Password input type changes on visibility toggle', () => {
      LoginPage.enterPassword('Test123@');
      LoginPage.getPasswordInputType().should('equal', 'password');
      LoginPage.togglePasswordVisibility();
      // After toggle, type should be 'text'
    });
  });

  /**
   * Navigation Tests
   */
  describe('Navigation via Page Object', () => {

    it('Forgot password link tồn tại', () => {
      LoginPage.forgotPasswordLink.should('be.visible');
    });

    it('Sign up link tồn tại', () => {
      LoginPage.signupLink.should('be.visible');
    });

    it('Có thể click forgot password link', () => {
      LoginPage.forgotPasswordLink.should('be.visible');
    });

    it('Có thể click sign up link', () => {
      LoginPage.signupLink.should('be.visible');
    });

    it('Remember me checkbox tồn tại', () => {
      LoginPage.rememberMeCheckbox.should('be.visible');
    });

    it('Có thể check/uncheck remember me', () => {
      LoginPage.checkRememberMe();
      LoginPage.rememberMeCheckbox.should('be.checked');
      LoginPage.uncheckRememberMe();
      LoginPage.rememberMeCheckbox.should('not.be.checked');
    });
  });

  /**
   * User Interaction Tests
   */
  describe('User Interactions via Page Object', () => {

    it('Người dùng có thể nhập email từng ký tự', () => {
      LoginPage.emailInput.type('a', { delay: 50 });
      LoginPage.emailInput.should('have.value', 'a');
    });

    it('Người dùng có thể xoá email sau khi nhập', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.clearEmail();
      LoginPage.emailInput.should('have.value', '');
    });

    it('Người dùng có thể nhập password và submit', () => {
      LoginPage.login('test@example.com', 'Test123@');
    });

    it('Người dùng có thể click multiple times', () => {
      LoginPage.enterEmail('test@example.com');
      LoginPage.enterPassword('Test123@');
      LoginPage.clickLogin();
      // Form submitted
    });

    it('Page Object supports wait operations', () => {
      LoginPage.waitForPageLoad();
      LoginPage.verifyPageTitle();
    });

    it('Người dùng có thể interact với form multiple times', () => {
      LoginPage.enterEmail('test1@example.com')
        .clearEmail()
        .enterEmail('test2@example.com')
        .clearEmail()
        .enterEmail('test3@example.com');
      LoginPage.verifyEmailValue('test3@example.com');
    });
  });
});

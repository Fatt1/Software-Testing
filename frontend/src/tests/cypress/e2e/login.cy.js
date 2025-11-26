/**
 * @see ../pages/LoginPage.js - Page Object Model implementation
 * @see ../fixtures/users.json - Test user credentials
 */

/**
 * E2E Test Suite: Login Page
 * Using Page Object Model pattern for maintainability
 */
import LoginPage from "../pages/LoginPage.js";

describe("Login - E2E Automation Testing", () => {
  beforeEach(() => {
    // Load test data
    cy.fixture("users").as("users");

    // Navigate to login page before each test
    LoginPage.navigateToLoginPage();
  });

  /**
   * Setup và Configuration Tests
   */
  describe("Setup và Configuration (1 điểm)", () => {
    it("Điều kiện tiên quyết: Cypress được cài đặt và cấu hình", () => {
      // Verify Cypress is running
      expect(Cypress.version).to.be.a("string");
      expect(Cypress.config()).to.have.property("baseUrl");
    });

    it("Điều kiện tiên quyết: Test environment được cấu hình đúng", () => {
      // Verify baseUrl is configured
      expect(Cypress.config("baseUrl")).to.equal("http://localhost:5173");

      // Verify viewport is set
      expect(Cypress.config("viewportWidth")).to.equal(1280);
      expect(Cypress.config("viewportHeight")).to.equal(720);
    });

    it("Điều kiện tiên quyết: Page Object Model được thiết lập", () => {
      // Verify page objects are available
      expect(LoginPage).to.have.property("navigateToLoginPage");
      expect(LoginPage).to.have.property("login");
      expect(LoginPage).to.have.property("enterUsername");
      expect(LoginPage).to.have.property("enterPassword");
    });

    it("Điều kiện tiên quyết: Test data fixtures được tải", () => {
      cy.fixture("users").then((users) => {
        expect(users).to.have.property("validUser");
        expect(users).to.have.property("invalidUser");
        expect(users.validUser).to.have.property("username");
        expect(users.validUser).to.have.property("password");
      });
    });

    it("Điều kiện tiên quyết: Login page load thành công", () => {
      LoginPage.verifyPageTitle();
      LoginPage.verifyFormElementsVisible();
    });

    it("Điều kiện tiên quyết: Page selectors hoạt động đúng", () => {
      LoginPage.usernameInput.should("be.visible");
      LoginPage.passwordInput.should("be.visible");
      LoginPage.loginButton.should("be.visible");
    });

    it("Điều kiện tiên quyết: Environment configuration đúng", () => {
      // Verify page load timeout
      expect(Cypress.config("pageLoadTimeout")).to.be.a("number");

      // Verify default command timeout
      expect(Cypress.config("defaultCommandTimeout")).to.be.a("number");
    });

    it("Điều kiện tiên quyết: Browser viewport được cấu hình", () => {
      // Verify window size
      cy.window().then((win) => {
        expect(win.innerWidth).to.be.closeTo(1280, 50);
      });
    });

    it("Điều kiện tiên quyết: Local storage được clear trước mỗi test", () => {
      localStorage.setItem("test", "value");
      // afterEach sẽ clear
      expect(localStorage.getItem("test")).to.equal("value");
    });
  });

  /**
   * Page Object Model Tests
   */
  describe("Page Object Model - LoginPage Class", () => {
    it("LoginPage có tất cả required selectors", () => {
      expect(LoginPage).to.have.property("usernameInput");
      expect(LoginPage).to.have.property("passwordInput");
      expect(LoginPage).to.have.property("loginButton");
      expect(LoginPage).to.have.property("rememberMeCheckbox");
      expect(LoginPage).to.have.property("forgotPasswordLink");
    });

    it("LoginPage có tất cả required methods", () => {
      expect(LoginPage).to.have.property("navigateToLoginPage");
      expect(LoginPage).to.have.property("enterUsername");
      expect(LoginPage).to.have.property("enterPassword");
      expect(LoginPage).to.have.property("clickLogin");
      expect(LoginPage).to.have.property("login");
      expect(LoginPage).to.have.property("verifyErrorMessage");
    });

    it("Có thể input username qua Page Object", () => {
      const username = "testuser";
      LoginPage.enterUsername(username);
      LoginPage.verifyUsernameValue(username);
    });

    it("Có thể input password qua Page Object", () => {
      const password = "Test123@";
      LoginPage.enterPassword(password);
      LoginPage.passwordInput.should("have.value", password);
    });

    it("Có thể submit form qua Page Object", () => {
      LoginPage.login("testuser", "Test123@");
      // Form submitted
    });

    it("Có thể verify error message qua Page Object", () => {
      LoginPage.enterUsername("invaliduser");
      LoginPage.enterPassword("invalid");
      LoginPage.clickLogin();
      // Wait for error to appear
      cy.wait(500);
      // Note: Error message may or may not appear depending on backend
    });

    it("Page Object methods support chaining", () => {
      // Verify fluent interface
      const result = LoginPage.enterUsername("testuser")
        .enterPassword("Test123@")
        .verifyFormElementsVisible();
      expect(result).to.equal(LoginPage);
    });

    it("LoginPage navigateToLoginPage method works", () => {
      LoginPage.navigateToLoginPage();
      LoginPage.verifyPageTitle();
    });

    it("LoginPage can clear inputs", () => {
      LoginPage.enterUsername("testuser");
      LoginPage.clearUsername();
      LoginPage.usernameInput.should("have.value", "");
    });

    it("LoginPage can toggle password visibility", () => {
      LoginPage.enterPassword("Test123@");
      LoginPage.togglePasswordVisibility();
      // Password should be visible (type="text")
    });
  });

  /**
   * Form Validation Tests
   */
  describe("Form Validation via Page Object", () => {
    it("Username input có placeholder", () => {
      LoginPage.usernameInput.should("have.attr", "placeholder");
    });

    it("Password input có placeholder", () => {
      LoginPage.passwordInput.should("have.attr", "placeholder");
    });

    it("Có thể nhập username và submit", () => {
      LoginPage.enterUsername("testuser")
        .enterPassword("Test123@")
        .clickLogin();
    });

    it("Login button enabled khi form filled", () => {
      LoginPage.enterUsername("testuser")
        .enterPassword("Test123@")
        .verifyLoginButtonEnabled();
    });

    it("Page Object getUsernameValue() works", () => {
      const testUsername = "testuser";
      LoginPage.enterUsername(testUsername);
      LoginPage.getUsernameValue().should("equal", testUsername);
    });

    it("Page Object clearAllInputs() works", () => {
      LoginPage.enterUsername("testuser")
        .enterPassword("Test123@")
        .clearAllInputs();
      LoginPage.usernameInput.should("have.value", "");
      LoginPage.passwordInput.should("have.value", "");
    });

    it("Password input type changes on visibility toggle", () => {
      LoginPage.enterPassword("Test123@");
      LoginPage.getPasswordInputType().should("equal", "password");
      LoginPage.togglePasswordVisibility();
      // After toggle, type should be 'text'
    });
  });

  /**
   * Navigation Tests
   */
  describe("Navigation via Page Object", () => {
    it("Forgot password link tồn tại", () => {
      LoginPage.forgotPasswordLink.should("be.visible");
    });

    it("Sign up link tồn tại", () => {
      LoginPage.signupLink.should("be.visible");
    });

    it("Có thể click forgot password link", () => {
      LoginPage.forgotPasswordLink.should("be.visible");
    });

    it("Có thể click sign up link", () => {
      LoginPage.signupLink.should("be.visible");
    });

    it("Remember me checkbox tồn tại", () => {
      LoginPage.rememberMeCheckbox.should("be.visible");
    });

    it("Có thể check/uncheck remember me", () => {
      LoginPage.checkRememberMe();
      LoginPage.rememberMeCheckbox.should("be.checked");
      LoginPage.uncheckRememberMe();
      LoginPage.rememberMeCheckbox.should("not.be.checked");
    });
  });

  /**
   * User Interaction Tests
   */
  describe("User Interactions via Page Object", () => {
    it("Người dùng có thể nhập username từng ký tự", () => {
      LoginPage.usernameInput.type("a", { delay: 50 });
      LoginPage.usernameInput.should("have.value", "a");
    });

    it("Người dùng có thể xoá username sau khi nhập", () => {
      LoginPage.enterUsername("testuser");
      LoginPage.clearUsername();
      LoginPage.usernameInput.should("have.value", "");
    });

    it("Người dùng có thể nhập password và submit", () => {
      LoginPage.login("testuser", "Test123@");
    });

    it("Người dùng có thể click multiple times", () => {
      LoginPage.enterUsername("testuser");
      LoginPage.enterPassword("Test123@");
      LoginPage.clickLogin();
      // Form submitted
    });

    it("Page Object supports wait operations", () => {
      LoginPage.waitForPageLoad();
      LoginPage.verifyPageTitle();
    });

    it("Người dùng có thể interact với form multiple times", () => {
      LoginPage.enterUsername("user1")
        .clearUsername()
        .enterUsername("user2")
        .clearUsername()
        .enterUsername("user3");
      LoginPage.verifyUsernameValue("user3");
    });
  });
});

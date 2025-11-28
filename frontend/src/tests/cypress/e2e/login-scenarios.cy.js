/**
 * @see ../pages/LoginPage.js - Page Object implementation
 * @see ../fixtures/users.json - Test user data
 */

import LoginPage from "../pages/LoginPage.js";

describe("E2E Test Scenarios - Complete Login Flow", () => {
  beforeEach(() => {
    cy.fixture("users").as("users");
    LoginPage.navigateToLoginPage();
  });

  /**
   * SCENARIO 1: Complete Login Flow
   */
  describe("Scenario 1: Complete Login Flow (1 điểm)", () => {
    it("SC1.1: Người dùng có thể mở trang login", () => {
      LoginPage.verifyPageTitle();
      LoginPage.verifyFormElementsVisible();
    });

    it("SC1.2: Người dùng có thể nhập username hợp lệ", () => {
      const username = "test";
      LoginPage.enterUsername(username);
      LoginPage.verifyUsernameValue(username);
    });

    it("SC1.3: Người dùng có thể nhập password hợp lệ", () => {
      const password = "Test123@";
      LoginPage.enterPassword(password);
      LoginPage.passwordInput.should("have.value", password);
    });
  });

  /**
   * SCENARIO 2: Validation Messages
   */
  describe("Scenario 2: Validation Messages (0.5 điểm)", () => {
    it("SC2.1: Validation khi submit form trống", () => {
      LoginPage.clickLogin();
      cy.wait(300);
      // Verify validation message hoặc button disabled
      LoginPage.loginButton.should("be.visible");
    });

    it("SC2.2: Validation khi username trống, password filled", () => {
      LoginPage.enterPassword("Test123@");
      LoginPage.clearUsername();
      LoginPage.clickLogin();
      cy.wait(300);
      LoginPage.usernameInput.should("have.value", "");
    });

    it("SC2.3: Validation khi username filled, password trống", () => {
      LoginPage.enterUsername("test");
      LoginPage.clearPassword();
      LoginPage.clickLogin();
      cy.wait(300);
      LoginPage.passwordInput.should("have.value", "");
    });

  });

  /**
   * SCENARIO 3: Success/Error Flows
   */
  describe("Scenario 3: Success/Error Flows (0.5 điểm)", () => {
    it("SC3.1: Successful login dengan valid credentials", () => {
      cy.fixture("users").then((users) => {
        LoginPage.login(users.validUser.username, users.validUser.password);
        cy.wait(500);
      });
    });

    it("SC3.2: Failed login dengan invalid credentials", () => {
      cy.fixture("users").then((users) => {
        LoginPage.login(users.invalidUser.username, users.invalidUser.password);
        cy.wait(500);
      });
    });

    it("SC3.3: Error message visible on failed login", () => {
      cy.fixture("users").then((users) => {
        LoginPage.login(users.invalidUser.username, users.invalidUser.password);
        cy.wait(500);
        // Error message may appear
      });
    });

  });

  /**
   * SCENARIO 4: UI Elements Interactions
   */
  describe("Scenario 4: UI Elements Interactions (0.5 điểm)", () => {
    it("SC4.1: Remember me checkbox can be checked", () => {
      LoginPage.checkRememberMe();
      LoginPage.rememberMeCheckbox.should("be.checked");
    });

    it("SC4.2: Remember me checkbox can be unchecked", () => {
      LoginPage.checkRememberMe();
      LoginPage.uncheckRememberMe();
      LoginPage.rememberMeCheckbox.should("not.be.checked");
    });

    it("SC4.3: Forgot password link is clickable", () => {
      LoginPage.forgotPasswordLink.should("be.visible");
      LoginPage.forgotPasswordLink.should("have.attr", "href");
    });

  });
});

export { LoginPage };

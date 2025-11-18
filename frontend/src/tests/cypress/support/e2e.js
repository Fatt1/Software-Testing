// cypress/support/e2e.js
// Support file for E2E tests

// Hide console messages
const app = window.top;

if (!app.document.head.querySelector('[data-hide-command-log-request]')) {
  const style = app.document.createElement('style');
  style.innerHTML =
    '.command-name-request, .command-name-xhr { display: none }';
  style.setAttribute('data-hide-command-log-request', '');

  app.document.head.appendChild(style);
}

// Custom commands
Cypress.Commands.add('login', (email, password) => {
  cy.visit('/');
  cy.get('input[type="email"]').type(email);
  cy.get('input[type="password"]').type(password);
  cy.get('button').contains(/login|đăng nhập/i).click();
});

Cypress.Commands.add('fillLoginForm', (email, password) => {
  cy.get('input[type="email"]').type(email);
  cy.get('input[type="password"]').type(password);
});

Cypress.Commands.add('submitLoginForm', () => {
  cy.get('button').contains(/login|đăng nhập/i).click();
});

// Before each test
beforeEach(() => {
  // Clear any stored data
  window.localStorage.clear();
  window.sessionStorage.clear();
});

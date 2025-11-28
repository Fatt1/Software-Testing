/**
 * ProductPage - Page Object Model for Product Management Page
 * 
 * This class encapsulates all selectors and methods for Product Management page
 * interactions following the Page Object Model (POM) design pattern.
 * 
 * Page Sections:
 * 1. Header & Search - Title, add button, search, filters
 * 2. Product Table - Table rows, product data display
 * 3. Action Buttons - View, Edit, Delete for each product
 * 4. Pagination - Next/Previous page navigation
 * 5. Modal Forms - Add/Edit product forms
 * 6. Form Inputs - Name, price, quantity, category, description
 * 7. Validation Messages - Error messages for each field
 * 8. Confirmation Dialogs - Delete confirmation
 * 9. Notifications - Success/Error notifications
 * 10. Detail View - Product detail display modal
 * 
 * Features:
 * - Complete CRUD operations (Create, Read, Update, Delete)
 * - Search and filter products
 * - Form validation
 * - Pagination support
 * - Confirmation dialogs
 * - Success/Error notifications
 * - Product detail view
 * - Comprehensive element selectors
 * - Chainable methods for fluent API
 * 
 * Usage Example:
 * ```javascript
 * import ProductPage from './pages/ProductPage';
 * 
 * describe('Product Management Tests', () => {
 *   it('should create new product', () => {
 *     ProductPage
 *       .navigateToProductPage()
 *       .createProduct({
 *         name: 'iPhone 15',
 *         price: '999.99',
 *         quantity: '50',
 *         category: 'Electronics',
 *         description: 'Latest iPhone model'
 *       })
 *       .verifySuccessNotification('Product created successfully')
 *       .verifyProductExists('iPhone 15');
 *   });
 *   
 *   it('should update product', () => {
 *     ProductPage
 *       .updateProduct('iPhone 15', { price: '1099.99' })
 *       .verifySuccessNotification('Product updated');
 *   });
 *   
 *   it('should delete product', () => {
 *     ProductPage
 *       .deleteProduct('iPhone 15')
 *       .verifyProductNotExists('iPhone 15');
 *   });
 * });
 * ```
 * 
 * @class ProductPage
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 */
class ProductPage {
  // ============ HEADER & SEARCH SELECTORS ============
  // Elements in the page header and search section
  
  /**
   * Get page title element
   * @returns {Cypress.Chainable} Page title heading
   */
  get pageTitle() {
    return cy.contains('h1', /quản lý sản phẩm|product management/i);
  }

  get addProductButton() {
    return cy.contains('button', /thêm sản phẩm|add product/i);
  }

  get searchInput() {
    return cy.get('.search-input');
  }

  get categoryFilter() {
    return cy.get('.filter-select');
  }

  // ============ TABLE SELECTORS ============
  get productTable() {
    return cy.get('table.product-table');
  }

  get tableRows() {
    return cy.get('table.product-table tbody tr');
  }

  get firstProductRow() {
    return cy.get('table.product-table tbody tr').first();
  }

  getProductByName(productName) {
    return cy.contains('table.product-table tbody tr', productName);
  }

  // ============ ACTION BUTTONS IN TABLE ============
  getViewButton(productName) {
    return this.getProductByName(productName).within(() => {
      return cy.get('button.blue');
    });
  }

  getEditButton(productName) {
    return this.getProductByName(productName).within(() => {
      return cy.get('button.green');
    });
  }

  getDeleteButton(productName) {
    return this.getProductByName(productName).within(() => {
      return cy.get('button.red');
    });
  }

  // ============ PAGINATION SELECTORS ============
  get paginationPrevButton() {
    return cy.get('.pagination-btn').first();
  }

  get paginationNextButton() {
    return cy.get('.pagination-btn').last();
  }

  get paginationInfo() {
    return cy.get('.pagination-info');
  }

  // ============ MODAL SELECTORS ============
  get modal() {
    return cy.get('.modal');
  }

  get modalTitle() {
    return cy.get('.modal-title');
  }

  get modalCloseButton() {
    return cy.get('.modal-close');
  }

  get modalOverlay() {
    return cy.get('.modal-overlay');
  }

  // ============ FORM INPUTS ============
  get productNameInput() {
    return cy.get('#name-input');
  }

  get priceInput() {
    return cy.get('#price-input');
  }

  get quantityInput() {
    return cy.get('#quantity-input');
  }

  get categorySelect() {
    return cy.get('#category-select');
  }

  get descriptionInput() {
    return cy.get('#description-textarea');
  }

  // ============ FORM ERROR MESSAGES ============
  get nameError() {
    return cy.contains('p.error-message', /tên sản phẩm|name/i);
  }

  get priceError() {
    return cy.contains('p.error-message', /giá|price/i);
  }

  get quantityError() {
    return cy.contains('p.error-message', /số lượng|quantity/i);
  }

  get categoryError() {
    return cy.contains('p.error-message', /danh mục|category/i);
  }

  get descriptionError() {
    return cy.contains('p.error-message', /mô tả|description/i);
  }

  // ============ ACTION BUTTONS IN MODAL ============
  get submitButton() {
    return cy.get('.form-actions').find('button.btn-primary, button.btn-success, button.btn-danger').first();
  }

  get cancelButton() {
    return cy.get('.form-actions').find('button.btn-secondary');
  }

  // ============ CONFIRMATION DIALOG ============
  get deleteConfirmDialog() {
    return cy.get('.modal-small');
  }

  get deleteConfirmButton() {
    return cy.get('.modal-small').find('button.btn-danger');
  }

  get deleteConfirmCancelButton() {
    return cy.get('.modal-small').find('button.btn-secondary');
  }

  // ============ NOTIFICATION ============
  get notification() {
    return cy.get('.notification');
  }

  get successNotification() {
    return cy.get('.notification.success');
  }

  get errorNotification() {
    return cy.get('.notification.error');
  }

  // ============ DETAIL VIEW SELECTORS ============
  get detailValue() {
    return cy.get('.detail-value');
  }

  get detailDescription() {
    return cy.get('.detail-description');
  }

  get editButtonInDetail() {
    return cy.contains('button', /chỉnh sửa|edit/i);
  }

  get emptyState() {
    return cy.get('.empty-state');
  }

  // ============ METHODS ============

  /**
   * Navigate to product management page
   */
  navigateToProductPage() {
    cy.visit('/products');
    this.pageTitle.should('be.visible');
    return this;
  }

  /**
   * Click Add Product button
   */
  clickAddProduct() {
    this.addProductButton.click();
    return this;
  }

  /**
   * Fill product form
   */
  fillProductForm(productData) {
    if (productData.name) {
      this.productNameInput.clear().type(productData.name, { delay: 100 });
    }
    if (productData.price) {
      this.priceInput.clear().type(productData.price, { delay: 100 });
    }
    if (productData.quantity) {
      this.quantityInput.clear().type(productData.quantity, { delay: 100 });
    }
    if (productData.category) {
      // Select using the correct #category-select selector
      cy.get('#category-select').select(productData.category, { force: true });
    }
    if (productData.description) {
      this.descriptionInput.clear().type(productData.description, { delay: 100 });
    }
    cy.wait(300);
    return this;
  }

  /**
   * Submit product form
   */
  submitForm() {
    this.submitButton.click();
    cy.wait(500);
    return this;
  }

  /**
   * Create new product
   */
  createProduct(productData) {
    this.clickAddProduct();
    cy.wait(300);
    this.fillProductForm(productData);
    this.submitForm();
    return this;
  }

  /**
   * Search product by name
   */
  searchProduct(productName) {
    this.searchInput.clear().type(productName, { delay: 100 });
    cy.wait(300);
    return this;
  }

  /**
   * Filter by category
   */
  filterByCategory(category) {
    this.categoryFilter.select(category);
    cy.wait(300);
    return this;
  }

  /**
   * View product details
   */
  viewProduct(productName) {
    this.getViewButton(productName).click();
    cy.wait(300);
    return this;
  }

  /**
   * Edit product
   */
  editProduct(productName) {
    this.getEditButton(productName).click();
    cy.wait(300);
    return this;
  }

  /**
   * Update product
   */
  updateProduct(productName, updatedData) {
    this.editProduct(productName);
    this.fillProductForm(updatedData);
    this.submitForm();
    return this;
  }

  /**
   * Delete product with confirmation
   */
  deleteProduct(productName) {
    this.getDeleteButton(productName).click();
    cy.wait(300);
    this.deleteConfirmButton.click();
    cy.wait(300);
    return this;
  }

  /**
   * Open delete confirmation and cancel
   */
  deleteProductCancel(productName) {
    this.getDeleteButton(productName).click();
    cy.wait(300);
    this.deleteConfirmCancelButton.click();
    cy.wait(300);
    return this;
  }

  /**
   * Clear all form inputs
   */
  clearForm() {
    this.productNameInput.clear();
    this.priceInput.clear();
    this.quantityInput.clear();
    this.categorySelect.select('');
    this.descriptionInput.clear();
    return this;
  }

  /**
   * Close modal
   */
  closeModal() {
    this.modalCloseButton.click();
    cy.wait(200);
    return this;
  }

  /**
   * Verify product form is visible
   */
  verifyFormVisible() {
    this.productNameInput.should('be.visible');
    this.priceInput.should('be.visible');
    this.quantityInput.should('be.visible');
    this.categorySelect.should('be.visible');
    this.descriptionInput.should('be.visible');
    return this;
  }

  /**
   * Verify product exists in table
   */
  verifyProductExists(productName) {
    this.getProductByName(productName).should('be.visible');
    return this;
  }

  /**
   * Verify product not exists in table
   */
  verifyProductNotExists(productName) {
    cy.contains('table.product-table tbody tr', productName).should('not.exist');
    return this;
  }

  /**
   * Verify success notification
   */
  verifySuccessNotification(message) {
    cy.get('.notification.success', { timeout: 10000 }).should('be.visible');
    if (message) {
      if (message instanceof RegExp) {
        cy.get('.notification').invoke('text').should('match', message);
      } else {
        cy.get('.notification').should('contain', message);
      }
    }
    return this;
  }

  /**
   * Verify error notification
   */
  verifyErrorNotification(message) {
    cy.get('.notification.error', { timeout: 10000 }).should('be.visible');
    if (message) {
      if (message instanceof RegExp) {
        cy.get('.notification').invoke('text').should('match', message);
      } else {
        cy.get('.notification').should('contain', message);
      }
    }
    return this;
  }

  /**
   * Verify error message for field
   */
  verifyFieldError(fieldName) {
    let errorSelector;
    switch(fieldName.toLowerCase()) {
      case 'name':
      case 'tên':
        errorSelector = this.nameError;
        break;
      case 'price':
      case 'giá':
        errorSelector = this.priceError;
        break;
      case 'quantity':
      case 'số lượng':
        errorSelector = this.quantityError;
        break;
      case 'category':
      case 'danh mục':
        errorSelector = this.categoryError;
        break;
      case 'description':
      case 'mô tả':
        errorSelector = this.descriptionError;
        break;
      default:
        throw new Error(`Unknown field: ${fieldName}`);
    }
    errorSelector.should('be.visible');
    return this;
  }

  /**
   * Get total product count
   */
  getTotalProductCount() {
    return this.tableRows.its('length');
  }

  /**
   * Verify pagination visible
   */
  verifyPaginationVisible() {
    this.paginationInfo.should('be.visible');
    return this;
  }

  /**
   * Click next page
   */
  clickNextPage() {
    this.paginationNextButton.click();
    cy.wait(200);
    return this;
  }

  /**
   * Click previous page
   */
  clickPreviousPage() {
    this.paginationPrevButton.click();
    cy.wait(200);
    return this;
  }

  /**
   * Verify table empty
   */
  verifyTableEmpty() {
    // Check if empty state element exists or table shows empty message
    cy.get('.product-table tbody tr').then($rows => {
      expect($rows.text()).to.include('Không có sản phẩm');
    });
    return this;
  }

  /**
   * Verify table not empty
   */
  verifyTableNotEmpty() {
    cy.get('.product-table tbody tr').then($rows => {
      expect($rows.text()).not.to.include('Không có sản phẩm');
    });
    return this;
  }

  /**
   * Get product row data
   */
  getProductRowData(productName) {
    return this.getProductByName(productName).within(() => {
      return {
        name: cy.get('td').eq(0),
        price: cy.get('td').eq(1),
        quantity: cy.get('td').eq(2),
        category: cy.get('td').eq(3)
      };
    });
  }
}

// Export page object
export default new ProductPage();

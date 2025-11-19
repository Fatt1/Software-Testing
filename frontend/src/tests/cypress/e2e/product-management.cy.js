/**
 * E2E Test: Product Management - Basic Operations
 * Using Page Object Model pattern
 */

import ProductPage from '../pages/ProductPage.js';

describe('Product Management - Basic Operations', () => {
  
  beforeEach(() => {
    ProductPage.navigateToProductPage();
  });

  describe('Scenario 1: Page Load and UI Elements', () => {

    it('Should load product management page successfully', () => {
      ProductPage.pageTitle.should('be.visible');
      ProductPage.addProductButton.should('be.visible');
    });

    it('Should display search and filter controls', () => {
      ProductPage.searchInput.should('be.visible');
      ProductPage.categoryFilter.should('be.visible');
    });

    it('Should display product table', () => {
      ProductPage.productTable.should('be.visible');
    });

    it('Should have all header columns visible', () => {
      ProductPage.productTable.within(() => {
        cy.get('thead th').should('have.length', 5);
      });
    });

    it('Should display action buttons for each product', () => {
      ProductPage.tableRows.each(($row) => {
        cy.wrap($row).within(() => {
          cy.get('button.blue').should('be.visible');  // View
          cy.get('button.green').should('be.visible'); // Edit
          cy.get('button.red').should('be.visible');   // Delete
        });
      });
    });
  });

  describe('Scenario 2: Create Product', () => {

    it('Should open product form when clicking Add Product', () => {
      ProductPage.clickAddProduct();
      ProductPage.modal.should('be.visible');
      ProductPage.modalTitle.should('contain', /thêm|add/i);
    });

    it('Should display all form fields', () => {
      ProductPage.clickAddProduct();
      ProductPage.verifyFormVisible();
    });

    it('Should create product with valid data', () => {
      const productData = {
        name: 'Laptop Mới',
        price: '15000000',
        quantity: '5',
        category: 'Điện tử',
        description: 'Laptop cao cấp với hiệu năng mạnh mẽ'
      };

      ProductPage.createProduct(productData);
      ProductPage.verifyProductExists('Laptop Mới');
      ProductPage.verifySuccessNotification(/thêm sản phẩm thành công|added successfully/i);
    });

    it('Should show error when creating product without name', () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: '',
        price: '1000000',
        quantity: '10',
        category: 'Điện tử',
        description: 'Sản phẩm không có tên'
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError('name');
    });

    it('Should show error when creating product with invalid price', () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: 'Sản Phẩm',
        price: '-1000',
        quantity: '10',
        category: 'Điện tử',
        description: 'Sản phẩm có giá âm'
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError('price');
    });

    it('Should show error when creating product with invalid quantity', () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: 'Sản Phẩm',
        price: '1000000',
        quantity: '-5',
        category: 'Điện tử',
        description: 'Sản phẩm có số lượng âm'
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError('quantity');
    });

    it('Should show error when creating product without description', () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: 'Sản Phẩm',
        price: '1000000',
        quantity: '5',
        category: 'Điện tử',
        description: ''
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError('description');
    });

    it('Should close modal when clicking Cancel', () => {
      ProductPage.clickAddProduct();
      ProductPage.modal.should('be.visible');
      ProductPage.cancelButton.click();
      ProductPage.modal.should('not.exist');
    });

    it('Should close modal when clicking X button', () => {
      ProductPage.clickAddProduct();
      ProductPage.modal.should('be.visible');
      ProductPage.modalCloseButton.click();
      ProductPage.modal.should('not.exist');
    });
  });

  describe('Scenario 3: Search and Filter', () => {

    before(() => {
      // Create test products
      ProductPage.navigateToProductPage();
      const product1 = {
        name: 'iPhone 14',
        price: '20000000',
        quantity: '10',
        category: 'Điện tử',
        description: 'Điện thoại thông minh Apple'
      };
      const product2 = {
        name: 'T-Shirt Nam',
        price: '150000',
        quantity: '50',
        category: 'Thời trang',
        description: 'Áo phông nam chất lượng cao'
      };
      ProductPage.createProduct(product1);
      ProductPage.createProduct(product2);
    });

    it('Should search product by name', () => {
      ProductPage.searchProduct('iPhone');
      ProductPage.verifyProductExists('iPhone 14');
    });

    it('Should show no results when searching non-existent product', () => {
      ProductPage.searchProduct('NonExistentProduct123');
      ProductPage.verifyTableEmpty();
    });

    it('Should filter products by category', () => {
      ProductPage.filterByCategory('Điện tử');
      ProductPage.tableRows.should('have.length.greaterThan', 0);
    });

    it('Should show all products when selecting all categories', () => {
      ProductPage.filterByCategory('all');
      ProductPage.verifyTableNotEmpty();
    });

    it('Should clear search when deleting search text', () => {
      ProductPage.searchProduct('iPhone');
      ProductPage.searchInput.clear();
      cy.wait(300);
      ProductPage.verifyTableNotEmpty();
    });
  });

  describe('Scenario 4: View Product Details', () => {

    before(() => {
      ProductPage.navigateToProductPage();
      const productData = {
        name: 'Điều Hòa LG',
        price: '5000000',
        quantity: '3',
        category: 'Đồ gia dụng',
        description: 'Điều hòa không khí 2 chiều tiết kiệm điện năng'
      };
      ProductPage.createProduct(productData);
    });

    it('Should open product detail modal when clicking View', () => {
      ProductPage.viewProduct('Điều Hòa LG');
      ProductPage.modal.should('be.visible');
      ProductPage.modalTitle.should('contain', /chi tiết|details/i);
    });

    it('Should display product details in modal', () => {
      ProductPage.viewProduct('Điều Hòa LG');
      ProductPage.detailValue.should('be.visible');
      ProductPage.detailDescription.should('be.visible');
    });

    it('Should have Edit button in detail view', () => {
      ProductPage.viewProduct('Điều Hòa LG');
      ProductPage.editButtonInDetail.should('be.visible');
    });

    it('Should close detail modal when clicking close button', () => {
      ProductPage.viewProduct('Điều Hòa LG');
      ProductPage.modalCloseButton.click();
      ProductPage.modal.should('not.exist');
    });
  });

  describe('Scenario 5: Edit Product', () => {

    before(() => {
      ProductPage.navigateToProductPage();
      const productData = {
        name: 'Sản Phẩm Chỉnh Sửa',
        price: '500000',
        quantity: '20',
        category: 'Thực phẩm',
        description: 'Sản phẩm thực phẩm chất lượng cao'
      };
      ProductPage.createProduct(productData);
    });

    it('Should open edit form when clicking Edit', () => {
      ProductPage.editProduct('Sản Phẩm Chỉnh Sửa');
      ProductPage.modal.should('be.visible');
      ProductPage.modalTitle.should('contain', /chỉnh sửa|edit/i);
    });

    it('Should update product successfully', () => {
      const updatedData = {
        name: 'Sản Phẩm Đã Cập Nhật',
        price: '600000',
        quantity: '25'
      };
      ProductPage.updateProduct('Sản Phẩm Chỉnh Sửa', updatedData);
      ProductPage.verifySuccessNotification(/cập nhật|updated/i);
    });

    it('Should validate edited product data', () => {
      const updatedData = {
        name: 'Lỗi Cập Nhật',
        price: '-100',
        quantity: '10'
      };
      ProductPage.editProduct('Sản Phẩm Đã Cập Nhật');
      ProductPage.fillProductForm(updatedData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError('price');
    });
  });

  describe('Scenario 6: Delete Product', () => {

    beforeEach(() => {
      ProductPage.navigateToProductPage();
      const productData = {
        name: 'Sản Phẩm Xóa',
        price: '250000',
        quantity: '15',
        category: 'Sách',
        description: 'Sách tham khảo và học tập'
      };
      ProductPage.createProduct(productData);
    });

    it('Should show delete confirmation dialog', () => {
      ProductPage.getDeleteButton('Sản Phẩm Xóa').click();
      cy.wait(300);
      ProductPage.deleteConfirmDialog.should('be.visible');
    });

    it('Should delete product when confirming', () => {
      ProductPage.deleteProduct('Sản Phẩm Xóa');
      ProductPage.verifyProductNotExists('Sản Phẩm Xóa');
      ProductPage.verifySuccessNotification(/xóa sản phẩm thành công|deleted successfully/i);
    });

    it('Should keep product when canceling delete', () => {
      ProductPage.deleteProductCancel('Sản Phẩm Xóa');
      cy.wait(300);
      ProductPage.verifyProductExists('Sản Phẩm Xóa');
    });
  });

  describe('Scenario 7: Multiple Products Management', () => {

    it('Should handle multiple product operations', () => {
      // Create first product
      const product1 = {
        name: 'Sản Phẩm 1',
        price: '100000',
        quantity: '10',
        category: 'Điện tử',
        description: 'Mô tả sản phẩm thứ nhất'
      };
      ProductPage.createProduct(product1);
      ProductPage.verifyProductExists('Sản Phẩm 1');

      // Create second product
      const product2 = {
        name: 'Sản Phẩm 2',
        price: '200000',
        quantity: '20',
        category: 'Thời trang',
        description: 'Mô tả sản phẩm thứ hai'
      };
      ProductPage.createProduct(product2);
      ProductPage.verifyProductExists('Sản Phẩm 2');

      // Verify both exist
      ProductPage.verifyProductExists('Sản Phẩm 1');
      ProductPage.verifyProductExists('Sản Phẩm 2');
    });

    it('Should maintain data integrity when managing multiple products', () => {
      ProductPage.tableRows.should('have.length.greaterThan', 0);
      ProductPage.tableRows.each(($row) => {
        cy.wrap($row).within(() => {
          cy.get('td').eq(0).should('not.be.empty');
        });
      });
    });
  });

  describe('Scenario 8: Form Validation Details', () => {

    it('Should show name validation - too short', () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: 'AB',
        price: '1000000',
        quantity: '10',
        category: 'Điện tử',
        description: 'Tên sản phẩm quá ngắn'
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError('name');
    });

    it('Should show category validation error when not selected', () => {
      ProductPage.clickAddProduct();
      ProductPage.productNameInput.type('Sản Phẩm');
      ProductPage.priceInput.type('1000000');
      ProductPage.quantityInput.type('10');
      ProductPage.descriptionInput.type('Mô tả sản phẩm không có danh mục');
      ProductPage.submitForm();
      ProductPage.verifyFieldError('category');
    });
  });

  describe('Scenario 9: Product Form Interaction', () => {

    it('Should handle rapid form interactions', () => {
      ProductPage.clickAddProduct();
      ProductPage.productNameInput.type('Product1');
      ProductPage.productNameInput.clear();
      ProductPage.productNameInput.type('Product2');
      ProductPage.productNameInput.should('have.value', 'Product2');
    });

    it('Should allow decimal prices', () => {
      ProductPage.clickAddProduct();
      ProductPage.priceInput.type('1500.50');
      ProductPage.priceInput.should('have.value', '1500.50');
    });
  });
});

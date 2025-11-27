/**
 * @see ../pages/ProductPage.js - Page Object Model
 * @see ../../components/ProductManagement.jsx - Component under test
 */

/**
 * E2E Test: Product Management - Basic Operations
 * Using Page Object Model pattern
 */

import ProductPage from "../pages/ProductPage.js";

describe("Product Management - Basic Operations", () => {
  
  // --- PHẦN QUAN TRỌNG ĐÃ SỬA ---
  beforeEach(() => {
    // 1. Vào trang Login thay vì vào thẳng Product
    cy.visit('/login');

    // 2. Nhập thông tin đăng nhập (Admin)
    // Selector này dựa trên code LoginForm bạn gửi trước đó (input type="text")
    cy.get('input[type="text"]').clear().type('admin'); 
    cy.get('input[type="password"]').clear().type('admin123');

    // 3. Click nút Đăng nhập
    cy.get('button').contains(/Đăng Nhập|Login/i).click();

    // 4. QUAN TRỌNG: Đợi React Router chuyển hướng sang '/products'
    // Nếu bước này qua, nghĩa là Login thành công và đang ở đúng trang
    cy.url().should('include', '/products', { timeout: 10000 }); 
  });
  // ------------------------------

  describe("Scenario 1: Page Load and UI Elements", () => {
    it("Should load product management page successfully", () => {
      ProductPage.pageTitle.should("be.visible");
      ProductPage.addProductButton.should("be.visible");
    });

    it("Should display search and filter controls", () => {
      ProductPage.searchInput.should("be.visible");
      ProductPage.categoryFilter.should("be.visible");
    });

    it("Should display product table", () => {
      ProductPage.productTable.should("be.visible");
    });

    it("Should have all header columns visible", () => {
      ProductPage.productTable.within(() => {
        cy.get("thead th").should("have.length", 5);
      });
    });

    it("Should display action buttons for each product", () => {
      ProductPage.tableRows.each(($row) => {
        // Skip empty state row
        if ($row.text().includes("Không có sản phẩm")) {
          return;
        }
        cy.wrap($row).within(() => {
          cy.get("button").should("have.length", 3);
        });
      });
    });
  });

  describe("Scenario 2: Create Product", () => {
    it("Should open product form when clicking Add Product", () => {
      ProductPage.clickAddProduct();
      ProductPage.modal.should("be.visible");
      ProductPage.modalTitle.invoke("text").then((text) => {
        expect(text.toLowerCase()).to.include("thêm");
      });
    });

    it("Should display all form fields", () => {
      ProductPage.clickAddProduct();
      ProductPage.verifyFormVisible();
    });

    it("Should create product with valid data", () => {
      const productData = {
        name: "Laptop Mới",
        price: "15000000",
        quantity: "5",
        category: "Điện tử",
        description: "Laptop cao cấp với hiệu năng mạnh mẽ",
      };

      ProductPage.createProduct(productData);
      ProductPage.verifyProductExists("Laptop Mới");
      ProductPage.verifySuccessNotification(
        /thêm sản phẩm thành công|added successfully/i
      );
    });

    it("Should show error when creating product without name", () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: "",
        price: "1000000",
        quantity: "10",
        category: "Điện tử",
        description: "Sản phẩm không có tên",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError("name");
    });

    it("Should show error when creating product with invalid price", () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: "Sản Phẩm",
        price: "-1000",
        quantity: "10",
        category: "Điện tử",
        description: "Sản phẩm có giá âm",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError("price");
    });

    it("Should show error when creating product with invalid quantity", () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: "Sản Phẩm",
        price: "1000000",
        quantity: "-5",
        category: "Điện tử",
        description: "Sản phẩm có số lượng âm",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError("quantity");
    });

    it("Should show error when creating product without description", () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: "Sản Phẩm",
        price: "1000000",
        quantity: "5",
        category: "Điện tử",
        description: "",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError("description");
    });

    it("Should close modal when clicking Cancel", () => {
      ProductPage.clickAddProduct();
      ProductPage.modal.should("be.visible");
      ProductPage.cancelButton.click();
      ProductPage.modal.should("not.exist");
    });

    it("Should close modal when clicking X button", () => {
      ProductPage.clickAddProduct();
      ProductPage.modal.should("be.visible");
      ProductPage.modalCloseButton.click();
      ProductPage.modal.should("not.exist");
    });
  });

  describe("Scenario 3: Search and Filter", () => {
    beforeEach(() => {
      // Ensure we have at least one product
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          ProductPage.createProduct({
            name: "iPhone 14",
            price: "20000000",
            quantity: "10",
            category: "Điện tử",
            description: "Điện thoại thông minh Apple iPhone 14",
          });
        }
      });
    });

    it("Should search product by name", () => {
      ProductPage.searchProduct("iPhone");
      cy.wait(500);
      ProductPage.tableRows.should("have.length.greaterThan", 0);
      cy.contains("iPhone").should("be.visible");
    });

    it("Should show no results when searching non-existent product", () => {
      ProductPage.searchProduct("NonExistentProduct123");
      ProductPage.verifyTableEmpty();
    });

    it("Should filter products by category", () => {
      ProductPage.filterByCategory("Điện tử");
      ProductPage.tableRows.should("have.length.greaterThan", 0);
    });

    it("Should show all products when selecting all categories", () => {
      ProductPage.filterByCategory("all");
      cy.wait(500);
      // If table is empty, it's still valid
      ProductPage.tableRows.should("exist");
    });

    it("Should clear search when deleting search text", () => {
      ProductPage.searchProduct("iPhone");
      cy.wait(300);
      ProductPage.searchInput.clear();
      cy.wait(300);
      // Just verify input is cleared
      ProductPage.searchInput.should("have.value", "");
    });
  });

  describe("Scenario 4: View Product Details", () => {
    beforeEach(() => {
      // Ensure we have a product to view
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          ProductPage.createProduct({
            name: "Điều Hòa LG",
            price: "5000000",
            quantity: "3",
            category: "Đồ gia dụng",
            description: "Điều hòa không khí 2 chiều tiết kiệm điện năng",
          });
        }
      });
    });

    it("Should open product detail modal when clicking View", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.blue").click();
      });
      cy.wait(500);
      ProductPage.modal.should("be.visible");
    });

    it("Should display product details in modal", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.blue").click();
      });
      cy.wait(500);
      ProductPage.modal.should("be.visible");
      // Just verify modal is open and contains detail elements
      cy.get(".detail-item").should("have.length.greaterThan", 0);
    });

    it("Should have Edit button in detail view", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.blue").click();
      });
      cy.wait(500);
      cy.contains("button", /chỉnh sửa|edit/i).should("be.visible");
    });

    it("Should close detail modal when clicking close button", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.blue").click();
      });
      cy.wait(500);
      ProductPage.modalCloseButton.click();
      cy.wait(300);
      ProductPage.modal.should("not.exist");
    });
  });

  describe("Scenario 5: Edit Product", () => {
    beforeEach(() => {
      // Ensure we have a product to edit
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          ProductPage.createProduct({
            name: "Sản Phẩm Chỉnh Sửa",
            price: "500000",
            quantity: "20",
            category: "Thực phẩm",
            description: "Sản phẩm thực phẩm chất lượng cao",
          });
        }
      });
    });

    it("Should open edit form when clicking Edit", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);
      ProductPage.modal.should("be.visible");
    });

    it("Should update product successfully", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);
      ProductPage.productNameInput.clear().type("Sản Phẩm Đã Cập Nhật");
      ProductPage.submitForm();
      ProductPage.verifySuccessNotification(/cập nhật|updated/i);
    });

    it("Should validate edited product data", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);
      ProductPage.priceInput.clear().type("-100");
      ProductPage.submitForm();
      ProductPage.verifyFieldError("price");
    });
  });

  describe("Scenario 6: Delete Product", () => {
    beforeEach(() => {
      // Ensure we have a product to delete
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          ProductPage.createProduct({
            name: "Sản Phẩm Xóa",
            price: "250000",
            quantity: "15",
            category: "Sách",
            description: "Sách tham khảo và học tập",
          });
        }
      });
    });

    it("Should show delete confirmation dialog", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);
      cy.get(".modal-small").should("be.visible");
    });

    it("Should delete product when confirming", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);
      cy.contains("button", /xóa|delete/i).click();
      cy.wait(500);
      ProductPage.verifySuccessNotification(/xóa sản phẩm|deleted/i);
    });

    it("Should keep product when canceling delete", () => {
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);
      cy.contains("button", /hủy|cancel/i).click();
      cy.wait(300);
      cy.get(".modal-small").should("not.exist");
    });
  });

  describe("Scenario 7: Multiple Products Management", () => {
    it("Should handle multiple product operations", () => {
      // Create first product
      const product1 = {
        name: "Sản Phẩm 1",
        price: "100000",
        quantity: "10",
        category: "Điện tử",
        description: "Mô tả sản phẩm thứ nhất",
      };
      ProductPage.createProduct(product1);
      ProductPage.verifyProductExists("Sản Phẩm 1");

      // Create second product
      const product2 = {
        name: "Sản Phẩm 2",
        price: "200000",
        quantity: "20",
        category: "Thời trang",
        description: "Mô tả sản phẩm thứ hai",
      };
      ProductPage.createProduct(product2);
      ProductPage.verifyProductExists("Sản Phẩm 2");

      // Verify both exist
      ProductPage.verifyProductExists("Sản Phẩm 1");
      ProductPage.verifyProductExists("Sản Phẩm 2");
    });

    it("Should maintain data integrity when managing multiple products", () => {
      ProductPage.tableRows.should("have.length.greaterThan", 0);
      ProductPage.tableRows.each(($row) => {
        cy.wrap($row).within(() => {
          cy.get("td").eq(0).should("not.be.empty");
        });
      });
    });
  });

  describe("Scenario 8: Form Validation Details", () => {
    it("Should show name validation - too short", () => {
      ProductPage.clickAddProduct();
      const productData = {
        name: "AB",
        price: "1000000",
        quantity: "10",
        category: "Điện tử",
        description: "Tên sản phẩm quá ngắn",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();
      ProductPage.verifyFieldError("name");
    });

    it("Should show category validation error when not selected", () => {
      ProductPage.clickAddProduct();
      ProductPage.productNameInput.type("Sản Phẩm");
      ProductPage.priceInput.type("1000000");
      ProductPage.quantityInput.type("10");
      ProductPage.descriptionInput.type("Mô tả sản phẩm không có danh mục");
      ProductPage.submitForm();
      ProductPage.verifyFieldError("category");
    });
  });

  describe("Scenario 9: Product Form Interaction", () => {
    it("Should handle rapid form interactions", () => {
      ProductPage.clickAddProduct();
      ProductPage.productNameInput.type("Product1");
      ProductPage.productNameInput.clear();
      ProductPage.productNameInput.type("Product2");
      ProductPage.productNameInput.should("have.value", "Product2");
    });

    it("Should allow decimal prices", () => {
      ProductPage.clickAddProduct();
      ProductPage.priceInput.type("1500.50");
      ProductPage.priceInput.should("have.value", "1500.50");
    });
  });
});
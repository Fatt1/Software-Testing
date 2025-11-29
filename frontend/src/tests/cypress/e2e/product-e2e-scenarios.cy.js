/**
 * @see ../pages/ProductPage.js - Page Object Model
 * @see ../../components/ProductManagement.jsx - Component under test
 * @see ../../services/productService.js - API service layer
 */

import ProductPage from "../pages/ProductPage.js";

describe("Product Management - E2E Test Scenarios (2.5 điểm)", () => {
  // ========== AUTO LOGIN & SETUP ==========
  beforeEach(() => {
    // 1. Xóa localStorage để test fresh
    cy.clearLocalStorage();
    
    // 2. Auto login trước khi chạy test
    cy.visit("/login");
    cy.get('input[type="text"]').first().type("admin", { delay: 50 });
    cy.get('input[type="password"]').type("admin123", { delay: 50 });
    cy.get("button").contains(/login|đăng nhập/i).click();
    
    // 3. Chờ login thành công (2 giây) - app tự chuyển /products
    cy.wait(2000);
    
    // 4. Verify đã ở Product page
    cy.url().should('include', '/products');
  });

  // ============================================================
  // SCENARIO 1: Test Create Product Flow
  // ============================================================
  describe("Scenario 1: Test Create product flow", () => {
    it("SC1.1: Should open Create Product dialog when clicking Add Product button", () => {
      // Given: User is on Product Management page
      ProductPage.pageTitle.should("be.visible");

      // When: User clicks Add Product button
      ProductPage.addProductButton.click();

      // Then: Create Product modal should be displayed
      ProductPage.modal.should("be.visible");
      ProductPage.modalTitle.invoke("text").then((text) => {
        expect(text.toLowerCase()).to.include("thêm");
      });
    });

    it("SC1.2: Should display all required form fields", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();
      ProductPage.modal.should("be.visible");

      // Then: All required fields should be visible
      ProductPage.productNameInput
        .should("be.visible")
        .and("have.attr", "placeholder");
      ProductPage.priceInput
        .should("be.visible")
        .and("have.attr", "placeholder");
      ProductPage.quantityInput
        .should("be.visible")
        .and("have.attr", "placeholder");
      ProductPage.categorySelect.should("be.visible");
      ProductPage.descriptionInput
        .should("be.visible")
        .and("have.attr", "placeholder");
    });
  });

  // ============================================================
  // SCENARIO 2: Test Read/List Products (0.5 điểm)
  // ============================================================
  describe("Scenario 2: Test Read/List products (0.5 điểm)", () => {
    beforeEach(() => {
      // Ensure we have test data by creating products if table is empty
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          ProductPage.createProduct({
            name: "Samsung Galaxy S24",
            price: "20000000",
            quantity: "15",
            category: "Electronics",
            description: "Điện thoại thông minh Samsung Galaxy S24",
          });
        }
      });
    });

    it("SC2.1: Should display product management page with table", () => {
      // Given: User navigates to Product Management
      // Then: Page should display product table
      ProductPage.pageTitle.should("be.visible");
      ProductPage.productTable.should("be.visible");
    });

    it("SC2.2: Should display table with correct columns", () => {
      // Given: Product table is visible
      // Then: Table should have all required columns
      cy.get("thead th").eq(0).should("contain", "Tên");
      cy.get("thead th").eq(1).should("contain", "Giá");
      cy.get("thead th").eq(2).should("contain", "Số lượng");
      cy.get("thead th").eq(3).should("contain", "Danh mục");
    });

    it("SC2.3: Should display all products in table rows", () => {
      // Given: Table contains products
      // Then: All product data should be displayed correctly
      ProductPage.tableRows.each(($row) => {
        // Skip empty state row
        if ($row.text().includes("Không có sản phẩm")) {
          return;
        }
        // Verify each product has data in required cells
        cy.wrap($row).within(() => {
          cy.get("td").eq(0).should("not.be.empty"); // Name
          cy.get("td").eq(1).should("not.be.empty"); // Price
          cy.get("td").eq(2).should("not.be.empty"); // Quantity
          cy.get("td").eq(3).should("not.be.empty"); // Category
        });
      });
    });
  });

  // ============================================================
  // SCENARIO 3: Test Update Product
  // ============================================================
  describe("Scenario 3: Test Update product ", () => {
    beforeEach(() => {
      // Ensure we have a product to edit
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          ProductPage.createProduct({
            name: "Sản Phẩm Để Chỉnh Sửa",
            price: "5000000",
            quantity: "20",
            category: "Groceries",
            description: "Sản phẩm thực phẩm chất lượng cao",
          });
        }
      });
    });

    it("SC3.1: Should open Edit Product dialog when clicking Edit button", () => {
      // Given: Products are displayed
      // When: User clicks Edit button on first product
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });

      // Then: Edit Product modal should be displayed
      cy.wait(500);
      ProductPage.modal.should("be.visible");
      ProductPage.modalTitle.invoke("text").then((text) => {
        expect(text.toLowerCase()).to.include("chỉnh sửa");
      });
    });
  });

  // ============================================================
  // SCENARIO 4: Test Delete Product (0.5 điểm)
  // ============================================================
  describe("Scenario 4: Test Delete product (0.5 điểm)", () => {
    beforeEach(() => {
      // Ensure we have a product to delete
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          ProductPage.createProduct({
            name: "Sản Phẩm Để Xóa",
            price: "2000000",
            quantity: "15",
            category: "Books",
            description: "Sản phẩm này sẽ bị xóa trong test",
          });
        }
      });
    });

    it("SC4.1: Should show delete confirmation dialog when clicking Delete button", () => {
      // Given: Products are displayed
      // When: User clicks Delete button on first product
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });

      // Then: Delete confirmation modal should be displayed
      cy.wait(500);
      cy.get(".modal-small").should("be.visible");
      cy.contains(/xác nhận xóa|confirm delete/i).should("be.visible");
    });

    it("SC4.2: Should show confirmation message with product details", () => {
      // Given: Delete confirmation dialog is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);

      // Then: Confirmation message should be displayed
      cy.get(".modal-small").should("be.visible");
      cy.contains(/bạn có chắc chắn muốn xóa|are you sure/i).should(
        "be.visible"
      );
    });
  });

  // ============================================================
  // SCENARIO 5: Test Search/Filter Functionality (0.5 điểm)
  // ============================================================
  describe("Scenario 5: Test Search/Filter functionality (0.5 điểm)", () => {
    beforeEach(() => {
      // Create test data for search and filter tests
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.text().includes("Không có sản phẩm")) {
          const testProducts = [
            {
              name: "iPhone 15 Pro",
              price: "30000000",
              quantity: "10",
              category: "Electronics",
              description: "Điện thoại iPhone 15 Pro cao cấp",
            },
            {
              name: "iPad Air 2024",
              price: "25000000",
              quantity: "8",
              category: "Electronics",
              description: "Máy tính bảng iPad Air thế hệ mới",
            },
            {
              name: "Áo thun Cotton",
              price: "250000",
              quantity: "50",
              category: "Clothing",
              description: "Áo thun cotton thoáng mát",
            },
          ];

          testProducts.forEach((product) => {
            ProductPage.createProduct(product);
          });
        }
      });
    });

    it("SC5.1: Should display search input field", () => {
      // Given: Product Management page is loaded
      // Then: Search input should be visible
      ProductPage.searchInput.should("be.visible");
      ProductPage.searchInput.should("have.attr", "placeholder");
    });

    it("SC5.2: Should search product by name successfully", () => {
      // Given: Multiple products exist in the table
      // When: User enters search term
      ProductPage.searchProduct("iPhone");
      cy.wait(500);

      // Then: Only products matching search term should be displayed
      ProductPage.tableRows.should("have.length.greaterThan", 0);
      ProductPage.tableRows.each(($row) => {
        if (!$row.text().includes("Không có sản phẩm")) {
          cy.wrap($row).should("contain", "iPhone");
        }
      });
    });
  });
});

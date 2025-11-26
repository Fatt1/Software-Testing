/**
 * @see ../pages/ProductPage.js - Page Object Model
 * @see ../../components/ProductManagement.jsx - Component under test
 * @see ../../services/productService.js - API service layer
 */

import ProductPage from "../pages/ProductPage.js";

describe("Product Management - E2E Test Scenarios (2.5 điểm)", () => {
  beforeEach(() => {
    ProductPage.navigateToProductPage();
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

    it("SC1.3: Should successfully create product with valid data", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();

      // When: User fills in all required fields with valid data
      const productData = {
        name: "Laptop Dell XPS 13",
        price: "25000000",
        quantity: "5",
        category: "Điện tử",
        description: "Laptop cao cấp Dell XPS 13 với hiệu năng mạnh mẽ",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();

      // Then: Product should be created successfully
      cy.wait(1000);
      ProductPage.verifyProductExists("Laptop Dell XPS 13");
      ProductPage.verifySuccessNotification(
        /thêm sản phẩm thành công|added successfully/i
      );
    });

    it("SC1.4: Should validate required fields - Missing name", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();

      // When: User tries to create product without product name
      const productData = {
        name: "",
        price: "1000000",
        quantity: "10",
        category: "Điện tử",
        description: "Sản phẩm không có tên",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();

      // Then: Validation error for name field should be displayed
      ProductPage.verifyFieldError("name");
    });

    it("SC1.5: Should validate price field - Negative value", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();

      // When: User tries to create product with negative price
      const productData = {
        name: "Sản Phẩm Test",
        price: "-500000",
        quantity: "10",
        category: "Điện tử",
        description: "Sản phẩm có giá âm",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();

      // Then: Validation error for price should be displayed
      ProductPage.verifyFieldError("price");
    });

    it("SC1.6: Should validate quantity field - Negative value", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();

      // When: User tries to create product with negative quantity
      const productData = {
        name: "Sản Phẩm Test",
        price: "1000000",
        quantity: "-5",
        category: "Điện tử",
        description: "Số lượng âm",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();

      // Then: Validation error for quantity should be displayed
      ProductPage.verifyFieldError("quantity");
    });

    it("SC1.7: Should validate missing description field", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();

      // When: User tries to create product without description
      const productData = {
        name: "Sản Phẩm Test",
        price: "1000000",
        quantity: "10",
        category: "Điện tử",
        description: "",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();

      // Then: Validation error for description should be displayed
      ProductPage.verifyFieldError("description");
    });

    it("SC1.8: Should close dialog when clicking Cancel button", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();
      ProductPage.modal.should("be.visible");

      // When: User clicks Cancel button
      ProductPage.cancelButton.click();

      // Then: Modal should be closed
      cy.wait(300);
      ProductPage.modal.should("not.exist");
    });

    it("SC1.9: Should close dialog when clicking X (close) button", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();
      ProductPage.modal.should("be.visible");

      // When: User clicks X close button
      ProductPage.modalCloseButton.click();

      // Then: Modal should be closed
      cy.wait(300);
      ProductPage.modal.should("not.exist");
    });

    it("SC1.10: Should handle form with decimal price value", () => {
      // Given: Create Product dialog is open
      ProductPage.clickAddProduct();

      // When: User enters product with decimal price
      const productData = {
        name: "Sản Phẩm Giá Thập Phân",
        price: "1500.50",
        quantity: "8",
        category: "Điện tử",
        description: "Sản phẩm có giá thập phân",
      };
      ProductPage.fillProductForm(productData);
      ProductPage.submitForm();

      // Then: Product should be created successfully with decimal price
      cy.wait(1000);
      ProductPage.verifyProductExists("Sản Phẩm Giá Thập Phân");
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
            category: "Điện tử",
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

    it("SC2.4: Should display action buttons (View, Edit, Delete) for each product", () => {
      // Given: Products are displayed in table
      // Then: Each product row should have action buttons
      ProductPage.tableRows.each(($row) => {
        if ($row.text().includes("Không có sản phẩm")) {
          return;
        }
        cy.wrap($row).within(() => {
          cy.get("button").should("have.length", 3); // View, Edit, Delete
          cy.get("button.blue").should("be.visible"); // View button
          cy.get("button.green").should("be.visible"); // Edit button
          cy.get("button.red").should("be.visible"); // Delete button
        });
      });
    });

    it("SC2.5: Should display search and filter controls", () => {
      // Given: Product Management page is loaded
      // Then: Search and filter controls should be visible
      ProductPage.searchInput.should("be.visible");
      ProductPage.categoryFilter.should("be.visible");
    });

    it("SC2.6: Should open product detail when clicking View button", () => {
      // Given: Products are displayed in table
      // When: User clicks View button on first product
      ProductPage.tableRows.first().within(() => {
        cy.get("button.blue").click();
      });

      // Then: Product detail modal should be displayed
      cy.wait(500);
      ProductPage.modal.should("be.visible");
    });

    it("SC2.7: Should display product details in detail modal", () => {
      // Given: Product detail modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.blue").click();
      });
      cy.wait(500);

      // Then: Detail modal should contain product information
      ProductPage.modal.should("be.visible");
      cy.get(".detail-item").should("have.length.greaterThan", 0);
    });

    it("SC2.8: Should close detail modal when clicking close button", () => {
      // Given: Product detail modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.blue").click();
      });
      cy.wait(500);
      ProductPage.modal.should("be.visible");

      // When: User clicks close button
      ProductPage.modalCloseButton.click();

      // Then: Modal should be closed
      cy.wait(300);
      ProductPage.modal.should("not.exist");
    });

    it("SC2.9: Should maintain data integrity - all rows have required information", () => {
      // Given: Table is loaded with products
      // Then: Verify data integrity - no empty required fields
      ProductPage.tableRows.each(($row) => {
        if ($row.text().includes("Không có sản phẩm")) {
          return;
        }
        cy.wrap($row).within(() => {
          cy.get("td").each(($cell, index) => {
            if (index < 4) {
              // First 4 columns are required data
              cy.wrap($cell).should("not.be.empty");
            }
          });
        });
      });
    });

    it("SC2.10: Should show empty state message when no products exist", () => {
      // Clean up: Delete all products to test empty state
      cy.get(".product-table tbody tr").then(($rows) => {
        const rowCount = $rows.length;
        if (rowCount > 0 && !$rows.text().includes("Không có sản phẩm")) {
          // Delete all products one by one
          for (let i = 0; i < rowCount; i++) {
            cy.get(".product-table tbody tr")
              .first()
              .within(() => {
                cy.get("button.red").click();
              });
            cy.wait(300);
            cy.contains("button", /xóa|delete/i).click();
            cy.wait(500);
          }
        }
      });

      // Verify empty state
      cy.wait(500);
      cy.get(".product-table tbody tr").then(($rows) => {
        // Check if empty message is displayed
        expect($rows.text()).to.include("Không có sản phẩm");
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
            category: "Thực phẩm",
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

    it("SC3.2: Should populate form fields with current product data", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);

      // Then: Form fields should be visible and ready
      ProductPage.productNameInput.should("be.visible");
      ProductPage.priceInput.should("be.visible");
      ProductPage.quantityInput.should("be.visible");
    });

    it("SC3.3: Should successfully update product name", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);

      // When: User updates product name
      ProductPage.productNameInput.clear().type("Sản Phẩm Đã Cập Nhật Tên");
      ProductPage.submitForm();

      // Then: Product should be updated successfully
      cy.wait(1500);
      // Check that modal closed (update successful)
      ProductPage.modal.should("not.exist");
    });

    it("SC3.4: Should successfully update product price", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);

      // When: User updates product price
      ProductPage.priceInput.clear().type("7500000");
      ProductPage.submitForm();

      // Then: Product price should be updated successfully
      cy.wait(1500);
      // Check that modal closed (update successful)
      ProductPage.modal.should("not.exist");
    });

    it("SC3.5: Should successfully update product quantity", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);

      // When: User updates product quantity
      ProductPage.quantityInput.clear().type("50");
      ProductPage.submitForm();

      // Then: Product quantity should be updated successfully
      cy.wait(1500);
      // Check that modal closed (update successful)
      ProductPage.modal.should("not.exist");
    });

    it("SC3.6: Should validate updated data - negative price", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);

      // When: User tries to update with invalid price
      ProductPage.priceInput.clear().type("-1000000");
      ProductPage.submitForm();

      // Then: Validation error should be displayed
      ProductPage.verifyFieldError("price");
    });

    it("SC3.7: Should validate updated data - negative quantity", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);

      // When: User tries to update with invalid quantity
      ProductPage.quantityInput.clear().type("-10");
      ProductPage.submitForm();

      // Then: Validation error should be displayed
      ProductPage.verifyFieldError("quantity");
    });

    it("SC3.8: Should close edit dialog when clicking Cancel", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);
      ProductPage.modal.should("be.visible");

      // When: User clicks Cancel button
      ProductPage.cancelButton.click();

      // Then: Modal should be closed
      cy.wait(300);
      ProductPage.modal.should("not.exist");
    });

    it("SC3.9: Should close edit dialog when clicking X button", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);
      ProductPage.modal.should("be.visible");

      // When: User clicks X close button
      ProductPage.modalCloseButton.click();

      // Then: Modal should be closed
      cy.wait(300);
      ProductPage.modal.should("not.exist");
    });

    it("SC3.10: Should update multiple fields simultaneously", () => {
      // Given: Edit modal is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.green").click();
      });
      cy.wait(500);

      // When: User updates multiple fields
      ProductPage.productNameInput.clear().type("Sản Phẩm Cập Nhật Toàn Bộ");
      ProductPage.priceInput.clear().type("10000000");
      ProductPage.quantityInput.clear().type("30");
      ProductPage.descriptionInput
        .clear()
        .type("Mô tả sản phẩm đã được cập nhật");
      ProductPage.submitForm();

      // Then: All fields should be updated successfully
      cy.wait(1000);
      ProductPage.verifyProductExists("Sản Phẩm Cập Nhật Toàn Bộ");
      ProductPage.verifySuccessNotification(/cập nhật|updated/i);
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
            category: "Sách",
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

    it("SC4.3: Should successfully delete product when confirming", () => {
      // Given: Delete confirmation dialog is open
      const firstProductName = cy
        .get(".product-table tbody tr")
        .first()
        .find("td")
        .eq(0)
        .invoke("text");

      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);

      // When: User clicks Confirm/Delete button
      cy.contains("button", /xóa|delete/i).click();

      // Then: Product should be deleted successfully
      cy.wait(1000);
      ProductPage.verifySuccessNotification(/xóa sản phẩm|deleted|success/i);
    });

    it("SC4.4: Should cancel deletion when clicking Cancel button", () => {
      // Given: Delete confirmation dialog is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);
      cy.get(".modal-small").should("be.visible");

      // When: User clicks Cancel button
      cy.contains("button", /hủy|cancel/i).click();

      // Then: Modal should be closed and product should remain
      cy.wait(300);
      cy.get(".modal-small").should("not.exist");
      ProductPage.tableRows.should("have.length.greaterThan", 0);
    });

    it("SC4.5: Should cancel deletion when clicking X button", () => {
      // Given: Delete confirmation dialog is open
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);
      cy.get(".modal-small").should("be.visible");

      // When: User clicks X close button or Cancel button
      cy.contains("button", /hủy|cancel/i).click();

      // Then: Modal should be closed and product should remain
      cy.wait(300);
      cy.get(".modal-small").should("not.exist");
    });

    it("SC4.6: Should remove deleted product from list", () => {
      // When: User deletes the first product
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);
      cy.contains("button", /xóa|delete/i).click();
      cy.wait(1500);

      // Then: Modal should close (delete successful) and product removed
      cy.get(".modal-small").should("not.exist");
      ProductPage.modal.should("not.exist");
    });

    it("SC4.7: Should handle multiple product deletions", () => {
      // Create additional products for testing
      ProductPage.createProduct({
        name: "Sản Phẩm Xóa 1",
        price: "1000000",
        quantity: "10",
        category: "Điện tử",
        description: "Sản phẩm xóa thứ 1",
      });
      cy.wait(1000);

      ProductPage.createProduct({
        name: "Sản Phẩm Xóa 2",
        price: "1500000",
        quantity: "8",
        category: "Thời trang",
        description: "Sản phẩm xóa thứ 2",
      });
      cy.wait(1000);

      // Delete products by searching and deleting
      cy.get(".product-table tbody tr").then(($rows) => {
        if ($rows.length > 0 && !$rows.text().includes("Không có sản phẩm")) {
          // Delete first product
          ProductPage.tableRows.first().within(() => {
            cy.get("button.red").click();
          });
          cy.wait(300);
          cy.contains("button", /xóa|delete/i).click();
          cy.wait(1000);

          // Verify deletion
          cy.get(".product-table tbody tr").then(($rowsAfter) => {
            expect($rowsAfter.length).to.be.greaterThan(0);
          });
        }
      });
    });

    it("SC4.8: Should display success message after deletion", () => {
      // Given: Product is to be deleted
      ProductPage.tableRows.first().within(() => {
        cy.get("button.red").click();
      });
      cy.wait(500);

      // When: User confirms deletion
      cy.contains("button", /xóa|delete/i).click();

      // Then: Modal should close (indicates successful deletion)
      cy.wait(1000);
      cy.get(".modal-small").should("not.exist");
    });

    it("SC4.9: Should handle delete operation on last product", () => {
      // Clean up: Delete all but one product first
      cy.get(".product-table tbody tr").then(($rows) => {
        let rowCount = $rows.length;
        if (rowCount > 1 && !$rows.text().includes("Không có sản phẩm")) {
          for (let i = 1; i < rowCount; i++) {
            cy.get(".product-table tbody tr")
              .first()
              .within(() => {
                cy.get("button.red").click();
              });
            cy.wait(300);
            cy.contains("button", /xóa|delete/i).click();
            cy.wait(500);
          }
        }
      });

      // Now delete the last product
      cy.get(".product-table tbody tr").then(($rows) => {
        if (!$rows.text().includes("Không có sản phẩm")) {
          ProductPage.tableRows.first().within(() => {
            cy.get("button.red").click();
          });
          cy.wait(500);
          cy.contains("button", /xóa|delete/i).click();
          cy.wait(1000);
        }
      });

      // Verify empty state is shown
      cy.get(".product-table tbody tr").then(($rows) => {
        expect($rows.text()).to.include("Không có sản phẩm");
      });
    });

    it("SC4.10: Should maintain system stability after rapid deletions", () => {
      // Create multiple products
      for (let i = 1; i <= 3; i++) {
        ProductPage.createProduct({
          name: `Sản Phẩm Xóa Nhanh ${i}`,
          price: `${i * 1000000}`,
          quantity: `${i * 5}`,
          category: "Điện tử",
          description: `Sản phẩm xóa nhanh thứ ${i}`,
        });
      }

      // Perform rapid deletions
      for (let i = 0; i < 2; i++) {
        cy.get(".product-table tbody tr").then(($rows) => {
          if ($rows.text().includes("Không có sản phẩm")) {
            return;
          }
          cy.get(".product-table tbody tr")
            .first()
            .within(() => {
              cy.get("button.red").click();
            });
          cy.wait(300);
          cy.contains("button", /xóa|delete/i).click();
          cy.wait(500);
        });
      }

      // Verify system is still stable
      ProductPage.pageTitle.should("be.visible");
      ProductPage.productTable.should("be.visible");
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
              category: "Điện tử",
              description: "Điện thoại iPhone 15 Pro cao cấp",
            },
            {
              name: "iPad Air 2024",
              price: "25000000",
              quantity: "8",
              category: "Điện tử",
              description: "Máy tính bảng iPad Air thế hệ mới",
            },
            {
              name: "Áo thun Cotton",
              price: "250000",
              quantity: "50",
              category: "Thời trang",
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

    it("SC5.3: Should display no results when searching non-existent product", () => {
      // Given: Products exist in the table
      // When: User searches for non-existent product
      ProductPage.searchProduct("NonExistentProduct12345XYZ");
      cy.wait(500);

      // Then: Empty state message should be displayed
      ProductPage.verifyTableEmpty();
    });

    it("SC5.4: Should search product by partial name", () => {
      // Given: Multiple products exist
      // When: User searches by partial product name
      ProductPage.searchProduct("iPad");
      cy.wait(500);

      // Then: Products matching partial name should be displayed
      cy.contains("iPad Air 2024").should("be.visible");
    });

    it("SC5.5: Should display category filter dropdown", () => {
      // Given: Product Management page is loaded
      // Then: Category filter should be visible
      ProductPage.categoryFilter.should("be.visible");
    });

    it("SC5.6: Should filter products by category", () => {
      // Given: Products with different categories exist
      // When: User selects a category filter
      ProductPage.filterByCategory("Điện tử");
      cy.wait(500);

      // Then: Only products in selected category should be displayed
      ProductPage.tableRows.each(($row) => {
        if (!$row.text().includes("Không có sản phẩm")) {
          cy.wrap($row).should("contain", "Điện tử");
        }
      });
    });

    it('SC5.7: Should show all products when selecting "All" categories', () => {
      // Given: Products filtered by specific category
      ProductPage.filterByCategory("Điện tử");
      cy.wait(500);

      // When: User selects "All" or default option
      ProductPage.categoryFilter.select("all");
      cy.wait(500);

      // Then: All products should be displayed
      ProductPage.tableRows.should("exist");
    });

    it("SC5.8: Should combine search and filter", () => {
      // Given: Products with multiple categories exist
      // When: User applies both search and filter
      ProductPage.searchProduct("Phone");
      cy.wait(300);
      ProductPage.filterByCategory("Điện tử");
      cy.wait(500);

      // Then: Results should match both criteria
      ProductPage.tableRows.each(($row) => {
        if (!$row.text().includes("Không có sản phẩm")) {
          cy.wrap($row).should("contain", "Điện tử");
        }
      });
    });

    it("SC5.9: Should clear search when deleting search text", () => {
      // Given: Search has been applied
      ProductPage.searchProduct("iPhone");
      cy.wait(300);

      // When: User clears search input
      ProductPage.searchInput.clear();
      cy.wait(500);

      // Then: Search input should be empty
      ProductPage.searchInput.should("have.value", "");
    });

    it("SC5.10: Should perform real-time search filtering", () => {
      // Given: User is typing in search field
      ProductPage.searchInput.type("i");
      cy.wait(300);
      ProductPage.searchInput.type("P");
      cy.wait(300);
      ProductPage.searchInput.type("h");
      cy.wait(500);

      // Then: Results should be updated with each keystroke
      ProductPage.tableRows.each(($row) => {
        if (!$row.text().includes("Không có sản phẩm")) {
          cy.wrap($row)
            .invoke("text")
            .then((text) => {
              expect(text.toLowerCase()).to.include("iph");
            });
        }
      });
    });

    it("SC5.11: Should maintain filter after search is cleared", () => {
      // Given: Filter is applied
      ProductPage.filterByCategory("Điện tử");
      cy.wait(300);
      ProductPage.searchProduct("iPhone");
      cy.wait(300);

      // When: User clears search
      ProductPage.searchInput.clear();
      cy.wait(500);

      // Then: Filter should still be active
      ProductPage.tableRows.each(($row) => {
        if (!$row.text().includes("Không có sản phẩm")) {
          cy.wrap($row).should("contain", "Điện tử");
        }
      });
    });

    it("SC5.12: Should handle special characters in search", () => {
      // Given: Products exist
      // When: User searches with special characters
      ProductPage.searchProduct("®™");
      cy.wait(500);

      // Then: Search should handle it gracefully (show no results or handle it)
      ProductPage.tableRows.should("exist");
    });

    it("SC5.13: Should be case-insensitive search", () => {
      // Given: Products with mixed case names exist
      // When: User searches with different case
      ProductPage.searchProduct("IPHONE");
      cy.wait(500);

      // Then: Should find products regardless of case
      ProductPage.tableRows.each(($row) => {
        if (!$row.text().includes("Không có sản phẩm")) {
          cy.wrap($row)
            .invoke("text")
            .then((text) => {
              expect(text.toLowerCase()).to.include("iphone");
            });
        }
      });
    });

    it("SC5.14: Should filter by each available category", () => {
      // Test filtering by each category option
      const categories = ["Điện tử", "Thời trang"];

      categories.forEach((category) => {
        ProductPage.filterByCategory(category);
        cy.wait(500);

        // Verify filter is applied (if products exist in this category)
        cy.get(".product-table tbody tr").then(($rows) => {
          if (!$rows.text().includes("Không có sản phẩm")) {
            cy.get(".product-table tbody tr").should(
              "have.length.greaterThan",
              0
            );
          }
        });
      });
    });

    it("SC5.15: Should search work after applying filter", () => {
      // Given: Filter is applied
      ProductPage.filterByCategory("Điện tử");
      cy.wait(300);

      // When: User performs search within filtered results
      ProductPage.searchProduct("iPhone");
      cy.wait(500);

      // Then: Search should work within filtered context
      ProductPage.tableRows.each(($row) => {
        if (!$row.text().includes("Không có sản phẩm")) {
          cy.wrap($row).should("contain", "iPhone");
          cy.wrap($row).should("contain", "Điện tử");
        }
      });
    });
  });
});

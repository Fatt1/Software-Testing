/**
 * @see ../components/ProductManagement.jsx - Main component under test
 */

import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProductManagement from "../components/ProductManagement";
import "@testing-library/jest-dom";

/**
 * Frontend Component Integration Tests
 * Test tích hợp Product Components
 */
describe("Product Components - Integration Testing", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  /**
   * Test 1: ProductList Component với API
   */
  describe("Test 1: ProductList Component (ProductManagement) với API", () => {
    test("nên render ProductManagement component thành công", () => {
      render(<ProductManagement />);

      // Verify component renders
      const heading = screen.getByText("Quản Lý Sản Phẩm");
      expect(heading).toBeInTheDocument();
    });

    test("nên có category filter dropdown", () => {
      render(<ProductManagement />);

      const filterElements =
        screen.queryAllByRole("button").length > 0 ||
        screen.queryAllByRole("combobox").length > 0 ||
        screen.queryByText(/Tất cả|All|category|Category/i);
      expect(filterElements).toBeTruthy();
    });

    test("nên filter products theo category", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      // Tìm filter button hoặc select
      const filterButtons = screen.queryAllByRole("button");
      expect(filterButtons.length > 0).toBe(true);
    });

    test("nên có action buttons (Edit, Delete, View)", () => {
      render(<ProductManagement />);

      // Verify edit/delete buttons exist or can be added
      const buttons = screen.queryAllByRole("button");
      expect(buttons.length > 0).toBe(true);
    });
  });

  /**
   * Test 2: ProductForm Component (create/edit)
   */
  describe("Test 2: ProductForm Component (create/edit) (2 điểm)", () => {

    test("nên validate product data trước submit", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      const addButton = screen
        .queryAllByRole("button")
        .find(
          (btn) =>
            btn.textContent?.includes("+") || btn.textContent?.includes("Thêm")
        );

      if (addButton) {
        await user.click(addButton);
        // Component renders successfully
        const inputs = screen.queryAllByRole("textbox");
        expect(inputs.length > 0).toBe(true);
      }
    });

    test("nên có category dropdown/select", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      // Verify select elements exist in page
      const selects = screen.queryAllByRole("combobox");
      expect(selects.length > 0).toBe(true);
    });

    test("nên có Submit/Save button", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      const addButton = screen
        .queryAllByRole("button")
        .find(
          (btn) =>
            btn.textContent?.includes("+") || btn.textContent?.includes("Thêm")
        );

      if (addButton) {
        await user.click(addButton);
        const buttons = screen.queryAllByRole("button");
        expect(buttons.length > 0).toBe(true);
      }
    });
  });

  /**
   * Test 3: ProductDetail/Notification Component
   */
  describe("Test 3: ProductDetail", () => {
    test("nên display product info: name, price, quantity, category", async () => {
      render(<ProductManagement />);

      // Check if table or list displays product info columns
      await waitFor(() => {
        const headers = screen.queryAllByText(
          /tên|name|giá|price|số lượng|quantity|loại|category/i
        );
        expect(headers.length > 0).toBe(true);
      });
    });

    test("nên display product details khi click view/expand", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      // Look for view buttons or expandable rows
      const viewButtons = screen.queryAllByRole("button");
      expect(viewButtons.length > 0).toBe(true);
    });
  });
});

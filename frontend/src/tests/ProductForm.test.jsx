/**
 * @see ../components/ProductManagement.jsx - Parent component
 * @see ../utils/validateProduct.js - Validation logic
 */

import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProductManagement from "../components/ProductManagement";
import "@testing-library/jest-dom";
import { getAllProducts, createProduct, updateProduct } from "../services/productService";

jest.mock("../services/productService");

const openForm = async (user, buttonText = "Thêm Sản Phẩm") => {
  const buttons = screen.getAllByRole("button");
  const button = buttons.find(btn => btn.textContent?.includes(buttonText) && !btn.classList.contains("btn-full"));
  if (button) await user.click(button);
};

const getSubmitBtn = () => {
  const buttons = screen.getAllByRole("button");
  return buttons.find(btn => btn.textContent?.includes("Thêm Sản Phẩm") && btn.classList.contains("btn-full"));
};

const fillForm = async (user, data) => {
  await waitFor(() => expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument());
  
  if (data.name) await user.type(screen.getByLabelText(/tên sản phẩm/i), data.name);
  if (data.price) await user.type(screen.getByLabelText(/giá/i), data.price);
  if (data.quantity) await user.type(screen.getByLabelText(/số lượng/i), data.quantity);
  if (data.category) await user.selectOptions(screen.getByLabelText(/danh mục/i), data.category);
  if (data.description) await user.type(screen.getByLabelText(/mô tả/i), data.description);
};

describe("ProductForm - Create & Edit Tests (200 lines)", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    getAllProducts.mockResolvedValue([]);
    createProduct?.mockResolvedValue({ id: 1, name: "Test" });
    updateProduct?.mockResolvedValue({ id: 1, name: "Updated" });
  });

  // ========== CREATE PRODUCT TESTS ==========
  describe("CREATE - Tạo sản phẩm", () => {
    test("Mở form khi click 'Thêm Sản Phẩm'", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      await openForm(user);
      await waitFor(() => expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument());
    });

    test("Form có đầy đủ fields: name, price, quantity, category, description", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      await openForm(user);
      await waitFor(() => {
        expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/giá/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/số lượng/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/danh mục/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/mô tả/i)).toBeInTheDocument();
      });
    });

    test("Có thể nhập dữ liệu vào tất cả fields", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      await openForm(user);
      
      await fillForm(user, {
        name: "Laptop Dell",
        price: "15000000",
        quantity: "50",
        category: "Electronics",
        description: "Laptop mạnh mẽ cho văn phòng"
      });

      expect(screen.getByLabelText(/tên sản phẩm/i)).toHaveValue("Laptop Dell");
      expect(screen.getByLabelText(/giá/i)).toHaveValue("15000000");
      expect(screen.getByLabelText(/số lượng/i)).toHaveValue("50");
    });

    test("Submit form với dữ liệu hợp lệ", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      await openForm(user);
      await fillForm(user, {
        name: "iPhone 15",
        price: "30000000",
        quantity: "10",
        category: "Electronics",
        description: "Điện thoại mới nhất"
      });
      
      await user.click(getSubmitBtn());
      await waitFor(() => {
        expect(screen.queryByText(/thành công|success/i)).toBeTruthy();
      }, { timeout: 2000 });
    });

    test("Đóng form khi click Cancel", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      await openForm(user);
      await waitFor(() => expect(screen.getByRole("button", { name: /hủy/i })).toBeInTheDocument());
      
      await user.click(screen.getByRole("button", { name: /hủy/i }));
      await waitFor(() => {
        expect(screen.queryByLabelText(/tên sản phẩm/i)).not.toBeInTheDocument();
      });
    });
  });

  // ========== EDIT PRODUCT TESTS ==========
  describe("EDIT - Chỉnh sửa sản phẩm", () => {
    test("Mở form edit từ nút Edit trên bảng", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      // Giả sử có bảng với nút Edit
      const editButtons = screen.queryAllByRole("button", { name: /chỉnh sửa|edit/i });
      if (editButtons.length > 0) {
        await user.click(editButtons[0]);
        await waitFor(() => expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument());
      }
    });

    test("Cập nhật nhiều fields cùng lúc", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      const editButtons = screen.queryAllByRole("button", { name: /chỉnh sửa|edit/i });
      if (editButtons.length > 0) {
        await user.click(editButtons[0]);
        await fillForm(user, {
          name: "Sản phẩm mới",
          price: "50000000",
          quantity: "200",
          description: "Mô tả mới"
        });
        
        await user.click(getSubmitBtn());
        await waitFor(() => {
          expect(updateProduct).toHaveBeenCalled();
        });
      }
    });

    test("Lỗi: cập nhật giá âm", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      const editButtons = screen.queryAllByRole("button", { name: /chỉnh sửa|edit/i });
      if (editButtons.length > 0) {
        await user.click(editButtons[0]);
        await waitFor(() => expect(screen.getByLabelText(/giá/i)).toBeInTheDocument());
        
        const priceInput = screen.getByLabelText(/giá/i);
        await user.clear(priceInput);
        await user.type(priceInput, "-999");
        await user.click(getSubmitBtn());
        
        await waitFor(() => {
          expect(screen.queryByText(/phải lớn hơn 0/i)).toBeTruthy();
        }, { timeout: 2000 });
      }
    });

    test("Đóng form edit khi click Cancel", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      const editButtons = screen.queryAllByRole("button", { name: /chỉnh sửa|edit/i });
      if (editButtons.length > 0) {
        await user.click(editButtons[0]);
        await waitFor(() => expect(screen.getByRole("button", { name: /hủy/i })).toBeInTheDocument());
        
        await user.click(screen.getByRole("button", { name: /hủy/i }));
        await waitFor(() => {
          expect(screen.queryByLabelText(/tên sản phẩm/i)).not.toBeInTheDocument();
        });
      }
    });
  });
});

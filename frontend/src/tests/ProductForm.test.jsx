/**
 * @see ../components/ProductManagement.jsx - Parent component containing form
 * @see ../utils/validateProduct.js - Validation logic being tested
 */

import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProductManagement from "../components/ProductManagement";
import "@testing-library/jest-dom";
import { getAllProducts, createProduct } from "../services/productService";
// Helper function to click the header "Thêm Sản Phẩm" button

jest.mock("../services/productService");

const openProductForm = async (user) => {
  const buttons = screen.getAllByRole("button");
  const addButton = buttons.find((btn) => {
    // Look for the button in header (exclude form submit button)
    return (
      btn.textContent?.includes("Thêm Sản Phẩm") &&
      !btn.classList.contains("btn-full")
    );
  });

  if (addButton) {
    await user.click(addButton);
  }
};

// Helper function to get submit button (the one with btn-full class)
const getSubmitButton = () => {
  const buttons = screen.getAllByRole("button");
  return buttons.find((btn) => {
    return (
      btn.textContent?.includes("Thêm Sản Phẩm") &&
      btn.classList.contains("btn-full")
    );
  });
};

describe("ProductForm Component (create/edit) Tests (2 điểm)", () => {
  beforeEach(() => {
    jest.clearAllMocks();

    getAllProducts.mockResolvedValue([]);
    if (createProduct) {
        createProduct.mockResolvedValue({ id: 1, name: "Test Product" });
    }
  });

  /**
   * Test 1: Form Rendering - Kiểm tra các input fields render đúng
   */
  describe("Test 1: Form Rendering", () => {
    test("nên render form khi open modal", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      // Verify form inputs appear
      await waitFor(() => {
        expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/giá/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/số lượng/i)).toBeInTheDocument();
      });
    });

    test("nên có product name input", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const nameInput = screen.getByLabelText(/tên sản phẩm/i);
        expect(nameInput).toBeInTheDocument();
        expect(nameInput).toHaveAttribute("type", "text");
        expect(nameInput).toHaveAttribute("placeholder", "Nhập tên sản phẩm");
      });
    });

    test("nên có product price input", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const priceInput = screen.getByLabelText(/giá/i);
        expect(priceInput).toBeInTheDocument();
        expect(priceInput).toHaveAttribute("type", "text");
      });
    });

    test("nên có product quantity input", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const quantityInput = screen.getByLabelText(/số lượng/i);
        expect(quantityInput).toBeInTheDocument();
        expect(quantityInput).toHaveAttribute("type", "text");
      });
    });

    test("nên có category dropdown/select", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const categorySelect = screen.getByLabelText(/danh mục/i);
        expect(categorySelect).toBeInTheDocument();
        expect(categorySelect.tagName).toBe("SELECT");
      });
    });

    test("nên có description textarea", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const descriptionInput = screen.getByLabelText(/mô tả/i);
        expect(descriptionInput).toBeInTheDocument();
        expect(descriptionInput.tagName).toBe("TEXTAREA");
      });
    });

    test("nên có Cancel/Close button", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const cancelButton = screen.getByRole("button", { name: /hủy/i });
        expect(cancelButton).toBeInTheDocument();
      });
    });

    test("nên có Submit/Save button", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const submitBtn = getSubmitButton();
        expect(submitBtn).toBeInTheDocument();
      });
    });
  });

  /**
   * Test 2: Form Input Handling - Kiểm tra nhập dữ liệu
   */
  describe("Test 2: Form Input Handling", () => {
    test("nên có thể nhập tên sản phẩm", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument();
      });

      const nameInput = screen.getByLabelText(/tên sản phẩm/i);
      await user.type(nameInput, "Laptop Dell");

      expect(nameInput).toHaveValue("Laptop Dell");
    });

    test("nên có thể nhập giá sản phẩm", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/giá/i)).toBeInTheDocument();
      });

      const priceInput = screen.getByLabelText(/giá/i);
      await user.type(priceInput, "15000000");

      expect(priceInput).toHaveValue("15000000");
    });

    test("nên có thể nhập số lượng", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/số lượng/i)).toBeInTheDocument();
      });

      const quantityInput = screen.getByLabelText(/số lượng/i);
      await user.type(quantityInput, "50");

      expect(quantityInput).toHaveValue("50");
    });

    test("nên có thể chọn danh mục", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/danh mục/i)).toBeInTheDocument();
      });

      const categorySelect = screen.getByLabelText(/danh mục/i);
      await user.selectOptions(categorySelect, "Điện tử");

      expect(categorySelect).toHaveValue("Điện tử");
    });

    test("nên có thể nhập mô tả sản phẩm", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/mô tả/i)).toBeInTheDocument();
      });

      const descriptionInput = screen.getByLabelText(/mô tả/i);
      await user.type(descriptionInput, "Đây là laptop mạnh mẽ cho văn phòng");

      expect(descriptionInput).toHaveValue(
        "Đây là laptop mạnh mẽ cho văn phòng"
      );
    });

    test("nên có thể clear input sau khi nhập", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument();
      });

      const nameInput = screen.getByLabelText(/tên sản phẩm/i);
      await user.type(nameInput, "Test Product");
      await user.clear(nameInput);

      expect(nameInput).toHaveValue("");
    });
  });

  /**
   * Test 3: Form Validation - Kiểm tra lỗi validation
   */
  describe("Test 3: Form Validation", () => {
    test("nên show error khi submit form trống", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(getSubmitButton()).toBeInTheDocument();
      });

      const submitBtn = getSubmitButton();
      await user.click(submitBtn);

      // Should show error notification
      await waitFor(
        () => {
          const notification = screen.queryByText(/vui lòng kiểm tra lại/i);
          expect(notification).toBeInTheDocument();
        },
        { timeout: 2000 }
      );
    });

    test("nên show error khi tên sản phẩm quá ngắn", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument();
      });

      const nameInput = screen.getByLabelText(/tên sản phẩm/i);
      await user.type(nameInput, "AB");

      const submitBtn = getSubmitButton();
      await user.click(submitBtn);

      // Should show error for short name
      await waitFor(
        () => {
          const errorMessage = screen.queryByText(/phải có ít nhất 3 ký tự/i);
          expect(errorMessage).toBeTruthy();
        },
        { timeout: 2000 }
      );
    });

    test("nên show error khi giá âm", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/giá/i)).toBeInTheDocument();
      });

      const priceInput = screen.getByLabelText(/giá/i);
      await user.type(priceInput, "-1000");

      const submitBtn = getSubmitButton();
      await user.click(submitBtn);

      // Should show error for negative price
      await waitFor(
        () => {
          const errorMessage = screen.queryByText(/không được là số âm/i);
          expect(errorMessage).toBeTruthy();
        },
        { timeout: 2000 }
      );
    });

    test("nên show error khi số lượng âm", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/số lượng/i)).toBeInTheDocument();
      });

      const quantityInput = screen.getByLabelText(/số lượng/i);
      await user.type(quantityInput, "-5");

      const submitBtn = getSubmitButton();
      await user.click(submitBtn);

      // Should show error for negative quantity
      await waitFor(
        () => {
          const errorMessage = screen.queryByText(/không được là số âm/i);
          expect(errorMessage).toBeTruthy();
        },
        { timeout: 2000 }
      );
    });

    test("nên show error khi không chọn danh mục", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument();
      });

      const nameInput = screen.getByLabelText(/tên sản phẩm/i);
      await user.type(nameInput, "Valid Product");

      const priceInput = screen.getByLabelText(/giá/i);
      await user.type(priceInput, "1000");

      const quantityInput = screen.getByLabelText(/số lượng/i);
      await user.type(quantityInput, "10");

      const descriptionInput = screen.getByLabelText(/mô tả/i);
      await user.type(descriptionInput, "Valid description here");

      const submitBtn = getSubmitButton();
      await user.click(submitBtn);

      // Should show error for missing category
      await waitFor(
        () => {
          const errorMessages = screen.getAllByText(/vui lòng chọn danh mục/i);
          const errorMessage = errorMessages.find((el) =>
            el.classList.contains("error-message")
          );
          expect(errorMessage).toBeInTheDocument();
        },
        { timeout: 2000 }
      );
    });

    test("nên show error khi mô tả quá ngắn", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/mô tả/i)).toBeInTheDocument();
      });

      const descriptionInput = screen.getByLabelText(/mô tả/i);
      await user.type(descriptionInput, "Short");

      const submitBtn = getSubmitButton();
      await user.click(submitBtn);

      // Should show error for short description
      await waitFor(
        () => {
          const errorMessage = screen.queryByText(/ít nhất 10 ký tự/i);
          expect(errorMessage).toBeTruthy();
        },
        { timeout: 2000 }
      );
    });
  });

  /**
   * Test 4: Form Submission - Kiểm tra lưu dữ liệu
   */
  describe("Test 4: Form Submission", () => {
    test("nên có thể submit form với dữ liệu hợp lệ", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        expect(screen.getByLabelText(/tên sản phẩm/i)).toBeInTheDocument();
      });

      const nameInput = screen.getByLabelText(/tên sản phẩm/i);
      await user.type(nameInput, "Test Product");

      const priceInput = screen.getByLabelText(/giá/i);
      await user.type(priceInput, "100000");

      const quantityInput = screen.getByLabelText(/số lượng/i);
      await user.type(quantityInput, "10");

      const categorySelect = screen.getByLabelText(/danh mục/i);
      await user.selectOptions(categorySelect, "Điện tử");

      const descriptionInput = screen.getByLabelText(/mô tả/i);
      await user.type(descriptionInput, "This is a test product description");

      const submitBtn = getSubmitButton();
      await user.click(submitBtn);

      // Should show success notification
      await waitFor(
        () => {
          const successMessage = screen.queryByText(/thành công/i);
          expect(successMessage).toBeTruthy();
        },
        { timeout: 2000 }
      );
    });

    test("nên gọi hủy form khi click Cancel button", async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);

      await openProductForm(user);

      await waitFor(() => {
        const cancelButton = screen.getByRole("button", { name: /hủy/i });
        expect(cancelButton).toBeInTheDocument();
      });

      const cancelButton = screen.getByRole("button", { name: /hủy/i });
      await user.click(cancelButton);

      // Modal should close - form inputs should disappear
      await waitFor(() => {
        const nameInput = screen.queryByLabelText(/tên sản phẩm/i);
        expect(nameInput).not.toBeInTheDocument();
      });
    });
  });
});

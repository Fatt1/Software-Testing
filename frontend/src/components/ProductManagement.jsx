import { useState, useEffect } from "react";

import {
  Search,
  Plus,
  Edit2,
  Trash2,
  Eye,
  X,
  Check,
  AlertCircle,
} from "lucide-react";
import "./ProductManagement.css";
import {
  getAllProducts,
  getProductById,
  createProduct,
  updateProduct,
  deleteProduct,
} from "../services/productService";

const ProductManagement = () => {
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState("create");
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterCategory, setFilterCategory] = useState("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [notification, setNotification] = useState(null);
  const itemsPerPage = 5;

  const categories = ["Electronics", "Books", "Clothing", "Toys", "Groceries"];

  const [formData, setFormData] = useState({
    productName: "",
    price: "",
    quantity: "",
    description: "",
    category: "",
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    loadProducts();
  }, []);

  useEffect(() => {
    // Apply search and category filters whenever products, searchTerm, or filterCategory change
    let filtered = [...products];

    if (searchTerm) {
      filtered = filtered.filter((p) =>
        p.productName.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (filterCategory !== "all") {
      filtered = filtered.filter((p) => p.category === filterCategory);
    }

    setFilteredProducts(filtered);
    setCurrentPage(1);
  }, [products, searchTerm, filterCategory]);

  // Load products from API
  const loadProducts = async () => {
    try {
      const response = await getAllProducts();
      // Backend trả về array products hoặc có thể là object với content property
      const productsData = Array.isArray(response)
        ? response
        : response.content || [];
      setProducts(productsData);
    } catch (error) {
      console.error("Lỗi khi tải sản phẩm:", error);
      showNotification("Không thể tải danh sách sản phẩm", "error");
      setProducts([]);
    }
  };

  const showNotification = (message, type = "success") => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 3000);
  };

  const validateForm = () => {
    const newErrors = {};

    // Name validation: 3-100 ký tự, không được rỗng
    if (!formData.productName || !formData.productName.trim()) {
      newErrors.productName = "Tên sản phẩm không được để trống";
    } else if (formData.productName.trim().length < 3) {
      newErrors.productName = "Tên sản phẩm phải có ít nhất 3 ký tự";
    } else if (formData.productName.trim().length > 100) {
      newErrors.productName = "Tên sản phẩm không được vượt quá 100 ký tự";
    }

    // Price validation: > 0, <= 999,999,999
    if (
      formData.price === "" ||
      formData.price === null ||
      formData.price === undefined
    ) {
      newErrors.price = "Giá không được để trống";
    } else if (isNaN(formData.price)) {
      newErrors.price = "Giá phải là số";
    } else {
      const priceValue = parseFloat(formData.price);
      if (priceValue <= 0) {
        newErrors.price = "Giá phải lớn hơn 0";
      } else if (priceValue > 999999999) {
        newErrors.price = "Giá không được vượt quá 999,999,999";
      }
    }

    // Quantity validation: >= 0, <= 99,999
    if (
      formData.quantity === "" ||
      formData.quantity === null ||
      formData.quantity === undefined
    ) {
      newErrors.quantity = "Số lượng không được để trống";
    } else if (isNaN(formData.quantity)) {
      newErrors.quantity = "Số lượng phải là số";
    } else {
      const quantityValue = parseInt(formData.quantity);
      if (quantityValue < 0) {
        newErrors.quantity = "Số lượng không được là số âm";
      } else if (quantityValue > 99999) {
        newErrors.quantity = "Số lượng không được vượt quá 99,999";
      }
    }

    // Category validation: Phải thuộc danh sách categories
    if (!formData.category) {
      newErrors.category = "Vui lòng chọn danh mục";
    } else if (!categories.includes(formData.category)) {
      newErrors.category = "Danh mục không hợp lệ";
    }

    // Description validation: <= 500 ký tự
    if (!formData.description || !formData.description.trim()) {
      newErrors.description = "Mô tả không được để trống";
    } else if (formData.description.trim().length > 500) {
      newErrors.description = "Mô tả không được vượt quá 500 ký tự";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleOpenModal = (mode, product = null) => {
    setModalMode(mode);
    if (mode === "create") {
      setFormData({
        productName: "",
        price: "",
        quantity: "",
        description: "",
        category: "",
      });
    } else if (mode === "edit" && product) {
      setFormData({ ...product });
    } else if (mode === "view" && product) {
      setSelectedProduct(product);
    }
    setErrors({});
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedProduct(null);
    setFormData({
      productName: "",
      price: "",
      quantity: "",
      description: "",
      category: "",
    });
    setErrors({});
  };

  // Call API to create or update product
  const handleSubmit = async () => {
    if (!validateForm()) {
      showNotification("Vui lòng kiểm tra lại thông tin nhập vào", "error");
      return;
    }

    try {
      if (modalMode === "create") {
        await createProduct(formData);
        showNotification("Thêm sản phẩm thành công!");
      } else if (modalMode === "edit") {
        await updateProduct(formData.id, formData);
        showNotification("Cập nhật sản phẩm thành công!");
      }

      await loadProducts();
      handleCloseModal();
    } catch (error) {
      console.error("Lỗi khi submit sản phẩm:", error);
      showNotification(
        error.message || "Có lỗi xảy ra. Vui lòng thử lại!",
        "error"
      );
    }
  };

  // Call API to delete product
  const handleDelete = async () => {
    try {
      await deleteProduct(deleteId);
      await loadProducts();
      showNotification("Xóa sản phẩm thành công!");
      setShowDeleteConfirm(false);
      setDeleteId(null);
    } catch (error) {
      console.error("Lỗi khi xóa sản phẩm:", error);
      showNotification(
        error.message || "Có lỗi xảy ra khi xóa sản phẩm",
        "error"
      );
    }
  };

  // Handle Enter key press to navigate between inputs
  const handleKeyPress = (e, nextFieldId) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (nextFieldId === 'submit') {
        // Submit form when on last field
        handleSubmit();
      } else {
        // Focus next field
        document.getElementById(nextFieldId)?.focus();
      }
    }
  };

  const openDeleteConfirm = (id) => {
    setDeleteId(id);
    setShowDeleteConfirm(true);
  };

  const totalPages = Math.ceil(filteredProducts.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentProducts = filteredProducts.slice(
    startIndex,
    startIndex + itemsPerPage
  );

  return (
    <div className="app-container">
      {notification && (
        <div className={`notification ${notification.type}`}>
          {notification.type === "success" ? (
            <Check size={20} />
          ) : (
            <AlertCircle size={20} />
          )}
          {notification.message}
        </div>
      )}

      <div className="max-width-container">
        <div className="header-card">
          <div className="header-top">
            <h1 className="page-title">Quản Lý Sản Phẩm</h1>
            <button
              onClick={() => handleOpenModal("create")}
              className="btn btn-primary"
            >
              <Plus size={20} />
              Thêm Sản Phẩm
            </button>
          </div>

          <div className="search-filter-container">
            <div className="search-box">
              <Search className="search-icon" size={20} />
              <input
                type="text"
                placeholder="Tìm kiếm sản phẩm..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="search-input"
              />
            </div>
            <select
              value={filterCategory}
              onChange={(e) => setFilterCategory(e.target.value)}
              className="filter-select"
            >
              <option value="all">Tất cả danh mục</option>
              {categories.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="table-card">
          <div className="table-container">
            <table className="product-table">
              <thead>
                <tr>
                  <th>Tên sản phẩm</th>
                  <th>Giá</th>
                  <th>Số lượng</th>
                  <th>Danh mục</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {currentProducts.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="empty-state">
                      Không có sản phẩm nào
                    </td>
                  </tr>
                ) : (
                  currentProducts.map((product) => (
                    <tr key={product.id}>
                      <td>{product.productName}</td>
                      <td>
                        {parseFloat(product.price).toLocaleString("vi-VN")} đ
                      </td>
                      <td>{product.quantity}</td>
                      <td>
                        <span className="category-badge">
                          {product.category}
                        </span>
                      </td>
                      <td>
                        <div className="action-buttons">
                          <button
                            onClick={() => handleOpenModal("view", product)}
                            className="icon-btn blue"
                            title="Xem chi tiết"
                          >
                            <Eye size={18} />
                          </button>
                          <button
                            onClick={() => handleOpenModal("edit", product)}
                            className="icon-btn green"
                            title="Chỉnh sửa"
                          >
                            <Edit2 size={18} />
                          </button>
                          <button
                            onClick={() => openDeleteConfirm(product.id)}
                            className="icon-btn red"
                            title="Xóa"
                          >
                            <Trash2 size={18} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <div className="pagination-info">
                Hiển thị {startIndex + 1} -{" "}
                {Math.min(startIndex + itemsPerPage, filteredProducts.length)}{" "}
                của {filteredProducts.length} sản phẩm
              </div>
              <div className="pagination-buttons">
                <button
                  onClick={() =>
                    setCurrentPage((prev) => Math.max(1, prev - 1))
                  }
                  disabled={currentPage === 1}
                  className="pagination-btn"
                >
                  Trước
                </button>
                
                {/* Page numbers */}
                {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                  <button
                    key={page}
                    onClick={() => setCurrentPage(page)}
                    className={`pagination-btn ${currentPage === page ? 'active' : ''}`}
                  >
                    {page}
                  </button>
                ))}
                
                <button
                  onClick={() =>
                    setCurrentPage((prev) => Math.min(totalPages, prev + 1))
                  }
                  disabled={currentPage === totalPages}
                  className="pagination-btn"
                >
                  Sau
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {showModal && modalMode !== "view" && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-content">
              <div className="modal-header">
                <h2 className="modal-title">
                  {modalMode === "create"
                    ? "Thêm Sản Phẩm Mới"
                    : "Chỉnh Sửa Sản Phẩm"}
                </h2>
                <button onClick={handleCloseModal} className="modal-close">
                  <X size={24} />
                </button>
              </div>

              <form>
                <div className="form-group">
                  <label htmlFor="name-input" className="form-label">
                    Tên sản phẩm <span className="required">*</span>
                  </label>
                  <input
                    id="name-input"
                    type="text"
                    value={formData.productName}
                    onChange={(e) =>
                      setFormData({ ...formData, productName: e.target.value })
                    }
                    onKeyPress={(e) => handleKeyPress(e, 'price-input')}
                    className={`form-input ${
                      errors.productName ? "error" : ""
                    }`}
                    placeholder="Nhập tên sản phẩm"
                  />
                  {errors.productName && (
                    <p className="error-message">{errors.productName}</p>
                  )}
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="price-input" className="form-label">
                      Giá (VNĐ) <span className="required">*</span>
                    </label>
                    <input
                      id="price-input"
                      type="text"
                      value={formData.price}
                      onChange={(e) =>
                        setFormData({ ...formData, price: e.target.value })
                      }
                      onKeyPress={(e) => handleKeyPress(e, 'quantity-input')}
                      className={`form-input ${errors.price ? "error" : ""}`}
                      placeholder="0"
                    />
                    {errors.price && (
                      <p className="error-message">{errors.price}</p>
                    )}
                  </div>

                  <div className="form-group">
                    <label htmlFor="quantity-input" className="form-label">
                      Số lượng <span className="required">*</span>
                    </label>
                    <input
                      id="quantity-input"
                      type="text"
                      value={formData.quantity}
                      onChange={(e) =>
                        setFormData({ ...formData, quantity: e.target.value })
                      }
                      onKeyPress={(e) => handleKeyPress(e, 'category-select')}
                      className={`form-input ${errors.quantity ? "error" : ""}`}
                      placeholder="0"
                    />
                    {errors.quantity && (
                      <p className="error-message">{errors.quantity}</p>
                    )}
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="category-select" className="form-label">
                    Danh mục <span className="required">*</span>
                  </label>
                  <select
                    id="category-select"
                    value={formData.category}
                    onChange={(e) =>
                      setFormData({ ...formData, category: e.target.value })
                    }
                    onKeyPress={(e) => handleKeyPress(e, 'description-textarea')}
                    className={`form-select ${errors.category ? "error" : ""}`}
                  >
                    <option value="">Chọn danh mục</option>
                    {categories.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                  {errors.category && (
                    <p className="error-message">{errors.category}</p>
                  )}
                </div>

                <div className="form-group">
                  <label htmlFor="description-textarea" className="form-label">
                    Mô tả <span className="required">*</span>
                  </label>
                  <textarea
                    id="description-textarea"
                    value={formData.description}
                    onChange={(e) =>
                      setFormData({ ...formData, description: e.target.value })
                    }
                    onKeyPress={(e) => handleKeyPress(e, 'submit')}
                    rows="4"
                    className={`form-textarea ${
                      errors.description ? "error" : ""
                    }`}
                    placeholder="Nhập mô tả sản phẩm"
                  />
                  {errors.description && (
                    <p className="error-message">{errors.description}</p>
                  )}
                </div>
              </form>

              <div className="form-actions">
                <button
                  onClick={handleSubmit}
                  className="btn btn-primary btn-full"
                >
                  {modalMode === "create" ? "Thêm Sản Phẩm" : "Cập Nhật"}
                </button>
                <button
                  onClick={handleCloseModal}
                  className="btn btn-secondary btn-full"
                >
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {showModal && modalMode === "view" && selectedProduct && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-content">
              <div className="modal-header">
                <h2 className="modal-title">Chi Tiết Sản Phẩm</h2>
                <button onClick={handleCloseModal} className="modal-close">
                  <X size={24} />
                </button>
              </div>

              <div>
                <div className="detail-grid">
                  <div className="detail-item">
                    <p className="detail-label">Tên sản phẩm</p>
                    <p className="detail-value">
                      {selectedProduct.productName}
                    </p>
                  </div>
                  <div className="detail-item">
                    <p className="detail-label">Danh mục</p>
                    <span className="category-badge">
                      {selectedProduct.category}
                    </span>
                  </div>
                </div>

                <div className="detail-grid">
                  <div className="detail-item">
                    <p className="detail-label">Giá</p>
                    <p className="detail-value price">
                      {parseFloat(selectedProduct.price).toLocaleString(
                        "vi-VN"
                      )}{" "}
                      đ
                    </p>
                  </div>
                  <div className="detail-item">
                    <p className="detail-label">Số lượng</p>
                    <p className="detail-value">{selectedProduct.quantity}</p>
                  </div>
                </div>

                <div className="detail-item">
                  <p className="detail-label">Mô tả</p>
                  <p className="detail-description">
                    {selectedProduct.description}
                  </p>
                </div>
              </div>

              <div className="form-actions">
                <button
                  onClick={() => {
                    handleCloseModal();
                    handleOpenModal("edit", selectedProduct);
                  }}
                  className="btn btn-success btn-full"
                >
                  Chỉnh Sửa
                </button>
                <button
                  onClick={handleCloseModal}
                  className="btn btn-secondary btn-full"
                >
                  Đóng
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {showDeleteConfirm && (
        <div className="modal-overlay">
          <div className="modal modal-small">
            <div className="modal-content">
              <div className="confirm-header">
                <div className="confirm-icon">
                  <AlertCircle size={24} />
                </div>
                <h3 className="confirm-title">Xác Nhận Xóa</h3>
              </div>
              <p className="confirm-message">
                Bạn có chắc chắn muốn xóa sản phẩm này? Hành động này không thể
                hoàn tác.
              </p>
              <div className="form-actions">
                <button
                  onClick={handleDelete}
                  className="btn btn-danger btn-full"
                >
                  Xóa
                </button>
                <button
                  onClick={() => {
                    setShowDeleteConfirm(false);
                    setDeleteId(null);
                  }}
                  className="btn btn-secondary btn-full"
                >
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductManagement;

# Product Management POM - Quick Reference Guide

## 🎯 Quick Start

### Import & Setup
```javascript
import ProductPage from '../pages/ProductPage.js';

beforeEach(() => {
  ProductPage.navigateToProductPage();
});
```

## 📋 Common Operations

### Create Product
```javascript
ProductPage.createProduct({
  name: 'Laptop',
  price: '20000000',
  quantity: '5',
  category: 'Điện tử',
  description: 'High-performance laptop with latest features'
});
```

### Search Product
```javascript
ProductPage.searchProduct('iPhone');
ProductPage.verifyProductExists('iPhone 14');
```

### View Product Details
```javascript
ProductPage.viewProduct('Laptop');
ProductPage.detailValue.should('be.visible');
ProductPage.closeModal();
```

### Edit Product
```javascript
ProductPage.updateProduct('Laptop', {
  name: 'Gaming Laptop',
  price: '25000000'
});
```

### Delete Product
```javascript
ProductPage.deleteProduct('Laptop');
ProductPage.verifyProductNotExists('Laptop');
```

### Filter by Category
```javascript
ProductPage.filterByCategory('Điện tử');
ProductPage.verifyTableNotEmpty();
```

## ✅ Verification Methods

```javascript
// Product existence
ProductPage.verifyProductExists('Product Name')
ProductPage.verifyProductNotExists('Product Name')

// Table state
ProductPage.verifyTableEmpty()
ProductPage.verifyTableNotEmpty()

// Form visibility
ProductPage.verifyFormVisible()

// Notifications
ProductPage.verifySuccessNotification('message')
ProductPage.verifyErrorNotification('message')

// Field errors
ProductPage.verifyFieldError('name')
ProductPage.verifyFieldError('price')
ProductPage.verifyFieldError('quantity')
ProductPage.verifyFieldError('category')
ProductPage.verifyFieldError('description')

// Pagination
ProductPage.verifyPaginationVisible()
```

## 🔄 Chainable Methods

Most methods support chaining:
```javascript
ProductPage.clickAddProduct()
  .fillProductForm(productData)
  .submitForm()
  .verifySuccessNotification(/added/i);

// Or individual steps:
ProductPage.searchProduct('iPhone');
ProductPage.filterByCategory('Điện tử');
ProductPage.verifyProductExists('iPhone 14');
```

## 📊 Form Data Structure

```javascript
{
  name: string,           // 3-100 chars required
  price: string,          // Positive number required
  quantity: string,       // Non-negative required
  category: string,       // Select from list required
  description: string     // 10-500 chars required
}
```

## 🎭 Available Categories

- Điện tử (Electronics)
- Thời trang (Fashion)
- Thực phẩm (Food)
- Đồ gia dụng (Appliances)
- Sách (Books)

## 🔍 Main Selectors

| Element | Selector |
|---------|----------|
| Page Title | `h1` with text matching "quản lý sản phẩm" |
| Add Button | Button with text "thêm sản phẩm" |
| Search Input | `input[placeholder*="Tìm kiếm"]` |
| Category Filter | `select` (first one) |
| Product Table | `table.product-table` |
| Modal | `.modal` |
| Notification | `.notification` |

## 💡 Pro Tips

### 1. Always Navigate First
```javascript
beforeEach(() => {
  ProductPage.navigateToProductPage();
});
```

### 2. Use Chainable Methods
```javascript
// Good ✅
ProductPage.clickAddProduct()
  .fillProductForm(data)
  .submitForm();

// Avoid ❌
ProductPage.clickAddProduct();
ProductPage.fillProductForm(data);
ProductPage.submitForm();
```

### 3. Wait for Notifications
```javascript
ProductPage.createProduct(data);
ProductPage.verifySuccessNotification(/added/i);
// Automatic 500ms wait built-in
```

### 4. Clear Search After Use
```javascript
ProductPage.searchProduct('iPhone');
// ... assertions ...
ProductPage.searchInput.clear(); // Reset for next test
```

### 5. Test Isolation
```javascript
beforeEach(() => {
  ProductPage.navigateToProductPage();
  // Clear any previous state if needed
});

afterEach(() => {
  // Clean up if needed
});
```

## 🛠️ Method Reference

### Navigation
```javascript
navigateToProductPage()      // Go to product page
```

### CRUD
```javascript
createProduct(data)          // Create new
updateProduct(name, data)    // Edit existing
deleteProduct(name)          // Delete with confirm
viewProduct(name)            // View details
```

### Search & Filter
```javascript
searchProduct(name)          // Search by name
filterByCategory(category)   // Filter by category
```

### Form Operations
```javascript
fillProductForm(data)        // Fill all fields
submitForm()                 // Submit form
clearForm()                  // Clear all inputs
closeModal()                 // Close modal
```

### Verification
```javascript
verifyProductExists(name)
verifyProductNotExists(name)
verifySuccessNotification(msg)
verifyErrorNotification(msg)
verifyFieldError(field)
verifyFormVisible()
verifyTableEmpty()
verifyTableNotEmpty()
```

### Pagination
```javascript
clickNextPage()              // Go to next page
clickPreviousPage()          // Go to previous page
verifyPaginationVisible()    // Check pagination exists
getTotalProductCount()       // Get product count
```

## 🐛 Debugging Tips

### Check if Element Exists
```javascript
ProductPage.productNameInput.should('be.visible');
ProductPage.priceInput.should('not.be.disabled');
```

### Debug Notifications
```javascript
ProductPage.notification.should('be.visible');
ProductPage.notification.should('contain', 'expected text');
```

### Inspect Table Data
```javascript
ProductPage.tableRows.each(($row) => {
  cy.log($row.text());
});
```

### Check Modal State
```javascript
ProductPage.modal.should('be.visible');
ProductPage.modalTitle.should('contain', /create|edit/i);
```

## 📝 Common Test Patterns

### Pattern 1: Simple CRUD
```javascript
it('should create product', () => {
  ProductPage.createProduct(testData);
  ProductPage.verifyProductExists(testData.name);
});
```

### Pattern 2: Search & Verify
```javascript
it('should find product by search', () => {
  ProductPage.searchProduct('Test');
  ProductPage.verifyProductExists('Test Product');
});
```

### Pattern 3: Validation
```javascript
it('should validate form', () => {
  ProductPage.clickAddProduct();
  ProductPage.fillProductForm({ price: '-100' });
  ProductPage.submitForm();
  ProductPage.verifyFieldError('price');
});
```

### Pattern 4: Multiple Operations
```javascript
it('should handle multiple operations', () => {
  ProductPage.createProduct(data1);
  ProductPage.verifyProductExists(data1.name);
  ProductPage.updateProduct(data1.name, data2);
  ProductPage.verifyProductExists(data2.name);
  ProductPage.deleteProduct(data2.name);
});
```

## 🔗 Related Documentation

- **Full POM Details**: See `PRODUCT_POM_README.md`
- **Implementation Summary**: See `PRODUCT_POM_IMPLEMENTATION_SUMMARY.md`
- **Test Examples**: See `src/tests/cypress/e2e/product-management.cy.js`

## ⚡ Performance Tips

1. **Minimize waits** - POM has built-in delays
2. **Batch operations** - Create multiple products in one test
3. **Use fixtures** - For consistent test data
4. **Clear storage** - Between test runs to avoid conflicts

## 🆘 Common Issues & Solutions

### Issue: "Element not found"
```javascript
// Solution: Increase wait time
cy.get('selector', { timeout: 10000 })
```

### Issue: "Modal not closing"
```javascript
// Solution: Use explicit close
ProductPage.modalCloseButton.click();
cy.wait(300);
```

### Issue: "Form data not saved"
```javascript
// Solution: Wait after submit
ProductPage.submitForm();  // Already has 500ms wait
cy.wait(1000);  // Add extra if needed
```

---

**Last Updated**: November 19, 2025  
**Version**: 1.0

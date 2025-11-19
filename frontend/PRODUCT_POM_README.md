# Product Management - Page Object Model (POM) Implementation

## Overview

This document describes the implementation of Page Object Model (POM) for Product Management E2E tests using Cypress.

## Files Created

### 1. **ProductPage.js** - Page Object Model
Location: `src/tests/cypress/pages/ProductPage.js`

The ProductPage class encapsulates all selectors and methods for interacting with the Product Management page.

#### Key Features:

**Selectors Grouped by Functionality:**
- Header & Search Controls
- Table Elements
- Modal Components
- Form Inputs
- Error Messages
- Pagination Controls
- Notifications
- Detail Views

**Methods Available:**

##### Navigation & Page Setup
```javascript
navigateToProductPage()      // Navigate to product page
```

##### CRUD Operations
```javascript
createProduct(productData)   // Create new product
updateProduct(productName, updatedData)  // Edit product
deleteProduct(productName)   // Delete product with confirmation
viewProduct(productName)     // View product details
```

##### Search & Filter
```javascript
searchProduct(productName)   // Search by product name
filterByCategory(category)   // Filter by category
```

##### Form Operations
```javascript
fillProductForm(productData) // Fill form with product data
submitForm()                  // Submit the form
clearForm()                   // Clear all inputs
closeModal()                  // Close modal dialog
```

##### Verification Methods
```javascript
verifyProductExists(productName)           // Check if product exists
verifyProductNotExists(productName)        // Check if product not exists
verifySuccessNotification(message)         // Verify success message
verifyErrorNotification(message)           // Verify error message
verifyFieldError(fieldName)                // Verify field error
verifyFormVisible()                        // Verify form elements
verifyTableEmpty()                         // Check if no products
verifyTableNotEmpty()                      // Check if has products
verifyPaginationVisible()                  // Check pagination
```

##### Pagination
```javascript
clickNextPage()                            // Go to next page
clickPreviousPage()                        // Go to previous page
getTotalProductCount()                     // Get number of products
```

## Usage Examples

### Example 1: Create a Product
```javascript
import ProductPage from '../pages/ProductPage.js';

it('Should create a new product', () => {
  ProductPage.navigateToProductPage();
  
  const productData = {
    name: 'New Laptop',
    price: '15000000',
    quantity: '5',
    category: 'Điện tử',
    description: 'High-performance laptop'
  };
  
  ProductPage.createProduct(productData);
  ProductPage.verifyProductExists('New Laptop');
  ProductPage.verifySuccessNotification(/added successfully/i);
});
```

### Example 2: Search and Filter
```javascript
it('Should search products', () => {
  ProductPage.navigateToProductPage();
  ProductPage.searchProduct('iPhone');
  ProductPage.verifyProductExists('iPhone 14');
});
```

### Example 3: Edit Product
```javascript
it('Should update product', () => {
  ProductPage.navigateToProductPage();
  
  const updatedData = {
    name: 'Updated Product Name',
    price: '5000000'
  };
  
  ProductPage.updateProduct('Old Name', updatedData);
  ProductPage.verifySuccessNotification(/updated/i);
});
```

### Example 4: Delete Product
```javascript
it('Should delete product with confirmation', () => {
  ProductPage.navigateToProductPage();
  ProductPage.deleteProduct('Product to Delete');
  ProductPage.verifyProductNotExists('Product to Delete');
  ProductPage.verifySuccessNotification(/deleted successfully/i);
});
```

### Example 5: Form Validation
```javascript
it('Should show validation errors', () => {
  ProductPage.navigateToProductPage();
  ProductPage.clickAddProduct();
  
  ProductPage.productNameInput.type('AB');  // Too short
  ProductPage.priceInput.type('-1000');     // Negative
  ProductPage.submitForm();
  
  ProductPage.verifyFieldError('name');
  ProductPage.verifyFieldError('price');
});
```

### Example 6: Multiple Operations
```javascript
it('Should handle multiple products', () => {
  ProductPage.navigateToProductPage();
  
  // Create multiple products
  for (let i = 1; i <= 3; i++) {
    const product = {
      name: `Product ${i}`,
      price: `${100000 * i}`,
      quantity: `${10 + i}`,
      category: 'Điện tử',
      description: `Description for product ${i}`
    };
    ProductPage.createProduct(product);
  }
  
  // Verify all created
  ProductPage.verifyProductExists('Product 1');
  ProductPage.verifyProductExists('Product 2');
  ProductPage.verifyProductExists('Product 3');
});
```

## Test File

### **product-management.cy.js**
Location: `src/tests/cypress/e2e/product-management.cy.js`

Comprehensive E2E test suite covering:

1. **Page Load and UI Elements**
   - Page loading
   - Display of controls and buttons
   - Table headers

2. **Create Product Operations**
   - Valid product creation
   - Form validation
   - Error messages

3. **Search and Filter**
   - Product search
   - Category filtering
   - Multiple filters

4. **View Product Details**
   - Detail modal display
   - Product information
   - Edit button availability

5. **Edit Product**
   - Edit form display
   - Product updates
   - Validation during edit

6. **Delete Product**
   - Delete confirmation
   - Product removal
   - Delete cancellation

7. **Multiple Products Management**
   - Multiple creation
   - Data integrity
   - Batch operations

8. **Form Validation**
   - Field validation
   - Error display
   - Field constraints

9. **Product Form Interaction**
   - Rapid interactions
   - Decimal values
   - Currency formatting

## POM Pattern Benefits

1. **Maintainability** - All selectors in one place
2. **Reusability** - Methods can be reused across tests
3. **Readability** - Test code is cleaner and more understandable
4. **Scalability** - Easy to add new methods and selectors
5. **Isolation** - Element changes only require updates in POM

## Best Practices Applied

✅ Grouped selectors by functionality
✅ Chainable methods (return `this`)
✅ Descriptive method names
✅ Comprehensive verification methods
✅ Built-in wait times for stability
✅ Clear separation of concerns
✅ Fixture/test data management
✅ Error handling methods

## Page Elements Covered

### Header Section
- Page title
- Add Product button
- Search input
- Category filter

### Product Table
- Table headers
- Product rows
- Action buttons (View, Edit, Delete)
- Pagination controls

### Modal Dialogs
- Create/Edit modal
- View details modal
- Delete confirmation modal

### Form Fields
- Product name input
- Price input
- Quantity input
- Category selector
- Description textarea

### Error Messages
- Field-level errors
- Validation messages
- Notification alerts

### Display Elements
- Empty state message
- Pagination info
- Category badges
- Currency formatting

## How to Extend

### Adding New Selectors
```javascript
get newElement() {
  return cy.get('selector');
}
```

### Adding New Methods
```javascript
doSomething() {
  this.newElement.click();
  cy.wait(300);
  return this;
}
```

### Adding New Tests
```javascript
it('Should do something', () => {
  ProductPage.navigateToProductPage();
  ProductPage.doSomething();
  ProductPage.verifyExpectedResult();
});
```

## Running Tests

```bash
# Run all Product Management tests
npx cypress run --spec "src/tests/cypress/e2e/product-management.cy.js"

# Run specific test scenario
npx cypress run --spec "src/tests/cypress/e2e/product-management.cy.js" --grep "Scenario 1"

# Run in headed mode
npx cypress run --spec "src/tests/cypress/e2e/product-management.cy.js" --headed

# Run with browser UI
npx cypress open
```

## Test Data Structure

All test data follows this structure:
```javascript
const productData = {
  name: string,           // 3-100 characters
  price: string,          // Positive number
  quantity: string,       // Non-negative integer
  category: string,       // One of: Điện tử, Thời trang, Thực phẩm, Đồ gia dụng, Sách
  description: string     // 10-500 characters
};
```

## Validation Rules Enforced

- **Name**: 3-100 characters
- **Price**: Positive number only
- **Quantity**: Non-negative integer
- **Category**: Must select from list
- **Description**: 10-500 characters

## Future Enhancements

1. Add API mocking for faster tests
2. Add visual regression testing
3. Add performance metrics
4. Add accessibility testing
5. Add advanced filtering combinations
6. Add bulk operations testing
7. Add export/import functionality

## Support

For issues or questions about the POM implementation, refer to:
- ProductPage.js - Main POM file
- product-management.cy.js - Test examples
- Cypress documentation - https://docs.cypress.io

---
**Last Updated**: November 19, 2025
**Version**: 1.0
